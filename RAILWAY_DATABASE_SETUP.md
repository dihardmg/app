# Railway Database Setup Guide

This guide helps you properly configure PostgreSQL database for Railway deployment.

## 🔍 The Problem

The error shows that `${DATABASE_URL}` is not being resolved correctly:
```
Driver org.postgresql.Driver claims to not accept jdbcUrl, ${DATABASE_URL}
```

This means the environment variable is not being set or properly formatted.

## 🛠️ Solution 1: Add PostgreSQL Service to Railway

### Step 1: Add PostgreSQL Service
1. Go to your Railway project
2. Click "New Service"
3. Select "Add Database"
4. Choose "PostgreSQL"
5. Railway will automatically create and configure the database

### Step 2: Connect Database to Your App
1. Click on your application service
2. Go to "Variables" tab
3. Click "Connect Service"
4. Select your PostgreSQL database
5. Railway will automatically add the required environment variables

### Step 3: Verify Environment Variables
After connecting, you should see these variables in your app service:

**Required Variables (automatically added by Railway):**
- `DATABASE_URL` - Full connection URL
- `DATABASE_USERNAME` - Database username
- `DATABASE_PASSWORD` - Database password
- `PGUSER` - PostgreSQL username (alternative)
- `PGPASSWORD` - PostgreSQL password (alternative)
- `RAILWAY_PRIVATE_URL` - Private database URL (alternative)

**Example of what Railway sets:**
```
DATABASE_URL=postgresql://user:password@host:port/database
DATABASE_USERNAME=user
DATABASE_PASSWORD=password
```

## 🛠️ Solution 2: Manual Environment Variable Setup

If automatic connection doesn't work, set these manually:

### Get Database Connection Details
1. Click on your PostgreSQL service
2. Go to "Connect" tab
3. Copy the connection URL

### Set Environment Variables in Your App
1. Go to your application service
2. Click "Variables" tab
3. Add these variables:

```bash
# Option A: Use full DATABASE_URL
DATABASE_URL=postgresql://user:password@host:port/database_name

# Option B: Use separate variables
DATABASE_URL=postgresql://user:password@host:port/database_name
DATABASE_USERNAME=user
DATABASE_PASSWORD=password

# Option C: Railway-specific variables
RAILWAY_PRIVATE_URL=postgresql://user:password@host:port/database_name
PGUSER=user
PGPASSWORD=mypassword
```

## 🔧 Application Configuration

The application is now configured to handle multiple Railway database URL formats:

```properties
# Try Railway-specific variables first, then fallback
spring.datasource.url=${RAILWAY_PRIVATE_URL:${DATABASE_URL:jdbc:postgresql://localhost:5432/springboot_db}}
spring.datasource.username=${PGUSER:${DATABASE_USERNAME:myuser}}
spring.datasource.password=${PGPASSWORD:${DATABASE_PASSWORD:mypassword}}
```

## ✅ Verification Steps

### 1. Check Railway Variables
After setting up, verify these variables exist:
- Go to your app service → "Variables" tab
- Confirm `DATABASE_URL` is set correctly
- Confirm database username/password are set

### 2. Test Database Connection
```bash
# Test with psql (if available)
psql $DATABASE_URL

# Or test with curl
curl -X POST https://your-app.railway.app/api/v1/registration \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","firstName":"Test","lastName":"User"}'
```

### 3. Check Application Logs
1. Go to your application service
2. Click "Logs" tab
3. Look for successful database connection messages
4. Check for any remaining database errors

## 🚨 Common Issues & Solutions

### Issue 1: "Database connection failed"
**Solution:** Verify `DATABASE_URL` format is correct
```
# Correct format:
postgresql://username:password@host:port/database

# Wrong format:
${DATABASE_URL} (not resolved)
```

### Issue 2: "Connection refused"
**Solution:**
- Ensure database service is running
- Check network connectivity between app and database
- Verify firewall settings

### Issue 3: "Authentication failed"
**Solution:**
- Verify username and password are correct
- Check database user permissions
- Ensure user has access to the specified database

## 🔄 Enable Flyway After Database Works

Once database connection is working, you can re-enable Flyway:

1. Uncomment these lines in `application-production.properties`:
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.clean-disabled=true
spring.flyway.validate-on-migrate=true
```

2. Redeploy your application

## ✅ Success Indicators

Your deployment is successful when you see:
- ✅ Application starts without database errors
- ✅ Database connection established in logs
- ✅ API endpoints respond correctly
- ✅ User registration/registration works
- ✅ Swagger UI loads without errors

## 🆘 Still Having Issues?

1. **Check Railway logs** for specific error messages
2. **Verify database service** is running and connected
3. **Test with manual database connection** using psql or other tools
4. **Contact Railway support** with error details

Once database is properly configured, your Railway deployment should work flawlessly! 🎉