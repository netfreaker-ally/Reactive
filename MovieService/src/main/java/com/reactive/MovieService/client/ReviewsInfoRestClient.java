package com.reactive.MovieService.client;




import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.reactive.MovieService.domain.Review;
import com.reactive.MovieService.exception.MoviesInfoServerException;
import com.reactive.MovieService.exception.ReviewsClientException;
import com.reactive.MovieService.exception.ReviewsServerException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetrySpec;

@Component
@Slf4j
public class ReviewsInfoRestClient {

	@Autowired
	private WebClient webClient;
	@Value("${restClient.movieInfoUrl}")
	private String reviewsUrl;


	  public Flux<Review> retrieveReviews(String movieId){
		  Retry retrySpec = RetrySpec.fixedDelay(3, Duration.ofSeconds(1))
			        .filter((ex) -> ex instanceof ReviewsServerException)
			        .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> retrySignal.failure()));

	        var url = UriComponentsBuilder.fromHttpUrl(reviewsUrl)
	                .queryParam("movieInfoId", movieId)
	                .buildAndExpand().toString();

	        return webClient.get()
	                .uri(url)
	                .retrieve()
	                .onStatus(HttpStatusCode::is4xxClientError, (clientResponse -> {
	                    log.info("Status code : {}", clientResponse.statusCode().value());
	                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
	                        return Mono.empty();
	                    }
	                    return clientResponse.bodyToMono(String.class)
	                            .flatMap(response -> Mono.error(new ReviewsClientException(response)));
	                }))
	                .onStatus(HttpStatusCode::is5xxServerError, (clientResponse -> {
	                    log.info("Status code : {}", clientResponse.statusCode().value());
	                    return clientResponse.bodyToMono(String.class)
	                            .flatMap(response -> Mono.error(new ReviewsServerException(response)));
	                }))
	                .bodyToFlux(Review.class)
	                .retryWhen(retrySpec)
	                .log();

	    }

}
