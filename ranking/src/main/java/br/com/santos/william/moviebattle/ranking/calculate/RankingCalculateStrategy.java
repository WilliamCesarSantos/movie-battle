package br.com.santos.william.moviebattle.ranking.calculate;

import br.com.santos.william.moviebattle.ranking.Ranking;
import br.com.santos.william.moviebattle.ranking.dto.BattleMovieFinished;

public interface RankingCalculateStrategy {

    void calculate(BattleMovieFinished battle, Ranking ranking);

}
