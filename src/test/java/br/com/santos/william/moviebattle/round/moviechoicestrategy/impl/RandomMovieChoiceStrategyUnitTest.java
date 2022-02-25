package br.com.santos.william.moviebattle.round.moviechoicestrategy.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class RandomMovieChoiceStrategyUnitTest {

    @InjectMocks
    private RandomMovieChoiceStrategy strategy;

    @Test
    public void choiceReturnsNewPairWithRandomMovie() {
        var movies = TestUtil.buildMovies(10);
        var pair = strategy.choice(new Battle(), movies);
        assertNotNull(pair);
    }

    @Test
    public void choiceReturnsNewPairWithDiferentsMovies() {
        var movies = TestUtil.buildMovies(4);
        var pair = strategy.choice(new Battle(), movies);
        assertFalse(pair.getFirst().getId() == pair.getSecond().getId());
    }

}
