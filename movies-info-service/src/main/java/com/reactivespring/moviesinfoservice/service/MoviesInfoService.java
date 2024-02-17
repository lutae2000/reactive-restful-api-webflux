package com.reactivespring.moviesinfoservice.service;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoService {

    private MovieInfoRepository movieInfoRepository;

    public MoviesInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id){
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updateMovieInfo, String id){
        return movieInfoRepository.findById(id)
            .flatMap(movieInfo -> {
                movieInfo.setCast(updateMovieInfo.getCast());
                movieInfo.setYear(updateMovieInfo.getYear());
                movieInfo.setName(updateMovieInfo.getName());
                movieInfo.setRelease_date(updateMovieInfo.getRelease_date());
                return movieInfoRepository.save(movieInfo);
            });
    }

    public Mono<Void> deleteMovieById(String id){
        return movieInfoRepository.deleteById(id);
    }
}
