# capacitor-ibeacon
  <a href="https://capgo.app/"><img src='https://raw.githubusercontent.com/Cap-go/capgo/main/assets/capgo_banner.png' alt='Capgo - Instant updates for capacitor'/></a>

<div align="center">
  <h2><a href="https://capgo.app/?ref=plugin_ibeacon"> ➡️ Get Instant updates for your App with Capgo</a></h2>
  <h2><a href="https://capgo.app/consulting/?ref=plugin_ibeacon"> Missing a feature? We'll build the plugin for you 💪</a></h2>
</div>

iBeacon plugin for Capacitor - proximity detection and beacon region monitoring.

## Documentation

The most complete doc is available here: https://capgo.app/docs/plugins/ibeacon/

## Compatibility

| Plugin version | Capacitor compatibility | Maintained |
| -------------- | ----------------------- | ---------- |
| v8.\*.\*       | v8.\*.\*                | ✅          |
| v7.\*.\*       | v7.\*.\*                | On demand   |
| v6.\*.\*       | v6.\*.\*                | ❌          |
| v5.\*.\*       | v5.\*.\*                | ❌          |

> **Note:** The major version of this plugin follows the major version of Capacitor. Use the version that matches your Capacitor installation (e.g., plugin v8 for Capacitor 8). Only the latest major version is actively maintained.

## Install

```bash
npm install @capgo/capacitor-ibeacon
npx cap sync
```

## Configuration

### iOS

#### Step 1: Configure Info.plist

Add the following keys to your `ios/App/App/Info.plist` file:

```xml
<!-- Required: Location permission for beacon detection -->
<key>NSLocationWhenInUseUsageDescription</key>
<string>This app needs location access to detect nearby beacons</string>

<!-- Required for background monitoring: Always authorization -->
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>This app needs location access to monitor beacons in the background</string>

<!-- Required: Bluetooth usage (iOS 13+) -->
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app uses Bluetooth to detect nearby beacons</string>

<!-- Required for iOS 12 and earlier -->
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app uses Bluetooth to detect and advertise as beacons</string>

<!-- Required for background beacon monitoring -->
<key>UIBackgroundModes</key>
<array>
  <string>location</string>
  <string>bluetooth-central</string>
</array>
```

#### Step 2: Enable Capabilities in Xcode

1. Open your project in Xcode: `ios/App/App.xcworkspace`
2. Select your app target
3. Go to the "Signing & Capabilities" tab
4. Click "+ Capability" and add "Background Modes"
5. Check the following options:
   - ✅ **Location updates** (required for background beacon monitoring)
   - ✅ **Uses Bluetooth LE accessories** (required for beacon detection)

#### Permissions Required

- **When In Use**: For foreground beacon ranging and monitoring
- **Always**: Required for background beacon monitoring (detecting beacons when app is not active)
- **Bluetooth**: Automatically granted on iOS 13+ when using CoreLocation for beacons

> **Note**: The plugin uses CoreLocation for beacon detection, which handles Bluetooth scanning internally. Direct Bluetooth permissions are only needed if your app also uses CoreBluetooth for other purposes (e.g., advertising as a beacon).

### Android

The plugin automatically includes all required permissions and dependencies. No manual configuration needed.

**Permissions included:**
- Location permissions (fine, coarse, background)
- Bluetooth permissions (with proper legacy support for Android ≤11 and modern permissions for Android 12+)
- Foreground service permissions for background beacon scanning
- Boot completed permission for persistent monitoring

**Dependencies included:**
- AltBeacon Android Beacon Library (2.21.2)

**Important:** You still need to request permissions at runtime using the plugin's authorization methods (see API section below).

## API

All methods are available through the `CapacitorIbeacon` object:

```typescript
import { CapacitorIbeacon } from '@capgo/capacitor-ibeacon';
```

### startMonitoringForRegion(options: BeaconRegion)

Start monitoring for a beacon region. Triggers events when entering/exiting the region.

```typescript
await CapacitorIbeacon.startMonitoringForRegion({
  identifier: 'MyBeaconRegion',
  uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D',
  major: 1,
  minor: 2
});
```

### stopMonitoringForRegion(options: BeaconRegion)

Stop monitoring for a beacon region.

```typescript
await CapacitorIbeacon.stopMonitoringForRegion({
  identifier: 'MyBeaconRegion',
  uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D'
});
```

### startRangingBeaconsInRegion(options: BeaconRegion)

Start ranging beacons in a region. Provides continuous distance updates.

```typescript
await CapacitorIbeacon.startRangingBeaconsInRegion({
  identifier: 'MyBeaconRegion',
  uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D'
});
```

### stopRangingBeaconsInRegion(options: BeaconRegion)

Stop ranging beacons in a region.

```typescript
await CapacitorIbeacon.stopRangingBeaconsInRegion({
  identifier: 'MyBeaconRegion',
  uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D'
});
```

### startAdvertising(options: BeaconAdvertisingOptions)

Start advertising the device as an iBeacon (iOS only).

```typescript
await CapacitorIbeacon.startAdvertising({
  uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D',
  major: 1,
  minor: 2,
  identifier: 'MyBeacon'
});
```

### stopAdvertising()

Stop advertising the device as an iBeacon (iOS only).

```typescript
await CapacitorIbeacon.stopAdvertising();
```

### requestWhenInUseAuthorization()

Request "When In Use" location authorization.

```typescript
const { status } = await CapacitorIbeacon.requestWhenInUseAuthorization();
console.log('Authorization status:', status);
```

### requestAlwaysAuthorization()

Request "Always" location authorization (required for background monitoring).

```typescript
const { status } = await CapacitorIbeacon.requestAlwaysAuthorization();
console.log('Authorization status:', status);
```

### getAuthorizationStatus()

Get current location authorization status.

```typescript
const { status } = await CapacitorIbeacon.getAuthorizationStatus();
// status: 'not_determined' | 'restricted' | 'denied' | 'authorized_always' | 'authorized_when_in_use'
```

### isBluetoothEnabled()

Check if Bluetooth is enabled on the device.

```typescript
const { enabled } = await CapacitorIbeacon.isBluetoothEnabled();
if (!enabled) {
  console.log('Please enable Bluetooth');
}
```

### isRangingAvailable()

Check if ranging is available on the device.

```typescript
const { available } = await CapacitorIbeacon.isRangingAvailable();
```

### enableARMAFilter(options: { enabled: boolean })

Enable ARMA filtering for distance calculations (Android only).

```typescript
await CapacitorIbeacon.enableARMAFilter({ enabled: true });
```

### Android background scanning

Android 8+ requires a foreground service to keep Bluetooth scanning alive in the background. You can opt in once and the plugin will automatically
toggle background scanning when the app moves between foreground and background.

```typescript
// Enable background scanning globally (Android only)
await CapacitorIbeacon.enableBackgroundMode({ enabled: true });

// Or opt in per call
await CapacitorIbeacon.startMonitoringForRegion({
  identifier: 'MyBeaconRegion',
  uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D',
  enableBackgroundMode: true,
});
```

You can also enable it via Capacitor config:

```typescript
// capacitor.config.ts
plugins: {
  CapacitorIbeacon: {
    enableBackgroundMode: true,
  },
}
```

## Events

Listen to beacon events using Capacitor's event system:

```typescript
import { CapacitorIbeacon } from '@capgo/capacitor-ibeacon';
import { PluginListenerHandle } from '@capacitor/core';

// Listen for ranging events
const rangingListener: PluginListenerHandle = await CapacitorIbeacon.addListener(
  'didRangeBeacons',
  (data) => {
    console.log('Beacons detected:', data.beacons);
    data.beacons.forEach(beacon => {
      console.log(`Beacon: ${beacon.uuid}, Distance: ${beacon.accuracy}m, Proximity: ${beacon.proximity}`);
    });
  }
);

// Listen for region enter events
const enterListener: PluginListenerHandle = await CapacitorIbeacon.addListener(
  'didEnterRegion',
  (data) => {
    console.log('Entered region:', data.region.identifier);
  }
);

// Listen for region exit events
const exitListener: PluginListenerHandle = await CapacitorIbeacon.addListener(
  'didExitRegion',
  (data) => {
    console.log('Exited region:', data.region.identifier);
  }
);

// Listen for region state changes
const stateListener: PluginListenerHandle = await CapacitorIbeacon.addListener(
  'didDetermineStateForRegion',
  (data) => {
    console.log(`Region ${data.region.identifier}: ${data.state}`);
  }
);

// Clean up listeners when done
rangingListener.remove();
enterListener.remove();
exitListener.remove();
stateListener.remove();
```

## Complete Example

```typescript
import { CapacitorIbeacon } from '@capgo/capacitor-ibeacon';

// Define your beacon region
const beaconRegion = {
  identifier: 'MyStore',
  uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D',
  major: 1
};

async function setupBeaconMonitoring() {
  try {
    // Request permission
    const { status } = await CapacitorIbeacon.requestWhenInUseAuthorization();
    
    if (status !== 'authorized_when_in_use' && status !== 'authorized_always') {
      console.error('Location permission denied');
      return;
    }

    // Check if Bluetooth is enabled
    const { enabled } = await CapacitorIbeacon.isBluetoothEnabled();
    if (!enabled) {
      console.error('Bluetooth is not enabled');
      return;
    }

    // Set up event listeners
    await CapacitorIbeacon.addListener('didEnterRegion', (data) => {
      console.log('Welcome! You entered:', data.region.identifier);
      // Show welcome notification or trigger action
    });

    await CapacitorIbeacon.addListener('didExitRegion', (data) => {
      console.log('Goodbye! You left:', data.region.identifier);
    });

    await CapacitorIbeacon.addListener('didRangeBeacons', (data) => {
      data.beacons.forEach(beacon => {
        console.log(`Beacon ${beacon.minor}: ${beacon.proximity} (${beacon.accuracy.toFixed(2)}m)`);
      });
    });

    // Start monitoring
    await CapacitorIbeacon.startMonitoringForRegion(beaconRegion);
    console.log('Started monitoring for beacons');

    // Start ranging for distance updates
    await CapacitorIbeacon.startRangingBeaconsInRegion(beaconRegion);
    console.log('Started ranging beacons');

  } catch (error) {
    console.error('Error setting up beacon monitoring:', error);
  }
}

// Call the setup function
setupBeaconMonitoring();
```

## TypeScript Types

### BeaconRegion

```typescript
interface BeaconRegion {
  identifier: string;
  uuid: string;
  major?: number;
  minor?: number;
  notifyEntryStateOnDisplay?: boolean;
}
```

### Beacon

```typescript
interface Beacon {
  uuid: string;
  major: number;
  minor: number;
  rssi: number;
  proximity: 'immediate' | 'near' | 'far' | 'unknown';
  accuracy: number;
}
```

### BeaconAdvertisingOptions

```typescript
interface BeaconAdvertisingOptions {
  uuid: string;
  major: number;
  minor: number;
  identifier: string;
  measuredPower?: number;
}
```

## Proximity Values

- **immediate**: Very close to the beacon (within a few centimeters)
- **near**: Relatively close to the beacon (within a couple of meters)
- **far**: Further away from the beacon (10+ meters)
- **unknown**: Distance cannot be determined

## Important Notes

1. **iOS Background Monitoring**: To monitor beacons in the background, you need:
   - "Always" location authorization (`requestAlwaysAuthorization()`)
   - Background Modes capability enabled in Xcode
   - `UIBackgroundModes` with both `location` and `bluetooth-central` in Info.plist

2. **iOS Xcode Configuration**: Adding Info.plist keys alone is not enough. You **must** enable Background Modes capability in Xcode (see Configuration section above). This is the most common setup issue.

3. **Android Library**: This plugin includes the AltBeacon Android Beacon Library (2.21.2) automatically.

4. **Battery Usage**: Continuous beacon ranging can consume significant battery. Consider using monitoring (which is more battery-efficient) and only start ranging when needed.

5. **UUID Format**: UUIDs must be in the standard format: `XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX`

6. **Major/Minor Values**: These are optional 16-bit unsigned integers (0-65535) used to identify specific beacons within a UUID.

7. **Permissions**: Always request and check permissions before starting beacon operations.

## Troubleshooting

### iOS Issues

**"Beacons not detected" or "Plugin not working"**

1. **Verify Info.plist configuration**:
   - Ensure all required keys are present (see Configuration section above)
   - Check that permission descriptions are meaningful and not empty

2. **Enable Background Modes in Xcode**:
   - This is a common issue - you MUST enable capabilities in Xcode, not just add Info.plist keys
   - Open `ios/App/App.xcworkspace` in Xcode
   - Select your target → "Signing & Capabilities"
   - Add "Background Modes" capability
   - Check "Location updates" and "Uses Bluetooth LE accessories"

3. **Request proper authorization**:
   ```typescript
   // For foreground detection only
   await CapacitorIbeacon.requestWhenInUseAuthorization();
   
   // For background monitoring (required for region enter/exit events when app is closed)
   await CapacitorIbeacon.requestAlwaysAuthorization();
   ```

4. **Check Bluetooth is enabled**:
   ```typescript
   const { enabled } = await CapacitorIbeacon.isBluetoothEnabled();
   if (!enabled) {
     // Prompt user to enable Bluetooth in Settings
   }
   ```

5. **Verify authorization status**:
   ```typescript
   const { status } = await CapacitorIbeacon.getAuthorizationStatus();
   console.log('Auth status:', status);
   // Should be 'authorized_when_in_use' or 'authorized_always'
   ```

6. **Check device compatibility**:
   - iBeacon requires iOS 7.0+
   - Some features require specific iOS versions (e.g., ranging in background)
   - Use `isRangingAvailable()` to check device support

**"No events firing" or "addListener not working"**

- Ensure you set up listeners **before** starting monitoring/ranging:
  ```typescript
  // Set up listeners first
  await CapacitorIbeacon.addListener('didEnterRegion', (data) => {
    console.log('Entered:', data.region.identifier);
  });
  
  // Then start monitoring
  await CapacitorIbeacon.startMonitoringForRegion({
    identifier: 'MyRegion',
    uuid: 'YOUR-UUID-HERE'
  });
  ```

**Background monitoring not working**

- Requires "Always" authorization: `requestAlwaysAuthorization()`
- Must have `UIBackgroundModes` with `location` and `bluetooth-central` in Info.plist
- Must have Background Modes capability enabled in Xcode
- iOS may throttle background scanning to preserve battery

### Android Issues

See the Android permissions section in the Configuration above. Most Android issues relate to:
- Not requesting runtime permissions
- Missing foreground service on Android 8+
- Bluetooth not enabled

For detailed Android troubleshooting, check the AltBeacon library documentation.

## Credits

This plugin was inspired by [cordova-plugin-ibeacon](https://github.com/petermetz/cordova-plugin-ibeacon) and adapted for Capacitor.

## License

MIT

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute to this plugin.
