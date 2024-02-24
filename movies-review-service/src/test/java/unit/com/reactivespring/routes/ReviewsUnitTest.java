package com.reactivespring.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.handler.ReviewRouter;
import com.reactivespring.repository.ReviewReactiveRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.Mockito.*;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {
    static String REVIEWS_URL = "/v1/reviews";

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void addReview(){
        //given
        var review = new Review(null, null, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", null, "Awesome Movie", 9.0)));

        //when
        webTestClient.post()
            .uri(REVIEWS_URL)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Review.class)
            .consumeWith(movieInfoEntityExchangeResult -> {
                var savedReview = movieInfoEntityExchangeResult.getResponseBody();
                assert savedReview != null;
                assert savedReview.getReviewId() != null;
            });

        //then
    }

    @Test
    void getAllReview(){
        //given
        var reviewsList = List.of(
            new Review(null, 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie1", 9.0),
            new Review(null, 2L, "Excellent Movie", 8.0));

        when(reviewReactiveRepository.findAll())
            .thenReturn(Flux.fromIterable(reviewsList));

        //when
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(REVIEWS_URL)
                .build())
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .value(reviews -> {
                assertEquals(3, reviews.size());
            });
    }

    @Test
    void updateReview(){
        //given
        var review = new Review(null, null, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", 1L, "Not an Awesome Movie", 8.0)));

        when(reviewReactiveRepository.findById((String) any()))
            .thenReturn(Mono.just(new Review("abc", 1L, "Awesome movie", 9.0)));

        //when
        webTestClient.put()
            .uri(uriBuilder -> uriBuilder.path(REVIEWS_URL)
                .path("/{id}")
                .build( "abc"))
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Review.class)
            .consumeWith(reviewEntityExchangeResult -> {
                var updatedReview = reviewEntityExchangeResult.getResponseBody();
                assert updatedReview != null;
                System.out.println("updatedReview : "+ updatedReview);
                assertEquals(8.0, updatedReview.getRating());
                assertEquals("Not an Awesome Movie", updatedReview.getComment());
            })
            ;
    }

    @Test
    void deleteReview(){
        //given
        var reviewId = "abc";
        when(reviewReactiveRepository.findById((String) any()))
            .thenReturn(Mono.just(new Review("abc", 1L, "Awesome movie", 9.0)));
        when(reviewReactiveRepository.deleteById(reviewId))
            .thenReturn(Mono.empty());

        webTestClient.delete()
            .uri(uriBuilder -> uriBuilder.path(REVIEWS_URL)
                .path("/{id}")
                .build(reviewId))
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}
