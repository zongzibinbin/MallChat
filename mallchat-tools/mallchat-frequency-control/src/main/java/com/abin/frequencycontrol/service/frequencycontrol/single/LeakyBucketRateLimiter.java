package com.abin.frequencycontrol.service.frequencycontrol.single;

public class LeakyBucketRateLimiter {
    private final int capacity; // 桶的容量
    private final int rate; // 出桶速率 (每秒的请求数)
    private long lastRequestTime; // 上一个请求的时间戳

    public LeakyBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.lastRequestTime = System.currentTimeMillis();
    }

    /**
     * 尝试请求
     *
     * @return >0 请求进入桶里，返回的是需要休眠的时间。
     */
    public synchronized long tryAcquire() {
        //当前时间
        long currentTime = System.currentTimeMillis();
        //漏桶空的
        if (currentTime > lastRequestTime) {
            lastRequestTime = currentTime;
            return 0; // 请求被允许
        }
        //上次取水的间隔时间
        long elapsedTime = lastRequestTime - currentTime;
        // 计算桶中的水量
        int water = (int) elapsedTime * rate / 1000;
        //水量不超过容量
        if (water < capacity) {
            long sleepTime = (1000 / rate) + elapsedTime;
            lastRequestTime = currentTime + sleepTime;
            return sleepTime;
        }
        return -1; // 请求被拒绝
    }
}
