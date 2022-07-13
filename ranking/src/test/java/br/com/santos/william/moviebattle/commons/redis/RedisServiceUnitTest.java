package br.com.santos.william.moviebattle.commons.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.*;

public class RedisServiceUnitTest {

    private RedisTemplate<String, Object> template = mock(RedisTemplate.class);
    private ValueOperations<String, Object> operations = mock(ValueOperations.class);
    private RedisService service = new RedisService(template);

    @BeforeEach
    private void setup() {
        doReturn(operations).when(template).opsForValue();
    }

    @Test
    public void putShouldItemInRedis() {
        var duration = Duration.ofMillis(1000);
        service.put("key", "value", duration);

        verify(operations, times(1)).setIfAbsent("key", "value", duration);
    }

}
