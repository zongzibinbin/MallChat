package com.abin.frequencycontrol.domain.dto;

import com.abin.frequencycontrol.exception.BusinessErrorEnum;
import com.abin.frequencycontrol.exception.BusinessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Data
@Slf4j
public class TokenBucketDTO extends FrequencyControlDTO {

    private final long capacity; // 令牌桶容量
    private final double refillRate; // 每秒补充的令牌数
    private double tokens; // 当前令牌数量
    private long lastRefillTime; // 上次补充令牌的时间

    private final ReentrantLock lock = new ReentrantLock();

    public TokenBucketDTO(long capacity, double refillRate) {
        if (capacity <= 0 || refillRate <= 0) {
            throw new BusinessException(BusinessErrorEnum.CAPACITY_REFILL_ERROR);
        }
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    public boolean tryAcquire(int permits) {
        lock.lock();
        try {
            refillTokens();
            if (tokens < permits) {
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void deductionToken(int permits) {
        lock.lock();
        try {
            tokens -= permits;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 补充令牌
     */
    private void refillTokens() {
        long currentTime = System.nanoTime();
        // 转换为秒
        double elapsedTime = (currentTime - lastRefillTime) / 1e9;
        double tokensToAdd = elapsedTime * refillRate;
        log.info("tokensToAdd is {}", tokensToAdd);
        // 令牌总数不能超过令牌桶容量
        tokens = Math.min(capacity, tokens + tokensToAdd);
        log.info("current tokens is {}", tokens);
        lastRefillTime = currentTime;
    }
}
