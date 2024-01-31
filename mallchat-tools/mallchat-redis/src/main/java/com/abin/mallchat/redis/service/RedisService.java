package com.abin.mallchat.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Description: 待扩展的RedisService
 *
 * @author likain
 * @since 2023/8/27 15:40
 */
@Service
public class RedisService<K, V> {

    @Autowired
    private RedisTemplate<K, V> redisTemplate;

    public V getValue(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setValue(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValue(K key, V value, long expire, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, expire, unit);
    }

    public void deleteKey(K key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(K key) {
        Boolean b = redisTemplate.hasKey(key);
        return b != null && b;
    }

    public long getExpire(K key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        return expire == null ? -1L : expire;
    }

    public void setExpire(K key, long expire, TimeUnit unit) {
        redisTemplate.expire(key, expire, unit);
    }

    public void addExpire(K key, long extra, TimeUnit unit) {
        long oldExp = this.getExpire(key, unit);
        if (oldExp == -1) {
            throw new ExpireNotExistException("There is no expire for key: [" + key + "]");
        }

        long newExp = oldExp + extra;
        this.setExpire(key, newExp, unit);
    }
}
