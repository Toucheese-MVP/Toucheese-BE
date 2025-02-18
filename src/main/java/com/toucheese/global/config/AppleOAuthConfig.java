package com.toucheese.global.config;



import com.toucheese.global.exception.FeignErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppleOAuthConfig {
    @Bean
    Retryer.Default retryer() {
        return new Retryer.Default(1000, 1500, 1);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}



