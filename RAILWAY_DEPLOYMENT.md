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

### 5. Health Check

Railway will automatically monitor your application using the `/actuator/health` endpoint. The application includes:

- Health check endpoint
- Application info endpoint
- Automatic restart on failure
- Connection pooling for database efficiency

### 6. File Uploads

For production deployment:
- File uploads are stored in `/tmp/uploads` directory
- Make sure to configure persistent storage if needed for long-term file storage
- Maximum file size: 10MB per file

## 🔧 Configuration Files

### `railway.toml`
- Minimal configuration using Railway's auto-detection
- Configures health check path and restart policies
- Railway automatically detects Java/Maven project structure

### `Procfile`
- Tells Railway how to run the application
- Uses Railway's `$PORT` variable automatically

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
   - Remove invalid builder configurations
   - Use minimal configuration as shown in the example

2. **Application doesn't start**
   - Check build logs for compilation errors
   - Ensure all dependencies are available
   - Verify Java version compatibility
   - Make sure `Procfile` exists and points to correct JAR file

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