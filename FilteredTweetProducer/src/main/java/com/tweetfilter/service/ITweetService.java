package com.tweetfilter.service;
import org.springframework.stereotype.Service;
import java.util.List;

public interface ITweetService {
    void tweetReceiver(List<String> terms);
}
