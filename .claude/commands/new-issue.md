---
description: Create a GitHub issue with structured REASON, type, and GIVEN/WHEN/THEN acceptance criteria for the ATDD+ARD workflow
---

Guide the user through creating a well-structured GitHub issue that captures the REASON behind the change and acceptance criteria that will directly drive the ATDD tests.

## Your goal

Gather the information below through conversation, then create the issue with `gh issue create`. Do not create the issue until the user has answered every required field. Required fields are marked with *.

## Information to gather

Ask these questions one section at a time. Do not dump all questions at once.

### 1. Type and title *

Ask: "What type of change is this?"
- `feature` — new user-facing behavior
- `bug` — incorrect existing behavior
- `non-functional` — no behavior change (sub-types: `refactor`, `performance`, `security`, `ci`, `deps`, `config`)

Then ask for a short title (imperative, max 72 chars, e.g. "Add DELETE /employees/{id} endpoint").

### 2. REASON *

This is the most important section. Ask each question separately and push back if answers are vague.

**Problem** — "What specific problem, gap, or risk does this address? Describe the observable symptom or unmet need — not the solution you have in mind."

**Trigger** — "Why is this being raised now? What happened or changed? (e.g., user report, production incident, product decision, performance data, security finding, tech debt threshold reached)"

**Cost of inaction** — "What happens if this is never done? Be concrete — degraded UX, security exposure, blocked roadmap, mounting maintenance burden, etc."

If the user cannot answer these clearly, say: "Let's pause here — the issue isn't ready to be written until the reason is clear. What specifically prompted this?" Do not proceed until the reason is solid.

### 3. Acceptance criteria *

Ask: "What must be true for this to be considered done? Write each criterion as a testable statement — these will become the ATDD integration tests."

Format each criterion as:
- `GIVEN <precondition> WHEN <action> THEN <expected outcome>`

Push back on vague criteria like "it should work" or "it should be better". Each criterion must be specific enough to write a test for.

Minimum one criterion for bugs, two for features.

### 4. Out of scope (optional)

Ask: "Is there anything related that should explicitly NOT be addressed in this issue? Listing out-of-scope items prevents scope creep."

## Issue body format

Once all required fields are gathered, create the issue with this body:

```
## Type
{type}

## REASON

### Problem being solved
{problem}

### Why now?
{trigger}

### Cost of not doing this
{cost_of_inaction}

## Acceptance criteria
{each criterion as a checklist item: - [ ] GIVEN ... WHEN ... THEN ...}

## Out of scope
{out_of_scope or "N/A"}

---
> This issue was created with /new-issue. The REASON section will seed the ARD when the branch is opened.
```

## After creating the issue

1. Show the issue URL and number.
2. Tell the user the next step: `git checkout -b issue-{number}-{kebab-title}`
3. Remind them to copy the REASON section into `docs/ard/ARD-NNNN-title.md` when they start the branch — it should not be rewritten from scratch, just refined.
