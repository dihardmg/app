# K6 Load Testing for Virtual Threads

This directory contains k6 load testing scripts to test and monitor virtual threads performance for the topup transaction endpoint.

## Prerequisites

1. **Install k6**
   - Windows: `choco install k6` or download from https://k6.io/release/
   - Linux: `sudo apt-get install k6` or `brew install k6`
   - Verify: `k6 version`

2. **Install jq** (for monitoring scripts)
   - Windows: `choco install jq`
   - Linux: `sudo apt-get install jq`
   - Verify: `jq --version`

3. **Start the Application**
   ```bash
   docker-compose up -d
   ```

## File Structure

```
k6/
├── topup-load-test.js          # Main k6 load test script
├── monitor-virtual-threads.sh  # Virtual threads monitoring script (Linux/Mac)
├── monitor-virtual-threads.bat # Virtual threads monitoring script (Windows)
├── run-load-test.sh            # Combined test runner (Linux/Mac)
├── run-load-test.bat           # Combined test runner (Windows)
└── README.md                   # This file
```

## Quick Start

### Option 1: Run with Combined Script (Recommended)

**Linux/Mac:**
```bash
# Run constant load test (default)
bash k6/run-load-test.sh

# Run specific scenario
bash k6/run-load-test.sh constant_load
bash k6/run-load-test.sh ramp_up
bash k6/run-load-test.sh stress_test
```

**Windows:**
```cmd
REM Run constant load test (default)
k6\run-load-test.bat

REM Run specific scenario
k6\run-load-test.bat constant_load
```

### Option 2: Run Manually

**Terminal 1 - Start Monitor:**
```bash
# Linux/Mac
export BASE_URL=http://localhost:8081
export MONITOR_INTERVAL=1
bash k6/monitor-virtual-threads.sh

# Windows
set BASE_URL=http://localhost:8081
set MONITOR_INTERVAL=1
k6\monitor-virtual-threads.bat
```

**Terminal 2 - Run Load Test:**
```bash
# Linux/Mac/Windows
export BASE_URL=http://localhost:8081
export AUTH_TOKEN=your_jwt_token_here
k6 run k6/topup-load-test.js

# Run specific scenario
k6 run --scenarios ramp_up k6/topup-load-test.js
k6 run --scenarios stress_test k6/topup-load-test.js
```

## Test Scenarios

### 1. Constant Load (constant_load)
Steady state test with fixed number of virtual users.
- **Virtual Users:** 50
- **Duration:** 2 minutes
- **Use case:** Baseline performance measurement

### 2. Ramp Up (ramp_up)
Gradual increase in load.
- **Stages:**
  - 50 VUs → 100 VUs (1 min)
  - 100 VUs → 200 VUs (30 sec)
  - Hold at 200 VUs (1 min)
- **Use case:** Test scaling behavior

### 3. Stress Test (stress_test)
Maximum load with arrival rate controller.
- **Stages:**
  - 100 req/sec → 200 req/sec (1 min)
  - 200 req/sec → 500 req/sec (30 sec)
  - Hold at 500 req/sec (30 sec)
- **Use case:** Find breaking point

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `BASE_URL` | `http://localhost:8081` | Application base URL |
| `AUTH_TOKEN` | (default token) | JWT authentication token |
| `MONITOR_INTERVAL` | `1` (seconds) | Monitoring polling interval |

### Set Custom Variables

**Linux/Mac:**
```bash
export BASE_URL=http://localhost:8081
export AUTH_TOKEN=your_token_here
export MONITOR_INTERVAL=2
```

**Windows:**
```cmd
set BASE_URL=http://localhost:8081
set AUTH_TOKEN=your_token_here
set MONITOR_INTERVAL=2
```

## Results

After test completion, results are saved to `k6-results/<timestamp>/`:

```
k6-results/20241222_143022/
├── k6-results.json         # Detailed JSON results
├── k6-results.csv          # CSV results for analysis
├── k6-summary.json         # Summary statistics
├── virtual-threads.csv     # Virtual threads monitoring data
└── virtual-threads.log     # Monitoring logs
```

## Metrics Tracked

### K6 Metrics
- `http_req_duration` - Request response time
- `http_reqs` - Total requests
- `http_req_failed` - Failed requests
- `topup_success` - Custom success rate metric
- `topup_response_time` - Custom response time metric

### Virtual Threads Metrics
- `total_threads` - Total thread count
- `virtual_threads` - Virtual thread count
- `platform_threads` - Platform thread count
- `peak_threads` - Peak thread count
- `current_is_virtual` - Whether current thread is virtual

## Analyzing Results

### View K6 Summary
```bash
# View JSON summary
cat k6-results/*/k6-summary.json | jq

# View specific metrics
cat k6-results/*/k6-summary.json | jq '.metrics'
```

### View Virtual Threads Data
```bash
# View CSV
cat k6-results/*/virtual-threads.csv

# Load in Excel/Google Sheets for visualization
```

### Generate HTML Report
```bash
# Install k6-reporter
npm install -g k6-reporter

# Generate HTML report
k6 run --out json=results.json k6/topup-load-test.js
k6-reporter results.json --output k6-report.html
```

## Expected Virtual Threads Behavior

With virtual threads enabled, you should see:

1. **High Virtual Thread Count:** 100-1000+ virtual threads during load test
2. **Low Platform Thread Count:** Should stay near CPU core count
3. **isVirtual=true:** In application logs
4. **Better Throughput:** Handle more concurrent requests

### Example Output:
```
2024-12-22_14:30:22.123 | Total: 450 | Virtual: 420 | Platform: 30 | Current Virtual: true
2024-12-22_14:30:24.456 | Total: 890 | Virtual: 850 | Platform: 40 | Current Virtual: true
2024-12-22_14:30:26.789 | Total: 1234 | Virtual: 1200 | Platform: 34 | Current Virtual: true
```

## Troubleshooting

### Server Not Responding
```
Error: Server is not running at http://localhost:8081
```
**Solution:** Start the application with `docker-compose up -d`

### Authentication Failed
```
Failed request. Status: 401
```
**Solution:** Update `AUTH_TOKEN` with valid JWT token

### High Failure Rate
```
errors: [rate>=0.05]
```
**Possible Causes:**
- Database connection pool exhausted
- Redis connection issues
- Insufficient system resources

### Virtual Threads Not Increasing
**Check:**
```bash
curl http://localhost:8081/api/v1/monitor/threads | jq '.currentThread.isVirtual'
```
Should return `true`

## Performance Benchmarks

### Expected Performance (with Virtual Threads)

| Metric | Target |
|--------|--------|
| Throughput | 1000+ req/sec |
| P95 Response Time | < 500ms |
| P99 Response Time | < 1000ms |
| Success Rate | > 95% |
| Virtual Threads | 1000+ during peak load |

### Compare: Platform Threads vs Virtual Threads

| Metric | Platform Threads | Virtual Threads |
|--------|-----------------|-----------------|
| Max Concurrent Requests | ~200 | ~10,000+ |
| Memory per Request | ~1MB | ~KB |
| Context Switching | High | Low |
| Thread Creation Cost | High | Negligible |

## Advanced Usage

### Custom Test Configuration

Edit `topup-load-test.js` to customize:

```javascript
export const options = {
  scenarios: {
    custom_test: {
      executor: 'constant-vus',
      vus: 100,              // Change virtual users
      duration: '5m',        // Change duration
      exec: 'topupTest',
    },
  },
};
```

### Distributed Testing with k6 Cloud

```bash
# Run on k6 Cloud (requires account)
k6 cloud k6/topup-load-test.js

# Run with specific cloud options
k6 cloud --project-id=123 k6/topup-load-test.js
```

## References

- [k6 Documentation](https://k6.io/docs/)
- [Java Virtual Threads Guide](https://openjdk.org/jeps//444)
- [Spring Boot Virtual Threads](https://spring.io/blog/2023/09/09/virtual-threads-in-spring-boot)
