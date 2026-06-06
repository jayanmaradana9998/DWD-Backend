# DWD Backend

Backend service for the DWD (Digital Warranty & Billing) appliance service platform.

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
| API Docs | SpringDoc OpenAPI / Swagger |

---

## Local Setup

```bash
# 1. Start PostgreSQL
docker-compose up -d

# 2. Run backend
./mvnw spring-boot:run

# 3. Swagger UI
http://localhost:8080/swagger-ui/index.html
```

---

## Package Structure

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
├── retailer/
│   ├── controller/   RetailerController.java
│   ├── dto/          RetailerRegisterRequest, RetailerRegisterResponse
│   ├── entity/       RetailerProfile.java
│   ├── repository/   RetailerProfileRepository.java
│   └── service/      RetailerService, RetailerServiceImpl
│
├── kyc/
│   ├── controller/   KycController.java
│   ├── dto/          KycStatusResponse.java
│   ├── entity/       KycDocument.java, KycStatus.java
│   ├── repository/   KycDocumentRepository.java
│   └── service/      KycService, KycServiceImpl
│
├── admin/
│   ├── controller/   AdminKycController.java
│   │                 AdminUserController.java
│   │                 AdminDashboardController.java
│   ├── dto/          KycListItemResponse, KycDetailResponse, KycRejectRequest
│   │                 AdminUserResponse, AdminRetailerResponse, AdminDashboardResponse
│   └── service/      AdminKycService, AdminKycServiceImpl
│                     AdminUserService, AdminUserServiceImpl
│                     AdminDashboardService, AdminDashboardServiceImpl
│
├── storage/
│   ├── controller/   FileServeController.java
│   ├── service/      StorageService.java  (interface)
│   └── impl/         LocalStorageServiceImpl.java
│
├── user/
│   ├── entity/       User.java, UserStatus.java
│   └── repository/   UserRepository.java
│
├── role/
│   └── entity/       Role.java  (RETAILER, CUSTOMER, TECHNICIAN, ADMIN)
│
├── security/
│   ├── config/       SecurityConfig.java, SwaggerConfig.java
│   ├── jwt/
│   │   ├── filter/   JwtAuthenticationFilter.java
│   │   └── service/  JwtService.java
│   └── util/         SecurityUtils.java
│
├── common/
│   ├── controller/   HealthController.java
│   ├── dto/          BaseResponse.java
│   └── entity/       BaseEntity.java
│
├── config/           PasswordConfig.java
└── exception/
    ├── custom/       BadRequestException, ResourceNotFoundException
    └── handler/      GlobalExceptionHandler
```

---

## API Endpoints

### Public — no token needed

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Server health check |
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/verify-email-otp` | Verify email OTP |
| POST | `/api/v1/otp/send-phone-otp` | Send registration phone OTP |
| POST | `/api/v1/otp/verify-phone-otp` | Verify phone OTP → account ACTIVE + uniqueId |
| POST | `/api/v1/auth/login` | Email + password login → JWT |
| POST | `/api/v1/auth/send-phone-login-otp` | Phone login step 1 |
| POST | `/api/v1/auth/verify-phone-login-otp` | Phone login step 2 → JWT |

### Protected — Bearer token required (any active user)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/retailer/register` | Create retailer profile → RET000001 |
| GET | `/api/v1/retailer/profile` | Get own retailer profile |
| POST | `/api/v1/kyc/submit` | Submit KYC form + documents (multipart) |
| GET | `/api/v1/kyc/status` | Get own KYC status |

### Admin only — Bearer token + ADMIN role required

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/admin/kyc` | List all KYC (`?status=PENDING`) |
| GET | `/api/v1/admin/kyc/{id}` | KYC detail with file paths |
| POST | `/api/v1/admin/kyc/{id}/approve` | Approve KYC |
| POST | `/api/v1/admin/kyc/{id}/reject` | Reject KYC with reason |
| GET | `/api/v1/admin/files/**` | View uploaded document file |
| GET | `/api/v1/admin/users` | List all users |
| POST | `/api/v1/admin/users/{id}/block` | Block user |
| POST | `/api/v1/admin/users/{id}/unblock` | Unblock user |
| GET | `/api/v1/admin/retailers` | List all retailers with KYC status |
| GET | `/api/v1/admin/dashboard` | Platform stats |

---

## Database Tables

| Table | Purpose |
|-------|---------|
| `users` | Core user accounts |
| `user_roles` | Multi-role: user_id + role |
| `email_otps` | Email OTP records |
| `phone_otps` | Phone OTP records (REGISTRATION / LOGIN) |
| `retailer_profiles` | Store details per retailer |
| `kyc_documents` | KYC submission info + status |
| `kyc_document_files` | Uploaded file paths per KYC |

---

## Registration Flow

```
1. POST /api/v1/auth/register            → user PENDING, email OTP to console
2. POST /api/v1/auth/verify-email-otp    → emailVerified = true
3. POST /api/v1/otp/send-phone-otp       → phone OTP to console
4. POST /api/v1/otp/verify-phone-otp     → phoneVerified = true, ACTIVE, USR000001
5. POST /api/v1/auth/login               → { token, uniqueId, roles }
6. POST /api/v1/retailer/register        → { retailerUniqueId: RET000001 }
7. POST /api/v1/kyc/submit               → KYC PENDING
8. [Admin] POST /api/v1/admin/kyc/{id}/approve → KYC APPROVED
```

---

## Development Rules

- Business logic → service layer only
- DB access → repository only
- Request/response → DTOs only (never expose entities)
- Validation → annotations on request DTOs
- Passwords → BCrypt always
- Secrets → `application.yml` only
- All responses → `BaseResponse<T>` wrapper

---

## VS Code — Fix IDE False Errors

```
Cmd+Shift+P → Java: Clean Java Language Server Workspace → Reload
```
