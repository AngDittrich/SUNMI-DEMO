# Repo: SUNMI-DEMO

Monorepo with two independent projects. No shared build system between them.

## Structure

```
backend/       Node.js + Express + Prisma + SQLite + Socket.IO
mobile-kiosk/  Android (Kotlin, Jetpack Compose, Retrofit, Coil)
```

## Backend (`backend/`)

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
- API base URL hardcoded in mobile app: `http://192.168.10.4:3000/` — update `ApiService.kt` for different environments.
- Backend has no test suite or linting configured.

## Mobile Kiosk (`mobile-kiosk/`)

- **Build:** Gradle with AGP 9.3.1, Kotlin 2.2.10, Compose BOM 2026.02.01
- **Min SDK:** 24, Target SDK: 36, Compile SDK: 36
- **Key deps:** Retrofit 2.11, Coil 2.7, Navigation Compose 2.8.9, Socket.IO 2.1.1, Material Icons Extended

### Commands

```bash
cd mobile-kiosk
./gradlew :app:assembleDebug    # Build debug APK
./gradlew :app:lint             # Lint check
```

- Requires `JAVA_HOME` set — build fails without it.
- No custom lint or typecheck commands beyond Gradle defaults.

### Architecture

- **Entry:** `MainActivity.kt` — sets up navigation, cart state, API fetch
- **Navigation:** 3 routes: `product_list`, `product_detail/{productId}`, `cart`
- **Screens:** `SnackKioskScreen` (grid), `ProductDetailScreen` (detail), `CartScreen` (cart)
- **Data flow:** Retrofit fetches products → `Product` data class → cart managed via `mutableStateOf<List<CartItem>>` in MainActivity
- **Socket.IO:** `SocketManager.kt` exists but is not wired into any screen yet
- **Edge-to-edge:** `enableEdgeToEdge()` is active — screens that float content at the bottom must apply `navigationBarsPadding()`

### Key conventions

- All composables are `private` except screen-level entry points and `SnackCard`
- Product model is in `Product.kt` — Retrofit uses Gson converter, so field names must match JSON keys exactly (camelCase)
- The `Product` model has a `description` field with default `""` — the API may or may not return it
- Image loading uses Coil `AsyncImage` — images are URLs from the backend
- Theme colors defined in `ui/theme/Color.kt`: `NeonGreen`, `DarkCharcoal`, `DarkCardBg`, `LightBg`, `TextMuted`
