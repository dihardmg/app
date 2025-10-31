# Railway Spring Boot Deployment Guide (YouTube Style)
🚀 Deploy Spring Boot Application to Railway Cloud Platform

## 📋 Prerequisites
- ✅ GitHub account
- ✅ Railway account (free tier available)
- ✅ Spring Boot project ready
- ✅ Basic knowledge of command line

## 🎥 Step 1: Create Railway Account

1. **Go to [railway.app](https://railway.app)**
2. **Sign up with GitHub**
3. **Verify email address**
4. **Welcome to Railway dashboard! 🎉**

## 🚀 Step 2: Deploy Your Application

### 2.1 Connect Your Repository
1. **Click "New Project"**
2. **Select "Deploy from GitHub repo"**
3. **Connect your GitHub account**
4. **Choose your repository**
5. **Select branch (usually main/master)**
6. **Click "Deploy Now"**

### 2.2 Add PostgreSQL Database
1. **After successful build, click "New Service"**
2. **Select "Add Database"**
3. **Choose PostgreSQL**
4. **Click "Add PostgreSQL"**
5. **Wait for database to be created** ⏳

### 2.3 Connect Database to Your App
1. **Click on your application service**
2. **Go to "Variables" tab**
3. **Click "Connect Service"**
4. **Select your PostgreSQL database**
5. **Railway automatically adds environment variables:**
   - `DATABASE_URL`
   - `DATABASE_USERNAME`
   - `DATABASE_PASSWORD`

## ⚙️ Step 3: Configure Environment Variables

In your application service → "Variables" tab, add these variables:

### Required Variables:
```
SPRING_PROFILES_ACTIVE=production
PORT=8081
```

### Optional Variables:
```
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=43200000
```

## 🔧 Step 4: Verify Configuration

Your configuration should look like this:

### `railway.toml` (automatically created by this guide):
```toml
[build]
builder = "nixpacks"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 120000
restartPolicyType = "on_failure"
restartPolicyMaxRetries = 5

[variables]
PORT = "8081"
SPRING_PROFILES_ACTIVE = "production"
```

### `Procfile`:
```
web: java -Xmx512m -Xms256m -jar target/digital-service-1.0.0.jar --server.port=$PORT --spring.profiles.active=production
```

## 🚀 Step 5: Deploy and Test

### 5.1 Deploy
1. **Push your changes to GitHub**
2. **Railway will automatically redeploy**
3. **Wait for deployment to complete** ⏳

### 5.2 Test Your Application
Your app will be available at:
- **Main Application**: `https://your-app-name.railway.app`
- **Swagger UI**: `https://your-app-name.railway.app/swagger-ui.html`
- **Health Check**: `https://your-app-name.railway.app/actuator/health`

### 5.3 Verify Database Connection
Test with curl or Postman:
```bash
curl -X POST https://your-app-name.railway.app/api/v1/registration \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

## 📱 Step 6: Access Your Application

### URLs:
- **API Base URL**: `https://your-app-name.railway.app/api/v1`
- **Swagger Documentation**: `https://your-app-name.railway.app/swagger-ui.html`
- **Health Check**: `https://your-app-name.railway.app/actuator/health`

### API Endpoints Available:
- `POST /api/v1/registration` - User registration
- `POST /api/v1/login` - User login
- `GET /api/v1/profile` - Get user profile
- `GET /api/v1/balance` - Get user balance
- `POST /api/v1/topup` - Top up balance
- `GET /api/v1/transaction/history` - Transaction history
- `POST /api/v1/transaction` - Make transaction
- `GET /api/v1/services` - Get available services
- `GET /api/v1/banner` - Get banner information

## 🛠️ Step 7: Troubleshooting Common Issues

### Issue 1: Build Failed
**Solution:**
- Check Java version (should be Java 21 or higher)
- Verify Maven configuration
- Check compilation errors in logs

### Issue 2: Health Check Failed
**Solution:**
- Wait longer for startup (first time can take 2-3 minutes)
- Check application logs for errors
- Verify environment variables are set correctly

### Issue 3: Database Connection Failed
**Solution:**
- Ensure PostgreSQL service is running
- Verify DATABASE_URL format
- Check database service is connected to app

### Issue 4: Port Already in Use
**Solution:**
- Railway automatically handles port mapping
- Remove hardcoded port configuration
- Use `$PORT` variable

## 📊 Step 8: Monitor Your Application

### Railway Dashboard Features:
- **Logs**: View application logs in real-time
- **Metrics**: Monitor performance
- **Settings**: Adjust resources and scaling
- **Triggers**: Set up automated deployments

### Health Check Monitoring:
- Railway automatically checks `/actuator/health`
- Application restarts if unhealthy
- Maximum 5 restart attempts

## 💰 Cost Management

### Railway Free Tier Includes:
- **$5 free credits** every month
- **500 hours** of compute time
- **100GB** of storage
- **1 PostgreSQL database** (1GB storage)

### Tips to Minimize Costs:
- Use sleep schedule for development projects
- Optimize JVM memory settings
- Regular cleanup of unused services

## 🔄 Step 9: Updates and Maintenance

### Updating Your Application:
1. **Push changes to GitHub**
2. **Railway automatically detects changes**
3. **Automatic redeployment** 🚀

### Database Updates:
- **Flyway handles migrations automatically**
- **Version your database changes**
- **Automatic rollback on failure**

## 🎯 Success Metrics

Your deployment is successful when:
- ✅ Application builds without errors
- ✅ Health check passes (`/actuator/health` returns 200)
- ✅ Database tables are created automatically
- ✅ API endpoints respond correctly
- ✅ Swagger UI loads and shows documentation
- ✅ User registration/login works with database

## 🎉 Congratulations!

🎊 **Your Spring Boot application is now running on Railway!**

### Next Steps:
1. **Test all your API endpoints**
2. **Monitor application performance**
3. **Set up backup strategies**
4. **Consider upgrading to paid plan** for production use

### Railway Benefits:
- 🚀 **Easy deployment** from GitHub
- 💾 **Managed databases**
- 🔧 **Automatic SSL certificates**
- 📊 **Built-in monitoring**
- 💰 **Generous free tier**

### Support:
- 📚 [Railway Documentation](https://docs.railway.app/)
- 💬 [Railway Discord](https://discord.gg/railway)
- 🐦 [Railway GitHub](https://github.com/railwayapp)

## 📱 Important Links

- **Your App**: `https://your-app-name.railway.app`
- **Railway Dashboard**: `https://railway.app/dashboard`
- **Documentation**: [docs.railway.app](https://docs.railway.app/)

---

**Happy deploying! 🚀 Your Spring Boot application is now running in the cloud!**