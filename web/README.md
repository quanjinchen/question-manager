# Vue Scaffold

`vue-scaffold` is a monorepo starter extracted from the current IAM admin project.

## Workspace

```text
vue-scaffold/
  apps/
    admin/
    h5/
  packages/
    api/
    constants/
    config/
    directives/
    h5-ui/
    pc-ui/
    styles/
    types/
    utils/
```

## Migrated capabilities

- `apps/admin` for the desktop admin app
- `apps/h5` for the mobile H5 app
- `packages/pc-ui` for Element Plus based desktop UI wrappers
- `packages/h5-ui` for Vant based mobile UI wrappers
- `packages/utils`, `packages/directives`, `packages/constants`, and `packages/types` for shared foundations
- `packages/api` for request infrastructure
- `packages/styles` for global styling

## Commands

```bash
pnpm install
pnpm dev
pnpm build
pnpm typecheck
pnpm lint
```
