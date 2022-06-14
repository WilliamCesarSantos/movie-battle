package br.com.santos.william.moviebattle.login;

import br.com.santos.william.moviebattle.commons.redis.RedisService;
import br.com.santos.william.moviebattle.commons.token.JwtTokenProvider;
import br.com.santos.william.moviebattle.player.Player;
import br.com.santos.william.moviebattle.player.PlayerRepository;
import br.com.santos.william.moviebattle.player.dto.PlayerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.*;
import java.util.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlayerRepository playerRepository;
    private final ApplicationEventPublisher publisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    public LoginController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            PlayerRepository playerRepository,
            RedisService redisService,
            ApplicationEventPublisher publisher
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.playerRepository = playerRepository;
        this.publisher = publisher;
    }

    @Operation(summary = "Login in application")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    headers = {
                            @Header(name = HttpHeaders.AUTHORIZATION,
                                    description = "Token generated")
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not logged",
                    content = {
                            @Content(examples = {
                                    @ExampleObject(value = "{\"code\": \"UNAUTHORIZED\", \"message\": \"User invalid\"}")})
                    })
    })
    @PostMapping(consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<LoginDtoOutput> login(@Valid @RequestBody LoginDto login) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
        var user = playerRepository.findByUsername(login.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User is invalid"));

        var token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        log.debug("New token was generated for player: {}", user.getName());

        publisher.publishEvent(new LoginEvent(user, token));

        log.info("Player: {} logged", login.getUsername());
        var output = new LoginDtoOutput(login.getUsername(), token);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(output);
    }
}
