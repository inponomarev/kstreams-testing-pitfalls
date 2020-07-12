package ru.inponomarev.example.distinct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static ru.inponomarev.example.distinct.configuration.TopologyConfiguration.INPUT_TOPIC_RIGHT;
import static ru.inponomarev.example.distinct.configuration.TopologyConfiguration.INPUT_TOPIC_WRONG;
import static ru.inponomarev.example.distinct.configuration.TopologyConfiguration.OUTPUT_TOPIC_RIGHT;
import static ru.inponomarev.example.distinct.configuration.TopologyConfiguration.OUTPUT_TOPIC_WRONG;

@SpringBootTest
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestTopologyKafkaEmbedded extends BaseTest {
    @Autowired
    private KafkaProperties properties;

    @Test
    public void wrongDistinctTopology() {
        String servers = String.join(",", properties.getBootstrapServers());
        System.out.printf("Kafka Container: %s%n", servers);
        this.testDistinct(servers, INPUT_TOPIC_WRONG, OUTPUT_TOPIC_WRONG);
    }

    @Test
    public void rightDistinctTopology() {
        String servers = String.join(",", properties.getBootstrapServers());
        System.out.printf("Kafka Container: %s%n", servers);
        this.testDistinct(servers, INPUT_TOPIC_RIGHT, OUTPUT_TOPIC_RIGHT);
    }

}
