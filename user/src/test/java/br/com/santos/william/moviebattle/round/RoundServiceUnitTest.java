package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.movie.MovieService;
import br.com.santos.william.moviebattle.round.answerstrategy.AnswerStrategy;
import br.com.santos.william.moviebattle.round.event.RoundEvent;
import br.com.santos.william.moviebattle.round.moviechoicestrategy.MovieChoiceStrategy;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(SpringExtension.class)
public class RoundServiceUnitTest {

    private final Long roundId = 10l;

    @Mock
    private RoundRepository repository;

    @Mock
    private MovieService movieService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private MovieChoiceStrategy movieChoiceStrategy;

    @Mock
    private AnswerStrategy answerStrategy;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private RoundService service;

    @BeforeEach
    public void setup() {
        given(movieService.list()).willReturn(Collections.emptyList());
        given(movieChoiceStrategy.choice(any(), any())).willReturn(Pair.of(new Movie(), new Movie()));
        given(answerStrategy.answer(any(), any())).willReturn(RoundStatus.HIT);

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
    public void createRoundShouldReturnsNew() {
        var battle = new Battle();
        battle.setRounds(new ArrayList<>());

        var round = service.createRound(battle);

        assertNotNull(round);
    }

    @Test
    public void createRoundShouldMovieChoiceStrategyToSelectNewPair() {
        var battle = new Battle();
        battle.setRounds(new ArrayList<>());

        var first = new Movie();
        var second = new Movie();
        given(movieChoiceStrategy.choice(battle, Collections.emptyList())).willReturn(Pair.of(first, second));

        var round = service.createRound(battle);

        verify(movieChoiceStrategy).choice(battle, Collections.emptyList());
        assertNotNull(round);
        assertEquals(first, round.getFirst());
        assertEquals(second, round.getSecond());
    }

    @Test
    public void createRoundShouldSaveIt() {
        var round = service.createRound(new Battle());

        verify(repository).save(round);
    }

    @Test
    public void createRoundShouldPublishEvent() {
        service.createRound(new Battle());

        verify(publisher).publishEvent(any());
    }

    @Test
    public void createRoundShouldPublishEventWithRoundInSource() {
        var round = service.createRound(new Battle());

        var captor = ArgumentCaptor.forClass(RoundEvent.class);
        verify(publisher).publishEvent(captor.capture());

        var event = captor.getValue();
        assertEquals(round, event.getSource());
    }

    @Test
    public void createRoundShouldPublishEventWithStatusIsOpen() {
        service.createRound(new Battle());

        var captor = ArgumentCaptor.forClass(RoundEvent.class);
        verify(publisher).publishEvent(captor.capture());

        var event = captor.getValue();
        assertEquals(RoundStatus.OPEN, event.getNewStatus());
    }

    @Test
    public void answerRoundShouldThrowExceptionWhenRoundStatusIsNotOpen() {
        var round = new Round();
        round.setStatus(RoundStatus.MISS);

        given(repository.findByBattleAndId(any(), eq(roundId))).willReturn(Optional.of(round));

        assertThrows(BattleException.class, () ->
                service.answer(round, new Movie())
        );
    }

    @Test
    public void answerShouldNotAcceptWhenRoundStatusIsNotOpen() {
        var round = new Round();
        round.setStatus(RoundStatus.HIT);

        assertThrows(BattleException.class, () -> service.answer(round, new Movie()));

        verifyNoInteractions(answerStrategy, repository, publisher);
    }

    @Test
    public void answerShouldDelegateToStrategy() {
        var round = new Round();
        round.setStatus(RoundStatus.OPEN);

        var chosen = new Movie();

        service.answer(round, chosen);

        verify(answerStrategy).answer(round, chosen);
    }

    @Test
    public void answerShouldChangeStatusToReturnedFromStrategy() {
        var round = new Round();
        round.setStatus(RoundStatus.OPEN);

        var chosen = new Movie();

        given(answerStrategy.answer(round, chosen)).willReturn(RoundStatus.HIT);

        service.answer(round, chosen);

        assertEquals(RoundStatus.HIT, round.getStatus());
    }

    @Test
    public void answerShouldUpdate() {
        var round = new Round();

        var chosen = new Movie();

        given(answerStrategy.answer(round, chosen)).willReturn(RoundStatus.MISS);
        round.setStatus(RoundStatus.OPEN);

        service.answer(round, chosen);

        verify(repository).save(round);
    }

    @Test
    public void answerShouldPublishEventWithNewStatus() {
        var round = new Round();
        round.setStatus(RoundStatus.OPEN);

        var chosen = new Movie();

        given(answerStrategy.answer(round, chosen)).willReturn(RoundStatus.HIT);

        service.answer(round, chosen);

        var captor = ArgumentCaptor.forClass(RoundEvent.class);
        verify(publisher).publishEvent(captor.capture());

        var event = captor.getValue();
        assertEquals(RoundStatus.HIT, event.getNewStatus());
    }

    @Test
    public void answerPublishEventWithRoundInSource() {
        var round = new Round();
        round.setStatus(RoundStatus.OPEN);

        var chosen = new Movie();

        given(answerStrategy.answer(round, chosen)).willReturn(RoundStatus.HIT);

        service.answer(round, chosen);

        var captor = ArgumentCaptor.forClass(RoundEvent.class);
        verify(publisher).publishEvent(captor.capture());

        var event = captor.getValue();
        assertEquals(round, event.getSource());
    }

    @Test
    public void answerReturnsWithStatus() {
        var round = new Round();
        round.setStatus(RoundStatus.OPEN);

        var chosen = new Movie();

        given(answerStrategy.answer(round, chosen)).willReturn(RoundStatus.MISS);

        var answer = service.answer(round, chosen);

        assertEquals(RoundStatus.MISS, answer.getStatus());
    }

}
