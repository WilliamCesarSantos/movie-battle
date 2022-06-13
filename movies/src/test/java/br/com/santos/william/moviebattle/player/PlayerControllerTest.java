package br.com.santos.william.moviebattle.player;

import br.com.santos.william.moviebattle.commons.token.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @Autowired
    private JwtTokenProvider provider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PlayerService service;

    private String baseUrl = "/api/player";
    private String token = "token";

    @BeforeEach
    public void setup() {
        var player = new Player();
        given(service.loadUserByUsername("user")).willReturn(player);

        token = provider.createToken("user", "*");
    }

    @Test
    public void listShouldListPlayers() throws Exception {
        var player = new Player();
        player.setId(10l);
        player.setUsername("unit-test");

        given(service.list(any())).willReturn(new PageImpl<>(List.of(player)));

        this.mockMvc.perform(
                        get(baseUrl)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("10"))
                .andExpect(jsonPath("$.content[0].username").value("unit-test"));
    }

    @Test
    public void findByIdShouldReturnItem() throws Exception {
        var player = new Player();
        player.setId(10l);
        player.setUsername("unit-test");

        given(service.findById(10l)).willReturn(Optional.of(player));

        this.mockMvc.perform(
                        get(baseUrl + "/10")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("10"))
                .andExpect(jsonPath("$.username").value("unit-test"));
    }

    @Test
    public void findByIdShouldReturnNotFound() throws Exception {
        given(service.findById(any())).willReturn(Optional.empty());

        this.mockMvc.perform(
                        get(baseUrl + "/10")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
