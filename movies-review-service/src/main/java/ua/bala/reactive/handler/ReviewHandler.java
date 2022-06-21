package ua.bala.reactive.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import ua.bala.reactive.domain.Review;
import ua.bala.reactive.exception.ReviewDataException;
import ua.bala.reactive.exception.ReviewNotFoundException;
import ua.bala.reactive.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;

    private final ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {

        return request.bodyToMono(Review.class)
            .doOnNext(this::validate)
            .flatMap(reviewReactiveRepository::save)
            .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        var constraintValidation = validator.validate(review);
        log.info("constraintValidation : {}", constraintValidation);
        if(constraintValidation.size() > 0) {
            String errorMessage = constraintValidation
                .stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(", "));
            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {

        Optional<String> movieInfoId = request.queryParam("movieInfoId");

        Flux<Review> reviewFlux;

        if (movieInfoId.isPresent()) {
            reviewFlux = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
        } else {
            reviewFlux = reviewReactiveRepository.findAll();
        }

        return ServerResponse.ok().body(reviewFlux, Review.class);
    }

    public Mono<ServerResponse> getReview(ServerRequest request) {

        Optional<String> reviewId = request.queryParam("reviewId");

        Mono<Review> reviewFlux = reviewId.map(reviewReactiveRepository::findById).orElseGet(Mono::empty);

        return ServerResponse.ok().body(reviewFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {

        String reviewId = request.pathVariable("id");

        Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId);
//            .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given Review id " + reviewId)));

        return existingReview
            .flatMap(review -> request.bodyToMono(Review.class)
                .map(reqReview -> {
                    review.setComment(reqReview.getComment());
                    review.setRating(reqReview.getRating());
                    return review;
                })
                .flatMap(reviewReactiveRepository::save)
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview)))
            .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {

        String reviewId = request.pathVariable("id");

        Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId);

        return existingReview
            .flatMap(review -> reviewReactiveRepository.deleteById(reviewId)
                .then(ServerResponse.noContent().build()));
    }
}
