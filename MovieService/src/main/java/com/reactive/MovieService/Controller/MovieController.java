package com.reactive.MovieService.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactive.MovieService.client.MovieInfoRestClient;
import com.reactive.MovieService.client.ReviewsInfoRestClient;
import com.reactive.MovieService.domain.Movie;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MovieController {
	@Autowired
	private MovieInfoRestClient movieInfoRestClient;
	@Autowired
	private ReviewsInfoRestClient reviewsInfoRestClient;

	

	@GetMapping("/{id}")
	public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
		return movieInfoRestClient.retrieveMovieInfoId(movieId).flatMap(movieInfo -> {
			var reviewsMonoList = reviewsInfoRestClient.retrieveReviews(movieId).collectList();
			return reviewsMonoList.map(reviews -> new Movie(movieInfo, reviews));
		});
	}

}
