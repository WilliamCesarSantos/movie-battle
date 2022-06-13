package br.com.santos.william.moviebattle.battle.gameover.strategy.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class TimesGameOverStrategyUnitTest {

    @InjectMocks
    private TimesGameOverStrategy strategy;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(strategy, "timesLimit", 1);
    }

    @Test
    public void isGameOverShouldReturnFalseWhenOncePlayed() {
        var round = new Round();
        round.setStatus(RoundStatus.MISS);

        var battle = new Battle();
        battle.setRounds(new ArrayList<>());
        battle.getRounds().add(round);
        round.setBattle(battle);

        var gameOver = strategy.isGameOver(battle);

        assertFalse(gameOver);
    }

    @Test
    public void isGameOverShouldReturnTrueWhenTwicePlayed() {
        var roundOne = new Round();
        roundOne.setStatus(RoundStatus.HIT);

        var roundTwo = new Round();
        roundTwo.setStatus(RoundStatus.HIT);

        var battle = new Battle();
        battle.setRounds(new ArrayList<>());
        battle.getRounds().add(roundOne);
        battle.getRounds().add(roundTwo);
        roundOne.setBattle(battle);
        roundTwo.setBattle(battle);

        var gameOver = strategy.isGameOver(battle);

        assertTrue(gameOver);
    }

}
