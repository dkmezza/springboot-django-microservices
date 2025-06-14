# Microservices Demo: Spring Boot + Django + Docker

## 🧱 Architecture Overview

A simple microservices application with two backend services:

- `auth-service`: Manages users and tenants (Spring Boot + PostgreSQL)
- `company-service`: Manages companies linked to tenants and users (Django + PostgreSQL)
- Both communicate via REST APIs
- Shared JWT-based authentication
- Deployed locally using Docker Compose

---

## 🧹 Services Breakdown

### 1. Auth Service (`auth-service`)

**Technology:** Spring Boot + Java + PostgreSQL
**Responsibilities:**

- User Registration/Login
- JWT Token generation
- Tenant CRUD operations
- Expose `/api/users/`, `/api/tenants/`

#### ✅ Models:

- `User`:

  - `id` (UUID)
  - `email` (unique)
  - `password` (hashed)
  - `role` (admin, user)

- `Tenant`:

  - `id` (UUID)
  - `name`
  - `status`

#### ✅ Endpoints:

| Method | Path              | Description             |
| ------ | ----------------- | ----------------------- |
| POST   | `/api/auth/login` | User login, return JWT  |
| GET    | `/api/users/me`   | Authenticated user info |
| CRUD   | `/api/tenants/`   | Manage tenants          |

---

### 2. Company Service (`company-service`)

**Technology:** Django + PostgreSQL
**Responsibilities:**

- CRUD for companies
- Validates JWT token issued by `auth-service`
- Uses `tenant_id` and `created_by` to reference data in `auth-service`

#### ✅ Models:

- `Company`:

  - `id` (UUID)
  - `name`
  - `description`
  - `tenant_id` (UUID, from JWT)
  - `created_by` (UUID, from JWT)
  - `created_at` (timestamp)

#### ✅ Endpoints:

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
3. Django validates token (shared secret or public key)
4. If valid, request is processed with `user_id` and `tenant_id` from token

#### JWT Payload Example:

```json
{
  "sub": "user-uuid",
  "tenant_id": "tenant-uuid",
  "email": "user@example.com",
  "role": "admin"
}
```

---

## 🐘 Databases

**PostgreSQL instance (shared):**

- `auth_db` for Spring Boot
- `company_db` for Django

Each service connects to its own separate database schema.

---

## 🐳 Docker Compose Services

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
    environment:
      DB_NAME: auth_db

  company-service:
    build: ./company-service
    ports:
      - "8000:8000"
    depends_on:
      - postgres
    environment:
      DB_NAME: company_db
```

---

## ⟳ Inter-Service Communication

Django does not query the auth DB directly. Instead:

- It uses the `tenant_id` and `created_by` from the JWT token
- (Optional) It can call Spring Boot's `/api/users/{id}` or `/api/tenants/{id}` to fetch more info

---

## 🔪 Testing the System

### Postman Flow:

1. Register/Login user → Get JWT
2. Use JWT to:

   - Create Tenant (Spring Boot)
   - Create Company (Django)

3. List Companies with JWT auth
4. Verify `tenant_id` and `created_by` relationships

---

## 🌟 Goal

The goal of this project:

- To understand how to build and run microservices with Spring Boot + Django
- Secure services using JWT
- Handle DB isolation and service communication
- Deploy and test everything locally using Docker Compose

---
