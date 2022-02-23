package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundService;
import br.com.santos.william.moviebattle.round.RoundStatus;
import br.com.santos.william.moviebattle.round.RoundStatusEvent;
import br.com.santos.william.moviebattle.user.Session;
import br.com.santos.william.moviebattle.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class BattleServiceUnitTest {

    private Battle battle;
    private Long battleId = 10l;

    private final User user = new User();
    private final Session session = new Session();
    private final Integer limitLostRound = 1;

    private final BattleRepository repository = Mockito.mock(BattleRepository.class);
    private final RoundService roundService = Mockito.mock(RoundService.class);
    private final ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);

    private BattleService service = new BattleService(repository, session, roundService, publisher, limitLostRound);

    @BeforeEach
    public void setup() {
        user.setId(battleId);
        user.setName("unit-test");

        session.setUser(user);

        battle = new Battle();
        battle.setStatus(BattleStatus.CREATED);
        battle.setCreatedAt(LocalDateTime.now());
        battle.setPlayer(user);
        battle.setDescription("unit-test");
        battle.setRounds(new ArrayList<>());

        given(repository.save(battle)).will((Answer<Battle>) invocationOnMock -> {
            Battle battle = invocationOnMock.getArgument(0);
            battle.setId(battleId);
            return battle;
        });
    }

    @Test
    public void insertShouldSetStatusAndPlayerBeforeInsert() {
        battle.setCreatedAt(null);
        battle.setStatus(null);
        battle.setPlayer(null);

        service.insert(battle);

        assertNotNull(battle.getCreatedAt());
        assertEquals(BattleStatus.CREATED, battle.getStatus());
        assertEquals(session.getUser(), battle.getPlayer());
    }

    @Test
    public void insertShouldCallsRepository() {
        service.insert(battle);

        verify(repository).save(battle);
        assertEquals(battleId, battle.getId());
    }

    @Test
    public void insertShouldPublishNewEvent() {
        service.insert(battle);

        var captor = ArgumentCaptor.forClass(BattleStatusEvent.class);
        verify(publisher).publishEvent(captor.capture());
        var event = captor.getValue();

        assertNotNull(event);
        assertEquals(battle, event.getSource());
        assertNull(event.getOldStatus());
        assertEquals(BattleStatus.CREATED, event.getNewStatus());
    }

    @Test
    public void startShouldThrowExceptionWhenBattleStatusIsNotCreated() {
        battle.setStatus(BattleStatus.STARTED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));

        assertThrows(BattleException.class, () -> service.start(battleId).get());
    }

    @Test
    public void startShouldReturnEmptyOptionalWhenBattleNotFound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.empty());

        var battle = service.start(battleId);
        assertNotNull(battle);
        assertTrue(battle.isEmpty());
    }

    @Test
    public void startShouldCreateNewRoundWhenNotExistsAnyOpened() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));
        given(roundService.findRoundOpened(battle)).willReturn(Optional.empty());
        given(roundService.createRound(battle)).willReturn(new Round());

        service.start(battleId).get();

        verify(roundService).createRound(battle);
    }

    @Test
    public void startShouldUseOpenedRound() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));
        given(roundService.findRoundOpened(battle)).willReturn(Optional.of(new Round()));

        service.start(battleId).get();

        verify(roundService).findRoundOpened(battle);
        verify(roundService, never()).createRound(battle);
    }

    @Test
    public void startShouldChangeStatusToStarted() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));

        service.start(battleId).get();

        verify(repository).save(battle);
        assertEquals(BattleStatus.STARTED, battle.getStatus());
    }

    @Test
    public void startShouldPublishNewEvent() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));

        service.start(battleId);

        var captor = ArgumentCaptor.forClass(BattleStatusEvent.class);
        verify(publisher).publishEvent(captor.capture());
        var event = captor.getValue();

        assertNotNull(event);
        assertEquals(battle, event.getSource());
        assertEquals(BattleStatus.CREATED, event.getOldStatus());
        assertEquals(BattleStatus.STARTED, event.getNewStatus());
    }

    @Test
    public void endShouldThrowExceptionWhenBattleStatusIsNotStarted() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));

        assertThrows(BattleException.class, () -> service.end(battleId).get());
    }

    @Test
    public void endShouldChangeStatusToFinished() {
        battle.setStatus(BattleStatus.STARTED);

        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));

        service.end(battleId).get();

        verify(repository).save(battle);
        assertEquals(BattleStatus.FINISHED, battle.getStatus());
    }

    @Test
    public void endShouldPublishNewEvent() {
        battle.setStatus(BattleStatus.STARTED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));

        service.end(battleId).get();

        var captor = ArgumentCaptor.forClass(BattleStatusEvent.class);
        verify(publisher).publishEvent(captor.capture());
        var event = captor.getValue();

        assertNotNull(event);
        assertEquals(battle, event.getSource());
        assertEquals(BattleStatus.STARTED, event.getOldStatus());
        assertEquals(BattleStatus.FINISHED, event.getNewStatus());
    }

    @Test
    public void endShouldReturnEmptyOptionalWhenBattleNotFound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.empty());

        var result = service.end(battleId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listShouldForwardParametersToRepository() {
        service.list(Pageable.unpaged());

        verify(repository).findByPlayer(user, Pageable.unpaged());
    }

    @Test
    public void findByIdShouldForwardParametersToRepository() {
        service.findById(battleId);

        verify(repository).findByPlayerAndId(user, battleId);
    }

    @Test
    public void findByPlayerShouldParametersToRepository() {
        service.findByPlayer(user, Pageable.unpaged());

        verify(repository).findByPlayer(user, Pageable.unpaged());
    }

    @Test
    public void createRoundShouldFindOpenedRound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));
        given(roundService.findRoundOpened(battle)).willReturn(Optional.of(new Round()));

        service.createRound(battleId).get();

        verify(roundService).findRoundOpened(battle);
        verify(roundService, never()).createRound(battle);
    }

    @Test
    public void createRoundShouldCreateNewWhenNotFoundAnyOpened() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));
        given(roundService.findRoundOpened(battle)).willReturn(Optional.empty());
        given(roundService.createRound(battle)).willReturn(new Round());

        service.createRound(battleId).get();

        verify(roundService).findRoundOpened(battle);
        verify(roundService).createRound(battle);
    }

    @Test
    public void createRoundShouldReturnsEmptyOptionalWhenBattleNotFound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.empty());

        var result = service.createRound(battleId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundShouldReturnsEmptyOptionalWhenBattleNotFound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.empty());

        var result = service.listRound(battleId, 1l);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundShouldReturnsEmptyOptionalWhenRoundNotFound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));
        given(roundService.findByBattleAndId(battle, 1l)).willReturn(Optional.empty());

        var result = service.listRound(battleId, 1l);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundShouldForwardParametersToRoundService() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));
        given(roundService.findByBattleAndId(battle, 1l)).willReturn(Optional.of(new Round()));

        service.listRound(battleId, 1l).get();

        verify(roundService).findByBattleAndId(battle, 1l);
    }

    @Test
    public void listRoundsShouldReturnsEmptyPageWhenBattleNotFound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.empty());

        var result = service.listRounds(battleId, Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundsShouldReturnsEmptyPageWhenAnyRoundNotFound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));
        given(roundService.findByBattle(battle, Pageable.unpaged())).willReturn(Page.empty());

        var result = service.listRounds(battleId, Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundsShouldForwardParametersToRoundService() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));
        given(roundService.findByBattle(battle, Pageable.unpaged())).willReturn(Page.empty());

        service.listRounds(battleId, Pageable.unpaged()).get();

        verify(roundService).findByBattle(battle, Pageable.unpaged());
    }

    @Test
    public void answerShouldReturnsEmptyOptionalWhenBattleNotFound() {
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.empty());

        var result = service.answer(battleId, 1l, new Movie());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void answerShouldThrowExceptionWhenBattleStatusIsNotStarted() {
        battle.setStatus(BattleStatus.CREATED);
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));

        assertThrows(BattleException.class, () -> service.answer(battleId, 1l, new Movie()));

        verifyNoInteractions(roundService);
    }

    @Test
    public void answerShouldForwardParametersToRoundService() {
        var choose = new Movie();
        battle.setStatus(BattleStatus.STARTED);
        given(repository.findByPlayerAndId(user, battleId)).willReturn(Optional.of(battle));

        service.answer(battleId, 1l, choose);

        verify(roundService).answerRound(battle, 1l, choose);
    }

    @Test
    public void calculateLostRoundsShouldIgnoreMessageWhenStatusIsHit() {
        battle.setStatus(BattleStatus.STARTED);
        var event = new RoundStatusEvent(new Round(), null, RoundStatus.HIT);

        service.calculateLostRounds(event);

        assertEquals(BattleStatus.STARTED, battle.getStatus());
    }

    @Test
    public void calculateLostRoundsShouldNotCloseBattleWhenMissesIsLessLimit() {
        battle.setStatus(BattleStatus.STARTED);

        var round = new Round();
        round.setStatus(RoundStatus.MISS);
        round.setBattle(battle);

        battle.getRounds().add(round);

        var event = new RoundStatusEvent(round, RoundStatus.OPEN, RoundStatus.MISS);

        service.calculateLostRounds(event);

        assertEquals(BattleStatus.STARTED, battle.getStatus());
    }

    @Test
    public void calculateLostRoundsShouldCloseBattleWhenMissesIsGreaterLimit() {
        battle.setStatus(BattleStatus.STARTED);

        var roundOne = new Round();
        roundOne.setStatus(RoundStatus.MISS);
        roundOne.setBattle(battle);

        var roundTwo = new Round();
        roundTwo.setStatus(RoundStatus.MISS);
        roundTwo.setBattle(battle);

        battle.getRounds().add(roundOne);
        battle.getRounds().add(roundTwo);

        var event = new RoundStatusEvent(roundTwo, RoundStatus.OPEN, RoundStatus.MISS);

        service.calculateLostRounds(event);

        assertEquals(BattleStatus.FINISHED, battle.getStatus());
        verify(repository).save(battle);
    }

}
