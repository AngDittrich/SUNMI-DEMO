# SUNMI Developer Documentation - Complete Reference

> **Source:** https://docs.sunmi.com
> **Extracted:** August 17, 2026
> **Base URL:** `https://docs.sunmi.com/en-US/{categoryID}/{pageID}`

---

## Documentation Structure

### Top-Level Categories

| Section | Category ID | URL |
|---------|-------------|-----|
| **Docs Home** | `ciczeghjk557` | [Link](https://docs.sunmi.com/en-US/ciczeghjk557/xdxmeghjk546) |
| **Integration Guide** | `cdixeghjk491` | [Link](https://docs.sunmi.com/en-US/cdixeghjk491/xmafeghjk535) |
| **Hardware Products** | `ceghjk502` | [Link](https://docs.sunmi.com/en-US/ceghjk502/fcmeghjk546) |
| **Software Handbooks & Bulletins** | `cicmeghjk546` | [Link](https://docs.sunmi.com/en-US/cicmeghjk546/xmdmeghjk546) |

---

## Complete Navigation Tree

### 1. Integration Guide

#### 1.1 Get Started
- Get Started (`xmafeghjk535`)

#### 1.2 Development Guide and Basics
- USB Debugging Management User Guide (`xdrzeghjk557`)
- Sunmi Cashier Demo (`xdiceghjk502`)
- General Interface Description (RJ12/RJ11/USB) (`xdixeghjk491`)
- **Development Tips:**
  - How to get the SN and Identifier of Device (`xdzdeghjk524`)
  - How to achieve full-screen display of applications
  - How to avoid repeatedly applying for peripheral permissions
  - How to avoid application data from being cleared
  - How to avoid app interface flickering when plugging in USB peripherals
  - How to prevent scanned data from not matching the actual content
  - How to customize volume keys
- FAQ

#### 1.3 SunmiCustomer API
- Overview of SDK (`xdcxeghjk491`)
- Device Information Module (`xdqieghjk579`)
- Device Management Module (`xddaeghjk480`)
- Software Management Module (`xddxeghjk491`)
- System Management Module (`xddceghjk502`)
- System UI Management Module (`xddqeghjk513`)
- Device Runtime Information Module (`xdddeghjk524`)
- Network Management Module (`xddfeghjk535`)
- Device Software Information Module (`xddmeghjk546`)
- Kiosk Management Module (`xmrqeghjk513`)
- Certificate Management Module (`xmrdeghjk524`)
- Log Management Module (`xziceghjk502`)

#### 1.4 Payment SDK Development
- Payment SDK for SUNMI's P series devices

#### 1.5 Artificial Intelligence (AI) SDK

##### Face Recognition
- Functions Introduction
- Face Recognition V2.0 SDK Interface Documentation
- NIR Liveness Detection
- Face Recognition Error Codes

##### OCR
- OCR V3.8 SDK Interface Documentation
- OCR Error Code

##### Product Recognition
- Fruit and Vegetable Recognition V2.3 SDK Interface Documentation
- Food Recognition V2.0 SDK Interface Documentation

##### Barcode Reader
- Barcode Reader Professional SDK 2.0

#### 1.6 SUNMI OpenAPI
- Capability Integration Guide (`xcmmeghjk546`)
- Signature & Verification
- Generating App Public Key
- Common Error Codes

##### Remote Management
- Introduction
- **Device APIs:** Request for Remote Control, Device Online/Offline Status, Get Device Coordinates And Geographic Location, Get Device Real-time Status, Get Device Real-time Status [Batch], Get the App List of the Device in Real Time, Get Device Latest Status [Batch], Get Device Real-Time Switch Status, Get Device Real-Time traffic statistics
- **Command APIs:** Issue Power Off Command, Issue Reboot Command, Issue Lock Device Command, Issue Unlock Device Command, Issue Clear Lock Screen Password Command, Send Notifications
- **Device Group APIs:** Get device information, Get Group info by SN list, Bulk Update Device Remarks, Get Group list, Move devices to group, Rename Group, Delete group, Create Group
- **Device Transfer APIs:** Transfer to friend, Get Friend list, Bound to sub account, Get Sub Account list, Unbound from sub account, Add Friend, Device Force Reclaim

##### OS Configuration
- OS Configuration Introduction (`xzxdeghjk524`)
- **Development Guide:** Access Rules, Signing Documentation, Error Codes
- **Interface List:**
  - **APN:** Create/Update/Get APN strategy
  - **Scheduled Power on/off:** Create/Update/Get scheduled Power on/off strategy
  - **APP Permission:** Create/Update/Get APP permission strategy
  - **Setting items Manage:** Create/Update/Get setting items strategy

##### App Store
- **App Publishing API:**
  - App store OpenAPI Integration Guide
  - App store API (`faceghjk502`)
  - **App Management:** Create/Delete App in AppStore, Upgrade App version, Get App Detail, Get App review, Update App detail, Update App version detail, Get App latest version, Audit Result Callback
  - **App Assets:** Upload APK, Upload Image
  - **App Metadata:** Get App categories, Get country language codes, Get device models
- App Publishing Process
- APK Uploading FAQs
- App Test Specifications
- Jump to the app store app details
- App Store Public and Private Pools Function Manual
- Scheduled App Updates
- App Cleanup FAQ

##### SUNMI Remote Key Injection
- Remote Key Injection Introduction (`xqdqeghjk513`)
- Appendix on Remote Key Injection
- **Key APIs:** Upload key, Assign key, Reassign key, Delete key, Get protection key, Save initial key, Generate Random Key, Get Key Info
- **Device APIs:** Gets the device injection status, Device Unbind, Device Unlock, Push ThirdParty Key Download, Get ThirdParty Key Injection Result
- **Task APIs:** Get Create Task Device, Create Task

##### Parameter Management
- Parameter Management Introduction (`xzfaeghjk480`)
- **Development Guide:** Access Rules, Signing Documentation, Error Codes, Parameter JSON Structure Description, Parameter Management API Documentation
- **API Interfaces:**
  - **Parameter Template:** Summary List, Application Version Parameter Template List, Parameter Template Details/Clone/Delete/Update, Create Normal/Official Template, Parameter Template Validate
  - **Device Parameter:** Device Application Parameter List/Details/Delete, Save/Stop/Get Device Application Parameter Push, Device Application Parameter Push List/Details
- Android SDK Integration

##### OTA
- OTA Introduction (`xziqeghjk513`)
- Callback for OTA Upgrade Task Approval Notifications

#### 1.7 Printer Development

##### Built-in Printer Service
- Introduction to SUNMI Printing Services (`xdzaeghjk480`)
- SDK Upgrade Description (`xdzxeghjk491`)
- **SDK Reference (New):**
  - SUNMI Printing SDK Overview (`xdzceghjk502`)
  - SDK Release Notes
  - APIs for Printing Thermal Receipts (`xdzfeghjk535`)
  - APIs for Printing Labels & Receipts (`xdzmeghjk546`)
  - APIs for Printing Files (`xdzzeghjk557`)
  - APIs for Querying Printer (`xdzreghjk568`)
  - APIs for Printing Instruction Sets (`xdzieghjk579`)
  - APIs for Controlling Cash Drawers
  - APIs for Controlling LCD Customer Display
- **SDK Reference (Old):** Inbuilt Non-detachable Printers Documentation, SDK Versions of Devices With Built-in Printers, Self-service printed documentation for SunmiK1/K2
- **JavaScript Printer:** JavaScript SDK Overview, JavaScript API for Thermal Receipt Printing, JavaScript API for Label Printing, JavaScript Command APIs for Printing, JavaScript APIs for Querying Printer
- **Flutter Printer:** Flutter Printing SDK Reference, Thermal Receipt Printing API, Label and Receipt Printing API, Command Set Printing API, Printer Query API, Cash Drawer Control API
- **Uniapp Printer:** UniApp SDK Overview, UniApp Thermal Receipt/Label/Command Set Printing API, Uniapp Cash Drawer Control API
- **Cordova Printer:** Cordova SDK Overview, Cordova Print Thermal Receipt/Label Receipt/Command Set Printing API, Cordova Cash Drawer Control Interface

##### Cloud Printer V2
- 25 sub-sections covering API integration, drivers for Windows/macOS/OPOS, WeChat Mini Program SDK, iOS SDK, Android SDK, macOS SDK, LAN HTTP, FAQs

##### Cloud Printer V1 (SDK_V1 only)
- 7 sub-sections

##### 58mm Label Recipe Printer
- Windows drivers

#### 1.8 Scanning Development
- Using Camera-Based Barcode Scanner SDK (Android, Flutter, uni-app, Cordova)
- Code Scanner Engine (Infrared scan code)
- Code scanning base
- CodeID
- Barcode Scanner User Guide

#### 1.9 Customer Display Development
- Secondary Display API Documentation
- T2 Mini Customer display
- T1 Vice Screen (T1 devices only): Debugging instructions, T1 built-in/custom Vice Screen Display App, T1 Dual Screen Communication Interface
- ClientView Open Source Display Solution

#### 1.10 Electronic Scale Development
- Electronic Scale

#### 1.11 Card Reader Development
- NFC Related SDK Guide
- Magnetic Stripe Reader Service Guide
- PSAM, ETC, M112 card reader development
- **UHF RFID:** RFID SDK Integration Guide, RFID Uniapp Plugin User Guide

#### 1.12 Cash Drawer Development
- How to operate cash drawer on Sunmi device
- **Cash Drawer Driver Trigger** (8 sub-sections for Windows/Android/iOS/macOS, Testing Tool, Square, Trigger APP)

#### 1.13 Status Light Development
- Status Light Service Guide
- CPad Built-in LED Indicator Management

#### 1.14 Fingerprints Development
- Fingerprints Documentation

#### 1.15 Android Device Connection Development
- Sunmi Devices Connection SDK Guide

#### 1.16 Payment Sound Box Development
- Integrating the Soundbox with Your Device
- Enable sound box integration capability
- Integrating with the Soundbox API
- Soundbox API Callbacks
- Remote Management of the Sound Box
- Sound Box Demo for Android
- NFC card issuing tool use guide

#### 1.17 Biometric (Fingerprint) Development Guide
- Biometric (Fingerprint) Development Guide (`xzcxeghjk491`)

---

### 2. Hardware Products
- Device Models (`fcmeghjk546`)
- CPad (`xqimeghjk546`)
- CPad PAY (`xmzqeghjk513`)
- D3 & D3 80MM (`xddzeghjk557`)
- D3 PRO (`xadreghjk568`)

### 3. Software Handbooks & Bulletins
- Introduction (`xmdmeghjk546`)
- Sunmi Partner Platform (`xqfreghjk568`)
- SUNMI Local Cloud Solution Statement (`xqfieghjk579`)
- User Account (`xcrqeghjk513`)
- Entity Account (`xcrdeghjk524`)

### 4. Driver & Integration
- Debugging Devices (`ciieghjk579`)

---

## External Links

| Resource | URL |
|----------|-----|
| SUNMI Partner Platform (Global) | https://partner.sunmi.com |
| SUNMI Partner Platform (US) | https://partner.us.sunmi.com |
| SUNMI Partner Platform (EU) | https://partner.eu.sunmi.com |
| SUNMI Developer Documentation | https://docs.sunmi.com |
| SUNMI Developer Platform | https://sunmideveloper.com |
| Technical Support Line | 400-6666-509 (Business Days, 9:00-18:00 UTC+8) |

---

## Documentation Files Index

| File | Content |
|------|---------|
| [01-GET-STARTED.md](./01-GET-STARTED.md) | Account registration, environment setup, first app |
| [02-DEVELOPMENT-GUIDE.md](./02-DEVELOPMENT-GUIDE.md) | USB debugging, interfaces, development tips |
| [03-SUNMI-CUSTOMER-API.md](./03-SUNMI-CUSTOMER-API.md) | All 12 SDK modules (Device, Software, System, Network, etc.) |
| [04-PRINTER-DEVELOPMENT.md](./04-PRINTER-DEVELOPMENT.md) | Built-in printer SDK, LineApi, CanvasApi, FileApi, QueryApi, CommandApi |
| [05-OPENAPI.md](./05-OPENAPI.md) | Capability Integration, Remote Management, OS Config, App Store, OTA |
| [06-AI-SDK.md](./06-AI-SDK.md) | Face Recognition, OCR, Product Recognition, Barcode Reader |
| [07-SCANNING.md](./07-SCANNING.md) | Camera scanner, Code Scanner Engine, Barcode reader |
| [08-CARD-READER.md](./08-CARD-READER.md) | NFC, Magnetic Stripe, PSAM, UHF RFID |
| [09-CASH-DRAWER.md](./09-CASH-DRAWER.md) | Cash drawer operation and drivers |
| [10-CUSTOMER-DISPLAY.md](./10-CUSTOMER-DISPLAY.md) | Secondary display, T2 Mini, T1 Vice Screen |
| [11-ELECTRONIC-SCALE.md](./11-ELECTRONIC-SCALE.md) | Electronic scale integration |
| [12-PAYMENT-SOUND-BOX.md](./12-PAYMENT-SOUND-BOX.md) | Sound box integration |
| [13-BIOMETRIC.md](./13-BIOMETRIC.md) | BiometricPrompt, fingerprint authentication |
