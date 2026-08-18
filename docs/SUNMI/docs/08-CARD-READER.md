# Card Reader Development

> **Section:** Integration Guide > Card Reader Development
> **Source:** https://docs.sunmi.com/en-US/cdixeghjk491/

---

## Table of Contents

1. [NFC Related SDK Guide](#1-nfc-related-sdk-guide)
2. [Magnetic Stripe Reader Service Guide](#2-magnetic-stripe-reader-service-guide)
3. [PSAM, ETC, M112 Card Reader Development](#3-psam-etc-m112-card-reader-development)
4. [UHF RFID](#4-uhf-rfid)

---

## 1. NFC Related SDK Guide

> **URL:** https://docs.sunmi.com/en-US/cdixeghjk491/xmaqeghjk513
> **Update Time:** 2026-03-20 18:58:42

### Overview

SUNMI provides switching control for under-screen NFC and external NFC modules of devices such as FLEX 3, as well as under-screen NFC watermark control.

### SDK Import

Use the same SDK and import method as `StatusLightService`.

### API Reference

#### 1. Initialize NFC

Initialize in the Activity onCreate life cycle and turn on the device.

```java
NfcManager.init(this) { success ->
    // NFC initialized
}
```

#### 2. Register NFC Module Listener

Register a callback that monitors NFC module changes to obtain the list of NFC modules available for the current device in real time.

```java
NfcControlManager.registerNfcListener(object : INfcListener.Stub() {
    override fun onNfcListChanged(nfcList: MutableList<Nfc>?) {
        // Handle NFC module list changes
        nfcList?.forEach { nfc ->
            Log.d("NFC", "Module SN: ${nfc.sn}")
        }
    }
})
```

#### 3. Switch NFC Module

Specify to switch to the corresponding NFC module by using the SN of the NFC module obtained in the NFC list.

```java
NfcControlManager.switchNfc(nfcList!![0].sn)
```

#### 4. Set NFC Watermark

Actively set the watermark transparency of the current NFC module, ranging from 0 to 100.

```java
NfcControlManager.setNfcWaterMarkAlpha(100)
```

#### 5. Unregister NFC Listener

Registration can be ended when you do not need to use and focus on the NFC module.

```java
NfcControlManager.unregisterNfcListener(/* registered anonymous listener */)
```

#### 6. Destroy SDK

You can also directly destroy the SDK to end the operation.

```java
NfcControlManager.destroy(this)
```

### API Summary Table

| Method | Description | Parameters | Return Type |
|--------|-------------|------------|-------------|
| `NfcManager.init(this) { success -> }` | Initialize NFC SDK in Activity onCreate lifecycle | `this` (Activity context) | Callback with `success` boolean |
| `NfcControlManager.registerNfcListener(INfcListener.Stub)` | Register listener to monitor NFC module changes | `INfcListener.Stub` - anonymous listener with `onNfcListChanged` callback | Void (callback-based) |
| `NfcControlManager.switchNfc(sn)` | Switch to specific NFC module using its SN | `sn` - String, serial number of target NFC module | Void |
| `NfcControlManager.setNfcWaterMarkAlpha(alpha)` | Set watermark transparency of current NFC module | `alpha` - Int, range 0-100 | Void |
| `NfcControlManager.unregisterNfcListener(listener)` | Unregister NFC module change listener | The previously registered anonymous listener object | Void |
| `NfcControlManager.destroy(this)` | Destroy SDK and end all operations | `this` (Activity context) | Void |

### Key Classes and Interfaces

| Class/Interface | Purpose |
|-----------------|---------|
| `NfcManager` | Entry point class for NFC SDK initialization |
| `NfcControlManager` | Main control manager for NFC module operations |
| `INfcListener.Stub` | AIDL interface for receiving NFC module change notifications |
| `Nfc` | Data model representing an NFC module (contains `.sn` property) |

### Integration Notes

- **Device Support:** SDK supports devices such as FLEX 3 that have under-screen NFC and/or external NFC modules
- **Watermark Control:** Watermark transparency is configurable (0 = transparent, 100 = fully opaque)
- **Module Switching:** NFC modules are identified by their SN (serial number)
- **Lifecycle:** Initialization should happen in Activity `onCreate`
- **Cleanup:** SDK can be destroyed at any time using `NfcControlManager.destroy(this)`

### Full Example

```java
public class NfcActivity extends AppCompatActivity {
    
    private INfcListener.Stub nfcListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        
        // Initialize NFC
        NfcManager.init(this, success -> {
            if (success) {
                Log.d("NFC", "NFC initialized successfully");
            }
        });
        
        // Register listener
        nfcListener = new INfcListener.Stub() {
            @Override
            public void onNfcListChanged(List<Nfc> nfcList) {
                runOnUiThread(() -> {
                    if (nfcList != null && !nfcList.isEmpty()) {
                        // Switch to first NFC module
                        NfcControlManager.switchNfc(nfcList.get(0).getSn());
                        
                        // Set watermark
                        NfcControlManager.setNfcWaterMarkAlpha(50);
                    }
                });
            }
        };
        NfcControlManager.registerNfcListener(nfcListener);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup
        NfcControlManager.unregisterNfcListener(nfcListener);
        NfcControlManager.destroy(this);
    }
}
```

### Usage

```java
NfcManager nfcManager = new NfcManager(context);
nfcManager.initNFC();

if (nfcManager.isNFCEnabled()) {
    nfcManager.startDiscovery();
}

// In Activity - handle discovered tag
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    if (tag != null) {
        byte[] data = nfcManager.readTag(tag);
    }
}
```

### AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.NFC" />

<uses-feature
    android:name="android.hardware.nfc"
    android:required="true" />

<activity
    android:name=".NfcActivity"
    android:launchMode="singleTop">
    <intent-filter>
        <action android:name="android.nfc.action.NDEF_DISCOVERED" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

---

## 2. Magnetic Stripe Reader Service Guide

### Overview

SUNMI devices support magnetic stripe card reading for payment cards and access cards.

### Features

- Read Track 1, Track 2, Track 3
- Card data encryption
- Multiple card format support

### Integration

```gradle
dependencies {
    implementation 'com.sunmi:magneticreader:1.0.0'
}
```

### Key APIs

| API | Description |
|-----|-------------|
| `initReader()` | Initialize magnetic reader |
| `startReading()` | Start card reading |
| `stopReading()` | Stop card reading |
| `getTrackData()` | Get track data |
| `getCardNumber()` | Get card number |
| `getExpirationDate()` | Get expiration date |
| `getCardholderName()` | Get cardholder name |

### Usage

```java
MagneticReader reader = new MagneticReader(context);
reader.initReader();

reader.startReading(new CardCallback() {
    @Override
    public void onCardRead(CardData cardData) {
        String cardNumber = cardData.getCardNumber();
        String expiration = cardData.getExpirationDate();
        String track1 = cardData.getTrack1();
        String track2 = cardData.getTrack2();
    }
});
```

---

## 3. PSAM, ETC, M112 Card Reader Development

### PSAM Card Reader

PSAM (Payment Security Access Module) cards are used for secure payment transactions.

#### Integration

```java
PsamReader psamReader = new PsamReader(context);
psamReader.init();

// Read PSAM card
PsamData psamData = psamReader.readCard();
String cardId = psamData.getCardId();
```

### ETC Card Reader

ETC (Electronic Toll Collection) cards are used for highway toll payments.

#### Integration

```java
EtcReader etcReader = new EtcReader(context);
etcReader.init();

// Read ETC card
EtcData etcData = etcReader.readCard();
String vehicleId = etcData.getVehicleId();
```

### M112 Card Reader

M112 is a specific card reader model supported by SUNMI devices.

---

## 4. UHF RFID

### RFID SDK Integration Guide

SUNMI supports UHF (Ultra High Frequency) RFID for inventory tracking and asset management.

#### Features

- Read RFID tags (EPC, TID, User Data)
- Write RFID tags
- Inventory scan
- Tag filtering

#### Integration

```gradle
dependencies {
    implementation 'com.sunmi:rfid:1.0.0'
}
```

#### Key APIs

| API | Description |
|-----|-------------|
| `initRFID()` | Initialize RFID reader |
| `startInventory()` | Start inventory scan |
| `stopInventory()` | Stop inventory scan |
| `readTag(String epc)` | Read tag data |
| `writeTag(String epc, byte[] data)` | Write tag data |
| `killTag(String epc)` | Kill tag |
| `setPower(int power)` | Set reader power (0-30 dBm) |
| `getPower()` | Get current power |

#### Usage

```java
RfidReader rfidReader = new RfidReader(context);
rfidReader.initRFID();

// Set power
rfidReader.setPower(26);

// Start inventory
rfidReader.startInventory(new InventoryCallback() {
    @Override
    public void onTagFound(String epc, int rssi, String tid) {
        // Handle found tag
    }
});

// Stop inventory
rfidReader.stopInventory();
```

### RFID Uniapp Plugin User Guide

```javascript
import rfid from '@sunmi/rfid-plugin';

// Initialize
rfid.init();

// Start inventory
rfid.startInventory({
  success: (data) => {
    console.log('Tag found:', data.epc);
  }
});

// Stop inventory
rfid.stopInventory();
```

---

## Related Documentation

- [Scanning Development](./07-SCANNING.md)
- [Payment Sound Box](./12-PAYMENT-SOUND-BOX.md)
