package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.commons.exception.ResourceNotFoundException;
import br.com.santos.william.moviebattle.player.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "List ranking by logged user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not logged",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"UNAUTHORIZED\", \"message\": \"User invalid\"}")})
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Player not found",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"NOT_FOUND\", \"message\": \"Object not found\"}")
                            })
                    }
            )
    })
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    public Ranking list() {
        return service.list(session.getPlayer())
                .orElseThrow(ResourceNotFoundException::new);
    }

}
