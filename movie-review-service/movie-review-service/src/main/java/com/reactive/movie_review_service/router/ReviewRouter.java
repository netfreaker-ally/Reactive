package com.reactive.movie_review_service.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.movie_review_service.handler.ReviewHandler;

@Configuration
public class ReviewRouter {
	@Bean
	public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewsHandler) {
	    return route()
	        .path("/v1/reviews", builder ->
	            builder
	                .GET("", reviewsHandler::getReviews)
	                .POST("", reviewsHandler::addReview)
	                .PUT("/{id}", reviewsHandler::updateReview)
	                .DELETE("/{id}", reviewsHandler::deleteReview)
	                .GET("/stream", reviewsHandler::getReviewsStream))
	        
	        .GET("/v1/helloworld", request -> ServerResponse.ok().bodyValue("HelloWorld"))
	        
	        .GET("/v1/greeting/{name}", request ->
	            ServerResponse.ok().bodyValue("hello " + request.pathVariable("name")))
	        
	        .build();
	}
}
