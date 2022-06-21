package ua.bala.reactive.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import ua.bala.reactive.domain.MovieInfo;
import ua.bala.reactive.repository.MovieInfoRepository;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class MovieInfoControllerIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIES_INFO_URL = "/v1/movieinfos";

    @BeforeEach
    void setUp() {
        List<MovieInfo> movieInfoList = List.of(
            new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-05-15")),
            new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        movieInfoRepository.saveAll(movieInfoList)
            .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll()
            .block();
    }

    @Test
    public void getAllMovieInfo() {
        webTestClient.get()
            .uri(MOVIES_INFO_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(3);
    }

    @Test
    public void addMovieInfo() {

        MovieInfo movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-05-15"));

        webTestClient.post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityExchangeResult -> {
                MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();

                assertNotNull(savedMovieInfo.getMovieInfoId());
            });
    }

    @Test
    public void getMovieInfoById() {

        MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        String movieInfoId = "abc";

        webTestClient.get()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Dark Knight Rises");
//            .expectBody(MovieInfo.class)
//            .consumeWith(movieInfoEntityExchangeResult -> {
//                MovieInfo foundMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
//                assertNotNull(foundMovieInfo);
//                assertEquals(foundMovieInfo, movieInfo);
//            });
    }

    @Test
    public void getMovieInfoByYear() {

        URI uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
            .queryParam("year", 2005)
            .buildAndExpand().toUri();

        webTestClient.get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(1);
    }

    @Test
    public void getMovieInfoById_notFound() {

        MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        String movieInfoId = "def";

        webTestClient.get()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    public void updateMovieInfo() {

        String movieInfoId = "abc";
        MovieInfo movieInfo = new MovieInfo(null, "Dark Knight Rises", 2022, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));


        webTestClient.put()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityExchangeResult -> {
                MovieInfo updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                assertEquals("Dark Knight Rises", updatedMovieInfo.getName());
            });
    }

    @Test
    public void updateMovieInfo_notFound() {

        String movieInfoId = "def";
        MovieInfo movieInfo = new MovieInfo(null, "Dark Knight Rises", 2022, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        webTestClient.put()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    public void deleteMovieInfo() {

        String movieInfoId = "abc";

        webTestClient.delete()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .isNoContent()
            .expectBody(Void.class);
    }
}