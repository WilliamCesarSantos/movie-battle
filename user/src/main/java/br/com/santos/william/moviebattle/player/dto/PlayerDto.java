package br.com.santos.william.moviebattle.player.dto;

import java.io.Serializable;

public class PlayerDto implements Serializable {

    private final Long id;
    private final String name;
    private final String username;

    public PlayerDto(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

}
