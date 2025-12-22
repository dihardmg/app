#!/bin/bash

# Virtual Threads Monitoring Script for k6 Load Testing
# This script monitors virtual threads while k6 load test is running

set -e

# Configuration
BASE_URL="${BASE_URL:-http://localhost:8081}"
MONITOR_INTERVAL="${MONITOR_INTERVAL:-2}"  # seconds
OUTPUT_FILE="${OUTPUT_FILE:-virtual-threads-monitor-$(date +%Y%m%d_%H%M%S).csv}"
LOG_FILE="${LOG_FILE:-virtual-threads-monitor-$(date +%Y%m%d_%H%M%S).log}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Virtual Threads Monitor${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "Monitoring URL: ${GREEN}${BASE_URL}${NC}"
echo -e "Monitor Interval: ${GREEN}${MONITOR_INTERVAL}s${NC}"
echo -e "Output File: ${GREEN}${OUTPUT_FILE}${NC}"
echo -e "Log File: ${GREEN}${LOG_FILE}${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Create CSV header
echo "timestamp,total_threads,virtual_threads,platform_threads,peak_threads,daemon_threads,current_is_virtual,current_thread_name,current_thread_id,jvm_free_memory,jvm_total_memory,jvm_max_memory,jvm_available_processors" > "$OUTPUT_FILE"

# Function to fetch thread info
fetch_thread_info() {
    local response=$(curl -s "${BASE_URL}/api/v1/monitor/threads")
    echo "$response"
}

# Function to parse and display info
parse_and_display() {
    local json="$1"

    local timestamp=$(date +%Y-%m-%d_%H:%M:%S.%3N)
    local total_threads=$(echo "$json" | jq -r '.totalThreadCount // 0')
    local virtual_threads=$(echo "$json" | jq -r '.virtualThreadCount // 0')
    local platform_threads=$(echo "$json" | jq -r '.platformThreadCount // 0')
    local peak_threads=$(echo "$json" | jq -r '.peakThreadCount // 0')
    local daemon_threads=$(echo "$json" | jq -r '.daemonThreadCount // 0')
    local current_is_virtual=$(echo "$json" | jq -r '.currentThread.isVirtual // false')
    local current_thread_name=$(echo "$json" | jq -r '.currentThread.name // "N/A"')
    local current_thread_id=$(echo "$json" | jq -r '.currentThread.id // 0')
    local jvm_free_memory=$(echo "$json" | jq -r '.jvm.freeMemory // 0')
    local jvm_total_memory=$(echo "$json" | jq -r '.jvm.totalMemory // 0')
    local jvm_max_memory=$(echo "$json" | jq -r '.jvm.maxMemory // 0')
    local jvm_available_processors=$(echo "$json" | jq -r '.jvm.availableProcessors // 0')

    # Write to CSV
    echo "${timestamp},${total_threads},${virtual_threads},${platform_threads},${peak_threads},${daemon_threads},${current_is_virtual},${current_thread_name},${current_thread_id},${jvm_free_memory},${jvm_total_memory},${jvm_max_memory},${jvm_available_processors}" >> "$OUTPUT_FILE"

    # Display
    echo -e "${timestamp} | ${GREEN}Total:${NC} ${total_threads} | ${BLUE}Virtual:${NC} ${virtual_threads} | ${YELLOW}Platform:${NC} ${platform_threads} | ${GREEN}Current Virtual:${NC} ${current_is_virtual} | ${GREEN}Free Mem:${NC} $((jvm_free_memory / 1024 / 1024))MB"
}

# Trap for cleanup
trap 'echo -e "\n${YELLOW}Monitoring stopped${NC}"; echo "Results saved to: $OUTPUT_FILE"; exit 0' INT TERM

echo -e "${GREEN}Starting monitoring... (Press Ctrl+C to stop)${NC}"
echo ""

# Main monitoring loop
while true; do
    thread_info=$(fetch_thread_info)

    if [ $? -eq 0 ]; then
        parse_and_display "$thread_info" | tee -a "$LOG_FILE"
    else
        echo -e "${RED}Error fetching thread info${NC}" | tee -a "$LOG_FILE"
    fi

    sleep "$MONITOR_INTERVAL"
done
