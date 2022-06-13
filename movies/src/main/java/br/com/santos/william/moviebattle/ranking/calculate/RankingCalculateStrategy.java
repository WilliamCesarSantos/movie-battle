package br.com.santos.william.moviebattle.ranking.calculate;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.ranking.Ranking;

public interface RankingCalculateStrategy {

    void calculate(Battle battle, Ranking ranking);

}
