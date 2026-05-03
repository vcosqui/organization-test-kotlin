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

## Development Workflow

Every task follows this mandatory sequence. Do not skip or reorder steps.

### Step 1 — Issue reference

Before any other action, confirm a GitHub issue number. If one was not provided, ask:
> "Which GitHub issue does this work address? (e.g., #42)"

Never start work without a traceable issue.

### Step 2 — ARD: REASON section first

Create `docs/ard/ARD-NNNN-title.md` from `docs/ard/template.md` **before opening any source file**.

Fill in only the **REASON** section:
- What problem is being solved?
- Why now?
- What is the cost of not doing this?
- Why this approach over alternatives?

This is the most important step. The reason is the only information that cannot be reconstructed from the diff later. If you cannot fill this in clearly, stop and ask the user to clarify before proceeding.

### Step 3 — Branch

Create a branch named `issue-{number}-{kebab-description}`. Never work on `master`.

```bash
git checkout -b issue-42-add-employee-endpoint
```

### Step 4 — Explore

Use the Explore subagent to understand the relevant code paths. Gather evidence before forming conclusions. Do not suggest solutions yet.

### Step 5 — Plan

Use the Plan subagent to design the solution. Present the plan to the user and wait for explicit approval before writing any production code.

### Step 6 — ATDD (tests before implementation)

Write a failing acceptance test **first**. Tests must be black-box: use `IntegrationTests.kt` with `@SpringBootTest(webEnvironment = RANDOM_PORT)` against the real HTTP layer.

1. Write the test expressing the new behavior.
2. Run it and confirm it **fails** (`./mvnw test -Dtest=IntegrationTests`).
3. Implement the minimum code to make it pass.
4. Re-run and confirm it **passes**.

Do not write implementation code before seeing a red test.

### Step 7 — Complete the ARD

After the change is implemented and tests pass, return to the ARD and fill in:
- **Change**: which layers were touched, which contracts were altered.
- **Consequences**: trade-offs, new risks, follow-up work, metrics to watch.

### Step 8 — Commit

Stage the ARD together with the source changes. The commit hook will block any commit that lacks a staged ARD or violates the branch naming convention.

```bash
git add src/ docs/ard/ARD-NNNN-title.md
git commit -m "issue #42: ..."
```

Non-functional changes (CI, deps, refactors, config) require ARDs too.

---

## Key dependencies

- Java 25, Kotlin 2.3.21, Spring Boot 4.0.6
- Kotlin compiler plugins: `spring`, `jpa`, `all-open` (entities are opened automatically for JPA proxying)
- Jackson Kotlin module for JSON serialization of data classes
