package com.tweetfilter.service;

import com.tweetfilter.producer.ITweetProducer;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TweetServiceImpl implements ITweetService{

    ITweetProducer tweetProducer;

    public TweetServiceImpl(ITweetProducer tweetProducer) {
        this.tweetProducer = tweetProducer;
    }

    @Override
    public void tweetReceiver(List<String> terms) {
        tweetProducer.tweetProduce(terms);
    }
}
