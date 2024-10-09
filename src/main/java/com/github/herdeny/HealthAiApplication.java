package com.github.herdeny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HealthAiApplication {

	@Value("${server.port}")
 	private int port;

	public static void main(String[] args) {
		SpringApplication.run(HealthAiApplication.class, args);
	}

}
