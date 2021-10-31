package com.reactivespring.controller;

import com.reactivespring.MoviesInfoController;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.isA;


@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {
    private static final String MOVIE_INFO_URL = "/v1/movieinfos";

    List<MovieInfo> movieInfos = List.of(new MovieInfo(null, "Batman Begins", 2005,
                    List.of("Christan Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "The Dark Knight", 2008,
                    List.of("Christan Bale", "Heath Ledger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises", 2012,
                    List.of("Christan Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    @Test
    void getAllMovieInfos() {
        when(movieInfoServiceMock.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient.get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        String id = "abc";
        when(movieInfoServiceMock.getAllMovieInfoById(id)).thenReturn(Mono.just(new MovieInfo("abc", "Dark Knight Rises", 2012,
                List.of("Christan Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))));

        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                    assertEquals ("Dark Knight Rises", movieInfo.getName());
                });

        // Another approach to check the name
        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    void addMovieInfo() {
        MovieInfo movieInfo = new MovieInfo(null, "Man of Steel", 2012,
                List.of("Henry Cavill", "Amy Adams"), LocalDate.parse("2012-05-18"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn( Mono.just(new MovieInfo("mockId", "Man of Steel", 2012,
                List.of("Henry Cavill", "Amy Adams"), LocalDate.parse("2012-05-18"))));

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(savedMovieInfo);
                    assertNotNull(savedMovieInfo.getMovieInfoId());
                    assertEquals("mockId", savedMovieInfo.getMovieInfoId());
                });
    }
}
