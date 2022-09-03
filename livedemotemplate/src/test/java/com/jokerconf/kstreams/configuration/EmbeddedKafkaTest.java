package com.jokerconf.kstreams.configuration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.jokerconf.kstreams.configuration.TopicsConfiguration.INPUT_TOPIC_1;
import static com.jokerconf.kstreams.configuration.TopicsConfiguration.OUTPUT_TOPIC_1;

@SpringBootTest
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@Disabled
public class EmbeddedKafkaTest {

    @Autowired
    KafkaProperties kafkaProperties;

    @Test
    void testDistinct() {
        testTopology(INPUT_TOPIC_1, OUTPUT_TOPIC_1);
    }

    //TODO: add test for correct distinct implementation

    private void testTopology(String inputTopic, String outputTopic) {
        try (
                Producer<String, String> producer = configureProducer();
                Consumer<String, String> consumer = configureConsumer(outputTopic);
        ) {
            //TODO: write (and rewrite) the test
        }
    }

    private Consumer<String, String> configureConsumer(String outputTopicName) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                String.join(",", kafkaProperties.getBootstrapServers()), "testGroup", "true");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(), new StringDeserializer())
                .createConsumer();
        consumer.subscribe(Collections.singleton(outputTopicName));
        return consumer;
    }

    private Producer<String, String> configureProducer() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(
                String.join(",", kafkaProperties.getBootstrapServers())));
        return new DefaultKafkaProducerFactory<>(producerProps,
                new StringSerializer(), new StringSerializer()).createProducer();
    }
}
