# ORT CRM Backend

Spring Boot backend for ORT CRM (courses, students, leads, payments, auth).

## Tech Stack

- Java 17
- Spring Boot 3
- PostgreSQL
- Docker / Docker Compose

## Project Structure

```text
.
|- src/
|  |- main/
|  `- test/
|- deploy/
|  |- compose/
|  |  |- docker-compose.local.yml
|  |  |- docker-compose.prod.yml
|  |  `- docker-compose.fullstack.yml
|  |- docker/
|  |  |- backend.Dockerfile
|  |  `- frontend.Dockerfile
|  `- nginx/
|     `- default.conf
|- .env.example
|- DEPLOY.md
`- pom.xml
```

## Environment

1. Copy env template:

```bash
cp .env.example .env
```

2. Fill real values in `.env`:

- `POSTGRES_PASSWORD`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `APP_CORS_ALLOWED_ORIGINS`

Generate JWT secret:

```bash
openssl rand -base64 64
```

## Run Modes

### Local backend + postgres

```bash
docker compose -f deploy/compose/docker-compose.local.yml up -d --build
```

- backend: `http://localhost:8080`
- postgres: `localhost:5432`

### Production (backend + postgres + nginx)

```bash
docker compose -f deploy/compose/docker-compose.prod.yml up -d --build
docker compose -f deploy/compose/docker-compose.prod.yml ps
docker compose -f deploy/compose/docker-compose.prod.yml logs -f app
```

- public entrypoint: port `80` (via nginx)
- backend is internal (not published directly)

### Optional fullstack compose (with sibling frontend repo)

Requires two sibling repos:

- `/opt/ort-crm`
- `/opt/ort-crm-frontend`

Run:

```bash
docker compose -f deploy/compose/docker-compose.fullstack.yml up -d --build
```

Ports:

- frontend: `8086`
- backend: `${BACKEND_HOST_PORT}` (default `8087`)
- postgres: `5433`

## Local Java Run (without Docker)

```bash
mvn spring-boot:run
```

Or:

```bash
mvn clean package
java -jar target/*.jar
```

## Tests

```bash
mvn test
```

