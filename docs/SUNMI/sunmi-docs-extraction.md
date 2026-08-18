# SUNMI Developer Documentation Extraction

---

## 1. USB Debugging Management User Guide

- **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdrzeghjk557
- **Update Time:** 2026-02-27 15:04:39
- **Breadcrumb:** Documentation > Integration Guide > Development Guide and Basics > USB Debugging Management User Guide

### Content

#### Debugging Management Instructions

By default, Sunmi devices can be debugged by simply connecting them via USB and enabling debug mode through the Settings menu.

**For example:** [Method to Enable Debug Mode on V3 MIX Model](https://developer.sunmi.com/docs/read/en-US/xmzieghjk579)

SUNMI offers device debugging permission control. Once a partner enables this feature in the backend (see image below), debugging requires permission obtained via email or phone number. If direct debugging is not possible, please verify if this permission is enabled by the partner in the backend.

> **Note:** Debugging permission control only takes effect for devices that are bound to a partner account.

#### Edit Entry

- **Accounts without the "Configuration File" feature enabled:**
  Device Management > Common > Debuggers

- **Accounts with the "Configuration File" feature enabled:**
  Device Management > Profiles > Security Settings > USB Debugging Protection

#### How to Debug a Device After Enabling Debugging Control

If the partner has enabled "debugging permission control", the developer need to obtain the debugging permission via E-mail and mobile phone number on his/her own device to debug the device. The partner can add the mobile phone number or E-mail of the debugging personnel (developer) on the Sunmi partner platform. Steps to obtain the device debugging permission as follows:

1. **Add debugging personnel**
   Before debugging the device, the developer is required to know the channel that the device belongs to. He/she can look for the management personnel relevant to his/her own company for inquiry. The management personnel needs to add the debugging personnel's mobile phone number or E-mail in Sunmi partner platform background.

2. **Plug in USB**
   (Please switch off the 'debugging permission control' temporarily on V1s/V2/M2/L2)
   Connect the device to the computer after confirming that you have your own mobile phone number or E-mail for debugging permission. It is suggested that the developer debug under windows. If the mobile phone can be correctly recognized by the computer, the popup prompt as follows will normally appear.

   If the device has not been recognized by the computer, please confirm whether it is caused by the following causes:
   - Poor contact. Please confirm by plugging & twisting USB port several times.
   - Data line fault. Try to change one data line to see whether it can be recognized.
   - If the computer has not installed mobile device driver, you may use third party tool software to install.

3. **Get the verification code**
   Click the above "I want to debug" item, you will enter the step of permission verification via mobile phone number or E-mail. Meanwhile, the USB debugging mode of the device will be enabled by customization (here it refers to the basic debugging mode, but not permission); click "Got it" and it will quit the popup and USB debugging mode will not be enabled.

4. **Verify the permission**
   Click "Obtain the verification code" after entering the previously added mobile phone number or E-mail. Sunmi will send the verification code to the mobile phone number or E-mail. Fill in the verification code and click "Authorize and enable debugging"

5. **Open the permission**
   After opening the permission, you can check whether there is output in logcat to judge if it's OK to debug the device.

### Images
- https://cdn.sunmi.com/public/image/mgt-document/2406eb4a86d1476e965912cc984a15b7.png
- https://cdn.sunmi.com/public/image/mgt-document/f6d6a36e1c224f21adcba40f0fe8ab94.png
- https://cdn.sunmi.com/public/image/mgt-document/aab335f9082f4b87869ee830ae5df63e.png
- https://cdn.sunmi.com/public/image/mgt-document/f38d4c78895d40de8baef2ab0c49c0e0.png
- https://file.cdn.sunmi.com/SUNMIDOCS/5763098076687185.png
- https://file.cdn.sunmi.com/SUNMIDOCS/9207727942700776.png
- https://file.cdn.sunmi.com/SUNMIDOCS/5242169004101502.png
- https://file.cdn.sunmi.com/SUNMIDOCS/3074718981183502.png

---

## 2. Sunmi Cashier Demo

- **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdiceghjk502
- **Update Time:** 2025-11-20 13:54:20
- **Breadcrumb:** Documentation > Integration Guide > Development Guide and Basics > Sunmi Cashier Demo

### Content

SUNMI provides a simple app to show how to use the secondary screen, code scanning, printing, electronic scale, facial recognition payment, among other functions, which is applicable to desktop terminals (except for SUNMI T1, SUNMI T1 MINI), non-payment handheld terminals, electronic scales, and SUNMI K1.

**APK download:** V3.0.33

SunmiHotel demo, including ID card reading, card dispensing and indicator control.
**SunmiHotel:** 1.0.6

For version 2.0.0, payment switch and automatic installation of Alipay facial recognition payment app are added, a merged version.

Due to the use of GreenDao, it is necessary to build it to create Daomaster and Daosession when downloading it for use.

### Introduction to SUNMI Cashier Demo

For SUNMI T1 device, please refer to the contents in T1 directory in Developers Documentation. Here we'll skip it.

The reference code is under the `s` and `main` directories.

If you need to use Alipay facial recognition payment, please contact SUNMI customer service for realizing facial recognition payment function of SUNMI Cashier.

### Functions Introduction

#### 1. Dual screen function

The secondary screen of SUNMI dual screen device has two types: 15.6 inch large screen and 10.1 inch small screen, of which the secondary screen of the 15.6 inch type supports touch control. The touch control only functions when the secondary screen is displayed as Presentation.

The presentation class is used if you need to display different contents on two screens.

- Firstly, we need permissions
- Create a class to inherit Presentation
- Obtain a real secondary screen
- Display secondary screen

Now the secondary screen display is realized.

#### 2. Printing service

We developed SUNMI printing service using AIDL. There are some differences between the printing services of K1 series and desktop terminals. Connect the service using the methods below:

- For kiosk series
- For desktop and handheld series
- In Gradle: Compatible with previous AIDL methods

#### 3. Payment (QR code based payment, Alipay facial recognition payment)

It's recommended to use the aggregation payment of SUNMI Checkout.

Only basic transaction modes are available in the demo:
- Initialization
- Register a listening
- Enable facial recognition payment
- Enable QR code based payment
- Disable SUNMI Cashier
- Message receiving broadcast

**QA about Alipay facial recognition payment:**

**Q:** Why can't I use facial recognition payment when using the APK compiled by source codes?
**A:** After filling the parameters that you applied for in Alipay Open Platform in the AlipaySmileModel file in source codes, you can directly use Alipay facial recognition payment only after making some data adjustment according to Alipay's regulations.

**Q:** How to use Alipay facial recognition payment?
**A:** You can use SUNMI Checkout for calling facial recognition function, or directly apply for this function in Alipay Open Platform, and apply for calling the permission of Alipay facial recognition service in SUNMI Developer Platform.

**Q:** How to use facial recognition payment for dual screen devices?
**A:** You can directly use SUNMI Checkout. SUNMI Checkout supports the latest version of Alipay facial recognition function synchronically. Or a third-party software can be used to control whether to display on the secondary screen. Add parameter `params.put("smile_mode", "1")` when you need to call `zolozVerify` method.

**Q:** 1070 error code appears when calling facial recognition.
**A:** Find Smile software in Settings - Apps, and enable the permission of allowing to run in other apps.

#### 4. Electronic scales

For SUNMI S2 series electronic scales, you can directly use the encapsulated JAR package: `scale-service-lib.jar`.

#### 5. Handheld scanner

A handheld scanner can be seemed as an external keyboard, and the events are obtained in `dispatchKeyEvent`. Each bit of the QR code and barcode will trigger this method once. Some scanners will actively call the Enter key one more time after the input is completed.

A simple code scanning listening method is provided in demo.

### Downloads
- https://file.cdn.sunmi.com/SUNMIDOCS/SunmiHotel_1.0.6.zip

---

## 3. General Interface Description (RJ12/RJ11/USB)

- **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdixeghjk491
- **Update Time:** 2025-11-21 09:23:17
- **Breadcrumb:** Documentation > Integration Guide > Development Guide and Basics > General Interface Description (RJ12/RJ11/USB)

### Content

#### 1. Brief introduction

The peripheral interfaces mainly include: LAN port, RJ12 cash drawer port, RJ11 serial port, USB2.0 port, headphone jack and other general communication interfaces.

This article mainly introduces the use of RJ12 cash drawer port, RJ11 serial port and USB port.

#### 2. Interface description

##### 2.1 RJ12 - Cash drawer port

The RJ12 port is usually used to connect a cash drawer. The developers can control the cash drawer by sending data to the cash drawer port. Cash drawer developers document and resources file:

[Interface Documentation](https://developer.sunmi.com/docs/zh-CN/cdixeghjk491/xdiaeghjk480)

##### 2.2 RJ11 - Serial port

The serial port uses RJ11 port. The developers can control the peripherals by sending data from the serial port. The RJ11 of sunmi equipment is four-wire and does not support hard-flow control (some peripherals turn on hardware-flow control by default, and these devices can not connect sunmi mainframe through RJ11).

For security reasons, serial port nodes traversal is not supported, but you can directly open the port to communicate.

The currently supported serial port nodes are as follows:

**Desktop 1st/2nd Generation Products RJ11 Serial Port Summary:**

| Product | RJ11 Path (BSP) |
|---------|-----------------|
| D3 PRO | /dev/sunmi/pub/serial, /dev/ttyHS1 |
| T3 PRO/T3 PRO MAX | /dev/sunmi/pub/serial, /dev/ttyHS2 |
| D3 MINI | /dev/sunmi/pub/serial, /dev/ttyHS1 |
| D3/ D3 80MM | /dev/sunmi/pub/serial, /dev/ttyS1 |
| T3/ T3 80MM | /dev/sunmi/pub/serial, /dev/ttyHS1 |
| V3 MIX | /dev/sunmi/pub/serial, /dev/ttyUSB0 |
| FELX 3-6490 | /dev/sunmi/pub/serial, /dev/ttyHS2 |
| FELX 3-6225 | /dev/sunmi/pub/serial, /dev/ttyHS1 |
| K2_PRO | /dev/sunmi/pub/serial, /dev/ttyHS2 |
| K2_SUPER | /dev/sunmi/pub/serial, /dev/ttyHS2 |
| D2 MINI | /dev/ttyHSL1 |
| D2s LITE | /dev/ttyS3 |
| D2s lite_d_2nd | /dev/ttyS0 |
| D2s_KDS | /dev/ttyS0 |
| D2s PLUS | /dev/ttyS3 |
| D2_2nd | /dev/ttyS0 |
| D1s single screen | /dev/ttyS1 |
| D1s Dual Screen | /dev/ttyS3 |
| D2 single screen | /dev/ttyS1 |
| D2 Dual Screen | /dev/ttyS3 |
| N1 single screen | /dev/ttyS1 |
| N1 Dual Screen | /dev/ttyS3, /dev/ttyHSL3 |
| S2cc Dual Screen | /dev/ttyS3 |
| S2L CC | /dev/ttyS0, /dev/ttyHSL1 |
| T1 MINI | /dev/ttyHSL1, /dev/ttyHSL3 |
| T2s | /dev/ttyS1 |
| T2 MINI | /dev/ttyHSL3 |
| T2 LITE | /dev/ttyHSL3 |
| T2s LITE | /dev/ttyS1, /dev/ttyHSL3 |

**Serial port reference demo:** JNI reference

Communication document example: communication document for electronic scale at Jaynes serial port (different electronic scale communication agreements need to refer to the documents provided by every manufacturer)

##### 2.3 USB Port

Supports USB devices with USB2.0 agreement.

[USB communication developer documentation](https://ota.cdn.sunmi.com/DOC/resource/re_cn/usbDeveloper/project1.rar)

**Sunmi support list of USB to Serial port:**
- CH341
- FT series
- PL2303
- CP210X series

When used USB to Serial port, system will build the node: `/dev/ttyUSERx` (like `/dev/ttyUSER0`, `/dev/ttyUSER1`, etc.)

> **Important:** Don't use USB to debug system when you use USB to Serial port, it would make USB to Serial port not work.

###### 2.3.1 Obtain USB devices' PID/VID:

**Method 1: java code**
- Old method java code (obsolete, it is no longer possible to obtain PID/VID through this method)

**Method 2: adb command**

###### 2.3.2 HID device of universal USB peripherals

Supports the HID agreement devices (mouse, keyboard, code scanning gun, etc.), and you can plug and play.

**HID code scanning gun:**
As for HID code scanning gun, you can plug and play: connect Sunmi device, open an editable box on the Sunmi device to obtain the focal point and scan the code. Then bar code or QR code content should be inputted into this box.

As for obtaining code scanning content from the code, please refer to: [Communications Google Developer documentation](https://developer.android.google.cn/guide/topics/connectivity/usb/host.html)

**U disk:**
The supported U disk format:
- FAT32: readable & writable
- NTFS: readable & non-writable
- exFAT: not supported

**Camera:**
Sunmi supports USB UVC camera (e.g. LogitechC170).

Demo: [Source code](https://sunmi-ota.oss-cn-hangzhou.aliyuncs.com/DOC/resource/re_cn/T2demo/Camera.zip)

###### 2.3.3 Sunmi card reader

Sunmi card reader can be connected at the sidebar of Sunmi reader (Slot reader interface)

[Development package Demo source](https://ota.cdn.sunmi.com/DOC/resource/re_cn/usbDeveloper/Sunmicardreader.zip)

Sunmi card reader includes two parts:
1. **Magnetic stripe card** - which can be referenced to Sunmi card reader SDK and card reader demo
2. **NFC** - which is android native port and can be referenced to android nfc development document

[Sunmi card reader SDK](https://ota.cdn.sunmi.com/DOC/resource/re_cn/usbDeveloper/sunmicardtest.apk)
[Sunmi card reader demo](https://ota.cdn.sunmi.com/DOC/resource/re_cn/usbDeveloper/SunmiCardTest.rar)

**The third party universal reader & writer:**
Sunmi has now already supported four types of third party usb universal reader & writer:
1. Shanghai Yixi Intelligent Technology Co., Ltd - UM002 card reader
2. Shenzhen Deka Technology Co., Ltd - T10 reader & writer (needs to support android version, power charging version)
3. Hong Kong Longjie Smart Card Co., Ltd - ACR1281U-C1 reader & writer
4. Hong Kong Longjie Smart Card Co., Ltd - ACR1281U-K1 reader & writer

[Development document 2.0](https://ota.cdn.sunmi.com/DOC/resource/re_cn/%E4%B8%8A%E6%B5%B7%E5%95%86%E7%B1%B3%E7%A7%91%E6%8A%80%E5%A4%96%E6%8E%A5%E8%AF%BB%E5%86%99%E5%99%A8SDK%E4%BD%BF%E7%94%A8%E6%89%8B%E5%86%8C2.0.0.docx)
[Universal reader & writer demo](https://sunmi-ota.oss-cn-hangzhou.aliyuncs.com/DOC/resource/re_cn/SunmiReaderDemo-v3.0.1.apk)
[Universal reader & writer demo source code](https://sunmi-ota.oss-cn-hangzhou.aliyuncs.com/DOC/resource/re_cn/SunmiReaderDemo2.rar)

### Images
- https://file.cdn.sunmi.com/SUNMIDOCS/4985279011300119.jpg
- https://cdn.sunmi.com/public/image/mgt-document/edb21ca3af0b457c8ac387e70e98606e.png
- https://cdn.sunmi.com/public/image/mgt-document/a89a5ab9e9014c92b810ba41d6463ce0.png

---

## 4. How to get the SN and Identifier of Device

- **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzdeghjk524
- **Update Time:** 2026-07-27 10:23:56
- **Breadcrumb:** Documentation > Integration Guide > Development Guide and Basics > Development Tips > How to get the SN and Identifier of Device

### Content

#### 1. Document Description

This document is intended for developers and describes how to obtain device SN, device identifiers, and other critical information on SUNMI second-generation and third-generation devices. The document is organized by device generation—please select the integration method that corresponds to your target device.

**Version Differentiation Boundaries:**

- **Second-Generation Devices (Gen 2):** SUNMI's second-generation smart hardware (older models running Android 12 and below: V2, V2S, L2, M2, etc.)
- **Third-Generation Devices (Gen 3):** SUNMI's third-generation smart hardware (newer models running Android 13/16: T3 PRO, D3 PRO, V3/V3H, FLEX 3, M3, L3, etc.)

#### 2. Gen 2 Device SN & Device Identifier Acquisition Method

##### 2.1 Obtaining the Device SN

Add the following permission in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
```

Use the following code where needed to obtain the SUNMI SN.

##### 2.2 Obtaining SUNMI Device Identifiers

SUNMI recommends determining whether a device is a SUNMI device by obtaining the following information:

- **Device Brand Name (`brand`)** — e.g., `SUNMI`
  The brand name for all SUNMI devices is uniformly `SUNMI`

- **Device System Model (`model`)** — e.g., `V1-B18`
  The system model consists of: Product Model + Hardware Feature + '-' + Software Feature
  Models starting with `V`, or `M`, or `P`, or `L` are handheld devices; those starting with `T`, or `D`, or `S` are landscape-screen devices (as of December 2017).

- **Device ROM Version Name** — e.g., `1.1.0`

- **Device ROM Version Code** — e.g., `128`

You can download the Demo and follow its structure by creating an `android.os` package (this is a fixed naming convention) under your project's `src` directory, and placing the `SystemProperties.java` file into that package. Then, use the following methods to obtain the specified values:

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

#### 3. Gen 3 Device SN & Device Identifier Acquisition Method

Gen 3 devices mostly run Android 11 and above. Due to Google's privacy permission restrictions, regular apps cannot directly read `Build.SERIAL`. The SN must be obtained through the system built-in SDK interface provided by SUNMI.

##### 3.1 Standard Integration Solution (Recommended)

Add the `SunmiCustomerAPI` SDK as a dependency to your project.

No additional sensitive permissions are required; the system whitelist interface can be called directly.

For detailed API documentation, please refer to:
https://docs.sunmi.com/zh-CN/cdixeghjk491/xdqieghjk579

##### 3.2 Sample Code

*(Refer to the SunmiCustomerAPI documentation for complete sample code)*

---

## 5. Biometric (Fingerprint) Development Guide

- **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xzcxeghjk491
- **Update Time:** 2026-04-03 22:37:01
- **Breadcrumb:** Documentation > Integration Guide > Biometric (Fingerprint) Development Guide

### Content

#### Core Background (Must Read)

- **Deprecated Old API:** `FingerprintManager` (API 23-28) is marked deprecated by Google. It is disabled on Sunmi's new ROM/devices, and direct calls will result in errors.
- **New API Requirement:** Mandatory migration to `BiometricPrompt` + `BiometricManager` (AndroidX library). Supports fingerprint/facial recognition with system-level security verification, natively adapted for all Sunmi POS series.

**Official Documentation:**
- [Android Official Biometric Overview](https://developer.android.google.cn/reference/android/hardware/biometrics/package-summary)
- [AndroidX Biometric Library Documentation](https://developer.android.google.cn/jetpack/androidx/releases/biometric)
- [BiometricPrompt Development Guide](https://developer.android.google.cn/training/sign-in/biometric-auth)

#### Development Configuration

- **Dependency Configuration** (Module-level `build.gradle`)
- **Permission Configuration** (`AndroidManifest.xml`)
- **Dynamic Permission Request** (Required for Android 10+)

#### Core Integration Code

Complete Java Implementation (Activity/Fragment)

#### Adaptation Notes

**System Version Adaptation:**

- **Android 6.0-8.1:** Automatically compatible with legacy fingerprint underlying layers, no extra modifications needed.
- **Android 9.0+:** Mandatory use of BiometricPrompt; ensure `targetSdkVersion` >= 28.
- **New Sunmi POS (Android 11+):** `FingerprintManager` is completely disabled; legacy code must be fully removed.

#### Key Error Handling

| Error Code | Handling Suggestion |
|------------|---------------------|
| `ERROR_LOCKOUT` | Prompt user to retry after 30 seconds |
| `ERROR_LOCKOUT_PERMANENT` | Guide user to unlock with device password |
| `ERROR_HW_UNAVAILABLE` | Advise user to restart device or contact after-sales |

#### Technical Support

- [Sunmi Developer Platform](https://sunmideveloper.com)
- Technical Support Feedback: Contact Sunmi Technical Support Team

---

*Extracted on 2026-08-17 from SUNMI Developer Documentation (docs.sunmi.com)*
