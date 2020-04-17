package ru.curs.example.distinct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.curs.example.distinct.configuration.TopologyConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest(classes = TestAppContainerConfig.class)
@ContextConfiguration(
        initializers = TestAppContainerConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestTopologyKafkaContainers extends BaseTest {

    private final static Path STATE_DIR =
            Paths.get(System.getProperty("user.dir"), "build");

    @Autowired
    private KafkaProperties properties;

    @Test
    public void testWrongDistinct() {
        String servers = String.join(",", properties.getBootstrapServers());
        System.out.printf("Kafka Container: %s%n", servers);
        this.testDistinct(servers, TopologyConfiguration.INPUT_TOPIC_WRONG, TopologyConfiguration.OUTPUT_TOPIC_WRONG);
    }

    @Test
    public void testRightDistinct() {
        String servers = String.join(",", properties.getBootstrapServers());
        System.out.printf("Kafka Container: %s%n", servers);
        this.testDistinct(servers, TopologyConfiguration.INPUT_TOPIC_RIGHT, TopologyConfiguration.OUTPUT_TOPIC_RIGHT);
    }



}
