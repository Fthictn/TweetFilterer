package com.tweetfilter;

import com.tweetfilter.service.ITweetService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

@SpringBootApplication
public class TweetfilterapiApplication implements ApplicationRunner {

	ITweetService tweetService;

	public TweetfilterapiApplication(ITweetService tweetService) {
		this.tweetService = tweetService;
	}

	public static void main(String[] args) {
		SpringApplication.run(TweetfilterapiApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<String> wordsToFilter = List.of("ekonomi", "franz kafka", "türkiye");
		tweetService.tweetReceiver(wordsToFilter);
	}
}
