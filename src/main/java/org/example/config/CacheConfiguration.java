package org.example.config;

import java.lang.reflect.Method;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfiguration {
    public static final String USERS_CACHE = "users-cache";

    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(USERS_CACHE);
    }

    @Bean("usersCache")
    Cache usersCache() {
        return new ConcurrentMapCache(USERS_CACHE);
    }
}