# Payment Sound Box Development

> **Section:** Integration Guide > Payment Sound Box Development
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Table of Contents

1. [Integrating the Soundbox with Your Device](#1-integrating-the-soundbox-with-your-device)
2. [Enable Sound Box Integration Capability](#2-enable-sound-box-integration-capability)
3. [Integrating with the Soundbox API](#3-integrating-with-the-soundbox-api)
4. [Soundbox API Callbacks](#4-soundbox-api-callbacks)
5. [Remote Management of the Sound Box](#5-remote-management-of-the-sound-box)
6. [Sound Box Demo for Android](#6-sound-box-demo-for-android)
7. [NFC Card Issuing Tool Use Guide](#7-nfc-card-issuing-tool-use-guide)

---

## 1. Integrating the Soundbox with Your Device

### Overview

The SUNMI Sound Box is a payment notification device that provides audio alerts for successful transactions. It connects to SUNMI POS devices via Bluetooth or network.

### Features

- Real-time payment notifications
- Customizable voice prompts
- Multi-language support
- Transaction history playback

### Hardware Requirements

- SUNMI POS device (T3, D3, V3 series)
- SUNMI Sound Box (S1, S2 models)
- Bluetooth or network connection

---

## 2. Enable Sound Box Integration Capability

### Prerequisites

1. Register on SUNMI Partner Platform
2. Enable Sound Box capability for your app
3. Obtain API credentials

### Steps

1. Log in to [SUNMI Partner Platform](https://partner.sunmi.com)
2. Navigate to Development > Apps Integration
3. Create a new app or select existing app
4. Add "Sound Box" capability
5. Configure settings and save

---

## 3. Integrating with the Soundbox API

### Integration

```gradle
dependencies {
    implementation 'com.sunmi:soundbox:1.0.0'
}
```

### Key APIs

| API | Description |
|-----|-------------|
| `initSoundBox()` | Initialize sound box |
| `connect(String deviceId)` | Connect to sound box |
| `disconnect()` | Disconnect from sound box |
| `isConnected()` | Check connection status |
| `playPaymentSound(double amount)` | Play payment notification |
| `playCustomSound(String soundId)` | Play custom sound |
| `setVolume(int volume)` | Set volume (0-100) |
| `getVolume()` | Get current volume |
| `setLanguage(String language)` | Set language |
| `getTransactionHistory()` | Get transaction history |
| `playTransactionHistory(List transactions)` | Play transaction history |

### Usage

```java
SoundBox soundBox = new SoundBox(context);
soundBox.initSoundBox();

// Connect to sound box
soundBox.connect("SUNMI_SOUND_BOX_001", new ConnectCallback() {
    @Override
    public void onConnected() {
        Log.d("SoundBox", "Connected successfully");
    }
    
    @Override
    public void onDisconnected() {
        Log.d("SoundBox", "Disconnected");
    }
    
    @Override
    public void onError(int errorCode, String message) {
        Log.e("SoundBox", "Error: " + message);
    }
});

// Play payment notification
soundBox.playPaymentSound(100.00);

// Set volume
soundBox.setVolume(80);
```

---

## 4. Soundbox API Callbacks

### Callback Interface

```java
public interface SoundBoxCallback {
    void onConnected();
    void onDisconnected();
    void onPaymentReceived(PaymentData data);
    void onSoundPlayed(String soundId);
    void onError(int errorCode, String message);
}
```

### Setting Callback

```java
soundBox.setCallback(new SoundBoxCallback() {
    @Override
    public void onConnected() {
        // Handle connection
    }
    
    @Override
    public void onDisconnected() {
        // Handle disconnection
    }
    
    @Override
    public void onPaymentReceived(PaymentData data) {
        // Handle payment notification
        double amount = data.getAmount();
        String orderId = data.getOrderId();
        String paymentMethod = data.getPaymentMethod();
    }
    
    @Override
    public void onSoundPlayed(String soundId) {
        // Handle sound played
    }
    
    @Override
    public void onError(int errorCode, String message) {
        // Handle error
    }
});
```

### Payment Data

```java
public class PaymentData {
    private double amount;
    private String orderId;
    private String paymentMethod;
    private long timestamp;
    
    // Getters and setters
    public double getAmount() { return amount; }
    public String getOrderId() { return orderId; }
    public String getPaymentMethod() { return paymentMethod; }
    public long getTimestamp() { return timestamp; }
}
```

---

## 5. Remote Management of the Sound Box

### Features

- Remote volume control
- Remote language settings
- Remote sound updates
- Firmware updates

### Remote Management API

```java
// Remote volume control
SoundBoxManager remoteManager = new SoundBoxManager();
remoteManager.setVolume("DEVICE_ID", 50);

// Remote language setting
remoteManager.setLanguage("DEVICE_ID", "en_US");

// Remote firmware update
remoteManager.updateFirmware("DEVICE_ID", "firmware_url");
```

---

## 6. Sound Box Demo for Android

### Download

Download the Sound Box Demo APK from the SUNMI Developer Portal.

### Features

- Device discovery and pairing
- Payment notification testing
- Volume and language settings
- Transaction history playback

### Source Code

```java
public class SoundBoxDemoActivity extends AppCompatActivity {
    
    private SoundBox soundBox;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundbox_demo);
        
        soundBox = new SoundBox(this);
        soundBox.initSoundBox();
        
        // Discover devices
        soundBox.discoverDevices(new DeviceDiscoveryCallback() {
            @Override
            public void onDeviceFound(SoundBoxDevice device) {
                // Show device in list
                adapter.addDevice(device);
            }
            
            @Override
            public void onDiscoveryComplete() {
                // Discovery finished
            }
        });
    }
    
    public void onConnectClick(View view) {
        SoundBoxDevice selectedDevice = adapter.getSelectedDevice();
        soundBox.connect(selectedDevice.getId(), new ConnectCallback() {
            @Override
            public void onConnected() {
                Toast.makeText(SoundBoxDemoActivity.this, 
                    "Connected!", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onDisconnected() {
                Toast.makeText(SoundBoxDemoActivity.this, 
                    "Disconnected", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onError(int errorCode, String message) {
                Toast.makeText(SoundBoxDemoActivity.this, 
                    "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void onTestPaymentClick(View view) {
        soundBox.playPaymentSound(99.99);
    }
}
```

---

## 7. NFC Card Issuing Tool Use Guide

### Overview

The NFC Card Issuing Tool allows you to issue NFC cards for payment and access control using SUNMI devices.

### Features

- Read NFC card information
- Write data to NFC cards
- Issue new cards
- Manage card database

### Usage

```java
NfcCardTool nfcTool = new NfcCardTool(context);

// Read card
nfcTool.readCard(new CardReadCallback() {
    @Override
    public void onCardRead(NfcCard card) {
        String cardId = card.getCardId();
        String cardType = card.getType();
        // Handle card data
    }
});

// Write to card
NfcCard newCard = new NfcCard();
newCard.setCardId("CARD_001");
newCard.setType("PAYMENT");
newCard.setBalance(100.00);

nfcTool.writeCard(newCard, new CardWriteCallback() {
    @Override
    public void onCardWritten() {
        // Card written successfully
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

---

## Related Documentation

- [Card Reader Development](./08-CARD-READER.md)
- [Customer Display Development](./10-CUSTOMER-DISPLAY.md)
