package ru.curs.example.distinct;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import ru.curs.example.distinct.configuration.KafkaConfiguration;
import ru.curs.example.distinct.configuration.TopologyConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWithTopologyTestDriver {
    private TestInputTopic<String, String> inputTopicWrong;
    private TestOutputTopic<String, String> outputTopicWrong;

    private TestInputTopic<String, String> inputTopicRight;
    private TestOutputTopic<String, String> outputTopicRight;
    private TopologyTestDriver topologyTestDriver;

    @BeforeEach
    public void setUp() {
        KafkaProperties properties = new KafkaProperties();
        properties.setBootstrapServers(singletonList("localhost:9092"));
        KafkaStreamsConfiguration config = new KafkaConfiguration(properties).getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration().createTopology(sb);
        topologyTestDriver = new TopologyTestDriver(topology, config.asProperties());
        inputTopicWrong =
                topologyTestDriver.createInputTopic(TopologyConfiguration.INPUT_TOPIC_WRONG, new StringSerializer(),
                        new StringSerializer());
        outputTopicWrong =
                topologyTestDriver.createOutputTopic(TopologyConfiguration.OUTPUT_TOPIC_WRONG, new StringDeserializer(),
                        new StringDeserializer());

        inputTopicRight =
                topologyTestDriver.createInputTopic(TopologyConfiguration.INPUT_TOPIC_RIGHT, new StringSerializer(),
                        new StringSerializer());
        outputTopicRight =
                topologyTestDriver.createOutputTopic(TopologyConfiguration.OUTPUT_TOPIC_RIGHT, new StringDeserializer(),
                        new StringDeserializer());
    }

    @AfterEach
    public void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    void testWrongDistinctTopology() {
        testTopology(inputTopicWrong, outputTopicWrong);
    }

    @Test
    void testRightDistinctTopology() {
        testTopology(inputTopicRight, outputTopicRight);
    }

    private void testTopology(TestInputTopic<String, String> inputTopic,
                              TestOutputTopic<String, String> outputTopic) {
        inputTopic.pipeKeyValueList(Arrays.asList(
                KeyValue.pair("A", "A"),
                KeyValue.pair("B", "B"),
                KeyValue.pair("B", "B"),
                KeyValue.pair("A", "A"),
                KeyValue.pair("C", "C")
        ));
        List<String> expected = Arrays.asList("A", "B", "C");
        List<String> actual = outputTopic.readValuesToList();
        assertEquals(expected, actual);
    }


}
