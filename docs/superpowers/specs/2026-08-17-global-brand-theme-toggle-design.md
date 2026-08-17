# Global SUNMI/SYSCOM Theme Toggle Design

## Goal

Add a session-wide brand theme toggle and make Android Back return from the product catalog to the welcome screen instead of exiting the app.

## Theme model

Create a semantic `BrandTheme` model in the shared UI theme package. It provides:

- `base`: the main structural color.
- `accent`: action and interaction color.
- `background`: page background.
- `surface`: card and component surface.
- `textPrimary`: primary readable text.
- `logoAsset`: bundled logo URI.

Provide two global themes:

- **SUNMI**
  - Base: `Color(0xFF121212)` / dark charcoal.
  - Accent: `Color(0xFFFF9E00)`.
  - Background and surfaces: white and light grays.
  - Logo: `file:///android_asset/brand/sunmi.webp`.
- **SYSCOM**
  - Base: `Color(0xFF0C336A)`.
  - Accent: `Color(0xFF2F6FB2)`, a lighter blue from the same family for actions and selected states.
  - Background and surfaces: white and light grays.
  - Logo: `file:///android_asset/brand/syscom-large-logo.png`.

The active theme is held in `MainActivity` as session state. It is not persisted between launches. Screens receive the active semantic theme or the relevant values through their existing composition flow; product data, cart state, scanner behavior, navigation destinations, and offline storage remain unchanged.

## Header toggle

Update `SnackKioskScreen.kt` and its `KioskHeader`:

- Replace the static “SNACK” title and “¿Qué se te antoja hoy?” subtitle with the active bundled logo.
- Add a compact toggle immediately between the employee lock button and the shopping bag button.
- Show the active brand logo or a compact brand indicator in the toggle with a clear content description.
- Invoke `onThemeToggle` when pressed.
- Use the active theme for the header base, icons, cart count, and structural/action colors.
- Keep the control non-focusable for scanner keyboard protection.

## Global application behavior

The active theme applies to:

- Welcome screen.
- Product catalog and category filters.
- Product detail.
- Cart and checkout.
- Scan feedback overlays.
- Employee PIN dialog.
- Admin list and forms.
- Order summary.

SUNMI uses white/light-gray surfaces with dark-charcoal structural elements and orange actions. SYSCOM uses white/light-gray surfaces with Syscom-blue structural elements and blue-family actions. Error, delete, and destructive states remain red.

## Android Back behavior

When `WelcomeScreen` navigates to `PRODUCT_LIST`, it must preserve the welcome destination in the navigation back stack. Pressing Android Back from the product catalog therefore pops to Welcome instead of finishing `MainActivity`. Existing detail, cart, admin, order summary, and employee-mode behavior must remain intact.

## Verification

1. Confirm both theme definitions use semantic variables and no screen hardcodes brand-specific switch logic.
2. Confirm the header toggle is positioned between lock and bag and changes the active logo.
3. Confirm all listed screens read the active theme.
4. Confirm Android Back from the catalog reaches Welcome.
5. Run IDE diagnostics, Android lint, and `:app:assembleDebug`.
6. Confirm both logos remain bundled as local APK assets.
