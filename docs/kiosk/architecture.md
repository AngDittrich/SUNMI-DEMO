# Arquitectura del kiosco

Demo offline para dispositivos SUNMI. Tres recorridos salen del hub: punto de venta, encuesta y reserva de hotel. El tema (SYSCOM azul o SUNMI naranja) se elige en el hub y dura lo que dure la sesión.

El servidor de `backend/` es una referencia aparte. La app no lo llama y no tiene permiso de red.

## Árbol

```
mobile-kiosk/app/src/main/java/com/example/kiosco/
  MainActivity.kt          navegación, carrito, NFC, impresión
  DemoHubScreen.kt         hub y selector de tema
  WelcomeScreen.kt         portada del POS
  SnackKioskScreen.kt      catálogo
  ProductDetailScreen.kt
  CartScreen.kt            carrito y cobro
  OrderSummaryScreen.kt    ticket del POS
  AdminProductScreen.kt    alta y edición en modo empleado
  EmployeePinDialog.kt
  SurveyScreen.kt
  SurveyThankYouScreen.kt
  AddToCartFly.kt
  SunmiPrinterManager.kt
  NfcScanManager.kt
  BarcodeScanManager.kt
  data/                    Room: ProductEntity, DAO, seeder, repositorio
  hotel/                   catálogo, fechas, pago y ticket de hotel
  ui/theme/                BrandTheme, paleta, tipografía
```

Las fotos que ve el usuario viven en `app/src/main/assets/` (`products/`, `brand/`, `demo/` y las imágenes de hotel en la raíz de assets). Coil las carga con `file:///android_asset/...`.

## Navegación

Definida en `NavRoutes` dentro de `MainActivity.kt`.

| Ruta | Pantalla |
|---|---|
| `demo_hub` | Hub |
| `pos_welcome` → `product_list` | POS |
| `order_summary` | Ticket después de pagar el carrito |
| `admin_list`, `admin_form/{productId}/{barcode}` | Modo empleado |
| `survey` → `survey_thank_you` | Encuesta y cupón |
| `hotel_welcome` → `hotel_detail` → `hotel_dates` → `hotel_pay` → `hotel_ticket` | Reserva |

El modo empleado pide cualquier PIN de 4 dígitos. Un escaneo en ese modo abre el formulario del producto (o uno nuevo si el código no existe).

## Cobro

POS y hotel preguntan efectivo o tarjeta.

- **Efectivo.** Se intenta abrir la caja y el pago se da por hecho. Si la caja no está, el flujo sigue. Ver [hardware.md](hardware.md).
- **Tarjeta.** Aparece la zona NFC. El lector solo cuenta cuando esa zona está activa (`cartAwaitingCard` o `hotelAwaitingCard`). Cancelar regresa a la pregunta o sale del cobro.

En el hotel, un pago exitoso abre el ticket e imprime el comprobante de reserva. Después de imprimir, "Hacer check-out" imprime el comprobante de salida.

En el POS, el pago abre el resumen e imprime el ticket. Si la impresora falla, la pantalla deja continuar sin imprimir y reintentar.

La encuesta imprime un cupón con el texto y el QR `SYSCOM-SUNMI`.

## Tema

`MainActivity` guarda `isSunmiTheme`. `KioscoTheme` publica `BrandThemes.Syscom` o `BrandThemes.Sunmi` en `LocalBrandTheme`. El selector está en `DemoHubScreen`. SYSCOM usa base `#0C336A` y acento `#2F6FB2`. SUNMI usa base `#121212` y acento `#FF9E00`. El tema no se guarda al cerrar la app.

## Datos

Room, archivo `kiosco.db`. El catálogo se siembra en el primer arranque (`ProductSeeder`). El modelo de producto está en `Product.kt` y coincide con `ProductEntity`. Los campos de búsqueda por hardware son `barcode` y `nfcId`.

## Convenciones de UI

- Los composables son `private`, salvo las pantallas de entrada y `SnackCard`.
- `enableEdgeToEdge()` está activo. Lo que flota abajo lleva `navigationBarsPadding()`.
- Textos de la demo en español.
- Min SDK 24: fechas con `java.util.Calendar`, locale `es-MX`.
- No mezclar `Modifier.padding(horizontal = …)` con `top` en la misma llamada.
- Dentro de una tarjeta con altura fija, un fondo decorativo usa `matchParentSize()`, no `fillMaxSize()`.

## Build

```bash
cd mobile-kiosk
./gradlew :app:assembleDebug
./gradlew :app:lint
```

Hace falta `JAVA_HOME`. En esta máquina suele ser el JBR de Android Studio (`C:\Program Files\Android\Android Studio\jbr`).
