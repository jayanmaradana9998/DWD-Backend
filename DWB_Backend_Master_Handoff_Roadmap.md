# DWB Backend — Master Handoff & Roadmap

**Last updated:** 2026-06-01
**Backend developer:** Janesh (backend lead)
**Frontend team:** Separate team (`dbw-frontend/`, React + TypeScript + Vite)

---

## HOW TO USE THIS FILE

If you are starting a new chat or a new developer is joining — read this file top to bottom. It contains every decision made, what is built, what is next, and a copy-paste starter prompt at the bottom. You should be able to build the next step without asking for background.

---

## 1. What Is This Project?

**DWD (Digital Warranty & Billing)** — a web platform for appliance retailers to:
- Onboard customers and generate bills/invoices
- Manage warranties on sold appliances
- Assign technicians for service requests
- Track customer rewards and payments

**User types:** Retailer, Customer, Technician, Admin

**Build order agreed:** Retailer flow completely first → Customer → Technician → Admin UI

---

## 2. Architecture (Final, Not Changing)

| Decision | Choice |
|----------|--------|
| Architecture | Modular Monolith (no microservices) |
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Build | Maven |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (HS256) |
| Containers | Docker |
| API Docs | SpringDoc OpenAPI / Swagger UI |

---

## 3. All Key Decisions Made (Read Carefully)

### 3.1 Multi-Role Users
- One user account can have **multiple roles** simultaneously
- Roles stored in a **separate `user_roles` table** (`user_id FK | role VARCHAR`)
- At registration: roles are **empty**
- Role added when user completes a role-specific profile (e.g. retailer profile)
- Role enum: `RETAILER`, `CUSTOMER`, `TECHNICIAN`, `ADMIN`

### 3.2 Registration Flow (Both Email + Phone Required)
Account only becomes ACTIVE after both email AND phone are verified:
```
POST /api/v1/auth/register            → PENDING, empty roles, email OTP to console
POST /api/v1/auth/verify-email-otp    → emailVerified = true, still PENDING
POST /api/v1/otp/send-phone-otp       → phone OTP to console
POST /api/v1/otp/verify-phone-otp     → phoneVerified = true → ACTIVE → USR000001 generated
POST /api/v1/auth/login               → returns { token, uniqueId, roles }
```

### 3.3 UniqueId Format
- User uniqueId: `USR000001` — generated after both email + phone verified
- Retailer uniqueId: `RET000001` — on RetailerProfile entity, generated after retailer profile saved
- Future: `CUS000001` on CustomerProfile, `TEC000001` on TechnicianProfile
- Generated using DB auto-increment id: `"USR" + String.format("%06d", id)`
- Two saves needed: first to get id, second to set uniqueId

### 3.4 OTP Strategy
- Current: console print (`System.out.println`) — development only
- OTP always stored in DB (required for validation logic — backend compares stored vs submitted)
- Tables: `email_otps`, `phone_otps`
- `PhoneOtpType` enum: `REGISTRATION` (signup flow), `LOGIN` (phone login flow)
- Future: WhatsApp OTP via Twilio (production) — cheaper + better delivery in India
- Future email: Resend.com (3000 emails/month free)

### 3.5 File Storage
- Current: local disk (`./uploads/kyc/{userId}/` folder)
- Abstracted behind `StorageService` interface → `LocalStorageServiceImpl`
- Future: swap to **Cloudflare R2** (10GB free forever, no egress fees, S3-compatible)
  — just add `R2StorageServiceImpl`, zero changes to other code
- Files served via `GET /api/v1/admin/files/**`

### 3.6 JWT
- Secret: `application.yml` → `app.jwt.secret` (never hardcoded in Java)
- Expiry: 1 hour (`app.jwt.expiration-ms: 3600000`)
- Payload includes: `email` + `roles` list
- Algorithm: HS256

### 3.7 Admin Access
- Admin routes: `/api/v1/admin/**` — protected by `hasRole("ADMIN")` in SecurityConfig
- Admin user cannot self-register — must be created directly in DB via SQL script
- Admin Swagger tags appear as separate groups: `Admin - KYC Management`, `Admin - User Management`, `Admin - Dashboard`

### 3.8 KYC Flow
```
Retailer submits KYC → status: PENDING
Admin views: GET /api/v1/admin/kyc?status=PENDING
Admin approves: POST /api/v1/admin/kyc/{id}/approve → APPROVED
Admin rejects: POST /api/v1/admin/kyc/{id}/reject + reason → REJECTED
Retailer resubmits (allowed only after REJECTED)
```
- `rejectionReason` field on `KycDocument` — retailer can see why they were rejected
- KYC document files stored in `kyc_document_files` table (`@ElementCollection`)

### 3.9 ddl-auto: update — Important Rule
Hibernate auto-creates/updates tables BUT **never drops columns**. If you remove a field from an entity, you must manually run:
```sql
ALTER TABLE tablename DROP COLUMN IF EXISTS columnname;
```
This already happened in Phase 1 (old `role` column on `users` table).

---

## 4. Database Config

| Setting | Value |
|---------|-------|
| Port | 5433 (5432 was occupied on dev machine) |
| Database | `dwb_db` |
| Username | `postgres` |
| Password | `postgres` |
| Container | `dwb-postgres` (Docker) |

---

## 5. Current Package Structure

```
src/main/java/com/dwb/
├── auth/login/      → email+password login, phone OTP login
├── auth/register/   → user registration, email OTP verification
├── otp/             → phone OTP (registration + login types)
├── retailer/        → RetailerProfile CRUD
├── kyc/             → KYC submission + status
├── admin/           → KYC approval, user management, dashboard (ADMIN only)
├── storage/         → file upload (local disk now, R2 later) + file serve
├── user/            → User entity + repository
├── role/            → Role enum
├── security/        → JWT filter, JWT service, SecurityConfig, SecurityUtils
├── common/          → BaseEntity, BaseResponse, HealthController
├── config/          → PasswordConfig (BCrypt bean)
└── exception/       → BadRequestException, ResourceNotFoundException, GlobalExceptionHandler
```

**Pattern for every module:** `controller/` → `dto/` → `service/` (interface + impl) → `entity/` → `repository/`

---

## 6. All Implemented API Endpoints

### Public
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/health` | Health check |
| POST | `/api/v1/auth/register` | Register |
| POST | `/api/v1/auth/verify-email-otp` | Email OTP verify |
| POST | `/api/v1/otp/send-phone-otp` | Send registration phone OTP |
| POST | `/api/v1/otp/verify-phone-otp` | Verify phone OTP → ACTIVE |
| POST | `/api/v1/auth/login` | Email login → JWT |
| POST | `/api/v1/auth/send-phone-login-otp` | Phone login step 1 |
| POST | `/api/v1/auth/verify-phone-login-otp` | Phone login step 2 → JWT |

### Protected (any active user)
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/v1/retailer/register` | Create retailer profile |
| GET | `/api/v1/retailer/profile` | Get own retailer profile |
| POST | `/api/v1/kyc/submit` | Submit KYC (multipart) |
| GET | `/api/v1/kyc/status` | Get own KYC status |

### Admin only
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/admin/kyc` | List KYC (`?status=PENDING`) |
| GET | `/api/v1/admin/kyc/{id}` | KYC detail |
| POST | `/api/v1/admin/kyc/{id}/approve` | Approve KYC |
| POST | `/api/v1/admin/kyc/{id}/reject` | Reject KYC + reason |
| GET | `/api/v1/admin/files/**` | View uploaded file |
| GET | `/api/v1/admin/users` | List all users |
| POST | `/api/v1/admin/users/{id}/block` | Block user |
| POST | `/api/v1/admin/users/{id}/unblock` | Unblock user |
| GET | `/api/v1/admin/retailers` | List all retailers |
| GET | `/api/v1/admin/dashboard` | Platform stats |

---

## 7. Database Tables

| Table | Purpose | Key Fields |
|-------|---------|------------|
| `users` | Accounts | id, email, phoneNumber, status, uniqueId |
| `user_roles` | Multi-role | user_id FK, role |
| `email_otps` | Email OTPs | user_id FK, otp, expiresAt, verified |
| `phone_otps` | Phone OTPs | user_id FK, otp, type, expiresAt, verified |
| `retailer_profiles` | Store info | user_id FK (unique), retailerUniqueId, gst, pan |
| `kyc_documents` | KYC records | user_id FK, idType, status, rejectionReason |
| `kyc_document_files` | KYC file paths | kyc_document_id FK, file_path |

---

## 8. What Needs to Be Built Next

### Phase 3 — Core Business Flow (Next)

```
Dashboard → GET /api/v1/dashboard/summary
  → totalBills, totalWarranties, recentTransactions

Customer:
  POST /api/v1/customers               → create customer (name, phone, email)
  GET  /api/v1/customers/search        → search by phone or email
  POST /api/v1/customers/{id}/send-otp → send OTP to customer phone
  POST /api/v1/customers/{id}/verify-otp

Invoice:
  POST /api/v1/invoices                → create with line items
  GET  /api/v1/invoices                → list for logged-in retailer
  GET  /api/v1/invoices/{id}           → single invoice detail

Warranty:
  POST /api/v1/warranties              → create linked to invoice
  GET  /api/v1/warranties              → list for logged-in retailer
```

New entities: `Customer`, `Invoice`, `InvoiceItem`, `Warranty`

### Phase 4 — Management
```
GET/POST /api/v1/agents      → manage technicians under retailer
GET/POST /api/v1/templates   → invoice/document templates
```

### Phase 5 — Customer & Technician Flows
After retailer flow is fully complete.

---

## 9. Entity Roadmap

| Entity | Phase | Status | Linked To |
|--------|-------|--------|-----------|
| `User` | 1 | ✅ Done | — |
| `EmailOtp` | 1 | ✅ Done | User |
| `PhoneOtp` | 1 | ✅ Done | User |
| `RetailerProfile` | 2 | ✅ Done | User |
| `KycDocument` | 2 | ✅ Done | User |
| `Customer` | 3 | 🔲 Next | RetailerProfile |
| `Invoice` | 3 | 🔲 Next | RetailerProfile, Customer |
| `InvoiceItem` | 3 | 🔲 Next | Invoice |
| `Warranty` | 3 | 🔲 Next | Invoice |
| `Agent` | 4 | 🔲 Pending | RetailerProfile |
| `Template` | 4 | 🔲 Pending | RetailerProfile |

---

## 10. Future Technical Improvements

| Item | Priority | Notes |
|------|----------|-------|
| Real email (Resend.com) | Before launch | 3000 emails/month free |
| WhatsApp OTP (Twilio) | Before launch | Cheaper than SMS in India |
| Cloudflare R2 file storage | Before launch | 10GB free, S3-compatible, just add `R2StorageServiceImpl` |
| Flyway migrations | Before launch | Replace `ddl-auto: update` |
| Refresh tokens | After core features | JWT refresh flow |
| Role-based endpoint guards | Phase 3 start | `@PreAuthorize("hasRole('RETAILER')")` |
| Dockerize backend | Before production | Currently only DB is in Docker |
| CI/CD (GitHub Actions) | Before production | |

---

## 11. Development Rules

- Always ask before changing code
- Explain what changed and why after each file
- Business logic → service only
- DB access → repository only
- API input/output → DTOs only (never expose JPA entities)
- Passwords → BCrypt always
- Secrets → `application.yml` only
- All responses → `BaseResponse<T>` wrapper
- Errors → throw `BadRequestException` or `ResourceNotFoundException`
- Admin users → created via SQL script only, never self-registered

---

## 12. Starter Prompt for New Chat

```
We are building the DWB (Digital Warranty & Billing) backend.
Stack: Java 21, Spring Boot 3.5.0, Maven, PostgreSQL 17, Spring Security + JWT, Docker.
Architecture: Modular Monolith. No microservices.

COMPLETED:
Phase 1 — Full auth:
  - Register → email OTP → phone OTP → ACTIVE → login (email+password and phone+OTP)
  - Multi-role users: Set<Role> in user_roles table (user_id | role)
  - JWT: email + roles in payload, 1 hour, secret from application.yml
  - UniqueId: USR000001 after both verifications
  - OTP: console-printed. Tables: email_otps, phone_otps. PhoneOtpType: REGISTRATION/LOGIN
  - StorageService interface + LocalStorageServiceImpl (./uploads/)
  - CORS: http://localhost:5173

Phase 2 — Retailer onboarding:
  - POST /api/v1/retailer/register → RetailerProfile, adds RETAILER role, generates RET000001
  - GET  /api/v1/retailer/profile
  - POST /api/v1/kyc/submit (multipart: fields + files)
  - GET  /api/v1/kyc/status → NOT_SUBMITTED / PENDING / APPROVED / REJECTED
  - KycDocument has rejectionReason field (set when admin rejects)

Admin module (all under /api/v1/admin/**, ADMIN role required):
  - GET/POST /api/v1/admin/kyc → list, detail, approve, reject
  - GET /api/v1/admin/files/** → serve uploaded KYC files
  - GET /api/v1/admin/users → list, block, unblock
  - GET /api/v1/admin/retailers → list with KYC status
  - GET /api/v1/admin/dashboard → platform stats
  - Swagger: separate dropdown groups via @Tag

DB: PostgreSQL on localhost:5433, database dwb_db, user postgres/postgres.
NOTE: Hibernate never drops columns — do it manually if entity fields are removed.
NOTE: Admin user created via SQL script, never via signup.

NEXT TO BUILD — Phase 3:
  Dashboard summary, Customer CRUD + OTP, Invoice + line items, Warranty
  New entities: Customer, Invoice, InvoiceItem, Warranty
  All linked to RetailerProfile of the logged-in user.

Rules:
  - Ask permission before changing any code
  - Explain what changed and why after each file
  - Follow same package pattern: controller/ dto/ service/ entity/ repository/
```

---

## 13. Admin User SQL Script

See `commands.md` for the full SQL script to create the first admin user.
