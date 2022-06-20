package com.tweetconsumer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface TweetRepository  extends MongoRepository<String,String> {
}
