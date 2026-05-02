# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./mvnw clean install        # Build
./mvnw test                 # Run all tests
./mvnw test -Dtest=EmployeeTest  # Run a single test class
./mvnw spring-boot:run      # Run the application
```

## Architecture

This is a Spring Boot + Kotlin project for managing organizational hierarchies, structured in three layers:

**REST** (`rest/`) → **Domain** (`domain/`) → **Infrastructure** (`infra/`)

- `OrganizationController` exposes `GET /organization` — both to retrieve the org structure as hierarchical JSON and to set it from a `Map<String, String>` (supervisor → employee mapping).
- `Representation.kt` contains `topDown()`, a recursive function that converts the flat employee list into a nested JSON tree.
- `Organization` is a Spring `@Component` holding business logic for building and querying the org structure.
- `Employee` is a JPA entity with a self-referencing many-to-one relationship (`manager`) to model the hierarchy.
- `EmployeeRepository` wraps `EmployeeCrudRepository` (Spring Data JPA `CrudRepository`) with higher-level operations.
- H2 in-memory database is used at runtime; no persistent storage.

## Testing strategy

- `@DataJpaTest` for repository-layer tests (hits real H2 database).
- Mockito-Kotlin mocks for unit tests of `EmployeeRepository`.
- `@SpringBootTest` for integration tests in `IntegrationTests.kt`.

## Key dependencies

- Java 11, Kotlin 1.4.21, Spring Boot 2.4.2
- Kotlin compiler plugins: `spring`, `jpa`, `all-open` (entities are opened automatically for JPA proxying)
- Jackson Kotlin module for JSON serialization of data classes
