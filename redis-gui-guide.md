# Redis GUI Tools Implementation Guide

## Overview

Project ini menggunakan **Redis Commander** sebagai GUI tool untuk monitoring dan management Redis cache.

1. **Redis Commander** - Simple web-based Redis management tool ✅ **WORKING**

## Services Status

| Service | Port | Status | Description |
|---------|------|--------|-------------|
| **App** | 8081 | ✅ Running | Spring Boot Application |
| **PostgreSQL** | 5433 | ✅ Running | Database Server |
| **Redis** | 6379 | ✅ Running | Redis Cache Server |
| **Redis Commander** | 8082 | ✅ Running | Redis GUI Management Tool |

---

## 🚀 **Redis Commander** (Recommended - Working Perfectly)

### **Access Information**
- **URL:** http://localhost:8082
- **Username:** admin
- **Password:** admin123
- **Status:** ✅ **Fully Working**

### **Features Available**
- ✅ **Browse Redis Keys** - Visual key browser
- ✅ **Edit Key Values** - Direct value editing
- ✅ **Add/Delete Keys** - Full CRUD operations
- ✅ **CLI Interface** - Built-in Redis CLI
- ✅ **Real-time Monitoring** - Live statistics
- ✅ **Multi-Database Support** - Switch between databases

### **Quick Start**

#### **1. Access Redis Commander**
```bash
# Buka browser:
http://localhost:8082

# Login:
Username: admin
Password: admin123
```

#### **2. View Your Cache Data**
```
Login → Select "local" Redis instance → Browse Keys

You will see:
- app:cache:profiles::1
- app:cache:banners::allBanners
- app:cache:services::allServices
- test:cache:profiles::1
```

#### **3. Monitor Cache Operations**
```
CLI Tab → Enter Commands:
- KEYS app:cache:* (View all cache keys)
- GET app:cache:profiles::1 (View profile cache)
- INFO memory (Check memory usage)
- MONITOR (Real-time command monitoring)
```

### **Redis Commander Screenshots Guide**

#### **Main Dashboard**
```
Redis Commander Interface:
┌─────────────────────────────────────────────────────────┐
│ Connected: local (redis:6379)                           │
├─────────────────────────────────────────────────────────┤
│ [Keys] [CLI] [Info] [Config]                           │
│                                                         │
│ Key Browser:                                            │
│ 🔑 app:cache:profiles::1      String  112 bytes         │
│ 🔑 app:cache:banners::allBanners String  156 bytes      │
│ 🔑 app:cache:services::allServices String  234 bytes    │
│ 🔑 test:cache:profiles::1      String  89 bytes         │
└─────────────────────────────────────────────────────────┘
```

#### **Key Details View**
```
Selected Key: app:cache:profiles::1
┌─────────────────────────────────────────────────────────┐
│ Key: app:cache:profiles::1                              │
│ Type: String                                            │
│ TTL: -1 (no expiry)                                     │
│ Size: 112 bytes                                         │
│                                                         │
│ Value:                                                  │
│ {                                                       │
│   "email": "user@example.com",                          │
│   "firstName": "John",                                  │
│   "lastName": "Doe",                                    │
│   "profileImage": "http://localhost:8081/uploads/..."   │
│ }                                                       │
│                                                         │
│ [Edit] [Delete] [Set TTL]                              │
└─────────────────────────────────────────────────────────┘
```

---

## 📊 Redis Configuration Details

### **Docker Configuration**
```yaml
redis:
  image: 'redis:7-alpine'
  ports:
    - '6379:6379'
  volumes:
    - redis_data:/data
  networks:
    - app-network
  restart: unless-stopped
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
  command: redis-server --appendonly yes --maxmemory 256mb --maxmemory-policy allkeys-lru

redis-commander:
  image: rediscommander/redis-commander:latest
  ports:
    - '8082:8081'
  networks:
    - app-network
  restart: unless-stopped
  depends_on:
    - redis
  environment:
    - REDIS_HOSTS=local:redis:6379
    - HTTP_USER=admin
    - HTTP_PASSWORD=admin123
```

### **Redis Features**
- **Persistence**: AOF (Append Only File) untuk data persistence
- **Memory Management**: Max 256MB dengan LRU eviction policy
- **Health Check**: Monitoring Redis service availability
- **Data Volume**: Persistent storage untuk cache data

### **Cache Configuration**
```properties
# Redis Configuration
spring.data.redis.host=redis
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0

# Cache Configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=1800000
spring.cache.redis.cache-null-values=false
spring.cache.redis.use-key-prefix=true
spring.cache.redis.key-prefix=app:cache:
```

### **Cache TTL Settings**
| Cache Region | TTL | Description |
|-------------|-----|-------------|
| `profiles` | 1 hour | User profile data |
| `banners` | 15 minutes | Banner data |
| `services` | 10 minutes | Service list data |

---

## 🔧 **Testing Redis GUI with Application Cache**

### **1. Clear Cache & Test Flow**
```bash
# Via Redis Commander CLI:
FLUSHALL  # Clear all cache

# Test API call:
curl -X GET http://localhost:8081/api/v1/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Monitor in Redis Commander:
- See new keys appear automatically
- Watch memory usage increase
- Verify cache creation
```

### **2. Cache Hit Test**
```bash
# First call (Cache Miss):
curl -X GET http://localhost:8081/api/v1/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Second call (Cache Hit):
curl -X GET http://localhost:8081/api/v1/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Monitor in Redis Commander CLI:
MONITOR  # Watch real-time Redis operations
```

### **3. Cache Management**
```bash
# View specific cache:
GET app:cache:profiles::1

# Set TTL (1 hour):
EXPIRE app:cache:profiles::1 3600

# Check TTL:
TTL app:cache:profiles::1

# Delete specific cache:
DEL app:cache:profiles::1
```

---

## 📈 **Cache Performance Monitoring**

### **Using Redis Commander**

#### **1. Memory Usage**
```
CLI Tab → INFO memory
used_memory: 1098952 bytes (~1.06MB)
used_memory_human: 1.06M
used_memory_peak: 1234567 bytes
maxmemory: 268435456 bytes (256MB)
```

#### **2. Cache Statistics**
```
CLI Tab → INFO stats
keyspace_hits: 15
keyspace_misses: 3
total_commands_processed: 245
instantaneous_ops_per_sec: 2
```

#### **3. Connected Clients**
```
CLI Tab → INFO clients
connected_clients: 3
client_recent_max_input_buffer: 1024
client_recent_max_output_buffer: 2048
```

### **Performance Benchmarks**

#### **Test Cache Performance**
```bash
# Benchmark Redis performance:
docker exec app-redis-1 redis-benchmark -h redis -p 6379 -c 10 -n 10000

# Expected Results:
- PING_INLINE: ~85000 requests/sec
- PING_BULK: ~87000 requests/sec
- SET: ~82000 requests/sec
- GET: ~85000 requests/sec
```

---

## 🛠 **Advanced Cache Operations**

### **1. Pattern-based Operations**
```bash
# Find all profile cache:
KEYS app:cache:profiles:*

# Find all application cache:
KEYS app:cache:*

# Delete all test cache:
EVAL "return redis.call('del', unpack(redis.call('keys', 'test:*')))" 0
```

### **2. Cache Analysis**
```bash
# Check memory usage per key:
MEMORY USAGE app:cache:profiles::1

# Analyze key patterns:
SCAN 0 MATCH app:cache:* COUNT 100

# Check key types:
TYPE app:cache:profiles::1
```

### **3. Cache Optimization**
```bash
# Optimize memory usage:
MEMORY PURGE

# Check memory fragmentation:
INFO memory | grep mem_fragmentation_ratio

# Force memory defragmentation (Redis 4.0+):
MEMORY DOCTOR
```

---

## 🔍 **Troubleshooting**

### **Redis Commander Issues**

#### **1. Login Problems**
```bash
# Default credentials:
Username: admin
Password: admin123

# If forgot password, restart container:
docker-compose restart redis-commander
```

#### **2. Connection Issues**
```bash
# Check Redis connection:
docker exec app-redis-1 redis-cli ping

# Expected: PONG

# Check network:
docker network ls
docker network inspect app_app-network
```

#### **3. Performance Issues**
```bash
# Check Redis memory:
docker exec app-redis-1 redis-cli info memory

# Check slow queries:
docker exec app-redis-1 redis-cli slowlog get 10

# Restart Redis if needed:
docker-compose restart redis
```

---

## 📚 **Useful Redis Commands for Cache Management**

### **Basic Operations**
```bash
# View all cache keys
KEYS app:cache:*

# Get specific cache value
GET app:cache:profiles::1

# Set cache value
SET app:cache:test '{"name":"test"}'

# Delete cache key
DEL app:cache:test

# Check if key exists
EXISTS app:cache:profiles::1
```

### **TTL Management**
```bash
# Set TTL (1 hour)
EXPIRE app:cache:profiles::1 3600

# Check TTL
TTL app:cache:profiles::1

# Remove TTL (make persistent)
PERSIST app:cache:profiles::1
```

### **Cache Analysis**
```bash
# Get cache info
INFO keyspace

# Monitor real-time operations
MONITOR

# Check memory usage
MEMORY USAGE app:cache:profiles::1

# Scan large keysets
SCAN 0 MATCH app:cache:* COUNT 50
```

---

## 🎯 **Best Practices**

### **1. Cache Monitoring**
- ✅ **Daily:** Check memory usage in Redis Commander
- ✅ **Weekly:** Review cache hit/miss ratios
- ✅ **Monthly:** Clean up stale cache entries
- ✅ **Alert when:** Memory usage >80% of limit

### **2. Cache Management**
- ✅ Use consistent naming: `app:cache:{type}:{id}`
- ✅ Set appropriate TTL values
- ✅ Regular cleanup of test cache
- ✅ Monitor cache invalidation

### **3. Performance Optimization**
- ✅ Profile slow Redis operations
- ✅ Optimize memory usage patterns
- ✅ Use appropriate data structures
- ✅ Monitor connection pooling

---

## 🚀 **Quick Reference Commands**

```bash
# Start all services
docker-compose up -d

# Access Redis Commander
http://localhost:8082
# Username: admin, Password: admin123

# Redis CLI operations
docker exec -it app-redis-1 redis-cli

# Check cache data
docker exec app-redis-1 redis-cli keys "app:cache:*"

# Monitor Redis operations
docker exec app-redis-1 redis-cli monitor

# Check services status
docker-compose ps

# View logs
docker-compose logs redis-commander

# Restart services
docker-compose restart redis
docker-compose restart redis-commander
```

---

## 📋 **Summary**

✅ **Redis Commander** - Perfect for daily cache management
- **Web Access:** http://localhost:8082
- **Login:** admin/admin123
- **Features:** Full CRUD operations, CLI interface, monitoring

🎯 **Recommendation:** Use **Redis Commander** untuk routine cache management dan monitoring. Tool ini stabil, mudah digunakan, dan menyediakan semua fitur essential yang dibutuhkan untuk cache operations.

### **Key Benefits**
- **Distributed Cache**: Shared cache across multiple app instances
- **High Performance**: Sub-millisecond response time
- **Persistence**: Data survives restarts dengan AOF
- **Smart TTL**: Per-cache TTL configuration
- **Memory Efficient**: LRU eviction policy

Redis GUI tool sudah fully implemented dan siap untuk production use! 🎉