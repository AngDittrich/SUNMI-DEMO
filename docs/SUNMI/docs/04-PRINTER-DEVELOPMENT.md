# Printer Development

> **Section:** Integration Guide > Printer Development
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Table of Contents

1. [Introduction to SUNMI Printing Services](#1-introduction-to-sunmi-printing-services)
2. [SDK Upgrade Description](#2-sdk-upgrade-description)
3. [SUNMI Printing SDK Overview](#3-sunmi-printing-sdk-overview)
4. [APIs for Printing Thermal Receipts](#4-apis-for-printing-thermal-receipts)
5. [APIs for Printing Labels & Receipts](#5-apis-for-printing-labels--receipts)
6. [APIs for Printing Files](#6-apis-for-printing-files)
7. [APIs for Querying Printer](#7-apis-for-querying-printer)
8. [APIs for Printing Instruction Sets](#8-apis-for-printing-instruction-sets)

---

## 1. Introduction to SUNMI Printing Services

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzaeghjk480

### SUNMI Printing Services

SUNMI printing services have been preset in the system, which can enable SUNMI printer settings and settings for adding external printers. Developers can configure printers and directly control an external printer by virtue of SUNMI printing services, without any additional adaptation processes.

### How to Find SUNMI Printing Services

You can find SUNMI printing services on the homepage of the system settings, shown as [SunmiPrinter].

### SUNMI Printer Settings

#### 1. SUNMI Built-in Printers

**Main settings:**

| Setting | Description |
|---------|-------------|
| **Types of printers** | Thermal receipt, label printing, black mark printing |
| **Print density** | 70% to 130% |
| **Print style settings** | Override developer's layout design |
| **Font settings** | SunmiMonoSpaced 1.0/2.0/3.0 |
| **Paper specification** | 80mm can be adjusted to 58mm mode |
| **System alert pop-up** | Auto broadcast of printer status |
| **Startup alert DND** | Controls printer status detection on boot |
| **Virtual Bluetooth command** | Virtual Bluetooth named [InnerPrinter] |
| **Automatic cutter** | Auto paper cutting when enabled |

#### 2. SUNMI Laser Printer

SUNMI laser printers are configured in SUNMI kiosks, mainly for file printing. Supports two paper specifications.

#### 3. Manage Custom Printers

SUNMI printer supports developers to add external printers. Supports three types of connection: LAN, Bluetooth and USB.

- **Add custom printers:** Select the [+] option in the printer list
- **Custom printer settings:** Printer name, printing paper specifications, printer cutter support
- **Default printer:** After enabling, the default printer obtained by SDK will switch to the custom printer
- **Delete custom printer:** Swipe left on the custom printer

---

## 2. SDK Upgrade Description

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzxeghjk491

### Comparison: Old SDK vs New SDK

| Comparison Item | Old Version | New Version |
|-----------------|-------------|-------------|
| Type of printer supported | Only thermal receipt printer; Print by line | Support multiple types: thermal receipt, thermal label, file printer, etc. |
| Coverage | Only SUNMI built-in printer | All SUNMI devices + cloud printer |
| Commands | Only ESC standard commands | ESC commands and TSPL, more coming |
| User-friendly | Difficult to build complex style contents | Simple and easy to build complex style contents |

---

## 3. SUNMI Printing SDK Overview

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzceghjk502

### 1. How to Integrate SDK

```gradle
android {
    ...
}

dependencies {
    implementation 'com.sunmi:printerx:1.0.20'
}
```

### 2. Demo Example

- Github: https://github.com/nicennnnnnnlee/sunmi_printer_demo
- Gitee: https://gitee.com/nicen-lee/sunmi-printer-demo

### 3. How to Use the SDK

After integrating the SDK, use `getPrinter()` method to get the printer (asynchronous process). Use the `PrinterListen` callback `onDefPrinter()` method to control the built-in printing services.

**Available APIs:**

| API | Description |
|-----|-------------|
| QueryApi | Printer query API |
| CommandApi | Command set printing API |
| LineApi | Receipt printing API |
| CanvasApi | Label printing API |
| FileApi | File printing API |
| CashDrawerApi | Cash drawer control API |
| LcdApi | LCD customer display control API |

#### Get a Printer

```java
void getPrinter(Context context, PrinterListen printerListen)
```

**PrinterListen callbacks:**

```java
void onDefPrinter(Printer printer)
void onPrinters(List<Printer> printers)
```

#### API for Setting Log Output

```java
void log(boolean enable, String tag)
```

#### Release the SDK

```java
void destroy()
```

#### Jump to Printer Configuration Page

```java
boolean startSettings(Activity activity, SettingItem item)
```

**SettingItem enum values:**
- TYPE - Switch printer type (thermal/label)
- DENSITY - Set print density
- PAPER - Switch paper specification
- FONT - Switch printer font
- OTHER - Other configuration items

#### PrinterSdk Global Setting APIs

```java
// Set print density
public void setSunmiPrinterDensity(Context context, Density density)

// Set font type
public void setSunmiPrinterFontType(Context context, FontType fontType)

// Set print speed
public void setSunmiPrinterSpeed(Context context, Speed speed)

// Set alert
public void setSunmiPrinterAlert(Context context, boolean isAlert)

// Set boot alert
public void setSunmiPrinterBootAlert(Context context, boolean isAlert)
```

**Enumeration Descriptions:**
- **Density:** 70% - 130%
- **FontType:** Default, SunmiFont1, SunmiFont2, SunmiFont3
- **Speed:** LOW, MEDIUM, HIGH

---

## 4. APIs for Printing Thermal Receipts

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzfeghjk535

### Function Introduction

```java
public LineApi lineApi()
```

A set of APIs for printing ordinary thermal receipts in line output mode.

**Available methods:**

```java
void initLine(BaseStyle format)
void addText(String text, TextStyle style)
void printText(String text, TextStyle style)
void printTexts(String[] text, int[] colsWidthArr, TextStyle[] styles)
void printBarCode(String code, BarcodeStyle style)
void printQrCode(String code, QrStyle style)
void printBitmap(Bitmap bitmap, BitmapStyle style)
void printDividingLine(DividingLine style, int offset)
void autoOut()
void enableTransMode(boolean enable)
void printTrans(PrintResult listener)
```

### API Descriptions

#### 1. Line Settings

```java
void initLine(BaseStyle format)
```

**BaseStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| setWidth | Set printable area width | Printer paper width |
| setHeight | Set line height (0-255 pixels) | 30 pixels |
| setAlign | Set alignment | Left justify |
| setRenderColor | Set color | Black |
| setPosX | Set left margin | 0 pixels |

**Example:**
```java
printer.lineApi().initLine(BaseStyle.getStyle());
printer.lineApi().initLine(BaseStyle.getStyle().setAlign(Align.CENTER));
printer.lineApi().initLine(LineStyle.getStyle().setRotate(Rotate.ROTATE_180));
```

#### 2. Print Text

```java
void addText(String text, TextStyle style)
void printText(String text, TextStyle style)
```

**TextStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| setTextSize | Set character size (6-96 pixels) | - |
| setTextWidthRatio | Horizontal magnification | - |
| setTextHeightRatio | Longitudinal magnification | - |
| setTextSpace | Text word spacing (0-100 pixels) | - |
| enableBold | Text bold | Disable |
| enableUnderline | Text underline | Disable |
| enableStrikethrough | Text strikethrough | Disable |
| enableItalics | Italic text | Disable |
| enableInvert | Text inversion | Disable |
| enableAntiColor | Text reverse | Disable |
| setFont | Set custom font | Disable |

**Example:**
```java
PrinterSdk.getInstance().getPrinter().lineApi().addText(
    "Hello World", TextStyle.getStyle().setTextSize(24)
);
```

#### 3. Arrange Printing Content by Column

```java
void printTexts(String[] texts, int[] colsWidthArrs, TextStyle[] styles)
```

**Example:**
```java
TextStyle style = TextStyle.getStyle().setAlign(Align.CENTER);
PrinterSdk.getInstance()
    .getPrinter()
    .lineApi()
    .printTexts(
        new String[]{"Col1", "Col2", "Col3"},
        new int[]{1, 1, 1},
        new TextStyle[]{style, style, style}
    );
```

#### 4. Print a Barcode

```java
void printBarCode(String code, BarcodeStyle style)
```

**BarcodeStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| setDotWidth | Set barcode width (1-16 pixels) | - |
| setBarHeight | Set barcode height (1-255 pixels) | - |
| setReadable | Set HRI position | Not showing |
| setSymbology | Set barcode type | code128 |
| setAlign | Set alignment | Left justify |
| setWidth | Generate specific zoom width | No scaling |
| setHeight | Generate specific zoom height | No scaling |

**Example:**
```java
PrinterSdk.getInstance().getPrinter().lineApi().printBarCode(
    "123456789",
    BarcodeStyle.getStyle().setSymbology(Symbology.CODE128)
);
```

#### 5. Print a QR Code

```java
void printQrCode(String code, QrStyle style)
```

**QrStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| setDot | Set QR code size (1-16 pixels) | - |
| setErrorLevel | Set error correction level | ErrorLevel.L |
| setAlign | Set alignment | Left justify |
| setWidth | Generate specific zoom width | No scaling |
| setHeight | Generate specific zoom height | No scaling |

**Example:**
```java
PrinterSdk.getInstance().getPrinter().lineApi().printQrCode(
    "https://sunmi.com",
    QrStyle.getStyle().setDot(8)
);
```

#### 6. Print an Image

```java
void printBitmap(Bitmap bitmap, BitmapStyle style)
```

**BitmapStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| setAlgorithm | Set image conversion mode | BINARIZATION |
| setValue | Set additional parameters | - |
| setAlign | Set alignment | Left justify |
| setWidth | Image scaling width | No scaling |
| setHeight | Image scaling height | No scaling |

**Example:**
```java
BitmapStyle bitmapStyle = BitmapStyle.getStyle();
bitmapStyle.setAlgorithm(ImageAlgorithm.BINARIZATION);
bitmapStyle.setValue(200);
Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
PrinterSdk.getInstance().getPrinter().lineApi().printBitmap(image, bitmapStyle);
```

#### 7. Print a Dividing Line

```java
void printDividingLine(DividingLine style, int offset)
```

**DividingLine options:**
- `EMPTY` - Blank line
- `SOLID` - Solid line
- `DOTTED` - Dotted line

**Example:**
```java
PrinterSdk.getInstance().getPrinter().lineApi().printDividingLine(DividingLine.EMPTY, 30);
PrinterSdk.getInstance().getPrinter().lineApi().printDividingLine(DividingLine.DOTTED, 2);
```

#### 8. Transaction Mode

```java
// Enable/disable transaction mode
void enableTransMode(boolean enable)

// Submit transaction
void printTrans(PrintResult listener)
```

When transaction mode is enabled, all line printing commands are cached until `printTrans()` is executed.

### Enumeration Parameters

**Align:** DEFAULT (Left), LEFT, CENTER, RIGHT

**HumanReadable:** POS_ONE (Bottom left), POS_TWO (Bottom centered), POS_THREE (Top and Bottom)

**Symbology:** UPC-A, UPC-E, EAN13, EAN8, CODE39, ITF, CODABAR, CODE93, CODE128

**ErrorLevel:** L (7%), M (15%), Q (25%), H (30%)

**ImageAlgorithm:** BINARIZATION (adjustable float value), DITHERING (no float value needed)

---

## 5. APIs for Printing Labels & Receipts

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzmeghjk546

### Function Introduction

```java
public CanvasApi canvasApi()
```

A collection of APIs for printing after drawing the whole label & receipt.

**Available methods:**

```java
void initCanvas(BaseStyle style)
void renderText(String text, TextStyle style)
void renderBarCode(String code, BarcodeStyle style)
void renderQrCode(String text, QrStyle style)
void renderBitmap(Bitmap bitmap, BitmapStyle style)
void renderArea(AreaStyle format)
void printCanvas(int count, PrintResult listener)
```

### Service Restrictions
- Jump setting capability requires print service version 6.6.32 or above
- Built-in printer can also use this interface in thermal receipt mode

### API Descriptions

#### 1. Canvas Settings

```java
void initCanvas(BaseStyle style)
```

**BaseStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| setWidth | Initialize canvas width | 0 pixels |
| setHeight | Initialize canvas height | 0 pixels |
| setPosX | Relative position in drawing area | - |
| setPosY | Relative position in drawing area | - |
| setRenderColor | Color setting (black/red) | Black ink cartridge |

**LabelStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| enableReverse | Label printing direction | Printer default |
| enableMirror | Label printing mirror | Printer default |
| enableBack | Rollback after cutting | Printer default |
| enableTear | Feed to tear-off position | Printer default |

#### 2. Draw Text Content

```java
void renderText(String text, TextStyle style)
```

**TextStyle methods for Canvas:**

| Method | Description | Default |
|--------|-------------|---------|
| setPosX | Start abscissa position | - |
| setPosY | Start ordinate position | - |
| setTextSize | Text size (6-96 pixels) | - |
| setWidth | Width limit | Not limited |
| setHeight | Height limit | Not limited |
| setAlign | Position relative to coordinates | Align.DEFAULT |
| setRotate | Text direction | Horizontal |
| setTextSpace | Character spacing (0-100 pixels) | - |
| enableBold | Text bold | Disable |
| enableUnderline | Underline | Disable |
| enableStrikethrough | Strikethrough | Disable |
| enableItalics | Italic | Disable |

#### 3. Draw Barcode Content

```java
void renderBarCode(String code, BarcodeStyle style)
```

#### 4. Draw QR Code Content

```java
void renderQrCode(String text, QrStyle style)
```

#### 5. Draw an Image

```java
void renderBitmap(Bitmap bitmap, BitmapStyle style)
```

#### 6. Draw a Special Shape

```java
void renderArea(AreaStyle format)
```

**AreaStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| setStyle | Set shape type | Shape.RECT_FILL |
| setWidth | Shape width | - |
| setHeight | Shape height | - |
| setPosX | Starting X coordinate | - |
| setPosY | Starting Y coordinate | - |
| setEndX | End X coordinate (for lines) | - |
| setEndY | End Y coordinate (for lines) | - |
| setThick | Stroke width | - |

**Shape options:** RECT_FILL, RECT_WHITE, RECT_REVERSE, BOX, CIRCLE, OVAL, PATH

#### 7. Print the Drawn Content

```java
void printCanvas(int count, PrintResult listener)
```

### Example

```java
Printer printer = PrinterSdk.getInstance().getPrinter();
printer.canvasApi().initCanvas(
    BaseStyle.getStyle().setWidth(330).setHeight(330)
);
printer.canvasApi().renderArea(
    AreaStyle.getStyle()
        .setStyle(Shape.BOX)
        .setPosX(0).setPosY(0)
        .setWidth(330).setHeight(330)
);
printer.canvasApi().renderText(
    "Label Content",
    TextStyle.getStyle().setPosX(10).setPosY(10).setTextSize(24)
);
printer.canvasApi().printCanvas(1, null);
```

### Tips for Printing Labels
1. **Print blank label:** Call `printCanvas(1, null)` with empty canvas
2. **Handle label removal status:** Listen to broadcast `woyou.aidlservice.jiuv5.NORMAL_ACTION`

---

## 6. APIs for Printing Files

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzzeghjk557

### Function Introduction

```java
public FileApi fileApi()
```

File rendering APIs for directly printing files (PDF, JPEG, etc.)

**Available methods:**

```java
void printFile(String path, PrintResult listener)
void printFile(String path, FileStyle style, PrintResult listener)
```

### Coverage

Supported models:
- PANTUM serial: P3017D, CP1100, BP5126DN
- HPRT serial: U100

### API Descriptions

#### 1. Print Specified Files

```java
void printFile(String path, PrintResult listener)
```

Supports absolute path, network URLs, and local URI files.

**FileStyle methods:**

| Method | Description | Default |
|--------|-------------|---------|
| setFileCopies | Number of copies | 1 |
| setFileDuplex | Single/double-sided printing | Single sided |
| setFileRotate | Print direction | 0 degrees |
| setFileCollate | Print copy by copy | Print one by one |
| setFileStart | First page number | 0 (first page) |
| setFileEnd | Last page number | 0 (last page) |

#### 2. Print File with Callback

```java
void printFile(String path, FileStyle style, PrintResult listener)
```

**PrintResult:**
- `resultCode`: 0 for success, <0 for failure
- `message`: Additional information when failed

**Example:**
```java
FileStyle fileStyle = FileStyle.getStyle()
    .setFileCopies(4)
    .setFileStart(1)
    .setFileEnd(4)
    .setFileCollate(true)
    .setFileRoatate(Rotate.ROTATE_0)
    .setFileDuplex(FileDuplex.SINGLE);

PrinterSdk.getInstance().getPrinter().fileApi().printFile(
    "/path/to/document.pdf",
    fileStyle,
    new PrintResult() {
        @Override
        public void onResult(int resultCode, String message) {
            // Handle result
        }
    }
);
```

---

## 7. APIs for Querying Printer

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzreghjk568

### Function Introduction

```java
public QueryApi queryApi()
```

Get printer's basic information and real-time status.

**Available methods:**

```java
Status getStatus()
String getInfo(PrinterInfo info)
```

### API Descriptions

#### 1. Get Printer's Real-time Status

```java
Status getStatus()
```

**Printer Error Status (ERR_ prefix):**

| Status | Description |
|--------|-------------|
| Status.READY | Printer ready for printing |
| Status.OFFLINE | Printer offline/failure |
| Status.COMM | Communication error |
| Status.ERR_PAPER_OUT | Out of paper |
| Status.ERR_PAPER_JAM | Paper jam |
| Status.ERR_PAPER_MISMATCH | Paper mismatch |
| Status.ERR_PRINTER_HOT | Printhead overheated |
| Status.ERR_MOTOR_HOT | Motor overheated |
| Status.ERR_COVER | Paper bin cover open |
| Status.ERR_COVER_INCOMPLETE | Cover not completely closed |
| Status.ERR_CUTTER | Cutter error |
| Status.ERR_CARTRIDGE_LOSS | Ink cartridge not installed (laser) |
| Status.ERR_CARTRIDGE_MISMATCH | Ink cartridge mismatch (laser) |
| Status.ERR_CARTRIDGE_EMPTY | Ink has run out (laser) |
| Status.ERR_DUPLEX_LOSS | Duplex unit not installed (laser) |
| Status.ERR_CARTON_LOSS | Paper tray not installed (laser) |
| Status.ERR_CARTON_MISMATCH | Paper tray mismatch (laser) |
| Status.ERR_CARTON_EMPTY | Paper tray out of paper (laser) |
| Status.ERR_DRUM_LOSS | Drum not installed (laser) |
| Status.ERR_DRUM_MISMATCH | Drum mismatch (laser) |
| Status.ERR_DRUM_EMPTY | Drum has run out (laser) |
| Status.ERR_STEP | Carriage failure (laser) |

**Printer Warning Status (WARN_ prefix):**

| Status | Description |
|--------|-------------|
| Status.WARN_THERMAL_PAPER | Thermal paper running out |
| Status.WARN_STANDARD_PAPER | Standard paper running out (laser) |
| Status.WARN_SPECIAL_PAPER | Special paper running out (laser) |
| Status.WARN_CARTRIDGE | Ink running out (laser) |
| Status.WARN_PICK_PAPER | Paper not removed (value sensors) |

**Printer Status Broadcasts:**

| Broadcast | Description |
|-----------|-------------|
| `woyou.aidlservice.jiuv5.NORMAL_ACTION` | Printer ready |
| `woyou.aidlservice.jiuv5.OUT_OF_PAPER_ACTION` | Out of paper |
| `woyou.aidlservice.jiuv5.PAPER_ERROR_ACITON` | Paper jam |
| `woyou.aidlservice.jiuv5.OVER_HEATING_ACITON` | Printhead overheated |
| `woyou.aidlservice.jiuv5.MOTOR_HEATING_ACITON` | Motor overheated |
| `woyou.aidlservice.jiuv5.COVER_OPEN_ACTION` | Cover open |
| `woyou.aidlservice.jiuv5.COVER_ERROR_ACTION` | Cover not completely closed |
| `woyou.aidlservice.jiuv5.KNIFE_ERROR_ACTION_1` | Cutter exception |
| `woyou.aidlservice.jiuv5.KNIFE_ERROR_ACTION_2` | Cutter repaired |
| `woyou.aidlservice.jiuv5.BLACKLABEL_NON_EXISTENT_ACITON` | Black mark not detected |
| `woyou.aidlservice.jiuv5.LABEL_NON_EXISTENT_ACITON` | Label not detected |
| `woyou.aidlservice.jiuv5.ERROR_ACTION` | Unknown exception |
| `woyou.aidlservice.jiuv5.PICK_PAPER_ACTION` | Paper not removed |
| `woyou.aidlservice.jiuv5.LESS_OF_PAPER_ACTION` | Paper running out |
| `woyou.aidlservice.jiuv5.PRINTER_NON_EXISTENT_ACITON` | Printer not detected |

#### 2. Get Printer Information

```java
String getInfo(PrinterInfo info)
```

**PrinterInfo options:**

| Query | Description |
|-------|-------------|
| PrinterInfo.ID | Printer hardware version |
| PrinterInfo.VERSION | Printer firmware version |
| PrinterInfo.DISTANCE | Total print length since power-on (mm) |
| PrinterInfo.CUTTER | Cutting number since power-on |
| PrinterInfo.HOT | Times of overheating since power-on |
| PrinterInfo.DENSITY | Printer density (70%-130%) |
| PrinterInfo.TYPE | Printer type (see below) |
| PrinterInfo.PAPER | Current paper type |

**PrinterInfo.TYPE values:**

| Value | Type |
|-------|------|
| 0 | General thermal printer |
| 1 | Black mark thermal printer |
| 2 | Thermal label printer |
| 3 | Stylus printer |
| 4 | Laser printer |

---

## 8. APIs for Printing Instruction Sets

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzieghjk579

### Function Introduction

```java
public CommandApi commandApi()
```

Passthrough APIs for instructions. Two instruction sets available: ESC/POS and TSPL.

**Available methods:**

```java
void sendEscCommand(byte[] esc)
void sendTsplCommand(byte[] tspl)
```

### Service Restrictions
- SUNMI built-in printers support all instruction sets
- Other printers only support ESC/POS instruction sets currently

### API Descriptions

#### 1. Send ESC Commands

```java
void sendEscCommand(byte[] esc)
```

For ordinary receipt printing. Receives GB18030 character set by default.

**Example:**
```java
byte[] esc = new byte[]{0x1B, 0x61, 0x01};
PrinterSdk.getInstance().getPrinter().commandApi().sendEscCommand(esc);
```

#### 2. Send TSPL Instructions

```java
void sendTsplCommand(byte[] tspl)
```

For label or receipt content drawing. Applicable to label printers.

**Example:**
```java
byte[] content = "SIZE 75 mm, 50 mm\nCLS\nTEXT 100,100,\"FONT:0\",0,1,1,\"Hello\"\nPRINT 1\n".getBytes();
PrinterSdk.getInstance().getPrinter().commandApi().sendTsplCommand(content);
```

---

## 9. JavaScript Printer SDK

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdizeghjk557

### Overview

SUNMI has launched a JavaScript printing plugin, significantly streamlining and accelerating the adaptation process for developers using SUNMI printers. Third-party software developers can obtain this plugin by searching for "JS USDK" in Sunmi App Store.

### Environment Preparation

On the system desktop, open the App Store, search for "JS USDK", and install it.

### Integration

#### npm Installation

```bash
npm i sunmi-js-sdk
```

#### UMD (Direct HTML Reference)

Download and reference directly in HTML.

### Usage

#### 1. Initialization

Before utilizing the printing services of the JS-SDK, initialize the printer service by invoking the `launchPrinterService` function:

```javascript
let umd_sunmi = new SUNMI();
umd_sunmi.launchPrinterService().then((res) => {
    umd_sunmi.init();
    // Printer service ready
});
```

#### 2. Available APIs

- **QueryApi:** API for Querying Printer
- **CommandApi:** Command API for Printing
- **LineApi:** API for Thermal Receipt Printing
- **CanvasApi:** API for Label Printing

---

## 10. Flutter Printer SDK

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xfadeghjk524

### Overview

Sunmi has launched a Flutter plugin that greatly simplifies and accelerates the developer adaptation process for Sunmi printers.

### Integration

```bash
flutter pub add sunmi_flutter_plugin_printer
```

This will add to `pubspec.yaml`:

```yaml
dependencies:
  sunmi_flutter_plugin_printer: ^1.0.7+7
```

### Available APIs

- **QueryApi:** Printer query API
- **CommandApi:** Command set printing API
- **LineApi:** Receipt printing API
- **CanvasApi:** Label printing API
- **CashDrawerApi:** Cash drawer control API

### Usage

#### Get Printer

```dart
Future<void> getPrinter(PrinterListener listener)
```

**PrinterListener interface:**

```dart
abstract class PrinterListener {
  void onDefPrinter(Printer var1);
}
```

#### Set Log Output

```dart
Future<void> log(bool enable, String? tag)
```

#### Release the SDK

```dart
Future<void> destroy()
```

#### Jump to Printer Configuration Page

```dart
Future<bool?> startSettings(SettingItem item)
```

**SettingItem Enum:**

| Enum Type | Description |
|-----------|-------------|
| TYPE | Switch printer type (thermal/label) |
| DENSITY | Set print density |
| PAPER | Switch paper specification |
| FONT | Switch printer font |
| ALL | Other setting items |

> **Note:** Printer setting jump capabilities require print services version 6.6.32 or later.

---

## 11. UniApp Printer SDK

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xzaaeghjk480

### Overview

UniApp SDK for thermal receipt, label, and command printing on SUNMI devices.

### Available APIs

- **UniApp Thermal Receipt Printing API**
- **UniApp Label and Receipt Printing API**
- **UniApp Command Set Printing API**
- **UniApp Cash Drawer Control API**

---

## 12. Cordova Printer SDK

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xzmaeghjk480

### Overview

Cordova SDK for thermal receipt, label, and command printing on SUNMI devices.

### Available APIs

- **Cordova Print Thermal Receipt Interface**
- **Cordova Print Label Receipt Interface**
- **Cordova Command Set Printing API**
- **Cordova Cash Drawer Control Interface**

---

## 13. Cloud Printer V2

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xffaeghjk480

### Overview

Cloud printing integration with web and mobile SDKs. Supports multiple platforms and connection methods.

### Sections

1. Learn about the docking process
2. Enable cloud printed integration capability
3. API integration development
4. APIs provided by partners under "Device to Cloud" mode
5. APIs for device information callback
6. Cloud Printer Management Platform
7. Cloud Printer ESC/POS Commands
8. Label Printer TSPL Command Set
9. How to set up the Cloud Printer on Web
10. How to Set up Cloud Printer on Windows
11. How to wifi configuration for WeChat
12. Cloud Printer WeChat Mini Program SDK
13. Cloud Printer SDK for iOS
14. SUNMI Cloud Printer iOS SDK Multi-Device Connection
15. Cloud Printer SDK for Android System
16. Cloud Printer SDK for macOS System
17. Cloud Printer driver for macOS
18. Cloud Printer Drivers for OPOS
19. Bluetooth Printer driver for Windows
20. Receipt printing driver for Windows
21. Label printer drivers for Windows
22. Cloud Printer Windows DLL interface
23. How to Use Cloud Printer Over LAN HTTP
24. Binding a Cloud Printer on SUNMI Assistant
25. Cloud Print FAQs

---

## Related Documentation

- [Get Started](./01-GET-STARTED.md)
- [SunmiCustomer API](./03-SUNMI-CUSTOMER-API.md)
- [Cash Drawer Development](./09-CASH-DRAWER.md)
- [Cash Drawer Development](./09-CASH-DRAWER.md)
