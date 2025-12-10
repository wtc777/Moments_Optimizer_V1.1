# Moments Optimizer Backend (Spring Boot + MySQL + Flyway)

This service exposes REST APIs for tasks, history, and health, and runs a lightweight worker that can call pluggable model clients (stub by default, HTTP optional).

## Prerequisites
- JDK 17+
- MySQL instance
- Maven 3.9+ (项目使用 Maven，不使用 Gradle)
- 环境变量或本地配置文件提供数据源信息：
  - `DB_URL` (例如 `jdbc:mysql://localhost:3306/moments_optimizer?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4`)
  - `DB_USERNAME`
  - `DB_PASSWORD`

## Run (dev)
```bash
cd backend
mvn spring-boot:run
```
- 启动时 Flyway 会执行 `V1_0__baseline.sql`。
- 健康检查：`GET http://localhost:8080/health`

## Key Config (application.yml / application-dev.yml)
- `server.port`: 8080
- `spring.datasource`: uses env vars or local override
- `spring.flyway.enabled`: true
- Worker toggle: `moments.worker.enabled` (default true)
- Model client selection: `moments.model.client-type` (`stub` default, `http` optional)
  - HTTP client config: `moments.model.base-url`, `moments.model.auth-header`, `moments.model.auth-token`, `moments.model.timeout-ms`

## API Endpoints (enveloped responses)
- `POST /api/tasks` (create)
- `GET /api/tasks/{id}` (status/result)
- `GET /api/history?userId=...&page=0&size=20`
- `GET /api/history/{id}`
- `GET /health`

## Model Client
- Default: stub (no external calls)
- HTTP client (optional): POST `{baseUrl}/internal/model/runStep` with payload containing step/task/user context; enable via config without code changes.

## Notes
- Do not hard-code secrets; use env vars.
- No schema changes without new Flyway migrations.
- Worker runs single-threaded with bounded scanning; status transitions are service-controlled only.
- 现存的 Gradle 文件可忽略；请使用 Maven (`mvn spring-boot:run`)。若需清理，可手动删除 `backend/build.gradle`、`backend/settings.gradle`、`backend/gradle/`、`backend/gradlew*`（当前未生成 wrapper）。

## Local database config (keep secrets out of git)
- `application.yml` imports optional `application-local.yml` (gitignored).
- Create `backend/src/main/resources/application-local.yml` with your real credentials, e.g.:
  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/moments_optimizer?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
      username: root
      password: .Wellrainadmin123
  ```
- An example placeholder file is provided: `backend/src/main/resources/application-local.example.yml`. Do not commit real secrets.
