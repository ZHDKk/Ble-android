package com.example.bluetooth.le.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.activity.BluetoothLeService;
import com.example.bluetooth.le.activity.BoundaryActivity;
import com.example.bluetooth.le.activity.DeviceControlActivity;
import com.example.bluetooth.le.activity.HomeActivity;
import com.example.bluetooth.le.base.BaseFragment;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.example.bluetooth.le.view.SetDayPopuWindow;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/8.
 * Time:9:05.
 */

public class TimerFragment extends BaseFragment implements View.OnClickListener {
    private CardView layoutDay,layoutTime;
    private SetDayPopuWindow popuWindow;
    private LinearLayout layoutTimer,layoutTimes;
    private ImageView iconMore;
    private TextView tvT1,tvT2,tvT3;
    private static String callData;//机器返回的时间
    private String time;
    private TimerThread timerThread;
    private static FragmentCallback fragmentCallback;
    private static Handler handler;


    public static String PERSONAL_FRAGMENT = "timer_fragemnt";

    public static TimerFragment newInstance(String msg) {
        TimerFragment personalFragment = new TimerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PERSONAL_FRAGMENT, msg);
        personalFragment.setArguments(bundle);
        return personalFragment;
    }
    @Override
    public View initView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_timer,null);
    }

    @Override
    protected void initFindViewById(View view) {

        layoutDay = (CardView) view.findViewById(R.id.layout_setDay);
        layoutTime = (CardView) view.findViewById(R.id.layout_setTime);
        layoutTimer = (LinearLayout) view.findViewById(R.id.layout_timer);
        iconMore = (ImageView) view.findViewById(R.id.icon_more);
        layoutTimes= (LinearLayout) view.findViewById(R.id.layout_times);
        layoutTimes.setVisibility(View.GONE);
        tvT1= (TextView) view.findViewById(R.id.tv_t1);
        tvT1.setOnClickListener(this);
        tvT2= (TextView) view.findViewById(R.id.tv_t2);
        tvT2.setOnClickListener(this);
        tvT3= (TextView) view.findViewById(R.id.tv_t3);
        tvT3.setOnClickListener(this);

        handler  = new Handler(getActivity().getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1) {
                    if (data.length() >= 12) {
                        if (data.substring(6, 10).equals("0011")) {
                            UiUtils.backgroundAlpha(0.5f, getActivity());
                            popuWindow = new SetDayPopuWindow(getActivity(), data.substring(10, 12));


                            popuWindow.showAtLocation(getActivity().findViewById(R.id.layout_timer), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                            popuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    UiUtils.backgroundAlpha(1f, getActivity());
                                }
                            });

                            //日期设置回调
                            popuWindow.setOnCheckListener(new SetDayPopuWindow.OnCheckListener() {
                                @Override
                                public void setCommand(final String text) {
                                    fragmentCallback.getHomeactivity().sendData(getActivity(), text);

                                }
                            });
                            fragmentCallback.getHomeactivity().changeData();
                        } else if (data.substring(6, 10).equals("0013")) {
                            layoutTimes.setVisibility(View.VISIBLE);
                            iconMore.setImageResource(R.drawable.under);
                            fragmentCallback.getHomeactivity().changeData();

                        /*
                                TODO:返回的时间数据data
                             */
                            if (data.length() >= 80) {
                                String str1 = data.substring(10, 32);
                                String time1 = BleUtils.hexStr2Str(str1);

                                String str2 = data.substring(34, 56);
                                String time2 = BleUtils.hexStr2Str(str2);

                                String str3 = data.substring(58, 80);
                                String time3 = BleUtils.hexStr2Str(str3);

                                tvT1.setText(time1);
                                tvT2.setText(time2);
                                tvT3.setText(time3);
                            }
                        }
                    }
                }
            }
        };


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentCallback = (FragmentCallback) activity;
    }

    @Override
    public void initData() {
        layoutDay.setOnClickListener(this);
        layoutTime.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        timerThread =   new TimerThread();
        timerThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        timerThread.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        timerThread.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_setDay:
                String dayStr = "020011";
                fragmentCallback.getHomeactivity().sendData(getActivity(),dayStr);
                break;
            case R.id.layout_setTime:
                        if (layoutTimes.getVisibility() == View.GONE){
                            String timeStr = "020013";
                            fragmentCallback.getHomeactivity().sendData(getActivity(),timeStr);

                        }else if (layoutTimes.getVisibility() == View.VISIBLE){
                            layoutTimes.setVisibility(View.GONE);
                            iconMore.setImageResource(R.drawable.more);
                        }

                break;
            case R.id.tv_t1:
                setTime("0D0014",tvT1);
                break;
            case R.id.tv_t2:
                setTime("0D0015",tvT2);
                break;
            case R.id.tv_t3:
                setTime("0D0016",tvT3);
                break;
        }

    }

    private void setTime(final String str, final TextView tv) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog
                .newInstance(new TimePickerDialog
                                     .OnTimeSetListener() {
                                 @Override
                 public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
                                     String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                                     String minuteString = minute < 10 ? "0"+minute : ""+minute;
                                     String hourStringEnd = hourOfDayEnd < 10 ? "0"+hourOfDayEnd : ""+hourOfDayEnd;
                                     String minuteStringEnd = minuteEnd < 10 ? "0"+minuteEnd : ""+minuteEnd;
                                     time = hourString+":"+minuteString+"-"+hourStringEnd+":"+minuteStringEnd;
                                     String times = BleUtils.bin2hex2(time);
                                     String text =str+times;
                                     fragmentCallback.getHomeactivity().sendData(getActivity(),text);
                                     String timer = hourString+":"+minuteString+"-"+hourStringEnd+":"+minuteStringEnd;
                                     tv.setText(timer);
                                 }
                             },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false);
        tpd.show(getActivity().getFragmentManager(),"Timepickerdialog");
    }

    private static class TimerThread extends Thread{

        private boolean mRunning = false;
        @Override
        public void run() {
            mRunning = true;
            while (mRunning) {
                try {
                    Thread.sleep(1000);
                    callData = fragmentCallback.getHomeactivity().callBackData();
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
