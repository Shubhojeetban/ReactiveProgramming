package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // So that this test use different port than the main one
@ActiveProfiles("test") // So that this uses different port other than main mongo one
@AutoConfigureWebTestClient
class MoviesInfoControllerIntgTest {
    private static final String MOVIE_INFO_URL = "/v1/movieinfos";

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        List<MovieInfo> movieInfos = List.of(new MovieInfo(null, "Batman Begins", 2005,
                        List.of("Christan Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008,
                        List.of("Christan Bale", "Heath Ledger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012,
                        List.of("Christan Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        // don't use blockLast() in controller in actual only use it in test case
        movieInfoRepository.saveAll(movieInfos).blockLast(); // blockLast because all the method are asynchronous we don't want to start findAll before saving all the date
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addMovieInfo() {
        MovieInfo movieInfo = new MovieInfo(null, "Man of Steel", 2012,
                List.of("Henry Cavill", "Amy Adams"), LocalDate.parse("2012-05-18"));
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
                });
    }

    @Test
    void getAllMovieInfos() {
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
    void updateMovieInfo() {
        MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises Part 2", 2012,
                List.of("Christan Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        String id  = "abc";
        webTestClient
                .put()
                .uri(MOVIE_INFO_URL+"/{id}", id)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(updatedMovieInfo);
                    assertNotNull(updatedMovieInfo.getMovieInfoId());
                    assertEquals( updatedMovieInfo.getMovieInfoId(), movieInfo.getMovieInfoId());  // The updated MovieInfoId should not change
                    assertEquals("Dark Knight Rises Part 2", updatedMovieInfo.getName());
                });
    }

    @Test
    void deleteMovieInfo() {
        String id = "abc";

        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL+"/{id}", id)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty(); // To check the body is Empty or not
    }
}