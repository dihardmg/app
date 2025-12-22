package com.nutech.digitalservice.controller;

import com.nutech.digitalservice.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/cache-test")
public class CacheTestController {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ProfileService profileService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCacheStatus() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if Redis cache manager is available
            if (cacheManager != null) {
                response.put("cacheManager", cacheManager.getClass().getSimpleName());
                response.put("cacheNames", cacheManager.getCacheNames());

                // Check specific caches
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheManager.getCacheNames().forEach(cacheName -> {
                    var cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cacheInfo.put(cacheName, "Available");
                    } else {
                        cacheInfo.put(cacheName, "Not Available");
                    }
                });
                response.put("caches", cacheInfo);
                response.put("status", "OK");

                log.info("Cache manager status: {}", response);
            } else {
                response.put("status", "ERROR");
                response.put("message", "Cache manager is null");
            }

        } catch (Exception e) {
            log.error("Error checking cache status", e);
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-put")
    public ResponseEntity<Map<String, Object>> testCachePut() {
        Map<String, Object> response = new HashMap<>();

        try {
            if (cacheManager != null) {
                var cache = cacheManager.getCache("profiles");
                if (cache != null) {
                    // Test put operation
                    Map<String, Object> testData = new HashMap<>();
                    testData.put("id", 1L);
                    testData.put("email", "test@example.com");
                    testData.put("firstName", "Test");
                    testData.put("lastName", "User");

                    cache.put("1", testData);

                    // Test get operation
                    var cachedData = cache.get("1");

                    response.put("status", "OK");
                    response.put("message", "Cache put operation successful");
                    response.put("cachedData", cachedData != null ? "Data found in cache" : "No data in cache");

                    log.info("Cache test put successful");
                } else {
                    response.put("status", "ERROR");
                    response.put("message", "Cache 'profiles' not found");
                }
            } else {
                response.put("status", "ERROR");
                response.put("message", "Cache manager is null");
            }

        } catch (Exception e) {
            log.error("Error testing cache put", e);
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}