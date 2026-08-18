# SUNMI OpenAPI

> **Section:** Integration Guide > SUNMI OpenAPI
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/xcmmeghjk546

---

## Table of Contents

1. [Capability Integration Guide](#1-capability-integration-guide)
2. [Signature & Verification](#2-signature--verification)
3. [Generating App Public Key](#3-generating-app-public-key)
4. [Common Error Codes](#4-common-error-codes)
5. [Remote Management](#5-remote-management)
6. [OS Configuration](#6-os-configuration)
7. [App Store](#7-app-store)
8. [SUNMI Remote Key Injection](#8-sunmi-remote-key-injection)
9. [Parameter Management](#9-parameter-management)
10. [OTA](#10-ota)

---

## 1. Capability Integration Guide

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xcmmeghjk546
> **Update Time:** 2026-08-03 16:08:16

SUNMI provides dozens of capabilities such as VAS capabilities and OS capabilities. You can integrate those capabilities into the cloud apps or Android apps you built through different methods.

### List of Capabilities

1. Click [My Capabilities] to view all the capabilities that can be integrated.
2. Click [Details] to view the basic info of the capabilities.
3. Click [Integration] to view the apps integrated.

### Create Apps and Integration Capabilities

1. Log into [SUNMI Partners] and become a [Developer], click [Development]-[Apps Integration].
2. Click [Apps Integration], and [Create Apps] will pop up.
   - If the integration method of the capabilities is Cloud-to-Cloud, you need to create Cloud apps; if the integration method of the capabilities is End-to-End, you need to create Android apps.
   - If the integration methods of the capabilities are all accessible, you can create Cloud apps or Android apps as needed.
3. After Apps are successfully created, click Apps Integration and enter [App Details] to link capabilities as needed.
4. Select [Add Capabilities] to link the apps and capabilities.
5. After the apps are successfully linked to the capabilities, the interface corresponding to the capabilities can be called.

---

## 2. Signature & Verification

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/

### Overview

SUNMI OpenAPI uses HMAC-SHA256 signature for request verification. All API requests must include a valid signature.

### Signature Algorithm

```
signature = HMAC-SHA256(app_secret, string_to_sign)
```

**String to sign format:**
```
HTTP_METHOD\n
CONTENT_TYPE\n
TIMESTAMP\n
REQUEST_PATH\n
QUERY_STRING\n
BODY_HASH
```

### Headers

| Header | Description |
|--------|-------------|
| `X-App-Id` | Application ID |
| `X-Timestamp` | Unix timestamp (seconds) |
| `X-Signature` | HMAC-SHA256 signature |

---

## 3. Generating App Public Key

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/

### Overview

To integrate with SUNMI OpenAPI, you need to generate an RSA key pair and upload the public key to the SUNMI partner platform.

### Key Generation

```java
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
keyPairGenerator.initialize(2048);
KeyPair keyPair = keyPairGenerator.generateKeyPair();

// Get public key
String publicKey = Base64.encodeToString(
    keyPair.getPublic().getEncoded(), Base64.DEFAULT
);

// Get private key
String privateKey = Base64.encodeToString(
    keyPair.getPrivate().getEncoded(), Base64.DEFAULT
);
```

---

## 4. Common Error Codes

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Internal Server Error |

### Business Error Codes

| Code | Description |
|------|-------------|
| 10001 | Invalid signature |
| 10002 | Expired timestamp |
| 10003 | App not found |
| 10004 | Device not found |
| 10005 | Invalid parameter |
| 10006 | Operation failed |
| 10007 | Rate limit exceeded |
| 10008 | Permission denied |

---

## 5. Remote Management

> **URL:** https://docs.sunmi.com/en-US/caiaiieghjk579/xczxeghjk491

### Introduction

The Remote Management module provides APIs for managing SUNMI devices remotely through the cloud platform.

### Device APIs

| API | Description |
|-----|-------------|
| Request for Remote Control | Initiate remote control session |
| Device Online/Offline Status | Get device connection status |
| Get Device Coordinates And Geographic Location | Get device GPS location |
| Get Device Real-time Status | Get current device status |
| Get Device Real-time Status [Batch] | Get status of multiple devices |
| Get the App List of the Device in Real Time | Get installed apps |
| Get Device Latest Status [Batch] | Get latest status of multiple devices |
| Get Device Real-Time Switch Status | Get switch states (WiFi, BT, etc.) |
| Get Device Real-Time traffic statistics | Get network usage data |

### Command APIs

| API | Description |
|-----|-------------|
| Issue Power Off Command | Remote shutdown |
| Issue Reboot Command | Remote reboot |
| Issue Lock Device Command | Remote lock device |
| Issue Unlock Device Command | Remote unlock device |
| Issue Clear Lock Screen Password Command | Clear lock screen password |
| Send Notifications | Push notification to device |

### Device Group APIs

| API | Description |
|-----|-------------|
| Get device information | Get device details |
| Get Group info by SN list | Get group info by serial numbers |
| Bulk Update Device Remarks | Update device descriptions |
| Get Group list | List all groups |
| Move devices to group | Move devices between groups |
| Rename Group | Rename a group |
| Delete group | Delete a group |
| Create Group | Create a new group |

### Device Transfer APIs

| API | Description |
|-----|-------------|
| Transfer to friend | Transfer device to another account |
| Get Friend list | List friend accounts |
| Bound to sub account | Bind device to sub account |
| Get Sub Account list | List sub accounts |
| Unbound from sub account | Unbind device from sub account |
| Add Friend | Add friend account |
| Device Force Reclaim | Force reclaim device |

---

## 6. OS Configuration

> **URL:** https://docs.sunmi.com/en-US/caixqfeghjk535/xzxdeghjk524

### Introduction

The **OS Configuration Module** of SUNMI DMP is a flexible and efficient configuration tool designed to help users achieve refined device management with greater convenience.

Through this module, users can flexibly set a series of personalized rules and usage restrictions for managed devices or device groups, meeting the operational needs of different scenarios.

### Development Guide

#### Access Rules
- Must be a certified developer
- Must have devices registered to your account
- API credentials required (App ID + Secret)

#### Signing Documentation
- Same signature algorithm as other OpenAPI endpoints
- Use HMAC-SHA256

#### Error Codes

| Code | Description |
|------|-------------|
| 40001 | Invalid policy format |
| 40002 | Policy already exists |
| 40003 | Device not found |
| 40004 | Policy not found |
| 40005 | Association failed |

### Interface List

#### APN (Access Point Name)

| API | Description |
|-----|-------------|
| Create APN strategy | Create new APN configuration |
| Update APN strategy | Update existing APN |
| Get APN strategy | Get APN configuration |

#### Scheduled Power on/off

| API | Description |
|-----|-------------|
| Create scheduled Power on/off strategy | Set power schedule |
| Update scheduled Power on/off strategy | Modify power schedule |
| Get scheduled Power on/off strategy | Get power schedule |

#### APP Permission

| API | Description |
|-----|-------------|
| Create APP permission strategy | Set app permissions |
| Update APP permission strategy | Update app permissions |
| Get APP permission strategy | Get app permissions |

#### Setting Items Manage

| API | Description |
|-----|-------------|
| Create setting items strategy | Configure device settings |
| Update setting items strategy | Update device settings |
| Get setting items strategy | Get device settings |

---

## 7. App Store

> **URL:** https://docs.sunmi.com/en-US/caixfreghjk568/faceghjk502

### App Publishing API

#### App Management

| API | Description |
|-----|-------------|
| Create App in AppStore | Create a new app |
| Delete App from Appstore | Delete a created app |
| Upgrade App | Upgrade app version |
| Get App detail | Query app details |
| Get App review | Fetch audit statuses |
| Update App details | Update app information |
| Update App version detail | Switch deployment mode |
| Get App latest version | Get newest version data |
| Audit Result Callback | Receive audit result push |

#### App Assets

| API | Description |
|-----|-------------|
| Upload Apk | Upload APK installation package |
| Upload Image | Upload icons and screenshots |

#### App Metadata

| API | Description |
|-----|-------------|
| Get App categories | Get app category dictionary |
| Get country language codes | Get country language codes |
| Get device models | Get compatible device terminals |

### API Interaction Flow

#### Create / Delete App Flow

```
Upload Apk → Upload Image → Get device models → Get App categories →
Get country language codes → Create App in AppStore → Get App detail →
Get App review (polling) → [optional] Audit Result Callback →
Delete App from Appstore (when needed)
```

#### App Upgrade Flow

```
Upload Apk → Upgrade App → Get App review → Update App version detail →
Get App review → Get App latest version
```

### Changelog

| Version | Date | Update Content |
|---------|------|----------------|
| v1.3.0 | 2026-06-03 | Added `gray_release_mode` to Create App, Upgrade App APIs |
| v1.2.0 | 2026-05-22 | Added `is_param_app` field to Get App detail API |
| v1.1.0 | 2026-04-21 | Added `support_landscape_portrait` field to Get device models API |
| v1.0.0 | 2026-03-15 | Initial release |

---

## 8. SUNMI Remote Key Injection

> **URL:** https://docs.sunmi.com/en-US/caixrmeghjk546/xqdqeghjk513

### Introduction

Remote Key Injection (RKI) allows you to securely inject cryptographic keys into SUNMI devices remotely.

### Key APIs

| API | Description |
|-----|-------------|
| Upload key | Upload encryption key |
| Assign key | Assign key to device |
| Reassign key | Reassign key to different device |
| Delete key | Remove key from system |
| Get protection key | Get protection key info |
| Save initial key | Save initial key |
| Generate Random Key | Generate random encryption key |
| Get Key Info | Get key details |

### Device APIs

| API | Description |
|-----|-------------|
| Gets the device injection status | Check injection status |
| Device Unbind | Unbind device from key |
| Device Unlock | Unlock device |
| Push ThirdParty Key Download | Push key download to device |
| Get ThirdParty Key Injection Result | Get injection result |

### Task APIs

| API | Description |
|-----|-------------|
| Get Create Task Device | Get devices for task |
| Create Task | Create injection task |

---

## 9. Parameter Management

> **URL:** https://docs.sunmi.com/en-US/caicaieghjk579/xzfaeghjk480

### Introduction

#### What is Parameter Management?

Parameter management is a dedicated function for the payment industry. Developers define parameter templates (key and value) for their submitted applications. Service providers copy these templates, adjust the values, and remotely deploy the configurations to devices via different groups or serial numbers (SN).

#### Which Applications Require Parameter Configuration?

Payment applications need parameter configuration, including information such as:
- Merchant number
- Merchant ID
- Merchant address
- Tip

#### Who Will Configure the Parameter?

Developers create parameter templates, which can be set with default values or left blank. When pushing parameters to devices, distributors, service providers and other relevant parties shall fill in specific information based on the templates.

#### Prerequisites

The payment application developed must integrate the parameter SDK to successfully receive parameters pushed from the cloud.

### API Interfaces

#### Parameter Template

| API | Description |
|-----|-------------|
| Summary List | List all templates |
| Application Version Parameter Template List | List templates for app version |
| Parameter Template Details | Get template details |
| Parameter Template Clone | Clone template |
| Parameter Template Delete | Delete template |
| Parameter Template Update | Update template |
| Create Normal Template | Create normal template |
| Create Official Template | Create official template |
| Parameter Template Validate | Validate template |

#### Device Parameter

| API | Description |
|-----|-------------|
| Device Application Parameter List | List device parameters |
| Device Application Parameter Details | Get parameter details |
| Device Application Parameter Delete | Delete device parameter |
| Save Device Application Parameter Push | Save parameter push |
| Stop Device Application Parameter Push | Stop parameter push |
| Get Device Application Parameter Push | Get push status |
| Device Application Parameter Push List | List parameter pushes |
| Device Application Parameter Push Details | Get push details |

---

## 10. OTA

> **URL:** https://docs.sunmi.com/en-US/caicqzeghjk557/xziqeghjk513

### Introduction

OTA (Over-The-Air) allows remote firmware updates for SUNMI devices.

### Callback for OTA Upgrade Task Approval Notifications

Developers can receive notifications when OTA upgrade tasks are approved or rejected.

---

## Related Documentation

- [SunmiCustomer API](./03-SUNMI-CUSTOMER-API.md)
- [Printer Development](./04-PRINTER-DEVELOPMENT.md)
