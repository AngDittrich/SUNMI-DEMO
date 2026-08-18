# Biometric (Fingerprint) Development Guide

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xzcxeghjk491
> **Update Time:** 2026-04-03 22:37:01

---

## Table of Contents

1. [Core Background](#1-core-background)
2. [Development Configuration](#2-development-configuration)
3. [Core Integration Code](#3-core-integration-code)
4. [Adaptation Notes](#4-adaptation-notes)
5. [Error Handling](#5-error-handling)

---

## 1. Core Background

### Important: API Migration Required

- **Deprecated Old API:** `FingerprintManager` (API 23-28) is marked deprecated by Google. It is disabled on Sunmi's new ROM/devices, and direct calls will result in errors.
- **New API Requirement:** Mandatory migration to `BiometricPrompt` + `BiometricManager` (AndroidX library). Supports fingerprint/facial recognition with system-level security verification, natively adapted for all Sunmi POS series.

### Official Documentation

- [Android Official Biometric Overview](https://developer.android.google.cn/reference/android/hardware/biometrics/package-summary)
- [AndroidX Biometric Library Documentation](https://developer.android.google.cn/jetpack/androidx/releases/biometric)
- [BiometricPrompt Development Guide](https://developer.android.google.cn/training/sign-in/biometric-auth)

---

## 2. Development Configuration

### Dependency Configuration (Module-level build.gradle)

```java
android {
    compileSdkVersion 34
    defaultConfig {
        minSdkVersion 23  // Minimum system version
        targetSdkVersion 34
    }
}

dependencies {
    // Core dependency
    implementation 'androidx.biometric:biometric:1.1.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
}
```

### Permission Configuration (AndroidManifest.xml)

```java
<!-- Core biometric permission -->
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<!-- Compatible with legacy systems (optional) -->
<uses-permission android:name="android.permission.USE_FINGERPRINT" />

<!-- Declare hardware support (not mandatory, avoid device filtering) -->
<uses-feature
    android:name="android.hardware.fingerprint"
    android:required="false" />
```

### Dynamic Permission Request (Required for Android 10+)

```java
private static final int REQUEST_BIOMETRIC_PERMISSION = 1001;

// Request permission
private void requestBiometricPermission(Context context) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.USE_BIOMETRIC)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.USE_BIOMETRIC},
                REQUEST_BIOMETRIC_PERMISSION);
    }
}

// Permission callback
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_BIOMETRIC_PERMISSION) {
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Fingerprint permission not enabled, function unavailable", Toast.LENGTH_LONG).show();
        }
    }
}
```

---

## 3. Core Integration Code

### Complete Java Implementation (Activity/Fragment)

```java
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class SunmiBiometricActivity extends AppCompatActivity {

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunmi_biometric);

        // 1. Request permission
        requestBiometricPermission(this);
        // 2. Initialize biometrics
        initBiometric();
        // 3. Bind button to trigger verification
        findViewById(R.id.btn_verify).setOnClickListener(v -> startBiometricVerify());
    }

    /**
     * Initialize biometric components
     */
    private void initBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        // Build verification callback
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                runOnUiThread(() -> {
                    Toast.makeText(SunmiBiometricActivity.this, "Verification succeeded, execute business logic", Toast.LENGTH_SHORT).show();
                    // TODO: Integrate checkout/login/payment and other business logic
                });
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                runOnUiThread(() -> Toast.makeText(SunmiBiometricActivity.this, "Fingerprint mismatch, please try again", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                runOnUiThread(() -> Toast.makeText(SunmiBiometricActivity.this, "Verification error: " + errString, Toast.LENGTH_LONG).show());
            }
        });

        // Build verification dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Verification")
                .setSubtitle("Place finger to complete checkout/authorization")
                .setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG) // Strong security mode
                .build();
    }

    /**
     * Start fingerprint verification (pre-check device compatibility)
     */
    private void startBiometricVerify() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "No fingerprint hardware on device", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No fingerprint enrolled, please add in Settings", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "Fingerprint function unavailable", Toast.LENGTH_LONG).show();
        }
    }

    // Dynamic permission request method (same as 2.3)
    private void requestBiometricPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.USE_BIOMETRIC)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.USE_BIOMETRIC},
                    REQUEST_BIOMETRIC_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BIOMETRIC_PERMISSION) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fingerprint permission not enabled, function unavailable", Toast.LENGTH_LONG).show();
            }
        }
    }
}
```

---

## 4. Adaptation Notes

### System Version Adaptation

| Android Version | Adaptation |
|-----------------|------------|
| **Android 6.0-8.1** | Automatically compatible with legacy fingerprint underlying layers, no extra modifications needed |
| **Android 9.0+** | Mandatory use of BiometricPrompt; ensure `targetSdkVersion >= 28` |
| **Android 11+ (New Sunmi POS)** | `FingerprintManager` is completely disabled; legacy code must be fully removed |

### Device Compatibility

| Device Series | Support |
|---------------|---------|
| T3 PRO, D3 PRO | Full biometric support |
| V3, V3H | Full biometric support |
| FLEX 3 | Full biometric support |
| M3, L3 | Full biometric support |
| T2s, D2s | Partial support (fingerprint only) |
| V2, V2S | Legacy support (deprecated) |

---

## 5. Error Handling

### Key Error Codes

| Error Code | Constant | Handling Suggestion |
|------------|----------|---------------------|
| 7 | `ERROR_LOCKOUT` | Prompt user to retry after 30 seconds |
| 8 | `ERROR_LOCKOUT_PERMANENT` | Guide user to unlock with device password |
| 12 | `ERROR_HW_UNAVAILABLE` | Advise user to restart device or contact after-sales |
| 1 | `ERROR_CANCELED` | User cancelled the operation |
| 2 | `ERROR_TIMEOUT` | Verification timed out, prompt to try again |
| 3 | `ERROR_SPACE_NOT足够的空间` | Insufficient space for biometric data |
| 4 | `ERROR_TOO_MANY_ATTEMPTS` | Too many failed attempts, locked out |
| 5 | `ERROR_NEGATIVE_BUTTON` | User tapped negative button |

### Error Handling Implementation

```java
@Override
public void onAuthenticationError(int errorCode, CharSequence errString) {
    super.onAuthenticationError(errorCode, errString);
    
    switch (errorCode) {
        case BiometricPrompt.ERROR_LOCKOUT:
            // Locked out temporarily
            Toast.makeText(this, 
                "Too many attempts. Please wait 30 seconds.", 
                Toast.LENGTH_LONG).show();
            break;
            
        case BiometricPrompt.ERROR_LOCKOUT_PERMANENT:
            // Permanently locked out
            Toast.makeText(this, 
                "Device locked. Please use password to unlock.", 
                Toast.LENGTH_LONG).show();
            break;
            
        case BiometricPrompt.ERROR_HW_UNAVAILABLE:
            // Hardware unavailable
            Toast.makeText(this, 
                "Fingerprint sensor unavailable. Please restart device.", 
                Toast.LENGTH_LONG).show();
            break;
            
        case BiometricPrompt.ERROR_CANCELED:
            // User cancelled
            break;
            
        default:
            Toast.makeText(this, 
                "Error: " + errString, 
                Toast.LENGTH_LONG).show();
    }
}
```

---

## Technical Support

| Resource | Link |
|----------|------|
| Sunmi Developer Platform | https://sunmideveloper.com |
| Technical Support | Contact Sunmi Technical Support Team |
| Official Documentation | https://docs.sunmi.com |

---

## Related Documentation

- [Get Started](./01-GET-STARTED.md)
- [SunmiCustomer API](./03-SUNMI-CUSTOMER-API.md)
- [Card Reader Development](./08-CARD-READER.md)
