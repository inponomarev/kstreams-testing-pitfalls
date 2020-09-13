package com.jokerconf.kstreams.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicsConfiguration {
    public static final String INPUT_TOPIC_1 = "input-1";
    public static final String OUTPUT_TOPIC_1 = "output-1";

    public static final String INPUT_TOPIC_2 = "input-2";
    public static final String OUTPUT_TOPIC_2 = "output-2";

    @Bean
    public NewTopic input1() {
        return new NewTopic(INPUT_TOPIC_1, 1, (short) 1);
    }

    @Bean
    public NewTopic output1() {
        return new NewTopic(OUTPUT_TOPIC_1, 1, (short) 1);
    }

    @Bean
    public NewTopic input2() {
        return new NewTopic(INPUT_TOPIC_2, 1, (short) 1);
    }

    @Bean
    public NewTopic output2() {
        return new NewTopic(OUTPUT_TOPIC_2, 1, (short) 1);
    }

}
