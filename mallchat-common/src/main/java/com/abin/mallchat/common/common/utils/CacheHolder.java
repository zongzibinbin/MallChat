package com.abin.mallchat.common.common.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;

import java.time.Duration;

/**
 * Description: Cache管理器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-05
 */
public class CacheHolder {

    private static final Long MAX_MUM_SIZE = 10000L;

    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    /**
     * 所有请求登录的code与channel关系
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_MUM_SIZE)
            .build();
}
