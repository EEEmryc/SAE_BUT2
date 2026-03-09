@echo off
echo 🛑 Stopping LearnHub Project...

:: 1. Arrêt des conteneurs
docker-compose down

echo.
echo ✅ Services stopped successfully!
echo 💡 To delete all data (Database): docker-compose down -v
echo 💡 To restart: start.bat
pause