package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.commons.exception.ResourceNotFoundException;
import br.com.santos.william.moviebattle.player.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService service;
    private final Session session;

    public RankingController(RankingService service, Session session) {
        this.service = service;
        this.session = session;
    }

    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    public Ranking list() {
        return service.list(session.getPlayer())
                .orElseThrow(ResourceNotFoundException::new);
    }

}
