package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.BattleStatus;
import br.com.santos.william.moviebattle.battle.BattleStatusEvent;
import br.com.santos.william.moviebattle.round.RoundStatus;
import br.com.santos.william.moviebattle.user.Session;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RankingService {

    private final Session session;
    private final RankingRepository repository;

    public RankingService(
            Session session,
            RankingRepository repository
    ) {
        this.session = session;
        this.repository = repository;
    }

    @Async
    @EventListener
    public void calculateScore(BattleStatusEvent event) {
        if (event.getNewStatus() == BattleStatus.FINISHED) {
            var battle = (Battle) event.getSource();
            var ranking = repository.findByUser(battle.getPlayer())
                    .orElse(generateRanking());
            if (ranking.getScore() == null) {
                ranking.setScore(0f);
            }
            var increment = calculateScore(battle);
            ranking.setScore(ranking.getScore() + increment);
            repository.save(ranking);
        }
    }

    private float calculateScore(Battle battle) {
        var score = 0f;
        var rounds = battle.getRounds();
        if (!rounds.isEmpty()) {
            var hits = rounds.stream()
                    .filter(it -> it.getStatus() == RoundStatus.HIT)
                    .count();
            float percentage = hits * 100 / rounds.size();
            score = rounds.size() * percentage;
        }
        return score;
    }

    private Ranking generateRanking() {
        var ranking = new Ranking();
        ranking.setUser(session.getUser());
        ranking.setScore(0f);
        return ranking;
    }
}
