package br.com.santos.william.moviebattle.battle.gameover.strategy.impl;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.gameover.strategy.GameOverStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "game.over.strategy", havingValue = "times-limit")
public class TimesGameOverStrategy implements GameOverStrategy {

    private final Integer timesLimit;
    private Logger log = LoggerFactory.getLogger(getClass());

    public TimesGameOverStrategy(
            @Value("${game.over.times.limit}") Integer timesLimit
    ) {
        log.trace("Registering times-limit for GameOverStrategy");
        this.timesLimit = timesLimit;
    }

    @Override
    public Boolean isGameOver(Battle battle) {
        var rounds = battle.getRounds().size();
        log.debug("Rounds is: {} and the limit is: {}", rounds, timesLimit);
        return rounds > timesLimit;
    }

}
