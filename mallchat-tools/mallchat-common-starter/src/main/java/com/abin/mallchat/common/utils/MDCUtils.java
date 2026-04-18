package com.abin.mallchat.common.utils;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * MDC工具类，用于在异步任务中传递MDC上下文
 */
public class MDCUtils {

    /**
     * 包装Runnable，传递MDC上下文
     */
    public static Runnable wrap(Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 包装Callable，传递MDC上下文
     */
    public static <T> Callable<T> wrap(Callable<T> callable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 包装Supplier，传递MDC上下文
     */
    public static <T> Supplier<T> wrap(Supplier<T> supplier) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                return supplier.get();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 获取当前MDC上下文的副本
     */
    public static Map<String, String> getContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * 设置MDC上下文
     */
    public static void setContext(Map<String, String> contextMap) {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
    }

    /**
     * 清除MDC上下文
     */
    public static void clearContext() {
        MDC.clear();
    }
}
