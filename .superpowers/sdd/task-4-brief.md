### Task 4: Orchestrate fly in `MainActivity`

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt`

**Interfaces:**
- Consumes: `AddToCartFlyEvent`, `AddToCartFlyOverlay`, updated `SnackKioskScreen` / `CartSummaryBar` APIs
- Produces: working 0â†’1 fly + bag bounce in the running app

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

- [ ] **Step 2: Replace list `onAddToCart` with 0â†’1 detection**

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

Check `Product.imageUrl` type â€” if non-null `String`, use `product.imageUrl` directly (no `orEmpty` needed only if nullable).

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

If bag becomes measured during flight and end was fallback, v1 may keep the original end â€” acceptable per spec. Optionally, when `bagCenter` updates and `flyEvent != null`, leave event unchanged (no mid-flight retarget required).

- [ ] **Step 4: Build debug APK**

```bash
./gradlew :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Manual verification checklist**

1. Empty cart â†’ `+` on a product â†’ thumbnail flies to bag; bar appears; totals update.
2. Same product `+` again â†’ no fly.
3. Remove item in cart â†’ `+` again â†’ fly runs.
4. First-add near top and near bottom of grid â†’ lands on bag.
5. Rapid two different first-adds â†’ second replaces first overlay.

- [ ] **Step 6: Commit**

```bash
git add mobile-kiosk/app/src/main/java/com/example/kiosco/MainActivity.kt
git commit -m "feat: fly product thumbnail into cart on first add"
```

---

## Spec coverage (self-review)

| Spec requirement | Task |
|---|---|
| Thumbnail flies card â†’ bag on 0â†’1 | Task 4 |
| No fly on 1â†’2+ | Task 4 (`existingQty == 0`) |
| Re-fly after remove | Task 4 (qty returns to 0) |
| Curved ~600ms, scale, fade | Task 1â€“2 |
| Bag landing bounce | Task 3â€“4 |
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
