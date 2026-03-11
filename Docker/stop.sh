echo " Stopping LearnHub Docker Services..."

# Stop services
docker-compose down

echo " Services stopped successfully!"
echo ""
echo " To remove all data (including database): docker-compose down -v"
echo " To restart services: ./start.sh"