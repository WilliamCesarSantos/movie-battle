package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.battle.BattleRepository;
import br.com.santos.william.moviebattle.battle.BattleStatus;
import br.com.santos.william.moviebattle.battle.BattleStatusEvent;
import br.com.santos.william.moviebattle.user.Session;
import br.com.santos.william.moviebattle.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

public class RankingServiceUnitTest {

    private User user = new User();
    private Session session = new Session();
    private RankingRepository repository = Mockito.mock(RankingRepository.class);
    private BattleRepository battleRepository = Mockito.mock(BattleRepository.class);
    private RankingService service = new RankingService(session, repository, battleRepository);

    @BeforeEach
    public void setUp() {
        user.setId(1);
        session.setUser(user);
    }

    @Test
    public void calculateScoreShouldDiscardEventWhenStatusIsCreated() {
        BattleStatusEvent event = new BattleStatusEvent(new Battle(), null, BattleStatus.CREATED);
        service.calculateScore(event);

        verifyNoInteractions(repository, battleRepository);
    }

    @Test
    public void calculateScoreShouldDiscardEventWhenStatusIsStarted() {
        BattleStatusEvent event = new BattleStatusEvent(new Battle(), null, BattleStatus.STARTED);
        service.calculateScore(event);

        verifyNoInteractions(repository, battleRepository);
    }

    @Test
    public void calculateScoreShouldGenerateRankingWhenUserNoHasIt() {
        given(repository.findByUser(any())).willReturn(Optional.empty());


        BattleStatusEvent event = new BattleStatusEvent(new Battle(), null, BattleStatus.FINISHED);
    }
}
