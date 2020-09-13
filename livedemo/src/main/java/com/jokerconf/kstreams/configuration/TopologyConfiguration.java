package com.jokerconf.kstreams.configuration;

import com.jokerconf.kstreams.dedup.DeduplicationTransformer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopologyConfiguration {
    @Bean
    Topology getTopology(StreamsBuilder builder) {
        builder.stream(TopicsConfiguration.INPUT_TOPIC_1, Consumed.with(Serdes.String(), Serdes.String()))
                .groupByKey().reduce((l, r) -> "<STOP>").toStream().filterNot((k, v) -> "<STOP>".equals(v))
                .to(TopicsConfiguration.OUTPUT_TOPIC_1);

        DeduplicationTransformer.buildTopology(builder);
        Topology topology = builder.build();
        System.out.println("!!!!!!!!!!!");
        System.out.println(topology.describe());
        System.out.println("!!!!!!!!!!!");
        return topology;
    }
}
