# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

```bash
# Build the project
./gradlew build

# Run the application (port 8083)
./gradlew bootRun

# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests com.suresell.mscoreapp.YourTestClass

# Build production JAR
./gradlew bootJar

# Docker build
docker build -t ms-core-app .
```

## Architecture

This is a Spring Boot 3.4.1 microservice following **Hexagonal Architecture** (Ports & Adapters pattern).

### Layer Structure

```
src/main/java/com/suresell/mscoreapp/
├── application/
│   ├── dto/          # Request/Response objects for API
│   └── usecase/      # Business logic services and mappers
├── domain/
│   ├── model/        # JPA entities
│   └── port/out/     # Repository interfaces (contracts)
├── infrastructure/
│   ├── config/       # Spring configuration (OpenAPI, CORS)
│   ├── persistence/  # Repository implementations
│   │   └── jpa/      # Spring Data JPA repositories
│   └── web/adapter/  # REST controllers
└── shared/
    ├── enums/        # Domain enums (ValeraStatus, AccountStatus, etc.)
    └── exception/    # Custom exceptions
```

### Key Patterns

- **Repository Ports**: Interfaces in `domain/port/out/` define contracts, implementations in `infrastructure/persistence/` adapt to Spring Data JPA
- **MapStruct Mappers**: Entity-to-DTO mapping via MapStruct (requires `@Mapper(componentModel = "spring")`)
- **Use Cases**: Service classes named `Manage*UseCase` or `*Service` in application layer

## Domain Concepts

- **Valeras**: Meal voucher system with types, status tracking, and expiration
- **Account Receivable**: Customer debt/credit management with transactions
- **Supply Management**: Inventory with categories and consumption tracking
- **Meal Preparation**: Weekly meal planning
- **Shopping List**: Procurement tracking

## API

- **Base path**: `/api/core`
- **Port**: 8083
- **Swagger UI**: `http://localhost:8083/api/core/swagger-ui.html`
- **OpenAPI spec**: `/api/core/openapi`

## Key Dependencies

- Spring Data JPA with PostgreSQL
- MapStruct 1.5.5 for DTO mapping (use with Lombok binding)
- SpringDoc OpenAPI 2.8.3 for API documentation
- Apache POI for Excel export

## important
-  KISS principle
-  Patterns Desing
-  Simplicity
-  economy in services cloud
