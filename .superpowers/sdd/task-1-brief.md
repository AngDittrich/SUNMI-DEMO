### Task 1: Fly math helpers + unit tests

**Files:**
- Create: `mobile-kiosk/app/src/main/java/com/example/kiosco/AddToCartFly.kt`
- Create: `mobile-kiosk/app/src/test/java/com/example/kiosco/AddToCartFlyMathTest.kt`

**Interfaces:**
- Consumes: none
- Produces:
  - `data class AddToCartFlyEvent(val id: Long, val imageUrl: String, val startCenter: Offset, val startSize: Float, val endCenter: Offset, val endSize: Float)`
  - `fun quadraticBezier(t: Float, p0: Offset, p1: Offset, p2: Offset): Offset`
  - `fun defaultFlyControlPoint(start: Offset, end: Offset): Offset` â€” control point above the midpoint for a gentle arc

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

- [ ] **Step 2: Run tests â€” expect FAIL**

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

- [ ] **Step 4: Re-run unit tests â€” expect PASS**

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

