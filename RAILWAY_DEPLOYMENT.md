# Railway Deployment Guide

This guide will help you deploy the Spring Boot application to Railway.

## 🚀 Prerequisites

- Railway account
- GitHub repository with the application code
- PostgreSQL database addon in Railway

## 📋 Deployment Steps

### 1. Connect Your Repository

1. Go to [Railway Dashboard](https://railway.app/dashboard)
2. Click "New Project" → "Deploy from GitHub repo"
3. Connect your GitHub account
4. Select the repository containing this application

### 2. Configure Database

1. In your Railway project, add a PostgreSQL database:
   - Click "New Service" → "Add Database"
   - Select "PostgreSQL"
   - Railway will automatically provide database connection details

2. Railway will automatically set these environment variables:
   - `DATABASE_URL` - Full database connection URL
   - `DATABASE_USERNAME` - Database username
   - `DATABASE_PASSWORD` - Database password

### 3. Configure Environment Variables

Set these environment variables in your Railway service:

#### Required Variables
- `SPRING_PROFILES_ACTIVE` = `production`

#### Optional Variables
- `JWT_SECRET` - Your JWT secret key (minimum 512 characters recommended)
- `JWT_EXPIRATION` - Token expiration time in milliseconds (default: 43200000)
- `PORT` - Application port (default: 8081, Railway will set this automatically)
- `UPLOAD_DIR` - Directory for file uploads (default: /tmp/uploads)

### Alternative: If Maven Build is Too Slow

If the Maven build process is taking too long due to network issues, you have these alternatives:

#### Option A: Use Docker Build (Recommended for slow networks)
1. Rename `railway-docker.toml` to `railway.toml`
2. Rename `railway-dockerfile` to `Dockerfile`
3. Redeploy - this will use Docker multi-stage build with better caching

#### Option B: Use Pre-built JAR
1. Build locally: `mvn clean package -DskipTests`
2. Commit the target/digital-service-1.0.0.jar to your repository
3. Railway will skip Maven build and use the existing JAR

#### Option C: Optimize Current Maven Build
The current configuration already includes:
- Aliyun Maven mirror for faster downloads in Asia
- All test and documentation generation skipped
- Optimized JVM settings for Railway environment
- Dependency caching through Maven wrapper

### 4. Automatic Configuration

The application is pre-configured to work with Railway automatically:

#### Database Configuration
- Uses Railway's `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`
- Falls back to local development settings if Railway variables are not present

#### Base URL Configuration
- Automatically detects Railway domain using `RAILWAY_STATIC_URL` or `RAILWAY_PUBLIC_DOMAIN`
- Falls back to `http://localhost:8081` for local development

#### Swagger UI Configuration
- Swagger UI will be available at: `https://your-app.railway.app/swagger-ui.html`
- API docs at: `https://your-app.railway.app/api-docs`
- Base URL is automatically detected by SpringDoc OpenAPI

### 5. Deployment Status

**No Health Check Required** - Railway will deploy your application immediately after build completion:

- ✅ Direct deployment (no health check waiting)
- ✅ Automatic restart on failure (up to 3 retries)
- ✅ Connection pooling for database efficiency
- ✅ Fast deployment - no startup timeout

### 6. Accessing Your Application

After successful build, your application will be immediately available at:
- **API**: `https://your-app.railway.app`
- **Swagger UI**: `https://your-app.railway.app/swagger-ui.html`
- **API Documentation**: `https://your-app.railway.app/api-docs`

### 7. File Uploads

For production deployment:
- File uploads are stored in `/tmp/uploads` directory
- Make sure to configure persistent storage if needed for long-term file storage
- Maximum file size: 10MB per file

## 🔧 Configuration Files

### `railway.toml`
- Uses Railway's auto-detection for Java/Maven projects
- Configures health check path and restart policies
- Optimized health check timeout for Railway
- No custom builder configuration (avoids parse errors)

### `Procfile`
- Tells Railway how to run the application
- Uses Railway's `$PORT` variable automatically

### `system.properties`
- Specifies Java 21 runtime (better Railway support than Java 25)
- Sets Maven version for consistent builds

### `.mvn/jvm.config`
- Optimizes JVM memory settings for Railway builds
- Skips tests, javadoc, source generation, and many unnecessary Maven steps
- Configures headless mode for server environment
- Enables compilation optimization for faster builds

### `.mvn/settings.xml`
- Uses Aliyun Maven mirror for faster dependency downloads in Asia
- Enables Railway-optimized profile for better performance
- Reduces network latency significantly

### `application-production.properties`
- Production-optimized configuration
- Database connection pooling
- Optimized JPA settings
- Production logging levels
- Actuator health check configuration

## 📱 Accessing Your Application

After deployment, your application will be available at:

- **Main Application**: `https://your-app.railway.app`
- **Swagger UI**: `https://your-app.railway.app/swagger-ui.html`
- **API Documentation**: `https://your-app.railway.app/api-docs`
- **Health Check**: `https://your-app.railway.app/actuator/health`

## 🔍 Environment Variables Reference

| Variable | Description | Railway Auto-set? |
|----------|-------------|-------------------|
| `DATABASE_URL` | PostgreSQL connection URL | ✅ Yes |
| `DATABASE_USERNAME` | Database username | ✅ Yes |
| `DATABASE_PASSWORD` | Database password | ✅ Yes |
| `RAILWAY_STATIC_URL` | Your app's public URL | ✅ Yes |
| `RAILWAY_PUBLIC_DOMAIN` | Alternative domain | ✅ Yes |
| `PORT` | Application port | ✅ Yes |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | ❌ Set manually |
| `JWT_SECRET` | JWT signing secret | ❌ Recommended to set |
| `JWT_EXPIRATION` | JWT token expiration | ❌ Optional |
| `UPLOAD_DIR` | Upload directory path | ❌ Optional |

## 🐛 Troubleshooting

### Common Issues

1. **Configuration parse error**
   - Ensure `railway.toml` uses valid TOML syntax
   - Avoid custom `[build]` section with builder configuration
   - Use minimal configuration with only `[deploy]` section
   - Railway will auto-detect Java/Maven projects automatically

2. **Build timeout during dependency download**
   - Java 21 has better Railway support than Java 25
   - Build skips tests automatically for faster deployment
   - Memory settings optimized for Railway build environment
   - Maven wrapper configured for Railway environment

3. **Application doesn't start**
   - Check build logs for compilation errors
   - Ensure all dependencies are available
   - Verify Java version compatibility (now using Java 21)
   - Make sure `Procfile` exists and points to correct JAR file
   - Check that start command in railway.toml matches JAR filename

2. **Database connection issues**
   - Verify database addon is properly configured
   - Check that `DATABASE_URL` environment variable is set
   - Ensure database migrations can run

3. **Health check failing**
   - Verify `/actuator/health` endpoint is accessible
   - Check application logs for startup errors
   - Ensure port configuration is correct

4. **Swagger UI not accessible**
   - Check that application is running properly
   - Verify `/swagger-ui.html` path
   - Check SpringDoc configuration

### Logs and Monitoring

- View real-time logs in Railway dashboard
- Monitor application health and performance
- Set up alerts for downtime or errors

## 🔄 Local Development

To test locally with Railway-like configuration:

```bash
# Set environment variables similar to Railway
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL="jdbc:postgresql://localhost:5432/your_local_db"
export DATABASE_USERNAME="your_user"
export DATABASE_PASSWORD="your_password"

# Run the application
mvn spring-boot:run
```

## 📚 Additional Resources

- [Railway Documentation](https://docs.railway.app/)
- [Spring Boot on Railway](https://docs.railway.app/deploy/spring-boot)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [SpringDoc OpenAPI](https://springdoc.org/)