package br.com.santos.william.moviebattle.commons.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private RedisTemplate<String, Object> template;

    public RedisService(RedisTemplate<String, Object> template) {
        this.template = template;
    }

    public void put(String key, Object object, Duration duration) {
        template.opsForValue().setIfAbsent(key, object, duration);
    }
}
