package com.nutech.digitalservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName("redis");
        factory.setPort(6379);
        factory.setDatabase(0);

        log.info("Redis Connection Factory created with host: {}, port: {}", factory.getHostName(), factory.getPort());
        return factory;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        log.info("Creating Redis Cache Manager...");

        // Redis cache configuration dengan TTL dan serializer
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // TTL 30 menit
                .disableCachingNullValues() // Tidak cache null values
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // Cache configurations yang berbeda untuk setiap cache type
        RedisCacheConfiguration profileCacheConfig = config.entryTtl(Duration.ofHours(1)); // Profile cache 1 jam
        RedisCacheConfiguration bannerCacheConfig = config.entryTtl(Duration.ofMinutes(15)); // Banner cache 15 menit
        RedisCacheConfiguration serviceCacheConfig = config.entryTtl(Duration.ofMinutes(10)); // Service cache 10 menit

        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("profiles", profileCacheConfig)
                .withCacheConfiguration("banners", bannerCacheConfig)
                .withCacheConfiguration("services", serviceCacheConfig)
                .transactionAware()
                .build();

        log.info("Redis Cache Manager created successfully with caches: {}", cacheManager.getCacheNames());
        return cacheManager;
    }
}