package ru.curs.example.distinct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class DistinctApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistinctApplication.class, args);
	}

}

