# DWB Backend Project - Architecture & Handoff Documentation

## Project Overview

DWB (Digital Billing & Warranty) is a web-based appliance service and warranty management platform.

The application will support:
- Retailers
- Customers
- Technicians
- Admins

The platform is being built as:
- Responsive website
- Accessible from desktop and mobile browsers
- API-first backend architecture

---

# Final Technical Decisions

## Backend Stack

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

---

# Architecture Decision

## Selected Architecture

### Modular Monolith Architecture

Reason:
- Small team (2–5 members)
- Faster development
- Easier debugging
- Easier deployment
- Easier learning curve
- Production-friendly

Microservices are intentionally NOT being used.

---

# Package Structure

```text
com.dwb

├── auth
│   ├── controller
│   ├── dto
│   └── service
│
├── common
│   ├── controller
│   ├── dto
│   └── entity
│
├── config
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

# Current Completed Setup

## Environment Setup
- Java 21 installed
- Maven installed
- Docker installed
- DBeaver installed
- Git repository initialized
- Branch workflow started

## Git Workflow
Branches:
- main
- develop
- feature/*

Rule:
- Never commit directly to main
- One feature = one branch

---

# Database Setup

## PostgreSQL
Running through Docker.

Current DB Port:
```text
5433
```

## docker-compose.yml

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

---

# application.yml

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

# Authentication Design

## Current Decision

### Single Role Per User

Reason:
- Simpler architecture
- Easier JWT implementation
- Easier frontend integration
- Easier authorization

Future multi-role support can be added later.

---

# Registration Flow

## Final Current Flow

```text
Register
→ Email OTP Verification
→ Account Activated
→ Login
→ Dashboard
```

Phone number verification is intentionally postponed.

Phone number will:
- be collected
- be stored
- remain unverified for now

---

# Frontend Driven Backend Strategy

Backend APIs and database design will follow:
- frontend flow
- frontend UX
- frontend forms
- dashboard requirements

The project is feature-driven.

---

# User Registration Frontend Fields

Current frontend registration includes:
- Full Name
- Mobile Number
- Email
- Password
- Confirm Password

OTP verification:
- Email OTP only for now

---

# Current Database Design

## users table

```text
id
full_name
email
phone_number
password
role
status
email_verified
phone_number_verified
unique_id
created_at
updated_at
```

---

# Role Enum

```java
RETAILER
CUSTOMER
TECHNICIAN
ADMIN
```

---

# User Status Enum

```java
PENDING
ACTIVE
BLOCKED
```

---

# Unique ID Design

## Decision

Every activated user receives a business-friendly unique ID.

Examples:

```text
RET000001
CUS000001
TEC000001
ADM000001
```

## Important Rules

- uniqueId starts as NULL
- generated only after successful verification/activation
- generated using database ID
- NOT generated using count()

## Prefix Mapping

| Role | Prefix |
|---|---|
| RETAILER | RET |
| CUSTOMER | CUS |
| TECHNICIAN | TEC |
| ADMIN | ADM |

---

# Current Implemented Files

## BaseEntity.java
Contains:
- id
- createdAt
- updatedAt

---

## User.java
Contains:
- fullName
- email
- phoneNumber
- password
- role
- status
- emailVerified
- phoneNumberVerified
- uniqueId

---

## UserRepository.java

Methods:
```java
findByEmail()
existsByEmail()
existsByPhoneNumber()
```

---

# Security Setup

## Current SecurityConfig

- CSRF disabled
- /health permitted publicly
- all other endpoints protected

---

# Health API

## Endpoint

```http
GET /health
```

## Current Response Structure

```json
{
  "success": true,
  "message": "Service is running",
  "data": {
    "status": "UP",
    "service": "DWB Backend"
  }
}
```

---

# Global API Response Standard

## BaseResponse<T>

All APIs must follow:

```json
{
  "success": true,
  "message": "Some message",
  "data": {}
}
```

Reason:
- frontend consistency
- cleaner API contracts
- easier error handling

---

# Important Engineering Decisions

## DO
- Small Git commits
- Feature-based development
- Domain-based packages
- DTO separation
- Validation
- JWT security
- Dockerized DB

## DO NOT
- Use microservices
- Overengineer
- Build all tables first
- Expose DB IDs publicly
- Return entities directly

---

# Current Backend Roadmap

# Phase 1 - Foundation
- Environment setup
- PostgreSQL setup
- Docker setup
- Security setup
- Health API
- User entity
- Base response structure

Status: COMPLETED

---

# Phase 2 - Authentication System

## Next Steps

### Step 18 - Global Exception Handling
Build:
- custom exceptions
- global exception handler
- validation error handling

### Step 19 - DTO Layer
Build:
- RegisterRequest
- LoginRequest
- OTP request DTOs

### Step 20 - Password Encryption
Build:
- BCrypt password encoder

### Step 21 - Retailer Registration API
Build:
- register endpoint
- validations
- pending account creation

### Step 22 - Email OTP System
Build:
- OTP entity
- OTP generation
- expiration logic
- resend logic

### Step 23 - Email Verification API
Build:
- OTP verification
- account activation
- unique ID generation

### Step 24 - Login System
Build:
- JWT generation
- authentication flow
- login endpoint

### Step 25 - JWT Security
Build:
- JWT filter
- token validation
- secured APIs

---

# Future Feature Roadmap

## Retailer Dashboard
- retailer profile
- customers
- products
- billing
- warranty registration

## Customer Module
- customer accounts
- warranty tracking
- appliances

## Technician Module
- technician assignment
- service updates
- work tracking

## Admin Module
- user management
- analytics
- reporting

---

# Important Future Decisions

## SMS Verification
Planned later:
- Twilio
- Firebase
- MSG91
- WhatsApp OTP

## Email Provider
Initially:
- Console logging
- Mailtrap

Later:
- SMTP provider

---

# VS Code Important Extensions

Required:
- Extension Pack for Java
- Spring Boot Extension Pack
- Lombok Annotations Support
- Docker
- GitLens

---

# Lombok Fix Notes

If Lombok shows errors:
1. Install Lombok extension
2. Reload VS Code
3. Run:
```bash
mvn clean install
```
4. Clean Java workspace

---

# Important Team Rules

## Rule 1
Never push broken code.

## Rule 2
One feature = one branch.

## Rule 3
Use meaningful commit messages.

Examples:
```text
feat: add user entity
feat: add health endpoint
fix: postgres connection issue
```

---

# Starter Prompt For New Chat

Use this if continuing development in a new conversation:

```text
We are building a backend for DWB (Digital Billing & Warranty) using Java 21, Spring Boot 3, Maven, PostgreSQL, Docker, and JWT authentication.

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
Global exception handling, DTO layer, registration API, BCrypt password encoding, OTP system, and JWT authentication.
```

---

# Current Status

Project foundation is stable and ready for authentication feature development.
