# Artificial Intelligence (AI) SDK

> **Section:** Integration Guide > Artificial Intelligence (AI) SDK
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Table of Contents

1. [Face Recognition](#1-face-recognition)
2. [OCR](#2-ocr)
3. [Product Recognition](#3-product-recognition)
4. [Barcode Reader](#4-barcode-reader)

---

## 1. Face Recognition

### Functions Introduction

SUNMI Face Recognition SDK provides facial detection, recognition, and liveness detection capabilities for SUNMI devices with cameras.

### Face Recognition V2.0 SDK Interface Documentation

#### Integration

```gradle
dependencies {
    implementation 'com.sunmi:facerecognition:2.0.0'
}
```

#### Key APIs

| API | Description |
|-----|-------------|
| `init()` | Initialize face recognition |
| `detect(Bitmap bitmap)` | Detect face in image |
| `recognize(Bitmap bitmap)` | Recognize face |
| `verify(Bitmap bitmap, String userId)` | Verify face against user |
| `livenessDetect(Bitmap bitmap)` | Liveness detection |
| `release()` | Release resources |

### NIR Liveness Detection

Near-Infrared (NIR) liveness detection prevents spoofing attacks using photos or videos.

**Usage:**
```java
FaceRecognition faceRecognition = new FaceRecognition(context);
faceRecognition.init();

// Perform liveness detection
LivenessResult result = faceRecognition.livenessDetect(bitmap);
if (result.isLive()) {
    // Proceed with face recognition
}
```

### Face Recognition Error Codes

| Error Code | Description |
|------------|-------------|
| 1001 | No face detected |
| 1002 | Multiple faces detected |
| 1003 | Face too small |
| 1004 | Face too large |
| 1005 | Low image quality |
| 1006 | Liveness detection failed |
| 1007 | Recognition failed |
| 1008 | Database error |
| 1009 | Network error |

---

## 2. OCR

### OCR V3.8 SDK Interface Documentation

SUNMI OCR SDK provides optical character recognition capabilities for documents, ID cards, and other text.

#### Integration

```gradle
dependencies {
    implementation 'com.sunmi:ocr:3.8.0'
}
```

#### Key APIs

| API | Description |
|-----|-------------|
| `recognizeText(Bitmap bitmap)` | Recognize text from image |
| `recognizeIDCard(Bitmap bitmap)` | Recognize ID card |
| `recognizeBusinessLicense(Bitmap bitmap)` | Recognize business license |
| `recognizeInvoice(Bitmap bitmap)` | Recognize invoice |
| `recognizeBankCard(Bitmap bitmap)` | Recognize bank card |

#### Usage

```java
OcrEngine ocrEngine = new OcrEngine(context);
ocrEngine.init();

// Recognize text
TextResult result = ocrEngine.recognizeText(bitmap);
String recognizedText = result.getText();

// Recognize ID card
IDCardResult idResult = ocrEngine.recognizeIDCard(bitmap);
String name = idResult.getName();
String idNumber = idResult.getIDNumber();
```

### OCR Error Code

| Error Code | Description |
|------------|-------------|
| 2001 | Image too large |
| 2002 | Image format not supported |
| 2003 | Low image quality |
| 2004 | No text detected |
| 2005 | Recognition failed |
| 2006 | Model load failed |

---

## 3. Product Recognition

### Fruit and Vegetable Recognition V2.3 SDK Interface Documentation

SUNMI Product Recognition SDK can identify fruits, vegetables, and other products from images.

#### Integration

```gradle
dependencies {
    implementation 'com.sunmi:productrecognition:2.3.0'
}
```

#### Key APIs

| API | Description |
|-----|-------------|
| `recognizeProduct(Bitmap bitmap)` | Recognize product from image |
| `recognizeFruit(Bitmap bitmap)` | Recognize fruit |
| `recognizeVegetable(Bitmap bitmap)` | Recognize vegetable |

#### Usage

```java
ProductRecognition productRecognition = new ProductRecognition(context);
productRecognition.init();

// Recognize product
ProductResult result = productRecognition.recognizeProduct(bitmap);
String productName = result.getName();
float confidence = result.getConfidence();
```

### Food Recognition V2.0 SDK Interface Documentation

Similar to product recognition, specialized for food items.

---

## 4. Barcode Reader

### Barcode Reader Professional SDK 2.0

SUNMI Barcode Reader SDK provides professional barcode scanning capabilities using the device camera.

#### Integration

```gradle
dependencies {
    implementation 'com.sunmi:barcodereader:2.0.0'
}
```

#### Key APIs

| API | Description |
|-----|-------------|
| `startScan()` | Start barcode scanning |
| `stopScan()` | Stop barcode scanning |
| `scanFromBitmap(Bitmap bitmap)` | Scan barcode from image |
| `getSupportedFormats()` | Get supported barcode formats |

#### Supported Formats

- QR Code
- Data Matrix
- PDF417
- UPC-A
- UPC-E
- EAN-13
- EAN-8
- Code 128
- Code 39
- ITF
- Codabar

#### Usage

```java
BarcodeReader barcodeReader = new BarcodeReader(context);
barcodeReader.init();

// Start scanning
barcodeReader.startScan(new ScanCallback() {
    @Override
    public void onScanResult(String barcode, int format) {
        // Handle scanned barcode
    }
});

// Stop scanning
barcodeReader.stopScan();
```

---

## Related Documentation

- [Scanning Development](./07-SCANNING.md)
- [SunmiCustomer API](./03-SUNMI-CUSTOMER-API.md)
