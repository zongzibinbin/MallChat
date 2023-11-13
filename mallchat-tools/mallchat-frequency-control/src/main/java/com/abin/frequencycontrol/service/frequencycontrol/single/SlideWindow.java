package com.abin.frequencycontrol.service.frequencycontrol.single;

import java.util.LinkedList;

public class SlideWindow {

    private final int maxRequests;//最大请求
    private final long windowInMillis;//窗口范围
    private LinkedList<Long> requestTimestamps;//每个请求的时间戳

    public SlideWindow(int maxRequests, long windowInMillis) {
        this.maxRequests = maxRequests;
        this.windowInMillis = windowInMillis;
        this.requestTimestamps = new LinkedList<>();
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        //清除过期窗口的请求
        cleanExpiredRequests(currentTime);
        //统计窗口内的请求数小于总限制
        if (requestTimestamps.size() < maxRequests) {
            requestTimestamps.addLast(currentTime);
            return true;
        }

        return false;
    }

    private void cleanExpiredRequests(long currentTime) {
        //由于是LinkedList，头结点就是最早的请求，判断超出时间窗口就移除，留下的都是窗口内的请求
        while (!requestTimestamps.isEmpty() && (currentTime - requestTimestamps.getFirst() > windowInMillis)) {
            requestTimestamps.removeFirst();
        }
    }
}
