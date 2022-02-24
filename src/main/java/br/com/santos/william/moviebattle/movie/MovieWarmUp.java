package br.com.santos.william.moviebattle.movie;

import br.com.santos.william.moviebattle.omdb.OmdbService;
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
                .peek(it -> log.info("Insert movie: {}. Waiting....", it.getName()))
                .forEach(movieService::insert);
        log.info("Inserted movies");
    }
}
