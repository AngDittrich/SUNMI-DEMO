### Task 3: Wire `SnackCard` start bounds + `CartSummaryBar` bag target/bounce

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/SnackKioskScreen.kt`

**Interfaces:**
- Consumes: none from Task 1 except types used by callers later (`Offset` bounds)
- Produces:
  - `SnackKioskScreen(..., onAddToCart: (Product, Offset, Float) -> Unit)` â€” center + size in **window/root pixels**
  - `SnackCard(..., onAdd: (Offset, Float) -> Unit)` â€” image center + diameter/size
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

Expected: FAIL until `MainActivity` is updated (next task) **or** temporarily fix call sites in this step with stubs that ignore extra params â€” prefer completing Task 4 immediately after if compile fails on `MainActivity` only.

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

