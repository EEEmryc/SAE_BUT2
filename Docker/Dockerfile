# Dockerfile for LearnHub Spring Boot API

# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set metadata
LABEL maintainer="LearnHub Team"
LABEL description="LearnHub API - Spring Boot Application"
LABEL version="1.0.0"

# Set working directory
WORKDIR /app

# Create application user for security
RUN addgroup --system learnhub && adduser --system --ingroup learnhub learnhub

# Copy the built JAR file from target directory
COPY ../target/learnhub-*.jar app.jar

# Change ownership to application user
RUN chown learnhub:learnhub app.jar

# Switch to application user
USER learnhub

# Expose port 8081 (configured in application.properties)
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]