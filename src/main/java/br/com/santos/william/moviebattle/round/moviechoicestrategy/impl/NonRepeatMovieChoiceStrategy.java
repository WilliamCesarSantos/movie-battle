package br.com.santos.william.moviebattle.round.moviechoicestrategy.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.moviechoicestrategy.MovieChoiceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "movie.choice.strategy.type", havingValue = "non-repeat-movie")
class NonRepeatMovieChoiceStrategy extends RandomMovieChoiceStrategy implements MovieChoiceStrategy {

    private Logger log = LoggerFactory.getLogger(getClass());

    public NonRepeatMovieChoiceStrategy() {
        log.trace("Registering non-repeat-movie for MovieChoiceStrategy");
    }

    @Override
    public Pair<Movie, Movie> choice(Battle battle, List<Movie> movies) {
        var ids = usedMovies(battle);
        var newMovies = movies.stream().filter(
                        it -> !ids.contains(it.getId())
                )
                .peek(it -> log.trace("Removing movie: {} has been used for battle: {}", it.getName(), battle.getId()))
                .collect(Collectors.toList());

        return super.choice(battle, newMovies);
    }

    private Set<Long> usedMovies(Battle battle) {
        var rounds = battle.getRounds() != null ? battle.getRounds() : Collections.<Round>emptyList();
        log.debug("Collection movies on {} rounds", rounds.size());
        return rounds.stream()
                .collect(
                        TreeSet::new,
                        (collection, round) -> {
                            collection.add(round.getFirst().getId());
                            collection.add(round.getSecond().getId());
                        },
                        (first, second) -> first.addAll(second)
                );
    }

}
