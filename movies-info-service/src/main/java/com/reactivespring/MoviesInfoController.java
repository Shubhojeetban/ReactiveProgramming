package com.reactivespring;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private MovieInfoService movieInfoService;

    public MoviesInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @GetMapping("/movieinfos")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMovieInfos() {
        return this.movieInfoService.getAllMovieInfo();
    }

    @GetMapping("/movieinfos/{id}")
    public Mono<MovieInfo> getAllMovieInfoById(@PathVariable("id") String id) {
        return this.movieInfoService.getAllMovieInfoById(id);
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
        return this.movieInfoService.addMovieInfo(movieInfo);
    }

    @PutMapping("/movieinfos/{id}")
    public Mono<MovieInfo> updateMovieInfo(@RequestBody MovieInfo updatedMovieInfo, @PathVariable("id") String id) {
        return this.movieInfoService.updateMovieInfo(updatedMovieInfo, id);
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable("id") String id) {
        return this.movieInfoService.deleteMovieInfo(id);
    }
}
