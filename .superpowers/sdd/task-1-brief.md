### Task 1: Establish semantic brand palette and Material theme

**Files:**
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Color.kt`
- Modify: `mobile-kiosk/app/src/main/java/com/example/kiosco/ui/theme/Theme.kt`

**Interfaces:**
- Produce `SyscomBlue = Color(0xFF0C336A)` and `SunmiOrange = Color(0xFFFF6900)` for all screens.
- Material light/dark color schemes use these shared variables.
- Preserve existing semantic neutral colors and public APIs.

- [ ] **Step 1: Replace the green constants with brand constants**

Define `SyscomBlue` and `SunmiOrange` in `Color.kt`. Keep `DarkCharcoal`, `DarkCardBg`, `LightBg`, and `TextMuted` as neutral colors. Do not modify screen consumers in this task.

- [ ] **Step 2: Update Material color schemes**

Use `SyscomBlue` for `primary`, `SunmiOrange` for `secondary`, and choose readable `onPrimary`/`onSecondary` values for both light and dark schemes. Keep surfaces and backgrounds neutral or blue-tinted; do not add a third brand color.

- [ ] **Step 3: Validate compilation references**

Run from `mobile-kiosk`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
./gradlew :app:compileDebugKotlin --console=plain
```

Expected: `BUILD SUCCESSFUL`. Existing `NeonGreen` consumers may remain until later tasks migrate them.

---

