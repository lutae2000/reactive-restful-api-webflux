package com.reactivespring.moviesinfoservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {

        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "The Dark Knight",
                2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
            .blockLast();
    }

    @AfterEach
    void tearDown(){
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {
        //given

        //when
        var moviesInfoFlux = movieInfoRepository.findById("abc").log();

        //then
        StepVerifier.create(moviesInfoFlux)
//            .expectNextCount()
            .assertNext(movieInfo -> {
                assertEquals("Dark Knight Rises", movieInfo.getName());
            })
            .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins1",
            2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        var savedMovieInfo = movieInfoRepository.save(movieInfo);

        //then
        StepVerifier.create(savedMovieInfo)
            .assertNext(movieInfo1 -> {
                assertNotNull(movieInfo1.getMovieInfoId());
            });
    }

    @Test
    void updateMovieInfo() {
        //given
        var movieInfo = movieInfoRepository.findById("abc").block();

        movieInfo.setYear(2021);

        var savedMovieInfo = movieInfoRepository.save(movieInfo);

        //then
        StepVerifier.create(savedMovieInfo)
            .assertNext(movieInfo1 -> {
                assertEquals(2021, movieInfo1.getYear());
            });
    }


    @Test
    void deleteMovieInfo() {
        //given

        //when
        movieInfoRepository.deleteById("abc").block();
        var moviesInfo = movieInfoRepository.findAll();

        //then
        StepVerifier.create(moviesInfo)
            .expectNextCount(2)
            .verifyComplete();
    }
}