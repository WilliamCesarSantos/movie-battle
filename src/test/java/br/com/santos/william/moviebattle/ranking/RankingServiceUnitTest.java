package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.BattleStatus;
import br.com.santos.william.moviebattle.battle.BattleStatusEvent;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.ranking.calculate.RankingCalculateStrategy;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class RankingServiceUnitTest {

    @Mock
    private RankingRepository repository;

    @Mock
    private RankingCalculateStrategy strategy;

    @InjectMocks
    private RankingService service;

    @Test
    public void listShouldForwardParameters() {
        var player = new Player();

        service.list(player);

        verify(repository).findByPlayer(player);
    }

    @Test
    public void calculateScoreShouldDiscardEventWhenStatusIsCreated() {
        BattleStatusEvent event = new BattleStatusEvent(new Battle(), null, BattleStatus.CREATED);
        service.calculateScore(event);

        verifyNoInteractions(repository);
    }

    @Test
    public void calculateScoreShouldDiscardEventWhenStatusIsStarted() {
        BattleStatusEvent event = new BattleStatusEvent(new Battle(), null, BattleStatus.STARTED);
        service.calculateScore(event);

        verifyNoInteractions(repository);
    }

    @Test
    public void calculateScoreShouldGenerateRankingWhenUserNoHasIt() {
        given(repository.findByPlayer(any())).willReturn(Optional.empty());

        var battle = new Battle();
        battle.setRounds(Collections.emptyList());
        BattleStatusEvent event = new BattleStatusEvent(battle, null, BattleStatus.FINISHED);
        service.calculateScore(event);

        verify(repository).save(any());
    }

    @Test
    public void calculateScoreShouldUpdateExistsRanking() {
        var ranking = new Ranking();
        ranking.setScore(10f);
        ranking.setPlayer(new Player());
        ranking.setId(1l);

        var round = new Round();
        round.setStatus(RoundStatus.HIT);

        var battle = new Battle();
        battle.setRounds(List.of(round));

        given(repository.findByPlayer(any())).willReturn(Optional.of(ranking));

        BattleStatusEvent event = new BattleStatusEvent(battle, BattleStatus.STARTED, BattleStatus.FINISHED);
        service.calculateScore(event);

        verify(repository).save(ranking);
    }

    @Test
    public void calculateScoreShouldCallsStrategyWhenBattleStatusIsFinished() {
        var battle = new Battle();

        given(repository.findByPlayer(any())).willReturn(Optional.empty());

        BattleStatusEvent event = new BattleStatusEvent(battle, BattleStatus.STARTED, BattleStatus.FINISHED);

        service.calculateScore(event);

        verify(strategy).calculate(eq(battle), any());
    }

    @Test
    public void calculateScoreShouldNotCallStrategyWhenBattleStatusIsStarted() {
        var battle = new Battle();

        given(repository.findByPlayer(any())).willReturn(Optional.empty());

        BattleStatusEvent event = new BattleStatusEvent(battle, BattleStatus.STARTED, BattleStatus.STARTED);

        service.calculateScore(event);

        verifyNoMoreInteractions(strategy);
    }

}
