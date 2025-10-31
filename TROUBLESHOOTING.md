# Railway Deployment Troubleshooting

This guide helps troubleshoot common issues when deploying to Railway.

## 🔍 Health Check Failing

### Symptoms
- Build succeeds but health check keeps failing
- Application starts but doesn't respond to `/actuator/health`
- Multiple "service unavailable" errors

### Common Causes & Solutions

#### 1. Port Configuration Issues
**Problem:** Application not binding to correct port
```properties
# In application-production.properties
server.port=${PORT:8081}
server.address=0.0.0.0  # Important for Railway
```

#### 2. Database Connection Issues
**Problem:** Application can't connect to Railway database
```bash
# Check Railway environment variables
echo $DATABASE_URL
echo $DATABASE_USERNAME
echo $DATABASE_PASSWORD
```

#### 3. Application Startup Timeout
**Problem:** Application takes too long to start
```toml
# In railway.toml
[deploy]
healthcheckTimeout = 120000  # 2 minutes
healthcheckWait = 180000     # 3 minutes
startTimeout = 600000        # 10 minutes
```

#### 4. Missing Actuator Configuration
**Problem:** Health endpoint not configured properly
```properties
# In application-production.properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
management.health.defaults.enabled=true
```

## 🐛 Common Error Messages

### "Service unavailable"
- Application is starting up but not ready yet
- Increase `healthcheckWait` and `startTimeout`

### "Connection refused"
- Application not binding to correct port
- Check `server.address=0.0.0.0` configuration

### "Database connection failed"
- Wrong database credentials
- Database not ready yet
- Check DATABASE_URL format

## 🔧 Testing Locally Before Deploy

### 1. Use the Debug Script
```bash
# Make executable
chmod +x debug-healthcheck.sh

# Run with Railway-like environment
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL="jdbc:postgresql://localhost:5432/springboot_db"
./debug-healthcheck.sh
```

### 2. Manual Testing
```bash
# Build and run
mvn clean package -DskipTests
java -jar target/digital-service-1.0.0.jar --server.port=8081

# In another terminal, test health endpoint
curl http://localhost:8081/actuator/health
```

## 🚀 Quick Fixes

### Fix 1: Update Railway Configuration
```toml
# railway.toml
[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 120000
healthcheckWait = 180000
startTimeout = 600000
```

### Fix 2: Ensure Proper Server Binding
```properties
# application-production.properties
server.port=${PORT:8081}
server.address=0.0.0.0
```

### Fix 3: Optimize Database Connection
```properties
# application-production.properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.maximum-pool-size=10
```

## 📱 Monitoring & Logs

### Check Railway Logs
1. Go to Railway dashboard
2. Click on your service
3. View "Logs" tab
4. Look for startup errors

### Check Environment Variables
1. Railway service → "Variables" tab
2. Verify all required variables are set
3. Check `SPRING_PROFILES_ACTIVE=production`

## 🆘 Getting Help

### Railway Documentation
- [Railway Docs](https://docs.railway.app/)
- [Spring Boot on Railway](https://docs.railway.app/deploy/spring-boot)

### Common Debugging Commands
```bash
# Test database connection
psql $DATABASE_URL

# Check application logs
tail -f logs/application.log

# Test health endpoint manually
curl -v http://your-app.railway.app/actuator/health
```

## ✅ Deployment Checklist

Before deploying to Railway:

- [ ] `SPRING_PROFILES_ACTIVE=production` is set
- [ ] Database is properly configured
- [ ] Health check endpoints are working locally
- [ ] `server.address=0.0.0.0` is configured
- [ ] Port configuration uses `${PORT}` variable
- [ ] All environment variables are properly set
- [ ] Application starts within 5 minutes locally
- [ ] Health check responds within 30 seconds locally

If all these pass, your deployment should work! 🎉