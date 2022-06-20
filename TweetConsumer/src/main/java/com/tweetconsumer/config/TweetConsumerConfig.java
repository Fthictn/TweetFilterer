package com.tweetconsumer.config;

import com.tweetconsumer.service.TweetPersistentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class TweetConsumerConfig {

    TweetPersistentService service;

        public TweetConsumerConfig(TweetPersistentService service) {
            this.service = service;
        }

        @Bean
        ConcurrentKafkaListenerContainerFactory<?,?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
                                                                                   ConsumerFactory<Object, Object> kafkaConsumerFactory){
            ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
            configurer.configure(factory, kafkaConsumerFactory);
            factory.setConcurrency(3);
            factory.setRetryTemplate(retryTemplate());
            factory.setRecoveryCallback((context -> {
                if(context.getLastThrowable().getCause() instanceof RecoverableDataAccessException){
                    ConsumerRecord<Integer, String> consumerRecord = (ConsumerRecord<Integer, String>) context.getAttribute("record");
                    service.handleRecovery(consumerRecord);
                }else{
                    log.info("Inside the non recoverable logic");
                    throw new RuntimeException(context.getLastThrowable().getMessage());
                }


                return null;
            }));
            return factory;
        }

        private RetryTemplate retryTemplate() {

            FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
            fixedBackOffPolicy.setBackOffPeriod(1000);
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setRetryPolicy(simpleRetryPolicy());
            retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
            return retryTemplate;

        }

        private RetryPolicy simpleRetryPolicy() {
            Map<Class<? extends Throwable>, Boolean> exceptionsMap = new HashMap<>();
            exceptionsMap.put(IllegalArgumentException.class, false);
            exceptionsMap.put(RecoverableDataAccessException.class, true);
            return new SimpleRetryPolicy(3, exceptionsMap, true);
        }
}
