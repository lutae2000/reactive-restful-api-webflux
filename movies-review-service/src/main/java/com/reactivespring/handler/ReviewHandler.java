package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;
    private ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
            .doOnNext(this::validate)
            .flatMap(reviewReactiveRepository::save)
            .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)
            .log();
    }

    private void validate(Review review) {
        var constraintViolations = validator.validate(review);
        log.info("constraintViolations : {} ", constraintViolations);

        if(constraintViolations.size() > 0){
            var errorMessage = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
        }

    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {

        var movieInfoId = request.queryParam("movieInfoId");

        if(movieInfoId.isPresent()){
            var reviewsFlux = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get())).log();
            return buildReviewsResponse(reviewsFlux);
        } else {
            var reviewFlux = reviewReactiveRepository.findAll().log();
            return buildReviewsResponse(reviewFlux);
        }

    }

    private static Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviewFlux) {
        return ServerResponse.ok().body(reviewFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request){
        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId)
            .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given Review Id: " + reviewId)))
            .log();

        return existingReview.flatMap(review -> request.bodyToMono(Review.class)
            .map(requestReview -> {
                review.setComment(requestReview.getComment());
                review.setRating(requestReview.getRating());
                return review;
            })
            .flatMap(reviewReactiveRepository::save)
            .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
            .log()
        )
        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request){
        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);

        return existingReview.flatMap(review ->
            reviewReactiveRepository.deleteById(reviewId)
                .then(ServerResponse.noContent().build())
        );
    }
}
