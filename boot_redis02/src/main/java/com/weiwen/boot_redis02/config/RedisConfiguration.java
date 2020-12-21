package com.weiwen.boot_redis02.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * @author weiwen
 * @email yusiyiqingyan@163.com
 * @date 2020/12/21 11:35
 * @Description 配置类
 */

@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String host;

    /**
     * Redis 序列化后不乱码 配置
     *
     * @param connectionFactory LettuceConnectionFactory
     * @return RedisTemplate
     */
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    /**
     * 注入Redisson组件
     *
     * @return
     */
    @Bean
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":6379").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }
}