package ua.bala.reactive.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ua.bala.reactive.domain.MovieInfo;
import ua.bala.reactive.exception.MoviesInfoClientException;
import ua.bala.reactive.exception.MoviesInfoServerException;

@Component
@Slf4j
public class MoviesInfoRestClient {

    public final WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {

        String url = moviesInfoUrl.concat("/{id}");
        return webClient
            .get()
            .uri(url, movieId)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                log.info("Status code is : {}", clientResponse.statusCode().value());
                if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    return Mono.error(new MoviesInfoClientException(
                        "There is no MovieInfo Available for the passed on Id : " + movieId,
                        clientResponse.statusCode().value()
                    ));
                }
                return clientResponse.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(responseMessage, clientResponse.statusCode().value())));
            })
            .onStatus(HttpStatus::is5xxServerError, clientResponse1 -> {
                log.info("Status code is : {}", clientResponse1.statusCode().value());
                return clientResponse1.bodyToMono(String.class)
                    .flatMap(responseMessage -> Mono.error(new MoviesInfoServerException("Server Error in MoviesInfoServer" + responseMessage)));
            })
            .bodyToMono(MovieInfo.class)
            .log();

    }
}
