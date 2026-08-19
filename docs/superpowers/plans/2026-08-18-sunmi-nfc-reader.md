# SUNMI NFC Reader Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add NFC tag reading capability on the SUNMI FLEX 3 device to identify products by tapping NFC tags/cards, integrating with the existing offline POS kiosk flow.

**Architecture:** Create a focused `NfcScanManager` that reads NFC tags with the **standard Android NFC stack** (`NfcAdapter`, foreground dispatch, `onNewIntent`, `EXTRA_TAG`) — this works on FLEX 3, which exposes a standard Android NFC controller. `MainActivity` owns the NFC lifecycle and routes NFC reads through the existing barcode pipeline (product lookup by `barcode || nfcId`). The SUNMI-proprietary SDK (`NfcManager`/`NfcControlManager`, module switching + watermark) is not on Maven Central and is wired into a clearly marked `NfcScanManager.init()` hook once the local AAR is obtained.

**Tech Stack:** Kotlin, Jetpack Compose, Android NFC API, Room, Material 3. (SUNMI NFC SDK via AIDL — pending local AAR.)

## Reference Documentation

- **SUNMI NFC SDK Guide:** `docs/SUNMI/docs/08-CARD-READER.md` §1 "NFC Related SDK Guide"
  - Source URL: https://docs.sunmi.com/en-US/cdixeghjk491/xmaqeghjk513
  - Update Time: 2026-03-20 18:58:42
- **SUNMI SDK Import Pattern:** Same as `StatusLightService` (shared AIDL-based SDK)
- **Existing Scanner Reference:** `mobile-kiosk/app/src/main/java/com/example/kiosco/BarcodeScanManager.kt` — broadcast-based pattern to mirror

## Global Constraints

- FLEX 3 supports under-screen NFC and/or external NFC modules; the SUNMI SDK provides module switching via serial number.
- **Artifact check (2026-08-18):** `com.sunmi:nfc` does NOT exist on Maven Central (verified via search.maven.org; only `printerx`, `printerlibrary`, `L3AndRemoteOuterSDK`, etc. exist). The SUNMI `NfcControlManager` SDK is delivered as a local AAR via the SUNMI partner platform. Therefore the plan implements NFC reading with the **standard Android NFC stack** (works on FLEX 3), and leaves a clearly marked hook in `NfcScanManager.init()` for the proprietary module control once the AAR is available.
- NFC reading must work offline; no network permissions or HTTP calls.
- Tags are read via standard Android `NfcAdapter` → `onNewIntent` → `EXTRA_TAG` flow (foreground dispatch while the kiosk is open).
- `NfcManager.init()` / `NfcControlManager.*` (module switching + watermark) are documented in `docs/SUNMI/docs/08-CARD-READER.md` §1 and belong in the `NfcScanManager.init()` hook when the AAR is added.
- The app remains offline; do not add internet permission or HTTP client.
- Do not commit changes unless explicitly requested.
- **Do not compile or run Gradle** — the user builds from Android Studio.

---

## Key Classes and Interfaces (from SUNMI SDK — pending local AAR)

> These classes come from SUNMI's proprietary NFC AAR (not on Maven Central). They are reference only until the AAR is added and wired into the `NfcScanManager.init()` hook.

| Class/Interface | Purpose |
|-----------------|---------|
| `NfcManager` | Entry point for NFC SDK initialization |
| `NfcControlManager` | Main control manager for NFC module operations |
| `INfcListener.Stub` | AIDL interface for receiving NFC module change notifications |
| `Nfc` | Data model representing an NFC module (contains `.sn` property) |

## SUNMI NFC SDK API Summary (reference only)

| Method | Description | Parameters | Return |
|--------|-------------|------------|--------|
| `NfcManager.init(context) { success -> }` | Initialize NFC SDK in `onCreate` | `this` (Activity context) | Callback with `success` boolean |
| `NfcControlManager.registerNfcListener(INfcListener.Stub)` | Register listener for module changes | Anonymous listener with `onNfcListChanged` | Void (callback) |
| `NfcControlManager.switchNfc(sn)` | Switch to specific NFC module | `sn` — String, serial number of target module | Void |
| `NfcControlManager.setNfcWaterMarkAlpha(alpha)` | Set watermark transparency | `alpha` — Int, 0–100 | Void |
| `NfcControlManager.unregisterNfcListener(listener)` | Unregister module change listener | Previously registered listener | Void |
| `NfcControlManager.destroy(context)` | Destroy SDK and end operations | `this` (Activity context) | Void |

---

### Task 1: Add NFC permissions and manifest configuration

**Files:**
- Modify: `mobile-kiosk/app/src/main/AndroidManifest.xml`

**Note:** No new Maven dependency is added. The SUNMI NFC SDK artifact is not published on Maven Central; NFC tag reading uses the platform Android NFC APIs (see Task 2). If/when the SUNMI AAR is obtained, add it as a local file dependency and wire it into the `NfcScanManager.init()` hook.

- [ ] **Step 1: Add NFC permission and feature**

```xml
<uses-permission android:name="android.permission.NFC" />
<uses-feature android:name="android.hardware.nfc" android:required="false" />
```

`required="false"` so the app still installs on non-NFC devices for development.

- [ ] **Step 2: Configure MainActivity for NFC intents**

`launchMode="singleTop"` plus `NDEF_DISCOVERED` and `TAG_DISCOVERED` intent filters, so a tag tap delivers an intent via `onNewIntent()` while the kiosk runs or launches it cold.

- [ ] **Step 3: Static manifest check**

Verify only NFC permission/feature + intent filters were added. No network permission is introduced. Do not build.

---

### Task 2: Create NfcScanManager

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/NfcScanManager.kt`

**Interface:**
```kotlin
class NfcScanManager(
    private val context: Context,
    private val onTagRead: (String) -> Unit
) {
    fun init()
    fun onNewIntent(intent: Intent): String?
    fun enableForegroundDispatch(activity: Activity)
    fun disableForegroundDispatch(activity: Activity)
    fun destroy()
}
```

- [ ] **Step 1: Initialize NFC hardware detection**

Use `NfcAdapter.getDefaultAdapter()` in `init()` to detect hardware, log availability, and detect when NFC is disabled in system settings. This is also the hook where the SUNMI `NfcManager.init()` / `NfcControlManager` module-switching calls belong once the proprietary AAR is added to the build.

```kotlin
fun init() {
    nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    val adapter = nfcAdapter
    when {
        adapter == null -> Log.w(TAG, "No se detectó hardware NFC")
        !adapter.isEnabled -> Log.w(TAG, "NFC está desactivado")
        else -> Log.i(TAG, "NFC disponible y activo")
    }
    // Hook: SUNMI NfcManager.init + NfcControlManager.switchNfc/watermark
    // go here once the proprietary AAR is added to the build.
}
```

- [ ] **Step 2: Extract tag data from Intent**

Implement `onNewIntent(intent)` using standard Android NFC APIs. Prefer the NDEF payload (first non-blank UTF-8 record, decoded from a well-known text record or raw payload); fall back to the tag UID as uppercase hex. The manager dedupes repeated intents (same instance) to avoid double-handling cold-start vs. `onNewIntent` paths.

```kotlin
fun onNewIntent(intent: Intent): String? {
    val action = intent.action
    val isTagIntent = action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
        action == NfcAdapter.ACTION_TECH_DISCOVERED ||
        action == NfcAdapter.ACTION_TAG_DISCOVERED
    if (!isTagIntent) return null
    if (intent === lastHandledIntent) return null
    lastHandledIntent = intent

    val tag = extractTag(intent) ?: return null
    val ndefPayload = readNdefPayload(tag)
    val tagId = ndefPayload ?: tag.id.joinToString("") { "%02X".format(it) }
    if (tagId.isNotEmpty()) onTagRead(tagId)
    return tagId
}
```

`extractTag` uses the typed `getParcelableExtra(EXTRA_TAG, Tag::class.java)` on API 33+ and the legacy overload below.

- [ ] **Step 3: Implement foreground dispatch**

Foreground dispatch lets the Activity intercept NFC tags while it is open — critical for a kiosk app. Use a unique `PendingIntent` (with `FLAG_MUTABLE` on API 31+), filter for `NDEF_DISCOVERED` + `TAG_DISCOVERED`, and a null tech list.

```kotlin
fun enableForegroundDispatch(activity: Activity) {
    val adapter = nfcAdapter ?: NfcAdapter.getDefaultAdapter(activity) ?: return
    if (!adapter.isEnabled) return
    val filters = arrayOf(
        IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
        IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
    )
    try {
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, null)
    } catch (e: Exception) {
        Log.w(TAG, "No se pudo habilitar foreground dispatch: ${e.message}")
    }
}
```

- [ ] **Step 4: Cleanup**

```kotlin
fun destroy() {
    nfcAdapter = null
    lastHandledIntent = null
}
```

- [ ] **Step 5: Static manager review**

Verify the manager uses only Android NFC APIs (`android.nfc.*`), has no HTTP/Room/backend calls, and clearly documents the SUNMI SDK hook. No internet permission introduced.

---

### Task 3: Integrate NFC into MainActivity lifecycle

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Interfaces:**
- Add a class property `nfcScanManager: NfcScanManager?` reachable from lifecycle callbacks.
- Route NFC tag reads through the existing `onBarcodeScanned` pipeline (no new state).

- [ ] **Step 1: Initialize NfcScanManager in the composition**

Create the manager once per Activity composition (in the existing `DisposableEffect` block), store it in a class property so `onNewIntent`/`onResume`/`onPause` can reach it, call `init()`, and destroy it on disposal.

```kotlin
// Inside setContent { ... }, after the existing BarcodeScanManager DisposableEffect
DisposableEffect(Unit) {
    val nfc = NfcScanManager(this@MainActivity) { tagId ->
        onBarcodeScanned(tagId)
    }
    nfcScanManager = nfc
    nfc.init()
    onDispose {
        nfc.destroy()
        nfcScanManager = null
    }
}
```

- [ ] **Step 2: Override onNewIntent, onResume and onPause in MainActivity**

Add `onNewIntent` to forward NFC intents to the manager; enable foreground dispatch in `onResume` and disable it in `onPause`. `onResume` also feeds the launching intent to the manager so a cold-start tag tap is handled (the manager dedupes intent instances).

```kotlin
// In MainActivity class body
private var nfcScanManager: NfcScanManager? = null

override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    nfcScanManager?.onNewIntent(intent)
}

override fun onResume() {
    super.onResume()
    val manager = nfcScanManager
    manager?.enableForegroundDispatch(this)
    manager?.onNewIntent(intent)
}

override fun onPause() {
    super.onPause()
    nfcScanManager?.disableForegroundDispatch(this)
}
```

- [ ] **Step 3: Route NFC reads through the existing scanner pipeline**

The NFC callback reuses `onBarcodeScanned` directly. The product lookup in that handler matches `barcode == code || nfcId == code`, so a tag identifier behaves exactly like a barcode: employee mode navigates to the admin form, customer mode adds to the cart with the fly animation, and unknown tags show the existing "Producto no encontrado" overlay.

```kotlin
// In the existing onBarcodeScanned handler, both employee and customer paths:
val match = products.find { it.barcode == code || it.nfcId == code }
```

- [ ] **Step 4: Static lifecycle review**

Verify foreground dispatch is enabled/disabled correctly, `onNewIntent` forwards to the manager, the manager is destroyed on composition disposal, and no navigation or cart logic is broken.

---

### Task 4: Add NFC ID field to Product model (optional)

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/Product.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/data/ProductEntity.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/data/ProductSeeder.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/data/ProductRepository.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/data/AppDatabase.kt` (schema v2 + migration)
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/AdminProductScreen.kt` (admin form field + search)

- [ ] **Step 1: Add nfcId field to Product data class**

```kotlin
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val barcode: String = "",
    val nfcId: String = "",  // NEW: NFC tag UID or NDEF payload
    val imageUrl: String,
    val description: String? = null
)
```

- [ ] **Step 2: Add nfcId to ProductEntity (Room)**

```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val category: String,
    val barcode: String = "",
    val nfcId: String = "",  // NEW
    val imageUrl: String,
    val description: String? = null
)
```

- [ ] **Step 3: Update ProductSeeder with NFC IDs**

Add NFC IDs to seeded products for testing. Use placeholder NDEF payload values like `"NFC-001"`, `"NFC-002"`, etc. These are written to physical tags as NDEF text records (or replaced with real tag UIDs via the admin form).

- [ ] **Step 4: Update ProductRepository mapping and Room schema**

Ensure `toProduct()` / `toEntity()` include the new `nfcId` field, `createProduct()` accepts an `nfcId`, and the Room schema is migrated: `AppDatabase` version 1→2 with `ALTER TABLE products ADD COLUMN nfcId TEXT NOT NULL DEFAULT ''`.

- [ ] **Step 5: Admin form field**

Add an "ID NFC (tag)" text field to `AdminProductFormScreen`, included in create/update, and match `nfcId` in the admin list search filter.

- [ ] **Step 6: Static model review**

Verify the field is optional (default empty string), does not break existing barcode lookup, and the migration runs without clearing the seeded database.

---

### Task 5: NFC tag programming helper (optional, for testing)

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/NfcWriteManager.kt` (optional, for development only)

**Purpose:** Write product NFC IDs to blank NFC tags during development/testing. This is a development utility, not shipped in production.

- [ ] **Step 1: Implement NDEF write to tag**

```kotlin
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable

fun writeNdefMessage(tag: Tag, payload: String): Boolean {
    val record = NdefRecord.createTextRecord("en", payload)
    val message = NdefMessage(arrayOf(record))

    // Try Ndef first
    val ndef = Ndef.get(tag)
    if (ndef != null) {
        try {
            ndef.connect()
            if (!ndef.isWritable) return false
            if (ndef.maxSize < message.toByteArray().size) return false
            ndef.writeNdefMessage(message)
            return true
        } finally {
            ndef.close()
        }
    }

    // Try NdefFormatable for unformatted tags
    val formatable = NdefFormatable.get(tag)
    if (formatable != null) {
        try {
            formatable.connect()
            formatable.format(message)
            return true
        } finally {
            formatable.close()
        }
    }

    return false
}
```

- [ ] **Step 2: Static write manager review**

Verify this is clearly marked as a development utility and not called from production code paths.

---

### Task 6: UI feedback for NFC reads

**Files:**
- No new UI code required.

- [ ] **Step 1: Reuse the existing scan pipeline**

NFC reads route through `onBarcodeScanned`, so the existing `ScanSuccessOverlay` ("Agregado!") and `ProductNotFoundOverlay` ("El producto que escaneaste no existe") render automatically — no new overlay or state was added. This keeps the NFC UX identical to barcode scans.

- [ ] **Step 2: Static UI review**

Verify no new overlay/state was introduced and the UI remains consistent with the barcode flow.

---

### Task 7: Static verification only

**Files:**
- Verify all files from Tasks 1–6.

- [ ] **Step 1: Check source diagnostics**

Run IDE diagnostics on all modified/new Kotlin and Gradle files. Do not run Gradle or compilation.

- [ ] **Step 2: Check NFC behavior statically**

Verify `NfcScanManager.init()` is called from the composition, foreground dispatch is enabled/disabled in `onResume`/`onPause`, `onNewIntent` extracts the tag identifier (NDEF payload or UID hex), the manager dedupes intents, and product lookup matches `barcode || nfcId` through the existing scanner pipeline.

- [ ] **Step 3: Check offline isolation**

Search new NFC code for HTTP URLs, sockets, Room writes (outside of existing repository), and backend calls. Confirm no internet permission is added.

- [ ] **Step 4: Check SUNMI SDK hook**

The proprietary SUNMI SDK (`NfcManager`, `NfcControlManager`, `INfcListener`, `Nfc`) is documented in the plan's reference tables but not referenced from production code yet — it must be wired into the `NfcScanManager.init()` hook only after the AAR is added to the build:
- `NfcManager.init(context)` with Activity context
- `NfcControlManager.registerNfcListener(INfcListener.Stub)`
- `NfcControlManager.switchNfc(sn)` with module serial number
- `NfcControlManager.setNfcWaterMarkAlpha(alpha)` with 0–100 range
- `NfcControlManager.unregisterNfcListener(listener)` on cleanup
- `NfcControlManager.destroy(context)` on destroy

- [ ] **Step 5: Document device validation**

Record that Android Studio must verify on SUNMI FLEX 3 hardware:
- Tag tap triggers `onNewIntent` with valid `EXTRA_TAG`
- Tag UID or NDEF payload is extracted correctly
- Product lookup by NFC ID matches existing product
- Cart addition with fly animation works
- "Producto no encontrado" shows for unknown tags
- Foreground dispatch works while the kiosk is open
- Foreground dispatch does not leak when activity pauses
- Scanner (barcode) continues to work alongside NFC
- Cold start via tag tap adds the product
- Existing database upgrades from schema v1 without data loss
- Offline mode remains enforced
- (When the SUNMI AAR is added) module switching + watermark work on FLEX 3
