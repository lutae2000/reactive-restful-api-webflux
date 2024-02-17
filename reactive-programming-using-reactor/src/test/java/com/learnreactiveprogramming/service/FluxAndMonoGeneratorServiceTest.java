package com.learnreactiveprogramming.service;


import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void nameFlux(){
        //given

        //when
        var nameFlux = fluxAndMonoGeneratorService.nameFlux();

        //then
        StepVerifier.create(nameFlux)
//            .expectNextCount(3)
//            .expectNext("alex", "ben", "lee")
            .expectNext("alex")
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void nameFlux_map() {
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.nameFlux_map(stringLength);

        //then
        StepVerifier.create(namesFlux)
//            .expectNext("ALEX", "BEN", "LEE")
            .expectNext("4-ALEX")
            .verifyComplete();
    }

    @Test
    void nameFlux_immutability() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.nameFlux_immutability();

        //then
        StepVerifier.create(namesFlux)
            .expectNext("alex", "ben", "lee")
            .verifyComplete();
    }

    @Test
    void nameFlux_flatmap() {
        //given
        int stringLength = 2;
        //when
        var namesFlux = fluxAndMonoGeneratorService.nameFlux_flatmap(stringLength);

        //then
        StepVerifier.create(namesFlux)
            .expectNext("A","L","E","X","B","E","N","L","E","E")
            .verifyComplete();
    }

    @Test
    void nameFlux_flatmap_async() {
        //given
        int stringLength = 2;
        //when
        var namesFlux = fluxAndMonoGeneratorService.nameFlux_flatmap_async(stringLength);
        //then
        StepVerifier.create(namesFlux)
//            .expectNext("A","L","E","X","B","E","N","L","E","E")
            .expectNextCount(10)
            .verifyComplete();
    }

    @Test
    void nameFlux_concatmap() {
        //given
        int stringLength = 2;
        //when
        var namesFlux = fluxAndMonoGeneratorService.nameFlux_concatmap(stringLength);
        //then
        StepVerifier.create(namesFlux)
            .expectNext("A","L","E","X","B","E","N","L","E","E")
//            .expectNextCount(10)
            .verifyComplete();
    }

    @Test
    void nameFlux_transform() {
        //given
        int stringLength = 2;

        //when
        var namesFlux = fluxAndMonoGeneratorService.nameFlux_transform(stringLength);

        //then
        StepVerifier.create(namesFlux)
            .expectNext("A","L","E","X","B","E","N","L","E","E")
            .verifyComplete();
    }

    @Test
    void nameFlux_transform_switchifEmpty() {
        //given
        int stringLength = 6;

        //when
        var namesFlux = fluxAndMonoGeneratorService.nameFlux_transform_switchifEmpty(stringLength);

        //then
        StepVerifier.create(namesFlux)
            .expectNext("D","E","F","A","U","L","T")
            .verifyComplete();
    }


    @Test
    void explore_concat() {
        //given


        //when
        var concat = fluxAndMonoGeneratorService.explore_concat();

        //then
        StepVerifier.create(concat)
            .expectNext("A","B","C", "D","E","F")
            .verifyComplete();
    }

    @Test
    void explore_merge() {
        //given


        //when
        var value = fluxAndMonoGeneratorService.explore_merge();

        //then
        StepVerifier.create(value)
            .expectNext("A","D","B", "E","C","F")
            .verifyComplete();
    }

    @Test
    void explore_mergeSequential() {
        //given


        //when
        var value = fluxAndMonoGeneratorService.explore_mergeSequential();

        //then
        StepVerifier.create(value)
            .expectNext("A","D","B", "E","C","F")
            .verifyComplete();
    }

    @Test
    void explore_zip() {
        //given


        //when
        var value = fluxAndMonoGeneratorService.explore_zip();

        //then
        StepVerifier.create(value)
            .expectNext("AD","BE","CF")
            .verifyComplete();
    }

    @Test
    void explore_zipWith() {
        //given


        //when
        var value = fluxAndMonoGeneratorService.explore_zip();

        //then
        StepVerifier.create(value)
            .expectNext("AD","BE","CF")
            .verifyComplete();
    }

    @Test
    void explore_zip1() {
        //given


        //when
        var value = fluxAndMonoGeneratorService.explore_zip1();

        //then
        StepVerifier.create(value)
            .expectNext("AD14","BE25","CF36")
            .verifyComplete();
    }
}