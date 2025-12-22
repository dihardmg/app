package com.nutech.digitalservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration for Async processing with Virtual Threads (Project Loom)
 * Virtual Threads allow handling millions of concurrent requests with minimal resources
 *
 * Benefits of Virtual Threads:
 * - Lightweight: Can create millions of virtual threads vs thousands of platform threads
 * - Blocking operations don't block platform threads
 * - Better resource utilization for I/O-bound operations
 * - Simplified async programming model without complex reactive code
 * - Ideal for high-concurrency transaction processing
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Virtual Thread Executor for async operations
     * Uses Java 21+ Virtual Threads for high-concurrency scenarios
     *
     * Perfect for:
     * - Database operations (I/O bound)
     * - External API calls
     * - High-concurrency transaction processing
     * - Topup and payment processing
     */
    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        log.info("Initializing Virtual Thread Executor for async operations");
        log.info("Virtual Threads will be used for high-concurrency transaction processing");

        // Create executor that creates a new virtual thread for each task
        // Virtual threads are extremely lightweight (KB in memory vs MB for platform threads)
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
