package br.com.santos.william.moviebattle.round.answerstrategy.impl;

import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import br.com.santos.william.moviebattle.round.answerstrategy.AnswerStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static br.com.santos.william.moviebattle.round.RoundStatus.HIT;
import static br.com.santos.william.moviebattle.round.RoundStatus.MISS;

@Component
@ConditionalOnProperty(name = "answer.strategy.type", havingValue = "rating")
class MovieRatingAnswerStrategy implements AnswerStrategy {

    private Logger log = LoggerFactory.getLogger(getClass());

    public MovieRatingAnswerStrategy() {
        log.trace("Registering \"rating\" type for AnswerStrategy");
    }

    @Override
    public RoundStatus answer(Round round, Movie chosen) {
        var firstRating = round.getFirst().getRating();
        log.debug("Rating: {} for movie: {}", firstRating, round.getFirst().getName());

        var secondRating = round.getSecond().getRating();
        log.debug("Rating: {} for movie: {}", secondRating, round.getSecond().getName());

        var movie = firstRating > secondRating ? round.getFirst() : round.getSecond();
        log.debug("Movie: {} has greater rating", movie.getName());

        var result = movie.getId() == chosen.getId() ? HIT : MISS;
        log.debug("Result for answer is: {}", result);

        return result;
    }

}
