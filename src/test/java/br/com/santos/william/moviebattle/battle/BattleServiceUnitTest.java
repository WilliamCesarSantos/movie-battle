package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.battle.event.BattleStatusEvent;
import br.com.santos.william.moviebattle.battle.exception.BattleException;
import br.com.santos.william.moviebattle.battle.gameover.strategy.GameOverStrategy;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.Session;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BattleServiceUnitTest {

    private Player player;
    private Battle battle;
    private Long battleId = 10l;

    @Spy
    private Session session;

    @Mock
    private BattleRepository repository;

    @Mock
    private RoundService roundService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private GameOverStrategy gameOverStrategy;

    @InjectMocks
    private BattleService service;

    @BeforeEach
    public void setup() {
        player = new Player();
        player.setId(battleId);
        player.setName("unit-test");

        battle = new Battle();
        battle.setStatus(BattleStatus.CREATED);
        battle.setCreatedAt(LocalDateTime.now());
        battle.setPlayer(player);
        battle.setDescription("unit-test");
        battle.setRounds(new ArrayList<>());

        given(repository.save(battle)).will((Answer<Battle>) invocationOnMock -> {
            Battle battle = invocationOnMock.getArgument(0);
            battle.setId(battleId);
            return battle;
        });

        session.setPlayer(player);
    }

    @Test
    public void createShouldSetStatusAndPlayerAndCreatedAt() {
        battle.setCreatedAt(null);
        battle.setStatus(null);
        battle.setPlayer(null);

        service.create(battle);

        assertNotNull(battle.getCreatedAt());
        assertEquals(BattleStatus.CREATED, battle.getStatus());
        assertEquals(session.getPlayer(), battle.getPlayer());
    }

    @Test
    public void createShouldCallsRepositoryToSave() {
        service.create(battle);

        verify(repository).save(battle);
        assertEquals(battleId, battle.getId());
    }

    @Test
    public void createShouldPublishNewEvent() {
        service.create(battle);

        var captor = ArgumentCaptor.forClass(BattleStatusEvent.class);
        verify(publisher).publishEvent(captor.capture());

        var event = captor.getValue();
        assertNotNull(event);
    }

    @Test
    public void createShouldPublishNewEventWithBattleInSouce() {
        service.create(battle);

        var captor = ArgumentCaptor.forClass(BattleStatusEvent.class);
        verify(publisher).publishEvent(captor.capture());

        var event = captor.getValue();
        assertEquals(battle, event.getSource());
    }

    @Test
    public void createShouldPublishNewEventWithNullOldStatus() {
        service.create(battle);

        var captor = ArgumentCaptor.forClass(BattleStatusEvent.class);
        verify(publisher).publishEvent(captor.capture());

        var event = captor.getValue();
        assertNull(event.getOldStatus());
    }

    @Test
    public void createShouldPublishNewEventWithCreateInNewStatus() {
        service.create(battle);

        var captor = ArgumentCaptor.forClass(BattleStatusEvent.class);
        verify(publisher).publishEvent(captor.capture());

        var event = captor.getValue();
        assertEquals(BattleStatus.CREATED, event.getNewStatus());
    }

    @Test
    public void startShouldThrowExceptionWhenBattleStatusIsNotCreated() {
        battle.setStatus(BattleStatus.STARTED);
        battle.setId(battleId);

        assertThrows(BattleException.class, () -> service.start(battle));
    }

    @Test
    public void startShouldCreateNewRoundWhenNotExistsAnyOpened() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));
        given(roundService.findRoundOpened(battle)).willReturn(Optional.empty());
        given(roundService.createRound(battle)).willReturn(new Round());

        service.start(battle);

        verify(roundService).createRound(battle);
    }

    @Test
    public void startShouldUseOpenedRound() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));
        given(roundService.findRoundOpened(battle)).willReturn(Optional.of(new Round()));

        service.start(battle);

        verify(roundService).findRoundOpened(battle);
        verify(roundService, never()).createRound(battle);
    }

    @Test
    public void startShouldChangeStatusToStarted() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));

        service.start(battle);

        verify(repository).save(battle);
        assertEquals(BattleStatus.STARTED, battle.getStatus());
    }

    @Test
    public void startShouldPublishNewEvent() {
        battle.setStatus(BattleStatus.CREATED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));

        service.start(battle);

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

        assertThrows(BattleException.class, () -> service.end(battle));
    }

    @Test
    public void endShouldChangeStatusToFinished() {
        battle.setStatus(BattleStatus.STARTED);

        service.end(battle);

        verify(repository).save(battle);
        assertEquals(BattleStatus.FINISHED, battle.getStatus());
    }

    @Test
    public void endShouldSaveBattle() {
        battle.setStatus(BattleStatus.STARTED);

        service.end(battle);

        verify(repository).save(battle);
    }

    @Test
    public void endShouldPublishNewEvent() {
        battle.setStatus(BattleStatus.STARTED);
        battle.setId(battleId);

        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));

        service.end(battle);

        var captor = ArgumentCaptor.forClass(BattleStatusEvent.class);
        verify(publisher).publishEvent(captor.capture());
        var event = captor.getValue();

        assertNotNull(event);
        assertEquals(battle, event.getSource());
        assertEquals(BattleStatus.STARTED, event.getOldStatus());
        assertEquals(BattleStatus.FINISHED, event.getNewStatus());
    }

    @Test
    public void listShouldForwardParametersToRepository() {
        service.list(Pageable.unpaged());

        verify(repository).findByPlayer(player, Pageable.unpaged());
    }

    @Test
    public void findByIdShouldForwardParametersToRepository() {
        service.findById(battleId);

        verify(repository).findByPlayerAndId(player, battleId);
    }

    @Test
    public void findByPlayerShouldParametersToRepository() {
        service.findByPlayer(player, Pageable.unpaged());

        verify(repository).findByPlayer(player, Pageable.unpaged());
    }

    @Test
    public void createRoundShouldFindOpenedRound() {
        battle.setStatus(BattleStatus.STARTED);

        given(roundService.findRoundOpened(battle)).willReturn(Optional.of(new Round()));

        service.createRound(battle);

        verify(roundService).findRoundOpened(battle);
        verify(roundService, never()).createRound(battle);
    }

    @Test
    public void createRoundShouldCreateNewWhenNotFoundAnyOpened() {
        battle.setStatus(BattleStatus.STARTED);

        given(roundService.findRoundOpened(battle)).willReturn(Optional.empty());
        given(roundService.createRound(battle)).willReturn(new Round());

        var round = service.createRound(battle);

        verify(roundService).findRoundOpened(battle);
        verify(roundService).createRound(battle);
        assertNotNull(round);
    }

    @Test
    public void createRoundShouldThrowExceptionWhenBattleStatusIsNotStarted() {
        battle.setStatus(BattleStatus.CREATED);

        assertThrows(BattleException.class, () -> service.createRound(battle));

        verifyNoInteractions(roundService);
    }

    @Test
    public void listRoundShouldReturnsEmptyOptionalWhenBattleNotFound() {
        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.empty());

        var result = service.listRound(battleId, 1l);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundShouldReturnsEmptyOptionalWhenRoundNotFound() {
        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));
        given(roundService.findByBattleAndId(battle, 1l)).willReturn(Optional.empty());

        var result = service.listRound(battleId, 1l);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundShouldForwardParametersToRoundService() {
        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));
        given(roundService.findByBattleAndId(battle, 1l)).willReturn(Optional.of(new Round()));

        service.listRound(battleId, 1l).get();

        verify(roundService).findByBattleAndId(battle, 1l);
    }

    @Test
    public void listRoundsShouldReturnsEmptyPageWhenBattleNotFound() {
        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.empty());

        var result = service.listRounds(battleId, Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundsShouldReturnsEmptyPageWhenAnyRoundNotFound() {
        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));
        given(roundService.findByBattle(battle, Pageable.unpaged())).willReturn(Page.empty());

        var result = service.listRounds(battleId, Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listRoundsShouldForwardParametersToRoundService() {
        given(repository.findByPlayerAndId(player, battleId)).willReturn(Optional.of(battle));
        given(roundService.findByBattle(battle, Pageable.unpaged())).willReturn(Page.empty());

        service.listRounds(battleId, Pageable.unpaged()).get();

        verify(roundService).findByBattle(battle, Pageable.unpaged());
    }

    @Test
    public void answerShouldThrowExceptionWhenBattleStatusIsNotStarted() {
        battle.setStatus(BattleStatus.CREATED);

        assertThrows(BattleException.class, () -> service.answer(battle, new Round(), new Movie()));

        verifyNoInteractions(roundService);
    }

    @Test
    public void answerShouldCreateNewIt() {
        var choose = new Movie();
        battle.setStatus(BattleStatus.STARTED);

        given(roundService.answer(any(), any())).willReturn(new br.com.santos.william.moviebattle.round.Answer());
        given(gameOverStrategy.isGameOver(any())).willReturn(false);

        var answer = service.answer(battle, new Round(), choose);

        verify(roundService).answer(any(), any());
        assertNotNull(answer);
    }

    @Test
    public void answerShouldCreateNewRoundWhenIsNotGameOver() {
        var choose = new Movie();
        battle.setStatus(BattleStatus.STARTED);

        given(roundService.createRound(any())).willReturn(new Round());
        given(roundService.answer(any(), any())).willReturn(new br.com.santos.william.moviebattle.round.Answer());
        given(gameOverStrategy.isGameOver(any())).willReturn(false);

        var answer = service.answer(battle, new Round(), choose);

        verify(roundService).createRound(battle);
        assertNotNull(answer.getNextRound());
    }

    @Test
    public void answerShouldFinishGameWhenGameOver() {
        var choose = new Movie();
        battle.setStatus(BattleStatus.STARTED);

        given(roundService.createRound(any())).willReturn(new Round());
        given(roundService.answer(any(), any())).willReturn(new br.com.santos.william.moviebattle.round.Answer());
        given(gameOverStrategy.isGameOver(any())).willReturn(true);

        service.answer(battle, new Round(), choose);

        assertEquals(BattleStatus.FINISHED, battle.getStatus());
    }

}
