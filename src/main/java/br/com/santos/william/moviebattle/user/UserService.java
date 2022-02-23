package br.com.santos.william.moviebattle.user;

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
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public User insert(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is invalid"));
    }
}
