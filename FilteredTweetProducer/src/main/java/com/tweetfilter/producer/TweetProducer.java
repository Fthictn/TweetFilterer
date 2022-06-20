package com.tweetfilter.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TweetProducer implements ITweetProducer{

    KafkaTemplate<Integer, String> kafkaTemplate;
    ObjectMapper objectMapper;
    private String consumerKey = "CZib2bY2JwRMqinbLxpDVhtsQ";
    private String consumerSecret = "6wgkA8Gz1qtZhhGjTDQwGsFt4Sr2QLG7IaxAZLV818GWZwFKtP";
    private String token = "1428053550840651780-iQZiz9aCBEiATMjB68fwQ4DN1jvU6O";
    private String secret = "4TdFEEv0s50EgR9IfV66VPuAYFCN35r1J7xQd943yWLq2";

    BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);

    public TweetProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void tweetProduce(List<String> terms){
        Client client = createTwitterClient(terms);
        client.connect();

        while (!client.isDone()){
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                client.stop();
            }

            if(msg != null){
                log.info(msg);
                ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(1, msg);
                listenableFuture.addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        handleFailure(ex);
                    }

                    @Override
                    public void onSuccess(SendResult<Integer, String> result) {
                        handleSuccess(result);
                    }
                });
            }
        }

    }

    private Client createTwitterClient(List<String> terms){
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        hosebirdEndpoint.trackTerms(terms);

        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

        ClientBuilder builder = new ClientBuilder()
                .name("Twitter-client-for-filtering")
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        return builder.build();
    }

    private void handleSuccess(SendResult<Integer, String> result) {
        log.info(result.toString());
    }

    private void handleFailure(Throwable ex){
        log.info(ex.getMessage());
    }
}
