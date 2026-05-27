# DWD Backend 

Backend service for the DWD appliance service platform.

This repository contains the backend for a web-based appliance service and warranty management platform that is usable on desktop and mobile browsers.

## Project Goal

Build a clean, production-friendly backend for:

- Retailers
- Customers
- Technicians
- Admins

The current development focus is retailer onboarding first, based on the frontend flow.

---

## Current Architecture

### Architecture Style

**Modular Monolith**

Why this choice:

- Suitable for a small team
- Easier to debug
- Faster to build
- Easier to learn and maintain
- Good for a single backend app with multiple domains
- Can later be split into microservices if truly needed

### Not Using

- Microservices
- Multi-module Maven for now

### App Style

- Single Spring Boot application
- API-first backend
- Responsive web app backend
- Built for desktop and mobile browser use

---

## Tech Stack

### Backend

- Java 21
- Spring Boot 3.5.0
- Maven
- Spring Security
- Spring Data JPA / Hibernate
- JWT authentication planned
- PostgreSQL

### Development Tools

- VS Code
- Docker
- DBeaver
- Git + GitHub

---

## Current Completed Setup

### Environment
- Java 21 configured
- Maven configured to use Java 21
- Docker configured
- PostgreSQL running via Docker
- Spring Boot application running
- Database connection working

### Git
- Git repository initialized
- Branch workflow started
- Collaborative team setup in place

### Working Infrastructure
- `/health` endpoint working
- Spring Security base configuration in place
- Base response structure created
- Exception handling created
- Core user entity created
- Users table generated

---

## Database Setup

### PostgreSQL

Docker container:

- Container name: `dwb-postgres`
- Database: `dwb_db`
- Username: `postgres`
- Password: `postgres`

### Local Port

PostgreSQL is mapped to:

```text
5433
```

because port `5432` is already in use on the machine.

### `docker-compose.yml`

```yaml
services:
  postgres:
    image: postgres:17
    container_name: dwb-postgres
    restart: always
    environment:
      POSTGRES_DB: dwb_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/dwb_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

server:
  port: 8080
```

---

## Git Workflow

### Branches

- `main` → stable production code
- `develop` → integration branch
- `feature/*` → feature branches

### Rules

- Never commit directly to `main`
- Use one feature per branch
- Keep commits small and meaningful
- Merge only after review/testing

### Commit Examples

```text
feat: add health endpoint
feat: configure security
feat: add user entity
fix: resolve postgres datasource issue
chore: setup docker postgres
```

---

## Current Architecture Structure

```text
src/main/java/com/dwb

├── auth
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── common
│   ├── controller
│   ├── dto
│   └── entity
│
├── config
├── exception
│   ├── custom
│   └── handler
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

## Authentication Strategy

### Current Decision

**Single role per user**

Why:

- Simpler implementation
- Easier JWT logic
- Easier frontend integration
- Easier authorization rules

Future multi-role support can be added later if needed.

### Current Registration Flow

```text
Register
→ Email OTP Verification
→ Account Activated
→ Login
→ Dashboard
```

### Phone Number Verification

- Phone number is collected and stored
- Phone verification is postponed for later
- `phoneNumberVerified` is kept in the model and defaults to `false`

---

## Current Domain Model

### `users` table fields

- `id`
- `full_name`
- `email`
- `phone_number`
- `password`
- `role`
- `status`
- `email_verified`
- `phone_number_verified`
- `unique_id`
- `created_at`
- `updated_at`

### Important Rules

- `email_verified` defaults to `false`
- `phone_number_verified` defaults to `false`
- `status` starts as `PENDING`
- `unique_id` starts as `NULL`
- `unique_id` is generated after successful activation
- `unique_id` must be unique and human-readable

### Status Enum

```java
PENDING
ACTIVE
BLOCKED
```

### Role Enum

```java
RETAILER
CUSTOMER
TECHNICIAN
ADMIN
```

### Unique ID Format

Generated after activation using the database id:

- `RET000001`
- `CUS000001`
- `TEC000001`
- `ADM000001`

### Prefix Mapping

| Role | Prefix |
|---|---|
| RETAILER | RET |
| CUSTOMER | CUS |
| TECHNICIAN | TEC |
| ADMIN | ADM |

---

## Current Implemented Backend Pieces

### Core
- `BaseEntity`
- `BaseResponse<T>`
- global exception handler
- custom bad request / not found exceptions

### User
- `User` entity
- `UserRepository`
- `UserStatus` enum

### Role
- `Role` enum

### Security
- `SecurityConfig`
- public `/health` endpoint
- protected backend endpoint structure

### Auth
- `RegisterRequest`
- `VerifyEmailOtpRequest`
- `AuthController`
- `AuthService`
- `AuthServiceImpl`
- `EmailOtp` entity
- `EmailOtpRepository`
- `PasswordConfig`

---

## Current API Design

### Health

```http
GET /health
```

### Registration

```http
POST /api/v1/auth/register
```

### Email OTP Verification

```http
POST /api/v1/auth/verify-email-otp
```

---

## API Response Format

All APIs should follow the same response wrapper:

```json
{
  "success": true,
  "message": "Some message",
  "data": {}
}
```

For errors:

```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

---

## Current Backend Flow

### Retailer onboarding flow

1. Retailer opens registration page
2. Backend creates a pending user record
3. Backend generates an email OTP
4. User verifies OTP
5. Backend activates the account
6. Backend generates the role-based unique ID
7. User logs in
8. JWT is issued for protected APIs

### Important Notes

- Retailer onboarding is the first flow being built
- Frontend design should drive the backend API shape
- The backend is API-first
- Responses should be frontend-friendly and consistent

---

## Current Roadmap

### Phase 1 — Foundation and Authentication Base
- Finalize clean package structure
- Base response and global exception handling
- Request DTOs and validation
- Retailer registration API
- Email OTP generation and verification
- Login API
- JWT generation and validation

### Phase 2 — Retailer Features
- Retailer profile
- Dashboard summary
- Product / appliance registration
- Customer onboarding
- Warranty registration
- Service request creation
- Billing and invoice data

### Phase 3 — Expansion
- Customer-side flows
- Technician assignment and service status
- Notification system
- Audit logs
- Admin controls
- Phone verification later if needed

---

## What the Frontend Is Telling Us

- The frontend starts with retailer registration
- The current flow is: registration → email OTP verification → success → sign in
- The backend should support a pending-to-active account lifecycle
- The app should be responsive for desktop and mobile browsers
- Backend response shape should match frontend needs
- Backend APIs should be designed around frontend screens and forms

---

## Development Rules

- Do not expose JPA entities directly in API responses
- Use DTOs for request and response payloads
- Keep business logic inside services
- Keep database access inside repositories
- Keep security logic inside the security package
- Add validation annotations to request DTOs
- Use BCrypt for password hashing
- Use consistent response wrappers
- Avoid overengineering
- Avoid microservices for now

---

## VS Code Extensions

Recommended extensions:

- Extension Pack for Java
- Spring Boot Extension Pack
- Lombok Annotations Support for VS Code
- Docker
- GitLens
- REST Client

---

## Lombok Notes

If Lombok shows red lines in VS Code:

1. Install the Lombok VS Code extension
2. Reload VS Code
3. Run Maven clean build
4. Clean the Java language server workspace
5. Make sure annotation processing is enabled

---

## Future Improvements

- Swagger / OpenAPI documentation
- Flyway database migrations
- Dockerized backend app
- Refresh token support
- CI/CD pipeline
- Production deployment
- Phone verification later
- Optional SMS provider integration later

---

## Starter Prompt for a New Chat

Use this when starting a new chat:

```text
We are building the DWD backend from scratch using Java 21, Spring Boot 3.5.0, Maven, PostgreSQL, Docker, and JWT authentication.

Architecture:
- Modular Monolith
- Single Spring Boot app
- Domain-based package structure
- No microservices

Current completed setup:
- PostgreSQL running in Docker on port 5433
- Spring Boot connected successfully
- Health API working
- SecurityConfig created
- BaseEntity implemented
- User entity implemented
- Role enum implemented
- UserStatus enum implemented
- BaseResponse structure implemented
- Global exception handling implemented
- RegisterRequest and VerifyEmailOtpRequest DTOs implemented
- AuthController and AuthService started
- Email OTP entity and repository added

Current user fields:
- fullName
- email
- phoneNumber
- password
- role
- status
- emailVerified
- phoneNumberVerified
- uniqueId

Authentication flow:
Register → Email OTP Verification → Account Activation → Login → Dashboard

Unique IDs:
RET000001
CUS000001
TEC000001

Next step:
Continue auth flow, complete email OTP verification, account activation, unique ID generation, then login and JWT implementation.
```

---

## Current Status

The backend foundation is ready and the next step is to complete the email OTP verification + account activation flow, then move on to login and JWT security.
