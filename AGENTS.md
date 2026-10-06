# AGENTS.md

Orquestador para agentes que trabajen en este repositorio. Lee esta página, entra solo a la zona de la tarea y deja el resto quieto.

## Cómo elegir por dónde entrar

1. Clasifica el pedido con la tabla de abajo.
2. Lee el documento de esa fila antes de editar.
3. Cambia únicamente las rutas de la columna "Toca".
4. Si el pedido mezcla dos zonas, haz las dos, sin arrastrar archivos de una tercera.
5. No reescribas `docs/SUNMI/` ni `docs/SunmiDocs/`. Son manuales del fabricante. Cítalos; no los "limpies".
6. No hagas commit ni push salvo que el usuario lo pida.

| Pedido | Lee primero | Toca | Déjalo |
|---|---|---|---|
| Hub, tema azul/naranja | `docs/kiosk/architecture.md` | `DemoHubScreen.kt`, `ui/theme/` | El selector no vuelve al encabezado del POS |
| Catálogo, carrito, detalle, admin | `docs/kiosk/architecture.md` | `SnackKioskScreen.kt`, `ProductDetailScreen.kt`, `CartScreen.kt`, `WelcomeScreen.kt`, `AdminProductScreen.kt`, `data/` | Hotel y encuesta |
| Cobro POS o ticket de venta | `docs/kiosk/hardware.md` | `CartScreen.kt`, `OrderSummaryScreen.kt`, `SunmiPrinterManager.kt`, el cableado en `MainActivity.kt` | El cobro con efectivo no espera a la caja |
| Encuesta y cupón | `docs/kiosk/architecture.md` | `SurveyScreen.kt`, `SurveyThankYouScreen.kt`, `SurveyModels.kt` | POS y hotel |
| Reserva de hotel | `docs/kiosk/architecture.md` | `hotel/` | No restaures el check-in por RFID que se quitó |
| Impresora, caja, NFC, escáner | `docs/kiosk/hardware.md` y el capítulo de `docs/SUNMI/docs/` | `SunmiPrinterManager.kt`, `NfcScanManager.kt`, `BarcodeScanManager.kt` | No agregues el AAR de NFC propietario hasta que exista el archivo |
| Fotos de productos u hoteles | `docs/kiosk/architecture.md` | `app/src/main/assets/` y el seeder o los modelos que apuntan a esa ruta | No crees otra carpeta de imágenes en la raíz |
| Servidor Express | `backend/prisma/schema.prisma`, `backend/src/server.ts` | `backend/` | El kiosco sigue sin cliente HTTP |
| Manual SUNMI | el archivo que el usuario señaló | solo si pide corregir una extracción | El comportamiento de la app |

## Reglas que no se negocian

- La demo es offline. Sin permiso `INTERNET` y sin cliente HTTP en `mobile-kiosk/`.
- Un periférico ausente no corta el flujo. Impresión, NFC y caja fallan en log o en un estado visible, y la pantalla deja seguir.
- La caja se abre con `cashDrawerApi().open(PrintResult)` de PrinterX 1.0.20. Ese llamado es best-effort y no forma parte del resultado de la impresión.
- El NFC de pago solo se acepta cuando el usuario ya eligió tarjeta.
- Textos de interfaz en español. Min SDK 24: `java.util.Calendar`, no `java.time`.
- Composables `private`, excepto pantallas de entrada y `SnackCard`.
- Contenido pegado al borde inferior: `navigationBarsPadding()`.
- Prisma, si tocas el backend, se importa desde `../generated/prisma/client.js`. Nunca desde `@prisma/client`.
- Package manager del backend: pnpm.

## Build

```bash
cd mobile-kiosk
./gradlew :app:compileDebugKotlin
```

Si `JAVA_HOME` no está definido, usa el JBR de Android Studio. En Windows: `C:\Program Files\Android\Android Studio\jbr`.

## Mapa del repo

```
mobile-kiosk/     app de la demo
backend/          API opcional, no la usa el kiosco
docs/kiosk/       comportamiento actual de esta demo
docs/SUNMI/       manuales docs.sunmi.com
docs/SunmiDocs/   módulos del SDK SunmiCustomer
```

Índice de docs: `docs/README.md`. Detalle de pantallas y hardware: `docs/kiosk/`.
