### Task 2: Redesign the welcome brand header and hero

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/WelcomeScreen.kt`
- Consume local asset: `mobile-kiosk/app/src/main/assets/brand/syscom-large-logo.png`
- Consume local asset: `mobile-kiosk/app/src/main/assets/brand/sunmi.webp`

**Interfaces:**
- Keep `WelcomeScreen(products: List<Product>, onGetStarted: () -> Unit)` unchanged.
- Keep `SlideToStartButton` behavior unchanged.
- Use `SyscomBlue` and `SunmiOrange` from `com.example.kiosco.ui.theme`.

- [ ] **Step 1: Replace the current text brand header**

Change `BrandHeader` to render both bundled images through Coil `AsyncImage` using:

```kotlin
model = "file:///android_asset/brand/syscom-large-logo.png"
model = "file:///android_asset/brand/sunmi.webp"
```

Place the Syscom logo on the left and SUNMI on the right in a spacious responsive row. Remove the “Auto Servicio” surface and green separator dot. Use `ContentScale.Fit`, clear content descriptions, and bounded responsive sizes.

- [ ] **Step 2: Remove the product-count badge**

Remove `productCount` from the `WelcomeHero` call and signature and delete the `$productCount OPCIONES LISTAS` surface. Preserve the welcome title and supporting copy.

- [ ] **Step 3: Apply the two-brand hierarchy**

Use `SyscomBlue` for the hero background and `SunmiOrange` for the highlighted title span, decorative accents, slide thumb, progress fill, and primary emphasis. Keep snack showcase colors illustrative and avoid green brand accents.

- [ ] **Step 4: Verify responsive composition**

Keep the existing `largeDisplay` layout branches. Ensure the logos do not distort or overflow on phone/tablet widths and keep the existing slide-to-start interaction unchanged.

- [ ] **Step 5: Validate**

Run from `mobile-kiosk`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
./gradlew :app:compileDebugKotlin --console=plain
```

Expected: `BUILD SUCCESSFUL`.
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

