# ARD-0001: Add DELETE /organization/employee/{name} endpoint

**Issue**: #1
**Branch**: issue-1-remove-employee
**Date**: 2026-05-03
**Type**: functional
**Author**: Victor Cosqui

---

## REASON

### Problem being solved

The organization API has no targeted delete operation. The only mutation is `POST /organization`, which replaces the entire hierarchy. When an employee leaves, callers must re-POST the full structure — there is no way to remove a single person.

### Why now?

Roadmap item: needed to keep the organization up to date when an employee leaves.

### Cost of not doing this

GDPR restrictions on stale data and a contaminated/inaccurate hierarchy that reflects employees who have already left.

### Why this approach over alternatives?

- **Cascading delete (auto-reassign reports)**: ruled out — silently restructuring the hierarchy would hide the organizational gap; callers must explicitly reassign reports first to keep the hierarchy intentional.
- **Soft delete / archive flag**: ruled out — the domain has no audit trail requirement and the added complexity is not justified now.
- **Targeted delete with 400 guard on managed employees**: chosen — keeps the invariant explicit and forces callers to act deliberately.

---

## Change

Four layers touched; no existing contracts broken:

- **Domain** — `EmployeeNotFoundException` (new) and `Organization.removeEmployee()` (new). The method validates two invariants before mutating: employee must exist (throws `EmployeeNotFoundException`) and must have no direct reports (throws `IllegalOrganizationException`). Only then is the employee removed from the in-memory list and unlinked from its manager's `managed` list.
- **Ports** — `OrganizationRepositoryPort.deleteByName()` (new output port method); `OrganizationUseCase.removeEmployee()` (new input port method).
- **Infrastructure** — `EmployeeCrudRepository.deleteByName()` (Spring Data derived delete); `OrganizationRepositoryAdapter.deleteByName()` delegates straight to the CRUD repository.
- **REST** — `DELETE /organization/employee/{name}` endpoint added to `OrganizationController`; `GlobalExceptionHandler` gains a 404 handler for `EmployeeNotFoundException`.

## Consequences

- **Trade-offs accepted**: callers must explicitly remove direct reports before deleting a manager. Cascading reassignment was ruled out as it would silently restructure the hierarchy.
- **New risks**: none beyond what already exists — the domain guard and the 400/404 responses make the constraint visible to callers.
- **Follow-up work**: no batch delete; no audit trail of removals. Both are out of scope for this issue.
- **Metrics to watch**: 400 rate on DELETE (indicates callers not reassigning reports before deleting).
