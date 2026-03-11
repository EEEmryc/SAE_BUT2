@echo off
echo Starting LearnHub Project...

:: 1. Vérification de Docker
docker info >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Docker is not running. Please start Docker Desktop.
    pause
    exit /b 1
)

:: 2. Compilation du JAR (si absent)
cd ..
if not exist "target\*.jar" (
    echo Building JAR file...
    call mvnw.cmd clean package -DskipTests
)

:: 3. Lancement des conteneurs
cd Docker
if not exist ".env" copy .env.example .env >nul

echo Launching Docker Compose...
docker-compose up -d --build

:: 4. Statut final
echo.
docker-compose ps
echo.
echo Project is running on: http://localhost:8081
echo Database is running on port: 5432
pause