# SUNMI + SYSCOM Brand Refresh Design

## Goal

Refresh the offline Android kiosk visual identity so SUNMI and SYSCOM are represented together across the entire application. Replace the existing neon-green brand accent with a coordinated blue-and-orange system while preserving product, cart, scanner, Room, and navigation behavior.

## Brand system

Define the brand colors centrally in `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Color.kt`:

- `SyscomBlue = Color(0xFF0C336A)`: structural color for headers, navigation, selected states, dark surfaces, and primary Material theme roles.
- `SunmiOrange = Color(0xFFFF6900)`: action color for primary actions, add-to-cart interactions, slider thumb, quantity controls, and active emphasis.
- Existing neutral colors remain available as semantic neutrals for backgrounds, surfaces, primary text, and muted text.
- Red remains reserved for errors, delete, and destructive confirmations.
- `NeonGreen` and `NeonGreenV2` are removed or replaced so no brand green remains in the UI.

Material light and dark color schemes use the global brand variables instead of hardcoded green values. Screen-specific components should consume these shared variables rather than defining duplicate brand colors.

## Welcome screen

Update `WelcomeScreen.kt` without changing its `onGetStarted` contract:

- Replace the text brand lockup and “Auto Servicio” badge with a spacious brand header.
- Render `syscom-large-logo.png` on the left and `sunmi.webp` on the right from the bundled assets.
- Keep both logos large, clear, and centered vertically within the header.
- Remove the product-count badge, including “23 OPCIONES LISTAS”.
- Use a Syscom-blue hero surface with Sunmi-orange decorative accents.
- Use orange for the slide-to-start thumb, progress fill, and action emphasis.
- Preserve responsive sizing for phone and tablet layouts and retain the existing slide-to-start behavior.

## App-wide application

Update screens that currently use `NeonGreen` or `NeonGreenV2`, including:

- Catalog and category filters.
- Product detail, quantity controls, and pager indicator.
- Cart summary, checkout, and payment confirmation.
- Employee PIN dialog.
- Admin product list and form.
- Add-to-cart animation and scan feedback.
- Main activity overlays and Material components.

Use blue for structural surfaces and orange for actions consistently. Existing error/delete red and neutral backgrounds are not brand colors and should remain semantically unchanged.

## Asset handling

Use bundled local assets only:

- `app/src/main/assets/brand/syscom-large-logo.png`
- `app/src/main/assets/brand/sunmi.webp`

The redesign must not add network loading or change the offline architecture. If Compose requires resource-backed images, copy assets into an appropriate drawable resource only if direct asset loading is not practical; otherwise use an offline asset loader.

## Constraints

- No changes to Room entities, repository behavior, scanner integration, cart logic, or navigation.
- No backend/API dependency.
- No hardcoded brand colors in screen files when a shared semantic color is available.
- Keep accessibility contrast acceptable for text and action controls.
- Keep current phone/tablet responsive behavior.

## Verification

1. Search Kotlin sources for remaining `NeonGreen`, `NeonGreenV2`, and duplicated green brand literals.
2. Run Android lint for the app.
3. Build `:app:assembleDebug`.
4. Confirm the generated APK includes both brand assets.
5. Manually verify welcome, catalog, detail, cart, checkout, PIN, admin, and scanner feedback screens on a tablet.
