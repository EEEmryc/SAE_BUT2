# SAE_BUT2

Projet LearnHub : API Spring Boot, client React et PostgreSQL.

## Documentation

- [Architecture, analyse critique et stratégie de tests](docs/ARCHITECTURE.md)

## Lancement avec Docker

Docker Compose charge automatiquement et uniquement `Docker/.env`.
Le fichier `Docker/docker.env` est conservé comme avertissement, mais il est
obsolète.

```powershell
cd Docker
Copy-Item .env.example .env
# Modifier DB_PASSWORD et JWT_SECRET dans .env
.\start.bat
```

Services :

- Front-end après `npm run dev` : http://localhost:5173
- API : http://localhost:8081
- Swagger : http://localhost:8081/swagger-ui/index.html
- Mailpit : http://localhost:8025
- PostgreSQL Docker : `127.0.0.1:5433`

Ne pas utiliser `docker compose down -v` pour un redémarrage normal : l’option
`-v` supprime définitivement le volume PostgreSQL.

## Connexion DBeaver

Créer une nouvelle connexion PostgreSQL :

- Hôte : `127.0.0.1`
- Port : `5433`
- Base : `elearning` (sans tiret)
- Utilisateur : valeur `DB_USERNAME` de `Docker/.env`
- Mot de passe : valeur `DB_PASSWORD` de `Docker/.env`
- Schéma : `public`
- Table des utilisateurs : `public.utilisateur`

Le port `5432` appartient au PostgreSQL Windows local. Il contient une autre
base `elearning` et ne correspond pas à l’API exécutée dans Docker.

Requête de vérification dans DBeaver :

```sql
SELECT current_database(), current_schema(), version();
SELECT id, nom, prenom, email, role, statut, date_creation
FROM public.utilisateur
ORDER BY id DESC;
```

## Lancement depuis l’IDE

Le profil normal utilise PostgreSQL. H2 est réservé aux tests avec le profil
`test`.

Configurer les variables d’environnement de la configuration Spring Boot :

```text
DB_URL=jdbc:postgresql://127.0.0.1:5433/elearning?currentSchema=public
DB_USERNAME=postgres
DB_PASSWORD=<valeur de Docker/.env>
DB_SCHEMA=public
```

Puis lancer :

```powershell
.\mvnw.cmd spring-boot:run
```

## Tests et build

```powershell
.\mvnw.cmd package

cd frontend
npm test
npm run lint
npm run build
```
