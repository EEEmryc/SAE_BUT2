#!/bin/bash
echo "Starting LearnHub Project..."

# 1. Vérification de Docker
if ! docker info > /dev/null 2>&1; then
    echo "Docker is not running. Please start Docker Desktop."
    exit 1
fi

# 2. Compilation du JAR (si absent)
cd ..
if [ ! -f target/*.jar ]; then
    echo "Building JAR file..."
    ./mvnw clean package -DskipTests
fi

# 3. Lancement des conteneurs
cd Docker
[ ! -f .env ] && cp .env.example .env

echo "Launching Docker Compose..."
docker-compose up -d --build

# 4. Statut final
echo ""
docker-compose ps
echo ""
echo "Project is running on: http://localhost:8081"
echo "Database is running on port: 5432"