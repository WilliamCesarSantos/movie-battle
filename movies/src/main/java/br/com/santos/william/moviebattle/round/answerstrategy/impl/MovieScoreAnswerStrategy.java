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
@ConditionalOnProperty(name = "answer.strategy.type", havingValue = "score", matchIfMissing = true)
class MovieScoreAnswerStrategy implements AnswerStrategy {

    private Logger log = LoggerFactory.getLogger(getClass());

    public MovieScoreAnswerStrategy() {
        log.trace("Registering \"score\" type for AnswerStrategy");
    }

    @Override
    public RoundStatus answer(Round round, Movie chosen) {
        var firstScore = calculateScore(round.getFirst());
        log.debug("Score: {} for movie: {}", firstScore, round.getFirst().getName());

        var secondScore = calculateScore(round.getSecond());
        log.debug("Score: {} for movie: {}", secondScore, round.getSecond().getName());

        var movie = firstScore >= secondScore ? round.getFirst() : round.getSecond();
        log.debug("Movie: {} has greater score", movie.getName());

        var result = movie.getId() == chosen.getId() ? HIT : MISS;
        log.debug("Result for answer is: {}", result);

        return result;
    }

    Float calculateScore(Movie movie) {
        return movie.getRating() * movie.getVotes();
    }
}
