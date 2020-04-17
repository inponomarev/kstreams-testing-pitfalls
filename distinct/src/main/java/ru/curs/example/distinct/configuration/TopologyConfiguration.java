package ru.curs.example.distinct.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class TopologyConfiguration {

    public static final String INPUT_TOPIC_WRONG = "input-wrong";
    public static final String OUTPUT_TOPIC_WRONG = "output-wrong";

    public static final String INPUT_TOPIC_RIGHT = "input-right";
    public static final String OUTPUT_TOPIC_RIGHT = "output-right";

    @Bean
    public NewTopic inputWrong() {
        return new NewTopic(INPUT_TOPIC_WRONG, 10, (short) 1);
    }

    @Bean
    public NewTopic outputWrong() {
        return new NewTopic(OUTPUT_TOPIC_WRONG, 10, (short) 1);
    }

    @Bean
    public NewTopic inputRight() {
        return new NewTopic(INPUT_TOPIC_RIGHT, 10, (short) 1);
    }

    @Bean
    public NewTopic outputRight() {
        return new NewTopic(OUTPUT_TOPIC_RIGHT, 10, (short) 1);
    }

    private void wrongDistinctTopology(StreamsBuilder streamsBuilder) {
        KStream<String, String> input =
                streamsBuilder.stream(INPUT_TOPIC_WRONG, Consumed.with(Serdes.String(), Serdes.String()));
        final String STOP_VALUE = "!STOP!";
        input.groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .reduce((first, second) -> STOP_VALUE)
                .toStream()
                .filter((key, value) -> !STOP_VALUE.equals(value))
                .to(OUTPUT_TOPIC_WRONG, Produced.with(Serdes.String(), Serdes.String()));
    }

    private void rightDistinctTopology(StreamsBuilder streamsBuilder) {
        KStream<String, String> input =
                streamsBuilder.stream(INPUT_TOPIC_RIGHT, Consumed.with(Serdes.String(), Serdes.String()));
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
                .to(OUTPUT_TOPIC_RIGHT, Produced.with(Serdes.String(), Serdes.String()));
    }

    @Bean
    public Topology createTopology(StreamsBuilder streamsBuilder) {
        wrongDistinctTopology(streamsBuilder);
        rightDistinctTopology(streamsBuilder);
        return streamsBuilder.build();
    }
}
