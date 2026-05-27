# DWB Backend Master Handoff & Roadmap

Digital Billing & Warranty — backend project context, current implementation status, and future roadmap for the team.

## 1. Project Overview

DWB (Digital Billing & Warranty) is a web-based appliance service and warranty management platform.

The backend is being built for a responsive website that works well on desktop and mobile browsers. The backend is API-first, so the frontend can stay separate and consume REST APIs cleanly.

The platform will support these user groups:
- Retailers
- Customers
- Technicians
- Admins

## 2. Final Technical Decisions

| Component | Decision |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Build Tool | Maven |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT |
| Containerization | Docker |
| Version Control | Git + GitHub |
| IDE | VS Code |
| DB Tool | DBeaver |

## 3. Architecture Decision

### Selected Architecture: Modular Monolith

We are intentionally **not** using microservices.

Why modular monolith fits this project:
- The team is small
- Development is faster
- Debugging is easier
- Deployment is simpler
- The codebase still stays clean through domain separation
- It can evolve later if the project grows

### Project Style
- One Spring Boot application
- One PostgreSQL database
- Domain-based package separation
- Not multi-module Maven

## 4. Git / Team Workflow

Repository setup:
- Git repository already created and connected
- Team collaboration is active
- Branching strategy is in place

Branches:
- `main` → stable production-ready branch
- `develop` → integration branch
- `feature/*` → feature branches

Rules:
- Never commit directly to `main`
- Keep commits small and meaningful
- One feature per branch
- Review before merging to `develop`

Example commit messages:
- `feat: add health endpoint`
- `feat: add user registration flow`
- `fix: postgres uniqueId constraint`
- `chore: update readme`

## 5. Local Environment Setup

Completed:
- Java 21 installed
- Maven installed and using Java 21
- Docker installed
- PostgreSQL running inside Docker
- DBeaver installed
- Spring Boot application runs successfully
- Git workflow started

## 6. Database Setup

### Current Local PostgreSQL Port
`5433`

Port `5432` is already occupied on the machine, so Docker Postgres runs on `5433`.

### docker-compose.yml

```yaml
services:
  postgres:
    image: postgres:17
    container_name: dwb-postgres
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

### application.yml

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

## 7. Authentication Strategy

### Current Decision
- Single role per user
- Login via email + password
- Email OTP verification is part of onboarding
- Phone number is stored, but phone verification is postponed

This keeps the current implementation simple and stable.

### Current Onboarding Flow
```text
Register
→ Email OTP Verification
→ Account Activated
→ Login
→ Dashboard
```

### Important Rule
- Phone number is collected now
- `phoneNumberVerified` exists and defaults to `false`
- Phone verification will be added later if needed

## 8. Frontend-Driven Backend Strategy

The backend is being shaped based on the frontend flow.

Current frontend tells us:
- Retailer registration is the starting entry point
- Registration uses email OTP
- The app is a website, but must work well on mobile browsers
- Backend must support a pending → active account lifecycle
- Backend response shape should fit frontend needs

This means we build feature-by-feature, starting with the retailer onboarding flow.

## 9. Current Domain / Entity Model

### User Entity Fields
- `fullName`
- `email`
- `phoneNumber`
- `password`
- `role`
- `status`
- `emailVerified`
- `phoneNumberVerified`
- `uniqueId`
- `createdAt`
- `updatedAt`

### Important Rules
- `emailVerified` defaults to `false`
- `phoneNumberVerified` defaults to `false`
- `status` starts as `PENDING`
- `uniqueId` starts as `null`
- `uniqueId` is generated only after successful activation
- `uniqueId` must be readable and role-based

### Role-based Unique ID Format
- Retailer → `RET000001`
- Customer → `CUS000001`
- Technician → `TEC000001`
- Admin → `ADM000001`

### User Status Values
- `PENDING`
- `ACTIVE`
- `BLOCKED`

## 10. Current Implementation Status

### Already Working
- Git repository connected
- Branching workflow active
- Java 21 configured
- Maven uses Java 21
- Docker works
- PostgreSQL works on port 5433
- Spring Boot app starts successfully
- Health endpoint works
- Base response structure exists
- Global exception handling exists
- Security config exists
- Base entity exists
- Role enum exists
- User status enum exists
- User entity exists
- User repository exists
- Registration DTO exists
- Auth controller exists
- Registration service exists
- Password hashing with BCrypt is wired
- Email OTP flow is being added

### Current In-Progress Area
- Email OTP entity
- Email OTP repository
- Email OTP verification endpoint
- Account activation
- Unique ID generation after activation

## 11. Current Package Structure

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

## 12. Important Files / Concepts

### BaseEntity
Contains:
- `id`
- `createdAt`
- `updatedAt`

### BaseResponse<T>
All API responses should follow a consistent structure:

```json
{
  "success": true,
  "message": "Some message",
  "data": {}
}
```

### SecurityConfig
- CSRF disabled
- `/health` permitted publicly
- `/api/v1/auth/**` will be public
- other endpoints are protected

## 13. Current API Shape

### Health API
```http
GET /health
```

### Registration API
```http
POST /api/v1/auth/register
```

### Email OTP Verification API
```http
POST /api/v1/auth/verify-email-otp
```

## 14. Validation / DTO Strategy

We are using DTOs for request input.

Rules:
- Do not expose JPA entities directly in API responses
- Use validation annotations on request DTOs
- Keep controller logic thin
- Put business logic in services

Current register request includes:
- `fullName`
- `email`
- `phoneNumber`
- `password`
- `confirmPassword`

## 15. Password Strategy

Passwords are:
- hashed using BCrypt
- never stored in plain text

`confirmPassword` is only for request validation and is not stored.

## 16. Email OTP Flow (Current Next Major Feature)

Planned behavior:
1. User submits registration
2. Backend creates a pending user
3. Backend generates a 6-digit email OTP
4. OTP is stored with expiry
5. OTP is printed/logged for local development
6. User submits OTP
7. Backend marks email verified
8. Backend activates account
9. Backend generates role-based `uniqueId`

## 17. Unique ID Strategy

### Rules
- `uniqueId` must remain `null` initially
- It must be generated after activation
- It must be based on the database ID
- It must be unique
- It should not be generated by counting rows

### Recommended Generation Style
Example logic:
- save user first
- get database `id`
- generate ID like `RET000001`, `CUS000001`, etc.

## 18. Current Roadmap

### Phase A — Foundation and Auth Base
- Final clean package structure
- BaseResponse and global exception handling
- Validation and request DTOs
- Retailer registration API
- Email OTP generation and verification
- Login API and JWT token generation

### Phase B — Retailer Features
- Retailer profile
- Dashboard summary
- Product / appliance registration
- Customer onboarding
- Warranty registration
- Service request creation
- Billing and invoice data

### Phase C — Expansion
- Customer-side flows
- Technician assignment and service status
- Notification system
- Audit logs
- Admin controls
- Phone verification later if needed

## 19. Immediate Coding Steps

1. Finish email OTP entity/repository and verification endpoint
2. Fix any persistence constraints around `uniqueId`
3. Activate account after email OTP verification
4. Generate role-based `uniqueId`
5. Add login endpoint
6. Add JWT generation
7. Secure protected APIs
8. Start retailer dashboard APIs

## 20. Development Rules for the Team

Do:
- Keep commits small
- Use feature branches
- Use DTOs
- Use service layer for business logic
- Keep security centralized
- Keep the backend API-first

Do not:
- Use microservices now
- Overengineer early
- Expose entities directly
- Build all modules before auth
- Change tech stack mid-project

## 21. Starter Prompt for a New Chat

Use this at the start of a new chat:

```text
We are building the DWB backend from scratch using Java 21, Spring Boot 3, Maven, PostgreSQL, Docker, and Git. We are using a modular monolith architecture, not microservices. We decided on single-role-per-user for now. Phone number is stored but not verified yet. Email OTP verification is part of onboarding. The backend is running successfully, PostgreSQL is on port 5433, the health endpoint works, and the users entity is already created. Current implementation includes BaseResponse, global exception handling, security config, BaseEntity, Role enum, UserStatus enum, User entity, UserRepository, registration DTO, AuthController, registration service, BCrypt password encoding, and work in progress for email OTP flow. We want to continue building from the retailer registration/login flow according to the frontend screens.
```

## 22. Current Status Summary

The foundation is in place and the backend is ready for the next auth milestones:
- email OTP verification
- account activation
- role-based unique ID generation
- login
- JWT security
