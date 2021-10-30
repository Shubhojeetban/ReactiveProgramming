package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe")); // in real data comes from db or service
    }

    public Mono<String> nameMono() {
        return Mono.just("alex");
    }

    public Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase); // in real data comes from db or service
    }

    public Mono<List<String>> nameMono_flatmap() {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMap(this::spitStringMono)
                .log();
    }

    public Flux<String> nameMono_flatMapMany() {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMapMany(this::splitStringFlux)  // flatMapMany requires function who returns flux
                .log();
    }

    private Mono<List<String>> spitStringMono(String s) {
        String[] charArray = s.split("");
        List<String> charList = List.of(charArray);
        return Mono.just(charList);
    }

    public Flux<String> namesFlux_filter(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .filter(name -> name.length() > stringLength)
                .map(s->s.length()+"-"+s) // 4-alex 5-chloe
                .log();
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(s -> splitStringFlux(s))
                .log();
    }

    public Flux<String> namesFlux_transform(int stringLength) {
        /*
         transform is used to extract the functionality and assign it into a variable.
         this can be used when the program have the same functionality used multiple times.
         Function<T, R>
         <T> – the type of the input to the function
         <R> – the type of the result of the function
         */
        Function<Flux<String>, Flux<String>> filterMap = n -> n.map(String::toUpperCase)
                .filter(name -> name.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(s -> splitStringFlux(s))
                .log();
    }

    public Flux<String> namesFlux_defaultIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = n ->
                n.map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMap(s -> splitStringFlux(s));

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .defaultIfEmpty("default") // takes the parameter T eg. String in this case
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .log();
    }

    public Flux<String> namesFlux_switchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = n ->
                n.map(String::toUpperCase)
                        .filter(name -> name.length() > stringLength)
                        .flatMap(s -> splitStringFlux(s));

        Flux<String> defaultFlux = Flux.just("default")
                                        .transform(filterMap);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux) // takes parameter as Publisher eg. like Flux in this case
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .log();
    }

    // the order will be jumbled because of the delay.
    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(s -> splitString_withDelay(s))
                .log();
    }

    // used same as flatMap but preserves the ordering
    public Flux<String> namesFlux_concatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .concatMap(s -> splitString_withDelay(s))
                .log();
    }

    public Flux<String> namesFlux_concat() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        // concat method is a static method
        return Flux.concat(abcFlux, defFlux);
    }

    public Flux<String> namesFlux_concatWith() {
        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        // concatWith method is a instance method
        return abcFlux.concatWith(defFlux);
    }

    public Flux<String> splitString_withDelay(String name) {
        String[] charArray = name.split("");
        int delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    // ALEX -> A, L, E, X
    public Flux<String> splitStringFlux(String name) {
        String[] charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux_map()
                .subscribe(name -> {
                    System.out.println("Name is : "+name);
                });

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> {
                    System.out.println("Mono name is : "+name);
                });
    }
}
