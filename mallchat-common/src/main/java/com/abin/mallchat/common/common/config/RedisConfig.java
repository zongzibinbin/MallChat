package com.abin.mallchat.common.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import java.util.Objects;

@Configuration
public class RedisConfig {

    @Bean("myRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建模板
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置序列化工具
        MyRedisSerializerCustomized jsonRedisSerializer =
                new MyRedisSerializerCustomized();
        // key和 hashKey采用 string序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // value和 hashValue采用 JSON序列化
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private static class MyRedisSerializerCustomized extends GenericJackson2JsonRedisSerializer {
        @Override
        public byte[] serialize(Object source) throws SerializationException {
            if (Objects.nonNull(source)) {
                if (source instanceof String || source instanceof Character) {
                    return source.toString().getBytes();
                }
            }
            return super.serialize(source);
        }

        @Override
        public <T> T deserialize(byte[] source, Class<T> type) throws SerializationException {
            Assert.notNull(type,
                    "Deserialization type must not be null! Please provide Object.class to make use of Jackson2 default typing.");
            if (source == null || source.length == 0) {
                return null;
            }
            if (type.isAssignableFrom(String.class) || type.isAssignableFrom(Character.class)) {
                return (T) new String(source);
            }
            return super.deserialize(source, type);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(1));
        System.out.println(objectMapper.writeValueAsString("1"));
        System.out.println(objectMapper.writeValueAsString(Boolean.TRUE));
    }
}
