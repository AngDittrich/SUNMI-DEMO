# SUNMI Printer Development Documentation

**Source:** https://docs.sunmi.com/en-US/cdixeghjk491/
**Extracted:** August 17, 2026

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

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzaeghjk480

### I. SUNMI printing services

SUNMI printing services have been preset in the system, which can enable SUNMI printer settings and settings for adding external printers. Developers can be able to configure printers and directly control an external printer by virtue of SUNMI printing services, without any additional adaptation processes.

### II. How to find SUNMI printing services

You can find SUNMI printing services on the homepage of the system settings, which is shown as [SunmiPrinter]. The following screenshot is a homepage example of a desktop terminal.

After selecting [SunmiPrinter], a list of printers that are controlled by the current SUNMI terminal will be shown. SUNMI printers will be dynamically shown on the list according to different device models, such as SUNMI laser printer and SUNMI stylus printer.

### III. How to set up SUNMI printers

#### 1. SUNMI built-in printers

The settings of SUNMI built-in printers are mainly used to manage the receipt printers that are integrated to SUNMI terminals. It provides different capabilities according to the functions of different device models.

**Main settings:**

- **Types of printers:** In general, printers are used for printing thermal receipts. But some SUNMI devices also support label printing and black mark printing, which can be realized by switching the printer mode.
  - Label mode: supports label study, manually or automatically
  - Black mark mode: set the cutting position of the black mark

- **Print density:** Set the print density of the printer in the range of 70% to 130%.

- **Print style settings:** Mainly used to temporarily override the developer's layout design. Since standard EPSON command set barcodes and QR codes can be printed directly, single-line multi-code printing will be enabled.

- **Font settings:** Three types of fonts available:
  - SunmiMonoSpaced 1.0: supports a small range of blocks, suitable for printing English
  - SunmiMonoSpaced 2.0: basically supports all blocks, suitable for printing small fonts
  - SunmiMonoSpaced 3.0: optimized version with improved fineness

- **Printing paper specification settings:** SUNMI built-in 80mm printer can be adjusted to 58mm mode.

- **System alert pop-up switch:** Supports automatic broadcast of printer status (out of paper, cover open, etc.)

- **Startup alert DND switch:** Controls whether device detects printer status every boot.

- **Select virtual Bluetooth command:** A virtual Bluetooth named [InnerPrinter] allows controlling built-in printer via Bluetooth connection. Data flow parsed according to ESC/POS commands by default.

- **Automatic cutter switch:** Provides automatic paper cutting when enabled.

#### 2. SUNMI laser printer

SUNMI laser printers mainly refer to the laser printers configured in SUNMI kiosks, mainly used for file printing. Supports two paper specifications.

#### 3. Manage custom printers

SUNMI printer supports developers to add external printers to the printing system. Supports three types of connection: LAN, Bluetooth and USB.

- **Add custom printers:** Select the [+] option in the printer list
- **Custom printer settings:** Printer name, printing paper specifications, printer cutter support
- **Default printer:** After enabling, the default printer obtained by SDK will switch to the custom printer
- **Delete custom printer:** Swipe left on the custom printer

---

## 2. SDK Upgrade Description

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzxeghjk491

### I. Why upgrade

To help developers use SUNMI printers more easily, and also to simplify the printing development process for developers, SUNMI reconstructed the printing SDK at the end of March.

### II. Comparison of the old SDK and the new SDK

| Comparison item | The old version | The new version |
|----------------|-----------------|-----------------|
| Type of printer supported | Only thermal receipt printer supported; Print by line | Support multiple types: thermal receipt, thermal label, file printer, etc. |
| Coverage | Only SUNMI built-in printer | All SUNMI devices + cloud printer |
| Commands | Only ESC standard commands | ESC commands and TSPL, more coming |
| User-friendly | Difficult to build complex style contents | Simple and easy to build complex style contents |

---

## 3. SUNMI Printing SDK Overview

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzceghjk502

### 1. How to integrate SDK

SUNMI printing SDK can be obtained through remote repository:

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

### 3. How to use the SDK

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

#### Get a printer

```java
void getPrinter(Context context, PrinterListen printerListen)
```

**PrinterListen callbacks:**

```java
void onDefPrinter(Printer printer)
void onPrinters(List<Printer> printers)
```

#### API for setting log output

```java
void log(boolean enable, String tag)
```

#### Release the SDK

```java
void destroy()
```

#### Jump to printer configuration page

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

**setSunmiPrinterDensity:**

```java
public void setSunmiPrinterDensity(Context context, Density density)
```

**setSunmiPrinterFontType:**

```java
public void setSunmiPrinterFontType(Context context, FontType fontType)
```

**setSunmiPrinterSpeed:**

```java
public void setSunmiPrinterSpeed(Context context, Speed speed)
```

**setSunmiPrinterAlert:**

```java
public void setSunmiPrinterAlert(Context context, boolean isAlert)
```

**setSunmiPrinterBootAlert:**

```java
public void setSunmiPrinterBootAlert(Context context, boolean isAlert)
```

**Enumeration Descriptions:**

- **Density:** 70% - 130%
- **FontType:** Default, SunmiFont1, SunmiFont2, SunmiFont3
- **Speed:** LOW, MEDIUM, HIGH

### 4. SDK exceptions

SUNMI Printing SDK is a set of APIs for various types of printers. Different types of APIs correspond to different types of printer calls. If you call unsupported APIs for some specific printers, the SDK exception may happen.

---

## 4. APIs for Printing Thermal Receipts

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzfeghjk535

### I. Function Introduction

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

### II. Descriptions of APIs

#### 1. Line settings

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

**LineStyle additional methods:**
- `setRotate` - Set print direction (0 or 180 degrees)

**Example:**

```java
printer.lineApi().initLine(BaseStyle.getStyle());
printer.lineApi().initLine(BaseStyle.getStyle().setAlign(Align.CENTER));
printer.lineApi().initLine(LineStyle.getStyle().setRotate(Rotate.ROTATE_180));
```

#### 2. Print text

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

#### 3. Arrange printing content by column

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

#### 4. Print a barcode

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

#### 5. Print a QR code

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

#### 6. Print an image

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

#### 7. Print a dividing line

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

#### 8. Printing output

```java
void autoOut()
```

Automatically moves paper to exit after printing. If printer has cutter, automatically cuts paper.

#### 9. Transaction mode - switch

```java
void enableTransMode(boolean enable)
```

Enables/disables transaction mode. When enabled, all line printing commands are cached until transaction mode is executed.

#### 10. Transaction mode - submit

```java
void printTrans(PrintResult listener)
```

Submits transaction. Executes cached line printing commands. PrintResult returns:
- `resultCode`: 0 for success, <0 for failure
- `message`: Additional information when failed

### III. Enumeration Parameter Description

**Align:** DEFAULT (Left), LEFT, CENTER, RIGHT

**HumanReadable:** POS_ONE (Bottom left), POS_TWO (Bottom centered), POS_THREE (Top and Bottom)

**Symbology:** UPC-A, UPC-E, EAN13, EAN8, CODE39, ITF, CODABAR, CODE93, CODE128

**ErrorLevel:** L (7%), M (15%), Q (25%), H (30%)

**ImageAlgorithm:** BINARIZATION (adjustable float value), DITHERING (no float value needed)

---

## 5. APIs for Printing Labels & Receipts

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzmeghjk546

### I. Function Introduction

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

### II. Service Restrictions

- Jump setting capability requires print service version 6.6.32 or above
- Built-in printer can also use this interface in thermal receipt mode

### III. Descriptions of APIs

#### 1. Canvas settings

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

#### 2. Draw text content

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

#### 3. Draw barcode content

```java
void renderBarCode(String code, BarcodeStyle style)
```

#### 4. Draw QR code content

```java
void renderQrCode(String text, QrStyle style)
```

#### 5. Draw an image

```java
void renderBitmap(Bitmap bitmap, BitmapStyle style)
```

#### 6. Draw a special shape

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

#### 7. Print the drawn content

```java
void printCanvas(int count, PrintResult listener)
```

### IV. Example

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

### V. Tips for Printing Labels

1. **Print blank label:** Call `printCanvas(1, null)` with empty canvas
2. **Handle label removal status:** Listen to broadcast `woyou.aidlservice.jiuv5.NORMAL_ACTION`

---

## 6. APIs for Printing Files

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzzeghjk557

### I. Function Introduction

```java
public FileApi fileApi()
```

File rendering APIs for directly printing files (PDF, JPEG, etc.)

**Available methods:**

```java
void printFile(String path, PrintResult listener)
void printFile(String path, FileStyle style, PrintResult listener)
```

### II. Coverage

Supported models:
- PANTUM serial: P3017D, CP1100, BP5126DN
- HPRT serial: U100

### III. Descriptions of APIs

#### 1. Print specified files

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

#### 2. Print file with callback

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

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzreghjk568

### I. Function Introduction

```java
public QueryApi queryApi()
```

Get printer's basic information and real-time status.

**Available methods:**

```java
Status getStatus()
String getInfo(PrinterInfo info)
```

### II. Descriptions of APIs

#### 1. Get printer's real-time status

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
| woyou.aidlservice.jiuv5.NORMAL_ACTION | Printer ready |
| woyou.aidlservice.jiuv5.OUT_OF_PAPER_ACTION | Out of paper |
| woyou.aidlservice.jiuv5.PAPER_ERROR_ACITON | Paper jam |
| woyou.aidlservice.jiuv5.OVER_HEATING_ACITON | Printhead overheated |
| woyou.aidlservice.jiuv5.MOTOR_HEATING_ACITON | Motor overheated |
| woyou.aidlservice.jiuv5.COVER_OPEN_ACTION | Cover open |
| woyou.aidlservice.jiuv5.COVER_ERROR_ACTION | Cover not completely closed |
| woyou.aidlservice.jiuv5.KNIFE_ERROR_ACTION_1 | Cutter exception |
| woyou.aidlservice.jiuv5.KNIFE_ERROR_ACTION_2 | Cutter repaired |
| woyou.aidlservice.jiuv5.BLACKLABEL_NON_EXISTENT_ACITON | Black mark not detected |
| woyou.aidlservice.jiuv5.LABEL_NON_EXISTENT_ACITON | Label not detected |
| woyou.aidlservice.jiuv5.ERROR_ACTION | Unknown exception |
| woyou.aidlservice.jiuv5.PICK_PAPER_ACTION | Paper not removed |
| woyou.aidlservice.jiuv5.LESS_OF_PAPER_ACTION | Paper running out |
| woyou.aidlservice.jiuv5.PRINTER_NON_EXISTENT_ACITON | Printer not detected |

#### 2. Get printer information

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

**URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xdzieghjk579

### I. Function Introduction

```java
public CommandApi commandApi()
```

Passthrough APIs for instructions. Two instruction sets available: ESC/POS and TSPL.

**Available methods:**

```java
void sendEscCommand(byte[] esc)
void sendTsplCommand(byte[] tspl)
```

### II. Service Restrictions

- SUNMI built-in printers support all instruction sets
- Other printers only support ESC/POS instruction sets currently

### III. Descriptions of APIs

#### 1. Send ESC commands

```java
void sendEscCommand(byte[] esc)
```

For ordinary receipt printing. Receives GB18030 character set by default.

**Example:**

```java
byte[] esc = new byte[]{0x1B, 0x61, 0x01};
PrinterSdk.getInstance().getPrinter().commandApi().sendEscCommand(esc);
```

#### 2. Send TSPL instructions

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

## Additional Printer Development Sections

The documentation also includes these additional sections (not fully extracted):

- **SDK Reference (Old):** Inbuilt non-detachable printers documentation
- **JavaScript Printer:** JavaScript SDK for thermal receipt, label, and command printing
- **Flutter Printer:** Flutter Printing SDK Reference
- **Uniapp Printer:** UniApp SDK for thermal receipt, label, and command printing
- **Cordova Printer:** Cordova SDK for thermal receipt, label, and command printing
- **Cloud Printer V2:** Cloud printing integration with web and mobile SDKs

---

*Documentation extracted from SUNMI Developer Documentation Center*
*Last updated in source: 2024*
