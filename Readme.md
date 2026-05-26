# DWD Backend

Backend service for the DWD appliance service platform.

---

# Current Architecture

## Architecture Style

Modular Monolith Architecture

Why?

* Easy for small teams
* Easier debugging
* Faster development
* Cleaner learning path
* Scalable enough for current project size
* Can later evolve into microservices if needed

---

# Tech Stack

## Backend

* Java 21
* Spring Boot 3.5.0
* Maven
* Spring Security
* Spring Data JPA
* JWT Authentication (planned)
* PostgreSQL

## Development Tools

* VS Code
* Docker
* DBeaver
* Git + GitHub

---

# Current Features Implemented

## Environment Setup

* Java 21 configured
* Maven configured
* Docker configured
* PostgreSQL running via Docker
* Spring Boot application running
* Database connection working

## Security Setup

* Spring Security configured
* Public `/health` endpoint added
* Base security filter chain configured

## Health API

Endpoint:

```http
GET /health
```

Sample Response:

```json
{
  "status": "UP",
  "service": "DWB Backend",
  "timestamp": "2026-05-26T13:00:00"
}
```

---

# Current Project Structure

```text
src/main/java/com/dwb

├── auth
│   ├── controller
│   ├── dto
│   └── service
│
├── common
│   └── controller
│
├── config
│
├── exception
│
├── role
│   ├── entity
│   └── repository
│
├── security
│   └── config
│
└── user
    ├── controller
    ├── dto
    ├── entity
    ├── repository
    └── service
```

---

# Database Setup

## Docker PostgreSQL

Container Name:

```text
dwb-postgres
```

Database:

```text
dwb_db
```

Username:

```text
postgres
```

Password:

```text
postgres
```

---

# Running the Project

## Start PostgreSQL

```bash
docker compose up -d
```

## Run Backend

```bash
mvn spring-boot:run
```

---

# API Testing

## Health Endpoint

```text
http://localhost:8080/health
```

---

# Git Workflow

## Main Branches

* `main` → stable production code
* `develop` → integration branch
* `feature/*` → feature branches

## Example Feature Branch

```bash
git checkout develop
git checkout -b feature/auth-system
```

## Commit Style

Examples:

```text
feat: add health endpoint
feat: configure security
fix: resolve postgres datasource issue
chore: setup docker postgres
```

---

# Next Planned Features

## Phase 1

* BaseEntity
* User entity
* Role enum
* Global exception handling
* API response structure
* Password encoding

## Phase 2

* Register API
* Login API
* JWT generation
* JWT validation filter
* Role-based authorization

## Phase 3

* Customer module
* Retailer module
* Technician module
* Service request module

---

# Important Development Rules

* Never push broken code
* Use feature branches
* Keep commits small
* Follow package structure consistently
* Do not expose entities directly in APIs
* Use DTOs for requests/responses

---

# Future Planned Improvements

* Swagger/OpenAPI documentation
* Flyway database migrations
* Dockerized backend app
* Refresh token support
* CI/CD pipeline
* Production deployment
