# Add-to-Cart Fly Animation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** On product-grid `+` when quantity goes 0 → 1, fly a product thumbnail into the `CartSummaryBar` bag icon with a short curve and landing bounce.

**Architecture:** Pure geometry helpers + `AddToCartFlyEvent` live in a small Kotlin file. `MainActivity` owns fly state, decides 0→1 vs increment, and draws `AddToCartFlyOverlay` above the nav host. `SnackCard` reports image bounds on add; `CartSummaryBar` reports bag bounds and plays a bounce when bumped.

**Tech Stack:** Kotlin, Jetpack Compose animation (`Animatable`, `tween`), Coil `AsyncImage`, existing cart state in `MainActivity`.

## Global Constraints

- Animate **only** grid `+` transitions from quantity **0 → 1** (not 1→2+).
- Re-animate when the same product is removed and added again (another 0 → 1).
- Destination is the **bag icon** on `CartSummaryBar`, not bottom-center vaguely.
- Flight ~**600 ms**, curved path, scale down, fade near end; **replace** if a second first-add starts mid-flight.
- Out of scope: `ProductDetailScreen` adds, backend, Socket.IO.
- Repo has no Compose UI test suite — unit-test pure math; verify UI manually / with `assembleDebug`.

---

## File structure

| File | Responsibility |
|---|---|
| Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt` | `AddToCartFlyEvent`, quadratic Bezier helper, `AddToCartFlyOverlay` composable |
| Create: `mobile-kiosk/app/src/test/java/com/example/kiosco/AddToCartFlyMathTest.kt` | Unit tests for Bezier / progress helpers |
| Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/SnackKioskScreen.kt` | `SnackCard` image positioning + `onAdd(bounds)`; `CartSummaryBar` bag positioning + bounce |
| Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt` | Detect 0→1, hold bag center, start/replace fly event, render overlay |

---

### Task 1: Fly math helpers + unit tests

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt`
- Create: `mobile-kiosk/app/src/test/java/com/example/kiosco/AddToCartFlyMathTest.kt`

**Interfaces:**
- Consumes: none
- Produces:
  - `data class AddToCartFlyEvent(val id: Long, val imageUrl: String, val startCenter: Offset, val startSize: Float, val endCenter: Offset, val endSize: Float)`
  - `fun quadraticBezier(t: Float, p0: Offset, p1: Offset, p2: Offset): Offset`
  - `fun defaultFlyControlPoint(start: Offset, end: Offset): Offset` — control point above the midpoint for a gentle arc

- [ ] **Step 1: Write the failing unit tests**

Create `AddToCartFlyMathTest.kt`:

```kotlin
package com.example.kiosco

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

class AddToCartFlyMathTest {
    @Test
    fun quadraticBezier_atEndpoints_matchesP0AndP2() {
        val p0 = Offset(0f, 0f)
        val p1 = Offset(50f, -100f)
        val p2 = Offset(100f, 200f)
        assertEquals(0f, quadraticBezier(0f, p0, p1, p2).x, 0.01f)
        assertEquals(0f, quadraticBezier(0f, p0, p1, p2).y, 0.01f)
        assertEquals(100f, quadraticBezier(1f, p0, p1, p2).x, 0.01f)
        assertEquals(200f, quadraticBezier(1f, p0, p1, p2).y, 0.01f)
    }

    @Test
    fun quadraticBezier_atMidpoint_pullsTowardControl() {
        val p0 = Offset(0f, 0f)
        val p1 = Offset(0f, -100f)
        val p2 = Offset(0f, 0f)
        val mid = quadraticBezier(0.5f, p0, p1, p2)
        assertEquals(0f, mid.x, 0.01f)
        assertEquals(-50f, mid.y, 0.01f)
    }

    @Test
    fun defaultFlyControlPoint_isAboveMidpoint() {
        val start = Offset(100f, 400f)
        val end = Offset(200f, 800f)
        val control = defaultFlyControlPoint(start, end)
        assertEquals(150f, control.x, 0.01f)
        // Control y must be less than midpoint y (higher on screen)
        assertEquals(true, control.y < (start.y + end.y) / 2f)
    }
}
```

- [ ] **Step 2: Run tests — expect FAIL**

Run from `mobile-kiosk`:

```bash
./gradlew :app:testDebugUnitTest --tests com.example.kiosco.AddToCartFlyMathTest
```

Expected: compile/fail because `quadraticBezier` / `defaultFlyControlPoint` are unresolved.

- [ ] **Step 3: Implement math + event type (no overlay yet)**

Create `AddToCartFly.kt` with:

```kotlin
package com.example.kiosco

import androidx.compose.ui.geometry.Offset

data class AddToCartFlyEvent(
    val id: Long,
    val imageUrl: String,
    val startCenter: Offset,
    val startSize: Float,
    val endCenter: Offset,
    val endSize: Float
)

fun quadraticBezier(t: Float, p0: Offset, p1: Offset, p2: Offset): Offset {
    val u = 1f - t
    val x = u * u * p0.x + 2f * u * t * p1.x + t * t * p2.x
    val y = u * u * p0.y + 2f * u * t * p1.y + t * t * p2.y
    return Offset(x, y)
}

fun defaultFlyControlPoint(start: Offset, end: Offset): Offset {
    val midX = (start.x + end.x) / 2f
    val midY = (start.y + end.y) / 2f
    val lift = (end.y - start.y).coerceAtLeast(120f) * 0.35f
    return Offset(midX, midY - lift)
}
```

- [ ] **Step 4: Re-run unit tests — expect PASS**

```bash
./gradlew :app:testDebugUnitTest --tests com.example.kiosco.AddToCartFlyMathTest
```

Expected: `BUILD SUCCESSFUL`, 3 tests passed.

- [ ] **Step 5: Commit**

```bash
git add mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt mobile-kiosk/app/src/test/java/com/example/kiosco/AddToCartFlyMathTest.kt
git commit -m "feat: add fly-to-cart bezier helpers and unit tests"
```

---

### Task 2: `AddToCartFlyOverlay` composable

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt`

**Interfaces:**
- Consumes: `AddToCartFlyEvent`, `quadraticBezier`, `defaultFlyControlPoint`
- Produces:
  - `@Composable fun AddToCartFlyOverlay(event: AddToCartFlyEvent?, onFinished: (Long) -> Unit)`
  - Duration **600 ms**; on finish call `onFinished(event.id)` so caller can clear / bump bag

- [ ] **Step 1: Append overlay implementation**

Add to `AddToCartFly.kt` (imports as needed: Compose animation, foundation layout, Coil, theme colors):

```kotlin
@Composable
fun AddToCartFlyOverlay(
    event: AddToCartFlyEvent?,
    onFinished: (Long) -> Unit
) {
    if (event == null) return

    val progress = remember(event.id) { Animatable(0f) }
    LaunchedEffect(event.id) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        onFinished(event.id)
    }

    val t = progress.value
    val control = remember(event.id) {
        defaultFlyControlPoint(event.startCenter, event.endCenter)
    }
    val center = quadraticBezier(t, event.startCenter, control, event.endCenter)
    val size = event.startSize + (event.endSize - event.startSize) * t
    val alpha = if (t < 0.75f) 1f else (1f - (t - 0.75f) / 0.25f).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (center.x - size / 2f).roundToInt(),
                        (center.y - size / 2f).roundToInt()
                    )
                }
                .size(with(LocalDensity.current) { size.toDp() })
                .clip(CircleShape)
                .graphicsLayer { this.alpha = alpha }
                .background(DarkCharcoal.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            if (event.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    Modifier
                        .fillMaxSize(0.55f)
                        .clip(CircleShape)
                        .background(NeonGreen)
                )
            }
        }
    }
}
```

Keep the overlay non-interactive (no clickable). Use `graphicsLayer`/`alpha` so it draws above content when parent places it last in a `Box`.

- [ ] **Step 2: Compile check**

```bash
./gradlew :app:compileDebugKotlin
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt
git commit -m "feat: add AddToCartFlyOverlay composable"
```

---

### Task 3: Wire `SnackCard` start bounds + `CartSummaryBar` bag target/bounce

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/SnackKioskScreen.kt`

**Interfaces:**
- Consumes: none from Task 1 except types used by callers later (`Offset` bounds)
- Produces:
  - `SnackKioskScreen(..., onAddToCart: (Product, Offset, Float) -> Unit)` — center + size in **window/root pixels**
  - `SnackCard(..., onAdd: (Offset, Float) -> Unit)` — image center + diameter/size
  - `CartSummaryBar(..., bagBounceTrigger: Int = 0, onBagPositioned: (Offset) -> Unit = {})`

- [ ] **Step 1: Update `SnackCard` to measure image and pass bounds on add**

On the product image container `Box` (the one with `AsyncImage`), add:

```kotlin
var imageCenter by remember { mutableStateOf(Offset.Zero) }
var imageSizePx by remember { mutableStateOf(0f) }

// on the image Box modifier:
.onGloballyPositioned { coords ->
    val pos = coords.positionInRoot()
    val size = coords.size
    imageCenter = Offset(pos.x + size.width / 2f, pos.y + size.height / 2f)
    imageSizePx = minOf(size.width, size.height).toFloat() * 0.55f
}
```

Change the `+` clickable to:

```kotlin
.clickable(onClick = { onAdd(imageCenter, imageSizePx.coerceAtLeast(48f)) })
```

Update `SnackCard` signature:

```kotlin
fun SnackCard(
    product: Product,
    onClick: () -> Unit,
    onAdd: (startCenter: Offset, startSize: Float) -> Unit,
    largeDisplay: Boolean = false
)
```

Update `SnackKioskScreen` signature and call site:

```kotlin
fun SnackKioskScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product, Offset, Float) -> Unit,
    onCartClick: () -> Unit
)

// grid:
onAdd = { center, size -> onAddToCart(product, center, size) }
```

Add imports: `positionInRoot`, `onGloballyPositioned`, `Offset`.

- [ ] **Step 2: Update `CartSummaryBar` for bag position + bounce**

Change signature to:

```kotlin
@Composable
fun CartSummaryBar(
    totalItems: Int,
    totalPrice: Double,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
    bagBounceTrigger: Int = 0,
    onBagPositioned: (Offset) -> Unit = {}
)
```

On the bag icon `Box` (circle behind `ShoppingBag`):

```kotlin
val bagScale = remember { Animatable(1f) }
LaunchedEffect(bagBounceTrigger) {
    if (bagBounceTrigger == 0) return@LaunchedEffect
    bagScale.snapTo(1f)
    bagScale.animateTo(1.18f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
    bagScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
}

Box(
    modifier = Modifier
        .size(if (largeBar) 46.dp else 36.dp)
        .graphicsLayer {
            scaleX = bagScale.value
            scaleY = bagScale.value
        }
        .clip(CircleShape)
        .background(Color.White.copy(alpha = 0.1f))
        .onGloballyPositioned { coords ->
            val pos = coords.positionInRoot()
            val size = coords.size
            onBagPositioned(
                Offset(pos.x + size.width / 2f, pos.y + size.height / 2f)
            )
        },
    contentAlignment = Alignment.Center
) { /* existing Icon */ }
```

- [ ] **Step 3: Compile**

```bash
./gradlew :app:compileDebugKotlin
```

Expected: FAIL until `MainActivity` is updated (next task) **or** temporarily fix call sites in this step with stubs that ignore extra params — prefer completing Task 4 immediately after if compile fails on `MainActivity` only.

If you stop here, update `MainActivity` call sites enough to compile:

```kotlin
onAddToCart = { product, _, _ -> addToCart(product) }
// CartSummaryBar: leave new params defaulted
```

- [ ] **Step 4: Commit**

```bash
git add mobile-kiosk/app/src/main/java/com/example/kiosco/SnackKioskScreen.kt mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt
git commit -m "feat: expose card image and bag positions for fly animation"
```

---

### Task 4: Orchestrate fly in `MainActivity`

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Interfaces:**
- Consumes: `AddToCartFlyEvent`, `AddToCartFlyOverlay`, updated `SnackKioskScreen` / `CartSummaryBar` APIs
- Produces: working 0→1 fly + bag bounce in the running app

- [ ] **Step 1: Add fly state next to cart state**

Inside `setContent` / `KioscoTheme` block:

```kotlin
var flyEvent by remember { mutableStateOf<AddToCartFlyEvent?>(null) }
var bagCenter by remember { mutableStateOf<Offset?>(null) }
var bagBounceTrigger by remember { mutableStateOf(0) }
var flyIdSeq by remember { mutableStateOf(0L) }
val density = LocalDensity.current
val configuration = LocalConfiguration.current
```

Add a helper for fallback bag center when the bar is not yet measured (empty cart):

```kotlin
fun fallbackBagCenter(): Offset {
    val widthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val heightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val horizontalPad = with(density) { 24.dp.toPx() }
    val bottomPad = with(density) { 16.dp.toPx() }
    val barHeight = with(density) { 72.dp.toPx() }
    // Bag sits near the left inside the pill (~ start padding + half icon)
    val bagX = horizontalPad + with(density) { 38.dp.toPx() }
    val bagY = heightPx - bottomPad - barHeight / 2f
    return Offset(bagX.coerceAtMost(widthPx / 2f), bagY)
}
```

- [ ] **Step 2: Replace list `onAddToCart` with 0→1 detection**

```kotlin
onAddToCart = { product, startCenter, startSize ->
    val existingQty = cartItems.value.find { it.product.id == product.id }?.quantity ?: 0
    addToCart(product)
    if (existingQty == 0) {
        flyIdSeq += 1
        val end = bagCenter ?: fallbackBagCenter()
        flyEvent = AddToCartFlyEvent(
            id = flyIdSeq,
            imageUrl = product.imageUrl.orEmpty(),
            startCenter = startCenter,
            startSize = startSize,
            endCenter = end,
            endSize = with(density) { 28.dp.toPx() }
        )
    }
}
```

Check `Product.imageUrl` type — if non-null `String`, use `product.imageUrl` directly (no `orEmpty` needed only if nullable).

- [ ] **Step 3: Pass bag callbacks on `CartSummaryBar` and draw overlay last**

```kotlin
if (totalItems > 0 && !isWelcomeRoute && !cartSheetVisible) {
    CartSummaryBar(
        totalItems = totalItems,
        totalPrice = totalPrice,
        onCartClick = { cartSheetVisible = true },
        bagBounceTrigger = bagBounceTrigger,
        onBagPositioned = { bagCenter = it },
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp)
            .navigationBarsPadding()
    )
}

AddToCartFlyOverlay(
    event = flyEvent,
    onFinished = { finishedId ->
        if (flyEvent?.id == finishedId) {
            flyEvent = null
            bagBounceTrigger += 1
        }
    }
)
```

Place `AddToCartFlyOverlay` as the **last** child of the root `Box` so it paints above the bar.

If bag becomes measured during flight and end was fallback, v1 may keep the original end — acceptable per spec. Optionally, when `bagCenter` updates and `flyEvent != null`, leave event unchanged (no mid-flight retarget required).

- [ ] **Step 4: Build debug APK**

```bash
./gradlew :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Manual verification checklist**

1. Empty cart → `+` on a product → thumbnail flies to bag; bar appears; totals update.
2. Same product `+` again → no fly.
3. Remove item in cart → `+` again → fly runs.
4. First-add near top and near bottom of grid → lands on bag.
5. Rapid two different first-adds → second replaces first overlay.

- [ ] **Step 6: Commit**

```bash
git add mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt
git commit -m "feat: fly product thumbnail into cart on first add"
```

---

## Spec coverage (self-review)

| Spec requirement | Task |
|---|---|
| Thumbnail flies card → bag on 0→1 | Task 4 |
| No fly on 1→2+ | Task 4 (`existingQty == 0`) |
| Re-fly after remove | Task 4 (qty returns to 0) |
| Curved ~600ms, scale, fade | Task 1–2 |
| Bag landing bounce | Task 3–4 |
| Fallback destination when bar missing | Task 4 |
| Replace concurrent flights | Task 4 (`flyEvent =` replace) |
| Missing imageUrl placeholder | Task 2 |
| Out of scope detail screen | Not touched |
| Manual test cases | Task 4 Step 5 |

## Type consistency

- `AddToCartFlyEvent.id: Long` matches `onFinished: (Long) -> Unit`.
- `onAddToCart: (Product, Offset, Float) -> Unit` matches `SnackCard` `onAdd: (Offset, Float) -> Unit`.
- `onBagPositioned: (Offset) -> Unit` stores into `bagCenter: Offset?`.
- `bagBounceTrigger: Int` increments on fly finish.
