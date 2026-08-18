# Electronic Scale Development

> **Section:** Integration Guide > Electronic Scale Development
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Overview

SUNMI supports electronic scale integration for retail and grocery applications. The electronic scale can be connected via serial port (RJ11) or USB.

---

## Supported Models

- SUNMI S2 Series
- SUNMI S2L
- Third-party scales (with appropriate drivers)

---

## Integration

### Gradle Dependency

```gradle
dependencies {
    implementation 'com.sunmi:scale:1.0.0'
}
```

### JAR Package

For S2 series electronic scales, use the encapsulated JAR package: `scale-service-lib.jar`

---

## Key APIs

| API | Description |
|-----|-------------|
| `initScale()` | Initialize electronic scale |
| `getWeight()` | Get current weight |
| `getUnitPrice()` | Get unit price |
| `getTotalPrice()` | Get total price |
| `tare()` | Set tare weight |
| `zero()` | Zero the scale |
| `setUnitPrice(double price)` | Set unit price |
| `setStableCallback(StableCallback callback)` | Set stable weight callback |
| `release()` | Release resources |

---

## Usage

### Basic Weight Reading

```java
ElectronicScale scale = new ElectronicScale(context);
scale.initScale();

// Get weight
double weight = scale.getWeight();
Log.d("Scale", "Weight: " + weight + " kg");

// Get unit price
double unitPrice = scale.getUnitPrice();
Log.d("Scale", "Unit Price: " + unitPrice);

// Get total price
double totalPrice = scale.getTotalPrice();
Log.d("Scale", "Total Price: " + totalPrice);
```

### Stable Weight Detection

```java
scale.setStableCallback(new StableCallback() {
    @Override
    public void onWeightStable(double weight) {
        // Weight is stable, safe to read
        runOnUiThread(() -> {
            weightTextView.setText(String.format("%.2f kg", weight));
        });
    }
    
    @Override
    public void onWeightUnstable() {
        // Weight is changing
        runOnUiThread(() -> {
            weightTextView.setText("Waiting...");
        });
    }
});
```

### Serial Port Communication

For scales connected via RJ11 serial port:

```java
SerialPort serialPort = new SerialPort("/dev/sunmi/pub/serial", 9600);

serialPort.setDataCallback(new DataCallback() {
    @Override
    public void onDataReceived(byte[] data) {
        // Parse scale data
        ScaleData scaleData = ScaleParser.parse(data);
        double weight = scaleData.getWeight();
    }
});

serialPort.open();
```

### USB Scale Communication

For scales connected via USB:

```java
UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
UsbDevice usbDevice = usbManager.getDeviceList().values().iterator().next();

UsbScaleDriver driver = new UsbScaleDriver(usbManager, usbDevice);
driver.open();

driver.setWeightCallback(new WeightCallback() {
    @Override
    public void onWeightUpdate(double weight) {
        // Handle weight update
    }
});
```

---

## Protocol Reference

### SUNMI Scale Protocol

| Byte | Description |
|------|-------------|
| 0x02 | Start byte |
| 0xXX | Command type |
| 0xXX - 0xXX | Weight data (ASCII) |
| 0xXX | Checksum |
| 0x0D | End byte |

### Weight Data Format

```
Weight = (Byte1 - 0x30) * 100 + (Byte2 - 0x30) * 10 + (Byte3 - 0x30) * 0.1
```

---

## Configuration

### Scale Settings

| Setting | Description |
|---------|-------------|
| Baud Rate | 9600 (default) |
| Data Bits | 8 |
| Stop Bits | 1 |
| Parity | None |

### Unit Settings

| Unit | Description |
|------|-------------|
| kg | Kilogram (default) |
| g | Gram |
| lb | Pound |
| oz | Ounce |

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Scale not detected | Check connection cable and power |
| Weight not stable | Ensure scale is on flat surface |
| Incorrect weight | Calibrate the scale |
| Communication error | Check baud rate and serial port settings |

---

## Related Documentation

- [General Interface Description](./02-DEVELOPMENT-GUIDE.md#general-interface-description)
- [Printer Development](./04-PRINTER-DEVELOPMENT.md)
