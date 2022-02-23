package br.com.santos.william.moviebattle.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    public void listShouldForwardParameter() {
        service.list(Pageable.unpaged());

        verify(repository).findAll(Pageable.unpaged());
    }

    @Test
    public void findByIdShouldForwardParameter() {
        service.findById(1l);

        verify(repository).findById(1l);
    }

    @Test
    public void insertShouldForwardParameter() {
        var user = new User();
        user.setPassword("123");

        service.insert(user);

        verify(repository).save(user);
    }

    @Test
    public void insertShouldEncryptPassword() {
        given(passwordEncoder.encode("123")).willReturn("abc");

        var user = new User();
        user.setPassword("123");

        service.insert(user);

        verify(passwordEncoder).encode("123");
        assertEquals("abc", user.getPassword());
    }

    @Test
    public void loadUserByUsernameShouldForwardParameter() {
        given(repository.findByUsername(any())).willReturn(Optional.of(new User()));

        service.loadUserByUsername("unit-test");

        verify(repository).findByUsername("unit-test");
    }

    @Test
    public void loadUserByUsernameShouldThrowExceptionWhenNotFound() {
        given(repository.findByUsername(any())).willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unit-test"));
    }
}
