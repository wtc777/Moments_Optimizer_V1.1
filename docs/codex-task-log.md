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

## [2025-12-09] Task: Phase 3 async worker stub

**Context**
- Add a lightweight Spring Boot worker to progress tasks without touching the legacy Node backend; no model calls or schema changes.
- Enforce backend-only status transitions with logging and a configurable on/off flag.

**Changes**
- Added worker manager with ScheduledExecutorService scanning runnable tasks every 5s (bounded batch) to advance steps, log transitions, and mark success/failure with stubbed per-step results; lifecycle start/stop via config.
- Extended TaskService with transactional status/step transition helpers and limited runnable-task fetch to avoid overload; repository updated for paged status queries.
- Introduced worker configuration flag `moments.worker.enabled` (default true) and lifecycle wiring.

**Impact**
- Backend can autonomously progress tasks through placeholder steps, producing stubbed result JSON while keeping legacy Node untouched and schema stable.
- Provides a safe, controllable worker foundation ready for real model integration in later phases.

---

## [2025-12-10] Task: Phase 4 pluggable model client for worker

**Context**
- Replace in-worker stubs with a pluggable model client abstraction so future HTTP/provider integrations can be added without changing worker logic.
- Keep legacy Node untouched and avoid schema changes; use config-driven selection (stub vs http skeleton).

**Changes**
- Added ModelClient interface and ModelClientException (errorCode, message, rawResponseSnippet) plus StubModelClient (deterministic, stateless) and HttpModelClient skeleton with config-driven base URL/auth/timeout, no secrets logged.
- Added ModelClientConfig to select implementation via `moments.model.client-type` (default stub) and updated application config keys.
- Updated TaskWorkerManager to delegate step execution to ModelClient, handling ModelClientException to mark steps/tasks failed; wiring adjusted to inject ModelClient.

**Impact**
- Worker now uses a pluggable model layer: ready to switch to HTTP or provider-specific clients by config without altering task workflow; remains stubbed by default for safe dev use.

---

## [2025-12-10] Task: Phase 5 HTTP model client wiring

**Context**
- Implement real HTTP calls in HttpModelClient to a Node-based internal model endpoint while keeping the abstraction configurable and safe; no schema changes, legacy Node untouched (HTTP-only).

**Changes**
- Enhanced HttpModelClient to POST to `{baseUrl}/internal/model/runStep` with headers from config, including userId and parsed payload; handles 2xx/success=true responses by returning data, maps non-2xx or success=false to ModelClientException with error codes (HTTP_ERROR/REMOTE_ERROR/PARSE_ERROR/CONFIG_ERROR) and truncated response snippets.
- Added response parsing with unknown-field tolerance and payload parsing for requests; ensured no secrets are logged and timeouts remain config-driven.
- Left client-type default as stub; HTTP mode activates via `moments.model.client-type=http` plus base-url/auth/timeout config.

**Impact**
- Worker can now call a real HTTP model service via configuration without code changes, preserving safety and error visibility; stub remains default for local dev.

---

## [2025-12-10] Task: Phase 6 frontend scaffold (Vue 3 + Vite + TS + Tailwind)

**Context**
- Build a new frontend (separate from legacy Node) under `/frontend` using Vue 3 + Vite + TypeScript + Tailwind, integrating with Spring Boot APIs for tasks/history and health; enforce i18n and envelope-aware HTTP handling.

**Changes**
- Scaffolded Vite + Vue + TS + Tailwind app structure with router, i18n, config constants, and Axios httpClient that unwraps the success/data envelope and surfaces typed errors.
- Implemented views for core flows: Home (create task), Task detail (with polling), History list (paginated by user), History detail; shared layout/header plus LoadingSpinner and ErrorAlert components for consistent UX.
- Added task/history API wrappers, components (TaskForm, TaskStepsTimeline, TaskResultCard, HistoryItemCard), config constants for polling/pagination, and locale files (en/zh) to keep UI strings out of code.

**Impact**
- Frontend can create tasks, poll progress, display results, and browse history via Spring Boot APIs using a clean layout and reusable UI feedback; ready for further UX polish and auth integration without touching legacy Node.

---
