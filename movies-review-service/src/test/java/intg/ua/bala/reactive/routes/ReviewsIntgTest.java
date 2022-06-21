package ua.bala.reactive.routes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.bala.reactive.MoviesReviewServiceApplication;
import ua.bala.reactive.domain.Review;
import ua.bala.reactive.repository.ReviewReactiveRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    static String REVIEWS_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {
        List<Review> reviewList = List.of(
            new Review(null, 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie 2", 9.0),
            new Review(null, 2L, "Excellent Movie", 8.0)
        );

        reviewReactiveRepository.saveAll(reviewList)
            .blockLast();
    }

    @AfterEach
    void tearDown(){
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {

        Review review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient
            .post()
            .uri(REVIEWS_URL)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Review.class)
            .consumeWith(reviewEntityExchangeResult -> {
                Review savedReview = reviewEntityExchangeResult.getResponseBody();
                assertNotNull(savedReview);
                assertNotNull(savedReview.getReviewId());
            });
    }

    @Test
    public void getReviews() {
        webTestClient.get()
            .uri(REVIEWS_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .hasSize(3);
    }

    @Test
    void updateReview() {
        Review review = new Review(null, 1L, "Awesome Movie", 9.0);
        Review savedReview = reviewReactiveRepository.save(review).block();
        Review reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        webTestClient
            .put()
            .uri(REVIEWS_URL+"/{id}", savedReview.getReviewId())
            .bodyValue(reviewUpdate)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Review.class)
            .consumeWith(reviewResponse -> {
                Review updatedReview = reviewResponse.getResponseBody();
                assertNotNull(savedReview);
                assertNotNull(savedReview.getReviewId());
                assertEquals(8.0, updatedReview.getRating());
                assertEquals("Not an Awesome Movie", updatedReview.getComment());
            });
    }

    @Test
    void deleteReview() {
        Review review = new Review(null, 1L, "Awesome Movie", 9.0);
        Review savedReview = reviewReactiveRepository.save(review).block();
        webTestClient
            .delete()
            .uri(REVIEWS_URL+"/{id}", savedReview.getReviewId())
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @Test
    void getReviewsByMovieInfoId() {
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(REVIEWS_URL)
                .queryParam("movieInfoId", "1")
                .build())
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .hasSize(2);
    }
}