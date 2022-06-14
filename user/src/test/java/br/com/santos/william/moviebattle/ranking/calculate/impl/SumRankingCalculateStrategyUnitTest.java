package br.com.santos.william.moviebattle.ranking.calculate.impl;

import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.dto.PlayerDto;
import br.com.santos.william.moviebattle.ranking.Ranking;
import br.com.santos.william.moviebattle.ranking.dto.BattleMovieFinished;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class SumRankingCalculateStrategyUnitTest {

    @InjectMocks
    private SumRankingCalculateStrategy strategy;

    @Test
    public void calculateShouldIncreaseHits() {
        var ranking = new Ranking();
        ranking.setPlayer(new Player());
        ranking.setId(1l);

        BattleMovieFinished finished = new BattleMovieFinished();
        finished.setId(10l);
        finished.setHits(2);
        finished.setMiss(1);
        finished.setTotalRounds(3);
        finished.setPlayerDto(new PlayerDto(10l, "unit-test", "unit-test"));

        strategy.calculate(finished, ranking);

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

        BattleMovieFinished finished = new BattleMovieFinished();
        finished.setId(10l);
        finished.setHits(0);
        finished.setMiss(0);
        finished.setTotalRounds(0);
        finished.setPlayerDto(new PlayerDto(10l, "unit-test", "unit-test"));


        strategy.calculate(finished, ranking);

        assertEquals(0f, ranking.getScore());
    }

}
