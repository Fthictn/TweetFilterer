package com.tweetfilter.controller;

import com.tweetfilter.service.ITweetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/tweets")
public class TweetController {

    ITweetService tweetService;

    public TweetController(ITweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping("/filter")
    public ResponseEntity<String> tweetFilter(@RequestBody List<String> terms){
        tweetService.tweetReceiver(terms);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tweets producing");
    }
}
