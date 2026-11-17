# rello-chat

A Spring Boot WebSocket chat application with profile-based database configuration, validation, and migration support.

## Key improvements

- Profile-driven database configuration:
  - `application-local.properties` for local H2 development
  - `application-test.properties` for tests
  - `application-prod.properties` for SQL Server production
- Local HTTPS uses a generated, untracked PKCS12 keystore
- Flyway migrations enabled for production
- Docker build optimized with an official Maven JDK 21 builder and smaller Java 21 runtime image
- Spring Boot 4 with Java 21

## Running locally

Create a local self-signed certificate (do not commit the generated `certs` directory):

```bash
mkdir -p certs
export SSL_KEYSTORE_PASSWORD='choose-a-local-password'
keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore certs/localhost.p12 -validity 3650 \
  -storepass "$SSL_KEYSTORE_PASSWORD" -keypass "$SSL_KEYSTORE_PASSWORD" \
  -dname "CN=localhost" -ext "SAN=DNS:localhost,IP:127.0.0.1"
```

Start the app with the local profile and the same password in the environment:

```bash
SSL_KEYSTORE_PASSWORD="$SSL_KEYSTORE_PASSWORD" \
  mvn -Dspring-boot.run.profiles=local spring-boot:run
```

Then open `https://localhost:8082`. The browser will warn that the local certificate is self-signed.

The H2 console is at `https://localhost:8082/h2-console`.

## Running tests

```bash
mvn test -Dspring.profiles.active=test
```

## Production profile

The production profile expects SQL Server environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Launch with:

```bash
mvn -Dspring-boot.run.profiles=prod spring-boot:run
```

## Docker

Build the container image:

```bash
docker build -t rello-chat .
```

Run with production profile and SQL Server settings:

```bash
docker run -e DB_URL="<jdbc-url>" -e DB_USERNAME="<user>" -e DB_PASSWORD="<password>" -p 8080:8080 rello-chat
```

## Docker Compose (local development)

A `docker-compose.yml` is provided for local development which builds the image from the repository and runs the app with the `local` profile.

1. Copy the example env file, set a local keystore password, and generate `certs/localhost.p12` as shown above:

```bash
cp .env.example .env
# edit .env to set LOCAL_PORT or DB_ variables if required
```

2. Build and start the service (rebuilds the image if required):

```bash
mvn -DskipTests package
docker compose up --build
```

3. Access the app over HTTPS:

- App: `https://localhost:${LOCAL_PORT:-8080}`
- H2 console (local profile): `https://localhost:${LOCAL_PORT:-8080}/h2-console`

Notes:
- The compose file mounts `./target` read-only so the image can use the locally-built JAR. Ensure `mvn package` has produced `target/rello.jar` before `docker compose up`.
- Secrets should be provided via a local `.env` file or CI/CD secret store; do not commit secrets to git.


## Flyway migrations

Database migrations are stored under `src/main/resources/db/migration`.
The initial schema migration is defined in `V1__create_user_tables.sql`.

## Notes

- `application.properties` contains only shared settings.
- SQL Server settings are isolated to `application-prod.properties`.
- Local and test environments do not run Flyway by default.
- Production TLS is expected to terminate at the deployment's reverse proxy by default. To enable direct Spring Boot TLS, provide `SERVER_SSL_ENABLED=true`, `SERVER_SSL_KEY_STORE`, `SERVER_SSL_KEY_STORE_PASSWORD`, and `SERVER_SSL_KEY_STORE_TYPE=PKCS12` through deployment secrets; never package a keystore in the application.

