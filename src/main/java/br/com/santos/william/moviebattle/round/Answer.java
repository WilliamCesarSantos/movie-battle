package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.movie.Movie;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class Answer implements Serializable {

    @NotNull
    private Movie choose;

    private RoundStatus status;

    private Round nextRound;

    public Answer() {
    }

    public Answer(Movie choose, RoundStatus status, Round nextRound) {
        this.choose = choose;
        this.status = status;
        this.nextRound = nextRound;
    }

    public Movie getChoose() {
        return choose;
    }

    public void setChoose(Movie choose) {
        this.choose = choose;
    }

    public RoundStatus getStatus() {
        return status;
    }

    public void setStatus(RoundStatus status) {
        this.status = status;
    }

    public Round getNextRound() {
        return nextRound;
    }

    public void setNextRound(Round nextRound) {
        this.nextRound = nextRound;
    }
}
