package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
@Slf4j
public class MoviesController {

    private MoviesInfoRestClient moviesInfoRestClient;

    private ReviewRestClient reviewRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient,
        ReviewRestClient reviewRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewRestClient = reviewRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId){

        return moviesInfoRestClient.retrieveMovieInfo(movieId)
            //moviesInfoRestClient.retrieveMovieInfo_exchange(movieId)
            .flatMap(movieInfo -> {
                var reviewList = reviewRestClient.retrieveReviews(movieId)
                    .collectList();
                return reviewList.map(reviews -> new Movie(movieInfo, reviews));
            });

    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> retrieveMovieInfos(){

        return moviesInfoRestClient.retrieveMovieInfoStream();

    }
}
