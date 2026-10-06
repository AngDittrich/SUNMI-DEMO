# SUNMI-DEMO

Demo de kiosco para dispositivos SUNMI: punto de venta, encuesta de satisfacción y reserva de hotel. Corre sin red. La impresora, el escáner, el NFC y la caja se usan cuando el equipo los tiene; si no están, la demo sigue.

## Proyectos

| Carpeta | Rol |
|---|---|
| `mobile-kiosk/` | App Android (Kotlin, Jetpack Compose, Room). Es la demo. |
| `backend/` | API de referencia con Express, Prisma y SQLite. El kiosco no la usa. |
| `docs/` | Guía del kiosco y manuales SUNMI. Índice en [docs/README.md](docs/README.md). |

## App

Requisitos: Android Studio y `JAVA_HOME` apuntando a un JDK 17 o al JBR de Android Studio.

```bash
cd mobile-kiosk
./gradlew :app:assembleDebug
```

El APK de depuración queda en `mobile-kiosk/app/build/outputs/apk/debug/`.

Al abrirla se elige el tema SYSCOM (azul) o SUNMI (naranja) y una de las tres demos. El catálogo, las fotos y la base Room van dentro de la app.

## Servidor de referencia

```bash
cd backend
pnpm install
pnpm db:migrate
pnpm db:seed
pnpm dev
```

Escucha en el puerto 3000. Variables: `DATABASE_URL` (por defecto `file:./dev.db`) y `PORT`.

## Licencia

MIT. Ver [LICENSE](LICENSE).
