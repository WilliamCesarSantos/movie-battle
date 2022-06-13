package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.commons.token.JwtTokenProvider;
import br.com.santos.william.moviebattle.movie.Movie;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.PlayerService;
import br.com.santos.william.moviebattle.round.Answer;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BattleControllerTest {

    private final Long battleId = 10l;

    @Autowired
    private JwtTokenProvider provider;

    @MockBean
    private PlayerService playerService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BattleService service;

    private String baseUrl = "/api/battle";
    private String token = "token";

    @BeforeEach
    public void setup() {
        var player = new Player();
        given(playerService.loadUserByUsername("user")).willReturn(player);

        token = provider.createToken("user", "*");
    }

    @Test
    public void listShouldListPageable() throws Exception {
        given(service.list(any())).willReturn(Page.empty());

        this.mockMvc.perform(
                        get(baseUrl)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void listShouldSearchByPlayer() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        battle.setDescription("unit-test");

        given(service.findByPlayer(any(), any())).willReturn(new PageImpl<>(List.of(battle)));

        this.mockMvc.perform(
                        get(baseUrl + "?player=1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(battleId))
                .andExpect(jsonPath("$.content[0].description").value("unit-test"));
    }

    @Test
    public void findByIdReturnsFound() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        battle.setDescription("unit-test");

        given(service.findById(battleId)).willReturn(Optional.of(battle));

        this.mockMvc.perform(
                        get(baseUrl + "/" + battleId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(battleId))
                .andExpect(jsonPath("$.description").value("unit-test"));
    }

    @Test
    public void findByIdReturnsResourceNotFound() throws Exception {
        given(service.findById(battleId)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        get(baseUrl + "/" + battleId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldCallsServiceInsert() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        battle.setDescription("unit-test");

        given(service.create(any())).willReturn(battle);

        this.mockMvc.perform(
                        post(baseUrl)
                                .content("{\"description\":\"unit-test\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(battleId))
                .andExpect(jsonPath("$.description").value("unit-test"));
    }

    @Test
    public void startShouldThrowExceptionWhenNotFoundBattle() throws Exception {
        given(service.findById(battleId)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/start")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void startShouldUpdateBattle() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        given(service.findById(battleId)).willReturn(Optional.of(battle));
        given(service.start(any())).willReturn(battle);

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/start")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void endShouldThrowExceptionWhenNotFoundBattle() throws Exception {
        given(service.findById(battleId)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/end")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void endShouldUpdateBattle() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        given(service.findById(battleId)).willReturn(Optional.of(battle));
        given(service.end(any())).willReturn(battle);

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/end")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).end(battle);
    }

    @Test
    public void putRoundShouldCreateNew() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);

        var round = new Round();
        round.setId(1l);
        round.setBattle(battle);

        given(service.findById(battleId)).willReturn(Optional.of(battle));
        given(service.createRound(any())).willReturn(round);

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/round")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void putRoundShouldReturnNotFoundWhenBattleIsNotFound() throws Exception {
        given(service.findById(battleId)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/round")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRoundShouldReturnFound() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);

        var round = new Round();
        round.setId(1l);
        round.setStatus(RoundStatus.OPEN);
        round.setBattle(battle);

        given(service.listRound(battleId, 1l)).willReturn(Optional.of(round));

        this.mockMvc.perform(
                        get(baseUrl + "/" + battleId + "/round/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    public void getRoundsShouldReturnFound() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);

        var round = new Round();
        round.setId(1l);
        round.setStatus(RoundStatus.OPEN);
        round.setBattle(battle);

        given(service.listRounds(eq(battleId), any())).willReturn(new PageImpl<>(List.of(round)));

        this.mockMvc.perform(
                        get(baseUrl + "/" + battleId + "/round")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].status").value("OPEN"));
    }

    @Test
    public void getRoundShouldThrowResourceNotFound() throws Exception {
        given(service.listRound(battleId, 1l)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        get(baseUrl + "/" + battleId + "/round/1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void putAnswerShouldReturnCreate() throws Exception {
        var battle = new Battle();
        battle.setId(10L);

        var round = new Round();
        round.setId(1l);
        round.setStatus(RoundStatus.HIT);
        round.setBattle(battle);

        var answer = new Answer();
        answer.setNextRound(round);
        answer.setStatus(RoundStatus.HIT);
        answer.setChoice(new Movie());

        given(service.listRound(eq(battleId), eq(1l))).willReturn(Optional.of(round));
        given(service.answer(any(), any(), any())).willReturn(answer);

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/round/1/answer")
                                .content("{\"choice\" : {\"id\": 1}}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.nextRound").isMap())
                .andExpect(jsonPath("$.status").value("HIT"))
                .andExpect(jsonPath("$.choice").isMap());
    }

    @Test
    public void putAnswerShouldThrowResourceNotFound() throws Exception {
        given(service.listRound(battleId, 1l)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/round/1/answer")
                                .content("{\"choice\" : {\"id\": 1}}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void putAnswerShouldThrowExceptionForInvalidPayload() throws Exception {
        given(service.listRound(battleId, 1l)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/round/1/answer")
                                .content("{}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errors[0].choice").value("must not be null"));
    }

}
