package br.com.santos.william.moviebattle.round.moviechoicestrategy.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.round.moviechoicestrategy.MovieChoiceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "movie.choice.strategy.type", havingValue = "random")
class RandomMovieChoiceStrategy implements MovieChoiceStrategy {

    private Logger log = LoggerFactory.getLogger(getClass());

    public RandomMovieChoiceStrategy() {
        log.trace("Registering \"random\" for MovieChoiceStrategy");
    }

    @Override
    public Pair<Movie, Movie> choice(Battle battle, List<Movie> movies) {
        var random = new Random();
        var length = movies.size();

        Movie first, second;
        do {
            log.debug("Building new pair for battle: {}", battle.getId());
            first = movies.get(random.nextInt(length));
            second = movies.get(random.nextInt(length));
        } while (first.getId().equals(second.getId()));

        return Pair.of(first, second);
    }
}
