package com.toucheese;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.toucheese.member.client")
@EnableScheduling
public class ToucheeseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToucheeseApplication.class, args);
	}

}
