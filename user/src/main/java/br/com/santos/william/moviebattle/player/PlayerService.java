package br.com.santos.william.moviebattle.player;

import br.com.santos.william.moviebattle.commons.redis.RedisService;
import br.com.santos.william.moviebattle.login.LoginEvent;
import br.com.santos.william.moviebattle.player.dto.PlayerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class PlayerService implements UserDetailsService {

    private final PlayerRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    private Long expiresSession;

    private Logger log = LoggerFactory.getLogger(getClass());

    public PlayerService(
            PlayerRepository repository,
            PasswordEncoder passwordEncoder,
            RedisService redisService,
            @Value("${security.jwt.token.expire-length:3600000}") Long expiresSession
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.expiresSession = expiresSession;
    }

    public Page<Player> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Player> findById(Long id) {
        return repository.findById(id);
    }

    public Player insert(Player player) {
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        return repository.save(player);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is invalid"));
    }

    @EventListener
    public void publishToRedis(LoginEvent event) {
        var user = event.getPlayer();
        log.debug("Setting player: {} in Redis session", user.getUsername());
        redisService.put(
                user.getUsername(),
                new PlayerDto(user.getId(), user.getName(), user.getUsername()),
                Duration.ofMillis(expiresSession)
        );
    }

}
