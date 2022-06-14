package br.com.santos.william.moviebattle.player.dto;

import br.com.santos.william.moviebattle.player.Player;

import java.io.Serializable;

public class PlayerDto implements Serializable {

    private Long id;
    private String name;
    private String username;

    public PlayerDto() {
    }

    public PlayerDto(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
