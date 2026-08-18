# Cash Drawer Development

> **Section:** Integration Guide > Cash Drawer Development
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Table of Contents

1. [How to Operate Cash Drawer on Sunmi Device](#1-how-to-operate-cash-drawer-on-sunmi-device)
2. [Cash Drawer Driver Trigger](#2-cash-drawer-driver-trigger)

---

## 1. How to Operate Cash Drawer on Sunmi Device

### Overview

SUNMI devices support cash drawer control through the RJ12 port. The cash drawer can be opened programmatically using the printer SDK.

### Integration

```gradle
dependencies {
    implementation 'com.sunmi:printerx:1.0.20'
}
```

### Key APIs

| API | Description |
|-----|-------------|
| `openCashDrawer()` | Open the cash drawer |
| `isCashDrawerOpen()` | Check if cash drawer is open |
| `setCashDrawerPin(int pin)` | Set cash drawer pin |

### Usage

```java
// Get printer instance
Printer printer = PrinterSdk.getInstance().getPrinter();

// Open cash drawer via CashDrawerApi
printer.cashDrawerApi().openCashDrawer();
```

### Alternative Method (Using Printer Service)

```java
// Using AIDL printer service
SunmiPrinterService printerService = MyApplication.getInstance().getSunmiPrinterService();

if (printerService != null) {
    try {
        // Send ESC/POS command to open cash drawer
        byte[] openDrawer = new byte[]{0x1B, 0x70, 0x00, 0x19, 0xFA};
        printerService.sendRawData(openDrawer, null);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### ESC/POS Commands for Cash Drawer

| Command | Description |
|---------|-------------|
| `1B 70 00 19 FA` | Open cash drawer (pin 0) |
| `1B 70 01 19 FA` | Open cash drawer (pin 1) |

---

## 2. Cash Drawer Driver Trigger

### Overview

SUNMI provides cash drawer driver triggers for different platforms and use cases.

### Platforms Supported

- Windows
- Android
- iOS
- macOS

### Windows

#### Driver Installation

1. Download the SUNMI Cash Drawer Driver from the official website
2. Run the installer
3. Connect the cash drawer via USB or serial port
4. The driver will automatically detect the cash drawer

#### Usage

```csharp
// C# Example
using Sunmi.CashDrawer;

CashDrawer drawer = new CashDrawer();
drawer.Open();
```

### Android

#### Using Printer SDK

```java
// See Section 1 for detailed usage
PrinterSdk.getInstance().getPrinter().cashDrawerApi().openCashDrawer();
```

### iOS

#### Using SUNMI SDK

```swift
import SunmiSDK

let cashDrawer = SunmiCashDrawer()
cashDrawer.open { result in
    if result.success {
        // Cash drawer opened
    }
}
```

### macOS

#### Using SUNMI SDK

```swift
import SunmiSDK

let cashDrawer = SunmiCashDrawer()
cashDrawer.open()
```

### Testing Tool

SUNMI provides a testing tool for cash drawer operations:

1. Download the Cash Drawer Testing Tool
2. Connect the cash drawer
3. Run the testing tool
4. Click "Open Drawer" to test

### Square Integration

SUNMI cash drawers are compatible with Square POS systems:

1. Connect the cash drawer to the SUNMI device
2. Configure Square POS to use the cash drawer
3. The cash drawer will automatically open after transactions

### Trigger APP

SUNMI provides a Trigger APP for cash drawer operations:

1. Install the Trigger APP on the SUNMI device
2. Configure the cash drawer settings
3. Use the APP to open/close the cash drawer

---

## Related Documentation

- [Printer Development](./04-PRINTER-DEVELOPMENT.md)
- [General Interface Description](./02-DEVELOPMENT-GUIDE.md#general-interface-description)
