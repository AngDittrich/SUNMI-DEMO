# Development Guide and Basics

> **Section:** Integration Guide > Development Guide and Basics
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Table of Contents

1. [USB Debugging Management](#1-usb-debugging-management)
2. [Sunmi Cashier Demo](#2-sunmi-cashier-demo)
3. [General Interface Description (RJ12/RJ11/USB)](#3-general-interface-description)
4. [Development Tips](#4-development-tips)

---

## 1. USB Debugging Management

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdrzeghjk557
> **Update Time:** 2026-02-27 15:04:39

### Overview

By default, Sunmi devices can be debugged by simply connecting them via USB and enabling debug mode through the Settings menu.

SUNMI offers device debugging permission control. Once a partner enables this feature in the backend, debugging requires permission obtained via email or phone number.

> **Note:** Debugging permission control only takes effect for devices that are bound to a partner account.

### Edit Entry

- **Accounts without the "Configuration File" feature enabled:**
  Device Management > Common > Debuggers

- **Accounts with the "Configuration File" feature enabled:**
  Device Management > Profiles > Security Settings > USB Debugging Protection

### How to Debug a Device After Enabling Debugging Control

1. **Add debugging personnel**
   - The developer needs to know the channel that the device belongs to
   - Management personnel must add the debugging personnel's mobile phone number or E-mail in Sunmi partner platform

2. **Plug in USB**
   - (Please switch off the 'debugging permission control' temporarily on V1s/V2/M2/L2)
   - Connect the device to the computer after confirming that you have your own mobile phone or E-mail for debugging permission
   - It is suggested that the developer debug under Windows

3. **Get the verification code**
   - Click "I want to debug" to enter permission verification
   - USB debugging mode will be enabled by customization

4. **Verify the permission**
   - Click "Obtain the verification code" after entering the previously added mobile phone number or E-mail
   - Fill in the verification code and click "Authorize and enable debugging"

5. **Open the permission**
   - After opening the permission, check if there is output in logcat to judge if it's OK to debug the device

---

## 2. Sunmi Cashier Demo

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdiceghjk502
> **Update Time:** 2025-11-20 13:54:20

### Overview

SUNMI provides a simple app to show how to use the secondary screen, code scanning, printing, electronic scale, facial recognition payment, among other functions.

**Applicable to:** Desktop terminals (except SUNMI T1, SUNMI T1 MINI), non-payment handheld terminals, electronic scales, and SUNMI K1.

**APK Download:** V3.0.33

### Functions Introduction

#### 1. Dual Screen Function

The secondary screen of SUNMI dual screen device has two types: 15.6 inch large screen and 10.1 inch small screen.

- The 15.6 inch type supports touch control
- Touch control only functions when the secondary screen is displayed as Presentation

**Implementation:**
1. Create a class to inherit Presentation
2. Obtain a real secondary screen
3. Display secondary screen

#### 2. Printing Service

SUNMI printing service is developed using AIDL. There are differences between the printing services of K1 series and desktop terminals.

**Connection methods:**
- For kiosk series
- For desktop and handheld series
- In Gradle: Compatible with previous AIDL methods

#### 3. Payment (QR code based payment, Alipay facial recognition payment)

It's recommended to use the aggregation payment of SUNMI Checkout.

**Basic transaction modes:**
- Initialization
- Register a listening
- Enable facial recognition payment
- Enable QR code based payment
- Disable SUNMI Cashier
- Message receiving broadcast

**FAQ:**
- **Q:** Why can't I use facial recognition payment when using the APK compiled by source codes?
  **A:** After filling the parameters in Alipay Open Platform in the AlipaySmileModel file, you can directly use Alipay facial recognition payment.

- **Q:** How to use facial recognition payment for dual screen devices?
  **A:** You can directly use SUNMI Checkout. Add parameter `params.put("smile_mode", "1")` when calling `zolozVerify` method.

- **Q:** 1070 error code appears when calling facial recognition.
  **A:** Find Smile software in Settings - Apps, and enable the permission of allowing to run in other apps.

#### 4. Electronic Scales

For SUNMI S2 series electronic scales, use the encapsulated JAR package: `scale-service-lib.jar`.

#### 5. Handheld Scanner

A handheld scanner acts as an external keyboard. Events are obtained in `dispatchKeyEvent`. Each bit of the QR code and barcode will trigger this method once.

---

## 3. General Interface Description

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdixeghjk491
> **Update Time:** 2025-11-21 09:23:17

### Overview

The peripheral interfaces include: LAN port, RJ12 cash drawer port, RJ11 serial port, USB2.0 port, headphone jack and other general communication interfaces.

### RJ12 - Cash Drawer Port

The RJ12 port is usually used to connect a cash drawer. Developers can control the cash drawer by sending data to the cash drawer port.

### RJ11 - Serial Port

The serial port uses RJ11 port. The RJ11 of Sunmi equipment is four-wire and does not support hard-flow control.

> **Important:** For security reasons, serial port nodes traversal is not supported, but you can directly open the port to communicate.

**Supported Serial Port Nodes:**

| Product | RJ11 Path |
|---------|-----------|
| D3 PRO | `/dev/sunmi/pub/serial`, `/dev/ttyHS1` |
| T3 PRO/T3 PRO MAX | `/dev/sunmi/pub/serial`, `/dev/ttyHS2` |
| D3 MINI | `/dev/sunmi/pub/serial`, `/dev/ttyHS1` |
| D3/ D3 80MM | `/dev/sunmi/pub/serial`, `/dev/ttyS1` |
| T3/ T3 80MM | `/dev/sunmi/pub/serial`, `/dev/ttyHS1` |
| V3 MIX | `/dev/sunmi/pub/serial`, `/dev/ttyUSB0` |
| FELX 3-6490 | `/dev/sunmi/pub/serial`, `/dev/ttyHS2` |
| FELX 3-6225 | `/dev/sunmi/pub/serial`, `/dev/ttyHS1` |
| K2_PRO | `/dev/sunmi/pub/serial`, `/dev/ttyHS2` |
| K2_SUPER | `/dev/sunmi/pub/serial`, `/dev/ttyHS2` |
| D2 MINI | `/dev/ttyHSL1` |
| D2s LITE | `/dev/ttyS3` |
| D2s lite_d_2nd | `/dev/ttyS0` |
| D2s_KDS | `/dev/ttyS0` |
| D2s PLUS | `/dev/ttyS3` |
| D2_2nd | `/dev/ttyS0` |
| D1s single screen | `/dev/ttyS1` |
| D1s Dual Screen | `/dev/ttyS3` |
| D2 single screen | `/dev/ttyS1` |
| D2 Dual Screen | `/dev/ttyS3` |
| N1 single screen | `/dev/ttyS1` |
| N1 Dual Screen | `/dev/ttyS3`, `/dev/ttyHSL3` |
| S2cc Dual Screen | `/dev/ttyS3` |
| S2L CC | `/dev/ttyS0`, `/dev/ttyHSL1` |
| T1 MINI | `/dev/ttyHSL1`, `/dev/ttyHSL3` |
| T2s | `/dev/ttyS1` |
| T2 MINI | `/dev/ttyHSL3` |
| T2 LITE | `/dev/ttyHSL3` |
| T2s LITE | `/dev/ttyS1`, `/dev/ttyHSL3` |

### USB Port

Supports USB devices with USB2.0 agreement.

**Supported USB to Serial port chips:**
- CH341
- FT series
- PL2303
- CP210X series

When used USB to Serial port, system will build the node: `/dev/ttyUSERx` (like `/dev/ttyUSER0`, `/dev/ttyUSER1`, etc.)

> **Important:** Don't use USB to debug system when you use USB to Serial port, it would make USB to Serial port not work.

#### HID Device of Universal USB Peripherals

Supports HID agreement devices (mouse, keyboard, code scanning gun, etc.) - plug and play.

**Supported U disk formats:**
- FAT32: readable & writable
- NTFS: readable & non-writable
- exFAT: not supported

**Camera:** Sunmi supports USB UVC camera (e.g. LogitechC170).

---

## 4. Development Tips

### How to get the SN and Identifier of Device

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzdeghjk524
> **Update Time:** 2026-07-27 10:23:56

**Version Differentiation:**
- **Second-Generation Devices (Gen 2):** V2, V2S, L2, M2, etc. (Android 12 and below)
- **Third-Generation Devices (Gen 3):** T3 PRO, D3 PRO, V3/V3H, FLEX 3, M3, L3, etc. (Android 13/16)

#### Gen 2 Device SN & Device Identifier

**Obtaining the Device SN:**

Add permission in `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
```

**Obtaining SUNMI Device Identifiers:**

```java
// Code to obtain the brand:
String brand = SystemProperties.get("ro.product.brand");

// Code to obtain the model:
String model = SystemProperties.get("ro.product.model");

// Code to obtain the versionname:
String versionname = SystemProperties.get("ro.version.sunmi_versionname");

// Code to obtain the versioncode:
String versioncode = SystemProperties.get("ro.version.sunmi_versioncode");
```

#### Gen 3 Device SN & Device Identifier

Gen 3 devices mostly run Android 11 and above. Due to Google's privacy permission restrictions, regular apps cannot directly read `Build.SERIAL`.

**Recommended:** Add the `SunmiCustomerAPI` SDK as a dependency. No additional sensitive permissions are required.

For detailed API documentation, see: [SunmiCustomer API](./03-SUNMI-CUSTOMER-API.md)

### Other Development Tips

- How to achieve full-screen display of applications
- How to avoid repeatedly applying for peripheral permissions
- How to avoid application data from being cleared
- How to avoid app interface flickering when plugging in USB peripherals
- How to prevent scanned data from not matching the actual content
- How to customize volume keys

> **Note:** See the original documentation site for detailed code examples for each tip.

---

## Next Steps
- [SunmiCustomer API](./03-SUNMI-CUSTOMER-API.md)
- [Printer Development](./04-PRINTER-DEVELOPMENT.md)
