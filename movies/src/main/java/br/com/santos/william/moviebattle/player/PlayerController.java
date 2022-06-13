package br.com.santos.william.moviebattle.player;

import br.com.santos.william.moviebattle.battle.BattleController;
import br.com.santos.william.moviebattle.commons.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @Operation(summary = "List all players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not logged",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"UNAUTHORIZED\", \"message\": \"User invalid\"}")})
                    })
    })
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    public Page<Player> list(Pageable pageable) {
        return service.list(pageable)
                .map(this::configureHateoas);
    }

    @Operation(summary = "List player by id")
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
    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public Player findById(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @PathVariable("id") Long id
    ) {
        return service.findById(id)
                .map(this::configureHateoas)
                .orElseThrow(ResourceNotFoundException::new);
    }

    private Player configureHateoas(Player player) {
        var battles = linkTo(
                methodOn(BattleController.class)
                        .list(null, player.getId(), PageRequest.of(1, 10))
        ).withRel("battles");
        player.add(battles);
        return player;
    }

}
