package br.com.santos.william.moviebattle.player;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class PlayerWarmUpUnitTest {

    @Mock
    private PlayerService service;

    @InjectMocks
    private PlayerWarmUp warmUp;

    @Test
    public void warmUpShouldInsertTwoUsers() {
        warmUp.warmUp(new ApplicationReadyEvent(new SpringApplication(), null, null));

        verify(service, atLeast(2)).insert(any());
    }

    @Test
    public void warmUpShouldIInsertAdminUser() {
        warmUp.warmUp(new ApplicationReadyEvent(new SpringApplication(), null, null));

        var captor = ArgumentCaptor.forClass(Player.class);

        verify(service, atLeast(1)).insert(captor.capture());

        var player = captor.getAllValues()
                .stream().filter(it -> it.getUsername().equalsIgnoreCase("admin"))
                .findAny().orElse(null);
        assertNotNull(player);
    }
}
