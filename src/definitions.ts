import type { Plugin, PluginListenerHandle } from '@capacitor/core';

/**
 * Capacitor iBeacon Plugin - Proximity detection and beacon region monitoring.
 *
 * @since 1.0.0
 */
export interface CapacitorIbeaconPlugin extends Plugin {
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

  /**
   * Enable or disable background beacon scanning mode (Android only).
   * This enables a foreground service for reliable background beacon detection.
   * Must be called after requesting "Always" location authorization.
   *
   * @param options - Background mode configuration
   * @returns Promise that resolves when background mode is configured
   * @throws Error if configuration fails
   * @since 8.0.9
   * @platform Android
   * @example
   * ```typescript
   * // Enable background mode for beacon scanning
   * await CapacitorIbeacon.enableBackgroundMode({ enabled: true });
   *
   * // Disable background mode
   * await CapacitorIbeacon.enableBackgroundMode({ enabled: false });
   * ```
   */
  enableBackgroundMode(options: { enabled: boolean }): Promise<void>;

  /**
   * Configure background scan periods (Android only).
   * Controls how often and how long the device scans for beacons when in background.
   *
   * @param options - Scan period configuration in milliseconds
   * @returns Promise that resolves when scan periods are configured
   * @throws Error if configuration fails
   * @since 8.0.9
   * @platform Android
   * @example
   * ```typescript
   * // Set background scan to 10 seconds every 30 seconds
   * await CapacitorIbeacon.setBackgroundScanPeriod({
   *   scanPeriod: 10000,        // 10 seconds of scanning
   *   betweenScanPeriod: 30000  // 30 seconds between scans
   * });
   * ```
   */
  setBackgroundScanPeriod(options: BackgroundScanPeriodOptions): Promise<void>;

  /**
   * Listen for beacon ranging events.
   *
   * @param eventName - The event name ('didRangeBeacons')
   * @param listenerFunc - Callback function that receives beacon data
   * @returns Promise that resolves with a PluginListenerHandle
   * @since 1.0.0
   * @example
   * ```typescript
   * const listener = await CapacitorIbeacon.addListener('didRangeBeacons', (data) => {
   *   console.log('Beacons:', data.beacons);
   * });
   * // Remove listener when done
   * await listener.remove();
   * ```
   */
  addListener(
    eventName: 'didRangeBeacons',
    listenerFunc: (data: RangingEventData) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen for region enter events.
   *
   * @param eventName - The event name ('didEnterRegion')
   * @param listenerFunc - Callback function that receives region data
   * @returns Promise that resolves with a PluginListenerHandle
   * @since 1.0.0
   * @example
   * ```typescript
   * const listener = await CapacitorIbeacon.addListener('didEnterRegion', (data) => {
   *   console.log('Entered region:', data.region.identifier);
   * });
   * ```
   */
  addListener(
    eventName: 'didEnterRegion',
    listenerFunc: (data: { region: BeaconRegion }) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen for region exit events.
   *
   * @param eventName - The event name ('didExitRegion')
   * @param listenerFunc - Callback function that receives region data
   * @returns Promise that resolves with a PluginListenerHandle
   * @since 1.0.0
   * @example
   * ```typescript
   * const listener = await CapacitorIbeacon.addListener('didExitRegion', (data) => {
   *   console.log('Exited region:', data.region.identifier);
   * });
   * ```
   */
  addListener(
    eventName: 'didExitRegion',
    listenerFunc: (data: { region: BeaconRegion }) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen for region state determination events.
   *
   * @param eventName - The event name ('didDetermineStateForRegion')
   * @param listenerFunc - Callback function that receives region and state data
   * @returns Promise that resolves with a PluginListenerHandle
   * @since 1.0.0
   * @example
   * ```typescript
   * const listener = await CapacitorIbeacon.addListener('didDetermineStateForRegion', (data) => {
   *   console.log('Region state:', data.state, 'for', data.region.identifier);
   * });
   * ```
   */
  addListener(
    eventName: 'didDetermineStateForRegion',
    listenerFunc: (data: MonitoringEventData) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen for monitoring failure events.
   *
   * @param eventName - The event name ('monitoringDidFailForRegion')
   * @param listenerFunc - Callback function that receives error data
   * @returns Promise that resolves with a PluginListenerHandle
   * @since 1.0.0
   * @example
   * ```typescript
   * const listener = await CapacitorIbeacon.addListener('monitoringDidFailForRegion', (data) => {
   *   console.error('Monitoring failed:', data.error);
   * });
   * ```
   */
  addListener(
    eventName: 'monitoringDidFailForRegion',
    listenerFunc: (data: { region: BeaconRegion; error: string }) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all listeners for this plugin.
   *
   * @returns Promise that resolves when all listeners are removed
   * @since 1.0.0
   * @example
   * ```typescript
   * await CapacitorIbeacon.removeAllListeners();
   * ```
   */
  removeAllListeners(): Promise<void>;
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

  /**
   * Enable Android background mode for this monitoring/ranging call.
   * When true, the plugin will keep scanning in background using a foreground service.
   */
  enableBackgroundMode?: boolean;
}

/**
 * Background scan period configuration options (Android only).
 */
export interface BackgroundScanPeriodOptions {
  /**
   * Duration of each scan period in milliseconds.
   * Default: 10000 (10 seconds)
   */
  scanPeriod?: number;

  /**
   * Duration between scan periods in milliseconds.
   * Default: 15000 (15 seconds)
   */
  betweenScanPeriod?: number;
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
