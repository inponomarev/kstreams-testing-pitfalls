package com.jokerconf.kstreams.configuration;

import com.jokerconf.kstreams.configuration.KafkaConfiguration;
import com.jokerconf.kstreams.configuration.TopicsConfiguration;
import com.jokerconf.kstreams.configuration.TopologyConfiguration;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopologyTTDTest {

    private TestInputTopic<String, String> inputTopic;
    private TestOutputTopic<String, String> outputTopic;
    private TopologyTestDriver testDriver;

    @BeforeEach
    void setUp() {
        StreamsBuilder sb = new StreamsBuilder();
        Properties properties = new KafkaConfiguration(new KafkaProperties()).getStreamsConfig().asProperties();
        //TODO
    }

    //TODO: tearDown

    //TODO: testDeduplicationTopology

}
