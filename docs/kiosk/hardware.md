# Hardware SUNMI en esta demo

La app habla con el dispositivo. Si el periférico no está, el recorrido de la demo continúa. Los manuales del fabricante están en `docs/SUNMI/docs/`. Este archivo dice qué hace el código de verdad.

## Impresora

Dependencia: `com.sunmi:printerx:1.0.20`.

`SunmiPrinterManager` se conecta a la impresora por defecto y encola el trabajo en un solo hilo. Las pantallas reciben `TicketPrintState`: `Idle`, `Printing`, `Printed`, `Failed`. Un `LaunchedEffect` dispara un solo intento automático; reintentar llama al mismo callback.

Tickets:

| Flujo | Método | Contenido |
|---|---|---|
| POS | impresión del resumen | productos, cantidades, total, agradecimiento |
| Encuesta | cupón | texto y QR `SYSCOM-SUNMI` |
| Hotel | `printHotelReceipt` / `printHotelCheckout` | código, huésped, habitación, fechas, noches, total |

Manual: `docs/SUNMI/docs/04-PRINTER-DEVELOPMENT.md` y `SUNMI_Printer_Documentation.md`.

## Caja registradora

Al elegir efectivo, `openCashDrawer()` llama `printer.cashDrawerApi().open(PrintResult)`. El nombre `openCashDrawer()` de la documentación vieja no existe en PrinterX 1.0.20.

El éxito es `resultCode == 0`. Cualquier fallo (sin impresora, sin caja, código distinto de 0) se escribe en log y no cambia el resultado del cobro ni la impresión del ticket. La caja va por RJ12 en la impresora.

Manual: `docs/SUNMI/docs/09-CASH-DRAWER.md`.

## Escáner

`BarcodeScanManager` escucha el broadcast `com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED`, extra `data`. En el dispositivo, la salida del escáner tiene que estar en modo broadcast, no teclado.

En el catálogo, el código busca `barcode` o `nfcId` y suma el producto al carrito. En encuesta y en las pantallas de hotel que no están esperando tarjeta, el escaneo se ignora.

Manual: `docs/SUNMI/docs/07-SCANNING.md`.

## NFC

`NfcScanManager` usa el stack de Android (`NfcAdapter`, foreground dispatch, `onNewIntent`). Identificador del tag:

1. Primer registro NDEF de texto no vacío.
2. Si no hay NDEF, el UID en hex mayúsculas.

Usos actuales:

- Catálogo del POS: el identificador se trata como código de producto (`barcode` o `nfcId`).
- Cobro con tarjeta (carrito u hotel): cualquier lectura confirma el pago, y solo si la pantalla ya está esperando la tarjeta.
- Sin hardware NFC, o con NFC apagado: se registra un warning y la demo sigue.

El SDK propietario `NfcControlManager` (cambiar antena y marca de agua en FLEX 3) no está en Maven. El punto de enganche está comentado en `NfcScanManager.init()`. Manual: `docs/SUNMI/docs/08-CARD-READER.md`.

`android.hardware.nfc` no es obligatorio, así la app instala en equipos sin NFC.
