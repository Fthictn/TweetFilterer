package com.tweetfilter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
//@Configuration
//@ConfigurationProperties(prefix = "twitter-auth")
public class TwitterAuthParams {
    private String consumerKey;
    private String consumerSecret;
    private String token;
    private String secret;
}
