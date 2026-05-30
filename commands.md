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
