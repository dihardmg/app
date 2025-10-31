# Use the official Eclipse Temurin JDK 25 image as the base
FROM eclipse-temurin:25-jdk

WORKDIR /app

# Copy Maven wrapper and pom.xml for dependency caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port 8081
EXPOSE 8081

# Use shell form to handle environment variable substitution
ENTRYPOINT exec java $JAVA_OPTS -jar target/digital-service-1.0.0.jar