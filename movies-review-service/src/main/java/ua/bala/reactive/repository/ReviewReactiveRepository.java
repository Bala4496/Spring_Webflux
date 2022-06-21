package ua.bala.reactive.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.reactive.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findReviewsByMovieInfoId(Long id);
}
