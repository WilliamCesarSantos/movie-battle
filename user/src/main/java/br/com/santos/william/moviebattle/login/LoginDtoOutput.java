package br.com.santos.william.moviebattle.login;

public class LoginDtoOutput {

    private String username;
    private String token;

    public LoginDtoOutput(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

}
