package com.abin.frequencycontrol.mannager;

import com.abin.frequencycontrol.domain.dto.TokenBucketDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class TokenBucketManager {

    private final Map<String, TokenBucketDTO> tokenBucketMap = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public void createTokenBucket(String key, long capacity, double refillRate) {
        lock.lock();
        try {
            if (!tokenBucketMap.containsKey(key)) {
                TokenBucketDTO tokenBucket = new TokenBucketDTO(capacity, refillRate);
                tokenBucketMap.put(key, tokenBucket);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeTokenBucket(String key) {
        lock.lock();
        try {
            tokenBucketMap.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public boolean tryAcquire(String key, int permits) {
        TokenBucketDTO tokenBucket = tokenBucketMap.get(key);
        if (tokenBucket != null) {
            return tokenBucket.tryAcquire(permits);
        }
        return false;
    }

    public void deductionToken(String key, int permits) {
        TokenBucketDTO tokenBucket = tokenBucketMap.get(key);
        if (tokenBucket != null) {
            tokenBucket.deductionToken(permits);
        }
    }
}
