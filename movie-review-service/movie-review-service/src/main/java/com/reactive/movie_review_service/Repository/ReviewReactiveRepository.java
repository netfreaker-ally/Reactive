package com.reactive.movie_review_service.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.reactive.movie_review_service.domain.Review;

import reactor.core.publisher.Flux;
@Repository
public interface ReviewReactiveRepository extends ReactiveCrudRepository<Review, String> {
	  Flux<Review> findReviewsByMovieInfoId(Long movieInfoId);
}
