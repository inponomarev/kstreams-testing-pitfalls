package com.jokerconf.kstreams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
