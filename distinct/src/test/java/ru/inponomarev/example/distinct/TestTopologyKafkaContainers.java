package ru.inponomarev.example.distinct;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.inponomarev.example.distinct.configuration.TopologyConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest(classes = TestAppContainerConfig.class)
@ContextConfiguration(
        initializers = TestAppContainerConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestTopologyKafkaContainers extends BaseTest {

    @Autowired
    private KafkaProperties properties;

    @Test
    @Disabled
    public void wrongDistinctTopology() {
        String servers = String.join(",", properties.getBootstrapServers());
        System.out.printf("Kafka Container: %s%n", servers);
        this.testDistinct(servers, TopologyConfiguration.INPUT_TOPIC_WRONG, TopologyConfiguration.OUTPUT_TOPIC_WRONG);
    }

    @Test
    public void rightDistinctTopology() {
        String servers = String.join(",", properties.getBootstrapServers());
        System.out.printf("Kafka Container: %s%n", servers);
        this.testDistinct(servers, TopologyConfiguration.INPUT_TOPIC_RIGHT, TopologyConfiguration.OUTPUT_TOPIC_RIGHT);
    }



}
