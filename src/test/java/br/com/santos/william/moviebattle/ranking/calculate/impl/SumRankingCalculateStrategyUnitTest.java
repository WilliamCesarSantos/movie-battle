package br.com.santos.william.moviebattle.ranking.calculate.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.ranking.Ranking;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class SumRankingCalculateStrategyUnitTest {

    @InjectMocks
    private SumRankingCalculateStrategy strategy;

    @Test
    public void calculateShouldIncreaseHits() {
        var player = new Player();
        player.setName("unit-test");

        var ranking = new Ranking();
        ranking.setPlayer(new Player());
        ranking.setId(1l);

        var roundOne = new Round();
        roundOne.setStatus(RoundStatus.HIT);

        var roundTwo = new Round();
        roundTwo.setStatus(RoundStatus.HIT);

        var roundThree = new Round();
        roundThree.setStatus(RoundStatus.MISS);

        var battle = new Battle();
        battle.setPlayer(player);
        battle.setRounds(List.of(
                roundOne,
                roundTwo,
                roundThree
        ));

        strategy.calculate(battle, ranking);

        assertEquals(2f, ranking.getScore());
    }

    @Test
    public void calculateShouldNotUpdateScoreWhenBattleHasNoRound() {
        var player = new Player();
        player.setName("unit-test");

        var ranking = new Ranking();
        ranking.setPlayer(new Player());
        ranking.setId(1l);
        ranking.setScore(null);

        var battle = new Battle();
        battle.setRounds(List.of());
        battle.setPlayer(player);

        strategy.calculate(battle, ranking);

        assertEquals(0f, ranking.getScore());
    }

}
