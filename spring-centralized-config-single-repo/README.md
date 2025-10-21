# Centralized Config — Single Repo (Java 22)

This is a **Maven multi-module** project with:
- `config-server` — Spring Cloud Config Server on port **8888**
- `inventory-service` — A client service that imports config from the server
- `config-repo` — A simple, file-based config repository (also works with `git`)

## Prereqs
- Java **22**
- Maven 3.9+
- (Optional) Git

## How to run (dev)
```bash
# Terminal 1: start Config Server
mvn -q -pl config-server -am spring-boot:run

# Terminal 2: start the client service
mvn -q -pl inventory-service -am spring-boot:run
```

## Verify
1) **Server catalog** of properties:
```bash
curl http://localhost:8888/application/default
curl http://localhost:8888/inventory-service/default
```

2) **Client sees externalized config**:
```bash
curl http://localhost:8080/api/info
```

You should see values coming from `config-repo` (e.g., `app.message`, `inventory.threshold`).

## Switching to a Git-backed repo
- Initialize a Git repo inside `config-repo`:
```bash
cd config-repo
git init
git add .
git commit -m "seed"
```
- Update `config-server/src/main/resources/application.yml` URI to use `file://` Git path or a remote repo.
- Restart the Config Server.

## Notes
- Uses `spring.config.import=optional:configserver:http://localhost:8888`.
- No deprecated `bootstrap.yml` required.
