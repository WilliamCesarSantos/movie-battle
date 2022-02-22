package br.com.santos.william.moviebattle.user;

import org.springframework.web.context.annotation.SessionScope;

@SessionScope
public class Session {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
