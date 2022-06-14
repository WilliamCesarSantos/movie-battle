package br.com.santos.william.moviebattle.login;

import br.com.santos.william.moviebattle.player.Player;
import org.springframework.context.ApplicationEvent;

public class LoginEvent extends ApplicationEvent {

    public LoginEvent(
            Player player,
            String token
    ) {
        super(player);
    }

    public Player getPlayer() {
        return (Player) getSource();
    }

}
