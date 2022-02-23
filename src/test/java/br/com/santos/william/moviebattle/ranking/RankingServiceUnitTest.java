package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.BattleStatus;
import br.com.santos.william.moviebattle.battle.BattleStatusEvent;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import br.com.santos.william.moviebattle.user.Session;
import br.com.santos.william.moviebattle.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class RankingServiceUnitTest {

    private User user = new User();
    private Session session = new Session();
    private RankingRepository repository = Mockito.mock(RankingRepository.class);
    private RankingService service = new RankingService(session, repository);

    @BeforeEach
    public void setUp() {
        user.setId(1);
        session.setUser(user);
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
        given(repository.findByUser(any())).willReturn(Optional.empty());

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
        ranking.setUser(new User());
        ranking.setId(1l);

        var round = new Round();
        round.setStatus(RoundStatus.HIT);

        var battle = new Battle();
        battle.setRounds(List.of(round));

        given(repository.findByUser(any())).willReturn(Optional.of(ranking));

        BattleStatusEvent event = new BattleStatusEvent(battle, BattleStatus.STARTED, BattleStatus.FINISHED);
        service.calculateScore(event);

        verify(repository).save(ranking);
    }

    @Test
    public void calculateScoreShouldIncrementScore() {
        var ranking = new Ranking();
        ranking.setUser(new User());
        ranking.setId(1l);

        var round = new Round();
        round.setStatus(RoundStatus.HIT);

        var battle = new Battle();
        battle.setRounds(List.of(round));

        given(repository.findByUser(any())).willReturn(Optional.of(ranking));

        BattleStatusEvent event = new BattleStatusEvent(battle, BattleStatus.STARTED, BattleStatus.FINISHED);
        service.calculateScore(event);

        assertEquals(100f, ranking.getScore());
    }

    @Test
    public void calculateScoreShouldNotIncrementScoreWhenBattleHasOnlyMissRound() {
        var ranking = new Ranking();
        ranking.setUser(new User());
        ranking.setId(1l);
        ranking.setScore(10f);

        var round = new Round();
        round.setStatus(RoundStatus.MISS);

        var battle = new Battle();
        battle.setRounds(List.of(round));

        given(repository.findByUser(any())).willReturn(Optional.of(ranking));

        BattleStatusEvent event = new BattleStatusEvent(battle, BattleStatus.STARTED, BattleStatus.FINISHED);
        service.calculateScore(event);

        assertEquals(10f, ranking.getScore());
    }

}
