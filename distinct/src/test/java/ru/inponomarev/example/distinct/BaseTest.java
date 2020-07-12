package ru.inponomarev.example.distinct;

import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class BaseTest {
    @SneakyThrows
    protected void testDistinct(String bootstrapServers, String inputTopicName, String outputTopicName) {
        try (Consumer<String, String> consumer = configureConsumer(bootstrapServers, outputTopicName);
             Producer<String, String> producer = configureProducer(bootstrapServers)) {

            List.of("A", "B", "B", "A", "C")
                    .stream().map(e->new ProducerRecord<>(inputTopicName, e, e))
                    .forEach(producer::send);
            producer.flush();

            //We are using thread-safe data structure here, since it's shared between consumer and verifier
            List<String> actual = new CopyOnWriteArrayList<>();
            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<?> consumingTask = service.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, 100);
                    for (ConsumerRecord<String, String> rec : records) {
                        actual.add(rec.value());
                    }
                }
            });

            try {
                Awaitility.await().atMost(5, SECONDS)
                        .until(() -> List.of("A", "B", "C").equals(actual));
            } finally {
                consumingTask.cancel(true);
                service.awaitTermination(100, MILLISECONDS);
            }
        }
    }


    private Consumer<String, String> configureConsumer(String bootstrapServers, String outputTopicName) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                String.join(",", bootstrapServers), "testGroup", "true");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(), new StringDeserializer())
                .createConsumer();
        consumer.subscribe(Collections.singleton(outputTopicName));
        return consumer;
    }

    private Producer<String, String> configureProducer(String bootstrapServers) {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(
                String.join(",", bootstrapServers)));
        return new DefaultKafkaProducerFactory<>(producerProps,
                new StringSerializer(), new StringSerializer()).createProducer();
    }
}
