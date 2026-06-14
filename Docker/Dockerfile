# Dockerfile for LearnHub Spring Boot API

# Use Eclipse Temurin OpenJDK 17 as base image
FROM eclipse-temurin:17-jre

# Set metadata
LABEL maintainer="LearnHub Team"
LABEL description="LearnHub API - Spring Boot Application"
LABEL version="1.0.0"

# Set working directory
WORKDIR /app

# Install runtime helpers and create application user for security
RUN apt-get update && apt-get install -y curl gosu && \
    addgroup --system learnhub && adduser --system --ingroup learnhub learnhub && \
    rm -rf /var/lib/apt/lists/*

# Copy the built JAR file from target directory
COPY target/learnhub-*.jar app.jar
COPY Docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

# Prepare the persistent upload directory before Docker initializes the volume.
RUN mkdir -p /app/uploads/resources && \
    chown -R learnhub:learnhub /app /usr/local/bin/docker-entrypoint.sh && \
    chmod +x /usr/local/bin/docker-entrypoint.sh

# Expose port 8081 (configured in application.properties)
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
