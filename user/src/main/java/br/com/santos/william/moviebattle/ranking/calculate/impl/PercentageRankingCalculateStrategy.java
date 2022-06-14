package br.com.santos.william.moviebattle.ranking.calculate.impl;

import br.com.santos.william.moviebattle.ranking.Ranking;
import br.com.santos.william.moviebattle.ranking.calculate.RankingCalculateStrategy;
import br.com.santos.william.moviebattle.ranking.dto.BattleMovieFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ranking.strategy.calculate.type", havingValue = "percentage", matchIfMissing = true)
class PercentageRankingCalculateStrategy implements RankingCalculateStrategy {

    private Logger log = LoggerFactory.getLogger(getClass());

    public PercentageRankingCalculateStrategy() {
        log.trace("Registering percentage for RankingCalculate");
    }

    @Override
    public void calculate(BattleMovieFinished battle, Ranking ranking) {
        if (ranking.getScore() == null) {
            log.trace("Ranking: {} without score, setting zero", ranking.getId());
            ranking.setScore(0f);
        }
        log.debug("Ranking old score is: {}", ranking.getScore());
        var increment = calculateScore(battle);
        ranking.setScore(ranking.getScore() + increment);
        log.info("Updated score to: {} for player: {}", ranking.getScore(), battle.getPlayerDto().getName());
    }

    private float calculateScore(BattleMovieFinished battle) {
        var score = 0f;
        log.trace("{} hists in battle: {}", battle.getHits(), battle.getId());

        if (battle.getTotalRounds() > 0) {
            float percentage = battle.getHits() * 100 / battle.getTotalRounds();
            log.debug("Percentage of wins rounds is: {} in battle: {}", percentage, battle.getId());
            score = battle.getTotalRounds() * percentage;
        }
        return score;
    }

}
