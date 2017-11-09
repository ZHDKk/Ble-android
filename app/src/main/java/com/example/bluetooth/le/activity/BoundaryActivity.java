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
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;

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
 * Time:16:27.
 */

public class BoundaryActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
   private CardView layoutTrim,layoutWidth,layoutSignal;
    private Toolbar toolbar;
    private TextView tvWidth,tvSignal;


    private RadioGroup radioGroup;
    private RadioButton radioBtn1,radioBtn2,radioBtn3,radioBtn4;
    private TextView tv_Width;
    private TextView tvTrim;


    private static Handler handler;
    private static String callData;
    private BoundaryThread boundaryThread;
    @Override
    public int getContentView() {
        return R.layout.boundary_layout;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.boundary);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layoutTrim = (CardView) findViewById(R.id.layout_trim);
        layoutTrim.setOnClickListener(this);
        layoutWidth = (CardView) findViewById(R.id.layout_width);
        layoutWidth.setOnClickListener(this);
        layoutSignal = (CardView) findViewById(R.id.layout_signal);
        layoutSignal.setOnClickListener(this);
        tvWidth= (TextView) findViewById(R.id.tvWidth);
        tvSignal= (TextView) findViewById(R.id.tvSignal);
        tvTrim = (TextView) findViewById(R.id.tvTrim);

        getData();
        HomeActivity.dismissDialog();
    }

    private void getData() {
        String trim = "020006";
        final String width = "020008";
         final String single = "02000A";
        HomeActivity.sendData(this,trim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeActivity.sendData(BoundaryActivity.this,width);
            }
        },500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeActivity.sendData(BoundaryActivity.this,single);
            }
        },1000);

        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1){
                    if (data.length() >=10) {
                        if (data.substring(6, 10).equals("0008")) {
                            String strWidth = data.substring(10, 12);
                            if (strWidth.equals("02")) {
                                tvWidth.setText("0.2");
                            } else if (strWidth.equals("03")) {
                                tvWidth.setText("0.3");
                            } else if (strWidth.equals("04")) {
                                tvWidth.setText("0.4");
                            } else if (strWidth.equals("05")) {
                                tvWidth.setText("0.5");
                            }
                        } else if (data.substring(6, 10).equals("0006")) {
                            String strTrim = data.substring(10, 12);
                            if (strTrim.equals("01")) {
                                tvTrim.setText(R.string.yes);
                            } else if (strTrim.equals("00")) {
                                tvTrim.setText(R.string.no);
                            }
                        } else if (data.substring(4, 10).equals("03000A")) {

                            String strSignal = data.substring(10, 12);
                            if (strSignal.equals("01")) {
                                tvSignal.setText("S1");
                            } else if (strSignal.equals("02")) {
                                tvSignal.setText("S2");
                            }
                        }
                        HomeActivity.changeData();
                    }

                }
            }
        };

    }

    @Override
    public void setWindowFeature() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        boundaryThread = new BoundaryThread();
        boundaryThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boundaryThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boundaryThread.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_trim:
                AlertDialog.Builder builder = new AlertDialog.Builder(BoundaryActivity.this);
                builder.setTitle(getString(R.string.prompt));
                builder.setMessage(getString(R.string.trimming));
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = "03000700";
                        HomeActivity.sendData(BoundaryActivity.this,text);
                        tvTrim.setText(getString(R.string.no));
                    }
                });
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog1, int which) {
                        String text = "03000701";
                        HomeActivity.sendData(BoundaryActivity.this,text);
                        tvTrim.setText(getString(R.string.yes));
                    }
                });
                builder.create().show();
                break;
            case R.id.layout_width:
                AlertDialog.Builder widthBuilder  = new AlertDialog.Builder(BoundaryActivity.this);
                widthBuilder.setTitle(getString(R.string.prompt));
                        View view = LayoutInflater.from(BoundaryActivity.this).inflate(R.layout.editwidth_dialog,null);
                 tv_Width = (TextView) view.findViewById(R.id.tvWidth);
                radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup1);
                radioGroup.setOnCheckedChangeListener(this);
                radioBtn1 = (RadioButton) view.findViewById(R.id.radio0);
                radioBtn2 = (RadioButton) view.findViewById(R.id.radio1);
                radioBtn3 = (RadioButton) view.findViewById(R.id.radio2);
                radioBtn4 = (RadioButton) view.findViewById(R.id.radio3);
                widthBuilder.setNegativeButton(R.string.no,null);
                widthBuilder.setView(view);
                String widthStr = tvWidth.getText().toString();
                if (widthStr.equals("0.2")){
                    radioBtn1.setChecked(true);
                    tv_Width.setText("0.2");
                }else  if (widthStr.equals("0.3")){
                    radioBtn2.setChecked(true);
                    tv_Width.setText("0.3");
                }else  if (widthStr.equals("0.4")){
                    radioBtn3.setChecked(true);
                    tv_Width.setText("0.4");
                }else  if (widthStr.equals("0.5")){
                    radioBtn4.setChecked(true);
                    tv_Width.setText("0.5");
                }
                widthBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, final int which) {
                        String width = "02";
                        String widthStr="0.2";
                        if (radioBtn1.isChecked()){
                            width =   "02";
                            widthStr="0.2";
                        }else if (radioBtn2.isChecked()){
                            width = "03";
                            widthStr="0.3";
                        }else if (radioBtn3.isChecked()){
                            width = "04";
                            widthStr="0.4";
                        }else if (radioBtn4.isChecked()){
                            width = "05";
                            widthStr="0.5";
                        }
                        String text = "030009"+width;
                        HomeActivity.sendData(BoundaryActivity.this,text);
                        tvWidth.setText(widthStr);
                    }
                });
                widthBuilder.create().show();
                break;
            case R.id.layout_signal:
                AlertDialog.Builder signalBuilder  = new AlertDialog.Builder(BoundaryActivity.this);
                signalBuilder.setTitle(getString(R.string.prompt));
                signalBuilder.setMessage(getString(R.string.signal));
                signalBuilder.setNegativeButton("S1", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog3, int which) {
                        setSignale("03000B01");
                        tvSignal.setText("S1");

                    }
                });
                signalBuilder.setPositiveButton("S2", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog4, int which) {
                        setSignale("03000B02");
                        tvSignal.setText("S2");

                    }
                });
                signalBuilder.create().show();
                break;
        }

    }

    private void setSignale( String text) {
                    HomeActivity.sendData(BoundaryActivity.this,text);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i){
            case R.id.radio0:
                tv_Width.setText("0.2");
                radioBtn1.setChecked(true);
                radioBtn1.setTextColor(getResources().getColor(R.color.white));
                radioBtn1.setBackgroundColor(getResources().getColor(R.color.green));
                radioBtn2.setTextColor(getResources().getColor(R.color.black));
                radioBtn2.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                radioBtn3.setTextColor(getResources().getColor(R.color.black));
                radioBtn3.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                radioBtn4.setTextColor(getResources().getColor(R.color.black));
                radioBtn4.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                break;
            case R.id.radio1:
                tv_Width.setText("0.3");
                radioBtn2.setChecked(true);
                radioBtn2.setTextColor(getResources().getColor(R.color.white));
                radioBtn2.setBackgroundColor(getResources().getColor(R.color.green));
                radioBtn1.setTextColor(getResources().getColor(R.color.black));
                radioBtn1.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                radioBtn3.setTextColor(getResources().getColor(R.color.black));
                radioBtn3.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                radioBtn4.setTextColor(getResources().getColor(R.color.black));
                radioBtn4.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                break;
            case R.id.radio2:
                tv_Width.setText("0.4");
                radioBtn3.setChecked(true);
                radioBtn3.setTextColor(getResources().getColor(R.color.white));
                radioBtn3.setBackgroundColor(getResources().getColor(R.color.green));

                radioBtn2.setTextColor(getResources().getColor(R.color.black));
                radioBtn2.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                radioBtn1.setTextColor(getResources().getColor(R.color.black));
                radioBtn1.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                radioBtn4.setTextColor(getResources().getColor(R.color.black));
                radioBtn4.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                break;
            case R.id.radio3:
                tv_Width.setText("0.5");
                radioBtn4.setChecked(true);
                radioBtn4.setTextColor(getResources().getColor(R.color.white));
                radioBtn4.setBackgroundColor(getResources().getColor(R.color.green));

                radioBtn1.setTextColor(getResources().getColor(R.color.black));
                radioBtn1.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                radioBtn2.setTextColor(getResources().getColor(R.color.black));
                radioBtn2.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                radioBtn3.setTextColor(getResources().getColor(R.color.black));
                radioBtn3.setBackgroundColor(getResources().getColor(R.color.grey_bg));
                break;
        }
    }


    private static class BoundaryThread extends Thread{

        private boolean mRunning = false;
        @Override
        public void run() {
            mRunning = true;
            while (mRunning) {
                try {
                    Thread.sleep(200);
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
