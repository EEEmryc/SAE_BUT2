# Dockerfile for LearnHub Spring Boot API

# Use Eclipse Temurin OpenJDK 17 as base image
FROM eclipse-temurin:17-jre

# Set metadata
LABEL maintainer="LearnHub Team"
LABEL description="LearnHub API - Spring Boot Application"
LABEL version="1.0.0"

# Set working directory
WORKDIR /app

# Install curl for health checks and create application user for security
RUN apt-get update && apt-get install -y curl && \
    addgroup --system learnhub && adduser --system --ingroup learnhub learnhub && \
    rm -rf /var/lib/apt/lists/*

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