package ru.curs.example.distinct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static ru.curs.example.distinct.configuration.TopologyConfiguration.INPUT_TOPIC_RIGHT;
import static ru.curs.example.distinct.configuration.TopologyConfiguration.INPUT_TOPIC_WRONG;
import static ru.curs.example.distinct.configuration.TopologyConfiguration.OUTPUT_TOPIC_RIGHT;
import static ru.curs.example.distinct.configuration.TopologyConfiguration.OUTPUT_TOPIC_WRONG;

@SpringBootTest
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestTopologyKafkaEmbedded extends BaseTest {
    @Autowired
    private KafkaProperties properties;

    @Test
    public void testWrongDistinct() {
        String servers = String.join(",", properties.getBootstrapServers());
        System.out.printf("Kafka Container: %s%n", servers);
        this.testDistinct(servers, INPUT_TOPIC_WRONG, OUTPUT_TOPIC_WRONG);
    }

    @Test
    public void testRightDistinct() {
        String servers = String.join(",", properties.getBootstrapServers());
        System.out.printf("Kafka Container: %s%n", servers);
        this.testDistinct(servers, INPUT_TOPIC_RIGHT, OUTPUT_TOPIC_RIGHT);
    }

}
