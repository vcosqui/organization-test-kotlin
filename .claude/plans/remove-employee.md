# Plan: Remove an employee from the organization

## Context

The organization currently has no way to remove an individual employee. The only mutation is `POST /organization`, which replaces the entire hierarchy. When someone leaves, there is no targeted operation — callers must re-POST the full structure. This feature adds `DELETE /organization/employee/{name}` as a first-class operation.

**Decisions captured here (will seed the ARD REASON section):**
- Employees with direct reports cannot be removed; caller must reassign them first (400).
- Unknown employee name returns 404 (new `EmployeeNotFoundException` type, separate from the existing 400-mapped `IllegalOrganizationException`).
- Successful delete returns 200 (consistent with existing endpoints).

---

## Step 0 — Workflow prerequisites (before any code)

1. Run `/new-issue` to create the GitHub issue.
2. Create `docs/ard/ARD-0001-remove-employee.md` from `docs/ard/template.md` and fill the **REASON** section only.
3. Create branch `issue-{N}-remove-employee`.

---

## Step 1 — Write failing acceptance tests (ATDD, red phase)

Add four new tests to:
`src/test/kotlin/com/company/organization/rest/IntegrationTests.kt`

```
GIVEN a leaf employee exists (no direct reports)
WHEN  DELETE /organization/employee/{name}
THEN  200 OK, subsequent GET does not include the employee

GIVEN an employee has direct reports
WHEN  DELETE /organization/employee/{name}
THEN  400 Bad Request, { "error": "..." }

GIVEN the root employee has no direct reports
WHEN  DELETE /organization/employee/{name}
THEN  200 OK, subsequent GET /organization returns empty

GIVEN an unknown employee name
WHEN  DELETE /organization/employee/{name}
THEN  404 Not Found, { "error": "..." }
```

Run `./mvnw test -Dtest=IntegrationTests` and confirm all four **fail** before proceeding.

---

## Step 2 — New domain exception

**New file:** `src/main/kotlin/com/company/organization/domain/EmployeeNotFoundException.kt`

```kotlin
class EmployeeNotFoundException(name: String) :
    RuntimeException("Employee '$name' not found")
```

No Spring or HTTP imports — pure domain.

---

## Step 3 — Domain: add `removeEmployee` to `Organization`

**File:** `src/main/kotlin/com/company/organization/domain/Organization.kt`

Add method:
```kotlin
fun removeEmployee(name: String) {
    val employee = _employees.find { it.name == name }
        ?: throw EmployeeNotFoundException(name)
    if (employee.managed.isNotEmpty())
        throw IllegalOrganizationException(
            "Cannot remove '$name': has ${employee.managed.size} direct report(s). Reassign them first.")
    employee.manager?.managed?.remove(employee)
    _employees.remove(employee)
}
```

---

## Step 4 — Output port: add `deleteByName`

**File:** `src/main/kotlin/com/company/organization/domain/port/EmployeeRepositoryPort.kt`

Add:
```kotlin
fun deleteByName(name: String)
```

---

## Step 5 — Input port: add `removeEmployee`

**File:** `src/main/kotlin/com/company/organization/domain/port/OrganizationUseCase.kt`

Add:
```kotlin
fun removeEmployee(name: String)
```

---

## Step 6 — Infrastructure: implement delete

**File:** `src/main/kotlin/com/company/organization/infrastructure/EmployeeCrudRepository.kt`

Add Spring Data method:
```kotlin
fun deleteByName(name: String)
```

**File:** `src/main/kotlin/com/company/organization/infrastructure/EmployeeRepository.kt`

Implement the port method:
```kotlin
override fun deleteByName(name: String) = crudRepository.deleteByName(name)
```

---

## Step 7 — Application service: implement use case

**File:** `src/main/kotlin/com/company/organization/application/OrganizationApplicationService.kt`

Add:
```kotlin
override fun removeEmployee(name: String) {
    val organization = Organization.reconstitute(repository.loadAll())
    organization.removeEmployee(name)        // domain validates; throws on error
    repository.deleteByName(name)            // persist only if domain approved
}
```

---

## Step 8 — REST: add DELETE endpoint

**File:** `src/main/kotlin/com/company/organization/rest/OrganizationController.kt`

Add:
```kotlin
@DeleteMapping("/organization/employee/{name}")
fun removeEmployee(@PathVariable name: String): ResponseEntity<Void> {
    organizationUseCase.removeEmployee(name)
    return ResponseEntity.ok().build()
}
```

---

## Step 9 — Exception handler: map 404

**File:** `src/main/kotlin/com/company/organization/rest/GlobalExceptionHandler.kt`

Add:
```kotlin
@ExceptionHandler(EmployeeNotFoundException::class)
fun handleNotFound(ex: EmployeeNotFoundException) =
    ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to ex.message))
```

---

## Step 10 — Green phase and complete ARD

Run `./mvnw test -Dtest=IntegrationTests` — all four new tests must pass.  
Run `./mvnw test` — full suite must stay green.

Fill in the **Change** and **Consequences** sections of `docs/ard/ARD-0001-remove-employee.md`.

---

## Step 11 — Commit

```bash
git add src/ docs/ard/ARD-0001-remove-employee.md
git commit -m "issue #N: add DELETE /organization/employee/{name}"
```

The commit hook will verify branch naming and the staged ARD.

---

## Files touched (summary)

| File | Change |
|------|--------|
| `domain/EmployeeNotFoundException.kt` | **new** |
| `domain/Organization.kt` | add `removeEmployee()` |
| `domain/port/EmployeeRepositoryPort.kt` | add `deleteByName()` |
| `domain/port/OrganizationUseCase.kt` | add `removeEmployee()` |
| `infrastructure/EmployeeCrudRepository.kt` | add `deleteByName()` Spring Data method |
| `infrastructure/EmployeeRepository.kt` | implement `deleteByName()` |
| `application/OrganizationApplicationService.kt` | implement `removeEmployee()` |
| `rest/OrganizationController.kt` | add `@DeleteMapping` |
| `rest/GlobalExceptionHandler.kt` | add 404 handler |
| `rest/IntegrationTests.kt` | add 4 ATDD tests (written first) |
| `docs/ard/ARD-0001-remove-employee.md` | **new** ARD |
