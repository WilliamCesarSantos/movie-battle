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

@ExtendWith(SpringExtension.class)
public class NonRepeatPairMovieChoiceStrategyUnitTest {

    @InjectMocks
    private NonRepeatPairMovieChoiceStrategy strategy;

    @Test
    public void choiceReturnsNewPairWithDiferentsMovies() {
        var movies = TestUtil.buildMovies(4);
        var pair = strategy.choice(new Battle(), movies);
        assertFalse(pair.getFirst().getId() == pair.getSecond().getId());
    }

    @Test
    public void choiceReturnsNewPairWithPairNotUsed() {
        var movies = TestUtil.buildMovies(10);

        var battle = new Battle();

        var roundOne = new Round();
        roundOne.setFirst(movies.get(0));
        roundOne.setSecond(movies.get(1));
        roundOne.setBattle(battle);

        var roundTwo = new Round();
        roundTwo.setFirst(movies.get(9));
        roundTwo.setSecond(movies.get(8));
        roundTwo.setBattle(battle);

        battle.setRounds(new ArrayList<>());
        battle.getRounds().add(roundOne);
        battle.getRounds().add(roundTwo);

        var pair = strategy.choice(battle, movies);

        var usedSet = roundOne.getFirst().getId() + "_" + roundOne.getSecond().getId();
        var set = pair.getFirst().getId() + "_" + pair.getSecond().getId();
        assertFalse(usedSet.equals(set));

        usedSet = roundOne.getSecond().getId() + "_" + roundOne.getFirst().getId();
        set = pair.getFirst().getId() + "_" + pair.getSecond().getId();
        assertFalse(usedSet.equals(set));

        usedSet = roundOne.getFirst().getId() + "_" + roundOne.getSecond().getId();
        set = pair.getSecond().getId() + "_" + pair.getFirst().getId();
        assertFalse(usedSet.equals(set));

        usedSet = roundOne.getSecond().getId() + "_" + roundOne.getFirst().getId();
        set = pair.getSecond().getId() + "_" + pair.getFirst().getId();
        assertFalse(usedSet.equals(set));

        usedSet = roundTwo.getFirst().getId() + "_" + roundTwo.getSecond().getId();
        set = pair.getFirst().getId() + "_" + pair.getSecond().getId();
        assertFalse(usedSet.equals(set));

        usedSet = roundTwo.getSecond().getId() + "_" + roundTwo.getFirst().getId();
        set = pair.getFirst().getId() + "_" + pair.getSecond().getId();
        assertFalse(usedSet.equals(set));

        usedSet = roundTwo.getFirst().getId() + "_" + roundTwo.getSecond().getId();
        set = pair.getSecond().getId() + "_" + pair.getFirst().getId();
        assertFalse(usedSet.equals(set));

        usedSet = roundTwo.getSecond().getId() + "_" + roundTwo.getFirst().getId();
        set = pair.getSecond().getId() + "_" + pair.getFirst().getId();
        assertFalse(usedSet.equals(set));
    }

}
