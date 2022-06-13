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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
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
        return page.map(this::configureHateoas);
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
    public Battle findById(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @PathVariable("id") Long id
    ) {
        return service.findById(id)
                .map(this::configureHateoas)
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
    public Battle create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @RequestBody Battle battle
    ) {
        var created = service.create(battle);
        return this.configureHateoas(created);
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
    public Battle start(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @PathVariable("id") Long id,
            @RequestBody Battle battle
    ) {
        return service.findById(id)
                .map(service::start)
                .map(this::configureHateoas)
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
    public Battle end(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @PathVariable("id") Long id,
            @RequestBody Battle battle
    ) {
        return service.findById(id)
                .map(service::end)
                .map(this::configureHateoas)
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
    @PutMapping(value = "/{battle_id}/round", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Round createRound(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @PathVariable("battle_id") Long battleId
    ) {
        return service.findById(battleId)
                .map(service::createRound)
                .map(this::configureHateoas)
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
    @GetMapping(value = "/{battle_id}/round", produces = {APPLICATION_JSON_VALUE})
    public Page<Round> listRounds(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @PathVariable("battle_id") Long battleId,
            Pageable pageable
    ) {
        return service.listRounds(battleId, pageable)
                .map(this::configureHateoas);
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
    @GetMapping(value = "/{battle_id}/round/{round_id}", produces = {APPLICATION_JSON_VALUE})
    public Round findRoundById(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @PathVariable("battle_id") Long battleId,
            @PathVariable("round_id") Long roundId
    ) {
        return service.listRound(battleId, roundId)
                .map(this::configureHateoas)
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
    @PutMapping(value = "/{battle_id}/round/{round_id}/answer", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Answer answer(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authorization,
            @PathVariable("battle_id") Long battleId,
            @PathVariable("round_id") Long roundId,
            @Valid @RequestBody Answer answer
    ) {
        return service.listRound(battleId, roundId)
                .map(it -> service.answer(it.getBattle(), it, answer.getChoice()))
                .map(this::configureHateoas)
                .orElseThrow(ResourceNotFoundException::new);
    }

    private Battle configureHateoas(Battle battle) {
        battle.add(
                linkTo(methodOn(BattleController.class)
                        .listRounds(null, battle.getId(), PageRequest.of(1, 10))
                ).withRel("rounds")
        );

        return battle;
    }

    private Round configureHateoas(Round round) {
        round.add(
                linkTo(methodOn(BattleController.class)
                        .findById(null, round.getBattle().getId())
                ).withRel("battle")
        );
        return round;
    }

    private Answer configureHateoas(Answer answer) {
        answer.add(
                linkTo(methodOn(BattleController.class)
                        .findRoundById(null, answer.getNextRound().getBattle().getId(), answer.getNextRound().getId())
                ).withRel("nextRound"));
        return answer;
    }

}
