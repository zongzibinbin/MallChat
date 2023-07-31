package com.abin.mallchat.common.common.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Slf4j
public class MdcThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {


    @Override
    public void execute(@NotNull Runnable task) {
        log.info("execute Runnable");
        Map<String, String> context = MDC.getCopyOfContextMap();    //复制主线程MDC
        super.execute(() -> {
            if (null != context) {
                MDC.setContextMap(context);     //主线程MDC赋予子线程
            }
            try {
                task.run();
            } finally {
                try {
                    MDC.clear();
                } catch (Exception e) {
                    log.warn("MDC clear exception：{}", e.getMessage());
                }
            }
        });
    }

    @Override
    @NotNull
    public Future<?> submit(@NotNull Runnable task) {
        log.info("submit Runnable");
        Map<String, String> context = MDC.getCopyOfContextMap();
        return super.submit(() -> {
            if (null != context) {
                MDC.setContextMap(context);     //主线程MDC赋予子线程
            }
            try {
                task.run();
            } finally {
                try {
                    MDC.clear();
                } catch (Exception e) {
                    log.warn("MDC clear exception：{}", e.getMessage());
                }
            }
        });


    }


    /**
     * 异步事件监听 会调用下述方法
     */
    @Override
    @NotNull
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        log.info("submit call");
        Map<String, String> context = MDC.getCopyOfContextMap();
        return super.submit(() -> {
            if (null != context) {
                MDC.setContextMap(context);     //主线程MDC赋予子线程
            }
            try {
                return task.call();
            } finally {
                try {
                    MDC.clear();
                } catch (Exception e) {
                    log.warn("MDC clear exception：{}", e.getMessage());
                }
            }
        });
    }
}