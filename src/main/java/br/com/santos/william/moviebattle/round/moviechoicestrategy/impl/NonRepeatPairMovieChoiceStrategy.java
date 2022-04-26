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

import java.util.*;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "movie.choice.strategy.type", havingValue = "non-repeat-pair", matchIfMissing = true)
class NonRepeatPairMovieChoiceStrategy extends RandomMovieChoiceStrategy implements MovieChoiceStrategy {

    private Logger log = LoggerFactory.getLogger(getClass());

    public NonRepeatPairMovieChoiceStrategy() {
        log.trace("Registering non-repeat-pair for MovieChoiceStrategy");
    }

    @Override
    public Pair<Movie, Movie> choice(Battle battle, List<Movie> movies) {
        var usedPairs = usedPairs(battle);
        Pair<Movie, Movie> pair;
        do {
            log.debug("Building new pair for battle: {}", battle.getId());
            pair = super.choice(battle, movies);
        } while (
                usedPairs.contains(
                        createOrderedPair(pair.getFirst(), pair.getSecond())
                )
        );
        return pair;
    }

    private Set<Pair<String, String>> usedPairs(Battle battle) {
        var rounds = battle.getRounds() != null ? battle.getRounds() : Collections.<Round>emptyList();
        log.debug("Collection pair of movies on {} rounds", rounds.size());
        return rounds.stream()
                .map(it -> {
                    var first = it.getFirst();
                    var second = it.getSecond();
                    return createOrderedPair(first, second);
                })
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(Pair::getFirst))
                ));
    }

    private Pair<String, String> createOrderedPair(Movie first, Movie second) {
        log.debug("Creating new ordered pair for movies: {}, {}", first.getId(), second.getId());
        var firstId = first.getId();
        var secondId = second.getId();
        Pair<String, String> pair;
        if (firstId.compareTo(secondId) < 0) {
            pair = Pair.of(firstId, secondId);
        } else {
            pair = Pair.of(secondId, firstId);
        }
        log.trace("Pair: {} used", pair);
        return pair;
    }

}
