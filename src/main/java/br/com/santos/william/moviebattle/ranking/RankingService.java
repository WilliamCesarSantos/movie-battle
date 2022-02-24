package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.BattleStatus;
import br.com.santos.william.moviebattle.battle.BattleStatusEvent;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.ranking.calculate.RankingCalculateStrategy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RankingService {

    private final RankingRepository repository;
    private final RankingCalculateStrategy strategy;

    public RankingService(
            RankingRepository repository,
            RankingCalculateStrategy strategy
    ) {
        this.repository = repository;
        this.strategy = strategy;
    }

    public Optional<Ranking> list(Player player) {
        return repository.findByPlayer(player);
    }

    @EventListener
    public void calculateScore(BattleStatusEvent event) {
        if (event.getNewStatus() == BattleStatus.FINISHED) {
            var battle = (Battle) event.getSource();
            var ranking = repository.findByPlayer(battle.getPlayer())
                    .orElse(buildRanking(battle.getPlayer()));
            strategy.calculate(battle, ranking);
            repository.save(ranking);
        }
    }

    private Ranking buildRanking(Player player) {
        var ranking = new Ranking();
        ranking.setPlayer(player);
        ranking.setScore(0f);
        return ranking;
    }
}
