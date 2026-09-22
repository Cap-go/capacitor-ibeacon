import { WebPlugin } from '@capacitor/core';

import type {
  CapacitorIbeaconPlugin,
  BeaconRegion,
  BeaconAdvertisingOptions,
  BackgroundScanPeriodOptions,
} from './definitions';

export class CapacitorIbeaconWeb extends WebPlugin implements CapacitorIbeaconPlugin {
  startMonitoringForRegion(_options: BeaconRegion): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  stopMonitoringForRegion(_options: BeaconRegion): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  startRangingBeaconsInRegion(_options: BeaconRegion): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  stopRangingBeaconsInRegion(_options: BeaconRegion): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  startAdvertising(_options: BeaconAdvertisingOptions): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  stopAdvertising(): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  requestWhenInUseAuthorization(): Promise<{ status: string }> {
    return Promise.resolve({ status: 'denied' });
  }

  requestAlwaysAuthorization(): Promise<{ status: string }> {
    return Promise.resolve({ status: 'denied' });
  }

  getAuthorizationStatus(): Promise<{ status: string }> {
    return Promise.resolve({ status: 'not_determined' });
  }

  isBluetoothEnabled(): Promise<{ enabled: boolean }> {
    return Promise.resolve({ enabled: false });
  }

  isRangingAvailable(): Promise<{ available: boolean }> {
    return Promise.resolve({ available: false });
  }

  enableARMAFilter(_options: { enabled: boolean }): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  async getPluginVersion(): Promise<{ version: string }> {
    return { version: 'web' };
  }

  enableBackgroundMode(_options: { enabled: boolean }): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  setBackgroundScanPeriod(_options: BackgroundScanPeriodOptions): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }
}
