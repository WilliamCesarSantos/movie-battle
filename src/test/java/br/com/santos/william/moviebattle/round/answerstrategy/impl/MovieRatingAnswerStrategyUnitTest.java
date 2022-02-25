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
//@TestPropertySource(properties = { "my.spring.property=20" })
public class MovieRatingAnswerStrategyUnitTest {

    @InjectMocks
    private MovieRatingAnswerStrategy strategy;

    @Test
    public void answerReturnsHitWhenChosenRatingGreater() {
        var movieOne = new Movie();
        movieOne.setId(1l);
        movieOne.setRating(1.0f);

        var movieTwo = new Movie();
        movieTwo.setId(2l);
        movieTwo.setRating(2.0f);

        var round = new Round();
        round.setFirst(movieOne);
        round.setSecond(movieTwo);

        var status = strategy.answer(round, movieTwo);

        assertEquals(RoundStatus.HIT, status);
    }

    @Test
    public void answerReturnsMissWhenChosenRatingLess() {
        var movieOne = new Movie();
        movieOne.setId(1l);
        movieOne.setRating(1.0f);

        var movieTwo = new Movie();
        movieTwo.setId(2l);
        movieTwo.setRating(2.0f);

        var round = new Round();
        round.setFirst(movieOne);
        round.setSecond(movieTwo);

        var status = strategy.answer(round, movieOne);

        assertEquals(RoundStatus.MISS, status);
    }

}
