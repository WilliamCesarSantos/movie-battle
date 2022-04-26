package br.com.santos.william.moviebattle.movie;

import br.com.santos.william.moviebattle.omdb.OmdbService;
import br.com.santos.william.moviebattle.omdb.dto.OmdbMovie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MovieWarmUp {

    private final MovieService movieService;
    private final OmdbService omdbService;
    private final Logger log = LoggerFactory.getLogger(MovieWarmUp.class);

    public MovieWarmUp(MovieService movieService, OmdbService omdbService) {
        this.movieService = movieService;
        this.omdbService = omdbService;
    }

    @EventListener
    public void warmUp(ApplicationReadyEvent event) {
        omdbService.listAll()
                .peek(it -> log.info("Inserting movie: {}.", it.getTitle()))
                .map(this::convert)
                .forEach(movieService::insert);
        log.info("Inserted movies! Let it go.");
    }

    private Movie convert(OmdbMovie omdb) {
        var movie = new Movie();
        movie.setId(omdb.getImdbID());
        movie.setRating(omdb.getImdbRating());
        movie.setVotes(omdb.getImdbVotes());
        movie.setName(omdb.getTitle());
        movie.setGenre(omdb.getGenre());
        movie.setPoster(omdb.getPoster());
        return movie;
    }
}
