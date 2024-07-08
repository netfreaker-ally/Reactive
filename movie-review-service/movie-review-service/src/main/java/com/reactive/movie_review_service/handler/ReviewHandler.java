package com.reactive.movie_review_service.handler;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.movie_review_service.Repository.ReviewReactiveRepository;
import com.reactive.movie_review_service.domain.Review;
import com.reactive.movie_review_service.exception.ReviewDataException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
@Slf4j
public class ReviewHandler {
    private ReviewReactiveRepository reviewReactiveRepository;
    //private ReviewValidator reviewValidator;

    Sinks.Many<Review> reviewsSink = Sinks.many().replay().latest();

    @Autowired
    private Validator validator;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

 /*    public ReviewsHandler(ReviewReactiveRepository reviewReactiveRepository, ReviewValidator reviewValidator) {
        this.reviewReactiveRepository = reviewReactiveRepository;
        this.reviewValidator = reviewValidator;
    }*/


    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();


    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {
        var movieInfoId = serverRequest.queryParam("movieInfoId");
        if (movieInfoId.isPresent()) {
            var reviews = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return buildReviewsResponse(reviews);
        } else {
            var reviews = reviewReactiveRepository.findAll();
            return buildReviewsResponse(reviews);
        }
    }

    private Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviews) {
        return ServerResponse.ok()
                .body(reviews, Review.class);
    }

    public Mono<ServerResponse> addReview(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(review -> reviewReactiveRepository.save(review))
                .doOnNext(review -> {
                    reviewsSink.tryEmitNext(review);
                })
                .flatMap(savedReview ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .bodyValue(savedReview));
    }

    private void validate(Review review) {
        Errors errors = new BeanPropertyBindingResult(review, "review");
       /* reviewValidator.validate(review, errors);
        if (errors.hasErrors()) {
            var errorMessage = errors.getAllErrors()
                    .stream()
                    .map(error -> error.getCode() + " : " + error.getDefaultMessage())
                    .sorted()
                    .collect(Collectors.joining(", "));
            log.info("errorMessage : {} ", errorMessage);
            throw new ReviewDataException(errorMessage);
        }*/

        var constraintViolations = validator.validate(review);
     //   log.info("constraintViolations : {} ", constraintViolations);
        if (constraintViolations.size() > 0) {
            var errorMessage = constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(", "));
           // log.info("errorMessage : {} ", errorMessage);
            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {

        var reviewId = serverRequest.pathVariable("id");

        var existingReview = reviewReactiveRepository.findById(reviewId);
        //.switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not Found for the given Review Id")));

        return existingReview
                .flatMap(review -> serverRequest.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        })
                        .flatMap(reviewReactiveRepository::save)
                        .flatMap(savedReview ->
                                ServerResponse.status(HttpStatus.OK)
                                        .bodyValue(savedReview)))
                .switchIfEmpty(notFound);


    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        return reviewReactiveRepository.findById(reviewId)
                .flatMap(review -> reviewReactiveRepository.deleteById(reviewId))
                .then(ServerResponse.noContent().build());

    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewsSink.asFlux(), Review.class)
                .log();


    }
}