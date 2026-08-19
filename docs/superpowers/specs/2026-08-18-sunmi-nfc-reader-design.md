# SUNMI NFC Reader Design

## Goal

Add NFC tag reading capability on the SUNMI FLEX 3 device so products can be identified by tapping NFC tags/cards, integrating with the existing offline POS kiosk flow.

## Approved behavior

- NFC reading works entirely offline with no network dependency.
- The SUNMI NFC SDK handles module selection (under-screen vs external NFC on FLEX 3).
- Tapping an NFC tag looks up the product by its NFC ID (tag UID or NDEF payload).
- If the NFC tag matches a known product, it is added to the cart with the same "Agregado!" feedback as barcode scans.
- If the NFC tag is unknown, the same "Producto no encontrado" overlay is shown.
- The barcode scanner continues to work alongside NFC without conflict.
- Foreground dispatch ensures NFC tags are captured even when the app is not in the exact screen.
- The watermark transparency for the NFC indicator is configurable.
- The app remains offline; no internet permission or HTTP client is added.

## Architecture

Add an `NfcScanManager` that reads NFC tags with the **standard Android NFC stack** (`NfcAdapter`, foreground dispatch, `onNewIntent`, `EXTRA_TAG`). This works on the FLEX 3 because it exposes a standard Android NFC controller. The SUNMI-proprietary `NfcControlManager` SDK (module switching between the under-screen and external antennas + watermark) is **not published on Maven Central** (`com.sunmi:nfc` does not exist — verified 2026-08-18) and is delivered as a local AAR via the SUNMI partner platform; `NfcScanManager.init()` documents the exact hook where those calls belong once the AAR is added. No fake dependency is added.

`MainActivity` owns the NFC lifecycle: the manager is created in the existing `DisposableEffect`, foreground dispatch is enabled/disabled in `onResume`/`onPause`, and tag intents are intercepted in `onNewIntent`. Tag reads are routed through the existing `onBarcodeScanned` pipeline, so products are matched by `barcode == code || nfcId == code`.

The NFC tag identifier is the NDEF payload (first non-blank UTF-8 record, e.g. "NFC-001") or, when no NDEF message exists, the tag UID as uppercase hex. Products carry an optional `nfcId` column (Room schema v2 with a 1→2 migration) configurable from the admin form.

## State and flow

NFC reads feed into the exact same state paths as barcode scans:

- `onNewIntent(tag)` → `NfcScanManager` extracts identifier → callback → `onBarcodeScanned(identifier)`
- Successful match → `cartItems` update + `ScanSuccessOverlay` + fly animation
- No match → `productNotFoundVisible = true` + `ProductNotFoundOverlay`
- Employee mode → navigates to the admin product form (same as scanning an unknown/catalogued code)

## SUNMI NFC SDK reference (pending local AAR)

The SUNMI SDK API is documented in `docs/SUNMI/docs/08-CARD-READER.md` §1. These calls are NOT in production code yet; they belong in the `NfcScanManager.init()` hook once the AAR is added:

```kotlin
// Initialize
NfcManager.init(activity) { success -> }

// Listen for modules
NfcControlManager.registerNfcListener(object : INfcListener.Stub() {
    override fun onNfcListChanged(nfcList: MutableList<Nfc>?) { }
})

// Switch module
NfcControlManager.switchNfc(nfc.sn)

// Watermark
NfcControlManager.setNfcWaterMarkAlpha(50)

// Cleanup
NfcControlManager.unregisterNfcListener(listener)
NfcControlManager.destroy(context)
```

## Error handling

- No NFC hardware or NFC disabled → logged in `NfcScanManager.init()`; the app works normally and barcode scanning continues.
- Unknown tags show the existing "Producto no encontrado" overlay.
- Foreground dispatch exceptions are caught and logged without crashing.
- NDEF read/decoding errors fall back to the tag UID.

## Compatibility and offline constraints

- NFC reading is local to the device hardware; no network permission or HTTP client is added.
- The app installs and runs on non-NFC devices (`android.hardware.nfc` `required="false"`).
- The existing seeded database upgrades from schema v1 to v2 via an `ALTER TABLE` migration (no data loss).
- Android Studio/device validation is required because this environment does not run Gradle.

## Verification

Static checks will verify the manager boundaries, lifecycle hooks, foreground dispatch, tag extraction, product lookup, offline isolation, Room migration, and absence of network calls. The user builds from Android Studio and validates on FLEX 3 hardware.
