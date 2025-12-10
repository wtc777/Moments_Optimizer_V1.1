# Moments Optimizer Frontend (Vue 3 + Vite + TS + Tailwind)

This is the new frontend that talks to the Spring Boot backend (port 8080) via relative `/api` paths and Vite proxy.

## Prerequisites
- Node.js 18+ and npm
- Backend running on `http://localhost:8080` (Spring Boot + Flyway + MySQL)

## Setup
```bash
cd frontend
npm install
```

## Development
```bash
npm run dev
```
- Opens Vite dev server (default http://localhost:5173).
- Proxy forwards `/api` and `/health` to `http://localhost:8080`.

## Build
```bash
npm run build
```
- Outputs production assets to `dist/` (do not commit build artifacts).

## Key Routes
- `/` create task (requires `userId`)
- `/tasks/:id` task detail with polling
- `/history` history list (requires `userId` input)
- `/history/:id` history detail

## Configuration Notes
- HTTP client uses relative URLs; no host is hard-coded.
- Polling interval and history page size are in `src/config/appConfig.ts`.
- UI text is managed via i18n in `src/locales/`.

