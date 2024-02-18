package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    static String REVIEWS_URL = "/v1/reviews";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {

        var reviewsList = List.of(
            new Review(null, 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie1", 9.0),
            new Review(null, 2L, "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
            .blockLast();
    }

    @AfterEach
    void tearDown(){
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview(){
        //given
        var review = new Review(null, null, "Awesome Movie", 9.0);

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
}
