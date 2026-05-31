# DWB Backend — Master Handoff & Roadmap

**Last updated:** 2026-05-30
**Backend developer:** Janesh (backend lead)
**Frontend team:** Separate team (dbw-frontend, React + TypeScript + Vite)

---

## HOW TO USE THIS FILE

If you are starting a new chat or a new developer is joining, read this file top to bottom. It contains every decision made, what is already built, what is next, and a ready-to-use context prompt at the bottom. You should be ready to build the next step without asking for background.

---

## 1. What Is This Project?

**DWD (Digital Warranty & Billing)** — a web platform for appliance retailers to:
- Onboard customers
- Generate bills and invoices
- Manage warranties
- Assign technicians for service requests
- Track rewards and payments

The platform has 4 user types: **Retailer, Customer, Technician, Admin**.

We are building the **backend only**. The frontend is built separately by another team (React + TypeScript, lives in `dbw-frontend/` folder).

---

## 2. Architecture Decisions (Final, Not Changing)

| Decision | Choice | Why |
|----------|--------|-----|
| Architecture | Modular Monolith | Small team, easier to build and debug, can split later |
| Language | Java 21 | |
| Framework | Spring Boot 3.5.0 | |
| Build | Maven | |
| Database | PostgreSQL 17 | |
| ORM | Spring Data JPA / Hibernate | |
| Security | Spring Security + JWT (HS256) | |
| Containers | Docker | |
| API Docs | SpringDoc OpenAPI / Swagger | |

**We do NOT use:** microservices, multi-module Maven, GraphQL.

---

## 3. Key Design Decisions Made in Chat (Important for Future Developer)

### 3.1 Multi-Role Users
- One user account can have **multiple roles** (RETAILER, CUSTOMER, TECHNICIAN, ADMIN)
- Roles are stored in a **separate `user_roles` table** (not a column on `users`)
- `user_roles` table: `user_id (FK) | role (VARCHAR)`
- Why separate table: easy to query by role, JPA handles it via `@ElementCollection`, no schema change when adding future roles
- Roles are **empty at registration** — user picks role after full verification
- Role is added to the user record when they complete a role-specific profile (e.g. retailer profile)

### 3.2 Registration Flow (Two-Step Verification)
Registration requires BOTH email AND phone verification before account becomes ACTIVE:

```
POST /api/v1/auth/register          → user saved (PENDING, empty roles)
POST /api/v1/auth/verify-email-otp  → emailVerified = true (still PENDING)
POST /api/v1/otp/send-phone-otp     → OTP sent to phone
POST /api/v1/otp/verify-phone-otp   → phoneVerified = true → ACTIVE → uniqueId generated
POST /api/v1/auth/login             → returns { token, uniqueId, roles }
```

### 3.3 UniqueId Format
- Generated only after BOTH email + phone verified
- Format: `USR000001` (generic, not role-based)
- Why not role-based: role is not known at registration time
- Role-specific IDs (like `RET000001`) will live on the individual profile entities (RetailerProfile, etc.), not on the User

### 3.4 OTP Strategy
- Currently: OTP is printed to **console** (development mode)
- All OTPs are saved in DB — required for verification (backend must compare what it generated vs what user submitted)
- Tables: `email_otps`, `phone_otps`
- Future production plan: WhatsApp OTP via Twilio (cheaper + better delivery in India), Resend.com for email
- `PhoneOtpType` enum: `REGISTRATION` (used during signup), `LOGIN` (used during phone login) — same table, different type

### 3.5 File Storage Strategy
- Currently: local disk (`./uploads/` folder)
- Abstracted via `StorageService` interface + `LocalStorageServiceImpl`
- Future: swap to **Cloudflare R2** (10GB free forever, no egress fees, S3-compatible) — just add `R2StorageServiceImpl`, zero other code changes
- File uploads needed for: KYC documents

### 3.6 CORS
- Configured for `http://localhost:5173` (frontend dev server)
- Config in `application.yml` under `app.cors.allowed-origins`
- Update this value for production deployment

### 3.7 JWT
- Secret lives in `application.yml` under `app.jwt.secret` (not hardcoded in Java)
- Expiry: 1 hour (`app.jwt.expiration-ms: 3600000`)
- Token payload: `email` + `roles` list
- Algorithm: HS256

### 3.8 Build Order (Agreed)
Build **Retailer flow completely first**, then Customer, Technician, Admin.
The frontend is ahead of the backend — backend must catch up to match frontend screens.

---

## 4. Database Setup

| Config | Value |
|--------|-------|
| Container | `dwb-postgres` (Docker) |
| Port | 5433 (5432 was already occupied on dev machine) |
| Database | `dwb_db` |
| Username | `postgres` |
| Password | `postgres` |

`ddl-auto: update` — Hibernate auto-creates/updates tables. **It never drops columns.** If you remove a field from an entity, manually drop the column in DB (example: `ALTER TABLE users DROP COLUMN IF EXISTS role;` — this was already needed and done in Phase 1).

---

## 5. Current Package Structure

```
src/main/java/com/dwb/
│
├── auth/
│   ├── login/
│   │   ├── controller/   LoginController.java
│   │   ├── dto/          LoginRequest, LoginResponse
│   │   │                 SendPhoneLoginOtpRequest, VerifyPhoneLoginOtpRequest
│   │   └── service/      LoginService, LoginServiceImpl
│   │                     PhoneLoginService, PhoneLoginServiceImpl
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
│   ├── entity/       PhoneOtp.java, PhoneOtpType.java
│   ├── repository/   PhoneOtpRepository.java
│   └── service/      OtpService, OtpServiceImpl
│
├── storage/
│   ├── service/   StorageService.java  (interface)
│   └── impl/      LocalStorageServiceImpl.java
│
├── user/
│   ├── entity/      User.java, UserStatus.java
│   └── repository/  UserRepository.java
│
├── role/
│   └── entity/      Role.java
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
    ├── custom/  BadRequestException, ResourceNotFoundException
    └── handler/ GlobalExceptionHandler
```

**Adding a new module** (e.g. retailer): create `retailer/` folder with `controller/`, `dto/`, `entity/`, `repository/`, `service/` inside. Follow the same pattern.

---

## 6. Completed API Endpoints

### Public (no token needed)

| Method | URL | What it does |
|--------|-----|--------------|
| GET | `/health` | Server health check |
| POST | `/api/v1/auth/register` | Register: fullName, email, phoneNumber, password, confirmPassword |
| POST | `/api/v1/auth/verify-email-otp` | Verify email OTP |
| POST | `/api/v1/otp/send-phone-otp` | Send registration phone OTP |
| POST | `/api/v1/otp/verify-phone-otp` | Verify phone OTP → account ACTIVE + uniqueId generated |
| POST | `/api/v1/auth/login` | Email + password → JWT token |
| POST | `/api/v1/auth/send-phone-login-otp` | Phone login step 1: send OTP |
| POST | `/api/v1/auth/verify-phone-login-otp` | Phone login step 2: OTP → JWT token |

### Login Response Shape
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJ...",
    "uniqueId": "USR000001",
    "roles": ["RETAILER"]
  }
}
```

---

## 7. Database Tables (Auto-created by Hibernate)

| Table | Purpose |
|-------|---------|
| `users` | Core user accounts |
| `user_roles` | user_id + role (multi-role support) |
| `email_otps` | Email OTP records for verification |
| `phone_otps` | Phone OTP records (registration + login) |

---

## 8. What Needs to Be Built Next

### Phase 2 — Retailer Onboarding (NEXT TO BUILD)

The frontend has these retailer portal pages ready. Backend APIs needed:

#### 8.1 Retailer Profile
```
POST /api/v1/retailer/register
Auth: Bearer token
Body: {
  storeName, storeType, ownerName, phone, email,
  address, city, state, pincode, gst, pan, operatingHours
}
Response: { retailerId, uniqueId: "RET000001" }
```
- Creates `RetailerProfile` entity linked to `User`
- Adds `RETAILER` role to `user_roles`
- Generates `RET000001` style ID on the profile

Note: phone and email on retailer form are the same as the user's registered credentials. Frontend will show them read-only (pre-filled, disabled) if already verified.

#### 8.2 KYC Submission
```
POST /api/v1/kyc/submit
Auth: Bearer token
Body: multipart/form-data {
  fullName, idType (Aadhaar/PAN/Passport), idNumber, gstNumber, file (document image)
}
Response: { kycId, status: "PENDING" }
```
- Creates `KycDocument` entity
- Saves file via `StorageService.store(file, "kyc/{userId}")`
- KYC status: PENDING → APPROVED / REJECTED (admin approves later)

```
GET /api/v1/kyc/status
Auth: Bearer token
Response: { status, submittedAt }
```

#### 8.3 New Entities Needed
- `RetailerProfile` — linked to `User`, stores store details
- `KycDocument` — linked to `User`, stores KYC info + file path

### Phase 3 — Core Business Flow

```
Customer:
  POST /api/v1/customers               → create customer
  GET  /api/v1/customers/search        → search by phone/email
  POST /api/v1/customers/{id}/send-otp
  POST /api/v1/customers/{id}/verify-otp

Invoice:
  POST /api/v1/invoices                → create with line items
  GET  /api/v1/invoices                → list for this retailer
  GET  /api/v1/invoices/{id}           → single invoice detail

Warranty:
  POST /api/v1/warranties              → create linked to invoice
  GET  /api/v1/warranties              → list for this retailer

Dashboard:
  GET  /api/v1/dashboard/summary       → counts + recent transactions
```

New entities: `Customer`, `Invoice`, `InvoiceItem`, `Warranty`

### Phase 4 — Management Features

```
Agents (technicians under a retailer):
  GET  /api/v1/agents
  POST /api/v1/agents

Templates:
  GET  /api/v1/templates
  POST /api/v1/templates
```

New entities: `Agent`, `Template`

### Phase 5 — Customer & Technician Flows

After retailer flow is complete, introduce customer-facing and technician-facing APIs.

---

## 9. Future Technical Improvements (Plan For Later)

| Item | When | Notes |
|------|------|-------|
| Real email sending | After retailer flow | Use **Resend.com** (3000 emails/month free) |
| WhatsApp OTP | Before production | Twilio WhatsApp sandbox (cheaper than SMS in India) |
| File storage → cloud | Before production | **Cloudflare R2** (10GB free forever, S3-compatible, just add `R2StorageServiceImpl`) |
| Flyway migrations | Before production | Replace `ddl-auto: update` with proper migration scripts |
| Refresh tokens | After core features | JWT refresh token flow |
| Role-based endpoint protection | Phase 2 start | Use `@PreAuthorize("hasRole('RETAILER')")` on protected endpoints |
| Admin panel APIs | Phase 5 | KYC approval, fraud detection, monitoring |
| Dockerize backend app | Before production | Currently only DB is in Docker |
| CI/CD | Before production | GitHub Actions |

---

## 10. Entity Roadmap

| Entity | Phase | Status | Linked To |
|--------|-------|--------|-----------|
| `User` | 1 | ✅ Done | — |
| `EmailOtp` | 1 | ✅ Done | User |
| `PhoneOtp` | 1 | ✅ Done | User |
| `RetailerProfile` | 2 | 🔲 Next | User |
| `KycDocument` | 2 | 🔲 Next | User |
| `Customer` | 3 | 🔲 Pending | RetailerProfile |
| `Invoice` | 3 | 🔲 Pending | RetailerProfile, Customer |
| `InvoiceItem` | 3 | 🔲 Pending | Invoice |
| `Warranty` | 3 | 🔲 Pending | Invoice |
| `Agent` | 4 | 🔲 Pending | RetailerProfile |
| `Template` | 4 | 🔲 Pending | RetailerProfile |

---

## 11. Development Rules

- Business logic → **service layer only**
- Database access → **repository only**
- Request/response → **DTOs only** (never expose JPA entities in API responses)
- Validation → annotations on request DTOs (`@NotBlank`, `@Email`, `@Pattern`, etc.)
- Passwords → **BCrypt always**
- Secrets → **`application.yml` only**, never hardcode in Java
- Responses → always use `BaseResponse<T>` wrapper
- Errors → throw `BadRequestException` or `ResourceNotFoundException` — `GlobalExceptionHandler` catches them
- Commits → small and meaningful (`feat:`, `fix:`, `chore:`)
- Never commit directly to `main`

---

## 12. Starter Prompt for a New Chat

Copy and paste this at the start of a new conversation:

```
We are building the DWB (Digital Warranty & Billing) backend.
Stack: Java 21, Spring Boot 3.5.0, Maven, PostgreSQL 17, Spring Security + JWT, Docker.
Architecture: Modular Monolith. No microservices.

COMPLETED (Phase 1):
- Full auth flow: register → email OTP → phone OTP → account ACTIVE → login (email+password and phone+OTP)
- Multi-role users: Set<Role> stored in separate user_roles table (user_id | role)
- JWT includes email + roles, expiry 1 hour, secret from application.yml
- UniqueId: USR000001 format, generated after both email + phone verified
- OTP: console-printed for dev. DB tables: email_otps, phone_otps. Type enum: REGISTRATION / LOGIN
- File storage: StorageService interface + LocalStorageServiceImpl (./uploads/). Future: swap to Cloudflare R2
- CORS configured for http://localhost:5173
- Package structure: auth/login/, auth/register/, otp/, storage/, user/, role/, security/, common/, exception/

DB: PostgreSQL on localhost:5433, database dwb_db, user postgres / password postgres.
ddl-auto: update. NOTE: Hibernate never drops columns — do it manually if you remove entity fields.

NEXT TO BUILD (Phase 2 — Retailer Onboarding):
1. POST /api/v1/retailer/register — creates RetailerProfile entity linked to User, adds RETAILER role, generates RET000001 ID on profile
2. POST /api/v1/kyc/submit — multipart form with document upload, uses StorageService, creates KycDocument entity
3. GET  /api/v1/kyc/status — returns current KYC status for logged-in user

New entities needed: RetailerProfile, KycDocument.
Frontend is ready and waiting — build backend to match the frontend screens.
Build Retailer flow completely before moving to Customer/Technician.
Always explain changes after each file and ask for permission before modifying.
```
