package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.movie.MovieService;
import br.com.santos.william.moviebattle.round.answerstrategy.AnswerStrategy;
import br.com.santos.william.moviebattle.round.event.RoundEvent;
import br.com.santos.william.moviebattle.round.moviechoicestrategy.MovieChoiceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoundService {

    private final RoundRepository repository;
    private final MovieService movieService;
    private final ApplicationEventPublisher publisher;
    private final MovieChoiceStrategy movieChoiceStrategy;
    private final AnswerStrategy answerStrategy;
    private Logger log = LoggerFactory.getLogger(getClass());

    public RoundService(
            RoundRepository repository,
            MovieService movieService,
            ApplicationEventPublisher publisher,
            MovieChoiceStrategy movieChoiceStrategy,
            AnswerStrategy answerStrategy
    ) {
        this.repository = repository;
        this.movieService = movieService;
        this.publisher = publisher;
        this.movieChoiceStrategy = movieChoiceStrategy;
        this.answerStrategy = answerStrategy;
    }

    public Optional<Round> findRoundOpened(Battle battle) {
        return repository.findByBattleAndStatus(battle, RoundStatus.OPEN);
    }

    public Optional<Round> findByBattleAndId(Battle battle, Long id) {
        return repository.findByBattleAndId(battle, id);
    }

    public Page<Round> findByBattle(Battle battle, Pageable pageable) {
        return repository.findByBattle(battle, pageable);
    }

    public Round createRound(Battle battle) {
        log.debug("Creating new round for battle: {}", battle.getId());
        var movies = movieService.list();
        log.trace("Found: {} movies to new round", movies.size());
        var pair = movieChoiceStrategy.choice(battle, movies);

        var round = new Round();
        round.setBattle(battle);
        round.setFirst(pair.getFirst());
        round.setSecond(pair.getSecond());
        round.setStatus(RoundStatus.OPEN);
        round = repository.save(round);
        log.debug("Publishing new event for round: {}", round.getId());
        publisher.publishEvent(new RoundEvent(round, null, RoundStatus.OPEN));
        log.info("Round: {} was created!", round.getId());
        return round;
    }

    public Answer answer(Round round, Movie chosen) {
        if (round.getStatus() != RoundStatus.OPEN) {
            throw new BattleException("Round deve estar aberto para que seja respondido");
        }

        log.debug("Answering round: {}", round.getId());
        var status = answerStrategy.answer(round, chosen);
        round.setStatus(status);

        movieService.findById(chosen.getId())
                .ifPresent(round::setChoice);
        var updated = repository.save(round);

        log.debug("Publishing new event for round: {}", round.getId());
        publisher.publishEvent(new RoundEvent(updated, RoundStatus.OPEN, status));
        log.info("Round: {} was answered!", round.getId());
        return new Answer(round.getChoice(), status, null);
    }

}
