package com.example.apigateway.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory redisConnectionFactory,
            GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer) {
        StringRedisSerializer keySerializer =
                new StringRedisSerializer();

        RedisSerializationContext<String, Object> context =
                RedisSerializationContext
                        .<String, Object>newSerializationContext(keySerializer)
                        .value(genericJacksonJsonRedisSerializer)
                        .hashKey(keySerializer)
                        .hashValue(genericJacksonJsonRedisSerializer)
                        .build();
        return new ReactiveRedisTemplate<>(redisConnectionFactory, context);

    }

    @Bean
    public GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer() {
        return GenericJacksonJsonRedisSerializer.builder().build();
    }
}
