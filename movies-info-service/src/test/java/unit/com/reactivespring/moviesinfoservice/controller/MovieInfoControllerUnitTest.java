package com.reactivespring.moviesinfoservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MoviesInfoService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    String MOVIE_INFO_URL = "/v1/movieinfos";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoService;

    @Test
    void getAllMoviesInfo(){

        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        when(moviesInfoService.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient.get()
            .uri(MOVIE_INFO_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(3);
    }

    @Test
    void getMovieInfoById(){
        var movieInfoId = "abc";


        when(moviesInfoService.getMovieInfoById(isA(String.class)))
            .thenReturn(Mono.just(
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
                )
            );

        //when
        webTestClient.get()
            .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody()
            .consumeWith(movieInfoResult ->{
                var movieInfo = movieInfoResult.getResponseBody();
                assertNotNull(movieInfo);
            })
            .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    void addMovieInfo(){
        //given
        var movieInfo = new MovieInfo(null, "Avatar", 2009
            , List.of("Neteyam", "Jake Sully", "Neytiri", "Kiri", "Lo'ak", "Ronal", "Tsireya"),LocalDate.parse("2009-12-17") );

        when(moviesInfoService.addMovieInfo(isA(MovieInfo.class))).thenReturn(
            Mono.just(new MovieInfo("mockId", "Avatar", 2009
                , List.of("Neteyam", "Jake Sully", "Neytiri", "Kiri", "Lo'ak", "Ronal", "Tsireya"),LocalDate.parse("2009-12-17") ))
        );

        //when
        webTestClient.post()
            .uri(MOVIE_INFO_URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityExchangeResult -> {
                var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                assert savedMovieInfo != null;
                assert savedMovieInfo.getMovieInfoId() != null;
                assertEquals("mockId", savedMovieInfo.getMovieInfoId());

            });
    }

    @Test
    void updateMovieInfo(){
        //given
        var movieInfoId = "abc";
        //given
        var movieInfo = new MovieInfo(null, "Avatar", 2009
            , List.of("Neteyam", "Jake Sully", "Neytiri", "Kiri", "Lo'ak", "Ronal", "Tsireya"),LocalDate.parse("2009-12-17") );

        when(moviesInfoService.updateMovieInfo(isA(MovieInfo.class), isA(String.class))).thenReturn(
            Mono.just(new MovieInfo(movieInfo.getMovieInfoId(), "Avatar", 2009
                , List.of("Neteyam", "Jake Sully", "Neytiri", "Kiri", "Lo'ak", "Ronal", "Tsireya"),LocalDate.parse("2009-12-17") ))
        );
        //when
        //when
        webTestClient.put()
            .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityResult -> {
                var updatedMovieInfo = movieInfoEntityResult.getResponseBody();
                assert updatedMovieInfo != null;
                assert updatedMovieInfo.getName() != null;
                assertEquals("Avatar", updatedMovieInfo.getName());
            });
//            .jsonPath("$.name").isEqualTo("Avatar");
    }

    @Test
    void deleteMovieInfo(){
        //given
        var movieInfoId = "abc";

        when(moviesInfoService.deleteMovieById(isA(String.class)))
            .thenReturn(Mono.empty());
        //when
        webTestClient.delete()
            .uri(MOVIE_INFO_URL+"/{id}",movieInfoId)
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}
