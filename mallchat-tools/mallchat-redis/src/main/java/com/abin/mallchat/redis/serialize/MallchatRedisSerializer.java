package com.abin.mallchat.redis.serialize;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Description: 可扩展的Redis序列化器
 *
 * @author likain
 * @since 2023/8/27 15:36
 */
public class MallchatRedisSerializer<T> extends Jackson2JsonRedisSerializer<T> {

    public MallchatRedisSerializer(Class<T> type) {
        super(type);
    }
}
