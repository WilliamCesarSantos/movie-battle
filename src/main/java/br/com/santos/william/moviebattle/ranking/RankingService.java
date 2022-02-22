package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.BattleRepository;
import br.com.santos.william.moviebattle.battle.BattleStatus;
import br.com.santos.william.moviebattle.battle.BattleStatusEvent;
import br.com.santos.william.moviebattle.round.RoundStatus;
import br.com.santos.william.moviebattle.user.Session;
import br.com.santos.william.moviebattle.user.User;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class RankingService {

    private final Session session;
    private final RankingRepository repository;
    private final BattleRepository battleRepository;

    public RankingService(
            Session session,
            RankingRepository repository,
            BattleRepository battleRepository
    ) {
        this.session = session;
        this.repository = repository;
        this.battleRepository = battleRepository;
    }

    @Async
    @EventListener
    public void calculateScore(BattleStatusEvent event) {
        if (event.getNewStatus() == BattleStatus.FINISHED) {
            var battle = (Battle) event.getSource();
            var ranking = repository.findByUser(battle.getPlayer())
                    .orElse(generateRanking());
            ranking.setScore(calculateScore(battle.getPlayer()));
            repository.save(ranking);
        }
    }

    private float calculateScore(User player) {
        var battles = battleRepository.findByPlayer(player);
        var rounds = battles.stream()
                .flatMap(it -> it.getRounds().stream())
                .collect(Collectors.toList());
        var hits = rounds.stream()
                .filter(it -> it.getStatus() == RoundStatus.HIT)
                .count();
        float percentage = hits * 100 / rounds.size();
        return rounds.size() * percentage;
    }

    private Ranking generateRanking() {
        var ranking = new Ranking();
        ranking.setUser(session.getUser());
        ranking.setScore(0f);
        return ranking;
    }
}
