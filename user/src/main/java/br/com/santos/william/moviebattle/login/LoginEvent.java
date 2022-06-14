package br.com.santos.william.moviebattle.login;

import br.com.santos.william.moviebattle.player.Player;
import org.springframework.context.ApplicationEvent;

public class LoginEvent extends ApplicationEvent {

    private final String token;

    public LoginEvent(
            Player player,
            String token
    ) {
        super(player);
        this.token = token;
    }

    public Player getPlayer() {
        return (Player) getSource();
    }

    public String getToken() {
        return token;
    }
}
