### Task 5: Lint, build, and APK asset verification

**Files:**
- Verify all implementation files from Tasks 1–4.
- Verify APK: `mobile-kiosk/app/build/outputs/apk/debug/app-debug.apk`

- [ ] **Step 1: Run Android lint**

From `mobile-kiosk`, set `JAVA_HOME` to `C:\Program Files\Android\Android Studio\jbr` and run `./gradlew :app:lint --console=plain`. Record the result and distinguish existing Gradle warnings from errors.

- [ ] **Step 2: Build the debug APK**

Run `./gradlew :app:assembleDebug --console=plain` with the same `JAVA_HOME`. Expected output contains `BUILD SUCCESSFUL`.

- [ ] **Step 3: Verify bundled brand assets**

Inspect the generated APK and confirm it contains `assets/brand/syscom-large-logo.png` and `assets/brand/sunmi.webp`.

- [ ] **Step 4: Verify source cleanup**

Run `rg "NeonGreen|NeonGreenV2|C6F533|D2FD02" mobile-kiosk/app/src`. Expected: no matches.

- [ ] **Step 5: Report manual validation boundary**

Report that physical-device visual validation remains a human step unless an emulator/device is available; do not claim it was performed without evidence.
