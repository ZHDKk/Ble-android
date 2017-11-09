package com.example.bluetooth.le.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.LogUtil;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.example.bluetooth.le.view.InputCodeView;
import com.example.bluetooth.le.view.VerificationCodeInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import dmax.dialog.SpotsDialog;


/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/3.
 * Time:15:53.
 */

public class PinCodeActivity extends BaseActivity implements View.OnClickListener {
    private String mDeviceAddress,mDeviceName;
    private BluetoothLeService mBluetoothLeService;

    private ArrayList<ArrayList<HashMap<String, String>>> uuids;
    private Toolbar toolbar;
    private String characterUuid;//读写特征uuid
    private String serviceUuids;
    private BluetoothGattService mnotyGattService;
    private BluetoothGattCharacteristic characteristic;
    private byte[] writeBytes = new byte[20];
    private String callData;
    private Dialog sportDialog;

    private String notifyUuid;
    private BluetoothGattCharacteristic notifyCharacteristic;
//    private VerificationCodeInput codeInput;
    private InputCodeView codeInput;
    private TextView tvError;

    private Handler handler;
    private Dialog dialog;
    private SPUtils spUtils;
    private AlertDialog.Builder builder;

    @Override
    public int getContentView() {
        return R.layout.pincode_layout;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.pin_code));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unBindService();
                onBackPressed();
            }
        });

        codeInput = (InputCodeView) findViewById(R.id.verificationCodeInput);
//        codeInput = (VerificationCodeInput)findViewById(R.id.verificationCodeInput);
        tvError = (TextView)findViewById(R.id.tv_error);
        sportDialog = new SpotsDialog(this);
        handler = new Handler();
//        SPUtils spUtils = new SPUtils(this,"fileName");
//        mDeviceAddress=spUtils.getString(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,"address");
//        mDeviceName= spUtils.getString(DeviceControlActivity.EXTRAS_DEVICE_NAME,"name");
        mDeviceAddress = getIntent().getStringExtra(SampleGattAttributes.DEVICE_ADDRESS);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        sportDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sportDialog.isShowing()) {
                    sportDialog.dismiss();
                    ToastUtil.showToast(PinCodeActivity.this, getString(R.string.connectedFail));
                    unBindService();
                    finish();
                }
            }
        },10000);

        spUtils = new SPUtils(PinCodeActivity.this,"fileName");
//        spUtils.getString("pincode","");
        setPinCode();

    }

    private void setPinCode() {

        codeInput.setInputCompleteListener(new InputCodeView.InputCompleteListener() {
            @Override
            public void inputComplete(String text) {
                         String pin = spUtils.getString("pincode", "");
                    if (!TextUtils.isEmpty(pin) && pin.length() >= 18) {
                        final String code = pin.substring(10, 18);
                        final String pinCode = BleUtils.hexStr2Str(code);
                        if (text.equals(pinCode)) {
                            Intent intent = new Intent(PinCodeActivity.this, HomeActivity.class);
                            intent.putExtra(SampleGattAttributes.DEVICE_ADDRESS, mDeviceAddress);
                            startActivity(intent);
                            spUtils.putString("pincode", "");
                            finish();
                            unBindService();
                            DeviceScanActivity.instance.finish();
                            if (HomeActivity.instance != null)
                                HomeActivity.instance.finish();
                        } else {
                            ToastUtil.showToast(PinCodeActivity.this,getString(R.string.pin_Wrong));
                            codeInput.clearAllText();
                        }
                    }
            }

            @Override
            public void deleteContent() {

            }
        });
//            codeInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
//                @Override
//                public void onComplete(String s) {
//                    String pin = spUtils.getString("pincode", "");
//                    if (!TextUtils.isEmpty(pin) && pin.length() >= 18) {
//                        final String code = pin.substring(10, 18);
//                        final String pinCode = BleUtils.hexStr2Str(code);
//                        if (s.equals(pinCode)) {
//                            Intent intent = new Intent(PinCodeActivity.this, HomeActivity.class);
//                            intent.putExtra(SampleGattAttributes.DEVICE_ADDRESS, mDeviceAddress);
//                            startActivity(intent);
//                            spUtils.putString("pincode","");
//                            finish();
//                            unBindService();
//                            DeviceScanActivity.instance.finish();
//                            if (HomeActivity.instance != null)
//                                HomeActivity.instance.finish();
//                        } else {
////                    Animation anim = AnimationUtils.loadAnimation(PinCodeActivity.this, R.anim.myanim);
////                    R.layout.pincode_layout.startAnimation(anim);
//                            codeInput.setEnabled(true);
//                            AlertDialog.Builder builder = new AlertDialog.Builder(PinCodeActivity.this);
//                            builder.setTitle(R.string.prompt);
//                            builder.setMessage(R.string.pin_Wrong);
//                            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    codeInput.clearText();
//                                }
//                            });
//                            builder.create().show();
////                        tvError.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
//
//            });

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };



    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                uuids = displayGattServices(mBluetoothLeService,mBluetoothLeService.getSupportedGattServices());
                serviceUuids = mBluetoothLeService.getSupportedGattServices().get(uuids.size() - 1).getUuid().toString();
                characterUuid = uuids.get(uuids.size() - 1).get(0).get(LIST_UUID);
                notifyUuid= uuids.get(uuids.size() -1).get(1).get(LIST_UUID);
                mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString(serviceUuids));
                notifyCharacteristic= mnotyGattService.getCharacteristic(UUID.fromString(notifyUuid));
                characteristic = mnotyGattService.getCharacteristic(UUID.fromString(characterUuid));
                sendData();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                callData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                LogUtil.fussenLog().d("返回"+callData);
                if (BleUtils.bin2hex(callData).length()>=10) {
                    if (sportDialog!=null)
                    sportDialog.dismiss();
                    if (BleUtils.bin2hex(callData).substring(6, 10).equals("0003")) {
                        spUtils.putString("pincode", BleUtils.bin2hex(callData));
//                setPinCode(BleUtils.bin2hex(callData));
                    }
                }
            }else if (BluetoothLeService.WRITE_SUCCESS.equals(action)){

            }
        }
    };

    private void sendData() {
        if(characteristic !=null){
            //写
            String pin = SampleGattAttributes.BLE_PROTOCOL+"020003";
            String checkNum = BleUtils.makeChecksum(pin);
            String text = pin+checkNum;
            writeBytes = BleUtils.hex2byte(text.getBytes());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //数据分包
                    setDataSubcontractor();

                    //读
                    mBluetoothLeService.setCharacteristicNotification(
                            notifyCharacteristic, true);
                    mBluetoothLeService.readCharacteristic(characteristic);
                }
            }).start();

        }
    }
    private  void setDataSubcontractor() {
        int tmpLen = writeBytes.length;
        int start=0;
        int end=0;
        while (tmpLen>0){
            byte[] sendData = new byte[20];
            if (tmpLen>=20){
                end+=20;
                sendData = Arrays.copyOfRange(writeBytes, start, end);
                start+=20;
                tmpLen -= 20;
            }else {
                end+=tmpLen;
                sendData = Arrays.copyOfRange(writeBytes, start, end);
                tmpLen = 0;
            }
            SystemClock.sleep(100);
            characteristic.setValue(sendData);
            mBluetoothLeService.writeCharacteristic(characteristic);

        }
    }
    @Override
    public void setWindowFeature() {

    }


    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("ad", "Connect request result=" + result);
        }else {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            unBindService();
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);

    }

   private void  unBindService(){
       if (mGattUpdateReceiver!=null)
           unregisterReceiver(mGattUpdateReceiver);
       unbindService(mServiceConnection);
       mBluetoothLeService = null;
   }

}
