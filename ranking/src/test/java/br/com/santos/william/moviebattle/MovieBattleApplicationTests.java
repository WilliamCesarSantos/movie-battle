package br.com.santos.william.moviebattle;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;

@SpringBootTest
class MovieBattleApplicationTests {

    @MockBean
    private SimpleMessageListenerContainer listenerContainer;

    @Test
    void contextLoads() {
    }

}
