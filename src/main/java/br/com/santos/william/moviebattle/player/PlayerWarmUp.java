package br.com.santos.william.moviebattle.player;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerWarmUp {

    private final PlayerService userService;

    public PlayerWarmUp(PlayerService userService) {
        this.userService = userService;
    }

    @EventListener
    public void warmUp(ApplicationReadyEvent event) {
        var player = new Player();
        player.setPassword("123");
        player.setUsername("will");
        player.setName("will");
        player.setRoles(List.of("*"));
        userService.insert(player);

        player = new Player();
        player.setPassword("123");
        player.setUsername("admin");
        player.setName("administrator");
        player.setRoles(List.of("*"));
        userService.insert(player);
    }
}
