
## Prerequisites

- Docker Desktop installed and running
- Docker Compose (included with Docker Desktop)
- Port 8081 and 5432 available on your machine

### Option 1: Using Docker Compose

1. **Navigate to the project root:**
   ```bash
   cd SAE_BUT2
   ```

2. **Build the API first:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Start the services:**
   ```bash
   cd Docker
   docker-compose up -d
   ```

4. **Verify services are running:**
   ```bash
   docker-compose ps
   ```

### Option 2: Using Scripts

**On Windows:**
```cmd
cd Docker
start.bat
```

**On Unix/macOS:**
```bash
cd Docker
chmod +x start.sh
./start.sh
```

## Configuration


1. **Check API Health:**
   ```bash
   curl http://localhost:8081/actuator/health
   ```

2. **Access Swagger Documentation:**
   Open: http://localhost:8081/swagger-ui/index.html

3. **Test Sample Login:**
   ```bash
   curl -X POST http://localhost:8081/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "email": "admin@learnhub.com",
       "password": "admin123"
     }'
   ```


## Management Commands

### Start Services
```bash
docker-compose up -d
```

### Stop Services
```bash
docker-compose down
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f learnhub-api
docker-compose logs -f learnhub-database
```

### Restart Services
```bash
docker-compose restart
```

### Rebuild API (after code changes)
```bash
# Build new JAR
../mvnw clean package -DskipTests

# Rebuild and restart API container
docker-compose up -d --build learnhub-api
```

### Database Management
```bash
# Connect to database
docker-compose exec learnhub-database psql -U postgres -d elearning

# Backup database
docker-compose exec learnhub-database pg_dump -U postgres elearning > backup.sql

# Restore database
docker-compose exec -T learnhub-database psql -U postgres -d elearning < backup.sql
```







