## [2025-12-09] Task: Backend foundation with Flyway baseline

**Context**
- Start Phase 1 of refactor: stand up Spring Boot backend on port 8080 while keeping legacy Node service untouched.
- Establish Flyway baseline mirroring existing SQLite schema (users, activation codes/logs, analysis logs/history, tasks/steps) with required indexes.

**Changes**
- Added Gradle-based Spring Boot project skeleton with dev profile placeholders, MySQL datasource config, and Flyway enabled.
- Introduced baseline migration `V1_0__baseline.sql` covering all legacy tables and indexes without constraints that could alter behavior.
- Created JPA entities and repositories reflecting the current data model, plus a `/health` readiness endpoint.

**Impact**
- Backend can be configured to run on port 8080 against MySQL with schema managed by Flyway; legacy Node service remains untouched for now.
- Provides a ready baseline for incremental API and domain migration in later phases.

---

## [2025-12-09] Task: Phase 2 REST API parity (tasks/history)

**Context**
- Implement Spring Boot REST endpoints to mirror legacy Node task and history reads while keeping the Node service active.
- Enforce response envelopes, validation, and status control in-service; no schema changes or async worker/model logic yet.

**Changes**
- Added API response wrapper, error codes, exceptions, and global handler for consistent envelopes and HTTP codes.
- Implemented task creation/status retrieval controllers and services with default step templates matching legacy labels; status is backend-controlled only.
- Added history listing and detail retrieval with pagination (page/size) and DTO mapping; repositories extended for ordered fetches.

**Impact**
- New backend on port 8080 can create tasks (PENDING with steps) and read task/history data safely without touching the legacy Node flow.
- Establishes a clean service/DTO layering ready for later worker/model integration and eventual frontend migration.

---
