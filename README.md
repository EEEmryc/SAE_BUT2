# LearnHub — SAE BUT2

Plateforme e-learning développée dans le cadre de la SAE de BUT2 FA.

## Documentation

- [Architecture, analyse critique et stratégie de tests](docs/ARCHITECTURE.md)
- [Configuration SMTP et Mailpit](docs/EMAIL_CONFIGURATION.md)

---

## Prérequis

- Java 21
- Maven 3.8+ (ou utiliser `./mvnw`)
- Node.js 20+ et npm
- PostgreSQL 15+ (si lancement sans Docker)
- Docker Desktop (si lancement avec Docker)

---

## Option 1 — Lancement avec Docker

### 1. Configurer l'environnement

```bash
cp Docker/.env.example Docker/.env
```

Ouvrir `Docker/.env` et renseigner au minimum :

```dotenv
DB_PASSWORD=votre_mot_de_passe
JWT_SECRET=unSecretLongEtAleatoire
```

### 2. Builder le JAR

```bash
./mvnw clean package -DskipTests
```

### 3. Démarrer les services

```bash
cd Docker
docker compose up -d
```

Cela démarre :
- `learnhub-postgres` — PostgreSQL sur le port **5433**
- `learnhub-api` — API Spring Boot sur le port **8081**

### 4. Vérifier

```bash
docker compose ps
docker compose logs -f learnhub-api
```

### Commandes utiles Docker

```bash
# Arrêter
docker compose down

# Rebuild après modification du code
./mvnw clean package -DskipTests
docker compose up -d --build learnhub-api

# Vider la base (repart de zéro)
docker compose down -v
```

---

## Option 2 — Lancement sans Docker (local)

### 1. Créer la base de données

Dans pgAdmin ou psql :

```sql
CREATE DATABASE elearning;
```

### 2. Configurer l'environnement

```bash
cp .env.example .env
```

Ouvrir `.env` et renseigner :

```dotenv
DB_URL=jdbc:postgresql://127.0.0.1:5432/elearning?currentSchema=public
DB_USERNAME=postgres
DB_PASSWORD=votre_mot_de_passe
DB_SCHEMA=public
JWT_SECRET=unSecretLongEtAleatoire
```

> Le port par défaut de PostgreSQL local est **5432**.

### 3. Charger les variables et démarrer l'API

**Windows (PowerShell) :**

```powershell
Get-Content .env | ForEach-Object {
  if ($_ -match '^([^#][^=]*)=(.*)$') {
    [System.Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
  }
}
./mvnw spring-boot:run
```

**Unix/macOS :**

```bash
export $(cat .env | xargs)
./mvnw spring-boot:run
```

> Spring Boot crée automatiquement les tables au démarrage (`ddl-auto=update`).

---

## Frontend

```bash
cd frontend
npm install
npm run dev
```

L'interface est accessible sur **http://localhost:5173**

---

## Accès à l'application

| Service | URL |
|---------|-----|
| API | http://localhost:8081 |
| Swagger UI | http://localhost:8081/swagger-ui/index.html |
| Health check | http://localhost:8081/actuator/health |
| Frontend | http://localhost:5173 |
| Page de connexion | http://localhost:5173/login |
| Page admin | http://localhost:5173/dashboard/admin/users |
| Base de données (Docker) | localhost:**5433**/elearning |
| Base de données (local) | localhost:**5432**/elearning |
| Mailpit (dev-mail) | http://localhost:8025 |

---

## Configuration des emails

Les emails (invitation, réinitialisation de mot de passe, demandes de compte) sont
désactivés par défaut (`MAIL_ENABLED=false`).

Pour les activer avec Gmail ou Mailpit, consulter :
[docs/EMAIL_CONFIGURATION.md](docs/EMAIL_CONFIGURATION.md)

---

## Tests

```bash
# Tous les tests (unitaires + intégration)
./mvnw test

# Build complet sans tests
./mvnw clean package -DskipTests
```

---

## Structure du projet

```
SAE_BUT2/
├── src/
│   ├── main/java/sae/learnhub/learnhub/
│   │   ├── api/            # Controllers, DTOs, Mappers
│   │   ├── application/    # Services, ports, exceptions
│   │   ├── domain/         # Modèles et interfaces repository
│   │   └── infrastructure/ # JPA, sécurité, configuration
│   └── test/
├── frontend/               # Application React
├── Docker/                 # Dockerfile, docker-compose, scripts
└── docs/                   # Documentation technique
```
