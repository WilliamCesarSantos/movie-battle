package br.com.santos.william.moviebattle.round.moviechoicestrategy;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.movie.Movie;
import org.springframework.data.util.Pair;

import java.util.List;

public interface MovieChoiceStrategy {

    Pair<Movie, Movie> choice(Battle battle, List<Movie> movies);

}
