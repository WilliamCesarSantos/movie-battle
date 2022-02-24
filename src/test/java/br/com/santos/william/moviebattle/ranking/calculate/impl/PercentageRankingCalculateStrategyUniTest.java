package br.com.santos.william.moviebattle.ranking.calculate.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.ranking.Ranking;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PercentageRankingCalculateStrategyUniTest {

    private final PercentageRankingCalculateStrategy strategy = new PercentageRankingCalculateStrategy();

    @Test
    public void calculateScoreMustBeEquals198() {
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
        battle.setRounds(List.of(
                roundOne,
                roundTwo,
                roundThree
        ));

        strategy.calculate(battle, ranking);

        assertEquals(198f, ranking.getScore());
    }

    @Test
    public void calculateShouldNotUpdateScoreWhenBattleHasNoRound() {
        var ranking = new Ranking();
        ranking.setPlayer(new Player());
        ranking.setId(1l);
        ranking.setScore(null);

        var battle = new Battle();
        battle.setRounds(List.of());

        strategy.calculate(battle, ranking);

        assertEquals(0f, ranking.getScore());
    }

}
