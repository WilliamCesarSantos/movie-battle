package br.com.santos.william.moviebattle.round.answerstrategy.impl;

import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class MovieScoreAnswerStrategyUniTest {

    @InjectMocks
    private MovieScoreAnswerStrategy strategy;

    @Test
    public void answerReturnsHitWhenChosenScoreGreater() {
        var movieOne = new Movie();
        movieOne.setId("1");
        movieOne.setRating(1.0f);
        movieOne.setVotes(1);

        var movieTwo = new Movie();
        movieTwo.setId("2");
        movieTwo.setRating(2.0f);
        movieTwo.setVotes(2);

        var round = new Round();
        round.setFirst(movieOne);
        round.setSecond(movieTwo);

        var status = strategy.answer(round, movieTwo);

        assertEquals(RoundStatus.HIT, status);
    }

    @Test
    public void answerReturnsMissWhenChosenScoreLess() {
        var movieOne = new Movie();
        movieOne.setId("1");
        movieOne.setRating(1.0f);
        movieOne.setVotes(1);

        var movieTwo = new Movie();
        movieTwo.setId("2");
        movieTwo.setRating(2.0f);
        movieTwo.setVotes(2);

        var round = new Round();
        round.setFirst(movieOne);
        round.setSecond(movieTwo);

        var status = strategy.answer(round, movieOne);

        assertEquals(RoundStatus.MISS, status);
    }

}
