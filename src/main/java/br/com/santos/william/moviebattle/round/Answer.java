package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.movie.Movie;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class Answer implements Serializable {

    @NotNull
    private Movie choose;

    public Movie getChoose() {
        return choose;
    }

    public void setChoose(Movie choose) {
        this.choose = choose;
    }
}
