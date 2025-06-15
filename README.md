# Microservices Demo: Spring Boot + Django + Docker

## 🧱 Architecture Overview

A simple microservices application with two backend services:

- `auth-service`: Manages users and tenants (Spring Boot + PostgreSQL) ✅ **COMPLETED**
- `company-service`: Manages companies linked to tenants and users (Django + PostgreSQL) 🚧 **IN PROGRESS**
- Both communicate via REST APIs
- Shared JWT-based authentication
- Deployed locally using Docker Compose

---

## 🚀 Quick Start

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Git

### Run the Auth Service (Currently Available)
```bash
# Clone the repository
git clone <my-repo-url>
cd auth-service

# Start auth service with PostgreSQL
docker-compose up --build
```

⏱️ **First build takes ~5 minutes** (downloads dependencies). Subsequent builds are much faster!

### Access the Auth Service
- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Documentation**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

---

## 🧹 Services Breakdown

### 1. Auth Service (`auth-service`) ✅ **COMPLETED**

**Technology:** Spring Boot 3.5.0 + Java 21 + PostgreSQL 15
**Status:** ✅ Fully implemented and containerized
**Responsibilities:**

- User Registration/Login
- JWT Token generation and validation
- Tenant CRUD operations
- Multi-tenant support
- Swagger/OpenAPI documentation

#### ✅ Models:

- `User`:
  - `id` (UUID)
  - `email` (unique)
  - `password` (hashed)
  - `role` (admin, user)
  - `tenantId` (UUID, for multi-tenancy)

- `Tenant`:
  - `id` (UUID)
  - `name`
  - `status`
  - `createdAt` (timestamp)

#### ✅ Endpoints:

| Method | Path                  | Description                |
| ------ | --------------------- | -------------------------- |
| POST   | `/api/auth/login`     | User login, return JWT     |
| POST   | `/api/auth/register`  | Register a new user        |
| GET    | `/api/users/me`       | Get current user profile   |
| GET    | `/api/tenants`        | Get all tenants            |
| POST   | `/api/tenants`        | Create a new tenant        |
| GET    | `/api/tenants/{id}`   | Get tenant by ID           |
| PUT    | `/api/tenants/{id}`   | Update tenant              |
| DELETE | `/api/tenants/{id}`   | Delete tenant              |

#### 🔧 Features Implemented:
- ✅ Spring Security with JWT
- ✅ BCrypt password hashing
- ✅ Custom JWT filter
- ✅ Swagger UI integration
- ✅ Health checks
- ✅ Multi-stage Docker build
- ✅ Production-ready configuration

---

### 2. Company Service (`company-service`) 🚧 **COMING NEXT**

**Technology:** Django + PostgreSQL
**Status:** 🚧 Not yet implemented
**Responsibilities:**

- CRUD for companies
- Validates JWT token issued by `auth-service`
- Uses `tenant_id` and `created_by` to reference data in `auth-service`

#### 📋 Planned Models:

- `Company`:
  - `id` (UUID)
  - `name`
  - `description`
  - `tenant_id` (UUID, from JWT)
  - `created_by` (UUID, from JWT)
  - `created_at` (timestamp)

#### 📋 Planned Endpoints:

| Method | Path                 | Description                    |
| ------ | -------------------- | ------------------------------ |
| GET    | `/api/companies/`    | List all companies (auth-only) |
| POST   | `/api/companies/`    | Create a new company           |
| GET    | `/api/companies/:id` | Get specific company           |
| PUT    | `/api/companies/:id` | Update company                 |
| DELETE | `/api/companies/:id` | Delete company                 |

---

## 🔐 JWT Authentication Flow

1. User logs in via Spring Boot → receives JWT
2. JWT is passed in `Authorization: Bearer <token>` header to Django
3. Django validates token (shared secret)
4. If valid, request is processed with `user_id` and `tenant_id` from token

#### JWT Payload Example:

```json
{
  "sub": "user-uuid",
  "tenant_id": "tenant-uuid",
  "email": "user@example.com",
  "role": "admin",
  "iat": 1639123456,
  "exp": 1639209856
}
```

---

## 🐘 Databases

**Current Setup (Auth Service):**
- PostgreSQL 15 with separate database: `auth_db`
- User: `mezza`
- Password: `ndaghafijo`
- Port: `5432`

**Planned Setup (Full System):**
- `auth_db` for Spring Boot ✅
- `company_db` for Django 🚧

---

## 🐳 Docker Configuration

### Current (Auth Service Only)
```yaml
services:
  postgres:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: auth_db
      POSTGRES_USER: mezza
      POSTGRES_PASSWORD: ndaghafijo

  auth-service:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
```

### Planned (Full System)
```yaml
services:
  postgres:
    image: postgres:15
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password

  auth-service:
    build: ./auth-service
    ports:
      - "8080:8080"
    depends_on:
      - postgres

  company-service:
    build: ./company-service
    ports:
      - "8000:8000"
    depends_on:
      - postgres
```

---

## ⟳ Inter-Service Communication (Planned)

Django will not query the auth DB directly. Instead:
- It uses the `tenant_id` and `created_by` from the JWT token
- (Optional) It can call Spring Boot's `/api/users/{id}` or `/api/tenants/{id}` to fetch more info

---

## 🔪 Testing the Current System

### Using Swagger UI (Recommended)
1. Open http://localhost:8080/swagger-ui.html
2. Test all endpoints directly from the browser
3. Use the "Authorize" button to set JWT tokens

### Using Postman/cURL:

1. **Register a new user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","role":"admin"}'
```

2. **Login to get JWT:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

3. **Create a tenant (with JWT):**
```bash
curl -X POST http://localhost:8080/api/tenants \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Acme Corp","status":"ACTIVE"}'
```

4. **Get user profile:**
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🛠️ Development

### Stop Services
```bash
docker-compose down
```

### View Logs
```bash
# All services
docker-compose logs -f

# Auth service only
docker-compose logs -f auth-service
```

### Database Access
```bash
# Connect to PostgreSQL
docker exec -it auth_postgres psql -U mezza -d auth_db
```

---

## 🏷️ Tech Stack

### Auth Service (Completed)
- **Framework**: Spring Boot 3.5.0
- **Language**: Java 21
- **Database**: PostgreSQL 15
- **Security**: Spring Security + JWT
- **Documentation**: SpringDoc OpenAPI 3
- **Build**: Maven + Docker multi-stage build

### Company Service (Planned)
- **Framework**: Django
- **Language**: Python
- **Database**: PostgreSQL 15
- **Authentication**: JWT validation

---

## 📦 Current Project Structure

```
microservices-demo/
├── auth-service/           ✅ COMPLETED
│   ├── src/main/java/
│   ├── docker-compose.yml
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
└── company-service/        🚧 COMING NEXT
    └── (Django project)
```

---

## 🌟 Project Status & Roadmap

### ✅ Completed
- [x] Auth Service with Spring Boot
- [x] User registration and login
- [x] JWT token generation and validation
- [x] Tenant CRUD operations
- [x] Multi-tenant architecture
- [x] Swagger/OpenAPI documentation
- [x] Docker containerization
- [x] Health checks and monitoring
- [x] Production-ready security

### 🚧 Next Steps
- [ ] Django Company Service
- [ ] JWT validation in Django
- [ ] Inter-service communication
- [ ] Complete Docker Compose setup
- [ ] Integration testing
- [ ] API Gateway (optional)

---

## 🌟 Goal

The goal of this project:
- ✅ Build secure microservices with Spring Boot
- ✅ Implement JWT-based authentication
- ✅ Create production-ready Docker setup
- 🚧 Add Django service for company management
- 🚧 Demonstrate inter-service communication
- 🚧 Show complete microservices ecosystem

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Test with Docker locally
4. Submit a pull request

## 📄 License

MIT License - see LICENSE file for details.
