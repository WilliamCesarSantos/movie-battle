package br.com.santos.william.moviebattle.player;

import br.com.santos.william.moviebattle.commons.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService service;

    @Autowired
    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    public Page<Player> list(Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public Player findById(@PathVariable("id") Long id) {
        return service.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

}
