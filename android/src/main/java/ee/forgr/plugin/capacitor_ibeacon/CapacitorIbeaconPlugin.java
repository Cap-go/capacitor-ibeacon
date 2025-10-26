package ee.forgr.plugin.capacitor_ibeacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

@CapacitorPlugin(
    name = "CapacitorIbeacon",
    permissions = {
        @Permission(alias = "location", strings = { 
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        }),
        @Permission(alias = "bluetooth", strings = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        })
    }
)
public class CapacitorIbeaconPlugin extends Plugin {

    private final String PLUGIN_VERSION = "0.0.1";

    @Override
    public void load() {
        // Initialize beacon library if needed
        // This requires adding a beacon library like AltBeacon
    }

    @PluginMethod
    public void startMonitoringForRegion(PluginCall call) {
        String identifier = call.getString("identifier");
        String uuid = call.getString("uuid");
        Integer major = call.getInt("major");
        Integer minor = call.getInt("minor");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return
;
        }

        // TODO: Implement beacon monitoring using AltBeacon or similar library
        call.reject("Beacon monitoring requires AltBeacon library integration");
    }

    @PluginMethod
    public void stopMonitoringForRegion(PluginCall call) {
        String identifier = call.getString("identifier");
        String uuid = call.getString("uuid");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return;
        }

        // TODO: Stop monitoring
        call.resolve();
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

        // TODO: Implement beacon ranging
        call.reject("Beacon ranging requires AltBeacon library integration");
    }

    @PluginMethod
    public void stopRangingBeaconsInRegion(PluginCall call) {
        String identifier = call.getString("identifier");
        String uuid = call.getString("uuid");

        if (identifier == null || uuid == null) {
            call.reject("Missing required parameters");
            return;
        }

        // TODO: Stop ranging
        call.resolve();
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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
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
        // TODO: Configure ARMA filter if using AltBeacon
        call.resolve();
    }

    @PluginMethod
    public void getPluginVersion(final PluginCall call) {
        try {
            final JSObject ret = new JSObject();
            ret.put("version", this.PLUGIN_VERSION);
            call.resolve(ret);
        } catch (final Exception e) {
            call.reject("Could not get plugin version", e);
        }
    }
}
