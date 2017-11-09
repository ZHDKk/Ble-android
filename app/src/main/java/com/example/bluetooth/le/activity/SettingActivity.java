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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/5.
 * Time:9:06.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private CardView layoutBoundary,layoutPin,layoutMulti,layoutReset,layoutReview,layoutRain;
    byte[] writeBytes = new byte[20];
    private Dialog dialog;
    private static Handler handler;

    private TextView tvRain;


    private static String callData;
    private SetThread setThread;

    @Override
    public int getContentView() {
        return R.layout.setting_layout;
    }

    @Override
    public void initView() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.setting));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layoutBoundary = (CardView) findViewById(R.id.layout_boundary);
        layoutBoundary.setOnClickListener(this);
        layoutPin = (CardView) findViewById(R.id.layout_pin);
        layoutPin.setOnClickListener(this);
        layoutMulti = (CardView) findViewById(R.id.layout_multi);
        layoutMulti.setOnClickListener(this);
        layoutReset = (CardView) findViewById(R.id.layout_reset);
        layoutReset.setOnClickListener(this);
        layoutReview = (CardView) findViewById(R.id.layout_review);
        layoutReview.setOnClickListener(this);
        layoutRain = (CardView) findViewById(R.id.layout_rain);
        layoutRain.setOnClickListener(this);
        tvRain = (TextView) findViewById(R.id.tvRain);
        dialog = new SpotsDialog(this);
        getData();

        new HomeActivity().setOnDConnectListener(new HomeActivity.OnDConnectListener() {
            @Override
            public void setDConnect() {
                UiUtils.showSackBar(SettingActivity.this,getString(R.string.disconnected),R.color.green,R.color.white);
            }
        });
    }

    private void getData() {
         String rainStr = "020022";
        HomeActivity.sendData(this,rainStr);


        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1) {
                    if (data.length() >= 10) {
                        if (data.substring(6, 10).equals("0020")) {
                            String str = data.substring(10, 12);
                            if (str.equals("02")) {
                                tvRain.setText("OFF");
                            } else if (str.equals("01")) {
                                tvRain.setText("ON");
                            }
                            HomeActivity.changeData();
                        }else  if (data.substring(6,10).equals("000F")){
                                    HomeActivity.sendData(SettingActivity.this,"020022");
                            HomeActivity.dismissDialog();
                        }
                    }
                }
            }
        };

        setThread = new SetThread();
        setThread.start();
    }

    @Override
    public void setWindowFeature() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        setThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setThread.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_boundary:
                startActivity(SettingActivity.this,BoundaryActivity.class);
                break;
            case R.id.layout_pin:
                startActivity(SettingActivity.this,ChangePwdActivity.class);
                break;
            case R.id.layout_multi:
                startActivity(SettingActivity.this,MultiareaActivity.class);
                break;
            case R.id.layout_reset:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
                builder1.setTitle(getString(R.string.prompt));
                builder1.setMessage(getString(R.string.allSettingReset));
                builder1.setNegativeButton(R.string.no,null);
                builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        String text = "02000F";
                        HomeActivity.sendData(SettingActivity.this,text);
                    }
                });
                builder1.create().show();
                break;
            case R.id.layout_review:
//                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
//                builder.setTitle(getString(R.string.prompt));
//                builder.setMessage(getString(R.string.review));
//                builder.setNegativeButton(R.string.no,null);
//                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog1, int which) {
//
//                    }
//                });
//                builder.create().show();
                startActivity(SettingActivity.this,AllReViewActivity.class);
                break;
            case R.id.layout_rain:
                AlertDialog.Builder builderRain = new AlertDialog.Builder(SettingActivity.this);
                builderRain.setTitle(getString(R.string.prompt));
                builderRain.setMessage(getString(R.string.rainSetting));
                builderRain.setNegativeButton("ON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = "03002301";
                        HomeActivity.sendData(SettingActivity.this,text);
                        tvRain.setText("ON");
                    }
                });
                builderRain.setPositiveButton("OFF", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        String text = "03002302";
                        HomeActivity.sendData(SettingActivity.this,text);
                        tvRain.setText("OFF");
                    }
                });
                builderRain.create().show();

                break;
        }
    }

    private static class SetThread extends Thread{

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
