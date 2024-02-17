package com.reactivespring.moviesinfoservice.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;


    @Test
    void flux() {
        webTestClient.get()
            .uri("/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Integer.class)
            .hasSize(3);
    }

    @Test
    void flux_approach2() {
        var flux = webTestClient.get()
            .uri("/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(Integer.class)
            .getResponseBody();

        StepVerifier.create(flux)
            .expectNext(1,2,3)
            .verifyComplete();
    }

    @Test
    void flux_approach3() {
        var flux = webTestClient.get()
            .uri("/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Integer.class)
            .consumeWith(listEntityExchangeResult -> {
                var responseBody = listEntityExchangeResult.getResponseBody();
                assert(Objects.requireNonNull(responseBody).size() == 3);
            });
    }

    @Test
    void mono() {
        var mono = webTestClient.get()
            .uri("/mono")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(String.class)
            .consumeWith(listEntityExchangeResult -> {
                var responseBody = listEntityExchangeResult.getResponseBody();
                assertEquals("hello",responseBody);
            });
    }

    @Test
    void stream() {
        var flux = webTestClient.get()
            .uri("/stream")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(Long.class)
            .getResponseBody();

        StepVerifier.create(flux)
            .expectNext(0L,1L,2L,3L)
            .thenCancel()
            .verify();
    }
}