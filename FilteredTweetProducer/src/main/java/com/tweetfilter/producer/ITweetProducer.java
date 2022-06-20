package com.tweetfilter.producer;

import org.springframework.stereotype.Component;
import java.util.List;

public interface ITweetProducer {
    public void tweetProduce(List<String> terms);
}
