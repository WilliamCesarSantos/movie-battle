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
public class LossesPercentageGameOverStrategyUnitTest {

    @InjectMocks
    private LossesPercentageGameOverStrategy strategy;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(strategy, "percentageToLost", 10f);
    }

    @Test
    public void isGameOverShouldReturnTrueWhenErrorsIsMoreTen() {
        var round = new Round();
        round.setStatus(RoundStatus.MISS);

        var battle = new Battle();
        battle.setRounds(new ArrayList<>());
        battle.getRounds().add(round);
        round.setBattle(battle);

        var gameOver = strategy.isGameOver(battle);

        assertTrue(gameOver);
    }

    @Test
    public void isGameOverShouldReturnFalseWhenErrorsIsLessTen() {
        var round = new Round();
        round.setStatus(RoundStatus.HIT);

        var battle = new Battle();
        battle.setRounds(new ArrayList<>());
        battle.getRounds().add(round);
        round.setBattle(battle);

        var gameOver = strategy.isGameOver(battle);

        assertFalse(gameOver);
    }

}
