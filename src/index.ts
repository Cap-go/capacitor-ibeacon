import { registerPlugin } from '@capacitor/core';

import type { CapacitorIbeaconPlugin } from './definitions';

const CapacitorIbeacon = registerPlugin<CapacitorIbeaconPlugin>('CapacitorIbeacon', {
  web: () => import('./web').then((m) => new m.CapacitorIbeaconWeb()),
});

export * from './definitions';
export { CapacitorIbeacon };
