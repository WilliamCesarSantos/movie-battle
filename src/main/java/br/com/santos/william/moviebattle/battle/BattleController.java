package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.commons.exception.ResourceNotFoundException;
import br.com.santos.william.moviebattle.round.Answer;
import br.com.santos.william.moviebattle.round.Round;
import br.com.santos.william.moviebattle.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/battle")
public class BattleController {

    private final BattleService service;

    @Autowired
    public BattleController(BattleService service) {
        this.service = service;
    }

    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    public Page<Battle> list(@RequestParam("player") Long playerId, Pageable pageable) {
        Page<Battle> page;
        if (playerId != null && playerId > 0) {
            User player = new User();
            player.setId(playerId);
            page = service.findByPlayer(player, pageable);
        } else {
            page = service.list(pageable);
        }
        return page;
    }

    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public Battle findById(@PathVariable("id") Long id) {
        return service.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping(consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Battle insert(@RequestBody Battle battle) {
        return service.insert(battle);
    }

    @PutMapping(value = "/{id}/start", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Battle start(@PathVariable("id") Long id, @RequestBody Battle battle) {
        return service.start(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping(value = "/{id}/end", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Battle end(@PathVariable("id") Long id, @RequestBody Battle battle) {
        return service.end(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping(value = "/{id}/round", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Round round(@PathVariable("id") Long id) {
        return service.createRound(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping(value = "/{id}/rounds", produces = {APPLICATION_JSON_VALUE})
    public Page<Round> round(@PathVariable Long id, Pageable pageable) {
        return service.listRounds(id, pageable);
    }

    @GetMapping(value = "/{id}/round/{round_id}", produces = {APPLICATION_JSON_VALUE})
    public Round round(@PathVariable Long id, @PathVariable("round_id") Long roundId) {
        return service.listRound(id, roundId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping(value = "/{id}/round/{round_id}/answer", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Round answer(@PathVariable("id") Long id, @PathVariable("round_id") Long roundId, @Valid @RequestBody Answer answer) {
        return service.answer(id, roundId, answer.getChoose())
                .orElseThrow(ResourceNotFoundException::new);
    }

}
