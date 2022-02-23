package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.movie.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class RoundServiceUnitTest {

    private final Long battleId = 9l;
    private final Long roundId = 10l;
    private final List<Movie> movies = IntStream.range(0, 51)
            .mapToObj(it -> {
                var movie = new Movie();
                movie.setId(Long.valueOf(it));
                movie.setName("unit-test" + it);
                movie.setRank((float) (it * 100 / (it + 1)));
                movie.setVotes(it * it);
                movie.setGenre("unit-test" + it);
                return movie;
            })
            .collect(Collectors.toList());

    @Mock
    private RoundRepository repository;

    @Mock
    private MovieService movieService;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private RoundService service;

    @BeforeEach
    public void setup() {
        given(movieService.list()).willReturn(movies);

        given(repository.save(any())).willAnswer((Answer<Round>) invocationOnMock -> {
            Round round = invocationOnMock.getArgument(0);
            round.setId(roundId);
            return round;
        });
    }

    @Test
    public void findRoundOpenedShouldForwardParameters() {
        var battle = new Battle();

        service.findRoundOpened(battle);

        verify(repository).findByBattleAndStatus(battle, RoundStatus.OPEN);
    }

    @Test
    public void findByBattleAndIdShouldForwardParameters() {
        var battle = new Battle();

        service.findByBattleAndId(battle, 1l);

        verify(repository).findByBattleAndId(battle, 1l);
    }

    @Test
    public void findByBattlePageableShouldForwardParameters() {
        var battle = new Battle();

        service.findByBattle(battle, Pageable.unpaged());

        verify(repository).findByBattle(battle, Pageable.unpaged());
    }

    @Test
    public void createRoundShouldReturnsNewPairForBattleWithoutRound() {
        var battle = new Battle();
        battle.setRounds(new ArrayList<>());

        var round = service.createRound(battle);

        assertNotNull(round);
    }

    @Test
    public void answerRoundShouldThrowExceptionWhenRoundStatusIsNotOpen() {
        var round = new Round();
        round.setStatus(RoundStatus.MISS);

        given(repository.findByBattleAndId(any(), eq(roundId))).willReturn(Optional.of(round));

        assertThrows(BattleException.class, () ->
                service.answerRound(new Battle(), roundId, new Movie()).get()
        );
    }

    @Test
    public void answerRoundShouldFlaggedHitWhenChooseIsGreaterScore() {
        var movieOne = new Movie();
        movieOne.setId(1l);
        movieOne.setVotes(10);
        movieOne.setRank(10.0f);

        var movieTwo = new Movie();
        movieTwo.setId(2l);
        movieTwo.setVotes(5);
        movieTwo.setRank(8.5f);

        var round = new Round();
        round.setFirst(movieOne);
        round.setSecond(movieTwo);
        round.setStatus(RoundStatus.OPEN);

        given(repository.findByBattleAndId(any(), eq(roundId))).willReturn(Optional.of(round));

        service.answerRound(new Battle(), roundId, movieOne);

        assertEquals(RoundStatus.HIT, round.getStatus());
        assertEquals(movieOne, round.getChoose());
    }

}
