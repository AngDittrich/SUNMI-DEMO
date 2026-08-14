# Repo: SUNMI-DEMO

Monorepo with two independent projects. No shared build system between them.

## Structure

```
backend/       Node.js + Express + Prisma + SQLite + Socket.IO (optional / unused by kiosk demo)
mobile-kiosk/  Android (Kotlin, Jetpack Compose, Room, Coil) — fully offline POS demo
images/        Source product images (copied into app assets)
```

## Backend (`backend/`)

Optional reference server. The mobile kiosk **does not** require it for the offline demo.

- **Package manager:** pnpm (enforced via `devEngines` in package.json)
- **Runtime:** Node with ESM (`"type": "module"`), TypeScript targeting ES2022
- **Database:** SQLite via `better-sqlite3` adapter. DB file: `dev.db`
- **Prisma schema:** `prisma/schema.prisma` — output goes to `generated/prisma/` (not node_modules)

### Commands

```bash
cd backend
pnpm install
pnpm db:generate   # Regenerate Prisma client
pnpm db:migrate    # Run migrations
pnpm db:seed       # Seed database
pnpm dev           # Start dev server with hot reload (tsx watch)
pnpm build         # prisma generate + tsc
pnpm start         # Run compiled JS
```

### Gotchas

- Prisma client imports from `../generated/prisma/client.js` (relative, not package name). Never import `@prisma/client` directly.
- `.env` is gitignored. Required vars: `DATABASE_URL` (defaults to `file:./dev.db`), `PORT` (defaults to 3000).
- `pnpm-workspace.yaml` exists but only configures build allowlist for native modules — there are no workspace packages.
- Backend has no test suite or linting configured.

## Mobile Kiosk (`mobile-kiosk/`)

- **Build:** Gradle with AGP 9.3.1, Kotlin 2.2.10, Compose BOM 2026.02.01, Room 2.7.2
- **Min SDK:** 24, Target SDK: 36, Compile SDK: 36
- **Key deps:** Room, Coil 2.7, Navigation Compose 2.8.9, Material Icons Extended
- **Data:** Local Room SQLite (`kiosco.db`); catalog seeded on first launch
- **Images:** Bundled under `app/src/main/assets/products/` (`file:///android_asset/products/...`)
- **Offline by design:** no network permissions in the manifest and no HTTP client on the classpath

### Commands

```bash
cd mobile-kiosk
./gradlew :app:assembleDebug    # Build debug APK
./gradlew :app:lint             # Lint check
```

- Requires `JAVA_HOME` set — build fails without it.
- No custom lint or typecheck commands beyond Gradle defaults.

### Architecture

- **Entry:** `MainActivity.kt` — navigation, cart state, Room repository
- **Data:** `data/ProductRepository`, `AppDatabase`, `ProductSeeder`
- **Navigation:** welcome, product list/detail, order summary, admin list/form
- **Screens:** `SnackKioskScreen`, `ProductDetailScreen`, `CartScreen`, admin screens
- **Scanner:** `BarcodeScanManager` (SUNMI broadcast); works with CPad scanner / Blink2 USB
- **Edge-to-edge:** `enableEdgeToEdge()` is active — screens that float content at the bottom must apply `navigationBarsPadding()`

### Key conventions

- All composables are `private` except screen-level entry points and `SnackCard`
- Product model is in `Product.kt` — field names match Room entity
- Image loading uses Coil `AsyncImage` with local asset URIs (offline)
- Theme colors defined in `ui/theme/Color.kt`: `NeonGreen`, `DarkCharcoal`, `DarkCardBg`, `LightBg`, `TextMuted`
