import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const topupSuccessRate = new Rate('topup_success');

// Response time trends
const topupResponseTime = new Trend('topup_response_time');
const dbResponseTime = new Trend('db_response_time');

// Request counters
const successfulRequests = new Counter('successful_requests');
const failedRequests = new Counter('failed_requests');

// Test configuration options
export const options = {
  // Scenarios for load testing
  scenarios: {
    // Constant load - steady state
    constant_load: {
      executor: 'constant-vus',
      vus: 50,              // 50 virtual users
      duration: '2m',       // 2 minutes
      gracefulStop: '30s',
      exec: 'topupTest',
    },

    // Ramp up load - gradual increase
    ramp_up: {
      executor: 'ramping-vus',
      startVUs: 10,
      stages: [
        { duration: '30s', target: 50 },   // Ramp up to 50 VUs
        { duration: '1m', target: 100 },   // Ramp up to 100 VUs
        { duration: '30s', target: 200 },  // Spike to 200 VUs
        { duration: '1m', target: 200 },   // Stay at 200 VUs
        { duration: '30s', target: 0 },    // Ramp down
      ],
      gracefulRampDown: '30s',
      exec: 'topupTest',
    },

    // Stress test - maximum load
    stress_test: {
      executor: 'ramping-arrival-rate',
      startRate: 10,
      timeUnit: '1s',
      preAllocatedVUs: 500,
      stages: [
        { duration: '1m', target: 100 },   // 100 req/sec
        { duration: '1m', target: 200 },   // 200 req/sec
        { duration: '30s', target: 500 },  // Spike to 500 req/sec
        { duration: '30s', target: 500 },  // Stay at peak
        { duration: '30s', target: 0 },    // Ramp down
      ],
      exec: 'topupTest',
    },
  },

  // Thresholds for pass/fail criteria
  thresholds: {
    // HTTP errors should be less than 5%
    'errors': ['rate<0.05'],
    // 95% of requests must complete below 500ms
    'http_req_duration': ['p(95)<500'],
    // 99% of requests must complete below 1000ms
    'http_req_duration': ['p(99)<1000'],
    // Topup success rate should be above 95%
    'topup_success': ['rate>0.95'],
  },
};

// Configuration
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';
const AUTH_TOKEN = __ENV.AUTH_TOKEN || 'eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImpvaG5kb2VAZ21haWwuY29tIiwic3ViIjoiam9obmRvZUBnbWFpbC5jb20iLCJpYXQiOjE3NjY0MTc4MDIsImV4cCI6MTc2NjQ2MTAwMn0.OgwJll77Mwsdy-xqkhKlfaccaXCmt3OKtzGTENc3qyU';

// Topup amounts for realistic testing
const TOPUP_AMOUNTS = [10000, 20000, 50000, 100000, 150000, 200000];

export function setup() {
  console.log(`Starting load test against: ${BASE_URL}`);
  console.log(`Using auth token: ${AUTH_TOKEN.substring(0, 20)}...`);

  // Pre-test: check if server is responsive
  const checkResponse = http.get(`${BASE_URL}/api/v1/balance`, {
    headers: {
      'Authorization': `Bearer ${AUTH_TOKEN}`,
    },
  });

  if (checkResponse.status !== 200) {
    console.error('Server is not responding correctly. Status:', checkResponse.status);
  }

  return { startTime: new Date().toISOString() };
}

// Main topup test function
export function topupTest() {
  // Random amount for each request
  const amount = TOPUP_AMOUNTS[Math.floor(Math.random() * TOPUP_AMOUNTS.length)];

  const payload = JSON.stringify({
    top_up_amount: amount,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${AUTH_TOKEN}`,
    },
    tags: { name: 'TopupRequest' },
  };

  // Measure response time
  const startTime = new Date();

  const response = http.post(`${BASE_URL}/api/v1/topup`, payload, params);

  const responseTime = new Date() - startTime;
  topupResponseTime.add(responseTime);

  // Validate response
  const isSuccessful = check(response, {
    'status is 200': (r) => r.status === 200,
    'response has data': (r) => r.json('data') !== null,
    'balance is present': (r) => r.json('data.balance') !== undefined,
    'status code is 0': (r) => r.json('status') === 0,
    'message is correct': (r) => r.json('message') === 'Top Up Balance berhasil',
  });

  // Update metrics
  errorRate.add(!isSuccessful);
  topupSuccessRate.add(isSuccessful);

  if (isSuccessful) {
    successfulRequests.add(1);
  } else {
    failedRequests.add(1);
    console.error(`Failed request. Status: ${response.status}, Body: ${response.body}`);
  }

  // Small think time between requests (100-500ms random)
  sleep(Math.random() * 0.4);
}

export function teardown(data) {
  const endTime = new Date().toISOString();
  console.log(`Load test completed. Start: ${data.startTime}, End: ${endTime}`);
  console.log('Successful requests:', successfulRequests.values);
  console.log('Failed requests:', failedRequests.values);
}

// Alternative: Simple test function
export default function () {
  topupTest();
}
