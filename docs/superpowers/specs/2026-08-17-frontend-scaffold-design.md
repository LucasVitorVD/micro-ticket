# Frontend scaffold (pre-Auth0) — design

## Context
micro-ticket has no frontend yet. The user wants a Next.js frontend to eventually
integrate with Auth0, but wants to implement the Auth0 wiring themselves by
following the official docs. This spec covers only the scaffold that should
exist *before* that work starts: project setup, styling stack, and an axios
client pointed at the gateway. No auth logic, no login/profile pages, no
`@auth0/nextjs-auth0` install.

## Goals
- A working Next.js app (TypeScript, App Router) at `frontend/`, next to the
  other services at the repo root.
- Tailwind CSS and shadcn/ui installed and usable.
- An axios instance (`lib/api.ts`) with `baseURL` pointed at the gateway
  (`http://localhost:8080`), ready for the user to extend later (e.g. adding
  a request interceptor once Auth0's `getAccessToken()` exists).
- Default home page (`app/page.tsx`) minimally styled with Tailwind/shadcn to
  confirm the stack renders correctly — no auth-aware content.

## Non-goals
- No Auth0 SDK, no `middleware.ts`, no login/logout routes, no `/profile`
  page, no session handling. The user does this themselves next.
- No Docker/compose integration — runs locally via `npm run dev` for now.
- No pages/components beyond the default home page.

## Structure
```
frontend/
  app/
    layout.tsx
    page.tsx        # default home, styled minimally
    globals.css
  components/ui/     # shadcn components (button, card)
  lib/
    api.ts           # axios instance, baseURL = gateway
  .env.local          # NEXT_PUBLIC_GATEWAY_URL (gitignored)
```

## Environment
- `.env.local` (gitignored, not committed): `NEXT_PUBLIC_GATEWAY_URL=http://localhost:8080`

## Verification
- `npm run dev` starts cleanly, home page renders with Tailwind/shadcn
  styling applied.
- `npm run build` succeeds (confirms TypeScript/ESLint config is sound).
- `lib/api.ts` exports an axios instance whose `baseURL` matches the env var.
