package com.ravdes.unforkedrepositories.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;



@Configuration

public class AppConfig {

	// Doesn't make sense to create new client with each request so im defining the bean
	// with default header provided in assignment and header filter

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
						.baseUrl("https://api.github.com")
						.defaultHeader("Accept", "application/json")
						.filter(ClientHeaderFilter.apply())
						.build();

	}



}
