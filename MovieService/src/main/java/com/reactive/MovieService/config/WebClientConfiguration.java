package com.reactive.MovieService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

	public WebClientConfiguration() {
		// TODO Auto-generated constructor stub
	}
	@Bean
	public WebClient webclient(WebClient.Builder builder) {
		return builder.build();
	}
}
