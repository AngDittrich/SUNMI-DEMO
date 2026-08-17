# Task 5 Verification Report

Date: 2026-08-17
Repository: `C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO`
JAVA_HOME: `C:\Program Files\Android\Android Studio\jbr`
Production code was not edited. No commit or worktree was created.

## Status

**PARTIAL / NOT CLEAN:** debug APK assembly, APK asset inspection, and source cleanup verification passed. Android lint failed because it found 2 errors. Physical-device visual validation was not performed and remains a human step.

## 1. Android lint

Command (from `mobile-kiosk`):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew :app:lint --console=plain
```

Result: **FAILED**, exit code `1`.

Exact terminal summary:

```text
> Task :app:lintReportDebug
Wrote HTML report to file:///C:/Users/E-EC1-4830/StudioProjects/SUNMI-DEMO/mobile-kiosk/app/build/reports/lint-results-debug.html
Wrote SARIF report to file:///C:/Users/E-EC1-4830/StudioProjects/SUNMI-DEMO/mobile-kiosk/app/build/reports/lint-results-debug.sarif
Lint found 2 errors, 45 warnings and 5 hints. First failure:
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:107: Error: ComponentActivity.dispatchKeyEvent can only be called from within the same library group prefix (referenced groupId=androidx.core with prefix androidx from groupId=Kiosco) [RestrictedApi]
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                 ~~~~~~~~~~~~~~~~

> Task :app:lintDebug FAILED
BUILD FAILED in 1m 57s
28 actionable tasks: 16 executed, 12 up-to-date
Configuration cache entry stored.
```

Non-error build/configuration warnings emitted during lint:

```text
WARNING: The option setting 'android.disallowKotlinSourceSets=false' is experimental.
The current default is 'true'.
Add android.sync.suppressAgpWarnings=UNSUPPORTED_PROJECT_OPTION_USE to the gradle.properties file to suppress this warning.

C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\AndroidManifest.xml:6:5-87 Warning:
    uses-permission#android.permission.INTERNET was tagged at AndroidManifest.xml:6 to remove other declarations but no other declaration present
```

These warnings are distinct from the two fatal lint `RestrictedApi` errors. The experimental Gradle option warning is also emitted by the successful `assembleDebug` run and does not stop assembly. No baseline comparison was run, so this report does not assert when any lint finding was introduced.

### Exact lint findings

```text
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\CartScreen.kt:399: Warning: Implicitly using the default locale is a common source of bugs: Use String.format(Locale, ...) instead [DefaultLocale]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\CartScreen.kt:431: Warning: Implicitly using the default locale is a common source of bugs: Use String.format(Locale, ...) instead [DefaultLocale]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\CartScreen.kt:538: Warning: Implicitly using the default locale is a common source of bugs: Use String.format(Locale, ...) instead [DefaultLocale]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\OrderSummaryScreen.kt:222: Warning: Implicitly using the default locale is a common source of bugs: Use String.format(Locale, ...) instead [DefaultLocale]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\OrderSummaryScreen.kt:229: Warning: Implicitly using the default locale is a common source of bugs: Use String.format(Locale, ...) instead [DefaultLocale]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\OrderSummaryScreen.kt:255: Warning: Implicitly using the default locale is a common source of bugs: Use String.format(Locale, ...) instead [DefaultLocale]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\build.gradle.kts:18: Warning: Not targeting the latest versions of Android; compatibility modes apply. Consider testing and updating this version. Consult the android.os.Build.VERSION_CODES javadoc for details. [OldTargetApi]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\AndroidManifest.xml:20: Warning: Redundant label can be removed [RedundantLabel]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\build.gradle.kts:10: Warning: A newer version of compileSdk than 36 is available: 37 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\build.gradle.kts:62: Warning: A newer version of androidx.navigation:navigation-compose than 2.8.9 is available: 2.9.8 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:3: Warning: A newer version of androidx.core:core-ktx than 1.10.1 is available: 1.19.0 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:5: Warning: A newer version of androidx.test.ext:junit than 1.1.5 is available: 1.3.0 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:6: Warning: A newer version of androidx.test.espresso:espresso-core than 3.5.1 is available: 3.7.0 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:7: Warning: A newer version of androidx.lifecycle:lifecycle-runtime-ktx than 2.6.1 is available: 2.11.0 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:8: Warning: A newer version of androidx.activity:activity-compose than 1.8.0 is available: 1.13.0 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:11: Warning: A newer version of androidx.room:room-compiler than 2.7.2 is available: 2.8.4 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:11: Warning: A newer version of androidx.room:room-ktx than 2.7.2 is available: 2.8.4 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:11: Warning: A newer version of androidx.room:room-runtime than 2.7.2 is available: 2.8.4 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:12: Warning: A newer version of androidx.compose:compose-bom than 2026.02.01 is available: 2026.08.00 [GradleDependency]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\gradle\libs.versions.toml:9: Warning: A newer version of org.jetbrains.kotlin.plugin.compose than 2.2.10 is available: 2.4.10 [NewerVersionAvailable]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:107: Error: ComponentActivity.dispatchKeyEvent can only be called from within the same library group prefix (referenced groupId=androidx.core with prefix androidx from groupId=Kiosco) [RestrictedApi]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:128: Error: ComponentActivity.dispatchKeyEvent can only be called from within the same library group prefix (referenced groupId=androidx.core with prefix androidx from groupId=Kiosco) [RestrictedApi]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:164: Warning: Using Configuration.screenWidthDp instead of LocalWindowInfo.current.containerSize [ConfigurationScreenWidthHeight from androidx.compose.ui]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:165: Warning: Using Configuration.screenHeightDp instead of LocalWindowInfo.current.containerSize [ConfigurationScreenWidthHeight from androidx.compose.ui]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:231: Warning: Using Configuration.screenWidthDp instead of LocalWindowInfo.current.containerSize [ConfigurationScreenWidthHeight from androidx.compose.ui]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:232: Warning: Using Configuration.screenHeightDp instead of LocalWindowInfo.current.containerSize [ConfigurationScreenWidthHeight from androidx.compose.ui]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\ProductDetailScreen.kt:113: Warning: Using Configuration.screenHeightDp instead of LocalWindowInfo.current.containerSize [ConfigurationScreenWidthHeight from androidx.compose.ui]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\ProductDetailScreen.kt:216: Warning: Reading a value annotated with @FrequentlyChangingValue inside composition [FrequentlyChangingValue from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\ProductDetailScreen.kt:240: Warning: Reading a value annotated with @FrequentlyChangingValue inside composition [FrequentlyChangingValue from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\SnackKioskScreen.kt:158: Warning: Reading a value annotated with @FrequentlyChangingValue inside composition [FrequentlyChangingValue from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\SnackKioskScreen.kt:158: Warning: Reading a value annotated with @FrequentlyChangingValue inside composition [FrequentlyChangingValue from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:148: Hint: Prefer mutableIntStateOf instead of mutableStateOf [AutoboxingStateCreation from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:149: Hint: Prefer mutableLongStateOf instead of mutableStateOf [AutoboxingStateCreation from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:154: Hint: Prefer mutableIntStateOf instead of mutableStateOf [AutoboxingStateCreation from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\MainActivity.kt:155: Hint: Prefer mutableIntStateOf instead of mutableStateOf [AutoboxingStateCreation from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\java\com\example\kiosco\SnackKioskScreen.kt:572: Hint: Prefer mutableFloatStateOf instead of mutableStateOf [AutoboxingStateCreation from androidx.compose.runtime]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\values\colors.xml:3: Warning: The resource R.color.purple_200 appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\values\colors.xml:4: Warning: The resource R.color.purple_500 appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\values\colors.xml:5: Warning: The resource R.color.purple_700 appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\values\colors.xml:6: Warning: The resource R.color.teal_200 appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\values\colors.xml:7: Warning: The resource R.color.teal_700 appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\values\colors.xml:8: Warning: The resource R.color.black appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\values\colors.xml:9: Warning: The resource R.color.white appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\drawable\ic_gummy.xml:1: Warning: The resource R.drawable.ic_gummy appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\drawable\ic_launcher_foreground.xml:1: Warning: The resource R.drawable.ic_launcher_foreground appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\drawable\ic_pretzel.xml:1: Warning: The resource R.drawable.ic_pretzel appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\drawable\ic_trail_mix.xml:1: Warning: The resource R.drawable.ic_trail_mix appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\drawable\logo.png: Warning: The resource R.drawable.logo appears to be unused [UnusedResources]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\src\main\res\drawable\logo.png: Warning: Found bitmap drawable res/drawable/logo.png in densityless folder [IconLocation]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\build.gradle.kts:60: Warning: Use version catalog instead [UseTomlInstead]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\build.gradle.kts:61: Warning: Use version catalog instead [UseTomlInstead]
C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\build.gradle.kts:62: Warning: Use version catalog instead [UseTomlInstead]
2 errors, 45 warnings, 5 hints
```

Full generated lint text report: `mobile-kiosk/app/build/intermediates/lint_intermediate_text_report/debug/lintReportDebug/lint-results-debug.txt`

## 2. Debug APK assembly

Command (from `mobile-kiosk`):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew :app:assembleDebug --console=plain
```

Result: **PASSED**, exit code `0`.

Exact terminal summary:

```text
WARNING: The option setting 'android.disallowKotlinSourceSets=false' is experimental.
The current default is 'true'.
Add android.sync.suppressAgpWarnings=UNSUPPORTED_PROJECT_OPTION_USE to the gradle.properties file to suppress this warning.

> Task :app:packageDebug
> Task :app:assembleDebug
> Task :app:createDebugApkListingFileRedirect

BUILD SUCCESSFUL in 2m 4s
37 actionable tasks: 6 executed, 31 up-to-date
Configuration cache entry stored.
```

APK: `mobile-kiosk/app/build/outputs/apk/debug/app-debug.apk`

## 3. APK bundled brand assets

The generated APK was opened as a ZIP archive and queried by exact entry name.

Exact output:

```text
APK: C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\build\outputs\apk\debug\app-debug.apk
FOUND: assets/brand/syscom-large-logo.png | uncompressed=4532 bytes | compressed=4532 bytes
FOUND: assets/brand/sunmi.webp | uncompressed=2216 bytes | compressed=2216 bytes
```

Result: **PASSED**. Both required assets are bundled.

## 4. Source cleanup search

Command:

```powershell
rg "NeonGreen|NeonGreenV2|C6F533|D2FD02" "mobile-kiosk/app/src"
```

`rg` returned exit code `1`, which means no matches. The verification wrapper converted that expected no-match condition to success and printed:

```text
RG_RESULT: no matches
```

Result: **PASSED**.

## 5. Manual validation boundary

Physical-device visual validation was **not performed**. No emulator/device evidence was available in this verification. Visual appearance, sizing, contrast, scanner behavior, and layout on SUNMI hardware remain human validation steps.

## Concerns

1. Android lint is not clean: two fatal `RestrictedApi` errors occur at `MainActivity.kt:107` and `MainActivity.kt:128` around `dispatchKeyEvent` / `super.dispatchKeyEvent`.
2. Lint also reports 45 warnings and 5 hints; exact one-line findings are recorded above and the full explanations are in the generated lint text report.
3. The manifest merger reports a non-fatal warning for a redundant `tools:node="remove"` INTERNET permission declaration.
4. Gradle reports the non-fatal experimental `android.disallowKotlinSourceSets=false` option warning in both verification builds.
5. Successful assembly and asset presence do not establish physical-device visual correctness.

## Final verification after latest brand fixes — 2026-08-17 12:27 UTC-6

No production code was edited and no commit was created during this verification.

### assembleDebug

Command from `mobile-kiosk` with `JAVA_HOME=C:\Program Files\Android\Android Studio\jbr`:

```text
.\gradlew :app:assembleDebug --console=plain
BUILD SUCCESSFUL in 10s
37 actionable tasks: 6 executed, 31 up-to-date
Configuration cache entry reused.
exit_code: 0
```

Result: **PASSED**.

### APK brand assets

```text
APK: C:\Users\E-EC1-4830\StudioProjects\SUNMI-DEMO\mobile-kiosk\app\build\outputs\apk\debug\app-debug.apk
FOUND: assets/brand/syscom-large-logo.png | uncompressed=4532 bytes | compressed=4532 bytes
FOUND: assets/brand/sunmi.webp | uncompressed=2216 bytes | compressed=2216 bytes
```

Result: **PASSED**.

### No-green source search

Command:

```text
rg "NeonGreen|NeonGreenV2|C6F533|D2FD02" "mobile-kiosk/app/src"
RG_EXIT_CODE=1
RG_RESULT=no matches
```

Result: **PASSED**. Ripgrep exit code 1 is the expected no-match result.
