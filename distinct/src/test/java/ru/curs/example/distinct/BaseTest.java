package ru.curs.example.distinct;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseTest {
    protected void testDistinct(String bootstrapServers, String inputTopicName, String outputTopicName) {
        try (Consumer<String, String> consumer = configureConsumer(bootstrapServers, outputTopicName);
             Producer<String, String> producer = configureProducer(bootstrapServers)) {
            producer.send(new ProducerRecord<>(inputTopicName, "A", "A"));
            producer.send(new ProducerRecord<>(inputTopicName, "B", "B"));
            producer.send(new ProducerRecord<>(inputTopicName, "B", "B"));
            producer.send(new ProducerRecord<>(inputTopicName, "A", "A"));
            producer.send(new ProducerRecord<>(inputTopicName, "C", "C"));
            producer.flush();
            List<String> actual = new ArrayList<>();

            while (true) {
                ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, 5000);
                if (records.isEmpty()) break;
                for (ConsumerRecord<String, String> rec : records) {
                    actual.add(rec.value());
                }
            }
            List<String> expected = Arrays.asList("A", "B", "C");
            Collections.sort(actual);
            assertEquals(expected, actual);
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
