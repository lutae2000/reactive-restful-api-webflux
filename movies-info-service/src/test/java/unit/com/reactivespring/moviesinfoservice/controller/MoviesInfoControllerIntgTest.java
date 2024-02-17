package com.reactivespring.moviesinfoservice.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import com.reactivespring.moviesinfoservice.service.MoviesInfoService;
import java.time.LocalDate;
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

@SpringBootTest
    (webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntgTest {

    String MOVIE_INFO_URL = "/v1/movieinfos";

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp(){
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        movieInfoRepository.saveAll(movieInfos)
            .blockLast();
    }

    @AfterEach
    void tearDown(){
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo(){
        //given
        var movieInfo = new MovieInfo(null, "Batman begins1", 2005, List.of("Christin Bale", "Michael cane"),LocalDate.parse("2005-06-15"));

        //when
        webTestClient.post()
            .uri(MOVIE_INFO_URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieInfo.class)
            .consumeWith(movieEntityExchangeReturn -> {
                var savedMovieInfo = movieEntityExchangeReturn.getResponseBody();
                assert savedMovieInfo != null;
                assert savedMovieInfo.getMovieInfoId() != null;
        });



        //then
    }

    @Test
    void getAllMovieInfos(){
        //given

        //when
        webTestClient.get()
            .uri(MOVIE_INFO_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(3);



        //then
    }

    @Test
    void getMovieInfoById(){
        //given
        var movieInfoId = "abc";

        //when
        webTestClient.get()
            .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Dark Knight Rises");
//            .expectBody(MovieInfo.class)
//            .consumeWith(movieInfoEntityExchangeResult -> {
//                var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
//                assertNotNull(movieInfo);
//            });



        //then
    }

    @Test
    void updateMovieInfo(){
        //given
        var movieInfoId = "abc";
        var movieInfo = new MovieInfo(null, "inception", 2008, List.of("Christina", "Micle"), LocalDate.parse("2008-01-01"));

        //when
        webTestClient.put()
            .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody()
            .jsonPath("$.name").isEqualTo("inception");
    }


    @Test
    void deleteMovieById(){
        //given
        var movieInfoId = "abc";

        //when
        webTestClient.delete()
            .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .isNoContent();
//            .expectBody()
//            .jsonPath("$.name").isEqualTo("Dark Knight Rises");
//            .expectBody(MovieInfo.class)
//            .consumeWith(movieInfoEntityExchangeResult -> {
//                var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
//                assertNotNull(movieInfo);
//            });



        //then
    }
}