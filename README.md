# capacitor-ibeacon
  <a href="https://capgo.app/"><img src='https://raw.githubusercontent.com/Cap-go/capgo/main/assets/capgo_banner.png' alt='Capgo - Instant updates for capacitor'/></a>

<div align="center">
  <h2><a href="https://capgo.app/?ref=plugin_ibeacon"> ➡️ Get Instant updates for your App with Capgo</a></h2>
  <h2><a href="https://capgo.app/consulting/?ref=plugin_ibeacon"> Missing a feature? We'll build the plugin for you 💪</a></h2>
</div>

iBeacon plugin for Capacitor - proximity detection and beacon region monitoring.

## Documentation

The most complete doc is available here: https://capgo.app/docs/plugins/ibeacon/

## Install

```bash
npm install @capgo/capacitor-ibeacon
npx cap sync
```

## Configuration

### iOS

Add the following to your `Info.plist`:

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>This app needs location access to detect nearby beacons</string>

<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>This app needs location access to monitor beacons in the background</string>

<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app uses Bluetooth to detect nearby beacons</string>

<key>UIBackgroundModes</key>
<array>
  <string>location</string>
</array>
```

### Android

Add the following to your `AndroidManifest.xml`:

```xml
<manifest>
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
  <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
</manifest>
```

**Important**: For Android, you need to integrate the [AltBeacon](https://altbeacon.github.io/android-beacon-library/) library into your project for beacon detection to work.

Add to your app's `build.gradle`:

```gradle
dependencies {
    implementation 'org.altbeacon:android-beacon-library:2.20+'
}
```

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

1. **iOS Background Monitoring**: To monitor beacons in the background, you need "Always" location permission and the `location` background mode in Info.plist.

2. **Android Library**: Android implementation requires the AltBeacon library to be integrated into your project.

3. **Battery Usage**: Continuous beacon ranging can consume significant battery. Consider using monitoring (which is more battery-efficient) and only start ranging when needed.

4. **UUID Format**: UUIDs must be in the standard format: `XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX`

5. **Major/Minor Values**: These are optional 16-bit unsigned integers (0-65535) used to identify specific beacons within a UUID.

6. **Permissions**: Always request and check permissions before starting beacon operations.

## Credits

This plugin was inspired by [cordova-plugin-ibeacon](https://github.com/petermetz/cordova-plugin-ibeacon) and adapted for Capacitor.

## License

MIT

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute to this plugin.
