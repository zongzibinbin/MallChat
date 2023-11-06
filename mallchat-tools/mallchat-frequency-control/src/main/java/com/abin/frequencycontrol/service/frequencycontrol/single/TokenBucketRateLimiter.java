package com.abin.frequencycontrol.service.frequencycontrol.single;

public class TokenBucketRateLimiter {
    private final int capacity; // 令牌桶容量
    private final int rate; // 令牌产生速率 (每秒的令牌数)
    private int tokens; // 当前令牌数量
    private long lastRefillTime; // 上次令牌补充时间
    private long left = 0;

    public TokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        refillTokens();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    //补充令牌
    private void refillTokens() {
        long currentTime = System.currentTimeMillis();
        //当前时间和上次请求时间相差的令牌数
        long elapsedTime = currentTime - lastRefillTime;

        if (elapsedTime > 0) {
            //需要补充的令牌
            int newTokens = (int) ((elapsedTime * rate + left) / 1000);
            left = (elapsedTime * rate + left) % 1000;
            //令牌不能超过桶的大小
            tokens = Math.min(tokens + newTokens, capacity);
            lastRefillTime = currentTime;
        }
    }
}
