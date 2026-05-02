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

This is a Spring Boot + Kotlin project for managing organizational hierarchies following **hexagonal (ports & adapters)** architecture. The dependency rule is enforced: only infrastructure and REST depend on domain, never the reverse.

```
rest/ ──► domain/port/ ◄── domain/
               ▲
          infrastructure/
```

### Domain (`domain/`)

Framework-free. No Spring or JPA imports anywhere in this package.

- `Employee` — pure Kotlin `data class` modeling the org hierarchy node (manager/managed relationships).
- `Organization` — plain Kotlin class containing all business logic: building the hierarchy, cycle detection, single-root invariant.
- `IllegalOrganizationException` — domain exception with no HTTP knowledge.
- `domain/port/EmployeeRepositoryPort` — output port interface; defines what persistence must provide. Infrastructure implements this.
- `domain/port/OrganizationUseCase` — input port interface; defines the application's use cases. REST depends on this, not on `Organization` directly.

### Infrastructure (`infrastructure/`)

Owns all JPA/persistence concerns.

- `EmployeeJpaEntity` — JPA `@Entity` with self-referencing `@ManyToOne`/`@OneToMany(mappedBy)` relationship. Kept entirely separate from the domain `Employee`.
- `EmployeeCrudRepository` — Spring Data `CrudRepository<EmployeeJpaEntity, Long>`.
- `EmployeeRepository` — `@Component` implementing `EmployeeRepositoryPort`. Translates between `EmployeeJpaEntity` and domain `Employee` via two private mappers: `toDomainWithParents()` (walks up the manager chain, used for cycle detection) and `toDomainWithChildren()` (walks down the managed tree, used for the org display).

### REST (`rest/`)

- `OrganizationController` — `@RestController` that depends on `OrganizationUseCase` (port), not on `Organization` directly.
- `Representation.kt` — `topDown()` and `upstreamHierarchy()` functions that serialize domain `Employee` objects to nested `Map<String, Any>`.
- `GlobalExceptionHandler` — `@RestControllerAdvice` that maps `IllegalOrganizationException` to HTTP 400.

### Composition root

- `OrganizationConfig` — `@Configuration` that wires the plain `Organization` class with `EmployeeRepositoryPort`, keeping Spring DI out of the domain.

### H2 in-memory database

No persistent storage; schema is auto-created from `EmployeeJpaEntity` on startup.

## Testing strategy

- `@SpringBootTest` + `@Transactional` for `EmployeeCrudRepositoryTests` (hits real H2 via `EntityManager`).
- Mockito-Kotlin mocks on `EmployeeCrudRepository` for `EmployeeRepositoryTest` (unit-tests the mapping logic).
- Plain instantiation with mocked `EmployeeRepositoryPort` for `OrganizationTest` (no Spring context needed).
- Mocked `OrganizationUseCase` for `OrganizationControllerTest` (no Spring context needed).
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` for `IntegrationTests` (full HTTP round-trip).

## Key dependencies

- Java 25, Kotlin 2.3.21, Spring Boot 4.0.6
- Kotlin compiler plugins: `spring`, `jpa`, `all-open` (entities are opened automatically for JPA proxying)
- Jackson Kotlin module for JSON serialization of data classes
