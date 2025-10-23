package org.eee.account.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil
{
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final static long EXPIRE_TIME = 60 * 60 * 5;

    /**
     * 写入缓存
     */
    public boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value, EXPIRE_TIME, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取缓存
     */
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? null : value.toString();
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
