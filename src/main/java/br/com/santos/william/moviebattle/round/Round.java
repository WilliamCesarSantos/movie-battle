package br.com.santos.william.moviebattle.round;

import br.com.santos.william.moviebattle.battle.Battle;
import br.com.santos.william.moviebattle.movie.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Round implements Serializable {

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

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private Round nextRound;

    @NotNull
    @ManyToOne
    private Battle battle;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoundStatus status;

    @ManyToOne
    @JsonIgnore
    private Movie choose;

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

    public Round getNextRound() {
        return nextRound;
    }

    public void setNextRound(Round nextRound) {
        this.nextRound = nextRound;
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

    public Movie getChoose() {
        return choose;
    }

    public void setChoose(Movie choose) {
        this.choose = choose;
    }
}
