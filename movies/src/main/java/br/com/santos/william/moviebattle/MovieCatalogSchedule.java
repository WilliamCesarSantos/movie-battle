package br.com.santos.william.moviebattle;

import br.com.santos.william.moviebattle.movie.MovieDto;
import br.com.santos.william.moviebattle.movie.MovieService;
import br.com.santos.william.moviebattle.omdb.OmdbService;
import br.com.santos.william.moviebattle.omdb.dto.OmdbMovie;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MovieCatalogSchedule {

    private final MovieService movieService;
    private final OmdbService omdbService;
    private final Logger log = LoggerFactory.getLogger(MovieCatalogSchedule.class);

    public MovieCatalogSchedule(MovieService movieService, OmdbService omdbService) {
        this.movieService = movieService;
        this.omdbService = omdbService;
    }

    @Scheduled(cron = "${movie-catalog.schedule.cron}")
    @Timed(description = "Time spent to load movies")
    public void warmUp() {
        omdbService.listAll()
                .peek(it -> log.info("Inserting movie: {}.", it.getTitle()))
                .map(this::convert)
                .forEach(movieService::publish);
        log.info("Inserted movies! Let it go.");
    }

    private MovieDto convert(OmdbMovie omdb) {
        var movie = new MovieDto();
        movie.setId(omdb.getImdbID());
        movie.setRating(omdb.getImdbRating());
        movie.setVotes(omdb.getImdbVotes());
        movie.setName(omdb.getTitle());
        movie.setGenre(omdb.getGenre());
        movie.setPoster(omdb.getPoster());
        return movie;
    }
}
