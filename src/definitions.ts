/**
 * Capacitor iBeacon Plugin - Proximity detection and beacon region monitoring.
 *
 * @since 1.0.0
 */
export interface CapacitorIbeaconPlugin {
  /**
   * Start monitoring for a beacon region. Triggers events when entering/exiting the region.
   *
   * @param options - Region to monitor
   * @returns Promise that resolves when monitoring starts
   * @throws Error if monitoring fails to start
   * @since 1.0.0
   * @example
   * ```typescript
   * await CapacitorIbeacon.startMonitoringForRegion({
   *   identifier: 'MyBeaconRegion',
   *   uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D'
   * });
   * ```
   */
  startMonitoringForRegion(options: BeaconRegion): Promise<void>;

  /**
   * Stop monitoring for a beacon region.
   *
   * @param options - Region to stop monitoring
   * @returns Promise that resolves when monitoring stops
   * @throws Error if stopping monitoring fails
   * @since 1.0.0
   * @example
   * ```typescript
   * await CapacitorIbeacon.stopMonitoringForRegion({
   *   identifier: 'MyBeaconRegion',
   *   uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D'
   * });
   * ```
   */
  stopMonitoringForRegion(options: BeaconRegion): Promise<void>;

  /**
   * Start ranging beacons in a region. Provides continuous distance updates.
   *
   * @param options - Region to range beacons in
   * @returns Promise that resolves when ranging starts
   * @throws Error if ranging fails to start
   * @since 1.0.0
   * @example
   * ```typescript
   * await CapacitorIbeacon.startRangingBeaconsInRegion({
   *   identifier: 'MyBeaconRegion',
   *   uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D'
   * });
   * ```
   */
  startRangingBeaconsInRegion(options: BeaconRegion): Promise<void>;

  /**
   * Stop ranging beacons in a region.
   *
   * @param options - Region to stop ranging beacons in
   * @returns Promise that resolves when ranging stops
   * @throws Error if stopping ranging fails
   * @since 1.0.0
   * @example
   * ```typescript
   * await CapacitorIbeacon.stopRangingBeaconsInRegion({
   *   identifier: 'MyBeaconRegion',
   *   uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D'
   * });
   * ```
   */
  stopRangingBeaconsInRegion(options: BeaconRegion): Promise<void>;

  /**
   * Start advertising the device as an iBeacon (iOS only).
   *
   * @param options - Beacon advertising parameters
   * @returns Promise that resolves when advertising starts
   * @throws Error if advertising fails to start or on Android
   * @since 1.0.0
   * @platform iOS
   * @example
   * ```typescript
   * await CapacitorIbeacon.startAdvertising({
   *   uuid: 'B9407F30-F5F8-466E-AFF9-25556B57FE6D',
   *   major: 1,
   *   minor: 2,
   *   identifier: 'MyBeacon'
   * });
   * ```
   */
  startAdvertising(options: BeaconAdvertisingOptions): Promise<void>;

  /**
   * Stop advertising the device as an iBeacon (iOS only).
   *
   * @returns Promise that resolves when advertising stops
   * @throws Error if stopping advertising fails
   * @since 1.0.0
   * @platform iOS
   * @example
   * ```typescript
   * await CapacitorIbeacon.stopAdvertising();
   * ```
   */
  stopAdvertising(): Promise<void>;

  /**
   * Request "When In Use" location authorization (required for ranging/monitoring).
   *
   * @returns Promise that resolves with authorization status
   * @throws Error if request fails
   * @since 1.0.0
   * @example
   * ```typescript
   * const { status } = await CapacitorIbeacon.requestWhenInUseAuthorization();
   * console.log('Authorization status:', status);
   * ```
   */
  requestWhenInUseAuthorization(): Promise<{ status: string }>;

  /**
   * Request "Always" location authorization (required for background monitoring).
   *
   * @returns Promise that resolves with authorization status
   * @throws Error if request fails
   * @since 1.0.0
   * @example
   * ```typescript
   * const { status } = await CapacitorIbeacon.requestAlwaysAuthorization();
   * console.log('Authorization status:', status);
   * ```
   */
  requestAlwaysAuthorization(): Promise<{ status: string }>;

  /**
   * Get current location authorization status.
   *
   * @returns Promise that resolves with authorization status
   * @throws Error if getting status fails
   * @since 1.0.0
   * @example
   * ```typescript
   * const { status } = await CapacitorIbeacon.getAuthorizationStatus();
   * console.log('Current status:', status);
   * ```
   */
  getAuthorizationStatus(): Promise<{ status: string }>;

  /**
   * Check if Bluetooth is enabled on the device.
   *
   * @returns Promise that resolves with Bluetooth state
   * @throws Error if checking state fails
   * @since 1.0.0
   * @example
   * ```typescript
   * const { enabled } = await CapacitorIbeacon.isBluetoothEnabled();
   * if (!enabled) {
   *   console.log('Please enable Bluetooth');
   * }
   * ```
   */
  isBluetoothEnabled(): Promise<{ enabled: boolean }>;

  /**
   * Check if ranging is available on the device.
   *
   * @returns Promise that resolves with availability status
   * @throws Error if checking availability fails
   * @since 1.0.0
   * @example
   * ```typescript
   * const { available } = await CapacitorIbeacon.isRangingAvailable();
   * if (available) {
   *   console.log('Ranging is supported');
   * }
   * ```
   */
  isRangingAvailable(): Promise<{ available: boolean }>;

  /**
   * Enable ARMA filtering for distance calculations (Android only).
   *
   * @param options - ARMA filter configuration
   * @returns Promise that resolves when filter is configured
   * @throws Error if configuration fails
   * @since 1.0.0
   * @platform Android
   * @example
   * ```typescript
   * await CapacitorIbeacon.enableARMAFilter({
   *   enabled: true
   * });
   * ```
   */
  enableARMAFilter(options: { enabled: boolean }): Promise<void>;

  /**
   * Get the native Capacitor plugin version.
   *
   * @returns Promise that resolves with the plugin version
   * @throws Error if getting the version fails
   * @since 1.0.0
   * @example
   * ```typescript
   * const { version } = await CapacitorIbeacon.getPluginVersion();
   * console.log('Plugin version:', version);
   * ```
   */
  getPluginVersion(): Promise<{ version: string }>;
}

/**
 * Beacon region definition for monitoring and ranging.
 */
export interface BeaconRegion {
  /**
   * Unique identifier for this region.
   */
  identifier: string;

  /**
   * UUID of the beacon(s) to detect.
   */
  uuid: string;

  /**
   * Major value for filtering (optional).
   */
  major?: number;

  /**
   * Minor value for filtering (optional).
   */
  minor?: number;

  /**
   * Notify when device enters region (iOS only).
   */
  notifyEntryStateOnDisplay?: boolean;
}

/**
 * Beacon advertising options for transmitting as an iBeacon (iOS only).
 */
export interface BeaconAdvertisingOptions {
  /**
   * UUID to advertise.
   */
  uuid: string;

  /**
   * Major value (0-65535).
   */
  major: number;

  /**
   * Minor value (0-65535).
   */
  minor: number;

  /**
   * Identifier for the advertising beacon.
   */
  identifier: string;

  /**
   * Measured power (RSSI at 1 meter). Optional, defaults to -59.
   */
  measuredPower?: number;
}

/**
 * Detected beacon information.
 */
export interface Beacon {
  /**
   * Beacon UUID.
   */
  uuid: string;

  /**
   * Major value.
   */
  major: number;

  /**
   * Minor value.
   */
  minor: number;

  /**
   * RSSI (Received Signal Strength Indicator).
   */
  rssi: number;

  /**
   * Proximity: 'immediate', 'near', 'far', or 'unknown'.
   */
  proximity: 'immediate' | 'near' | 'far' | 'unknown';

  /**
   * Estimated distance in meters.
   */
  accuracy: number;
}

/**
 * Event data when beacons are ranged.
 */
export interface RangingEventData {
  /**
   * Region that was ranged.
   */
  region: BeaconRegion;

  /**
   * Array of detected beacons.
   */
  beacons: Beacon[];
}

/**
 * Event data when entering or exiting a region.
 */
export interface MonitoringEventData {
  /**
   * Region that triggered the event.
   */
  region: BeaconRegion;

  /**
   * Event state: 'enter' or 'exit'.
   */
  state: 'enter' | 'exit';
}

/**
 * Event listeners for iBeacon plugin.
 */
export interface BeaconEventListeners {
  /**
   * Called when beacons are detected during ranging.
   */
  didRangeBeacons?: (data: RangingEventData) => void;

  /**
   * Called when entering or exiting a monitored region.
   */
  didDetermineStateForRegion?: (data: MonitoringEventData) => void;

  /**
   * Called when entering a monitored region.
   */
  didEnterRegion?: (data: { region: BeaconRegion }) => void;

  /**
   * Called when exiting a monitored region.
   */
  didExitRegion?: (data: { region: BeaconRegion }) => void;

  /**
   * Called when monitoring state changes.
   */
  monitoringDidFailForRegion?: (data: { region: BeaconRegion; error: string }) => void;
}
