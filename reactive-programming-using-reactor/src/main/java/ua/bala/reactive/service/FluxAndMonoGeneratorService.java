package ua.bala.reactive.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> nameFlux () {
        return Flux.fromIterable(List.of("Alex", "Bob", "Ihor")).log();
    }

    public Flux<String> nameFlux_map () {
        return Flux.fromIterable(List.of("Alex", "Bob", "Ihor"))
            .map(String::toUpperCase)
            .log();
    }

    public Flux<String> nameFlux_immutability () {
        Flux<String> names = Flux.fromIterable(List.of("Alex", "Bob", "Ihor"))
            .log();
        names.map(String::toUpperCase);
        return names;
    }

    public Flux<String> nameFlux_filter () {
        return Flux.fromIterable(List.of("Alex", "Bob", "Ihor"))
            .filter(name -> name.length() > 3)
            .log();
    }

    public Flux<String> nameFlux_flatMap () {
        return Flux.fromIterable(List.of("Alex", "Bob", "Ihor"))
            .flatMap(this::split)
            .log();
    }

    public Flux<String> nameFlux_flatMap_async () {
        return Flux.fromIterable(List.of("Alex", "Bob", "Ihor"))
            .flatMap(this::splitWithDelay)
            .log();
    }

    private Flux<String> split(String name) {
        String[] array = name.split("");
        return Flux.fromArray(array);
    }

    private Flux<String> splitWithDelay(String name) {
        String[] array = name.split("");
        int delay = new Random().nextInt(1000);
        return Flux.fromArray(array)
            .delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> nameFlux_concatMap () {
        return Flux.fromIterable(List.of("Alex", "Bob", "Ihor"))
            .concatMap(this::split)
            .log();
    }

    public Flux<String> nameFlux_transform (int size) {
        Function<Flux<String>, Flux<String>> filterAndMap = name ->
            name.filter(s -> s.length() > size)
                .map(String::toUpperCase)
            ;

        Flux<String> defaultFlux = Flux.just("default")
            .transform(filterAndMap);

        return Flux.fromIterable(List.of("Alex", "Bob", "Ihor"))
            .transform(filterAndMap)
            .flatMap(this::split)
//            .defaultIfEmpty("default")
            .switchIfEmpty(defaultFlux)
            .log();
    }

    public Flux<String> nameFlux_concat () {
        Flux<String> flux1 = Flux.just("A", "L");
        Flux<String> flux2 = Flux.just("E", "X");

        return Flux.concat(flux1, flux2);
    }

    public Flux<String> nameFlux_concatWith () {
        Flux<String> flux1 = Flux.just("A", "L");
        Flux<String> flux2 = Flux.just("E", "X");

        return flux1.concatWith(flux2);
    }

    public Flux<String> nameFlux_concatWithMono () {
        Mono<String> mono1 = Mono.just("A");
        Mono<String> mono2 = Mono.just("B");

        return mono1.concatWith(mono2);
    }

    public Flux<String> nameFlux_merge () {
        Flux<String> abc = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> def = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return Flux.merge(abc, def);
    }

    public Flux<String> nameFlux_mergeWith () {
        Flux<String> abc = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> def = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return abc.mergeWith(def);
    }

    public Flux<String> nameFlux_mergeWith_mono () {
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");

        return aMono.mergeWith(bMono);
    }

    public Flux<String> nameFlux_mergeSequential () {
        Flux<String> abc = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> def = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abc, def);
    }

    public Flux<String> nameFlux_zip () {
        Flux<String> abc = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(100));
        Flux<String> def = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(125));

        return Flux.zip(abc, def)
            .map(streams -> streams.getT1() + streams.getT2());
//        return Flux.zip(abc, def, (f, s) -> f + s);
    }

    public Mono<String> nameMono () {

        return Mono.just("Alex").log();
    }

    public Mono<String> nameMono_map_filter () {

        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > 3)
            .log();
    }

    private Mono<List<String>> splitMono(String name) {
        String[] array = name.split("");
        return Mono.just(List.of(array))
            .delayElement(Duration.ofSeconds(1));
    }

    public Mono<List<String>> nameMono_flatMap (int size) {

        return Mono.just("Alex")
            .map(String::toUpperCase)
            .filter(name -> name.length() > size)
            .flatMap(this::splitMono)
            .log();
    }

    public Flux<String> nameMono_flatMapMany () {

        return Mono.just("Alex")
            .flatMapMany(this::split)
            .log();
    }

//    public static void main(String[] args) {
//        FluxAndMonoGeneratorService generatorService = new FluxAndMonoGeneratorService();
//
//        generatorService.nameFlux()
//            .subscribe();
//
//        generatorService.nameMono()
//            .subscribe();
//    }
}
