package com.example.bluetooth.le.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/3.
 * Time:15:07.
 */

public class ScreenActivity extends BaseActivity{
    private ShimmerFrameLayout container ;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<HashMap<String, String>>> uuids;
    private String characterUuid;//读写特征uuid
    private String serviceUuids;
    private BluetoothGattService mnotyGattService;
    private BluetoothGattCharacteristic characteristic;
    byte[] writeBytes = new byte[20];
    @Override
    public int getContentView() {
        return R.layout.screen_layout;
    }

    @Override
    public void initView() {

        setConnected();


    }

    private void sendData() {
        if(characteristic !=null){
            byte[] message = new byte[20];
//            message[0] = (byte) 0x01;
            String versionandSn = "0x55 0xAA 0xXX 0x00 0x01 0xYY 0x55 0xAA 0xXX 0x00 0x02 0xYY";
            writeBytes = versionandSn.getBytes();

//            String sn = "0x55 0xAA 0xXX 0x00 0x02 0xYY";
//            message = sn.getBytes();
//            characteristic.setValue(message[0],BluetoothGattCharacteristic.FORMAT_UINT8,0);
            characteristic.setValue(writeBytes);
//            characteristic.setValue(message);
            mBluetoothLeService.writeCharacteristic(characteristic);
            mBluetoothLeService.setCharacteristicNotification(
                    characteristic, true);
            mBluetoothLeService.readCharacteristic(characteristic);
        }
    }

    private void setConnected() {
        SPUtils spUtils = new SPUtils(this,"fileName");
        mDeviceAddress=spUtils.getString(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,"address");
        mDeviceName= spUtils.getString(DeviceControlActivity.EXTRAS_DEVICE_NAME,"name");
//        getActionBar().setTitle(mDeviceName);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            if (!TextUtils.isEmpty(mDeviceAddress)) {
                mBluetoothLeService.connect(mDeviceAddress);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                uuids=   displayGattServices(mBluetoothLeService,mBluetoothLeService.getSupportedGattServices());
                serviceUuids=mBluetoothLeService.getSupportedGattServices().get(uuids.size()-1).getUuid().toString();
                characterUuid =  uuids.get(uuids.size()-1).get(0).get(LIST_UUID);
                mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString(serviceUuids));
                characteristic = mnotyGattService.getCharacteristic(UUID.fromString(characterUuid));
                sendData();
                container = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
                container.startShimmerAnimation();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(ScreenActivity.this,HomeActivity.class);
                        finish();
                    }
                },2000);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                tvData.setText(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }else if (BluetoothLeService.RSSI.equals(action)){
//                rssi =  intent.getIntExtra("rssi",0);
            }
        }
    };
    @Override
    public void setWindowFeature() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("ad", "Connect request result=" + result);
        }else {
            Intent gattServiceIntent = new Intent(ScreenActivity.this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver!=null)
            unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
}
