package com.example.bluetooth.le.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/14.
 * Time:11:32.
 */

public  class DateActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
//    private ClockView clockView;
    private CardView layoutDate,layoutTime;
    private TextView tvDate,tvTime;

    byte[] writeBytes = new byte[20];

    private static String callData;
    private static Handler handler;
    private DateThread dateThread;

    @Override
    public int getContentView() {
        return R.layout.date_layout;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.timeAndDate));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        clockView = (ClockView) findViewById(R.id.clockView);
        layoutDate = (CardView) findViewById(R.id.layout_date);
        layoutDate.setOnClickListener(this);
        layoutTime = (CardView) findViewById(R.id.layout_time);
        layoutTime.setOnClickListener(this);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);

        getData();
        HomeActivity.dismissDialog();

        new HomeActivity().setOnDConnectListener(new HomeActivity.OnDConnectListener() {
            @Override
            public void setDConnect() {
                UiUtils.showSackBar(DateActivity.this,getString(R.string.disconnected),R.color.green,R.color.white);
            }
        });

    }

    private void getData() {
        final String dateStr = "02001B";
        final String timeStr = "02001D";
        HomeActivity.sendData(DateActivity.this,dateStr);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeActivity.sendData(DateActivity.this,timeStr);
            }
        },500);

        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1){
                        if (data.substring(6, 10).equals("001B")) {
                            String time = data.substring(10, 20);
                            tvTime.setText(BleUtils.hexStr2Str(time));
                        } else if (data.length() <= 32 && data.length() > 25 && data.substring(6,10).equals("0019")) {
                            String date = data.substring(10, 30);
                            tvDate.setText(BleUtils.hexStr2Str(date));

                        } else if (data.length() >= 56 && data.substring(6,10).equals("001B") || data.substring(6,10).equals("0019") && data.length() >= 56) {

                            String date = data.substring(10, 30);
                            tvDate.setText(BleUtils.hexStr2Str(date));

                            String time = data.substring(46, 56);
                            tvTime.setText(BleUtils.hexStr2Str(time));
                        }

                        HomeActivity.changeData();
                }
            }
        };
        dateThread =  new DateThread();
        dateThread.start();
    }


    @Override
    public void setWindowFeature() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        dateThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dateThread.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_date:
                Calendar now = Calendar.getInstance();
               DatePickerDialog datePickerDialog = new DatePickerDialog(DateActivity.this, new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                       String month = (monthOfYear+1)<10 ? "0"+(monthOfYear+1) :""+(monthOfYear+1);
                       String day  = dayOfMonth <10 ? "0"+dayOfMonth : ""+dayOfMonth;
                       String date = year+"."+month+"."+day;
                       String dates = BleUtils.bin2hex2(date);
                       String text = "0C001C"+dates;
                       HomeActivity.sendData(DateActivity.this,text);
                       tvDate.setText(date);
                   }
               }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.layout_time:
                Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(DateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String mHour =  hour < 10 ? "0"+hour : ""+hour;
                        String mMinute = minute < 10 ? "0"+minute : ""+minute;
                        String  time = mHour+":"+mMinute;
                        String times = BleUtils.bin2hex2(time);
                        String text = "07001E"+times;
                        HomeActivity.sendData(DateActivity.this,text);
                        tvTime.setText(time);
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false).show();
                break;
        }
    }

    private static class DateThread extends Thread{

        private boolean mRunning = false;
        @Override
        public void run() {
            mRunning = true;
            while (mRunning) {
                try {
                    Thread.sleep(100);
                    callData = HomeActivity.callBackData();
                    if (!TextUtils.isEmpty(callData)&& callData.length()>=10) {
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
