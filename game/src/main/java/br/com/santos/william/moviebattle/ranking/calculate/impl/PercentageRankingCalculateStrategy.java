package br.com.santos.william.moviebattle.ranking.calculate.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.ranking.Ranking;
import br.com.santos.william.moviebattle.ranking.calculate.RankingCalculateStrategy;
import br.com.santos.william.moviebattle.round.RoundStatus;
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
    public void calculate(Battle battle, Ranking ranking) {
        if (ranking.getScore() == null) {
            log.trace("Ranking: {} without score, setting zero", ranking.getId());
            ranking.setScore(0f);
        }
        log.debug("Ranking old score is: {}", ranking.getScore());
        var increment = calculateScore(battle);
        ranking.setScore(ranking.getScore() + increment);
        log.info("Updated score to: {} for player: {}", ranking.getScore(), battle.getPlayer().getName());
    }

    private float calculateScore(Battle battle) {
        var score = 0f;
        var rounds = battle.getRounds();
        if (!rounds.isEmpty()) {
            var hits = rounds.stream()
                    .filter(it -> it.getStatus() == RoundStatus.HIT)
                    .count();
            log.trace("{} hists in battle: {}", hits, battle.getId());

            float percentage = hits * 100 / rounds.size();
            log.debug("Percentage of wins rounds is: {} in battle: {}", percentage, battle.getId());

            score = rounds.size() * percentage;
        }
        return score;
    }

}
