package com.example.bluetooth.le.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/8.
 * Time:16:26.
 */

public class ChangePwdActivity extends BaseActivity implements View.OnClickListener {
    private EditText edOldPwd,edNewPwd,edReNewPwd;
    private CardView changePwd;
    private Toolbar toolbar;
    byte[] writeBytes = new byte[20];
    private String oldPwd;


    private static Handler handler;
    private static String callData;
    private ChangePwdThread changePwdThread;
    @Override
    public int getContentView() {
        return R.layout.changepwd_layout;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.changePin));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        edOldPwd = (EditText) findViewById(R.id.ed_oldPwd);
        edNewPwd= (EditText) findViewById(R.id.ed_newPwd);
        edReNewPwd = (EditText) findViewById(R.id.ed_RenewPwd);
        changePwd= (CardView) findViewById(R.id.cardview_changePwd);
        changePwd.setOnClickListener(this);
        sendData();

        new HomeActivity().setOnDConnectListener(new HomeActivity.OnDConnectListener() {
            @Override
            public void setDConnect() {
                UiUtils.showSackBar(ChangePwdActivity.this,getString(R.string.disconnected),R.color.green,R.color.white);
            }
        });
    }

    private void sendData() {
        String text = "020003";
        HomeActivity.sendData(ChangePwdActivity.this,text);


        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1){
                    if (data.length()>=10) {
                        if (data.substring(0, 10).equals(SampleGattAttributes.BLE_PROTOCOL + "060003")) {
                            String code = data.substring(10, 18);
                            oldPwd = BleUtils.hexStr2Str(code);

                            HomeActivity.changeData();
                        }else if (data.substring(6,10).equals("000C")){
                            ToastUtil.showToast(ChangePwdActivity.this,getString(R.string.success));
                            finish();
                        }
                    }
                }
            }
        };
        changePwdThread =  new ChangePwdThread();
        changePwdThread.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        changePwdThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        changePwdThread.close();
    }

    @Override
    public void setWindowFeature() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardview_changePwd:
                if (!TextUtils.isEmpty(edNewPwd.getText().toString().trim())&&!TextUtils.isEmpty(edOldPwd.getText().toString().trim())&&!TextUtils.isEmpty(edReNewPwd.getText().toString().trim())) {
                    if (edOldPwd.getText().toString().trim().equals(oldPwd)) {
                        if (edNewPwd.getText().toString().trim().equals(edReNewPwd.getText().toString().trim())) {
                            String  message = BleUtils.bin2hex2(edNewPwd.getText().toString().trim());
                            String text = "06000C"+message;
                            HomeActivity.sendData(ChangePwdActivity.this,text);
                        } else {
                            ToastUtil.showToast(ChangePwdActivity.this,getString(R.string.pwdNotSame));
                        }

                    } else {
                        ToastUtil.showToast(ChangePwdActivity.this, getString(R.string.originalPwdFail));
                    }
                }else {
                    ToastUtil.showToast(ChangePwdActivity.this,getString(R.string.pwdNotEmpty));
                }
//                if (edOldPwd.getText().toString().trim().equals(oldPwd)&& edNewPwd.getText().toString().trim().equals(edReNewPwd.getText().toString().trim()) && !TextUtils.isEmpty(edNewPwd.getText().toString().trim())&&!TextUtils.isEmpty(edOldPwd.getText().toString().trim())&&!TextUtils.isEmpty(edReNewPwd.getText().toString().trim())){
//                    String  message = BleUtils.bin2hex2(edNewPwd.getText().toString().trim());
//                    String text = "06000C"+message;
//                    HomeActivity.sendData(ChangePwdActivity.this,text);
//
////                    UiUtils.showSackBar(ChangePwdActivity.this,getString(R.string.success),R.color.green,R.color.white);
//                }else {
//                    ToastUtil.showToast(ChangePwdActivity.this,getString(R.string.fail));
////                    UiUtils.showSackBar(ChangePwdActivity.this,getString(R.string.fail),R.color.green,R.color.white);
//                }
                break;
        }
    }

    private static class ChangePwdThread extends Thread{

        private boolean mRunning = false;
        @Override
        public void run() {
            mRunning = true;
            while (mRunning) {
                try {
                    Thread.sleep(1000);
                    callData = HomeActivity.callBackData();
                    if (!TextUtils.isEmpty(callData)) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = callData;
                        handler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }
        public void close() {
            mRunning = false;
        }

    }

}
