package ee.forgr.plugin.capacitor_ibeacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

@CapacitorPlugin(
    name = "CapacitorIbeacon",
    permissions = {
        @Permission(alias = "location", strings = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }),
        @Permission(alias = "bluetooth", strings = { Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN })
    }
)
public class CapacitorIbeaconPlugin extends Plugin implements BeaconConsumer {

    private final String pluginVersion = "7.0.7";
    private BeaconManager beaconManager;
    private Map<String, Region> monitoredRegions = new HashMap<>();
    private Map<String, Region> rangedRegions = new HashMap<>();
    private boolean beaconManagerBound = false;

    @Override
    public void load() {
        // Initialize beacon manager
        beaconManager = BeaconManager.getInstanceForApplication(getContext());

        // Set up iBeacon layout parser
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // Bind to beacon service
        beaconManager.bind(this);

        // Set up monitoring and ranging notifiers
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                notifyDidEnterRegion(region);
            }

            @Override
            public void didExitRegion(Region region) {
                notifyDidExitRegion(region);
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                notifyDidDetermineStateForRegion(state, region);
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                notifyDidRangeBeacons(beacons, region);
            }
        });
    }

    @Override
    protected void handleOnDestroy() {
        if (beaconManager != null && beaconManagerBound) {
            beaconManager.unbind(this);
            beaconManagerBound = false;
        }
        super.handleOnDestroy();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManagerBound = true;
    }

    @Override
    public Context getApplicationContext() {
        return getContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getContext().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(android.content.Intent intent, ServiceConnection serviceConnection, int i) {
        return getContext().bindService(intent, serviceConnection, i);
    }

    @PluginMethod
    public void startMonitoringForRegion(PluginCall call) {
        String identifier = call.getString("identifier");
        String uuid = call.getString("uuid");
        Integer major = call.getInt("major");
        Integer minor = call.getInt("minor");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return;
        }

        try {
            Region region = createRegion(identifier, uuid, major, minor);
            monitoredRegions.put(identifier, region);
            beaconManager.startMonitoring(region);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to start monitoring", e);
        }
    }

    @PluginMethod
    public void stopMonitoringForRegion(PluginCall call) {
        String identifier = call.getString("identifier");
        String uuid = call.getString("uuid");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return;
        }

        try {
            Region region = monitoredRegions.get(identifier);
            if (region != null) {
                beaconManager.stopMonitoring(region);
                monitoredRegions.remove(identifier);
            }
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to stop monitoring", e);
        }
    }

    @PluginMethod
    public void startRangingBeaconsInRegion(PluginCall call) {
        String identifier = call.getString("identifier");
        String uuid = call.getString("uuid");
        Integer major = call.getInt("major");
        Integer minor = call.getInt("minor");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return;
        }

        try {
            Region region = createRegion(identifier, uuid, major, minor);
            rangedRegions.put(identifier, region);
            beaconManager.startRangingBeacons(region);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to start ranging", e);
        }
    }

    @PluginMethod
    public void stopRangingBeaconsInRegion(PluginCall call) {
        String identifier = call.getString("identifier");
        String uuid = call.getString("uuid");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return;
        }

        try {
            Region region = rangedRegions.get(identifier);
            if (region != null) {
                beaconManager.stopRangingBeacons(region);
                rangedRegions.remove(identifier);
            }
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to stop ranging", e);
        }
    }

    @PluginMethod
    public void startAdvertising(PluginCall call) {
        call.reject("Advertising is not supported on Android through this API");
    }

    @PluginMethod
    public void stopAdvertising(PluginCall call) {
        call.reject("Advertising is not supported on Android through this API");
    }

    @PluginMethod
    public void requestWhenInUseAuthorization(PluginCall call) {
        if (
            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionForAlias("location", call, "locationPermissionCallback");
        } else {
            JSObject ret = new JSObject();
            ret.put("status", "authorized_when_in_use");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void requestAlwaysAuthorization(PluginCall call) {
        requestWhenInUseAuthorization(call);
    }

    @PluginMethod
    public void getAuthorizationStatus(PluginCall call) {
        JSObject ret = new JSObject();
        if (
            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            ret.put("status", "authorized_when_in_use");
        } else {
            ret.put("status", "denied");
        }
        call.resolve(ret);
    }

    @PluginMethod
    public void isBluetoothEnabled(PluginCall call) {
        JSObject ret = new JSObject();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ret.put("enabled", bluetoothAdapter != null && bluetoothAdapter.isEnabled());
        call.resolve(ret);
    }

    @PluginMethod
    public void isRangingAvailable(PluginCall call) {
        JSObject ret = new JSObject();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ret.put("available", bluetoothAdapter != null);
        call.resolve(ret);
    }

    @PluginMethod
    public void enableARMAFilter(PluginCall call) {
        Boolean enabled = call.getBoolean("enabled", false);
        if (enabled != null && enabled) {
            // Enable ARMA (Auto-Regressive Moving Average) filter for distance smoothing
            Beacon.setDistanceCalculator(new org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator(
                getContext(),
                org.altbeacon.beacon.BeaconManager.getDistanceModelUpdateUrl()
            ));
        }
        call.resolve();
    }

    @PluginMethod
    public void getPluginVersion(final PluginCall call) {
        try {
            final JSObject ret = new JSObject();
            ret.put("version", this.pluginVersion);
            call.resolve(ret);
        } catch (final Exception e) {
            call.reject("Could not get plugin version", e);
        }
    }

    // Helper methods

    private Region createRegion(String identifier, String uuid, Integer major, Integer minor) {
        List<Identifier> identifiers = new ArrayList<>();
        identifiers.add(Identifier.parse(uuid));

        if (major != null) {
            identifiers.add(Identifier.fromInt(major));
        }
        if (minor != null) {
            identifiers.add(Identifier.fromInt(minor));
        }

        return new Region(identifier, identifiers);
    }

    private void notifyDidEnterRegion(Region region) {
        JSObject ret = new JSObject();
        ret.put("region", serializeRegion(region));
        notifyListeners("didEnterRegion", ret);
    }

    private void notifyDidExitRegion(Region region) {
        JSObject ret = new JSObject();
        ret.put("region", serializeRegion(region));
        notifyListeners("didExitRegion", ret);
    }

    private void notifyDidDetermineStateForRegion(int state, Region region) {
        JSObject ret = new JSObject();
        ret.put("region", serializeRegion(region));
        ret.put("state", state == org.altbeacon.beacon.MonitorNotifier.INSIDE ? "enter" : "exit");
        notifyListeners("didDetermineStateForRegion", ret);
    }

    private void notifyDidRangeBeacons(Collection<Beacon> beacons, Region region) {
        JSObject ret = new JSObject();
        ret.put("region", serializeRegion(region));
        ret.put("beacons", serializeBeacons(beacons));
        notifyListeners("didRangeBeacons", ret);
    }

    private JSObject serializeRegion(Region region) {
        JSObject obj = new JSObject();
        obj.put("identifier", region.getUniqueId());

        if (region.getId1() != null) {
            obj.put("uuid", region.getId1().toString());
        }
        if (region.getId2() != null) {
            obj.put("major", region.getId2().toInt());
        }
        if (region.getId3() != null) {
            obj.put("minor", region.getId3().toInt());
        }

        return obj;
    }

    private JSArray serializeBeacons(Collection<Beacon> beacons) {
        JSArray array = new JSArray();

        for (Beacon beacon : beacons) {
            JSObject obj = new JSObject();

            if (beacon.getId1() != null) {
                obj.put("uuid", beacon.getId1().toString());
            }
            if (beacon.getId2() != null) {
                obj.put("major", beacon.getId2().toInt());
            }
            if (beacon.getId3() != null) {
                obj.put("minor", beacon.getId3().toInt());
            }

            obj.put("rssi", beacon.getRssi());
            obj.put("accuracy", beacon.getDistance());
            obj.put("proximity", getProximity(beacon.getDistance()));

            array.put(obj);
        }

        return array;
    }

    private String getProximity(double distance) {
        if (distance < 0) {
            return "unknown";
        } else if (distance < 0.5) {
            return "immediate";
        } else if (distance < 3.0) {
            return "near";
        } else {
            return "far";
        }
    }
}
