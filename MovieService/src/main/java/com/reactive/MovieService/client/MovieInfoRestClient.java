package com.reactive.MovieService.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.tomakehurst.wiremock.common.Exceptions;
import com.reactive.MovieService.domain.MovieInfo;
import com.reactive.MovieService.exception.MoviesInfoClientException;
import com.reactive.MovieService.exception.MoviesInfoServerException;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetrySpec;

@Component
public class MovieInfoRestClient {
	@Autowired
	private WebClient webClient;
	@Value("${restClient.movieInfoUrl}")
	private String movieinfourl;

	public Mono<MovieInfo> retrieveMovieInfoId(String movieInfoId) {
		var url = movieinfourl.concat("/{id}");
		Retry retrySpec = RetrySpec.fixedDelay(3, Duration.ofSeconds(1))
		        .filter((ex) -> ex instanceof MoviesInfoServerException)
		        .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> retrySignal.failure()));

		return webClient.get().uri(url, movieInfoId).retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
					if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.error(new MoviesInfoClientException(
								"There is no movie with this id: " + movieInfoId, clientResponse.statusCode().value()));
					}
					return clientResponse.bodyToMono(String.class).flatMap(response -> Mono
							.error(new MoviesInfoClientException(response, clientResponse.statusCode().value())));
				}).onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
					if (clientResponse.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
						return Mono.error(new MoviesInfoServerException("Internal server error occured"));
					}
					return clientResponse.bodyToMono(String.class).flatMap(
							response -> Mono.error(new MoviesInfoServerException("Internal server error occured")));
				}).bodyToMono(MovieInfo.class)
				//.retry(3)
				
				.retryWhen(retrySpec)
				.log();
	}

}
