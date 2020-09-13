package com.jokerconf.kstreams.dedup;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;

import java.time.Duration;

import static com.jokerconf.kstreams.configuration.TopicsConfiguration.INPUT_TOPIC_2;
import static com.jokerconf.kstreams.configuration.TopicsConfiguration.OUTPUT_TOPIC_2;

public class DeduplicationTransformer<K, V, E> implements ValueTransformerWithKey<K, V, V> {

    public static final String STORE_NAME = "dedup";

    private ProcessorContext context;

    /**
     * Key: ip address
     * Value: timestamp (event-time) of the corresponding event when the event ID was seen for the
     * first time
     */
    private WindowStore<E, Long> eventIdStore;

    private final long leftDurationMs;
    private final long rightDurationMs;

    private final KeyValueMapper<K, V, E> idExtractor;

    /**
     * @param maintainDurationPerEventInMs how long to "remember" a known ip address
     *                                     during the time of which any incoming duplicates
     *                                     will be dropped, thereby de-duplicating the
     *                                     input.
     * @param idExtractor                  extracts a unique identifier from a record by which we de-duplicate input
     *                                     records; if it returns null, the record will not be considered for
     *                                     de-duping but forwarded as-is.
     */
    DeduplicationTransformer(final long maintainDurationPerEventInMs, final KeyValueMapper<K, V, E> idExtractor) {
        if (maintainDurationPerEventInMs < 1) {
            throw new IllegalArgumentException("maintain duration per event must be >= 1");
        }
        leftDurationMs = maintainDurationPerEventInMs / 2;
        rightDurationMs = maintainDurationPerEventInMs - leftDurationMs;
        this.idExtractor = idExtractor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(final ProcessorContext context) {
        this.context = context;
        eventIdStore = (WindowStore<E, Long>) context.getStateStore(STORE_NAME);
    }

    @Override
    public V transform(final K key, final V value) {
        final E eventId = idExtractor.apply(key, value);
        if (eventId == null) {
            return value;
        } else {
            final V output;
            if (isDuplicate(eventId)) {
                output = null;
                updateTimestampOfExistingEventToPreventExpiry(eventId, context.timestamp());
            } else {
                output = value;
                rememberNewEvent(eventId, context.timestamp());
            }
            return output;
        }
    }

    private boolean isDuplicate(final E eventId) {
        final long eventTime = context.timestamp();
        final WindowStoreIterator<Long> timeIterator = eventIdStore.fetch(
                eventId,
                eventTime - leftDurationMs,
                eventTime + rightDurationMs);
        final boolean isDuplicate = timeIterator.hasNext();
        timeIterator.close();
        return isDuplicate;
    }

    private void updateTimestampOfExistingEventToPreventExpiry(final E eventId, final long newTimestamp) {
        eventIdStore.put(eventId, newTimestamp, newTimestamp);
    }

    private void rememberNewEvent(final E eventId, final long timestamp) {
        eventIdStore.put(eventId, timestamp, timestamp);
    }

    @Override
    public void close() {
        // Note: The store should NOT be closed manually here via `eventIdStore.close()`!
        // The Kafka Streams API will automatically close stores when necessary.
    }

    public static void buildTopology(StreamsBuilder streamsBuilder) {
        KStream<String, String> input =
                streamsBuilder.stream(INPUT_TOPIC_2, Consumed.with(Serdes.String(), Serdes.String()));
        final Duration windowSize = Duration.ofMinutes(2);
        final Duration retentionPeriod = windowSize;
        final StoreBuilder<WindowStore<String, Long>> dedupStoreBuilder = Stores.windowStoreBuilder(
                Stores.persistentWindowStore(DeduplicationTransformer.STORE_NAME,
                        retentionPeriod,
                        windowSize,
                        false
                ),
                Serdes.String(),
                Serdes.Long());

        streamsBuilder.addStateStore(dedupStoreBuilder);

        input
                .transformValues(() -> new DeduplicationTransformer<>(windowSize.toMillis(),
                        (key, value) -> key), DeduplicationTransformer.STORE_NAME)
                .filter((k, v) -> v != null)
                .to(OUTPUT_TOPIC_2, Produced.with(Serdes.String(), Serdes.String()));
    }

}
