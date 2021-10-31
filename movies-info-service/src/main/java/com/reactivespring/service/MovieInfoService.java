package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    MovieInfoRepository movieInfoRepository;

    public MovieInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfo() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getAllMovieInfoById(String Id) {
        Mono<MovieInfo> movieInfoMono =  movieInfoRepository.findById(Id);
        return movieInfoMono;
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id) {
        return movieInfoRepository.findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.setYear(updatedMovieInfo.getYear());
                    movieInfo.setCast(updatedMovieInfo.getCast());
                    movieInfo.setName(updatedMovieInfo.getName());
                    movieInfo.setRelease_date(updatedMovieInfo.getRelease_date());
                    return movieInfoRepository.save(movieInfo);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }
}
