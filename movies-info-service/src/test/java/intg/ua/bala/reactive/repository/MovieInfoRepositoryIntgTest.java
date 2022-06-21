package ua.bala.reactive.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.reactive.domain.MovieInfo;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
public class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

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
    void findAll() {

        Flux<MovieInfo> movieInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(movieInfoFlux)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void findById() {

        Mono<MovieInfo> movieInfoMono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(movieInfoMono)
//            .expectNextCount(1)
            .assertNext(movieInfo ->
                assertEquals("Dark Knight Rises", movieInfo.getName())
            )
            .verifyComplete();
    }

    @Test
    void findByYear() {

        Flux<MovieInfo> movieInfoMono = movieInfoRepository.findByYear(2005).log();

        StepVerifier.create(movieInfoMono)
//            .expectNextCount(1)
            .assertNext(movieInfo -> {
                assertEquals("Batman Begins", movieInfo.getName());
                assertEquals(2005, movieInfo.getYear());
            })
            .verifyComplete();
    }

    @Test
    void findByName() {

        Flux<MovieInfo> movieInfoMono = movieInfoRepository.findByName("Batman Begins").log();

        StepVerifier.create(movieInfoMono)
//            .expectNextCount(1)
            .assertNext(movieInfo -> {
                assertEquals("Batman Begins", movieInfo.getName());
            })
            .verifyComplete();
    }

    @Test
    void saveMovieInfo() {

        MovieInfo movieInfo = new MovieInfo(null, "Batman Begins1", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-05-15"));

        Mono<MovieInfo> movieInfoMono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoMono)
//            .expectNextCount(1)
            .assertNext(movieInfo1 -> {
                assertNotNull(movieInfo1.getMovieInfoId());
                assertEquals("Batman Begins1", movieInfo1.getName());
            })
            .verifyComplete();
    }

    @Test
    void updateMovieInfo() {

        MovieInfo movieInfo = movieInfoRepository.findById("abc").block();
        movieInfo.setYear(2022);

        Mono<MovieInfo> movieInfoMono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoMono)
            .assertNext(movieInfo1 -> {
                assertEquals(2022, movieInfo1.getYear());
            })
            .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {

        movieInfoRepository.deleteById("abc").block();
        Flux<MovieInfo> movieInfoMono = movieInfoRepository.findAll().log();

        StepVerifier.create(movieInfoMono)
            .expectNextCount(2)
            .verifyComplete();
    }

}