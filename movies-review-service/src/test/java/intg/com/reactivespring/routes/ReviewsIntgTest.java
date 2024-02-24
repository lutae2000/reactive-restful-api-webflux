package com.reactivespring.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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

    @Test
    void getAllReview(){
        //given


        //when
        webTestClient.get()
            .uri(REVIEWS_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .hasSize(3)
            .consumeWith(listEntityExchangeResult -> {
                var getReviews  = listEntityExchangeResult.getResponseBody();
                assert getReviews != null;
                assert getReviews.get(1).getComment() != null;
            });
    }

    @Test
    void getReviews(){
        //given
        var review = 1L;


        String reviewUri = UriComponentsBuilder.fromUriString(REVIEWS_URL)
            .queryParam("movieInfoId", review)
            .build()
            .toUriString();

        //when
        webTestClient.get()
            .uri(reviewUri)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            ;

/*        webTestClient.get()
            .uri(uriBuilder -> {
                return uriBuilder.path(REVIEWS_URL)
                    .queryParam("movieInfoId", review)
                    .build();
            })
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class);*/
    }

    @Test
    void updateReview(){
        //given
        var beforeUpdateReview = new Review(null, 1L, "Inception", 9.0);
        var updateReview = new Review(null, 1L, "edited Review", 8.0);
        var savedReview = reviewReactiveRepository.save(beforeUpdateReview).block();


        //when
        webTestClient.put()
            .uri(REVIEWS_URL + "/{id}",savedReview.getReviewId())
            .bodyValue(updateReview)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Review.class)
            .consumeWith(movieInfoEntityExchangeResult -> {
                var updatedReview = movieInfoEntityExchangeResult.getResponseBody();
                assert updatedReview != null;
                assertNotNull(savedReview.getReviewId());
                assertEquals(8.0, updatedReview.getRating());
                assertEquals("edited Review", updatedReview.getComment());
            });

        //then
    }

    @Test
    void deleteReview(){
        //given
        var beforeUpdateReview = new Review(null, 1L, "Inception", 9.0);
        var savedReview = reviewReactiveRepository.save(beforeUpdateReview).block();


        //when
        webTestClient.delete()
            .uri(REVIEWS_URL + "/{id}", savedReview.getReviewId())
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}
