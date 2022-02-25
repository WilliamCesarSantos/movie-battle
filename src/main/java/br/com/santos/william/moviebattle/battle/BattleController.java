package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.commons.exception.ResourceNotFoundException;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.round.Answer;
import br.com.santos.william.moviebattle.round.Round;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/battle")
public class BattleController {

    private final BattleService service;

    public BattleController(BattleService service) {
        this.service = service;
    }

    @Operation(summary = "List battles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not logged",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"UNAUTHORIZED\", \"message\": \"User invalid\"}")})
                    })
    })
    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    public Page<Battle> list(
            @RequestParam(value = "player", required = false) Long playerId,
            Pageable pageable
    ) {
        Page<Battle> page;
        if (playerId != null && playerId > 0) {
            Player player = new Player();
            player.setId(playerId);
            page = service.findByPlayer(player, pageable);
        } else {
            page = service.list(pageable);
        }
        return page;
    }

    @Operation(summary = "Find battle by id")
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
                    description = "Battle not found",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"NOT_FOUND\", \"message\": \"Object not found\"}")
                            })
                    }
            )
    })
    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public Battle findById(@PathVariable("id") Long id) {
        return service.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Operation(summary = "Create new battle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not logged",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"UNAUTHORIZED\", \"message\": \"User invalid\"}")})
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"errors\": [{\"description\":\"must not be null\"}]}")
                            })
                    }
            )
    })
    @PostMapping(consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Battle insert(@RequestBody Battle battle) {
        return service.create(battle);
    }

    @Operation(summary = "Start battle")
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
                    description = "Battle not found",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"NOT_FOUND\", \"message\": \"Object not found\"}")
                            })
                    }
            )
    })
    @PutMapping(value = "/{id}/start", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Battle start(@PathVariable("id") Long id, @RequestBody Battle battle) {
        return service.findById(id)
                .map(service::start)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Operation(summary = "End battle", description = "The battle has been started")
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
                    description = "Battle not found",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"NOT_FOUND\", \"message\": \"Object not found\"}")
                            })
                    }
            )
    })
    @PutMapping(value = "/{id}/end", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Battle end(@PathVariable("id") Long id, @RequestBody Battle battle) {
        return service.findById(id)
                .map(service::end)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Operation(summary = "Create new round", description = "Create new round if not exist any round opened. Battle must be started")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not logged",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"UNAUTHORIZED\", \"message\": \"User invalid\"}")})
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Battle not found",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"NOT_FOUND\", \"message\": \"Object not found\"}")
                            })
                    }
            )
    })
    @PutMapping(value = "/{id}/round", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Round round(@PathVariable("id") Long id) {
        return service.findById(id)
                .map(service::createRound)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Operation(summary = "List all rounds")
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
                    description = "Battle not found",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"NOT_FOUND\", \"message\": \"Object not found\"}")
                            })
                    }
            )
    })
    @GetMapping(value = "/{id}/rounds", produces = {APPLICATION_JSON_VALUE})
    public Page<Round> round(@PathVariable Long id, Pageable pageable) {
        return service.listRounds(id, pageable);
    }

    @Operation(summary = "Find round by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not logged",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"UNAUTHORIZED\", \"message\": \"User invalid\"}")})
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Battle or round not found",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"NOT_FOUND\", \"message\": \"Object not found\"}")
                            })
                    }
            )
    })
    @GetMapping(value = "/{id}/round/{round_id}", produces = {APPLICATION_JSON_VALUE})
    public Round round(@PathVariable Long id, @PathVariable("round_id") Long roundId) {
        return service.listRound(id, roundId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Operation(summary = "Answer round", description = "The battle has been started and not finished")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"BAD_REQUEST\", \"errors\": [{\"choice\":\"must not be null\"}]}")
                            })
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not logged",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"UNAUTHORIZED\", \"message\": \"User invalid\"}")})
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Battle or round not found",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"NOT_FOUND\", \"message\": \"Object not found\"}")
                            })
                    }
            )
    })
    @PutMapping(value = "/{id}/round/{round_id}/answer", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Answer answer(@PathVariable("id") Long id, @PathVariable("round_id") Long roundId, @Valid @RequestBody Answer answer) {
        return service.listRound(id, roundId)
                .map(it -> service.answer(it.getBattle(), it, answer.getChoice()))
                .orElseThrow(ResourceNotFoundException::new);
    }

}
