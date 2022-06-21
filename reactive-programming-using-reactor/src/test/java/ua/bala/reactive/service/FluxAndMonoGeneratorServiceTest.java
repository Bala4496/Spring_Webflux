package ua.bala.reactive.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService generatorService = new FluxAndMonoGeneratorService();

    @Test
    void nameFlux() {
        Flux<String> value = generatorService.nameFlux();
        StepVerifier.create(value)
            .expectNext("Alex", "Bob", "Ihor")
            .verifyComplete();
    }

    @Test
    void nameMono() {
        Mono<String> mono = generatorService.nameMono();
        StepVerifier.create(mono)
            .expectNext("Alex")
            .verifyComplete();
    }

    @Test
    void nameFlux_map() {
        Flux<String> value = generatorService.nameFlux_map();
        StepVerifier.create(value)
            .expectNext("ALEX", "BOB", "IHOR")
            .verifyComplete();
    }

    @Test
    void nameFlux_immutability() {
        Flux<String> value = generatorService.nameFlux_immutability();
        StepVerifier.create(value)
            .expectNext("Alex", "Bob", "Ihor")
            .verifyComplete();
    }

    @Test
    void nameFlux_filter() {
        Flux<String> value = generatorService.nameFlux_filter();
        StepVerifier.create(value)
            .expectNext("Alex", "Ihor")
            .verifyComplete();
    }

    @Test
    void nameFlux_flatMap() {
        Flux<String> value = generatorService.nameFlux_flatMap();
        StepVerifier.create(value)
            .expectNext("A", "l", "e", "x")
            .expectNextCount(7)
            .verifyComplete();
    }

    @Test
    void nameFlux_flatMap_async() {
        Flux<String> value = generatorService.nameFlux_flatMap_async();
        StepVerifier.create(value)
            .expectNextCount(11)
            .verifyComplete();
    }

    @Test
    void nameFlux_concatMap() {
        Flux<String> value = generatorService.nameFlux_concatMap();
        StepVerifier.create(value)
            .expectNext("A", "l", "e", "x")
            .expectNextCount(7)
            .verifyComplete();
    }

    @Test
    void nameMono_map_filter() {
        Mono<String> value = generatorService.nameMono_map_filter();
        StepVerifier.create(value)
            .expectNext("ALEX")
            .verifyComplete();
    }

    @Test
    void nameMono_flatMap() {
        Mono<List<String>> value = generatorService.nameMono_flatMap(3);
        StepVerifier.create(value)
            .expectNext(List.of("A", "L", "E", "X"))
            .verifyComplete();
    }

    @Test
    void nameMono_flatMapMany() {
        Flux<String> value = generatorService.nameMono_flatMapMany();
        StepVerifier.create(value)
            .expectNext("A", "l", "e", "x")
            .verifyComplete();
    }

    @Test
    void nameFlux_transform() {
        Flux<String> value = generatorService.nameFlux_transform(3);
        StepVerifier.create(value)
            .expectNext("A", "L", "E", "X")
            .expectNextCount(4)
            .verifyComplete();
    }

    @Test
    void nameFlux_transform_1() {
        Flux<String> value = generatorService.nameFlux_transform(6);
        StepVerifier.create(value)
            .expectNext("DEFAULT")
            .verifyComplete();
    }

    @Test
    void nameFlux_concat() {
        Flux<String> value = generatorService.nameFlux_concat();
        StepVerifier.create(value)
            .expectNext("A", "L", "E", "X")
            .verifyComplete();
    }

    @Test
    void nameFlux_concatWith() {
        Flux<String> value = generatorService.nameFlux_concatWith();
        StepVerifier.create(value)
            .expectNext("A", "L", "E", "X")
            .verifyComplete();
    }

    @Test
    void nameFlux_concatWithMono() {
        Flux<String> value = generatorService.nameFlux_concatWithMono();
        StepVerifier.create(value)
            .expectNext("A", "B")
            .verifyComplete();
    }

    @Test
    void nameFlux_merge() {
        Flux<String> value = generatorService.nameFlux_merge();
        StepVerifier.create(value)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete();
    }

    @Test
    void nameFlux_mergeWith() {
        Flux<String> value = generatorService.nameFlux_mergeWith();
        StepVerifier.create(value)
            .expectNext("A", "D", "B", "E", "C", "F")
            .verifyComplete();
    }

    @Test
    void nameFlux_mergeWith_mono() {
        Flux<String> value = generatorService.nameFlux_mergeWith_mono();
        StepVerifier.create(value)
            .expectNext("A", "B")
            .verifyComplete();
    }

    @Test
    void nameFlux_mergeSequential() {
        Flux<String> value = generatorService.nameFlux_mergeSequential();
        StepVerifier.create(value)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void nameFlux_zip() {
        Flux<String> value = generatorService.nameFlux_zip();
        StepVerifier.create(value)
            .expectNext("AD", "BE", "CF")
            .verifyComplete();
    }
}