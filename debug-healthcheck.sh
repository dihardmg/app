#!/bin/bash

echo "=== Health Check Debug Script for Railway Deployment ==="
echo ""

# Set Railway-like environment variables for testing
export SPRING_PROFILES_ACTIVE=production
export PORT=8081
export DATABASE_URL="${DATABASE_URL:-jdbc:postgresql://localhost:5432/springboot_db}"
export DATABASE_USERNAME="${DATABASE_USERNAME:-myuser}"
export DATABASE_PASSWORD="${DATABASE_PASSWORD:-mypassword}"

echo "Environment Variables:"
echo "- SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "- PORT: $PORT"
echo "- DATABASE_URL: $DATABASE_URL"
echo "- DATABASE_USERNAME: $DATABASE_USERNAME"
echo ""

echo "=== Starting Application ==="
# Build and run the application
mvn clean package -DskipTests
java -Xmx512m -Xms256m -jar target/digital-service-1.0.0.jar --server.port=$PORT &
APP_PID=$!

echo "Application PID: $APP_PID"
echo "Waiting for application to start..."

# Wait for application to start
sleep 30

echo "=== Checking Application Status ==="
# Check if application is running
if ps -p $APP_PID > /dev/null; then
    echo "✅ Application is running (PID: $APP_PID)"
else
    echo "❌ Application failed to start"
    exit 1
fi

echo ""
echo "=== Testing Health Check ==="
# Test health check endpoint
for i in {1..10}; do
    echo "Health check attempt #$i..."
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/actuator/health)

    if [ "$HTTP_STATUS" = "200" ]; then
        echo "✅ Health check passed! (HTTP 200)"
        curl -s http://localhost:8081/actuator/health | jq .
        break
    else
        echo "❌ Health check failed (HTTP $HTTP_STATUS)"
        if [ $i -eq 10 ]; then
            echo "❌ All health check attempts failed"
            exit 1
        fi
        sleep 10
    fi
done

echo ""
echo "=== Testing Application Endpoints ==="
# Test basic endpoints
echo "Testing root endpoint..."
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" http://localhost:8081/

echo "Testing Swagger UI..."
curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" http://localhost:8081/swagger-ui.html

echo ""
echo "=== Cleanup ==="
# Kill the application
kill $APP_PID
echo "Application stopped"

echo ""
echo "=== Debugging Complete ==="
echo "If all tests passed, your application is ready for Railway deployment!"