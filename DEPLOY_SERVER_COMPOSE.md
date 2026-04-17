# Server compose deployment

This setup assumes both repositories are placed side by side on the server:

- `/opt/ort-crm`
- `/opt/ort-crm-frontend`

## Run

```bash
cd /opt/ort-crm
docker compose -f docker-compose.server.yml up -d --build
```

## Stop

```bash
cd /opt/ort-crm
docker compose -f docker-compose.server.yml down
```

## Check

```bash
docker compose -f docker-compose.server.yml ps
docker compose -f docker-compose.server.yml logs -f backend
```

## Ports

- frontend: `8086`
- backend: `8087`
- postgres: `5433`

## Important

- Frontend build context is `../ort-crm-frontend`
- Backend CORS is configured for `http://38.180.36.182:8086`
- PostgreSQL host for backend stays `postgres:5432` because containers talk over the Docker network
