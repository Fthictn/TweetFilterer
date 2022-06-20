package com.tweetconsumer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TweetRepository  extends MongoRepository<String,String> {
}
