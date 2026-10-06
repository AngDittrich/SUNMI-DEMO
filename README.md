# SUNMI-DEMO

Suite de demostración para terminales SUNMI (SYSCOM + SUNMI). Tres recorridos táctiles salen del mismo hub: punto de venta, encuesta y reserva de hotel. Todo funciona **sin internet**; catálogo, imágenes y datos van dentro de la app.

En el hub eliges el tema **SYSCOM** (azul) o **SUNMI** (naranja) para toda la sesión. Si un periférico no está conectado o falla, la demo **sigue** (impresión, NFC, escáner o caja se registran en log o en pantalla, pero no bloquean el flujo).

## Las tres demos

### Punto de venta (POS)

Simula una tienda de snacks: portada, catálogo con búsqueda y filtros, detalle de producto, carrito y cobro.

**Qué puedes mostrar en el dispositivo**

| Capacidad | Dónde se ve |
|---|---|
| **Pantalla táctil** | Navegación, carrito, animación al agregar productos |
| **Escáner de código de barras** | En el catálogo: escanea y agrega al carrito (modo broadcast SUNMI, no teclado) |
| **NFC / tarjeta** | En catálogo: tag con el mismo código que `barcode` o `nfcId` del producto. En cobro: elige **Tarjeta** y acerca la tarjeta o tag para confirmar el pago |
| **Impresora térmica** | Tras pagar: ticket con líneas, cantidades y total (`PrinterX`) |
| **Caja registradora (RJ12)** | Al elegir **Efectivo** en el cobro: intento de apertura vía impresora; si no hay caja, el cobro y el ticket siguen |
| **Modo empleado** | Candado en el encabezado del catálogo (PIN de 4 dígitos): alta/edición de productos; el escáner abre el formulario del código leído |

### Encuesta de satisfacción

Formulario de opinión con calificación y comentarios; al enviar muestra agradecimiento y un cupón en pantalla.

**Qué puedes mostrar en el dispositivo**

| Capacidad | Dónde se ve |
|---|---|
| **Pantalla táctil** | Preguntas, estrellas y envío |
| **Impresora térmica** | Cupón impreso con texto y **QR** con payload `SYSCOM-SUNMI` |
| Escáner / NFC | No forman parte de este flujo (se ignoran en estas pantallas) |

Si la impresora falla, puedes continuar sin imprimir o reintentar desde la misma pantalla.

### Reserva de hotel

Catálogo de tres hoteles en Ciudad de México, habitaciones, calendario de fechas, pago y comprobante tipo ticket.

**Qué puedes mostrar en el dispositivo**

| Capacidad | Dónde se ve |
|---|---|
| **Pantalla táctil** | Listado, ficha del hotel, fechas y resumen |
| **NFC / tarjeta** | En pago: **Tarjeta** y zona de acercamiento (misma lógica que el POS) |
| **Impresora térmica** | Comprobante de reserva al pagar; botón **Hacer check-out** imprime comprobante de salida |
| **Caja registradora (RJ12)** | Con **Efectivo** en el pago (best-effort, no bloquea la reserva ni la impresión) |
| Escáner | Ignorado durante el flujo hotel |

## Proyectos del repositorio

| Carpeta | Rol |
|---|---|
| `mobile-kiosk/` | App Android (Kotlin, Jetpack Compose, Room). Es la demo. |
| `backend/` | API de referencia con Express, Prisma y SQLite. **El kiosco no la usa.** |
| `docs/` | Guía del kiosco y manuales SUNMI. Índice en [docs/README.md](docs/README.md). Detalle de flujos: [docs/kiosk/architecture.md](docs/kiosk/architecture.md). Hardware: [docs/kiosk/hardware.md](docs/kiosk/hardware.md). |

## Compilar la app

Requisitos: Android Studio y `JAVA_HOME` (JDK 17 o el JBR de Android Studio).

```bash
cd mobile-kiosk
./gradlew :app:assembleDebug
```

APK de depuración: `mobile-kiosk/app/build/outputs/apk/debug/`.

En Windows, si Gradle no encuentra Java:  
`$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`

## Servidor de referencia (opcional)

```bash
cd backend
pnpm install
pnpm db:migrate
pnpm db:seed
pnpm dev
```

Puerto 3000. Variables: `DATABASE_URL` (por defecto `file:./dev.db`) y `PORT`.

## Licencia

MIT. Ver [LICENSE](LICENSE).
