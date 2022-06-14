package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.PlayerRepository;
import br.com.santos.william.moviebattle.player.dto.PlayerDto;
import br.com.santos.william.moviebattle.ranking.calculate.RankingCalculateStrategy;
import br.com.santos.william.moviebattle.ranking.dto.BattleMovieFinished;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private RankingService service;

    @BeforeEach
    public void setup() {
        var player = mock(Player.class);
        doReturn(Optional.of(player)).when(playerRepository).findById(any());
    }

    @Test
    public void listShouldForwardParameters() {
        var player = new Player();

        service.list(player);

        verify(repository).findByPlayer(player);
    }

    @Test
    public void calculateScoreShouldGenerateRankingWhenUserNoHasIt() {
        var player = new PlayerDto(10l , "unit-test", "unit-test");

        var finished = new BattleMovieFinished();
        finished.setPlayerDto(player);
        finished.setTotalRounds(0);

        given(repository.findByPlayer(any())).willReturn(Optional.empty());

        service.calculateScore(finished);

        verify(repository).save(any());
    }

    @Test
    public void calculateScoreShouldUpdateExistsRanking() {
        var player = new PlayerDto(10l , "unit-test", "unit-test");

        var ranking = new Ranking();
        ranking.setScore(10f);
        ranking.setPlayer(new Player());
        ranking.setId(1l);

        var finished = new BattleMovieFinished();
        finished.setPlayerDto(player);
        finished.setTotalRounds(1);
        finished.setMiss(0);
        finished.setHits(1);

        given(repository.findByPlayer(any())).willReturn(Optional.of(ranking));

        service.calculateScore(finished);

        verify(repository).save(ranking);
    }

    @Test
    public void calculateScoreShouldCallsStrategyWhenBattleStatusIsFinished() {
        var player = new PlayerDto(10l , "unit-test", "unit-test");

        var finished = new BattleMovieFinished();
        finished.setPlayerDto(player);

        given(repository.findByPlayer(any())).willReturn(Optional.empty());

        service.calculateScore(finished);

        verify(strategy).calculate(eq(finished), any());
    }

}
