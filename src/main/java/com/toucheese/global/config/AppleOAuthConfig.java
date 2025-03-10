package com.toucheese.global.config;



import com.toucheese.global.exception.FeignErrorDecoder;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppleOAuthConfig {
    @Bean
    public Request.Options options() {

        return new Request.Options(5000, 30000);// 커넥션 타임아웃 5초, 읽기 타임아웃 30초
    }

    @Bean
    Retryer.Default retryer() {
        return new Retryer.Default(1000, 1500, 1);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}

