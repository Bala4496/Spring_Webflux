package ua.bala.reactive.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.reactive.domain.MovieInfo;
import ua.bala.reactive.service.MovieInfoService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    @Autowired
    WebTestClient webTestClient;
    static String MOVIES_INFO_URL = "/v1/movieinfos";

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    @Test
    public void getAllMovieInfo() {

        List<MovieInfo> movieInfoList = List.of(
            new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-05-15")),
            new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        when(movieInfoServiceMock.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieInfoList));

        webTestClient.get()
            .uri(MOVIES_INFO_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(3);
    }

    @Test
    public void getMovieInfoById() {

        String movieInfoId = "abc";

        MovieInfo movieInfo = new MovieInfo("abc", "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-05-15"));

        when(movieInfoServiceMock.getMovieInfoById(isA(String.class)))
            .thenReturn(Mono.just(movieInfo));

        webTestClient.get()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityExchangeResult -> {
                MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                assertNotNull(savedMovieInfo.getMovieInfoId());
            });
    }

    @Test
    public void addMovieInfoBy() {

        MovieInfo movieInfo = new MovieInfo(null, "Dark Knight Rises", 2022, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
            .thenReturn(Mono.just(new MovieInfo("abc", "Dark Knight Rises", 2022, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))));

        webTestClient.post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityExchangeResult -> {
                MovieInfo movieInfoEntity = movieInfoEntityExchangeResult.getResponseBody();
                assertNotNull(movieInfoEntity);
                assertNotNull(movieInfoEntity.getMovieInfoId());
            });
    }

    @Test
    public void addMovieInfoBy_validation() {

        String movieInfoId = "abc";

        MovieInfo movieInfo = new MovieInfo(movieInfoId, "", -2022, List.of("", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        webTestClient.post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .consumeWith(stringEntityExchangeResult -> {
                String responseBody = stringEntityExchangeResult.getResponseBody();
                System.out.println(responseBody);
                assertNotNull(responseBody);
            });
    }

    @Test
    public void updateMovieInfo() {
        String movieInfoId = "abc";

        MovieInfo movieInfo = new MovieInfo(movieInfoId, "Dark Knight Rises1", 2022, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        when(movieInfoServiceMock.updateMovieInfo(isA(MovieInfo.class), isA(String.class)))
            .thenReturn(Mono.just(movieInfo));

        webTestClient.put()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Dark Knight Rises1");
    }

    @Test
    public void deleteMovieInfo() {

        String movieInfoId = "abc";

        when(movieInfoServiceMock.deleteMovieInfo(isA(String.class)))
            .thenReturn(Mono.empty());

        webTestClient.delete()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .isNoContent()
            .expectBody(Void.class);
    }
}