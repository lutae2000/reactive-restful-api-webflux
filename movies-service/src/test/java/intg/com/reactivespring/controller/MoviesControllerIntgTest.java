package com.reactivespring.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(
    properties = {
        "restClient.movieInfoUrl=http://localhost:8084/v1/movieinfos",
        "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
    }
)
public class MoviesControllerIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void retrieveMovieById(){
        //given
        var movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
            .willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("movieinfo.json")
            )
        );

        stubFor(get(urlPathEqualTo("/v1/reviews"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("reviews.json")
            )
        );

        //when
        webTestClient.get()
            .uri("/v1/movies/{id}", movieId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Movie.class)
            .consumeWith(movieEntityExchangeResult -> {
                var movie = movieEntityExchangeResult.getResponseBody();
                assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                assertEquals("Batman Begins", movie.getMovieInfo().getName());

            });

        //then
    }

    @Test
    void retrieveMovieById_404(){
        //given
        var movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
            .willReturn(aResponse()
                .withStatus(404)
            )
        );

        stubFor(get(urlPathEqualTo("/v1/reviews"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("reviews.json")
            )
        );

        //when
        webTestClient.get()
            .uri("/v1/movies/{id}", movieId)
            .exchange()
            .expectStatus()
            .is4xxClientError()
            .expectBody(String.class)
            .isEqualTo("There is no MovieInfo available for the passed in Id : abc");

        //then
    }

    @Test
    void retrieveMovieById_review_404(){
        //given
        var movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos" + "/" + movieId))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("movieinfo.json")
            )
        );

        stubFor(get(urlPathEqualTo("/v1/reviews"))
            .willReturn(aResponse()
                .withStatus(404)
            )
        );

        //when
        webTestClient.get()
            .uri("/v1/movies/{id}", movieId)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Movie.class)
            .consumeWith(movieEntityExchangeResult -> {
                var movie = movieEntityExchangeResult.getResponseBody();
                assert Objects.requireNonNull(movie).getReviewList().isEmpty();
                assertEquals("Batman Begins", movie.getMovieInfo().getName());

            });
        //then
    }


    @Test
    void retrieveMovieById_Reviews_404() {
        //given
        var movieId = "abc";
        stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
            .withQueryParam("movieInfoId", equalTo(movieId))
            .willReturn(aResponse()
                .withStatus(404)));


        //when
        webTestClient.get()
            .uri("/v1/movies/{id}", "abc")
            .exchange()
            .expectStatus().is2xxSuccessful();
        //then
        // WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movieinfos/" + movieId)));;
    }

    @Test
    void retrieveMovieById_5XX() {
        //given
        var movieId = "abc";
        stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Server Exception in MovieInfoService Server Exception in MovieInfoService MovieInfo Service Unavailable")));

        /*stubFor(get(urlPathEqualTo("/v1/reviews"))
                .withQueryParam("movieInfoId", equalTo(movieId))
                .willReturn(aResponse()
                        .withStatus(500)));
*/

        //when
        webTestClient.get()
            .uri("/v1/movies/{id}", "abc")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody(String.class)
            .value(message -> {
                assertEquals("Server Exception in MovieInfoService Server Exception in MovieInfoService MovieInfo Service Unavailable", message);
            });
        //then

        WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movieinfos/" + movieId)));
    }

    @Test
    void retrieveMovieById_reviews_5XX() {
        //given
        var movieId = "abc";
        stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBodyFile("movieinfo.json")));


        stubFor(get(urlPathEqualTo("/v1/reviews"))
            .withQueryParam("movieInfoId", equalTo(movieId))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Review Service Unavailable")));

        //when
        webTestClient.get()
            .uri("/v1/movies/{id}", "abc")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody(String.class)
            .value(message -> {
                assertEquals("Review Service Unavailable", message);
            });
        //then

        WireMock.verify(4, getRequestedFor(urlPathMatching("/v1/reviews*")));
    }
}
