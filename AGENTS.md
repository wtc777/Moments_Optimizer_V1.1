# AGENTS.md — Codex CLI Authoritative Development Rules

Codex CLI MUST treat this file as the highest-priority rule set for this repository.
This project uses a **Vue 3 + Vite + TypeScript** frontend and a **Spring Boot** backend.
All rules below are binding for any Codex CLI operation.

---

## 0. Purpose

This document defines:

- What Codex CLI is allowed to modify
- Safety & patching rules
- Internationalization (i18n) rules
- Frontend coding standards for Vue + Tailwind (local build only)
- Backend editing rules for Spring Boot
- Dev vs Prod integration behavior
- Task execution logging in Markdown (English only)

Codex MUST strictly follow all rules.

---

# 1. Repository Scope & Modification Rules

## 1.1 High-level Structure (Target)

The repository is expected to have a structure similar to:

- `/frontend/`
  - `src/`
    - `components/`
    - `views/`
    - `store/`
    - `router/`
    - `api/`
    - `locales/`
    - `styles/`
  - `public/`
- `/backend/`
  - Spring Boot application (Kotlin/Java)
- `/docs/`
  - `AGENTS.md` (this file)
  - `architecture.md`
  - `dev-setup.md`
  - `codex-task-log.md` (see Section 9)

Codex MUST respect the actual repo layout and these semantics.

## 1.2 Allowed to Modify (By Default)

Unless the user explicitly states otherwise, Codex may edit:

### Frontend (Vue 3)

- `/frontend/src/components/**`
- `/frontend/src/views/**`
- `/frontend/src/api/**`
- `/frontend/src/store/**`
- `/frontend/src/styles/**`
- `/frontend/src/locales/**` (JSON only)
- `/frontend/public/**` (but NO deletions or renames unless explicitly authorized)

### Documentation

- `/docs/**` (including `codex-task-log.md`)

## 1.3 Restricted / Protected

Codex MUST NOT modify:

- Build outputs:
  - `/frontend/dist/**`
  - Bundled/minified JS/CSS
- Backend critical configuration files, unless explicitly requested:
  - `backend/pom.xml`
  - `backend/build.gradle*`
  - `backend/src/main/resources/application*.yml`
  - `backend/src/main/resources/application*.properties`
- Database migration history files, unless explicitly requested
- Deployment scripts / CI/CD configuration, unless explicitly requested

Codex MUST ask the user before:

- Deleting any file
- Renaming or moving directories
- Performing large-scale refactors across modules

---

# 2. Internationalization (i18n) Rules

## 2.1 Core Rule

> **No Chinese characters are allowed in any code file.**

This includes:

- Frontend: `.js`, `.ts`, `.vue`, `.jsx`, `.tsx`
- Backend: `.kt`, `.java`, `.yml`, `.properties` (config keys and values)

All Chinese UI text MUST be stored in i18n resource files, for example:

- `/frontend/src/locales/zh/*.json`

## 2.2 Usage in Vue

- Use an i18n library (e.g., `vue-i18n`) and call `t("key")` in components.
- Do NOT hard-code Chinese literals directly into templates or scripts.
- English literals are allowed in code where appropriate, but UI-facing texts SHOULD be centralized in i18n resources.

Example:

```ts
const { t } = useI18n();
const title = t("moment.title");
```

---

# 3. Patch Safety Rules

Codex MUST adhere to the following safety principles:

1. Prefer **small, focused patches** over large changes.
2. NEVER delete files unless the user explicitly instructs this.
3. NEVER modify build output files (e.g., `dist` bundles, generated CSS).
4. NEVER perform large refactors (multiple files, global renames) without clear approval.
5. If patch markers do not match or the file structure is unclear:
   - STOP and ask the user for clarification.
6. When unsure about potential impact:
   - Ask the user before proceeding.

Safety ALWAYS has higher priority than assumptions.

---

# 4. Frontend Coding Guidelines (Vue + TypeScript + Tailwind)

These rules adapt the previous "HTML = structure / CSS = style / JS = behavior" principle into the Vue Single-File Component (SFC) world.

## 4.1 General Principles

- Use Vue 3 SFCs with:
  - `<template>`
  - `<script setup lang="ts">`
  - `<style scoped>` (or global styles where appropriate)
- Templates define structure and declarative bindings.
- Styles are handled by Tailwind utility classes and/or scoped CSS.
- Logic is in TypeScript (composition API, composables, stores).

Forbidden in Vue templates:

- Inline `<script>` tags
- jQuery usage
- Hard-coded Chinese text

## 4.2 Vue SFC Layout

Standard SFC pattern:

```vue
<template>
  <button
    class="px-4 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700"
    @click="handleClick"
  >
    {{ t("moment.generateButton") }}
  </button>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";

const { t } = useI18n();

function handleClick() {
  // business logic here
}
</script>

<style scoped>
/* Optional component-specific styles when Tailwind is not sufficient */
</style>
```

## 4.3 TypeScript Rules

- Enable strict type checking.
- Prefer explicit types for public-facing functions.
- Extract shared logic into composables or utility modules.
- Avoid very large components; split into smaller, focused components.

---

# 5. Backend Editing Rules (Spring Boot)

The backend is a Spring Boot application and should be treated as sensitive.

Codex MUST:

- NOT edit backend code unless the user explicitly requests a backend change.
- When editing is requested:
  - Follow existing architectural patterns (controllers/services/repositories).
  - Keep changes localized to the described scope.
  - Avoid introducing new frameworks or patterns without approval.
  - Avoid changing ports, DB connection details, or other environment-specific settings unless explicitly instructed.

---

# 6. Tailwind CSS Rules (Local Build Only, NO CDN)

The project MUST rely on local Tailwind builds only. CDN usage is forbidden.

## 6.1 CDN is Strictly Forbidden

Codex MUST NOT introduce:

```html
<script src="https://cdn.tailwindcss.com"></script>
<link href="https://cdn.xxx/tailwind.min.css" rel="stylesheet">
```

or any other external Tailwind CDN.

Any CSS or JS dependency MUST be:

- Installed via npm/yarn/pnpm and bundled by Vite, or
- Served from local static assets in `/frontend/public/**`.

## 6.2 Tailwind Setup (Vite)

The typical Tailwind setup includes:

- `frontend/tailwind.config.js`
- `frontend/postcss.config.js`
- `frontend/src/styles/tailwind.css`

Example `tailwind.css`:

```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

Codex MUST:

- Modify only the **source** CSS files, not generated bundles.
- Keep Tailwind `content` paths correct and up to date when new components/views are added, e.g.:

```js
// tailwind.config.js
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {}
  },
  plugins: []
};
```

## 6.3 Arbitrary Value Classes

Arbitrary value classes such as `w-[880px]`, `tracking-[0.2em]` are allowed.

Codex MUST ensure:

- They appear in files covered by `content[]`, OR
- They are added to a `safelist` in `tailwind.config.js` if needed.

---

# 7. Local JS Library Rules

If libraries such as `marked`, `html2canvas`, or similar are used:

- They MUST be loaded from:
  - `node_modules` via ES imports, or
  - Local static assets in `/frontend/public/js/libs/**`.

Codex MUST NOT introduce CDN-based versions of these libraries unless explicitly authorized by the user.

---

# 8. Dev / Prod Integration Rules

## 8.1 Development Environment

Frontend (Vite):

```bash
cd frontend
npm install
npm run dev
```

- Vite dev server runs on a local port (e.g., 5173).
- Provides HMR and local tooling.

Backend (Spring Boot):

```bash
cd backend
./gradlew bootRun
```

or:

```bash
cd backend
mvn spring-boot:run
```

### 8.1.1 API Proxy (Vite Server)

Frontend should call backend APIs via a proxy, e.g. in `vite.config.ts`:

```ts
export default defineConfig({
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  }
});
```

Codex MUST NOT break or remove this proxy configuration without explicit instruction.

---

## 8.2 Production Environment

Typical production build pipeline:

```bash
# Frontend build
cd frontend
npm install
npm run build

# Backend build
cd backend
./gradlew clean build
# or: mvn clean package
```

- The frontend `dist/` output is served either:
  - Via a static file server / CDN + reverse proxy, or
  - Directly by Spring Boot as static resources (depending on deployment design).

Codex MUST NOT change the fundamental deployment model unless explicitly instructed by the user.

---

# 9. Task Execution Logging (Markdown, English Only)

For **every non-trivial task** (feature addition, bug fix, refactor, or architectural change) performed by Codex, it MUST:

1. Produce an English summary of what was done.
2. Append a new entry to:

   ```
   /docs/codex-task-log.md
   ```

3. If `/docs/codex-task-log.md` does not exist, Codex MUST create it.

## 9.1 Log Entry Format

Each entry MUST follow this structure:

```markdown
## [YYYY-MM-DD] Task: <Short Task Title>

**Context**
- Brief description of the problem or request.

**Changes**
- Key code changes and files touched.

**Impact**
- How this affects behavior, users, or future work.

---
```

Rules:

- Log entries MUST be in English only.
- No Chinese content in the log file.

---

# 10. Build Instructions (Reference)

### Frontend

```bash
cd frontend
npm install
npm run dev      # development
npm run build    # production build
```

### Backend

```bash
cd backend
./gradlew bootRun         # dev (Gradle)
./gradlew clean build     # prod build (Gradle)
# or:
mvn spring-boot:run       # dev (Maven)
mvn clean package         # prod build (Maven)
```

Codex MUST respect these commands and MUST NOT introduce new build entrypoints without user approval.

---

# 11. Final Notes for Codex

- Edit **source files only**, NEVER build outputs.
- Use local dependencies only; CDN is forbidden for Tailwind and major JS/CSS libraries unless explicitly authorized.
- Always keep i18n consistent: no Chinese in code, use locales.
- Keep patches safe, small, and easy to review.
- When in doubt or when a change seems risky, Codex MUST ask the user before proceeding.

This AGENTS.md supersedes all previous Codex-related specification files for this repository.
