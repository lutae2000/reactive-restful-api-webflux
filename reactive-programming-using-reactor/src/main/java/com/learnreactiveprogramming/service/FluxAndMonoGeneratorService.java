package com.learnreactiveprogramming.service;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxAndMonoGeneratorService {

    public Flux<String> nameFlux(){
        return Flux.fromIterable(List.of("alex", "ben", "lee"))
            .log();
    }

    public Flux<String> nameFlux_map(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "lee"))
            .map(String::toUpperCase)
            .filter(s -> s.length()>stringLength)
            .map(s->s.length() + "-" + s)
            .log();
    }

    public Flux<String> nameFlux_immutability(){
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "lee"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Mono<String> nameMono(){
        return Mono.just("alex");
    }

    public Flux<String> nameFlux_flatmap(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "lee"))
            .map(String::toUpperCase)
            .filter(s -> s.length()>stringLength)
//            .map(s->s.length() + "-" + s)
            .flatMap(s -> splitString(s))
            .log();
    }

    public Flux<String> nameFlux_transform(int stringLength){

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength);


        return Flux.fromIterable(List.of("alex", "ben", "lee"))
            .transform(filterMap)
            .flatMap(s -> splitString(s))
            .log();
    }

    public Flux<String> nameFlux_transform_switchifEmpty(int stringLength){

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength)
            .flatMap(s -> splitString(s));

        var defaultFlux = Flux.just("default")
            .transform(filterMap);

        return Flux.fromIterable(List.of("alex", "ben", "lee"))
            .transform(filterMap)
            .switchIfEmpty(defaultFlux)
            .log();
    }

    public Flux<String> nameFlux_flatmap_async(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "lee"))
            .map(String::toUpperCase)
            .filter(s -> s.length()>stringLength)
//            .map(s->s.length() + "-" + s)
            .flatMap(s -> splitString_withDelay(s))
            .log();
    }

    public Flux<String> nameFlux_concatmap(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "lee"))
            .map(String::toUpperCase)
            .filter(s -> s.length()>stringLength)
//            .map(s->s.length() + "-" + s)
            .concatMap(s -> splitString_withDelay(s))
            .log();
    }

    public Flux<String> explore_concat(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");
        return Flux.concat(abcFlux,defFlux).log();
    }

    public Flux<String> explore_concatWith(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");
        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> explore_merge(){
        var abcFlux = Flux.just("A","B","C")
            .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
            .delayElements(Duration.ofMillis(125));;
        return Flux.merge(abcFlux,defFlux).log();
    }

    public Flux<String> explore_mergeWith(){
        var abcFlux = Flux.just("A","B","C")
            .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
            .delayElements(Duration.ofMillis(125));
        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_mergeSequential(){
        var abcFlux = Flux.just("A","B","C")
            .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
            .delayElements(Duration.ofMillis(125));
        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_zip(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");
        return Flux.zip(abcFlux,defFlux, (first,second) -> first+second).log(); //AD,BE,CF
    }

    public Flux<String> explore_zipWith(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");
        var _123Flux = Flux.just("1","2","3");
        var _456Flux = Flux.just("4","5","6");
        return abcFlux.zipWith(defFlux, (first,second) -> first+second).log(); //AD,BE,CF
    }

    public Flux<String> explore_zip1(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");
        var _123Flux = Flux.just("1","2","3");
        var _456Flux = Flux.just("4","5","6");
        return Flux.zip(abcFlux,defFlux,_123Flux,_456Flux)
            .map(t4 -> t4.getT1()+t4.getT2()+t4.getT3()+t4.getT4())
            .log();
    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> splitString_withDelay(String name){
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
            .delayElements(Duration.ofMillis(delay));
    }

    public static void main(String[] ars){

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.nameFlux().subscribe(name -> {
            System.out.println("name is: " + name);
        });

        fluxAndMonoGeneratorService.nameMono().subscribe(
            name -> {
                System.out.println("mono name is: "+ name);
            }
        );
    }
}
