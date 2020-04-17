package ru.curs.example.distinct;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.KafkaContainer;

@TestConfiguration
public class TestAppContainerConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private final KafkaContainer kafkaContainer;

    TestAppContainerConfig() {
        kafkaContainer = new KafkaContainer();
        kafkaContainer.start();
    }

    @Bean
    public KafkaContainer kafka() {
        return kafkaContainer;
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers());
    }
}
