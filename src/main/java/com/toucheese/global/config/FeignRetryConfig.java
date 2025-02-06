package com.toucheese.global.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRetryConfig {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 1500, 1);
    }
}
