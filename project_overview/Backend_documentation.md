# DWB Backend Documentation

## 1. Purpose

This document describes the backend architecture, security, database model, and API surface for the `DWD-Backend` Spring Boot application. It is focused on backend behavior only and does not describe frontend implementation.

The current backend covers:
- user registration, email verification, and login
- retailer onboarding and profile management
- KYC submission and admin review
- retailer-scoped agent/staff management
- retailer invoice/warranty template settings
- admin user and retailer management
- JWT security, file uploads, and file serving

---

## 2. Technology Stack

- Spring Boot 3.5.0
- Spring Data JPA / Hibernate
- Spring Security with JWT
- PostgreSQL 17
- Maven build
- SpringDoc OpenAPI UI
- Lombok for boilerplate reduction
- Local file storage under `./uploads`

---

## 3. Key Project Packages

- `com.dwb.auth` — registration and login endpoints
- `com.dwb.retailer` — retailer registration and profile services
- `com.dwb.kyc` — KYC submission and status tracking
- `com.dwb.admin` — admin UI endpoints for user, retailer, and KYC management
- `com.dwb.agent` — retailer-scoped agent/staff record management
- `com.dwb.template` — retailer invoice/warranty template settings
- `com.dwb.security` — JWT filter, security configuration, authentication utilities
- `com.dwb.storage` — file storage and retrieval
- `com.dwb.common` — shared DTOs and entity base classes

---

## 4. Security and Authentication

### 4.1 JWT

- JWT handling is implemented in `com.dwb.security.jwt.service.JwtService`
- Tokens include:
  - subject: user email
  - claim `roles`: list of role names
- Token lifetime: `app.jwt.expiration-ms` = `3600000` (1 hour)
- Signing algorithm: HS256 using `app.jwt.secret`

### 4.2 Request Authorization

Configured in `com.dwb.security.config.SecurityConfig`:
- Public routes:
  - `/health`
  - `/api/v1/auth/**`
  - `/api/v1/otp/**`
  - `/swagger-ui/**`
  - `/v3/api-docs/**`
  - `/swagger-ui.html`
- Admin routes require `ROLE_ADMIN`:
  - `/api/v1/admin/**`
- All other routes require authentication.

### 4.3 User identity resolution

- `com.dwb.security.util.SecurityUtils.getCurrentUserEmail()` extracts the current user's email from the authenticated JWT principal.
- Retailer-scoped services use that email to load the `User` and the linked `RetailerProfile`.

---

## 5. Request Flow

### 5.1 Retailer-specific flow

1. User registers via `/api/v1/auth/register`
2. Email OTP is verified at `/api/v1/auth/verify-email-otp`
3. Retailer profile is created via `/api/v1/retailer/register`
4. KYC documents are submitted via `/api/v1/kyc/submit`
5. Admin approves/rejects KYC via `/api/v1/admin/kyc/**`
6. Retailer can manage:
   - agents via `/api/v1/retailer/agents/**`
   - template settings via `/api/v1/retailer/templates`

### 5.2 Agent record restriction

- Agent records are not login users.
- Agents are stored as shop staff records only.
- Each agent belongs to exactly one retailer profile.
- Access is enforced by `AgentServiceImpl` using the current user's retailer profile.

### 5.3 Template settings

- Template settings are stored in `RetailerTemplateSettings`
- A retailer may save invoice and warranty template preferences and an optional store logo.
- Template settings are scoped to a retailer profile.

---

## 6. API Endpoints Summary

### 6.1 Auth endpoints

- `POST /api/v1/auth/register`
  - Body: `RegisterRequest`
- `POST /api/v1/auth/verify-email-otp`
  - Body: `VerifyEmailOtpRequest`
- `POST /api/v1/auth/login`
  - Body: `LoginRequest`
- `POST /api/v1/auth/send-phone-login-otp`
  - Body: `SendPhoneLoginOtpRequest`
- `POST /api/v1/auth/verify-phone-login-otp`
  - Body: `VerifyPhoneLoginOtpRequest`

### 6.2 Retailer endpoints

- `POST /api/v1/retailer/register`
  - Body: `RetailerRegisterRequest`
- `GET /api/v1/retailer/profile`
  - Returns retailer profile and registration details

### 6.3 KYC endpoints

- `POST /api/v1/kyc/submit`
  - Form data: `fullName`, `idType`, `idNumber`, `gstNumber`, `documents` (multipart files)
- `GET /api/v1/kyc/status`
  - Returns the current retailer's latest KYC status

### 6.4 Admin endpoints

- `GET /api/v1/admin/users`
  - List all users
- `POST /api/v1/admin/users/{userId}/block`
  - Block a user
- `POST /api/v1/admin/users/{userId}/unblock`
  - Unblock a user
- `GET /api/v1/admin/retailers`
  - List all retailer profiles with KYC status
- `GET /api/v1/admin/kyc`
  - List all KYC submissions, optional `status` filter
- `GET /api/v1/admin/kyc/{kycId}`
  - KYC detail with document paths
- `POST /api/v1/admin/kyc/{kycId}/approve`
  - Approve KYC submission
- `POST /api/v1/admin/kyc/{kycId}/reject`
  - Reject KYC with reason
- `GET /api/v1/admin/files/**`
  - Serve uploaded files from storage

### 6.5 Retailer new modules

- `GET /api/v1/retailer/agents`
  - List all agents for current retailer
- `POST /api/v1/retailer/agents`
  - Body: `BulkAgentRequest` containing one or more `AgentRequest` items
- `PUT /api/v1/retailer/agents/{agentId}`
  - Update single agent
- `DELETE /api/v1/retailer/agents/{agentId}`
  - Delete single agent
- `GET /api/v1/retailer/templates`
  - Get saved template settings for current retailer
- `POST /api/v1/retailer/templates`
  - Multipart body: `TemplateRequest` + optional `storeLogo`

---

## 7. Database Model Summary

### 7.1 Core tables

- `users`
  - `id`, `created_at`, `updated_at`
  - `full_name`, `email`, `phone_number`, `password`
  - `status`, `email_verified`, `phone_number_verified`, `unique_id`
- `user_roles`
  - `user_id`, `role`
- `retailer_profiles`
  - `user_id`, `retailer_unique_id`, `store_name`, `store_type`, `address`, `city`, `state`, `pincode`, `gst`, `pan`, `operating_hours`
- `kyc_documents`
  - `user_id`, `full_name`, `id_type`, `id_number`, `gst_number`, `status`, `rejection_reason`
- `kyc_document_files`
  - `kyc_document_id`, `file_path`
- `agents`
  - `retailer_profile_id`, `name`, `email`, `phone_number`, `access_level`, `active`
- `retailer_template_settings`
  - `retailer_profile_id`, `invoice_template_type`, `warranty_template_type`, `store_logo_path`, `primary_color`, `footer_text`

### 7.2 New modules and relationships

- `Agent` is linked to `RetailerProfile` with `retailer_profile_id`.
- Each agent is scoped to a single retailer and is protected by validation in `AgentServiceImpl`.
- `RetailerTemplateSettings` is a one-to-one record per retailer.
- Uploaded logo files are stored on disk under `./uploads` and the relative path is persisted in `store_logo_path`.

---

## 8. New Modules Details

### 8.1 Agents

- Package: `com.dwb.agent`
- Entity: `com.dwb.agent.entity.Agent`
- Repository: `com.dwb.agent.repository.AgentRepository`
- Service: `com.dwb.agent.service.AgentServiceImpl`
- Controller: `com.dwb.agent.controller.AgentController`

Key behavior:
- `createAgents` accepts `BulkAgentRequest` containing 1..n `AgentRequest` items.
- Agents are validated for unique email and phone number per retailer.
- Update and delete operations only apply to agents owned by the current retailer.

Access levels defined in `AgentAccessLevel`:
- `FULL_ADMINISTRATOR`
- `BILLING_MANAGER`
- `WARRANTY_AGENT`
- `READ_ONLY`

### 8.2 Template Settings

- Package: `com.dwb.template`
- Entity: `com.dwb.template.entity.RetailerTemplateSettings`
- Repository: `com.dwb.template.repository.RetailerTemplateSettingsRepository`
- Service: `com.dwb.template.service.RetailerTemplateServiceImpl`
- Controller: `com.dwb.template.controller.RetailerTemplateController`

Key behavior:
- A retailer can save invoice/warranty template type and branding options.
- Store logo is optional and saved through `StorageService`.
- Existing settings are updated if they already exist.
- `getTemplate` returns current settings or null if not configured.

---

## 9. Configuration

### 9.1 `application.yml`

Important settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/dwb_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
server:
  port: 8080
app:
  jwt:
    secret: mySuperSecretKeyForJwtTokenGeneration123456789
    expiration-ms: 3600000
  cors:
    allowed-origins: http://localhost:5173
  upload:
    dir: ./uploads
```

### 9.2 Docker Compose

- Service: `postgres:17`
- DB: `dwb_db`
- User: `postgres`
- Password: `postgres`
- Host port: `5433`

---

## 10. Admin User Seed SQL

The repository already includes an admin user creation script in `project_overview/commands.md`.

Use this script to create the first admin user manually. Example:

```sql
-- Create admin user
-- Password below is BCrypt hash of: Admin@123
INSERT INTO users (
    created_at,
    updated_at,
    full_name,
    email,
    phone_number,
    password,
    status,
    email_verified,
    phone_number_verified,
    unique_id
) VALUES (
    NOW(),
    NOW(),
    'Platform Admin',
    'admin@dwb.com',
    '0000000000',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ACTIVE',
    true,
    true,
    'USR000000'
);

INSERT INTO user_roles (user_id, role)
VALUES (
    (SELECT id FROM users WHERE email = 'admin@dwb.com'),
    'ADMIN'
);
```

> If the password should be changed, generate a new BCrypt hash and replace the `password` value.

---

## 11. Notes and Recommendations

- Backend-only work should avoid changing any frontend code.
- The new `agent`/`template` modules are retailer-scoped and do not create separate login accounts for agents.
- Use admin SQL only for bootstrap admin creation; regular user onboarding should use the existing auth flow.
- File uploads are stored locally under `./uploads`, and admin file retrieval is handled by `GET /api/v1/admin/files/**`.
- All business flows rely on the current user email extracted from JWT, so correct token issuance and header usage are essential.

---

## 12. Helpful references

- `project_overview/commands.md` — admin SQL and developer commands
- `DWB_Backend_Master_Handoff_Roadmap.md` — roadmap and handoff notes
- `pom.xml` — dependency and build configuration
- `src/main/resources/application.yml` — runtime configuration
- `src/main/java/com/dwb/security/config/SecurityConfig.java` — security policy

---

## 13. Backend File Locations

- `src/main/java/com/dwb/agent` — agents feature
- `src/main/java/com/dwb/template` — template feature
- `src/main/java/com/dwb/admin` — admin management
- `src/main/java/com/dwb/retailer` — retailer onboarding
- `src/main/java/com/dwb/kyc` — KYC logic
- `src/main/java/com/dwb/auth` — authentication
- `src/main/java/com/dwb/storage` — upload storage

---

*Document generated from actual source code in `DWD-Backend/src/main/java`.*
