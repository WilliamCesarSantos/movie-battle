package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundService;
import br.com.santos.william.moviebattle.round.RoundStatus;
import br.com.santos.william.moviebattle.round.RoundStatusEvent;
import br.com.santos.william.moviebattle.user.Session;
import br.com.santos.william.moviebattle.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BattleService implements ApplicationListener<RoundStatusEvent> {

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
            Integer limitLostRound
    ) {
        this.repository = repository;
        this.session = session;
        this.roundService = roundService;
        this.publisher = publisher;
        this.limitLostRound = limitLostRound;
    }

    public Battle insert(@Valid Battle battle) {
        battle.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        battle.setStatus(BattleStatus.CREATED);
        battle.setPlayer(session.getUser());
        var created = repository.save(battle);
        publisher.publishEvent(new BattleStatusEvent(created, null, created.getStatus()));
        return created;
    }

    public Optional<Battle> start(Long id) {
        return repository.findById(id).map(it -> {
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
        return repository.findById(id)
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
        return repository.findByPlayer(session.getUser(), pageable);
    }

    public Optional<Battle> findById(Long id) {
        return repository.findByPlayerAndId(session.getUser(), id);
    }

    public Page<Battle> findByPlayer(User player, Pageable pageable) {
        return repository.findByPlayer(player, pageable);
    }

    private Round createRound(Battle battle) {
        if (battle.getStatus() != BattleStatus.STARTED) {
            throw new BattleException("Quiz deve estar iniciado para que seja possível criar um round");
        }
        return roundService.findRoundOpened(battle)
                .orElseGet(() -> roundService.createRound(battle));
    }

    public Optional<Round> createRound(Long battleId) {
        return repository.findByPlayerAndId(session.getUser(), battleId)
                .map(this::createRound);
    }

    public Optional<Round> listRound(Long battleId, Long roundId) {
        return repository.findByPlayerAndId(session.getUser(), battleId)
                .flatMap(it -> roundService.findByBattleAndId(it, roundId));
    }

    public Page<Round> listRounds(Long battleId, Pageable pageable) {
        return repository.findByPlayerAndId(session.getUser(), battleId)
                .map(it -> roundService.findByBattle(it, pageable))
                .orElse(Page.empty());
    }

    public Optional<Round> answer(Long battleId, Long roundId, Movie choose) {
        return repository.findByPlayerAndId(session.getUser(), battleId)
                .flatMap(it -> {
                    if (it.getStatus() != BattleStatus.STARTED) {
                        throw new BattleException("Quiz deve estar iniciado para que seja possível responder");
                    }
                    return roundService.answerRound(it, roundId, choose);
                });
    }

    @Override
    public void onApplicationEvent(RoundStatusEvent event) {
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
