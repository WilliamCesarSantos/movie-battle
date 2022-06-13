package br.com.santos.william.moviebattle.round.answerstrategy;

import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;

public interface AnswerStrategy {

    RoundStatus answer(Round round, Movie chosen);

}
