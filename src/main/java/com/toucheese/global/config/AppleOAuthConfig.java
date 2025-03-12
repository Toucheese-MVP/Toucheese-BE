package com.toucheese.global.config;



import com.toucheese.global.exception.FeignErrorDecoder;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AppleOAuthConfig {
    @Bean
    public Request.Options options() {
        return new Request.Options(
                Duration.ofSeconds(10), // 연결 타임아웃
                Duration.ofSeconds(60), // 읽기 타임아웃
                true); // 리다이렉트 팔로우 여부
    }

    @Bean
    Retryer.Default retryer() {
        return new Retryer.Default(1000, 5000, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}

