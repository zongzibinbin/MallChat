package com.abin.frequencycontrol.service.frequencycontrol.single;

public class FixWindow {
    public Integer count;  //当前窗口累计请求数
    public long lastAcquireTime;//最后一次请求时间
    public Long windowInMillis; //固定窗口时间区间(毫秒)
    public Integer maxRequests; // 最大请求限制

    public FixWindow(Long windowInMillis, Integer maxRequests) {
        this.windowInMillis = windowInMillis;
        this.maxRequests = maxRequests;
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();  //获取系统当前时间
        //当前和上次不在同一时间窗口
        if (currentTime - lastAcquireTime > windowInMillis) {
            count = 0;  // 计数器清0
            lastAcquireTime = currentTime;  //开启新的时间窗口
        } else { //同一窗口内
            if (count < maxRequests) {  // 小于阀值
                count++;  //计数统计器加1
                return true;
            }
        }
        return false;
    }
}
