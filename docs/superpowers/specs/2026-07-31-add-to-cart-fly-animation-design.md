# Add-to-Cart Fly Animation Design

**Date:** 2026-07-31  
**Scope:** Mobile kiosk product grid → bottom `CartSummaryBar`  
**Status:** Approved for implementation planning

## Goal

When the user taps `+` on a product that is **not** already in the cart (quantity goes from **0 → 1**), show a clear “product going into the cart” animation: a product thumbnail flies from the card into the shopping-bag icon on `CartSummaryBar`.

Quantity changes **1 → 2**, **2 → 3**, etc. must **not** trigger this animation.  
If the product is later removed (back to 0) and added again, the animation **must** run again on that 0 → 1 transition.

## Out of scope

- Animation when adding from `ProductDetailScreen`
- Animation for quantity increments above 1
- Changes to cart business logic beyond detecting first-add for animation
- Backend / Socket.IO changes

## Current behavior (context)

- `SnackCard` exposes a green circular `+` that calls `onAdd` → `onAddToCart(product)`.
- `MainActivity.addToCart` either inserts `CartItem(product, 1)` or increments quantity.
- `CartSummaryBar` is shown at the bottom of the root `Box` when `totalItems > 0` (and not on welcome / while cart sheet is open).
- The bar’s bag icon sits on the left side of the pill.

## Recommended approach

**Real thumbnail flight to the bag icon** (precise destination), not a vague path to “bottom center” and not a bag-only bounce without a flying product.

### Alternatives considered

| Approach | Pros | Cons |
|---|---|---|
| **A. Thumbnail → bag (chosen)** | Clear affordance; works across screen sizes | Needs coordinate capture for source + target |
| B. Thumbnail → bottom center | Simpler coordinates | May miss the bag |
| C. Bag bounce / flash only | Cheap | Weak “product went in” story |

## Interaction rules

1. On `+` tap in the product list, resolve current quantity for that product id.
2. If quantity **> 0**, call `addToCart` only — no fly animation.
3. If quantity **== 0**:
   - Capture source position (product image / card image bounds in root overlay coordinates).
   - Capture destination (bag icon bounds on `CartSummaryBar`), or a stable estimated bag point if the bar is about to appear for the first item.
   - Call `addToCart` so the bar becomes visible when needed.
   - Start the fly overlay animation.
4. Removing a product (qty → 0) clears that line; the next 0 → 1 for the same product animates again.

## Architecture

Keep animation orchestration near where cart state and the overlay live: **`MainActivity`** root `Box`.

### New / updated pieces

1. **`AddToCartFlyEvent` (or equivalent)**  
   Holds: `imageUrl`, start `Offset` + size, end `Offset` + size (or end center), optional product id for debugging.

2. **Coordinate hooks**
   - `SnackCard` / grid: `onGloballyPositioned` (or equivalent) on the product image so `+` can report start bounds when first-adding.
   - `CartSummaryBar`: expose a bag `onGloballyPositioned` callback (or `Modifier` on the bag `Box`) so destination stays accurate when the bar is already visible.
   - When the cart is empty and the bar is not yet composed, use a **fallback destination**: bottom-aligned bar region matching current padding (`horizontal 24.dp`, `bottom 16.dp` + nav bars) and the known bag position inside the pill (left icon cluster). Prefer measured bag bounds once the bar appears; start flight with fallback if needed so the first item still animates.

3. **Overlay composable** (e.g. `AddToCartFlyOverlay`)  
   Drawn above screens in the same root `Box` as `CartSummaryBar`. Renders a clipped circular (or rounded) `AsyncImage` that:
   - Moves along a short **curved** path (~quadratic Bezier) from start → bag.
   - Scales down (~1.0 → ~0.35–0.45) over the flight.
   - Fades out near the end.
   - Duration ~**600 ms**, eased (fast start, soft land).

4. **Landing feedback**  
   When the flight completes (or near completion), pulse/bounce the bag icon slightly (scale spring). Trigger via a one-shot flag/`Animatable` on `CartSummaryBar` or a shared “bump” state from `MainActivity`.

5. **API surface changes (minimal)**
   - `SnackKioskScreen` / `SnackCard`: first-add path must pass product + start bounds (or start bounds measured inside card and passed through `onAdd`).
   - `onAddToCart` may become something like `onAddToCart(product, startBounds?)` **or** stay as today with a separate `onFirstAddFly(product, startBounds)` called only when qty was 0 — prefer keeping cart mutation in one place in `MainActivity` that decides whether to fly.
   - `CartSummaryBar`: optional `bagBounceKey` / `Modifier` for bag positioning; do not change checkout CTA behavior.

## Animation details

- **Trigger:** only grid `+` with previous quantity 0.
- **Visual:** product image thumbnail (not the green `+` icon).
- **Path:** curved toward bag center.
- **Duration:** ~600 ms.
- **Concurrency:** one active flight is enough for v1; if the user taps another first-add quickly, either queue or replace the previous overlay (prefer **replace** to avoid clutter).
- **Cancel:** clearing overlay when flight ends; no need to block further taps.

## Error / edge cases

- Missing `imageUrl`: still fly a placeholder circle with brand green / charcoal so feedback remains.
- Product scrolled off-screen mid-flight: overlay uses captured start coords; continue flight.
- Cart sheet open: existing UI already hides the bar; first-add from list while sheet open is unlikely for this path — if bar is hidden, skip destination bump or animate to last known bag position; do not open the sheet.
- Rotation / config change mid-flight: drop overlay (acceptable for kiosk demo).

## Testing (manual)

1. Empty cart → tap `+` on a product → thumbnail flies into bag; bar appears; totals update.
2. Same product `+` again → no fly; quantity/total update only.
3. Remove product in cart (qty 0) → tap `+` again → fly runs again.
4. First add on a product near top of grid and near bottom of grid → destination still hits bag.
5. Large display (wide) and phone-sized width → destination still aligns with bag.

## Success criteria

- Users clearly see the product “go into” the cart on first add.
- No fly animation on subsequent increments of the same line.
- Re-add after removal animates again.
- Existing cart math and navigation unchanged aside from animation wiring.
