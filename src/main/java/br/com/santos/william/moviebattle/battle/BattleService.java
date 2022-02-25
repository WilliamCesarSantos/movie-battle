package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.battle.event.BattleStatusEvent;
import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.battle.gameover.strategy.GameOverStrategy;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.Session;
import br.com.santos.william.moviebattle.round.Answer;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class BattleService {

    private final BattleRepository repository;
    private final Session session;
    private final RoundService roundService;
    private final ApplicationEventPublisher publisher;
    private final GameOverStrategy gameOverStrategy;
    private Logger log = LoggerFactory.getLogger(getClass());

    public BattleService(
            BattleRepository repository,
            Session session,
            RoundService roundService,
            ApplicationEventPublisher publisher,
            GameOverStrategy gameOverStrategy
    ) {
        this.repository = repository;
        this.session = session;
        this.roundService = roundService;
        this.publisher = publisher;
        this.gameOverStrategy = gameOverStrategy;
    }

    public Battle create(Battle battle) {
        log.debug("Creating new battle for: {}", session.getPlayer().getName());
        battle.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        battle.setStatus(BattleStatus.CREATED);
        battle.setPlayer(session.getPlayer());
        var created = repository.save(battle);
        log.debug("Publishing new event for battle: {}", battle);
        publisher.publishEvent(new BattleStatusEvent(created, null, created.getStatus()));
        log.info("Created new battle: {}", battle.getId());
        return created;
    }

    public Battle start(Battle battle) {
        log.debug("Starting battle : {}", battle.getId());
        if (battle.getStatus() != BattleStatus.CREATED) {
            throw new BattleException("Quiz já iniciado");
        }
        battle.setStatus(BattleStatus.STARTED);
        createRound(battle);
        var updated = repository.save(battle);
        log.debug("Publishing new battle started event for: {}", battle.getId());
        publisher.publishEvent(new BattleStatusEvent(updated, BattleStatus.CREATED, BattleStatus.STARTED));
        log.info("Battle: {} was started!", battle.getId());
        return updated;
    }

    public Battle end(Battle battle) {
        log.debug("Finishing battle : {}", battle.getId());
        if (battle.getStatus() != BattleStatus.STARTED) {
            throw new BattleException("Quiz já encerrado");
        }
        battle.setStatus(BattleStatus.FINISHED);
        var updated = repository.save(battle);
        log.debug("Publishing new battle finished event for: {}", battle.getId());
        publisher.publishEvent(new BattleStatusEvent(updated, BattleStatus.STARTED, BattleStatus.FINISHED));
        log.info("Battle: {} was finished!", battle.getId());
        return updated;
    }

    public Page<Battle> list(Pageable pageable) {
        return repository.findByPlayer(session.getPlayer(), pageable);
    }

    public Optional<Battle> findById(Long id) {
        return repository.findByPlayerAndId(session.getPlayer(), id);
    }

    public Page<Battle> findByPlayer(Player player, Pageable pageable) {
        return repository.findByPlayer(player, pageable);
    }

    public Round createRound(Battle battle) {
        log.debug("Creating new round for battle : {}", battle.getId());
        if (battle.getStatus() != BattleStatus.STARTED) {
            throw new BattleException("Quiz não iniciado.");
        }
        return roundService.findRoundOpened(battle)
                .orElseGet(() -> {
                    log.debug("None opened round was found. Creating new");
                    return roundService.createRound(battle);
                });
    }

    public Optional<Round> listRound(Long battleId, Long roundId) {
        return repository.findByPlayerAndId(session.getPlayer(), battleId)
                .flatMap(it -> roundService.findByBattleAndId(it, roundId));
    }

    public Page<Round> listRounds(Long battleId, Pageable pageable) {
        return repository.findByPlayerAndId(session.getPlayer(), battleId)
                .map(it -> roundService.findByBattle(it, pageable))
                .orElse(Page.empty());
    }

    public Answer answer(Battle battle, Round round, Movie chosen) {
        log.debug("Answering round: {} of battle : {}", round.getId(), battle.getId());
        if (battle.getStatus() != BattleStatus.STARTED) {
            throw new BattleException("Quiz deve estar iniciado para que seja possível responder");
        }
        var answer = roundService.answer(round, chosen);
        if (gameOverStrategy.isGameOver(battle)) {
            log.info("Game over!!! Finishing battle");
            end(battle);
        } else {
            log.debug("Continue in game!!! Creating new round");
            var newRound = createRound(battle);
            answer.setNextRound(newRound);
        }
        log.info("Answered round: {} with chosen: {}", round.getId(), chosen);
        return answer;
    }

}
