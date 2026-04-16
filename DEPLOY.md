# Production deployment

## 1. Prepare server

Install Docker and Docker Compose plugin on the server.

## 2. Prepare environment

Copy the example env file and set real secrets:

```bash
cp .env.example .env
```

Required changes in `.env`:

- set a strong `POSTGRES_PASSWORD`
- set a long Base64 `JWT_SECRET`
- set `APP_CORS_ALLOWED_ORIGINS` to your frontend domain
- if you have a domain, set `APP_DOMAIN`

Generate a JWT secret:

```bash
openssl rand -base64 64
```

## 3. Start services

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

## 4. Check status

```bash
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f app
```

The API will be available on port `80` through nginx.

## 5. Update release

```bash
git pull
docker compose -f docker-compose.prod.yml up -d --build
```

## Notes

- Spring Boot runs with profile `prod`
- PostgreSQL data is stored in Docker volume `postgres-data`
- The application is bound to `127.0.0.1:${SERVER_PORT}` and exposed publicly only through nginx
- For HTTPS, place the server behind a real domain and add Certbot or another TLS terminator
