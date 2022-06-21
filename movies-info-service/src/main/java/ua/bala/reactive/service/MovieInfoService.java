package ua.bala.reactive.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.reactive.domain.MovieInfo;
import ua.bala.reactive.repository.MovieInfoRepository;

@Service
@Slf4j
public class MovieInfoService {

    private final MovieInfoRepository movieInfoRepository;

    public MovieInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Flux<MovieInfo> getAllMovieInfo() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getMovieInfoByYear(Integer year) {
        log.info("getMovieInfoByYear : {}", year);
        return movieInfoRepository.findByYear(year);
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id) {
        return movieInfoRepository.findById(id)
            .flatMap(movieInfo -> {
                movieInfo.setCast(updatedMovieInfo.getCast());
                movieInfo.setName(updatedMovieInfo.getName());
                movieInfo.setReleaseDate(updatedMovieInfo.getReleaseDate());
                movieInfo.setYear(updatedMovieInfo.getYear());
                return movieInfoRepository.save(movieInfo);
            });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }
}
