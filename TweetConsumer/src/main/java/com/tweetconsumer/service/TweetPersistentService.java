package com.tweetconsumer.service;

import com.tweetconsumer.repository.TweetRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class TweetPersistentService {

    KafkaTemplate<Integer,String> kafkaTemplate;
    TweetRepository repository;
    public TweetPersistentService(KafkaTemplate<Integer,String> kafkaTemplate, TweetRepository repository) {
        this.kafkaTemplate = kafkaTemplate;
        this.repository = repository;
    }

    public void processTweets(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("tweet : {} ", consumerRecord.value());
    }

    public void handleRecovery(ConsumerRecord<Integer,String> record){

        Integer key = record.key();
        String message = record.value();

        ListenableFuture<SendResult<Integer,String>> listenableFuture = kafkaTemplate.sendDefault(key, message);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, message, result);
            }
        });
    }

    private void handleFailure(Throwable ex) {
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
    }
}
