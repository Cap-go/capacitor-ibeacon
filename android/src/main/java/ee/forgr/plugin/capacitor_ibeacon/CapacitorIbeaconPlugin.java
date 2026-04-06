package ee.forgr.plugin.capacitor_ibeacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
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
        @Permission(alias = "backgroundLocation", strings = { Manifest.permission.ACCESS_BACKGROUND_LOCATION }),
        @Permission(alias = "bluetooth", strings = { Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN }),
        @Permission(alias = "bluetoothScan", strings = { Manifest.permission.BLUETOOTH_SCAN }),
        @Permission(alias = "bluetoothConnect", strings = { Manifest.permission.BLUETOOTH_CONNECT })
    }
)
public class CapacitorIbeaconPlugin extends Plugin implements BeaconConsumer {

    private final String pluginVersion = "8.1.19";
    private static final String FOREGROUND_CHANNEL_ID = "beacon_service_channel";
    private static final int FOREGROUND_NOTIFICATION_ID = 456;
    private BeaconManager beaconManager;
    private Map<String, Region> monitoredRegions = new HashMap<>();
    private Map<String, Region> rangedRegions = new HashMap<>();
    private boolean beaconManagerBound = false;
    private boolean backgroundModeEnabled = false;
    private boolean foregroundServiceEnabled = false;
    private boolean isInBackground = false;

    @Override
    public void load() {
        // Initialize beacon manager
        beaconManager = BeaconManager.getInstanceForApplication(getContext());

        // Set up iBeacon layout parser
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // Configure for background scanning - enable long-running scanning mode
        // This is critical for beacon detection when app is in background
        beaconManager.setEnableScheduledScanJobs(false);

        // Configure background scan periods (in milliseconds)
        // Default background scan: 10 seconds scan, 5 minutes between scans
        // We use more aggressive settings for better detection
        beaconManager.setBackgroundBetweenScanPeriod(15000L); // 15 seconds between scans
        beaconManager.setBackgroundScanPeriod(10000L); // 10 seconds scan duration

        // Configure foreground scan periods
        beaconManager.setForegroundBetweenScanPeriod(0L); // Continuous scanning in foreground
        beaconManager.setForegroundScanPeriod(1100L); // Standard scan period

        // Bind to beacon service
        beaconManager.bind(this);

        // Set up monitoring and ranging notifiers
        beaconManager.addMonitorNotifier(
            new MonitorNotifier() {
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
            }
        );

        beaconManager.addRangeNotifier(
            new RangeNotifier() {
                @Override
                public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                    notifyDidRangeBeacons(beacons, region);
                }
            }
        );

        Boolean configBackgroundMode = getConfig().getBoolean("enableBackgroundMode", false);
        if (configBackgroundMode != null && configBackgroundMode) {
            backgroundModeEnabled = true;
        }
    }

    @Override
    protected void handleOnDestroy() {
        applyBackgroundMode(false);
        if (beaconManager != null && beaconManagerBound) {
            beaconManager.unbind(this);
            beaconManagerBound = false;
        }
        super.handleOnDestroy();
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
        isInBackground = true;
        applyBackgroundMode(backgroundModeEnabled);
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        isInBackground = false;
        applyBackgroundMode(backgroundModeEnabled);
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
        Boolean enableBackgroundMode = call.getBoolean("enableBackgroundMode");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return;
        }

        try {
            if (enableBackgroundMode != null) {
                setBackgroundModeEnabled(enableBackgroundMode);
            }
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
        Boolean enableBackgroundMode = call.getBoolean("enableBackgroundMode");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return;
        }

        try {
            if (enableBackgroundMode != null) {
                setBackgroundModeEnabled(enableBackgroundMode);
            }
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
        // On Android 12+, also need to request BLUETOOTH_SCAN and BLUETOOTH_CONNECT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasBluetoothScan =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
            boolean hasBluetoothConnect =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED;

            if (!hasBluetoothScan) {
                requestPermissionForAlias("bluetoothScan", call, "bluetoothScanPermissionCallback");
                return;
            }
            if (!hasBluetoothConnect) {
                requestPermissionForAlias("bluetoothConnect", call, "bluetoothConnectPermissionCallback");
                return;
            }
        }

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
        // First ensure we have foreground location permission
        boolean hasFineLocation =
            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!hasFineLocation) {
            // Must request foreground location first before background
            requestPermissionForAlias("location", call, "foregroundLocationForBackgroundCallback");
            return;
        }

        // On Android 10+ (Q), need to request ACCESS_BACKGROUND_LOCATION separately
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasBackgroundLocation =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

            if (!hasBackgroundLocation) {
                requestPermissionForAlias("backgroundLocation", call, "backgroundLocationPermissionCallback");
                return;
            }
        }

        // On Android 12+, also need BLUETOOTH_SCAN and BLUETOOTH_CONNECT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasBluetoothScan =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
            boolean hasBluetoothConnect =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED;

            if (!hasBluetoothScan) {
                requestPermissionForAlias("bluetoothScan", call, "bluetoothScanForBackgroundCallback");
                return;
            }
            if (!hasBluetoothConnect) {
                requestPermissionForAlias("bluetoothConnect", call, "bluetoothConnectForBackgroundCallback");
                return;
            }
        }

        JSObject ret = new JSObject();
        ret.put("status", "authorized_always");
        call.resolve(ret);
    }

    @PermissionCallback
    private void locationPermissionCallback(PluginCall call) {
        JSObject ret = new JSObject();
        if (getPermissionState("location") == PermissionState.GRANTED) {
            ret.put("status", "authorized_when_in_use");
        } else {
            ret.put("status", "denied");
        }
        call.resolve(ret);
    }

    @PluginMethod
    public void getAuthorizationStatus(PluginCall call) {
        JSObject ret = new JSObject();

        boolean hasFineLocation =
            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!hasFineLocation) {
            ret.put("status", "denied");
            call.resolve(ret);
            return;
        }

        // On Android 10+, check for background location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasBackgroundLocation =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

            if (hasBackgroundLocation) {
                ret.put("status", "authorized_always");
            } else {
                ret.put("status", "authorized_when_in_use");
            }
        } else {
            // Below Android 10, foreground permission is enough for background
            ret.put("status", "authorized_always");
        }

        call.resolve(ret);
    }

    @PermissionCallback
    private void foregroundLocationForBackgroundCallback(PluginCall call) {
        if (getPermissionState("location") == PermissionState.GRANTED) {
            // Now request background location
            requestAlwaysAuthorization(call);
        } else {
            JSObject ret = new JSObject();
            ret.put("status", "denied");
            call.resolve(ret);
        }
    }

    @PermissionCallback
    private void backgroundLocationPermissionCallback(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasBackgroundLocation =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;

            if (hasBackgroundLocation) {
                // Continue with bluetooth permissions on Android 12+
                requestAlwaysAuthorization(call);
            } else {
                JSObject ret = new JSObject();
                ret.put("status", "authorized_when_in_use");
                call.resolve(ret);
            }
        } else {
            requestAlwaysAuthorization(call);
        }
    }

    @PermissionCallback
    private void bluetoothScanPermissionCallback(PluginCall call) {
        // Check if BLUETOOTH_SCAN was granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasBluetoothScan =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;

            if (hasBluetoothScan) {
                // Continue with the original request
                requestWhenInUseAuthorization(call);
            } else {
                // Permission denied, resolve with denied status
                JSObject ret = new JSObject();
                ret.put("status", "denied");
                call.resolve(ret);
            }
        } else {
            // Continue with the original request on older versions
            requestWhenInUseAuthorization(call);
        }
    }

    @PermissionCallback
    private void bluetoothConnectPermissionCallback(PluginCall call) {
        // Check if BLUETOOTH_CONNECT was granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasBluetoothConnect =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED;

            if (hasBluetoothConnect) {
                // Continue with the original request
                requestWhenInUseAuthorization(call);
            } else {
                // Permission denied, resolve with denied status
                JSObject ret = new JSObject();
                ret.put("status", "denied");
                call.resolve(ret);
            }
        } else {
            // Continue with the original request on older versions
            requestWhenInUseAuthorization(call);
        }
    }

    @PermissionCallback
    private void bluetoothScanForBackgroundCallback(PluginCall call) {
        // Check if BLUETOOTH_SCAN was granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasBluetoothScan =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;

            if (hasBluetoothScan) {
                // Continue with the background authorization flow
                requestAlwaysAuthorization(call);
            } else {
                // Permission denied, check what we can offer
                boolean hasFineLocation =
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;

                JSObject ret = new JSObject();
                if (hasFineLocation) {
                    ret.put("status", "authorized_when_in_use");
                } else {
                    ret.put("status", "denied");
                }
                call.resolve(ret);
            }
        } else {
            // Continue with the background authorization flow on older versions
            requestAlwaysAuthorization(call);
        }
    }

    @PermissionCallback
    private void bluetoothConnectForBackgroundCallback(PluginCall call) {
        // Check if BLUETOOTH_CONNECT was granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean hasBluetoothConnect =
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED;

            if (hasBluetoothConnect) {
                // Continue with the background authorization flow
                requestAlwaysAuthorization(call);
            } else {
                // Permission denied, check what we can offer
                boolean hasFineLocation =
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;

                JSObject ret = new JSObject();
                if (hasFineLocation) {
                    ret.put("status", "authorized_when_in_use");
                } else {
                    ret.put("status", "denied");
                }
                call.resolve(ret);
            }
        } else {
            // Continue with the background authorization flow on older versions
            requestAlwaysAuthorization(call);
        }
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
            Beacon.setDistanceCalculator(
                new org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator(
                    getContext(),
                    org.altbeacon.beacon.BeaconManager.getDistanceModelUpdateUrl()
                )
            );
        }
        call.resolve();
    }

    @PluginMethod
    public void enableBackgroundMode(PluginCall call) {
        Boolean enabled = call.getBoolean("enabled", true);
        try {
            setBackgroundModeEnabled(enabled != null && enabled);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to enable background mode", e);
        }
    }

    @PluginMethod
    public void setBackgroundScanPeriod(PluginCall call) {
        Long scanPeriod = call.getLong("scanPeriod", 10000L);
        Long betweenScanPeriod = call.getLong("betweenScanPeriod", 15000L);

        try {
            beaconManager.setBackgroundScanPeriod(scanPeriod);
            beaconManager.setBackgroundBetweenScanPeriod(betweenScanPeriod);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to set background scan period", e);
        }
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

    private void setBackgroundModeEnabled(boolean enabled) {
        backgroundModeEnabled = enabled;
        applyBackgroundMode(enabled);
    }

    private void applyBackgroundMode(boolean enabled) {
        if (beaconManager == null) {
            return;
        }

        boolean shouldEnableBackgroundMode = enabled && isInBackground;

        if (shouldEnableBackgroundMode) {
            enableForegroundServiceIfNeeded();
            beaconManager.setBackgroundMode(true);
        } else {
            disableForegroundServiceIfNeeded();
            beaconManager.setBackgroundMode(false);
        }
    }

    private void enableForegroundServiceIfNeeded() {
        if (foregroundServiceEnabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        // Create notification channel for foreground service
        android.app.NotificationChannel channel = new android.app.NotificationChannel(
            FOREGROUND_CHANNEL_ID,
            "Beacon Service",
            android.app.NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("Background beacon monitoring service");

        android.app.NotificationManager notificationManager = getContext().getSystemService(android.app.NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        // Build notification for foreground service
        android.app.Notification.Builder builder = new android.app.Notification.Builder(getContext(), FOREGROUND_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info);
        builder.setContentTitle("Beacon Monitoring");
        builder.setContentText("Scanning for nearby beacons");

        // Enable foreground service mode in AltBeacon
        beaconManager.enableForegroundServiceScanning(builder.build(), FOREGROUND_NOTIFICATION_ID);
        foregroundServiceEnabled = true;
    }

    private void disableForegroundServiceIfNeeded() {
        if (!foregroundServiceEnabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        beaconManager.disableForegroundServiceScanning();
        foregroundServiceEnabled = false;
    }
}
