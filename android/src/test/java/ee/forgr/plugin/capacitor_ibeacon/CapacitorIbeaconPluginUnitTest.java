package ee.forgr.plugin.capacitor_ibeacon;

import static org.junit.Assert.assertEquals;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import org.json.JSONException;
import org.junit.Test;

public class CapacitorIbeaconPluginUnitTest {

    @Test
    public void testLongOptionFromCallCoercesIntegerBridgeValue() throws JSONException {
        JSObject data = new JSObject();
        data.put("scanPeriod", 10000);
        data.put("betweenScanPeriod", 15000);

        PluginCall call = new PluginCall(null, "CapacitorIbeacon", "test-callback", "setBackgroundScanPeriod", data);

        assertEquals(
            "JS numbers within Integer range must be read as scanPeriod",
            10000L,
            CapacitorIbeaconPlugin.longOptionFromCall(call, "scanPeriod", 5000L)
        );
        assertEquals(
            "JS numbers within Integer range must be read as betweenScanPeriod",
            15000L,
            CapacitorIbeaconPlugin.longOptionFromCall(call, "betweenScanPeriod", 7000L)
        );
        assertEquals("PluginCall.getLong misses Integer bridge values", Long.valueOf(5000L), call.getLong("scanPeriod", 5000L));
        assertEquals("PluginCall.getLong misses Integer bridge values", Long.valueOf(7000L), call.getLong("betweenScanPeriod", 7000L));
    }

    @Test
    public void testLongOptionFromCallUsesDefaultWhenMissing() {
        PluginCall call = new PluginCall(null, "CapacitorIbeacon", "test-callback", "setBackgroundScanPeriod", new JSObject());

        assertEquals(10000L, CapacitorIbeaconPlugin.longOptionFromCall(call, "scanPeriod", 10000L));
        assertEquals(15000L, CapacitorIbeaconPlugin.longOptionFromCall(call, "betweenScanPeriod", 15000L));
    }
}
