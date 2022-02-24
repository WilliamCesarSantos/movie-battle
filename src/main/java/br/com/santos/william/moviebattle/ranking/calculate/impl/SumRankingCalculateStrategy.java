package br.com.santos.william.moviebattle.ranking.calculate.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.ranking.Ranking;
import br.com.santos.william.moviebattle.ranking.calculate.RankingCalculateStrategy;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ranking.strategy.calculate.type", havingValue = "sum")
class SumRankingCalculateStrategy implements RankingCalculateStrategy {

    @Override
    public void calculate(Battle battle, Ranking ranking) {
        if (ranking.getScore() == null) {
            ranking.setScore(0f);
        }
        var increment = calculateScore(battle);
        ranking.setScore(ranking.getScore() + increment);
    }

    private float calculateScore(Battle battle) {
        var score = 0f;
        var rounds = battle.getRounds();
        if (!rounds.isEmpty()) {
            score = rounds.stream()
                    .filter(it -> it.getStatus() == RoundStatus.HIT)
                    .count();
        }
        return score;
    }

}
