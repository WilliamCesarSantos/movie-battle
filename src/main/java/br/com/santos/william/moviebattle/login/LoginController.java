package br.com.santos.william.moviebattle.login;

import br.com.santos.william.moviebattle.commons.token.JwtTokenProvider;
import br.com.santos.william.moviebattle.player.PlayerRepository;
import br.com.santos.william.moviebattle.player.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlayerRepository playerRepository;
    private final Session session;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public LoginController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            PlayerRepository playerRepository,
            Session session
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.playerRepository = playerRepository;
        this.session = session;
    }

    @PostMapping(consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity login(@Valid @RequestBody LoginDto login) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
        var user = playerRepository.findByUsername(login.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User is invalid"));
        log.trace("Setting player: {} in session", user.getId());
        session.setPlayer(user);
        var token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        log.debug("New token was generated for player: {}", user.getName());

        log.info("Player: {} logged", login.getUsername());
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "token", token
        ));
    }
}
