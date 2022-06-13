package br.com.santos.william.moviebattle.battle.gameover.strategy.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.gameover.strategy.GameOverStrategy;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "game.over.strategy", havingValue = "losses-percentage-limit")
public class LossesPercentageGameOverStrategy implements GameOverStrategy {

    private final Float percentageToLost;
    private Logger log = LoggerFactory.getLogger(getClass());

    public LossesPercentageGameOverStrategy(
            @Value("${game.over.losses.percentage.limit}") Float percentageToLost
    ) {
        log.trace("Registering losses-percentage-limit for GameOverStrategy");
        this.percentageToLost = percentageToLost;
    }

    @Override
    public Boolean isGameOver(Battle battle) {
        var rounds = battle.getRounds();
        var miss = rounds.stream()
                .filter(it -> it.getStatus() == RoundStatus.MISS)
                .count();
        var percentage = miss * 100 / rounds.size();
        log.debug("Percentage of losses round is: {} and the limit is: {}", percentage, percentageToLost);
        return percentage > percentageToLost;
    }

}
