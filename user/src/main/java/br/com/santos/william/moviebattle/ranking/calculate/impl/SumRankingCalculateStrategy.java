package br.com.santos.william.moviebattle.ranking.calculate.impl;

import br.com.santos.william.moviebattle.ranking.Ranking;
import br.com.santos.william.moviebattle.ranking.calculate.RankingCalculateStrategy;
import br.com.santos.william.moviebattle.ranking.dto.BattleMovieFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ranking.strategy.calculate.type", havingValue = "sum")
class SumRankingCalculateStrategy implements RankingCalculateStrategy {

    private Logger log = LoggerFactory.getLogger(getClass());

    public SumRankingCalculateStrategy() {
        log.trace("Registering sum for RankingCalculate");
    }

    @Override
    public void calculate(BattleMovieFinished battle, Ranking ranking) {
        if (ranking.getScore() == null) {
            log.trace("Ranking: {} without score, setting zero", ranking.getId());
            ranking.setScore(0f);
        }
        log.debug("Ranking old score is: {}", ranking.getScore());
        var increment = battle.getHits();
        ranking.setScore(ranking.getScore() + increment);
        log.info("Updated score to: {} for player: {}", ranking.getScore(), battle.getPlayerDto().getName());
    }

}
