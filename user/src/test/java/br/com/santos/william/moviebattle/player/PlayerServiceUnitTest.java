package br.com.santos.william.moviebattle.player;

import br.com.santos.william.moviebattle.commons.redis.RedisService;
import br.com.santos.william.moviebattle.login.LoginEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class PlayerServiceUnitTest {

    @Mock
    private PlayerRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private PlayerService service;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(service, "expiresSession", 3000l);
    }

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
        var user = new Player();
        user.setPassword("123");

        service.insert(user);

        verify(repository).save(user);
    }

    @Test
    public void insertShouldEncryptPassword() {
        given(passwordEncoder.encode("123")).willReturn("abc");

        var user = new Player();
        user.setPassword("123");

        service.insert(user);

        verify(passwordEncoder).encode("123");
        assertEquals("abc", user.getPassword());
    }

    @Test
    public void loadUserByUsernameShouldForwardParameter() {
        given(repository.findByUsername(any())).willReturn(Optional.of(new Player()));

        service.loadUserByUsername("unit-test");

        verify(repository).findByUsername("unit-test");
    }

    @Test
    public void loadUserByUsernameShouldThrowExceptionWhenNotFound() {
        given(repository.findByUsername(any())).willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unit-test"));
    }

    @Test
    public void serviceShouldPublishToRedisLoggedUser() {
        var player = new Player();
        player.setId(10l);
        player.setUsername("unit-test");
        player.setName("unit-test");
        var event = new LoginEvent(player, "unit-test");

        service.publishToRedis(event);

        verify(redisService, times(1)).put(
                eq(player.getUsername()),
                any(),
                any()
        );
    }
}
