package ua.bala.reactive.router;

import reactor.core.publisher.Flux;
import ua.bala.reactive.domain.Review;
import ua.bala.reactive.globalerrorhandler.GlobalErrorHandler;
import ua.bala.reactive.handler.ReviewHandler;
import ua.bala.reactive.repository.ReviewReactiveRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
class ReviewsUnitTest {

    @MockBean
    ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    WebTestClient webTestClient;

    static String REVIEWS_URL = "/v1/reviews";

    @Test
    void addReview() {

        Review review = new Review(null, 1L, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient.post()
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
    void addReview_validation() {

        Review review = new Review(null, null, "Awesome Movie", -9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient.post()
            .uri(REVIEWS_URL)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .isEqualTo("Review.movieInfoId : must not be a null, Review.rating : rating is negative and please pass a non-negative value");
    }

    @Test
    void getAllReviews() {
        List<Review> reviewList = List.of(
            new Review("abc", 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie1", 9.0),
            new Review(null, 2L, "Excellent Movie", 8.0));

        when(reviewReactiveRepository.findAll()).thenReturn(Flux.fromIterable(reviewList));

        webTestClient
            .get()
            .uri("/v1/reviews")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .hasSize(3);
    }

    @Test
    void updateReview() {
        Review reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);

        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(new Review("abc", 1L, "Not an Awesome Movie", 8.0)));
        when(reviewReactiveRepository.findById((String) any())).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
            .put()
            .uri("/v1/reviews/{id}", "abc")
            .bodyValue(reviewUpdate)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Review.class)
            .consumeWith(reviewResponse -> {
                Review updatedReview = reviewResponse.getResponseBody();
                assertNotNull(updatedReview);
                assertEquals(8.0,updatedReview.getRating());
                assertEquals("Not an Awesome Movie", updatedReview.getComment());
            });
    }

    @Test
    void deleteReview() {
        var reviewId= "abc";
        when(reviewReactiveRepository.findById(anyString())).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        when(reviewReactiveRepository.deleteById((String) any())).thenReturn(Mono.empty());

        webTestClient
            .delete()
            .uri("/v1/reviews/{id}", reviewId)
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @Test
    void getReviewsByMovieInfoId() {

        when(reviewReactiveRepository.findById(anyString())).thenReturn(Mono.just(new Review(null, 1L, "Awesome Movie", 9.0)));

        webTestClient
            .get()
            .uri("/v1/reviews/{id}", "abc")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Review.class)
            .consumeWith(reviewEntityExchangeResult -> {
                Review review = reviewEntityExchangeResult.getResponseBody();
                assertNull(review);
            });
    }
}