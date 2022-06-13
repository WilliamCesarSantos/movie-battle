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
@ConditionalOnProperty(name = "game.over.strategy", havingValue = "losses-limit", matchIfMissing = true)
public class LossesQuizGameOverStrategy implements GameOverStrategy {

    private final Integer lossesLimit;
    private Logger log = LoggerFactory.getLogger(getClass());

    public LossesQuizGameOverStrategy(
            @Value("${game.over.losses.limit}") Integer lossesLimit
    ) {
        log.trace("Registering losses-limit for GameOverStrategy");
        this.lossesLimit = lossesLimit;
    }

    @Override
    public Boolean isGameOver(Battle battle) {
        var losses = battle.getRounds().stream()
                .filter(it -> it.getStatus() == RoundStatus.MISS)
                .count();
        log.debug("Losses rounds is: {} and the limit is: {}", losses, lossesLimit);
        return losses > lossesLimit;
    }

}
