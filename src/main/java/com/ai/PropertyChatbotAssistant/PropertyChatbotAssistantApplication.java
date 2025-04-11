package com.ai.PropertyChatbotAssistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PropertyChatbotAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(PropertyChatbotAssistantApplication.class, args);
	}

}
