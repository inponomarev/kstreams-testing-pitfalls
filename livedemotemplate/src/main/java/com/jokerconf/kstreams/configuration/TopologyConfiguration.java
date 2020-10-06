package com.jokerconf.kstreams.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopologyConfiguration {
    @Bean
    public Topology createTopology(StreamsBuilder sb) {
        //TODO: Topology

        return sb.build();
    }

}
