package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.commons.token.JwtTokenProvider;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.PlayerService;
import br.com.santos.william.moviebattle.player.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RankingControllerTest {

    private final Long battleId = 10l;

    @Autowired
    private JwtTokenProvider provider;

    @MockBean
    private PlayerService playerService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingService service;

    @MockBean
    private Session session;

    private String baseUrl = "/api/ranking";
    private String token = "token";

    @BeforeEach
    public void setup() {
        var player = new Player();
        given(playerService.loadUserByUsername("user")).willReturn(player);

        token = provider.createToken("user", "*");
    }

    @Test
    public void listShouldListRanking() throws Exception {
        var player = new Player();
        player.setId(10l);

        var ranking = new Ranking();
        ranking.setScore(10f);
        ranking.setId(10l);
        ranking.setPlayer(player);

        given(session.getPlayer()).willReturn(player);
        given(service.list(player)).willReturn(Optional.of(ranking));

        this.mockMvc.perform(
                        get(baseUrl)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("10"))
                .andExpect(jsonPath("$.score").value("10.0"));
    }

    @Test
    public void listShouldReturnsNotFound() throws Exception {
        given(service.list(any())).willReturn(Optional.empty());

        this.mockMvc.perform(
                        get(baseUrl)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
