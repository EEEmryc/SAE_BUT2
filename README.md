# SAE_BUT2
Repository contenant tout le contenu de la première partie du travail collaboratif SAE de BUT2 FA

### To run: docker-compose up -d
The easiest way to run LearnHub is using Docker:

### Mettre les bones mot de passe ( mot de passe de votre postgres) 
### voilà les fichiers à modifier :
# Docker :
- docker.env : 
  - POSTGRES_PASSWORD=your_postgres_password
  - DB_PASSWORD=your_DB_password 
- docker-compose.yml : 
  - POSTGRES_PASSWORD: your_postgres_password
  - SPRING_DATASOURCE_PASSWORD: your DB_password
- application.properties : 
  - spring.datasource.password= your_DB_password
```bash
# Navigate to project root
cd SAE_BUT2

# Build the application
./mvnw clean package -DskipTests

# Start with Docker
cd Docker
./start.sh    # On Unix/macOS
./start.bat   # On Windows
```
# une fois l'applications Docker est lancer : 
- Pour démarer proprement, on fais les étapes siuvantes : 
```bash 
# 1 - allez sur le repertoire de Docker : 
cd Docker
# 2- on va netoyer le docker, ( vider les volumes) : attention !!! celà supprime  le contenue de la base de données :
docker-compose down -v
# 3- Reconstrurie le fichier jar de l'application :
cd SAE_BUT2
./mvnw clean package -DskipTests
# 4- relancer le docker :
cd Docker
docker-compose up --build -d

```

Access the application:
- **API:** http://localhost:8081
- **Swagger UI:** http://localhost:8081/swagger-ui/index.html
- **Database:** localhost:5432/elearning

##  Manual Database Setup (Alternative)

Les étapes pour lancer la base de données : 
1- Créeer la base de données (elearninbg) : la commande à taper:
  # psql -U postgres -f src/main/resources/create_database_only.sql 
  le fichier : "create_database_only.sql " c'est le script de créations de la base de donnée "elearning" 
  # cela va demander de se seconnecter à Postgres(mettre le mot de passe de votre postgres).
2- Exécuter la deuxième commande : 
  # psql -U postgres -f src/main/resources/create_database.sql 
  le fichier : "create_database.sql " c'est le script de créations des tables de la base de donnée "elearning"
3- une fois c'es fait, on peut se connecter à la base via par exemple: DB Eaver, Data Grip,...

## Development

### Prerequisites
- Java 17
- Maven 3.8+
- PostgreSQL 15+ (if not using Docker)
- Docker Desktop (for containerized setup)

### Build and Run
```bash
# Build
./mvnw clean package

# Run API (requires database)
./mvnw spring-boot:run
```

