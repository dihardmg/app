package com.nutech.digitalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller untuk monitoring Virtual Threads
 * Gunakan endpoint ini untuk memverifikasi virtual threads berjalan
 */
@RestController
@RequestMapping("/api/v1/monitor")
@Tag(name = "Monitoring", description = "API untuk monitoring Virtual Threads")
public class ThreadMonitorController {

    @Operation(summary = "Check Thread Info", description = "Cek informasi thread termasuk Virtual Threads")
    @GetMapping("/threads")
    public ResponseEntity<Map<String, Object>> getThreadInfo() {
        Map<String, Object> threadInfo = new HashMap<>();

        // Get ThreadMXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        // Basic thread info
        threadInfo.put("totalThreadCount", threadMXBean.getThreadCount());
        threadInfo.put("daemonThreadCount", threadMXBean.getDaemonThreadCount());
        threadInfo.put("peakThreadCount", threadMXBean.getPeakThreadCount());
        threadInfo.put("totalStartedThreadCount", threadMXBean.getTotalStartedThreadCount());

        // Check current thread details
        Thread currentThread = Thread.currentThread();
        Map<String, Object> currentThreadInfo = new HashMap<>();
        currentThreadInfo.put("name", currentThread.getName());
        currentThreadInfo.put("id", currentThread.threadId());
        currentThreadInfo.put("isVirtual", currentThread.isVirtual());
        currentThreadInfo.put("state", currentThread.getState());
        currentThreadInfo.put("priority", currentThread.getPriority());

        threadInfo.put("currentThread", currentThreadInfo);

        // Count all live threads by type
        int platformThreadCount = 0;
        int virtualThreadCount = 0;

        Thread.getAllStackTraces().keySet().forEach(thread -> {
            if (thread.isVirtual()) {
                virtualThreadCount++;
            } else {
                platformThreadCount++;
            }
        });

        threadInfo.put("platformThreadCount", platformThreadCount);
        threadInfo.put("virtualThreadCount", virtualThreadCount);

        // JVM Info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("availableProcessors", runtime.availableProcessors());
        jvmInfo.put("freeMemory", runtime.freeMemory());
        jvmInfo.put("totalMemory", runtime.totalMemory());
        jvmInfo.put("maxMemory", runtime.maxMemory());
        threadInfo.put("jvm", jvmInfo);

        return ResponseEntity.ok(threadInfo);
    }

    @Operation(summary = "List All Threads", description = "List semua threads aktif")
    @GetMapping("/threads/list")
    public ResponseEntity<Map<String, Object>> listAllThreads() {
        Map<String, Object> result = new HashMap<>();

        var threadInfos = Thread.getAllStackTraces().keySet().stream()
                .map(thread -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("name", thread.getName());
                    info.put("id", thread.threadId());
                    info.put("isVirtual", thread.isVirtual());
                    info.put("state", thread.getState());
                    info.put("isDaemon", thread.isDaemon());
                    info.put("priority", thread.getPriority());
                    return info;
                })
                .toList();

        result.put("threads", threadInfos);
        result.put("totalThreads", threadInfos.size());

        long virtualCount = threadInfos.stream()
                .filter(t -> (Boolean) t.get("isVirtual"))
                .count();

        result.put("virtualThreadCount", virtualCount);
        result.put("platformThreadCount", threadInfos.size() - virtualCount);

        return ResponseEntity.ok(result);
    }
}
