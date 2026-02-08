# Culinary Recipe Manager

[![Angular](https://img.shields.io/badge/Angular-19-dd0031?logo=angular)](https://angular.dev/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6db33f?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-ed8b00?logo=openjdk)](https://openjdk.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7-3178c6?logo=typescript)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169e1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3-ff6600?logo=rabbitmq&logoColor=white)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ed?logo=docker&logoColor=white)](https://docs.docker.com/compose/)

A full-stack recipe management platform built with a **CQRS (Command Query Responsibility Segregation)** architecture. The system separates read and write operations across dedicated microservices that communicate asynchronously via RabbitMQ, fronted by a reactive API gateway handling JWT authentication and role-based access control.

---

## Architecture

```
                         ┌──────────────────────────────────┐
                         │          Angular 19 SPA           │
                         │    Tailwind CSS + DaisyUI         │
                         └──────────────┬───────────────────┘
                                        │ /api/*
                                        ▼
                         ┌──────────────────────────────────┐
                         │     API Gateway (WebFlux)         │
                         │  JWT Auth · RBAC · Route Filter   │
                         │         Spring Cloud Gateway      │
                         └──────┬───────────────┬───────────┘
                      GET /api/*│               │ POST/PUT/DELETE /api/*
                                ▼               ▼
               ┌─────────────────┐   ┌─────────────────────┐
               │  Query Service   │   │  Command Service     │
               │  Spring MVC     │   │  Spring MVC          │
               │  HATEOAS        │   │  Publishes events    │
               │  Read model     │   │  Write model         │
               └────────┬────────┘   └──────────┬──────────┘
                        │                        │
                        │    ┌──────────────┐    │
                        │◄───│  RabbitMQ    │◄───┘
                        │    │  (AMQP)      │
                        │    └──────────────┘
                        ▼                        ▼
               ┌─────────────────────────────────────────┐
               │           PostgreSQL 16                   │
               │  Write tables  │  Denormalized read table │
               │  (normalized)  │  (pg_trgm fuzzy search)  │
               └─────────────────────────────────────────┘
```

**Key design decisions:**

- **CQRS with event-driven sync** -- Write operations go through the command service, which publishes domain events to RabbitMQ. The query service consumes these events to maintain a denormalized read model optimized for search and retrieval.
- **Reactive gateway** -- The API gateway uses Spring Cloud Gateway (WebFlux) for non-blocking request routing, with custom `GatewayFilter` implementations for JWT validation and admin role enforcement.
- **Fuzzy search** -- PostgreSQL's `pg_trgm` extension with GIN indexes enables trigram-based fuzzy matching on recipe titles, tags, ingredients, and units.
- **Separate read/write models** -- The write side uses a fully normalized relational schema (recipes, steps, ingredients, units, tags with join tables), while the read side stores a single denormalized `recipes_read` table with PostgreSQL arrays for instant retrieval.

---

## Tech Stack

| Layer        | Technology                                                                                          |
|--------------|-----------------------------------------------------------------------------------------------------|
| **Frontend** | Angular 19 (standalone components, reactive forms, functional guards/interceptors), TypeScript 5.7   |
| **Styling**  | Tailwind CSS 3, DaisyUI 2, SCSS                                                                     |
| **Gateway**  | Spring Cloud Gateway (WebFlux), Spring Security, R2DBC, JJWT                                        |
| **Command**  | Spring Boot 3.5 (Web + JPA), Spring AMQP (RabbitMQ publisher), MapStruct, Lombok                    |
| **Query**    | Spring Boot 3.5 (Web + JPA + HATEOAS), Spring AMQP (RabbitMQ consumer), Hypersistence Utils         |
| **Database** | PostgreSQL 16 with `pg_trgm` extension, GIN indexes                                                 |
| **Messaging**| RabbitMQ 3 (AMQP)                                                                                   |
| **Auth**     | JWT (HMAC-SHA) with BCrypt password hashing                                                          |
| **Testing**  | JUnit 5, Testcontainers (PostgreSQL), WireMock, Jasmine + Karma                                     |
| **DevOps**   | Docker Compose (6 services), multi-stage Dockerfiles, nginx reverse proxy                            |

---

## Features

### Recipe Management
- Create, edit, and delete recipes with structured data: steps, ingredients (with quantities and units), tags, difficulty level, preparation time, and portions
- Autocomplete inputs with debounced search for ingredients, units, and tags
- View detailed recipe pages with full ingredient lists, step-by-step instructions, and metadata

### Search
- **Full-text fuzzy search** on recipe titles using PostgreSQL trigram similarity (`pg_trgm`)
- **Tag-based filtering** with trigram-indexed tag lookup
- **Author-based search** to find recipes by a specific user
- Paginated results with HATEOAS navigation links

### Authentication & Authorization
- User registration with real-time password strength indicator
- JWT-based stateless authentication issued by the gateway
- **Role-based access control** (USER / ADMIN) enforced at the gateway routing layer:
  - Standard users can create recipes and reviews
  - Admin-only endpoints for managing taxonomy (ingredients, units, tags) are protected by a dedicated `AdminRoleFilter`

### Reviews
- Rate recipes (1-5 scale) and leave comments
- Average rating computed and synced to the read model

### Admin Panel
- CRUD operations for the ingredient, unit, and tag taxonomies
- Protected by gateway-level admin role filter

---

## Quick Start

### Prerequisites
- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/)

### Run with Docker Compose

```bash
git clone https://github.com/<your-username>/culinary-recipe-manager.git
cd culinary-recipe-manager
docker compose up --build
```

Once all services are healthy:

| Service              | URL                          |
|----------------------|------------------------------|
| Frontend (SPA)       | http://localhost:4200        |
| API Gateway          | http://localhost:8080        |
| RabbitMQ Management  | http://localhost:15672        |

To stop and clean up:

```bash
docker compose down       # Stop services (data persists)
docker compose down -v    # Stop and wipe database volume
```

## API Overview

All endpoints are accessed through the API Gateway at `:8080`. The gateway routes requests based on HTTP method:

| Method | Path                          | Service         | Auth     | Description                    |
|--------|-------------------------------|-----------------|----------|--------------------------------|
| POST   | `/api/auth/login`             | Gateway         | Public   | Authenticate and receive JWT   |
| POST   | `/api/auth/register`          | Gateway         | Public   | Register a new user            |
| GET    | `/api/recipe/**`              | Query Service   | JWT      | Search/retrieve recipes        |
| POST   | `/api/recipe/**`              | Command Service | JWT      | Create/update recipes          |
| GET    | `/api/review/**`              | Query Service   | JWT      | Retrieve reviews               |
| POST   | `/api/review/**`              | Command Service | JWT      | Create/update reviews          |
| GET    | `/api/tag/**`                 | Query Service   | JWT      | Search tags                    |
| POST   | `/api/tag/**`                 | Command Service | JWT+Admin| Manage tags (admin only)       |
| GET    | `/api/ingredient/**`          | Query Service   | JWT      | Search ingredients             |
| POST   | `/api/ingredient/**`          | Command Service | JWT+Admin| Manage ingredients (admin only)|
| GET    | `/api/unit/**`                | Query Service   | JWT      | Search units                   |
| POST   | `/api/unit/**`                | Command Service | JWT+Admin| Manage units (admin only)      |

---

## Project Structure

```
culinary-recipe-manager/
├── frontend/                          # Angular 19 SPA
│   └── src/app/
│       ├── add-recipe/                # Recipe creation form
│       ├── admin-panel/               # Admin taxonomy management
│       ├── auth/                      # Login & registration
│       ├── my-recipes/                # User's own recipes
│       ├── recipe-details/            # Single recipe view + reviews
│       ├── recipe-search/             # Search with filters & pagination
│       └── service/                   # HTTP services, guards, interceptors
│
├── api-gateway/                       # Spring Cloud Gateway (WebFlux)
│   └── src/main/java/.../apigateway/
│       ├── auth/                      # AuthController, AuthService
│       ├── config/                    # Route definitions, Security, CORS
│       ├── dto/                       # Auth request/response records
│       ├── entity/                    # User entity (R2DBC)
│       ├── filter/                    # JwtAuthFilter, AdminRoleFilter
│       └── jwt/                       # JwtService (token issue/validate)
│
├── command-service/                   # Write-side microservice
│   └── src/main/java/.../commandservice/
│       ├── controller/                # REST endpoints for mutations
│       ├── entity/                    # JPA entities (normalized write model)
│       ├── messaging/                 # RabbitMQ event publisher
│       ├── service/                   # Business logic
│       └── repository/               # Spring Data JPA repositories
│
├── query-service/                     # Read-side microservice
│   └── src/main/java/.../queryservice/
│       ├── controller/                # REST endpoints with HATEOAS
│       ├── entity/                    # Denormalized read model entity
│       ├── messaging/                 # RabbitMQ event consumer
│       ├── service/                   # Query logic with pagination
│       └── repository/               # Custom native queries (pg_trgm)
│
├── schema.sql                         # Full database schema
├── docker-compose.yml                 # Multi-service orchestration
└── AGENTS.md                          # Coding agent instructions
```

---

## Testing

### Backend (JUnit 5 + Testcontainers)

```bash
# Run all tests for a service
cd api-gateway && ./mvnw test
cd command-service && ./mvnw test
cd query-service && ./mvnw test

# Run a specific test class
./mvnw test -Dtest=JwtServiceTest

# Run a specific test method
./mvnw test -Dtest=JwtServiceTest#testMethodName
```

Integration tests use **Testcontainers** to spin up disposable PostgreSQL instances and **WireMock** for HTTP mocking of downstream services. Docker must be running.

### Frontend (Jasmine + Karma)

```bash
cd frontend
npm test                                                    # All tests
npx ng test --include='**/recipe-details.component.spec.ts' # Single file
```

---

## Database Schema

The database uses a **dual-model design** reflecting the CQRS pattern:

**Write model** (11 normalized tables): `users`, `recipes_write`, `recipe_steps_write`, `ingredients_write`, `units_write`, `ingredients_units_write`, `recipe_ingredients_write`, `tags_write`, `recipes_tags_write`, `reviews`

**Read model** (1 denormalized table): `recipes_read` with PostgreSQL `TEXT[]` array columns for tags, steps, and ingredients -- enabling single-query retrieval of complete recipe data.

**Fuzzy search indexes** use `pg_trgm` trigram GIN indexes on recipe titles, tag names, ingredient names, and unit names for typo-tolerant autocomplete and search.

---

## 📫 Connect & Get in Touch  
💻 **Portfolio:** [mateusznasewicz.dev](https://mateusznasewicz.dev)  
📧 [mateusznasewicz@proton.me](mailto:mateusznasewicz@proton.me)  
🔗 [LinkedIn](#)  

---

## License

This project was developed as a university course project. All rights reserved.
