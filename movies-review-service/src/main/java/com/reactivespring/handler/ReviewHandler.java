package com.reactivespring.handler;

import static org.springframework.http.ResponseEntity.status;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {

    private ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
            .flatMap(reviewReactiveRepository::save)
            .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)
            .log();
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
        var existingReview = reviewReactiveRepository.findById(reviewId).log();

        return existingReview.flatMap(review -> request.bodyToMono(Review.class)
            .map(requestReview -> {
                review.setComment(requestReview.getComment());
                review.setRating(requestReview.getRating());
                return review;
            })
            .flatMap(reviewReactiveRepository::save)
            .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
            .log()
        );
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
