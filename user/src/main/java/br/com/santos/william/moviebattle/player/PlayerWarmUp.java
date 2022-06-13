package br.com.santos.william.moviebattle.player;

import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class PlayerWarmUp {

    private final PlayerService userService;
    private Logger log = LoggerFactory.getLogger(getClass());

    public PlayerWarmUp(PlayerService userService) {
        this.userService = userService;
    }

    @EventListener
    @Timed(description = "Time spent to insert players")
    public void warmUp(ApplicationReadyEvent event) {
        log.info("Executing player warmup");
        try {
            userService.loadUserByUsername("will");
        } catch (UsernameNotFoundException exception) {
            var player = new Player();
            player.setPassword("123");
            player.setUsername("will");
            player.setName("will");
            player.setRoles("*");
            userService.insert(player);
            log.debug("Inserted player: Will");
        }

        try {
            userService.loadUserByUsername("admin");
        } catch (UsernameNotFoundException exception) {
            var player = new Player();
            player.setPassword("123");
            player.setUsername("admin");
            player.setName("administrator");
            player.setRoles("*");
            userService.insert(player);
        }
        log.info("Executed player warmup. Lets go");
    }
}
