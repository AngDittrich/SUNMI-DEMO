# Scanning Development

> **Section:** Integration Guide > Scanning Development
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Table of Contents

1. [Camera-Based Barcode Scanner SDK](#1-camera-based-barcode-scanner-sdk)
2. [Code Scanner Engine](#2-code-scanner-engine)
3. [Code Scanning Base](#3-code-scanning-base)
4. [CodeID](#4-codeid)
5. [Barcode Scanner User Guide](#5-barcode-scanner-user-guide)

---

## 1. Camera-Based Barcode Scanner SDK

SUNMI provides camera-based barcode scanning SDK for multiple platforms.

### Android

#### Integration

```gradle
dependencies {
    implementation 'com.sunmi:camerascanner:1.0.0'
}
```

#### Key APIs

| API | Description |
|-----|-------------|
| `initCamera()` | Initialize camera |
| `startPreview()` | Start camera preview |
| `stopPreview()` | Stop camera preview |
| `scanBarcode()` | Scan barcode from camera feed |
| `scanFromBitmap(Bitmap bitmap)` | Scan barcode from image |
| `setScanCallback(ScanCallback callback)` | Set scan result callback |

#### Usage

```java
CameraScanner scanner = new CameraScanner(context);
scanner.initCamera();

scanner.setScanCallback(new ScanCallback() {
    @Override
    public void onScanResult(String result, int format) {
        // Handle scanned barcode
    }
});

scanner.startPreview();
```

### Flutter

```dart
import 'package:sunmi_scanner/sunmi_scanner.dart';

// Initialize scanner
SunmiScanner.init();

// Start scanning
SunmiScanner.startScan(
  onResult: (String barcode, int format) {
    // Handle scanned barcode
  },
);

// Stop scanning
SunmiScanner.stopScan();
```

### uni-app

```javascript
import scanner from '@sunmi/scanner-plugin';

// Start scanning
scanner.startScan({
  success: (data) => {
    console.log('Scanned:', data.barcode);
  },
  fail: (err) => {
    console.error('Scan failed:', err);
  }
});
```

### Cordova

```javascript
cordova.plugins.SunmiScanner.startScan(
  function(result) {
    console.log('Scanned:', result.barcode);
  },
  function(error) {
    console.error('Scan failed:', error);
  }
);
```

---

## 2. Code Scanner Engine

The Code Scanner Engine provides infrared scan code capabilities for SUNMI devices.

### Features

- High-speed barcode recognition
- Support for 1D and 2D barcodes
- Infrared scanning in low-light conditions
- Hardware-triggered scanning

### Integration

```gradle
dependencies {
    implementation 'com.sunmi:codescanner:1.0.0'
}
```

### Key APIs

| API | Description |
|-----|-------------|
| `init()` | Initialize scanner engine |
| `startScan()` | Start scanning |
| `stopScan()` | Stop scanning |
| `setScanMode(int mode)` | Set scan mode |
| `setScanType(int type)` | Set barcode type filter |
| `release()` | Release resources |

---

## 3. Code Scanning Base

### Overview

Code scanning base provides the foundational scanning infrastructure for SUNMI devices.

### Supported Barcode Types

#### 1D Barcodes
- UPC-A
- UPC-E
- EAN-13
- EAN-8
- Code 128
- Code 39
- Code 93
- ITF
- Codabar
- GS1 DataBar

#### 2D Barcodes
- QR Code
- Data Matrix
- PDF417
- Aztec
- MaxiCode

---

## 4. CodeID

### Overview

CodeID provides barcode format identification and classification.

### Usage

```java
CodeID codeID = new CodeID();
int format = codeID.identify(barcodeData);
String formatName = codeID.getFormatName(format);
```

---

## 5. Barcode Scanner User Guide

### Hardware Scanner (Keyboard Input Mode)

SUNMI devices with built-in 2D hardware scanners work like a **keyboard input device**. When the scanner reads a barcode, it sends the decoded text as keyboard input to the currently focused text field.

#### How It Works

1. User opens a text input field (EditText, etc.)
2. User presses the scan trigger button
3. Scanner reads the barcode/QR code
4. Scanner sends the decoded text as keyboard input to the focused field
5. App receives the text via normal text change listener

#### Implementation

No special SDK needed! Just use a standard EditText:

```xml
<EditText
    android:id="@+id/etScannerInput"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Scan barcode here" />
```

```java
EditText etScannerInput = findViewById(R.id.etScannerInput);

etScannerInput.addTextChangedListener(new TextWatcher() {
    @Override
    public void afterTextChanged(Editable s) {
        String scannedData = s.toString();
        if (!scannedData.isEmpty()) {
            // Process scanned data
            processScannedData(scannedData);
            // Clear the field for next scan
            etScannerInput.setText("");
        }
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
});

private void processScannedData(String data) {
    Log.d("Scanner", "Scanned: " + data);
    // Handle barcode data (product lookup, payment, etc.)
}
```

#### Broadcast Method (Alternative)

Some SUNMI devices also support receiving scan data via broadcast:

```java
BroadcastReceiver scanReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String barcode = intent.getStringExtra("barcode");
        int format = intent.getIntExtra("format", 0);
        // Handle scanned barcode
    }
};

IntentFilter filter = new IntentFilter("com.sunmi.scanner.ACTION");
registerReceiver(scanReceiver, filter);
```

#### Key Event Method (Legacy)

For older devices or specific use cases:

```java
@Override
public boolean dispatchKeyEvent(KeyEvent event) {
    // Scanner triggers key events
    // Process barcode data from key events
    return super.dispatchKeyEvent(event);
}
```

### Scanner Configuration

| Setting | Description |
|---------|-------------|
| Scan Mode | Continuous/Single trigger |
| Vibration | Enable/disable vibration on scan |
| Beep | Enable/disable beep on scan |
| LED | Enable/disable LED indicator |
| Prefix | Add prefix to scanned data |
| Suffix | Add suffix to scanned data |

### Configuration

#### Scanner Settings

| Setting | Description |
|---------|-------------|
| Scan Mode | Continuous/Single trigger |
| Vibration | Enable/disable vibration on scan |
| Beep | Enable/disable beep on scan |
| LED | Enable/disable LED indicator |
| Prefix | Add prefix to scanned data |
| Suffix | Add suffix to scanned data |

---

## Related Documentation

- [AI SDK](./06-AI-SDK.md)
- [Card Reader Development](./08-CARD-READER.md)
