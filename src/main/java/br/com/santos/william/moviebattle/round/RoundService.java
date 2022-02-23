package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.movie.MovieService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoundService {

    private final RoundRepository repository;
    private final MovieService movieService;
    private final ApplicationEventPublisher publisher;

    public RoundService(
            RoundRepository repository,
            MovieService movieService,
            ApplicationEventPublisher publisher
    ) {
        this.repository = repository;
        this.movieService = movieService;
        this.publisher = publisher;
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
        var used = usedMovies(battle);
        var movies = movieService.list();
        var pair = createNewPair(movies, used);

        var round = new Round();
        round.setBattle(battle);
        round.setFirst(pair.getFirst());
        round.setSecond(pair.getSecond());
        round.setStatus(RoundStatus.OPEN);
        round = repository.save(round);
        adjustNextRound(battle, round);
        battle.getRounds().add(round);
        publisher.publishEvent(new RoundStatusEvent(round, null, round.getStatus()));
        return round;
    }

    public Optional<Round> answerRound(Battle battle, Long roundId, Movie choose) {
        return repository.findByBattleAndId(battle, roundId)
                .map(it -> {
                    if (it.getStatus() != RoundStatus.OPEN) {
                        throw new BattleException("Round deve estar aberto para que seja respondido");
                    }
                    var firstScore = it.getFirst().calculateScore();
                    var secondScore = it.getSecond().calculateScore();
                    var movie = firstScore >= secondScore ? it.getFirst() : it.getSecond();
                    if (movie.getId().equals(choose.getId())) {
                        it.setStatus(RoundStatus.HIT);
                    } else {
                        it.setStatus(RoundStatus.MISS);
                    }
                    it.setChoose(choose);
                    var updated = repository.save(it);
                    publisher.publishEvent(new RoundStatusEvent(updated, RoundStatus.OPEN, updated.getStatus()));
                    return updated;
                });
    }

    private void adjustNextRound(Battle battle, Round round) {
        battle.getRounds().stream()
                .filter(it -> it.getId() != null && !Objects.equals(it.getId(), round.getId()) && it.getNextRound() == null)
                .findFirst()
                .ifPresent(it -> {
                    it.setNextRound(round);
                    repository.save(it);
                });
    }

    private Pair<Movie, Movie> createNewPair(List<Movie> movies, Set<String> usedKeys) {
        var count = movies.size();
        var random = new Random();
        String key;
        Movie first, second;
        do {
            first = movies.get(random.nextInt(count));
            second = movies.get(random.nextInt(count));
        } while (first == second || usedKeys.contains(createKey(first, second)));
        return Pair.of(first, second);
    }

    private Set<String> usedMovies(Battle battle) {
        return battle.getRounds().stream()
                .map(it -> {
                    var first = it.getFirst();
                    var second = it.getSecond();
                    return createKey(first, second);
                })
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private String createKey(Movie first, Movie second) {
        var compare = first.getId().compareTo(second.getId());
        String key;
        if (compare < 0) {
            key = first.getId().toString().concat("_").concat(second.getId().toString());
        } else {
            key = second.getId().toString().concat("_").concat(first.getId().toString());
        }
        return key;
    }

}
