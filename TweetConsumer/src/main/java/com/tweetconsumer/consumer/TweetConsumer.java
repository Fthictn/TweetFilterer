package com.tweetconsumer.consumer;

import com.tweetconsumer.service.TweetPersistentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TweetConsumer {
    private final TweetPersistentService service;

    public TweetConsumer(TweetPersistentService service) {
        this.service = service;
    }

    @KafkaListener(topics = {"tweet"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord){
        log.info("ConsumerRecord : {} ", consumerRecord);
        service.processTweets(consumerRecord);
    }
}
