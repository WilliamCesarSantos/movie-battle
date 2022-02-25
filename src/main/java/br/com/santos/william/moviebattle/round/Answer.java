package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.movie.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class Answer extends RepresentationModel<Answer> implements Serializable {

    @NotNull
    private Movie choice;

    private RoundStatus status;

    @JsonIgnore
    private Round nextRound;

    public Answer() {
    }

    public Answer(Movie choose, RoundStatus status, Round nextRound) {
        this.choice = choose;
        this.status = status;
        this.nextRound = nextRound;
    }

    public Movie getChoice() {
        return choice;
    }

    public void setChoice(Movie choice) {
        this.choice = choice;
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
