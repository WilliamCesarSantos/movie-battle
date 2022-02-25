package br.com.santos.william.moviebattle.round.moviechoicestrategy.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class NonRepeatMovieChoiceStrategyUnitTest {

    @InjectMocks
    private NonRepeatMovieChoiceStrategy strategy;

    @Test
    public void choiceReturnsNewPairWithDiferentsMovies() {
        var movies = TestUtil.buildMovies(4);
        var pair = strategy.choice(new Battle(), movies);
        assertFalse(pair.getFirst().getId() == pair.getSecond().getId());
    }

    @Test
    public void choiceReturnsNewPairWithMovieNotUsed() {
        var movies = TestUtil.buildMovies(10);

        var battle = new Battle();

        var round = new Round();
        round.setFirst(movies.get(0));
        round.setSecond(movies.get(1));
        round.setBattle(battle);

        battle.setRounds(new ArrayList<>());
        battle.getRounds().add(round);

        var pair = strategy.choice(battle, movies);
        assertTrue(movies.get(0).getId() != pair.getFirst().getId());
        assertTrue(movies.get(0).getId() != pair.getFirst().getId());
        assertTrue(movies.get(1).getId() != pair.getSecond().getId());
        assertTrue(movies.get(1).getId() != pair.getSecond().getId());
    }

}
