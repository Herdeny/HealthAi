package com.github.herdeny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HealthAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthAiApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		return application.sources(HealthAiApplication.class);
	}

}
