package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.commons.exception.ResourceNotFoundException;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.PlayerRepository;
import br.com.santos.william.moviebattle.ranking.calculate.RankingCalculateStrategy;
import br.com.santos.william.moviebattle.ranking.dto.BattleMovieFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RankingService {

    private final RankingRepository repository;
    private final RankingCalculateStrategy strategy;

    private final PlayerRepository playerRepository;
    private Logger log = LoggerFactory.getLogger(getClass());

    public RankingService(
            RankingRepository repository,
            RankingCalculateStrategy strategy,
            PlayerRepository playerRepository
    ) {
        this.repository = repository;
        this.strategy = strategy;
        this.playerRepository = playerRepository;
    }

    public Optional<Ranking> list(Player player) {
        return repository.findByPlayer(player);
    }

    @SqsListener("${battle-movie.finished}")
    public void calculateScore(BattleMovieFinished battle) {
        log.info("Battle: {} was finished. Calculating new score ranking...", battle.getId());
        var player = playerRepository.findById(battle.getPlayerDto().getId())
                .orElseThrow(ResourceNotFoundException::new);
        var ranking = repository.findByPlayer(player)
                .orElse(buildRanking(player));
        strategy.calculate(battle, ranking);
        repository.save(ranking);
        log.info("Ranking: {} updated", battle.getId());
    }

    private Ranking buildRanking(Player player) {
        log.trace("Player: {} does not have ranking. Generating new", player.getName());
        var ranking = new Ranking();
        ranking.setPlayer(player);
        ranking.setScore(0f);
        return ranking;
    }
}
