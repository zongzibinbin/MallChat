package com.abin.mallchat.common.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-22
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(String.format("redis://%s:%d", redisProperties.getHost(), redisProperties.getPort()))
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase());
        return Redisson.create(config);
    }

}
