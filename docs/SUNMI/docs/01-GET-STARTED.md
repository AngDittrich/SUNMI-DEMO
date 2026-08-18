# Get Started

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xmafeghjk535
> **Update Time:** 2026-05-29 18:17:46

---

## Overview

This guide walks you through registering a SUNMI developer account, setting up your development environment, integrating the unified SUNMI SDK, debugging your application on a physical device, and publishing it to the app store.

---

## Workflow Overview

```
[ Request Test Device ] --> [ Register Partner Platform ] --> [ Apply for Developer Status ]
                                                                        |
                                                                        v
[ App Deployment/Release ] <-- [ Integration & Debugging ] <-- [ Access Hardware Capabilities ]
```

---

## 1. Account Registration and Certification

### 1.1 Request a Development Device
- Contact your dedicated SUNMI sales representative
- **Required Information:** Company details, point of contact, target device models, and a detailed development roadmap
- **Review Timeline:** 1 to 3 business days

### 1.2 Register a SUNMI Partner Platform Account

Select the appropriate regional cloud instance:

| Region | URL |
|--------|-----|
| Global/China Cloud | [partner.sunmi.com](https://partner.sunmi.com) |
| US Cloud (North America) | [partner.us.sunmi.com](https://partner.us.sunmi.com) |
| EU Cloud (Europe) | [partner.eu.sunmi.com](https://partner.eu.sunmi.com) |

> **Note:** Register with your corporate email domain to streamline future device fleet management and enterprise app distribution.

### 1.3 Apply for Developer Certification
1. Log in and navigate to the "Apply to Become a Developer" page
2. Select entity type (Enterprise Developer or Individual Developer) and upload business/legal credentials
3. Wait for review (1-3 business days). Once approved, get access to SUNMI open APIs and hardware SDKs.

---

## 2. Development Environment Setup

SUNMI devices run Sunmi OS (deeply customized Android). Standard Android development practices apply.

### Prerequisites
- **IDE:** Android Studio (latest stable version)
- **Languages:** Java or Kotlin
- **Minimum SDK:** API Level 28 (Android 9) or higher

### 2.1 Add SDK Dependencies

```gradle
dependencies {
    // Core SUNMI Printer SDK
    implementation 'com.sunmi:printerlibrary:1.0.23'
}
```

### 2.2 Bind the Hardware Service

```java
package com.sm.sdk.demo;

import android.app.Application;
import android.content.Context;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;

public class MyApplication extends Application {
    private static MyApplication instance;
    private SunmiPrinterService sunmiPrinterService;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        bindPrintService(this);
    }

    private void bindPrintService(Context context) {
        try {
            InnerPrinterManager.getInstance().bindService(context, new InnerPrinterCallback() {
                @Override
                protected void onConnected(SunmiPrinterService service) {
                    sunmiPrinterService = service;
                }

                @Override
                protected void onDisconnected() {
                    sunmiPrinterService = null;
                }
            });
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    public SunmiPrinterService getSunmiPrinterService() {
        return sunmiPrinterService;
    }
}
```

---

## 3. Application Development and Debugging

### 3.1 Example: Printing Plain Text

```java
package com.sm.sdk.demo;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.sunmi.peripheral.printer.SunmiPrinterService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SunmiDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        executeTextPrint("Hello SUNMI!\n");
    }

    private void executeTextPrint(String text) {
        SunmiPrinterService printerService = MyApplication.getInstance().getSunmiPrinterService();
        
        if (printerService != null) {
            try {
                printerService.printText(text, null);
            } catch (Exception e) {
                Log.e(TAG, "Failed to send print command.", e);
            }
        } else {
            Log.w(TAG, "SUNMI Printer Service is unavailable.");
        }
    }
}
```

### 3.2 On-Device Debugging Steps
1. Connect the SUNMI hardware device using a **USB data cable**
2. Go to **Settings > About Device**, tap **Build Number** repeatedly to unlock developer mode
3. Enter **Developer Options**, enable **USB Debugging**
4. Select the connected SUNMI device in Android Studio, click **Run**

---

## 4. App Deployment and Store Publishing

### 4.1 Prepare Deployment Assets
- **Binary Artifact:** Release-signed APK (V2/V3 signature recommended)
- **Visual Assets:** High-res icons and 3+ screenshots
- **Legal Copy:** Localized descriptions, Privacy Policy URL, permission disclosures

### 4.2 Submit for Review and Target Distribution
1. Log in to [SUNMI Partner Platform](https://partner.sunmi.com)
2. Navigate to App Management, upload APK
3. **Configure Distribution Policies:** Scope by Device Model, Geographic Region, or Merchant Accounts
4. Submit for review (1-3 business days). Auto-goes live upon approval.

---

## 5. Technical Support

| Resource | Link/Info |
|----------|-----------|
| Documentation Center | [SUNMI Developer Docs](https://docs.sunmi.com/en-US/ciczeghjk557/xdxmeghjk546) |
| Technical Support Line | 400-6666-509 (Business Days, 9:00 - 18:00 UTC+8) |

---

## Next Steps
- [USB Debugging Management User Guide](./02-DEVELOPMENT-GUIDE.md#usb-debugging-management)
- [SunmiCustomer API](./03-SUNMI-CUSTOMER-API.md)
- [Printer Development](./04-PRINTER-DEVELOPMENT.md)
