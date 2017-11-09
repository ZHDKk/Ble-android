
package com.example.bluetooth.le;

import java.util.HashMap;

public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String MYCJ_BLE = "服务UUID";
    public static String MYCJ_BLE_READ = "读属性UUID";
    public static String MYCJ_BLE_WRITE = "写属性UUID";
    public  static String BLE_PROTOCOL="55AA";
    public static  String BLE_RETURNDATA="55EFBFBD";
    public static String DEVICE_ADDRESS="address";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
    public static String getUUID(String uuidKey) {
        return attributes.get(uuidKey);
    }
}
