package br.com.santos.william.moviebattle.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService implements UserDetailsService {

    private final PlayerRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(
            PlayerRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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
}
