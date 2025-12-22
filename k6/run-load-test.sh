#!/bin/bash

# Combined Load Test & Virtual Threads Monitor Runner
# This script runs k6 load test and virtual threads monitor simultaneously

set -e

# Configuration
SCENARIO="${1:-constant_load}"  # constant_load, ramp_up, stress_test
BASE_URL="${BASE_URL:-http://localhost:8081}"
AUTH_TOKEN="${AUTH_TOKEN:-eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImNhY2hlX3Rlc3RfdXNlcl8xNzY2NDEyMDM3QGV4YW1wbGUuY29tIiwic3ViIjoiY2FjaGVfdGVzdF91c2VyXzE3NjY0MTIwMzdAZXhhbXBsZS5jb20iLCJpYXQiOjE3NjY0MTIwMzksImV4cCI6MTc2NjQ1NTIzOX0.-yXGScOUtPCmKj_zrekKUNHxt3U-JG-y1EmmonFd2_w}"
MONITOR_INTERVAL="${MONITOR_INTERVAL:-1}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# Timestamp
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RESULTS_DIR="k6-results/${TIMESTAMP}"

# Create results directory
mkdir -p "$RESULTS_DIR"

echo -e "${BOLD}${BLUE}========================================${NC}"
echo -e "${BOLD}${BLUE}  K6 Load Test + Virtual Threads Monitor  ${NC}"
echo -e "${BOLD}${BLUE}========================================${NC}"
echo -e "${CYAN}Configuration:${NC}"
echo -e "  Scenario:        ${GREEN}${SCENARIO}${NC}"
echo -e "  Base URL:        ${GREEN}${BASE_URL}${NC}"
echo -e "  Monitor Interval: ${GREEN}${MONITOR_INTERVAL}s${NC}"
echo -e "  Results Dir:     ${GREEN}${RESULTS_DIR}${NC}"
echo -e "${BOLD}${BLUE}========================================${NC}"
echo ""

# Check if k6 is installed
if ! command -v k6 &> /dev/null; then
    echo -e "${RED}Error: k6 is not installed${NC}"
    echo "Install k6: https://k6.io/docs/getting-started/installation/"
    exit 1
fi

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo -e "${RED}Error: jq is not installed${NC}"
    echo "Install jq: https://stedolan.github.io/jq/download/"
    exit 1
fi

# Check if server is running
echo -e "${CYAN}Checking if server is running...${NC}"
HEALTH_CHECK=$(curl -s "${BASE_URL}/api/v1/monitor/threads" || echo "")

if [ -z "$HEALTH_CHECK" ]; then
    echo -e "${RED}Error: Server is not running at ${BASE_URL}${NC}"
    echo "Please start the application first"
    exit 1
fi

echo -e "${GREEN}Server is running!${NC}"
echo ""

# Function to cleanup background processes
cleanup() {
    echo ""
    echo -e "${YELLOW}Stopping all processes...${NC}"

    # Stop monitor if running
    if [ -n "$MONITOR_PID" ]; then
        kill $MONITOR_PID 2>/dev/null || true
        echo -e "${GREEN}Monitor stopped${NC}"
    fi

    # k6 will stop automatically

    echo -e "${GREEN}Results saved to: ${RESULTS_DIR}${NC}"
    echo -e "${BOLD}${BLUE}========================================${NC}"
    echo -e "${BOLD}${BLUE}  Test Complete!${NC}"
    echo -e "${BOLD}${BLUE}========================================${NC}"
}

trap cleanup EXIT INT TERM

# Export environment variables for scripts
export BASE_URL
export AUTH_TOKEN
export MONITOR_INTERVAL
export OUTPUT_FILE="${RESULTS_DIR}/virtual-threads.csv"
export LOG_FILE="${RESULTS_DIR}/virtual-threads.log"

# Start virtual threads monitor in background
echo -e "${CYAN}Starting Virtual Threads Monitor...${NC}"
bash k6/monitor-virtual-threads.sh &
MONITOR_PID=$!

sleep 2
echo -e "${GREEN}Monitor started (PID: ${MONITOR_PID})${NC}"
echo ""

# Run k6 load test
echo -e "${CYAN}Starting K6 Load Test (${SCENARIO})...${NC}"
echo -e "${BOLD}${BLUE}========================================${NC}"
echo ""

k6 run \
  --out json="${RESULTS_DIR}/k6-results.json" \
  --out csv="${RESULTS_DIR}/k6-results.csv" \
  --summary-export="${RESULTS_DIR}/k6-summary.json" \
  --scenarios "${SCENARIO}" \
  k6/topup-load-test.js

# k6 will finish, cleanup will be called by trap
