package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier.create(namesFlux)
                // .expectNext("alex", "ben", "chloe")
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_map() {
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_map();
        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_filter() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_filter(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("4-alex", "5-chloe")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_async() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);
        StepVerifier.create(namesFlux)
                // .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void nameMono_flatmap() {
        Mono<List<String>> nameMono = fluxAndMonoGeneratorService.nameMono_flatmap();
        StepVerifier.create(nameMono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void nameMono_flatMapMany() {
        Flux<String> namesFlux = fluxAndMonoGeneratorService.nameMono_flatMapMany();
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_defaultIfEmpty() {
        int stringLength = 6;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_defaultIfEmpty(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_switchIfEmpty() {
        int stringLength = 6;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_switchIfEmpty(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void namesFlux_concat() {
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_concat();
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void namesFlux_concatWith() {
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_concatWith();
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }
}