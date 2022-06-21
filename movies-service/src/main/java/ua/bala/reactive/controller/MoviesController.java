package ua.bala.reactive.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.bala.reactive.client.MoviesInfoRestClient;
import ua.bala.reactive.client.ReviewsRestClient;
import ua.bala.reactive.domain.Movie;
import ua.bala.reactive.domain.Review;

import java.util.List;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MoviesInfoRestClient moviesInfoRestClient;
    private ReviewsRestClient reviewsRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient, ReviewsRestClient reviewsRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {

        return moviesInfoRestClient.retrieveMovieInfo(movieId)
            .flatMap(movieInfo -> {
                Mono<List<Review>> reviewsList = reviewsRestClient.retrieveReviews(movieId).collectList();

                return reviewsList.map(reviews -> new Movie(movieInfo, reviews));
            });
    }
}
