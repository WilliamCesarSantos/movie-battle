package br.com.santos.william.moviebattle.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PlayerService service;

    private String baseUrl = "/api/player";

    @Test
    public void listShouldListPlayers() throws Exception {
        var player = new Player();
        player.setId(10l);
        player.setUsername("unit-test");

        given(service.list(any())).willReturn(new PageImpl<>(List.of(player)));

        this.mockMvc.perform(get(baseUrl))
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

        this.mockMvc.perform(get(baseUrl + "/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("10"))
                .andExpect(jsonPath("$.username").value("unit-test"));
    }

    @Test
    public void findByIdShouldReturnNotFound() throws Exception {
        given(service.findById(any())).willReturn(Optional.empty());

        this.mockMvc.perform(get(baseUrl + "/10"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldCallsServiceForIt() throws Exception {
        var player = new Player();
        player.setId(10l);
        player.setUsername("unit-test");
        player.setName("unit-test");
        player.setPassword("123");
        player.setRoles(List.of("*"));

        given(service.insert(any())).willReturn(player);

        this.mockMvc.perform(
                        post(baseUrl)
                                .content(mapper.writeValueAsString(player))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isCreated());

        verify(service).insert(any());
    }

    @Test
    public void insertShouldReturnsFieldsMinusThePassword() throws Exception {
        var player = new Player();
        player.setId(10l);
        player.setUsername("unit-test");
        player.setName("unit-test");
        player.setPassword("123");
        player.setRoles(List.of("*"));

        given(service.insert(any())).willReturn(player);

        this.mockMvc.perform(
                        post(baseUrl)
                                .content(mapper.writeValueAsString(player))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("10"))
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void insertShouldReturnsBadRequestWhenPlayerNoHasInformation() throws Exception {
        this.mockMvc.perform(
                        post(baseUrl)
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }
}
