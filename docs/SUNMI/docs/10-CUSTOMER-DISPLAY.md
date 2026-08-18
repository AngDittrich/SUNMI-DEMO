# Customer Display Development

> **Section:** Integration Guide > Customer Display Development
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Table of Contents

1. [Secondary Display API Documentation](#1-secondary-display-api-documentation)
2. [T2 Mini Customer Display](#2-t2-mini-customer-display)
3. [T1 Vice Screen](#3-t1-vice-screen)
4. [ClientView Open Source Display Solution](#4-clientview-open-source-display-solution)

---

## 1. Secondary Display API Documentation

### Overview

SUNMI devices with dual screens support displaying different content on the primary and secondary displays.

### Features

- Display different content on each screen
- Touch support on secondary screen (15.6 inch models)
- Presentation class for dual screen management

### Integration

```gradle
dependencies {
    implementation 'com.sunmi:display:1.0.0'
}
```

### Key APIs

| API | Description |
|-----|-------------|
| `getSecondaryDisplay()` | Get secondary display |
| `setSecondaryContent(View view)` | Set content for secondary display |
| `showSecondaryDisplay()` | Show secondary display |
| `hideSecondaryDisplay()` | Hide secondary display |
| `isSecondaryDisplayAvailable()` | Check if secondary display is available |

### Usage

```java
// Get secondary display manager
DisplayManager displayManager = new DisplayManager(context);

// Check if secondary display is available
if (displayManager.isSecondaryDisplayAvailable()) {
    // Create content for secondary display
    View secondaryView = LayoutInflater.from(context)
        .inflate(R.layout.secondary_display_layout, null);
    
    // Set content
    displayManager.setSecondaryContent(secondaryView);
    
    // Show secondary display
    displayManager.showSecondaryDisplay();
}
```

### Using Presentation Class

```java
public class SecondaryPresentation extends Presentation {
    
    public SecondaryPresentation(Context context, Display display) {
        super(context, display);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondary_layout);
    }
}

// Usage
DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
Display[] displays = displayManager.getDisplays();

if (displays.length > 1) {
    SecondaryPresentation presentation = new SecondaryPresentation(context, displays[1]);
    presentation.show();
}
```

---

## 2. T2 Mini Customer Display

### Overview

The T2 Mini is a compact customer display that connects to SUNMI POS devices.

### Features

- 7-inch LCD display
- USB connection
- Low power consumption
- VESA mount compatible

### Integration

```java
T2MiniDisplay t2Mini = new T2MiniDisplay(context);
t2Mini.init();

// Display content
t2Mini.showText("Total: $100.00");
t2Mini.showBarcode("123456789");
```

### Configuration

| Setting | Description |
|---------|-------------|
| Brightness | Adjust display brightness |
| Orientation | Portrait/Landscape |
| Font Size | Adjust text size |
| Timeout | Display timeout settings |

---

## 3. T1 Vice Screen

### Overview

The T1 Vice Screen is a secondary display for SUNMI T1 devices only.

### Debugging Instructions

1. Connect the T1 Vice Screen to the T1 device
2. Enable developer mode
3. Enable USB debugging
4. Use ADB to install the display driver

### T1 Built-in Vice Screen Display App

SUNMI provides a built-in app for the T1 Vice Screen:

1. Open the Settings app
2. Navigate to Display > Vice Screen
3. Configure the display settings
4. Launch the Vice Screen app

### T1 Custom Vice Screen Display App

To create a custom app for the T1 Vice Screen:

```java
public class CustomViceScreenActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vice_screen_layout);
        
        // Configure display
        DisplayManager displayManager = (DisplayManager) 
            getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();
        
        if (displays.length > 1) {
            // Use secondary display
            setDisplay(displays[1]);
        }
    }
}
```

### T1 Dual Screen Communication Interface

The T1 device supports communication between the primary and secondary screens:

```java
// Send data from primary to secondary
DualScreenManager.sendData("update_display", data);

// Receive data on secondary
DualScreenManager.setReceiver(new DualScreenReceiver() {
    @Override
    public void onDataReceived(String action, Bundle data) {
        // Handle received data
    }
});
```

---

## 4. ClientView Open Source Display Solution

### Overview

ClientView is an open-source solution for managing customer displays on SUNMI devices.

### Features

- Cross-platform support
- Customizable UI components
- Real-time updates
- Network connectivity

### GitHub Repository

https://github.com/nicennnnnnnlee/sunmi_clientview

### Integration

```gradle
dependencies {
    implementation 'com.sunmi:clientview:1.0.0'
}
```

### Key APIs

| API | Description |
|-----|-------------|
| `init(Context context)` | Initialize ClientView |
| `showView(View view)` | Show custom view |
| `showText(String text)` | Show text |
| `showImage(Bitmap bitmap)` | Show image |
| `showBarcode(String data)` | Show barcode |
| `showQRCode(String data)` | Show QR code |
| `clear()` | Clear display |
| `setCallback(ClientViewCallback callback)` | Set callback |

### Usage

```java
ClientView clientView = new ClientView();
clientView.init(context);

// Show text
clientView.showText("Welcome to our store!");

// Show barcode
clientView.showBarcode("123456789");

// Show QR code
clientView.showQRCode("https://example.com");

// Show custom view
View customView = LayoutInflater.from(context)
    .inflate(R.layout.custom_display, null);
clientView.showView(customView);
```

---

## Related Documentation

- [Printer Development](./04-PRINTER-DEVELOPMENT.md)
- [Payment Sound Box](./12-PAYMENT-SOUND-BOX.md)
