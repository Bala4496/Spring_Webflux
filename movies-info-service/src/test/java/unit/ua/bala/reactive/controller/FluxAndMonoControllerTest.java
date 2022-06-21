package ua.bala.reactive.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux() {
        webTestClient
            .get()
            .uri("/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Integer.class)
            .hasSize(3);
    }

    @Test
    public void flux_2() {
        Flux<Integer> flux = webTestClient
            .get()
            .uri("/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(Integer.class)
            .getResponseBody();

        StepVerifier.create(flux)
            .expectNext(1, 2, 3)
            .verifyComplete();
    }

    @Test
    public void flux_3() {
        webTestClient
            .get()
            .uri("/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Integer.class)
            .consumeWith(list -> {
                List<Integer> responseBody = list.getResponseBody();
                assertEquals(3, responseBody.size());
            });
    }

    @Test
    public void mono() {
        webTestClient
            .get()
            .uri("/mono")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(String.class)
            .consumeWith(result -> {
                String responseBody = result.getResponseBody();
                assertEquals("Hello, World!", responseBody);
            });
    }

    @Test
    public void stream() {
        Flux<Long> flux = webTestClient
            .get()
            .uri("/stream")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(Long.class)
            .getResponseBody();

        StepVerifier.create(flux)
            .expectNext(0L, 1L, 2L, 3L)
            .thenCancel()
            .verify();
    }}