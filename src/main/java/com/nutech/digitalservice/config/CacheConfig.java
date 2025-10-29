package com.nutech.digitalservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Cache untuk banners, services, dan profiles dengan In-Memory Cache
        return new ConcurrentMapCacheManager("banners", "services", "profiles");
    }
}