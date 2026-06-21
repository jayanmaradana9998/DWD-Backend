# DWB Backend — Commands Reference

---

## Spring Boot

```bash
# Run the app
./mvnw spring-boot:run

# Compile only (check for errors without running)
./mvnw compile -q

# Clean + compile
./mvnw clean compile

# Clean + package (builds the JAR)
./mvnw clean package

# Run tests
./mvnw test
```

---

## Docker (PostgreSQL)

```bash
# Start PostgreSQL container
docker-compose up -d

# Stop PostgreSQL container
docker-compose down

# Check if container is running
docker ps

# View container logs
docker logs dwb-postgres
```

---

## PostgreSQL (psql)

```bash
# Connect to database
PGPASSWORD=postgres psql -h localhost -p 5433 -U postgres -d dwb_db

# Check if port 5433 is in use
lsof -i :5433
```

### Useful SQL (run inside psql or DBeaver)

```sql
-- List all tables
\dt

-- See users table structure
\d users

-- View all users
SELECT id, full_name, email, phone_number, status, unique_id FROM users;

-- View user roles
SELECT * FROM user_roles;

-- View email OTPs
SELECT * FROM email_otps ORDER BY created_at DESC;

-- View phone OTPs
SELECT * FROM phone_otps ORDER BY created_at DESC;

-- Drop a column that Hibernate left behind (example: old role column)
ALTER TABLE users DROP COLUMN IF EXISTS role;

-- Manually activate a user for testing
UPDATE users SET status = 'ACTIVE', email_verified = true, phone_number_verified = true WHERE email = 'test@example.com';
```

---

## Git

```bash
# Check current branch and status
git status

# Create and switch to a new feature branch
git checkout -b feature/retailer-profile

# Switch to existing branch
git checkout develop

# Stage specific files
git add src/main/java/com/dwb/retailer/

# Commit
git commit -m "feat: add retailer profile registration API"

# Push branch to remote
git push origin feature/retailer-profile
```

---

## URLs

| URL | What it is |
|-----|-----------|
| `http://localhost:8080/swagger-ui/index.html` | Swagger API docs & testing |
| `http://localhost:8080/health` | Backend health check |
| `http://localhost:5173` | Frontend dev server |

---

## VS Code — Fix IDE False Errors

When IDE shows "package does not exist" or Lombok red lines (even though Maven compiles fine):

```
Cmd+Shift+P → Java: Clean Java Language Server Workspace → Reload
```

---

## application.yml — Key Config

```yaml
app:
  jwt:
    secret: mySuperSecretKeyForJwtTokenGeneration123456789
    expiration-ms: 3600000       # 1 hour
  cors:
    allowed-origins: http://localhost:5173
  upload:
    dir: ./uploads
```

---

## Admin User — Create via SQL

Admin users cannot self-register. Run this script once to create the first admin.

**Step 1 — Connect to DB:**
```bash
PGPASSWORD=postgres psql -h localhost -p 5433 -U postgres -d dwb_db
```

**Step 2 — Run the script:**
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

-- Assign ADMIN role
INSERT INTO user_roles (user_id, role)
VALUES (
    (SELECT id FROM users WHERE email = 'admin@dwb.com'),
    'ADMIN'
);

-- Confirm it worked
SELECT u.id, u.email, u.unique_id, u.status, ur.role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
WHERE u.email = 'admin@dwb.com';
```

**Login with:**
- Email: `admin@dwb.com`
- Password: `Admin@123`

**If you want a different password:**
Use this site to generate a BCrypt hash (strength 10): https://bcrypt-generator.com
Then replace the `$2a$10$...` hash in the INSERT above.

---

## KYC Management — Quick SQL Reference

```sql
-- View all KYC submissions
SELECT k.id, u.email, k.full_name, k.id_type, k.status, k.rejection_reason, k.created_at
FROM kyc_documents k
JOIN users u ON k.user_id = u.id
ORDER BY k.created_at DESC;

-- View KYC file paths for a submission
SELECT * FROM kyc_document_files WHERE kyc_document_id = 1;

-- Manually approve KYC (testing only — use admin API in production)
UPDATE kyc_documents SET status = 'APPROVED' WHERE id = 1;

-- Manually reject KYC with reason (testing only)
UPDATE kyc_documents SET status = 'REJECTED', rejection_reason = 'ID number invalid' WHERE id = 1;
```

---

## Retailer Management — Quick SQL Reference

```sql
-- View all retailers
SELECT r.id, r.retailer_unique_id, r.store_name, r.gst, r.pan, u.email, u.status
FROM retailer_profiles r
JOIN users u ON r.user_id = u.id
ORDER BY r.created_at DESC;
```
