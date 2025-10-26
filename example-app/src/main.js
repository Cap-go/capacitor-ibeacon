
import './style.css';
import { CapacitorIbeacon } from '@capgo/capacitor-ibeacon';

const plugin = CapacitorIbeacon;

const actions = [
  {
    id: 'request-permission',
    label: 'Request permission',
    description: 'Requests "When In Use" location authorization for beacon detection.',
    inputs: [],
    run: async () => {
      const { status } = await plugin.requestWhenInUseAuthorization();
      return { status };
    },
  },
  {
    id: 'check-bluetooth',
    label: 'Check Bluetooth enabled',
    description: 'Checks if Bluetooth is enabled on the device.',
    inputs: [],
    run: async () => {
      const { enabled } = await plugin.isBluetoothEnabled();
      return { enabled };
    },
  },
  {
    id: 'check-ranging',
    label: 'Check ranging available',
    description: 'Checks if beacon ranging is available on this device.',
    inputs: [],
    run: async () => {
      const { available } = await plugin.isRangingAvailable();
      return { available };
    },
  },
  {
    id: 'get-version',
    label: 'Get plugin version',
    description: 'Gets the native plugin version.',
    inputs: [],
    run: async () => {
      const { version } = await plugin.getPluginVersion();
      return { version };
    },
  },
];

const actionSelect = document.getElementById('action-select');
const formContainer = document.getElementById('action-form');
const descriptionBox = document.getElementById('action-description');
const runButton = document.getElementById('run-action');
const output = document.getElementById('plugin-output');

function buildForm(action) {
  formContainer.innerHTML = '';
  if (!action.inputs || !action.inputs.length) {
    const note = document.createElement('p');
    note.className = 'no-input-note';
    note.textContent = 'This action does not require any inputs.';
    formContainer.appendChild(note);
    return;
  }
}

function setAction(action) {
  descriptionBox.textContent = action.description || '';
  buildForm(action);
  output.textContent = 'Ready to run the selected action.';
}

function populateActions() {
  actionSelect.innerHTML = '';
  actions.forEach((action) => {
    const option = document.createElement('option');
    option.value = action.id;
    option.textContent = action.label;
    actionSelect.appendChild(option);
  });
  setAction(actions[0]);
}

actionSelect.addEventListener('change', () => {
  const action = actions.find((item) => item.id === actionSelect.value);
  if (action) {
    setAction(action);
  }
});

runButton.addEventListener('click', async () => {
  const action = actions.find((item) => item.id === actionSelect.value);
  if (!action) return;
  try {
    const result = await action.run();
    if (result === undefined) {
      output.textContent = 'Action completed.';
    } else if (typeof result === 'string') {
      output.textContent = result;
    } else {
      output.textContent = JSON.stringify(result, null, 2);
    }
  } catch (error) {
    output.textContent = 'Error: ' + (error?.message || error);
  }
});

populateActions();
