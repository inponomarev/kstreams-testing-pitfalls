package com.jokerconf.kstreams.configuration;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopologyTTDTest {

    private TestInputTopic<String, String> inputTopic;
    private TestOutputTopic<String, String> outputTopic;
    private TopologyTestDriver testDriver;

    @BeforeEach
    void setUp() {
        KafkaProperties kafkaProperties = new KafkaProperties();
        StreamsBuilder sb = new StreamsBuilder();
        testDriver = new TopologyTestDriver(new TopologyConfiguration().getTopology(sb),
                new KafkaConfiguration(kafkaProperties).getStreamsConfig().asProperties());
        inputTopic = testDriver.createInputTopic(TopicsConfiguration.INPUT_TOPIC_1,
                new StringSerializer(), new StringSerializer());
        outputTopic = testDriver.createOutputTopic(TopicsConfiguration.OUTPUT_TOPIC_1,
                new StringDeserializer(), new StringDeserializer());
    }

    @AfterEach
    void tearDown() {
        testDriver.close();
    }

    @Test
    void testDeduplicationTopology() {
        inputTopic.pipeKeyValueList(
                Stream.of("A", "B", "B", "A")
                        .map(x -> KeyValue.pair(x, x)).collect(Collectors.toList()));
        List<String> values = outputTopic.readValuesToList();
        Assertions.assertEquals(List.of("A", "B"), values);
    }

}
