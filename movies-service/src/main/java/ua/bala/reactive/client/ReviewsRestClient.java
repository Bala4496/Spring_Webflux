package ua.bala.reactive.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.reactive.domain.Review;
import ua.bala.reactive.exception.MoviesInfoClientException;
import ua.bala.reactive.exception.MoviesInfoServerException;
import ua.bala.reactive.exception.ReviewsClientException;
import ua.bala.reactive.exception.ReviewsServerException;

@Component
@Slf4j
public class ReviewsRestClient {

    public WebClient webClient;

    @Value("${restClient.reviewsUrl}")
    private String reviewsUrl;

    public ReviewsRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveReviews(String movieId) {

        String uriString = UriComponentsBuilder.fromHttpUrl(reviewsUrl)
            .queryParam("movieInfoId", movieId)
            .buildAndExpand().toUriString();

        return webClient.get()
            .uri(uriString)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                log.info("Status code is : {}", clientResponse.statusCode().value());
                if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    return Mono.empty();
                }
                return clientResponse.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(new ReviewsClientException(responseMessage)));
            })
            .onStatus(HttpStatus::is5xxServerError, clientResponse1 -> {
                log.info("Status code is : {}", clientResponse1.statusCode().value());
                return clientResponse1.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(new ReviewsServerException("Server Error in ReviewsServer" + responseMessage)));
            })
            .bodyToFlux(Review.class);

    }
}
