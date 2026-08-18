# SunmiCustomer API

> **Section:** Integration Guide > SunmiCustomer API
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/xdcxeghjk491

---

## Table of Contents

1. [Overview of SDK](#1-overview-of-sdk)
2. [Device Information Module](#2-device-information-module)
3. [Device Management Module](#3-device-management-module)
4. [Software Management Module](#4-software-management-module)
5. [System Management Module](#5-system-management-module)
6. [System UI Management Module](#6-system-ui-management-module)
7. [Device Runtime Information Module](#7-device-runtime-information-module)
8. [Network Management Module](#8-network-management-module)
9. [Device Software Information Module](#9-device-software-information-module)
10. [Kiosk Management Module](#10-kiosk-management-module)
11. [Certificate Management Module](#11-certificate-management-module)
12. [Log Management Module](#12-log-management-module)

---

## 1. Overview of SDK

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdcxeghjk491
> **Update Time:** 2026-07-29 17:10:31

The SunmiCustomer API provides a comprehensive set of AIDL-based interfaces for interacting with SUNMI device hardware and system features. The SDK is organized into multiple modules, each handling specific device capabilities.

### Integration

Add the SDK dependency to your project:

```gradle
dependencies {
    implementation 'com.sunmi:customeraidl:1.0.0'
}
```

### Key Features
- Device identification and information retrieval
- Software installation and management
- System settings and UI control
- Network configuration
- Kiosk mode management
- Certificate management
- Log collection

---

## 2. Device Information Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdqieghjk579

### API List

| API | Description |
|-----|-------------|
| `getDeviceSN()` | Get device serial number |
| `getDeviceModel()` | Get device model |
| `getDeviceBrand()` | Get device brand |
| `getROMVersion()` | Get ROM version |
| `getDeviceIdentifiers()` | Get all device identifiers |

### Usage

```java
// Get device SN
String sn = deviceInfoApi.getDeviceSN();

// Get device model
String model = deviceInfoApi.getDeviceModel();

// Get ROM version
String romVersion = deviceInfoApi.getROMVersion();
```

---

## 3. Device Management Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xddaeghjk480

### API List

| API | Description |
|-----|-------------|
| `reboot()` | Reboot the device |
| `shutdown()` | Shutdown the device |
| `setScreenBrightness(int level)` | Set screen brightness |
| `getScreenBrightness()` | Get screen brightness |
| `setVolume(int stream, int volume)` | Set volume |
| `getVolume(int stream)` | Get volume |
| `setTimeZone(String tz)` | Set time zone |
| `getTimeZone()` | Get time zone |
| `setLanguage(String lang)` | Set language |
| `getLanguage()` | Get language |
| `setRotation(int rotation)` | Set screen rotation |
| `getRotation()` | Get screen rotation |
| `enableStatusBar(boolean enable)` | Enable/disable status bar |
| `enableNavigationBar(boolean enable)` | Enable/disable navigation bar |
| `setAirplaneMode(boolean enable)` | Set airplane mode |
| `isAirplaneMode()` | Check airplane mode status |
| `setWifiEnabled(boolean enable)` | Enable/disable WiFi |
| `isWifiEnabled()` | Check WiFi status |
| `setBluetoothEnabled(boolean enable)` | Enable/disable Bluetooth |
| `isBluetoothEnabled()` | Check Bluetooth status |
| `setGPS(boolean enable)` | Enable/disable GPS |
| `isGPSEnabled()` | Check GPS status |

---

## 4. Software Management Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xddxeghjk491

### API List

| API | Description |
|-----|-------------|
| `installApp(String path, boolean autoStart, listener)` | Silently install app |
| `uninstallApp(String packageName, listener)` | Silently uninstall app |
| `killApp(String packageName)` | Force stop app |
| `restartApp(String packageName)` | Restart app |
| `setBatteryOptimizationWhitelist(String whitelist)` | Set battery optimization whitelist |
| `setAppEnabled(String packageName, boolean enabled)` | Enable/disable app |
| `grantAppPermissions(String packageName, String permissions)` | Grant app permissions |
| `getRequestPermissions(String packageName)` | Get app permissions |
| `allowAlertWindowPermission(String packageName)` | Grant floating window permission |
| `prohibitUninstall(String packageName, boolean allowUninstall)` | Prevent app uninstall |
| `getProhibitUninstallList()` | Get protected apps list |
| `enableAutoStartApp(boolean enable)` | Enable/disable auto-start |
| `isAutoStartAppEnabled()` | Check auto-start status |
| `setAutoStartApp(String packageName)` | Set auto-start app |
| `getAutoStartApp()` | Get auto-start app |
| `clearAutoStartApp()` | Clear auto-start app |
| `clearApplicationUserDataWithResult(String packageName, callback)` | Clear app data |
| `deleteApplicationCacheFiles(String packageName, callback)` | Delete app cache |
| `isForeground(String packageName)` | Check if app is in foreground |
| `addAppToCommonAppLockList(List packageNames, int type, String password)` | Add app lock |
| `removeAppFromCommonAppLockList(List packageNames)` | Remove app lock |
| `getCommonAppLockList()` | Get locked apps list |
| `isCommonAppLock(String packageName)` | Check if app is locked |
| `installApp(String appFilePath, OnInstallAppListener listener)` | Install app (legacy) |
| `revokeAppPermission(String packageName, String permissions)` | Remove app permissions |
| `setNotificationsEnabledForPackage(String packageName, boolean enabled)` | Set notification permission |
| `removeAllRecentTasks()` | Clear recent tasks |

### Key API Details

#### installAppV2

```java
void installAppV2(String appFilePath, boolean autoStart, OnInstallAppListener listener)
```

**Parameters:**
- `appFilePath` - Local path of the target APK file
- `autoStart` - Whether to automatically launch the app after installation
- `listener` - Callback for app installation

**Example:**
```java
sTmsApi.getSoftwareManager().installAppV2("/sdcard/xxx/test.apk", true, new OnInstallAppListener.Stub() {
    @Override
    public void onInstallFinished() throws RemoteException { }

    @Override
    public void onInstallError(int errorId) throws RemoteException { }

    @Override
    public void onInstallSuccess(String packagename) throws RemoteException { }

    @Override
    public void onInstallFail(String packagename, int errorId) throws RemoteException { }
});
```

#### uninstallApp

```java
void uninstallApp(String packageName, OnUninstallAppListener listener)
```

#### setAppEnabled

```java
boolean setAppEnabled(String packageName, boolean enabled)
```

> **Note:** You cannot operate system pre-installed apps whose package names start with `android`, `vendor`, or `com.android`.

#### addAppToCommonAppLockList

```java
boolean addAppToCommonAppLockList(List<String> packageNames, int type, String password)
```

**Password Types:**
- `-1`: Mixed password (supports up to 8 characters)
- `0`: 4-digit numeric password
- `1`: Mixed password (supports up to 8 characters)

---

## 5. System Management Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xddceghjk502

### API List

| API | Description |
|-----|-------------|
| `setSystemProperty(String key, String value)` | Set system property |
| `getSystemProperty(String key)` | Get system property |
| `setInstallNonMarketApps(boolean allow)` | Allow/disallow non-market apps |
| `isInstallNonMarketAppsEnabled()` | Check non-market apps setting |
| `setStayAwakeWhilePlugged(int what)` | Set stay awake when plugged |
| `getStayAwakeWhilePlugged()` | Get stay awake setting |
| `setTimeAutomatic(boolean auto)` | Set automatic time |
| `isTimeAutomatic()` | Check automatic time setting |
| `setAlwaysOnDisplay(boolean enable)` | Enable/disable always-on display |
| `isAlwaysOnDisplayEnabled()` | Check always-on display status |

---

## 6. System UI Management Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xddqeghjk513

### API List

| API | Description |
|-----|-------------|
| `setSystemUIVisibility(int visibility)` | Set system UI visibility |
| `getSystemUIVisibility()` | Get system UI visibility |
| `setStatusBarVisibility(boolean visible)` | Show/hide status bar |
| `setNavigationBarVisibility(boolean visible)` | Show/hide navigation bar |
| `setSystemBarMode(int mode)` | Set system bar mode |
| `getSystemBarMode()` | Get system bar mode |
| `setSystemUIStyle(int style)` | Set system UI style |
| `getSystemUIStyle()` | Get system UI style |

---

## 7. Device Runtime Information Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdddeghjk524

### API List

| API | Description |
|-----|-------------|
| `getCPUUsage()` | Get CPU usage |
| `getMemoryUsage()` | Get memory usage |
| `getStorageUsage()` | Get storage usage |
| `getBatteryLevel()` | Get battery level |
| `isCharging()` | Check if charging |
| `getScreenOnTime()` | Get screen-on time |
| `getUptime()` | Get device uptime |

---

## 8. Network Management Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xddfeghjk535

### API List

| API | Description |
|-----|-------------|
| `getWifiSSID()` | Get WiFi SSID |
| `getWifiBSSID()` | Get WiFi BSSID |
| `getWifiMAC()` | Get WiFi MAC address |
| `getWifiIP()` | Get WiFi IP address |
| `getEthernetMAC()` | Get Ethernet MAC address |
| `getEthernetIP()` | Get Ethernet IP address |
| `getIMSI()` | Get IMSI |
| `getIMEI()` | Get IMEI |
| `getSimSerialNumber()` | Get SIM serial number |
| `getNetworkType()` | Get network type |
| `isNetworkAvailable()` | Check network availability |
| `setWifi(String ssid, String password)` | Connect to WiFi |
| `forgetWifi(String ssid)` | Forget WiFi network |
| `getSavedWifiList()` | Get saved WiFi networks |
| `startWifiScan()` | Start WiFi scan |
| `getWifiScanResults()` | Get WiFi scan results |
| `setAPN(String name, String apn, String type)` | Set APN |
| `getAPN()` | Get current APN |
| `getAPNList()` | Get APN list |

---

## 9. Device Software Information Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xddmeghjk546

### API List

| API | Description |
|-----|-------------|
| `getInstalledApps()` | Get list of installed apps |
| `getAppVersion(String packageName)` | Get app version |
| `getAppName(String packageName)` | Get app name |
| `getAppInstallTime(String packageName)` | Get app install time |
| `getAppUpdateTime(String packageName)` | Get app update time |
| `isAppInstalled(String packageName)` | Check if app is installed |
| `isSystemApp(String packageName)` | Check if app is system app |
| `getAppPermissions(String packageName)` | Get app permissions |
| `getAppSignature(String packageName)` | Get app signature |

---

## 10. Kiosk Management Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xmrqeghjk513

### API List

| API | Description |
|-----|-------------|
| `enterKioskMode(String packageName)` | Enter kiosk mode |
| `exitKioskMode()` | Exit kiosk mode |
| `isKioskModeEnabled()` | Check kiosk mode status |
| `getKioskApp()` | Get current kiosk app |
| `setKioskPassword(String password)` | Set kiosk exit password |
| `exitKioskWithPassword(String password)` | Exit kiosk with password |
| `setKioskAllowedApps(List<String> allowedApps)` | Set allowed apps in kiosk |
| `getKioskAllowedApps()` | Get allowed apps in kiosk |

---

## 11. Certificate Management Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xmrdeghjk524

### API List

| API | Description |
|-----|-------------|
| `installCertificate(String certPath, String password)` | Install certificate |
| `uninstallCertificate(String alias)` | Uninstall certificate |
| `getInstalledCertificates()` | Get installed certificates |
| `isCertificateInstalled(String alias)` | Check if certificate is installed |
| `getCertificateInfo(String alias)` | Get certificate information |

---

## 12. Log Management Module

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xziceghjk502

### API List

| API | Description |
|-----|-------------|
| `getLogPath()` | Get log file path |
| `clearLogs()` | Clear all logs |
| `setLogLevel(int level)` | Set log level |
| `getLogLevel()` | Get log level |
| `exportLogs(String exportPath)` | Export logs to specified path |
| `getLogFiles()` | Get list of log files |
| `getLogFileSize(String logFile)` | Get log file size |
| `deleteLogFile(String logFile)` | Delete specific log file |

---

## Error Codes

### Silent Installation Error Codes

| Error Code | Description |
|------------|-------------|
| 0 | Success |
| 1 | Unknown error |
| 2 | Invalid APK file |
| 3 | APK parse error |
| 4 | APK verification failed |
| 5 | Insufficient storage space |
| 6 | App already installed |
| 7 | Installation cancelled |
| 8 | Installation failed |

---

## Related Documentation

- [Get Started](./01-GET-STARTED.md)
- [Development Guide](./02-DEVELOPMENT-GUIDE.md)
- [Printer Development](./04-PRINTER-DEVELOPMENT.md)
