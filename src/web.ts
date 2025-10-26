import { WebPlugin } from '@capacitor/core';

import type {
  CapacitorIbeaconPlugin,
  BeaconRegion,
  BeaconAdvertisingOptions,
} from './definitions';

export class CapacitorIbeaconWeb extends WebPlugin implements CapacitorIbeaconPlugin {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  startMonitoringForRegion(_options: BeaconRegion): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  stopMonitoringForRegion(_options: BeaconRegion): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  startRangingBeaconsInRegion(_options: BeaconRegion): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  stopRangingBeaconsInRegion(_options: BeaconRegion): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
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

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  enableARMAFilter(_options: { enabled: boolean }): Promise<void> {
    throw new Error('Method not implemented on web platform.');
  }

  async getPluginVersion(): Promise<{ version: string }> {
    return { version: 'web' };
  }
}
