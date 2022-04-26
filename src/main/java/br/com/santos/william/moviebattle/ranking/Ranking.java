package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.player.Player;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Ranking extends RepresentationModel<Ranking> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ranking_pk")
    @SequenceGenerator(name = "ranking_pk", sequenceName = "pk_table")
    private Long id;

    @ManyToOne
    private Player player;

    @NotNull
    @Min(0)
    private Float score = 0f;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

}
