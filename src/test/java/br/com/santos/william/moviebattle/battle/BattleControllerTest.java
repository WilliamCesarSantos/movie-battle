package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.round.RoundStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@Disabled
public class BattleControllerTest {

    private final Long battleId = 10l;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BattleService service;

    private String baseUrl = "/api/battle";

    @Test
    public void listShouldListPageable() throws Exception {
        given(service.findByPlayer(any(), any())).willReturn(Page.empty());

        this.mockMvc.perform(get(baseUrl + "?player=1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void listShouldSearchByPlayer() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        battle.setDescription("unit-test");

        given(service.findByPlayer(any(), any())).willReturn(new PageImpl<>(List.of(battle)));

        this.mockMvc.perform(get(baseUrl + "?player=1"))
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

        this.mockMvc.perform(get(baseUrl + "/" + battleId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(battleId))
                .andExpect(jsonPath("$.description").value("unit-test"));
    }

    @Test
    public void findByIdReturnsResourceNotFound() throws Exception {
        given(service.findById(battleId)).willReturn(Optional.empty());

        this.mockMvc.perform(get(baseUrl + "/" + battleId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldCallsServiceInsert() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        battle.setDescription("unit-test");

        given(service.insert(any())).willReturn(battle);

        this.mockMvc.perform(
                        post(baseUrl).content("{\"description\":\"unit-test\"}")
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(battleId))
                .andExpect(jsonPath("$.description").value("unit-test"));
    }

    @Test
    public void startShouldThrowExceptionWhenNotFoundBattle() throws Exception {
        given(service.start(battleId)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/start")
                                .content("{}")
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void startShouldUpdateBattle() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        given(service.start(battleId)).willReturn(Optional.of(battle));

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/start")
                                .content("{}")
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).start(battleId);
    }

    @Test
    public void endShouldThrowExceptionWhenNotFoundBattle() throws Exception {
        given(service.start(battleId)).willReturn(Optional.empty());

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/end")
                                .content("{}")
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void endShouldUpdateBattle() throws Exception {
        var battle = new Battle();
        battle.setId(battleId);
        given(service.start(battleId)).willReturn(Optional.of(battle));

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/end")
                                .content("{}")
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).end(battleId);
    }

    @Test
    public void putRoundShouldCreateNew() throws Exception {
        var round = new Round();
        round.setId(battleId);
        round.setStatus(RoundStatus.OPEN);

        given(service.createRound(battleId)).willReturn(Optional.of(round));

        this.mockMvc.perform(put(baseUrl + "/" + battleId + "/round"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(battleId))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    public void putRoundShouldReturnNotFoundWhenBattleIsNotFound() throws Exception {
        given(service.createRound(battleId)).willReturn(Optional.empty());

        this.mockMvc.perform(put(baseUrl + "/" + battleId + "/round"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void putRoundShouldReturnFound() throws Exception {
        var round = new Round();
        round.setId(1l);
        round.setStatus(RoundStatus.HIT);

        given(service.listRound(battleId, 1l)).willReturn(Optional.of(round));

        this.mockMvc.perform(get(baseUrl + "/" + battleId + "/round/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    public void getRoundShouldThrowResourceNotFound() throws Exception {
        given(service.listRound(battleId, 1l)).willReturn(Optional.empty());

        this.mockMvc.perform(get(baseUrl + "/" + battleId + "/round/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void putAnswerShouldReturnCreate() throws Exception {
//        var round = new Round();
//        round.setId(1l);
//        round.setStatus(RoundStatus.HIT);
//
//        given(service.answer(eq(battleId), eq(1l), any())).willReturn(Optional.of(round));
//
//        this.mockMvc.perform(
//                        put(baseUrl + "/" + battleId + "/round/1/answer")
//                                .content("{\"choose\" : {\"id\": 1}}")
//                )
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value("1"))
//                .andExpect(jsonPath("$.status").value("HIT"));
    }

    @Test
    public void putAnswerShouldThrowResourceNotFound() throws Exception {
        given(service.answer(eq(battleId), eq(1l), any())).willReturn(Optional.empty());

        this.mockMvc.perform(
                        put(baseUrl + "/" + battleId + "/round/1/answer")
                                .content("{\"choose\" : {\"id\": 1}}")
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
