package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.movie.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Round extends RepresentationModel<Round> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "movie_one_id")
    private Movie first;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "movie_two_id")
    private Movie second;

    @NotNull
    @ManyToOne
    @JsonIgnore
    private Battle battle;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoundStatus status;

    @ManyToOne
    @JsonIgnore
    private Movie choice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getFirst() {
        return first;
    }

    public void setFirst(Movie first) {
        this.first = first;
    }

    public Movie getSecond() {
        return second;
    }

    public void setSecond(Movie second) {
        this.second = second;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public RoundStatus getStatus() {
        return status;
    }

    public void setStatus(RoundStatus status) {
        this.status = status;
    }

    public Movie getChoice() {
        return choice;
    }

    public void setChoice(Movie choice) {
        this.choice = choice;
    }
}
