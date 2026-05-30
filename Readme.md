# DWD Backend

Backend service for the DWD (Digital Warranty & Billing) appliance service platform.

A web-based platform for retailers to manage appliance billing, warranties, customers, and technicians. Works on desktop and mobile browsers.

---

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Build | Maven |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (HS256) |
| Containers | Docker |
| Docs | SpringDoc OpenAPI / Swagger |
| IDE | VS Code |
| DB Tool | DBeaver |

---

## Architecture

**Modular Monolith** — one Spring Boot app, domain-based packages. No microservices.

---

## Local Setup

### Prerequisites
- Java 21
- Maven
- Docker Desktop

### Start Database
```bash
docker-compose up -d
```

### Run Backend
```bash
./mvnw spring-boot:run
```

### Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

---

## Database

| Config | Value |
|---|---|
| Host | localhost |
| Port | 5433 (5432 was already occupied) |
| Database | dwb_db |
| Username | postgres |
| Password | postgres |

### Connect via psql
```bash
PGPASSWORD=postgres psql -h localhost -p 5433 -U postgres -d dwb_db
```

---

## Package Structure

```
src/main/java/com/dwb/
│
├── auth/
│   ├── login/
│   │   ├── controller/   LoginController.java
│   │   ├── dto/          LoginRequest, LoginResponse, SendPhoneLoginOtpRequest, VerifyPhoneLoginOtpRequest
│   │   └── service/      LoginService, LoginServiceImpl, PhoneLoginService, PhoneLoginServiceImpl
│   │
│   └── register/
│       ├── controller/   RegisterController.java
│       ├── dto/          RegisterRequest, VerifyEmailOtpRequest
│       ├── entity/       EmailOtp.java
│       ├── repository/   EmailOtpRepository.java
│       └── service/      RegisterService, RegisterServiceImpl
│
├── otp/
│   ├── controller/   OtpController.java
│   ├── dto/          SendPhoneOtpRequest, VerifyPhoneOtpRequest
│   ├── entity/       PhoneOtp.java, PhoneOtpType.java (REGISTRATION / LOGIN)
│   ├── repository/   PhoneOtpRepository.java
│   └── service/      OtpService, OtpServiceImpl
│
├── storage/
│   ├── service/   StorageService.java  (interface — swap local → cloud later)
│   └── impl/      LocalStorageServiceImpl.java
│
├── user/
│   ├── entity/      User.java, UserStatus.java
│   └── repository/  UserRepository.java
│
├── role/
│   └── entity/      Role.java  (RETAILER, CUSTOMER, TECHNICIAN, ADMIN)
│
├── security/
│   ├── config/      SecurityConfig.java, SwaggerConfig.java
│   └── jwt/
│       ├── filter/  JwtAuthenticationFilter.java
│       └── service/ JwtService.java
│
├── common/
│   ├── controller/  HealthController.java
│   ├── dto/         BaseResponse.java
│   └── entity/      BaseEntity.java
│
├── config/      PasswordConfig.java
└── exception/
    ├── custom/  BadRequestException.java, ResourceNotFoundException.java
    └── handler/ GlobalExceptionHandler.java
```

---

## API Endpoints

### Public (no token required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Server health check |
| POST | `/api/v1/auth/register` | Register new user (name, email, phone, password) |
| POST | `/api/v1/auth/verify-email-otp` | Verify email OTP |
| POST | `/api/v1/otp/send-phone-otp` | Send registration phone OTP |
| POST | `/api/v1/otp/verify-phone-otp` | Verify registration phone OTP → account ACTIVE |
| POST | `/api/v1/auth/login` | Email + password login → JWT |
| POST | `/api/v1/auth/send-phone-login-otp` | Phone login step 1: send OTP |
| POST | `/api/v1/auth/verify-phone-login-otp` | Phone login step 2: verify OTP → JWT |

### Protected (Bearer token required)

> All future retailer, customer, invoice, warranty, KYC APIs go here.

---

## Registration Flow

```
1. POST /api/v1/auth/register
   → User created (status: PENDING, roles: empty)
   → Email OTP printed to console

2. POST /api/v1/auth/verify-email-otp
   → emailVerified = true
   → status stays PENDING

3. POST /api/v1/otp/send-phone-otp
   → Phone OTP printed to console

4. POST /api/v1/otp/verify-phone-otp
   → phoneVerified = true
   → status = ACTIVE
   → uniqueId generated: USR000001

5. POST /api/v1/auth/login  (or phone login)
   → Returns: { token, uniqueId, roles }
```

---

## User Model

### `users` table

| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | Primary key |
| full_name | VARCHAR | Not null |
| email | VARCHAR | Unique |
| phone_number | VARCHAR | Unique |
| password | VARCHAR | BCrypt hashed |
| status | ENUM | PENDING / ACTIVE / BLOCKED |
| email_verified | BOOLEAN | Default false |
| phone_number_verified | BOOLEAN | Default false |
| unique_id | VARCHAR | USR000001 — generated after both verifications |
| created_at | TIMESTAMP | Auto |
| updated_at | TIMESTAMP | Auto |

### `user_roles` table (separate from users)

| Column | Type | Notes |
|--------|------|-------|
| user_id | BIGINT | FK → users.id |
| role | VARCHAR | RETAILER / CUSTOMER / TECHNICIAN / ADMIN |

One user can have multiple roles. Roles are added when the user selects a role (e.g. completes retailer profile).

---

## JWT

- Algorithm: HS256
- Expiry: 1 hour
- Secret: configured in `application.yml` under `app.jwt.secret`
- Token payload includes: `email` + `roles`

---

## Response Format

All APIs return the same wrapper:

```json
{
  "success": true,
  "message": "Description",
  "data": {}
}
```

Errors:
```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

---

## Development Rules

- Business logic → service layer only
- Database access → repository only
- Request/response → DTOs only (never expose entities)
- Validation → annotations on request DTOs
- Passwords → BCrypt always
- Secrets → `application.yml`, never hardcoded in Java
- CORS → configured for `http://localhost:5173` (frontend dev server)

---

## VS Code Tips

If Lombok shows red lines:
1. Install "Lombok Annotations Support" extension
2. Press `Cmd+Shift+P` → `Java: Clean Java Language Server Workspace`
3. Reload VS Code

If IDE shows "package does not exist" errors on valid imports — same fix above. The code compiles correctly with Maven even when IDE shows false errors.
