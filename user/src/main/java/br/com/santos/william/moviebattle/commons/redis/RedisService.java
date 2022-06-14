package br.com.santos.william.moviebattle.commons.redis;

import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
        /*template.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.watch(key);
                operations.opsForValue().set(key, object, duration);
                operations.exec();
                return null;
            }
        });*/
    }
}
