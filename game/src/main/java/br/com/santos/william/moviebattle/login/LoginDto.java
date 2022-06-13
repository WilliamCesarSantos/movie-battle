package br.com.santos.william.moviebattle.login;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class LoginDto implements Serializable {

    @NotNull
    private String username;
    @NotNull
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
