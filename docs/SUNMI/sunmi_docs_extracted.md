# SUNMI Developer Documentation - Extracted Content

> Extracted from https://docs.sunmi.com on 2026-08-17

---

## Table of Contents

1. [Capability Integration Guide](#1-capability-integration-guide)
2. [Biometric (Fingerprint) Development Guide](#2-biometric-fingerprint-development-guide)
3. [Software Management Module](#3-software-management-module)
4. [Remote Management - Introduction](#4-remote-management---introduction)
5. [OS Configuration Introduction](#5-os-configuration-introduction)
6. [SUNMI Remote Key Injection Introduction](#6-sunmi-remote-key-injection-introduction)
7. [OTA Introduction](#7-ota-introduction)
8. [Parameter Management Introduction](#8-parameter-management-introduction)
9. [App Store API](#9-app-store-api)

---

## 1. Capability Integration Guide

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xcmmeghjk546

SUNMI provides dozens of capabilities such as VAS capabilities and OS capabilities. You can integrate those capabilities into the cloud apps or Android apps you built through different methods.

Please first create the app, link the capabilities needed and then debug.

### I. List of Capabilities

1. Click [My Capabilities] to view all the capabilities that can be integrated.
2. Click [Details] to view the basic info of the capabilities.
3. Click [Integration] to view the apps integrated.

### II. Create Apps and Integration Capabilities

1. Log into [SUNMI Partners] and become a [Developer], click [Development]-[Apps Integration].
2. Click [Apps Integration], and [Create Apps] will pop up.
   - If the integration method of the capabilities is Cloud-to-Cloud, you need to create Cloud apps; if the integration method of the capabilities is End-to-End, you need to create Android apps.
   - If the integration methods of the capabilities are all accessible, you can create Cloud apps or Android apps as needed.
3. After Apps are successfully created, click Apps Integration and enter [App Details] to link capabilities as needed.
4. Select [Add Capabilities] to link the apps and capabilities.
5. After the apps are successfully linked to the capabilities, the interface corresponding to the capabilities can be called.

---

## 2. Biometric (Fingerprint) Development Guide

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xzcxeghjk546

### Core Background (Must Read)

- **Deprecated Old API:** `FingerprintManager` (API 23-28) is marked deprecated by Google. It is disabled on Sunmi's new ROM/devices, and direct calls will result in errors.
- **New API Requirement:** Mandatory migration to `BiometricPrompt` + `BiometricManager` (AndroidX library). Supports fingerprint/facial recognition with system-level security verification, natively adapted for all Sunmi POS series.
- **Official Documentation:**
  - Android Official Biometric Overview: https://developer.android.google.cn/reference/android/hardware/biometrics/package-summary
  - AndroidX Biometric Library Documentation: https://developer.android.google.cn/jetpack/androidx/releases/biometric
  - BiometricPrompt Development Guide: https://developer.android.google.cn/training/sign-in/biometric-auth

### Development Configuration

#### Dependency Configuration (Module-level build.gradle)

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

#### Permission Configuration (AndroidManifest.xml)

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

#### Dynamic Permission Request (Required for Android 10+)

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

### Core Integration Code

#### Complete Java Implementation (Activity/Fragment)

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
        // ... Copy code from 2.3 ...
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ... Copy code from 2.3 ...
    }
}
```

### Adaptation Notes

- **System Version Adaptation:**
  - Android 6.0~8.1: Automatically compatible with legacy fingerprint underlying layers, no extra modifications needed.
  - Android 9.0+: Mandatory use of BiometricPrompt; ensure `targetSdkVersion >= 28`.
  - New Sunmi POS (Android 11+): `FingerprintManager` is completely disabled; legacy code must be fully removed.

- **Key Error Handling:**

| Error Code | Handling Suggestion |
|------------|---------------------|
| ERROR_LOCKOUT | Prompt user to retry after 30 seconds |
| ERROR_LOCKOUT_PERMANENT | Guide user to unlock with device password |
| ERROR_HW_UNAVAILABLE | Advise user to restart device or contact after-sales |

### Technical Support

- Sunmi Developer Platform: https://sunmideveloper.com
- Technical Support Feedback: Contact Sunmi Technical Support Team

---

## 3. Software Management Module

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xddxeghjk546

### 1 Overview

The Software Management Module includes functions such as silently installing/uninstalling an app, enabling/disabling an app, getting the permissions requested by an app, setting the auto-start app, and adding the general app lock.

### 2 API List

| API | Description |
|-----|-------------|
| `void installAppV2(in String appFilePath, boolean autoStart, in OnInstallAppListener listener)` | Silently installs an app (decides whether to automatically launch the app after installation) |
| `void uninstallApp(String packageName, OnUninstallAppListener listener)` | Silently uninstalls an app |
| `void killApp(String packageName)` | Forcibly stops an app |
| `void restartApp(String packageName)` | Restarts an app |
| `boolean setBatteryOptimizationWhitelist(String whitelist)` | Setting battery optimization allowlist |
| `boolean setAppEnabled(String packageName, boolean enabled)` | Enables/Disables an app |
| `boolean grantAppPermissions(String packageName, String permissions)` | Grants app permissions (only dynamic permissions) |
| `String getRequestPermissions(String packageName)` | Gets the permissions requested by an app |
| `boolean allowAlertWindowPermission(String packageName)` | Grants floating window permissions to an app |
| `void prohibitUninstall(String packageName, boolean allowUninstall)` | Sets the app to be prohibited from being uninstalled |
| `String getProhibitUninstallList()` | Gets the list of apps prohibited from being uninstalled |
| `boolean enableAutoStartApp(boolean enable)` | Enables/Disables the auto-start app |
| `boolean isAutoStartAppEnabled()` | Checks whether the auto-start app is available |
| `boolean setAutoStartApp(String packageName)` | Sets the auto-start app |
| `String getAutoStartApp()` | Gets the auto-start app |
| `boolean clearAutoStartApp()` | Clears the auto-start app |
| `void clearApplicationUserDataWithResult(String packageName, IUnifiedCallback callback)` | Clears app user data (with result callback) |
| `void deleteApplicationCacheFiles(String packageName, IUnifiedCallback callback)` | Deletes app cache data (with result callback) |
| `boolean isForeground(String packageName)` | Checks whether the app is in the foreground |
| `boolean addAppToCommonAppLockList(in List<String> packageNames, int type, String password)` | Adds the general app lock |
| `boolean removeAppFromCommonAppLockList(in List<String> packageNames)` | Removes the general app lock |
| `List<String> getCommonAppLockList()` | Gets the list of apps with general app locks |
| `boolean isCommonAppLock(String packageName)` | Checks whether the app has set a general app lock |
| `void installApp(String appFilePath, OnInstallAppListener listener)` | Silently installs an app |
| `void revokeAppPermission(String packageName, String permissions)` | Removes the dynamic permissions requested by the app |
| `boolean setNotificationsEnabledForPackage(String packageName, boolean enabled)` | Sets whether the app can use notifications |
| `int removeAllRecentTasks()` | Clear Recent Tasks List |

### 3 API Details

#### 3.1 Silently installing an app (with auto-launch option)

**Prototype:** `void installAppV2(String appFilePath, boolean autoStart, in OnInstallAppListener listener);`

**Purpose:** Silently installs an app (and automatically launches the app after installation)

**Parameters:**
- `String appFilePath` - Local path of the target APK file
- `boolean autoStart` - Decides whether to automatically launch the app after installation
- `OnInstallAppListener listener` - Callback for app installation

**Notes:**
1. If installation succeeds, the callback of `onInstallFinished` and `onInstallSuccess` will be triggered.
2. If installation fails, the callback of `onInstallError` and `onInstallFail` will be triggered.
3. When `autoStart` is set to true, it only takes effect for the activity. Some apps may not have a default launch Activity.
4. For error codes, see Error Codes for Silent Installation of Applications.

**Sample Code:**
```java
try {
    sTmsApi.getSoftwareManager().installAppV2("/sdcard/xxx/test.apk", true, new OnInstallAppListener.Stub() {
        @Override
        public void onInstallFinished() throws RemoteException {
        }
        @Override
        public void onInstallError(int errorId) throws RemoteException {
        }
        @Override
        public void onInstallSuccess(String packagename) throws RemoteException {
        }
        @Override
        public void onInstallFail(String packagename, int errorId) throws RemoteException {
        }
    });
} catch (Exception e) {
    e.printStackTrace();
}
```

#### 3.2 Silently uninstalls an app

**Prototype:** `void uninstallApp(String packageName, OnUninstallAppListener listener);`

**Parameters:**
- `packageName` - Package name of the app to be uninstalled
- `listener` - API callback: After uninstallation is complete, the result will be returned.

#### 3.3 Forcibly stopping an app

**Prototype:** `void killApp(String packageName);`

#### 3.4 Restarting an app

**Prototype:** `void restartApp(String packageName);`

**Notes:** An app may fail to start due to: App not installed, App has no launch Activity, App disabled, Launch Activity disabled, Installation incomplete, or System apps lack a public launch Intent.

#### 3.5 Setting battery optimization allowlist

**Prototype:** `boolean setBatteryOptimizationWhitelist(String whitelist);`

**Parameters:** `whiteList` - Battery optimization allowlist; pass in app package name; use English commas to separate multiple apps.

#### 3.6 Enabling/Disabling an app

**Prototype:** `boolean setAppEnabled(String packageName, boolean enabled)`

**Notes:** You cannot operate system pre-installed apps whose package names start with `android`, `vendor`, or `com.android`.

#### 3.7 Granting dynamic permissions to an app

**Prototype:** `boolean grantAppPermissions(String packageName, String permissions)`

#### 3.8 Getting permissions requested by an app

**Prototype:** `String getRequestPermissions(String packageName)`

#### 3.9 Granting floating window permissions to an app

**Prototype:** `boolean allowAlertWindowPermission(String packageName)`

#### 3.10 Setting the app to be prohibited from being uninstalled

**Prototype:** `void prohibitUninstall(String packageName, boolean allowUninstall)`

#### 3.11 Getting the list of apps prohibited from being uninstalled

**Prototype:** `String getProhibitUninstallList()`

**Returns:** List of apps prohibited from being uninstalled, separated by semicolons (e.g., `com.a:com.b:com.c`)

#### 3.12 Enabling/Disabling the auto-start app

**Prototype:** `boolean enableAutoStartApp(boolean enable)`

#### 3.13 Checking whether the auto-start app is available

**Prototype:** `boolean isAutoStartAppEnabled()`

#### 3.14 Setting the auto-start app

**Prototype:** `boolean setAutoStartApp(String packageName)`

**Note:** Only one auto-start app can be set.

#### 3.15 Getting the auto-start app

**Prototype:** `String getAutoStartApp()`

#### 3.16 Clearing the auto-start app

**Prototype:** `boolean clearAutoStartApp()`

#### 3.17 Clearing app user data (with result callback)

**Prototype:** `void clearApplicationUserDataWithResult(String packageName, IUnifiedCallback callback)`

#### 3.18 Deleting app cache data (with result callback)

**Prototype:** `void deleteApplicationCacheFiles(String packageName, IUnifiedCallback callback)`

#### 3.19 Checking whether the app is in the foreground

**Prototype:** `boolean isForeground(String packageName);`

#### 3.20 Adding the general app lock

**Prototype:** `boolean addAppToCommonAppLockList(List<String> packageNames, int type, String password);`

**Password Types:**
- `-1`: Mixed password (supports up to 8 characters)
- `0`: 4-digit numeric password
- `1`: Mixed password (supports up to 8 characters)

#### 3.21 Removing the general app lock

**Prototype:** `boolean removeAppFromCommonAppLockList(List<String> packageNames);`

#### 3.22 Getting the list of apps with general app locks

**Prototype:** `List<String> getCommonAppLockList();`

#### 3.23 Checking whether the app has set a general app lock

**Prototype:** `boolean isCommonAppLock(String packageName);`

#### 3.24 Silently installing the app (legacy)

**Prototype:** `void installApp(String appFilePath, OnInstallAppListener listener)`

**Note:** The file to be installed must be placed under the `/sdcard/` path.

#### 3.25 Removing app dynamic permissions

**Prototype:** `void revokeAppPermission(String packageName, String permissions)`

#### 3.26 Clear Recent Tasks List

**Prototype:** `int removeAllRecentTasks()`

**Returns:** 0=Success, 1=Failed, -40=Interface not supported, -41=System service not found

---

## 4. Remote Management - Introduction

**URL:** https://docs.sunmi.com/en-US/caiaiieghjk579/xczxeghjk491

> Content was not fully rendered in the fetched HTML (SPA dynamic loading). The Remote Management section includes:
> - Device APIs (Online/Offline Status, Coordinates, Real-time Status)
> - Command APIs (Power Off, Reboot, Lock/Unlock Device)
> - Device Group APIs (Get device info, Groups CRUD)
> - Device Transfer APIs (Transfer to friend, Sub account binding)

---

## 5. OS Configuration Introduction

**URL:** https://docs.sunmi.com/en-US/caixqfeghjk535/xzxdeghjk524

The **OS Configuration Module** of SUNMI DMP is a flexible and efficient configuration tool designed to help users achieve refined device management with greater convenience. Through this module, users can flexibly set a series of personalized rules and usage restrictions for managed devices or device groups, meeting the operational needs of different scenarios.

Based on device types, usage scenarios, or business requirements, users can configure the desired operating status or management policies for different devices. Whether supporting unified deployment or differentiated management, the OS Configuration Module provides stable and reliable configuration delivery capabilities, serving as an important means to efficiently apply configurations to managed devices.

Users can create configuration policies independently and publish them after verification. Once published, policies can be flexibly associated with one or multiple managed devices, enabling precise control and continuous management of device operation status.

With the OS Configuration Module, SUNMI DMP helps users quickly translate management intentions into device behaviors, effectively improving the efficiency and flexibility of device management and providing solid support for stable business operations.

On this basis, to further meet the needs of automated operations, system integration, and customized business scenarios, SUNMI DMP also provides **OS Configuration Open API**. Through the Open API, developers can seamlessly integrate configuration management capabilities into their own business systems, enabling automated creation, publishing, association, and querying of configuration policies, helping enterprises build a more efficient and flexible device management closed loop.

---

## 6. SUNMI Remote Key Injection Introduction

**URL:** https://docs.sunmi.com/en-US/caixrmeghjk546/xqdqeghjk513

> Content was not fully rendered in the fetched HTML (SPA dynamic loading). The Remote Key Injection section includes:
> - Key APIs (Upload key, Assign key, Reassign key, Delete key, Get protection key, Save initial key, Generate Random Key, Get Key Info)
> - Device injection status, Device Unbind/Unlock
> - Push ThirdParty Key Download
> - Task APIs (Get Create Task Device, Create Task)

---

## 7. OTA Introduction

**URL:** https://docs.sunmi.com/en-US/caicqzeghjk557/xziqeghjk513

> Content was not fully rendered in the fetched HTML (SPA dynamic loading). The OTA section includes:
> - OTA Introduction
> - Callback for OTA Upgrade Task Approval Notifications

---

## 8. Parameter Management Introduction

**URL:** https://docs.sunmi.com/en-US/caicaieghjk579/xzfaeghjk480

### Introduction

#### What is Parameter Management?

Parameter management is a dedicated function for the payment industry. Under our solution, developers define parameter templates (key and value) for their submitted applications. Service providers copy these templates, adjust the values, and remotely deploy the configurations to devices via different groups or serial numbers (SN). Developers may create separate parameter templates for different versions of the same application, or apply one shared template across all versions. We also provide an SDK for developers to integrate into their payment applications, so as to receive parameters pushed from the cloud.

#### Which Applications Requiring Parameter Configuration?

Payment applications need parameter configuration, including information such as merchant number, merchant ID, merchant address and Tip.

#### Who will configure the Parameter?

Developers create parameter templates, which can be set with default values or left blank. When pushing parameters to devices, distributors, service providers and other relevant parties shall fill in specific information (e.g., merchant name, merchant address) based on the templates before delivering the parameters to devices.

#### Which prerequisites for Parameter Push?

The payment application developed must integrate the parameter SDK to successfully receive parameters pushed from the cloud. Refer to Capability Integration Guide.

---

## 9. App Store API

**URL:** https://docs.sunmi.com/en-US/caixfreghjk568/faceghjk502

### 1. API List

#### 1.1 App Management

| API Name | Description |
|----------|-------------|
| Create App in AppStore | Create a new app, requires preparing category, terminal, and resource uuids first |
| Delete App from Appstore | Delete a created app within the channel |
| Upgrade App | Upgrade the version of an audited app, supports formal/gray upgrade |
| Get App detail | Query the basic details configuration of an app |
| Get App review | Batch proactively fetch app-related audit statuses and results |
| Update App details | Update basic information like app introduction, screenshots, and compatible terminals |
| Update App version detail | Switch deployment mode (gray to formal), adjust gray list and status |
| Get App latest version | Get the newest version data of an app, supports fetching historical versions |
| Audit Result Callback | [Implemented by Developer] Receive the audit result pushed by the server |

#### 1.2 App Assets

| API Name | Description |
|----------|-------------|
| Upload Apk | Upload the app's APK installation package to get the resource uuid |
| Upload Image | Upload app icons, vertical/horizontal screenshots to get the resource uuid |

#### 1.3 App Metadata

| API Name | Description |
|----------|-------------|
| Get App categories | Get the dictionary of supported app categories |
| Get country language codes | Get the dictionary of country language codes |
| Get device models | Get options for compatible device terminals |

### 2. API Interaction Flow

#### 2.1 Create / Delete App Flow

```
Upload Apk → Upload Image → Get device models → Get App categories →
Get country language codes → Create App in AppStore → Get App detail →
Get App review (polling) → [optional] Audit Result Callback →
Delete App from Appstore (when needed)
```

#### 2.2 App Upgrade Flow

```
Upload Apk → Upgrade App → Get App review → Update App version detail →
Get App review → Get App latest version
```

### 3. Changelog

| Version | Date | Update Content | Remarks |
|---------|------|----------------|---------|
| v1.3.0 | 2026-06-03 | Added optional `gray_release_mode` to Create App, Upgrade App, and Update App version detail APIs; added `gray_release_mode` echo field in `extend_version_info` response of Get App latest version API | Gray release mode field update |
| v1.2.0 | 2026-05-22 | Added read-only field `is_param_app` (whether the app is a parameter app) to Get App detail API | Parameter app exposure |
| v1.1.0 | 2026-04-21 | Added `support_landscape_portrait` field to Get device models API for landscape & portrait device support | Landscape & portrait feature |
| v1.0.0 | 2026-03-15 | Initial release | - |

---

## Navigation Structure

The SUNMI documentation site is organized into the following main sections:

- **Docs Home** - Developer Documentation Center Introduction
- **Integration Guide** - Get Started, USB Debugging, Cashier Demo, General Interface Description, SN/Identifier
- **Hardware Products** - Device Models, CPad, CPad PAY, D3, D3 PRO
- **Software Handbooks & Bulletins** - Introduction, Partner Platform, User Account, Entity Account
- **Driver & Integration** - Debugging Devices
- **SUNMI OpenAPI** - Capability Integration Guide, Signature & Verification, Generating App Public Key, Common Error Codes
- **Remote Management** - Device APIs, Command APIs, Device Group APIs, Device Transfer APIs
- **OS Configuration** - Introduction, Development Guide, Access Rules, Signing Documentation, Error Codes, Interface List (APN, Scheduled Power on/off, APP Permission, Setting items)
- **App Store** - App Publishing API, App Management, App Assets, App Metadata, App Publishing Process
- **SUNMI Remote Key Injection** - Introduction, Appendix, API List (Key APIs, Task APIs)
- **Parameter Management** - Introduction, JSON Structure, API Documentation, Templates, Device Parameters
- **OTA** - Introduction, Callback for OTA Upgrade Task Approval Notifications
- **Printer Development** - Built-in printer, SDK Reference, JavaScript/Flutter/UniApp/Cordova Printers, Cloud Printer
- **Scanning Development** - Camera-Based Barcode Scanner, Code Scanner Engine
- **Customer Display Development** - Secondary Display API, T2 Mini, T1 Vice Screen
- **Electronic Scale Development**
- **Card Reader Development** - NFC, Magnetic Stripe, PSAM/ETC/M112, UHF RFID
- **Cash Drawer Development**
- **Status Light Development**
- **Fingerprints Development**
- **Android Device Connection Development**
- **Payment Sound Box Development**
- **Electronic Label Development**
- **Biometric (Fingerprint) Development Guide**
