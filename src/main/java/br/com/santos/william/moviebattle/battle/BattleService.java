package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.Session;
import br.com.santos.william.moviebattle.round.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BattleService {

    private final BattleRepository repository;
    private final Session session;
    private final RoundService roundService;
    private final ApplicationEventPublisher publisher;
    private final Integer limitLostRound;

    @Autowired
    public BattleService(
            BattleRepository repository,
            Session session,
            RoundService roundService,
            ApplicationEventPublisher publisher,
            @Value("${battle.round.lost.limit}") Integer limitLostRound
    ) {
        this.repository = repository;
        this.session = session;
        this.roundService = roundService;
        this.publisher = publisher;
        this.limitLostRound = limitLostRound;
    }

    public Battle insert(Battle battle) {
        battle.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        battle.setStatus(BattleStatus.CREATED);
        battle.setPlayer(session.getPlayer());
        var created = repository.save(battle);
        publisher.publishEvent(new BattleStatusEvent(created, null, created.getStatus()));
        return created;
    }

    public Optional<Battle> start(Long id) {
        return repository.findByPlayerAndId(session.getPlayer(), id)
                .map(it -> {
                    if (it.getStatus() == BattleStatus.CREATED) {
                        it.setStatus(BattleStatus.STARTED);
                        createRound(it);
                        var updated = repository.save(it);
                        publisher.publishEvent(new BattleStatusEvent(updated, BattleStatus.CREATED, BattleStatus.STARTED));
                        return updated;
                    } else {
                        throw new BattleException("Quiz já iniciado");
                    }
                });
    }

    public Optional<Battle> end(Long id) {
        return repository.findByPlayerAndId(session.getPlayer(), id)
                .map(this::end);
    }

    private Battle end(Battle it) {
        if (it.getStatus() == BattleStatus.STARTED) {
            it.setStatus(BattleStatus.FINISHED);
            var updated = repository.save(it);
            publisher.publishEvent(new BattleStatusEvent(updated, BattleStatus.STARTED, BattleStatus.FINISHED));
            return updated;
        } else {
            throw new BattleException("Quiz já encerrado");
        }
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

    private Round createRound(Battle battle) {
        if (battle.getStatus() != BattleStatus.STARTED) {
            throw new BattleException("Quiz não iniciado.");
        }
        return roundService.findRoundOpened(battle)
                .orElseGet(() -> roundService.createRound(battle));
    }

    public Optional<Round> createRound(Long battleId) {
        return repository.findByPlayerAndId(session.getPlayer(), battleId)
                .map(this::createRound);
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

    public Optional<Answer> answer(Long battleId, Long roundId, Movie choose) {
        return repository.findByPlayerAndId(session.getPlayer(), battleId)
                .flatMap(it -> {
                    if (it.getStatus() != BattleStatus.STARTED) {
                        throw new BattleException("Quiz deve estar iniciado para que seja possível responder");
                    }
                    return roundService.answerRound(it, roundId, choose);
                })
                .map(it -> {
                    var newRound = createRound(it.getBattle());
                    return new Answer(it.getChoose(), it.getStatus(), newRound);
                });
    }

    @EventListener
    public void calculateLostRounds(RoundStatusEvent event) {
        if (event.getNewStatus() == RoundStatus.MISS) {
            var round = (Round) event.getSource();
            var loss = countLostRound(round.getBattle());
            if (loss > limitLostRound) {
                end(round.getBattle());
            }
        }
    }

    private Long countLostRound(Battle battle) {
        return battle.getRounds().stream()
                .filter(it -> it.getStatus() == RoundStatus.MISS)
                .collect(Collectors.counting());
    }
}
