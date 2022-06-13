package br.com.santos.william.moviebattle.login;

import br.com.santos.william.moviebattle.commons.token.JwtTokenProvider;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.PlayerRepository;
import br.com.santos.william.moviebattle.player.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    private final Long battleId = 10l;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private Session session;

    private String baseUrl = "/api/login";

    @BeforeEach
    public void setup() {
        var player = new Player();
        player.setUsername("unit");

        given(playerRepository.findByUsername(any())).willReturn(Optional.of(player));
        given(jwtTokenProvider.createToken(any(), any())).willReturn("token");
    }

    @Test
    public void loginShouldReturnUnauthorized() throws Exception {
        given(authenticationManager.authenticate(any())).willThrow(BadCredentialsException.class);

        this.mockMvc.perform(
                        post(baseUrl)
                                .content("{\"username\":\"unit\", \"password\":\"test\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturnInvalidUser() throws Exception {
        given(playerRepository.findByUsername(any())).willReturn(Optional.empty());

        this.mockMvc.perform(
                        post(baseUrl)
                                .content("{\"username\":\"unit\", \"password\":\"test\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturnTokenWhenUserLogged() throws Exception {
        this.mockMvc.perform(
                        post(baseUrl)
                                .content("{\"username\":\"unit\", \"password\":\"test\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

}
