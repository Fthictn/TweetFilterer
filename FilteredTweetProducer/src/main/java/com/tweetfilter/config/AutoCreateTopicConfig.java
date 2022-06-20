package com.tweetfilter.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateTopicConfig {
    @Bean
    @ConditionalOnProperty(name="create.topic", havingValue="true")
    public NewTopic tweetTopicCreator(){
        return TopicBuilder.name("tweet")
                .partitions(3)
                .replicas(3)
                .build();
    }
}
