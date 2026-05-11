# Deployment

Infrastructure files are grouped under `deploy/`:

- `deploy/docker/backend.Dockerfile`
- `deploy/docker/frontend.Dockerfile`
- `deploy/compose/docker-compose.local.yml`
- `deploy/compose/docker-compose.prod.yml`
- `deploy/compose/docker-compose.fullstack.yml`
- `deploy/nginx/default.conf`

## 1. Prepare environment

```bash
cp .env.example .env
```

Required changes in `.env`:

- set strong `POSTGRES_PASSWORD` and `SPRING_DATASOURCE_PASSWORD`
- set a real Base64 `JWT_SECRET`
- set `APP_CORS_ALLOWED_ORIGINS` to your frontend domain(s)

Generate secret:

```bash
openssl rand -base64 64
```

## 2. Production (backend + postgres + nginx)

```bash
docker compose -f deploy/compose/docker-compose.prod.yml up -d --build
docker compose -f deploy/compose/docker-compose.prod.yml ps
docker compose -f deploy/compose/docker-compose.prod.yml logs -f app
```

Public entrypoint: `http://<server-ip>:80`.
Backend is not exposed directly to host, only through nginx.

## 3. Local backend development

```bash
docker compose -f deploy/compose/docker-compose.local.yml up -d --build
```

- backend: `http://localhost:8080`
- postgres: `localhost:5432`

## 4. Optional fullstack compose (two sibling repos)

This mode assumes both repositories are side by side:

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
