package br.com.santos.william.moviebattle.ranking.dto;

import br.com.santos.william.moviebattle.player.dto.PlayerDto;

public class BattleMovieFinished {

    private Long id;
    private Integer totalRounds = 0;
    private Integer hits = 0;
    private Integer miss = 0;
    private PlayerDto playerDto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getMiss() {
        return miss;
    }

    public void setMiss(Integer miss) {
        this.miss = miss;
    }

    public PlayerDto getPlayerDto() {
        return playerDto;
    }

    public void setPlayerDto(PlayerDto playerDto) {
        this.playerDto = playerDto;
    }
}
