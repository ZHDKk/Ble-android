package com.example.bluetooth.le.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.activity.BluetoothLeService;
import com.example.bluetooth.le.activity.BoundaryActivity;
import com.example.bluetooth.le.activity.ChangePwdActivity;
import com.example.bluetooth.le.activity.DeviceControlActivity;
import com.example.bluetooth.le.activity.HomeActivity;
import com.example.bluetooth.le.activity.OperatingActivity;
import com.example.bluetooth.le.activity.ScreenActivity;
import com.example.bluetooth.le.base.BaseFragment;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.kongqw.rockerlibrary.view.RockerView;
import com.shinelw.library.ColorArcProgressBar;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/8.
 * Time:9:05.
 */

public class StartFragment extends BaseFragment implements View.OnClickListener {
    private CardView btnStart;
    private CardView btnHome;
    private CardView btnInstall;
    private  CardView btnSensor;
    private CardView btnStop;

    private static ColorArcProgressBar bar;

    private  EasyFlipView easyFlipView;
    private boolean flipStatus;

    private CardView runView;
    private static FragmentCallback fragmentCallback;

    private  RockerView rockerView;
    private  TextView tvRunStatus;
    private Timer timer;
    private TimerTask timerTask;
    private static String callData;
//    private StartThread startThread;
    private static Handler handler;
    private int num = 1;
    private TextView tvPower,tvSignal;
    private SPUtils spUtils ;
    public  int checkNum;

    int flag=0;
    private String mDeviceName;


    public static String PERSONAL_FRAGMENT = "start_fragment";

    public static StartFragment newInstance(String msg) {
        StartFragment personalFragment = new StartFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PERSONAL_FRAGMENT, msg);
        personalFragment.setArguments(bundle);
        return personalFragment;
    }

    @Override
    public View initView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_start,null);
    }

    @Override
    protected void initFindViewById(View view) {
        btnStart = (CardView) view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        btnHome= (CardView) view.findViewById(R.id.btn_home);
        btnHome.setOnClickListener(this);
        btnInstall = (CardView) view.findViewById(R.id.btn_install);
        btnInstall.setOnClickListener(this);
        btnSensor = (CardView) view.findViewById(R.id.btn_sensor);
        btnSensor.setOnClickListener(this);
        btnStop = (CardView) view.findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(this);
        bar = (ColorArcProgressBar) view.findViewById(R.id.bar1);
        runView = (CardView) view.findViewById(R.id.runView);
        easyFlipView = (EasyFlipView) view.findViewById(R.id.easyFlipView);
        runView.setOnClickListener(this);
        easyFlipView.setFlipEnabled(true);
        easyFlipView.setFlipDuration(1000);
        flipStatus = easyFlipView.isFlipEnabled();

        rockerView = (RockerView) view.findViewById(R.id.rocker_View);
        rockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);

        tvRunStatus = (TextView) view.findViewById(R.id.run_status);
        tvPower= (TextView) view.findViewById(R.id.tvPower);
        tvSignal = (TextView) view.findViewById(R.id.tvSignal);

        mDeviceName = fragmentCallback.getHomeactivity().strDevice();


        spUtils = new SPUtils(getActivity(),"fileName");



        fragmentCallback.getHomeactivity().setOnCallDataListener(new HomeActivity.OnCallDataListener() {
            @Override
            public void callData(String result) {
                String data = BleUtils.bin2hex(result);
                if (data.length()>=12 && data.length()<=24) {
                    if (data.substring(6,12).equals("002300")) {
//                            if (!easyFlipView.isFlipEnabled()){
//                                easyFlipView.setFlipEnabled(true);
//                                easyFlipView.flipTheView();
//                            }
//                            rockerView.setVisibility(View.VISIBLE);
//                            btnSensor.setVisibility(View.VISIBLE);
                        btnHome.setEnabled(true);
                        tvRunStatus.setText(getString(R.string.running));
                        spUtils.putString("str",getString(R.string.running));
//                            spUtils.putInt("num",1);
                    }else if (data.substring(6,10).equals("0026")){

                        tvRunStatus.setText(R.string.stop);
                        spUtils.putString("str",getString(R.string.stop));
                    }
                    else if (data.substring(6,10).equals("0005")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
//                            if (rockerView.getVisibility() == View.VISIBLE) {
//                                rockerView.setVisibility(View.GONE);
//                                btnSensor.setVisibility(View.GONE);
//                                btnHome.setEnabled(false);
//                            }
//                            spUtils.putInt("num",2);
                    }else  if (data.substring(6,10).equals("0022")) {
//                        UiUtils.showSackBar(getActivity(), getString(R.string.closeCover), R.color.green, R.color.white);
//                        tvRunStatus.setText(R.string.closeCover);
//                            spUtils.putString("str",getString(R.string.closeCover));
//                        fragmentCallback.getHomeactivity().changeData();
                    }else if (data.substring(6, 12).equals("00230A")) {
                            tvRunStatus.setText(R.string.closeCover);
                            spUtils.putString("str",getString(R.string.closeCover));
//                            UiUtils.showSackBar(getActivity(), getString(R.string.closeCover), R.color.green, R.color.white);
                    }else if (data.substring(6,12).equals("002301")){
                        tvRunStatus.setText(R.string.home);
                        spUtils.putString("str",getString(R.string.home));
                    }
                    else if (data.substring(6, 12).equals("002302")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.outside);
                        spUtils.putString("str",getString(R.string.outside));
                    } else if (data.substring(6, 12).equals("002303")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.noSignal);
                        spUtils.putString("str",getString(R.string.noSignal));
                    } else if (data.substring(6, 12).equals("002304")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.lift);
                        spUtils.putString("str",getString(R.string.lift));
                    } else if (data.substring(6, 12).equals("002305")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.tilt);
                        spUtils.putString("str",getString(R.string.tilt));
                    } else if (data.substring(6, 12).equals("002306")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.pit);
                        spUtils.putString("str",getString(R.string.pit));
                    } else if (data.substring(6, 12).equals("002307")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.lowVoltage);
                        spUtils.putString("str",getString(R.string.lowVoltage));
                    } else if (data.substring(6, 12).equals("002308")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.waiting);
                        spUtils.putString("str",getString(R.string.waiting));
                    } else if (data.substring(6, 12).equals("002309")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.spareTime);
                        spUtils.putString("str",getString(R.string.spareTime));
                    } else if (data.substring(6, 12).equals("00230B")) {
//                            easyFlipView.flipTheView();
//                            easyFlipView.setFlipEnabled(false);
                        tvRunStatus.setText(R.string.chargeNow);
                        spUtils.putString("str",getString(R.string.chargeNow));
                    }else if (data.substring(6,12).equals("00230C")){
                        tvRunStatus.setText(R.string.stand);
                        spUtils.putString("str",getString(R.string.stand));
                    } else if (data.substring(6, 10).equals("0025")) {
                        if (data.length()>=16) {
                            int strPower = BleUtils.HexToInt(data.substring(14, 16));
                            bar.setCurrentValues(strPower);
                            tvPower.setText(strPower + "%");
                            spUtils.putInt("powerNum", strPower);
                            if (data.substring(10,12).equals("01")){
                                tvRunStatus.setText(getString(R.string.chargeNow));
                                spUtils.putString("str",getString(R.string.chargeNow));
                            }
                        }
                    }else if (data.substring(6,10).equals("001F")){
                        if (flag==0) {
                            UiUtils.showSackBar(getActivity(), getString(R.string.installok), R.color.green, R.color.white);
                            flag=1;
                        }
                    }else if (data.substring(6,10).equals("0024")) {
//                        String str = BleUtils.hexStr2Str(data.substring(10,12));
                        int str = Integer.parseInt(data.substring(10,12),16);
                        if (str==0){
                            tvRunStatus.setText(getString(R.string.strNull));
                            spUtils.putString("str",getString(R.string.strNull));
                        }else if (str==1){
                            tvRunStatus.setText(getString(R.string.strLowBat));
                            spUtils.putString("str",getString(R.string.strLowBat));
                        }else if (str==2){
                            tvRunStatus.setText(getString(R.string.noSignal));
                            spUtils.putString("str",getString(R.string.noSignal));
                        }else if (str==3){
                            tvRunStatus.setText(getString(R.string.outside));
                            spUtils.putString("str",getString(R.string.outside));
                        }else if (str==4){
                            tvRunStatus.setText(getString(R.string.pit));
                            spUtils.putString("str",getString(R.string.pit));
                        }else if (str==5){
                            tvRunStatus.setText(getString(R.string.strSlope));
                            spUtils.putString("str",getString(R.string.strSlope));
                        }else if (str==6){
                            tvRunStatus.setText(getString(R.string.lift));
                            spUtils.putString("str",getString(R.string.lift));
                        }else if (str==7){
                            tvRunStatus.setText(getString(R.string.strM1));
                            spUtils.putString("str",getString(R.string.strM1));
                        }else if (str==8){
                            tvRunStatus.setText(getString(R.string.strM2));
                            spUtils.putString("str",getString(R.string.strM2));
                        }else if (str==9){
                            tvRunStatus.setText(getString(R.string.strTurnOver));
                            spUtils.putString("str",getString(R.string.strTurnOver));
                        }else if (str == 10){
                            tvRunStatus.setText(getString(R.string.error01));
                            spUtils.putString("str",getString(R.string.error01));
                        }else if (str==11){
                            tvRunStatus.setText(getString(R.string.error20L));
                            spUtils.putString("str",getString(R.string.error20L));
                        }else if (str==12){
                            tvRunStatus.setText(getString(R.string.error21));
                            spUtils.putString("str",getString(R.string.error21));
                        }else if (str == 13){
                            tvRunStatus.setText(getString(R.string.error22));
                            spUtils.putString("str",getString(R.string.error22));
                        }else if (str==14){
                            tvRunStatus.setText(getString(R.string.error23));
                            spUtils.putString("str",getString(R.string.error23));
                        }else if (str==15){
                            tvRunStatus.setText(getString(R.string.error24));
                            spUtils.putString("str",getString(R.string.error24));
                        }else if (str==16){
                            tvRunStatus.setText(getString(R.string.error3));
                            spUtils.putString("str",getString(R.string.error3));
                        }else if (str == 17){
                            tvRunStatus.setText(getString(R.string.error4));
                            spUtils.putString("str",getString(R.string.error4));
                        }else if (str == 18){
                            tvRunStatus.setText(getString(R.string.trapped));
                            spUtils.putString("str",getString(R.string.trapped));
                        }else if (str==19){
                            tvRunStatus.setText(getString(R.string.error20M));
                            spUtils.putString("str",getString(R.string.error20M));
                        }else if (str==20){
                            tvRunStatus.setText(getString(R.string.error12c));
                            spUtils.putString("str",getString(R.string.error12c));
                        }else if (str == 28){
                            tvRunStatus.setText(getString(R.string.error20r));
                            spUtils.putString("str",getString(R.string.error20r));
                        }

                    }else if (data.substring(4,10).equals("03000A")){
                        String strSignal = data.substring(10, 12);
                        if (strSignal.equals("01")) {
                            tvSignal.setText("S1");
                        } else if (strSignal.equals("02")) {
                            tvSignal.setText("S2");
                        }else {
                            tvSignal.setText(R.string.noSignal);
                        }
                    }
                }else if (data.length()>29 && data.length()<=30){
                    if (data.substring(24,28).equals("2308")){
                        tvRunStatus.setText(R.string.waiting);
                        spUtils.putString("str",getString(R.string.waiting));
                    }else if (data.substring(24,28).equals("230C")){
                        tvRunStatus.setText(R.string.stand);
                        spUtils.putString("str",getString(R.string.stand));
                    }
                }else if (data.length()>=31 && data.length()<=32){
                    if (data.substring(26,30).equals("230A")){
                        tvRunStatus.setText(R.string.closeCover);
                        spUtils.putString("str",getString(R.string.closeCover));
                    }else if (data.substring(26,30).equals("2301")){
                        tvRunStatus.setText(R.string.home);
                        spUtils.putString("str",getString(R.string.home));
                    }else if (data.substring(26,30).equals("2308")){
                        tvRunStatus.setText(R.string.waiting);
                        spUtils.putString("str",getString(R.string.waiting));
                    }else if (data.substring(26,30).equals("2300")){
                        tvRunStatus.setText(getString(R.string.running));
                        spUtils.putString("str",getString(R.string.running));
                    }else if (data.substring(26,30).equals("230C")){
                        tvRunStatus.setText(R.string.stand);
                        spUtils.putString("str",getString(R.string.stand));
                    }
                }else if (data.length()>=49 && data.length()<=50){
                    if (data.substring(42,48).equals("002301")){
                        tvRunStatus.setText(R.string.home);
                        spUtils.putString("str",getString(R.string.home));
                    }else if (data.substring(42,48).equals("002300")){
                        tvRunStatus.setText(getString(R.string.running));
                        spUtils.putString("str",getString(R.string.running));
                    }else if (data.substring(42,48).equals("00230C")){
                        tvRunStatus.setText(R.string.stand);
                        spUtils.putString("str",getString(R.string.stand));
                    }
                }else if (data.length()>=47 && data.length()<=48){
                    if (data.substring(42,46).equals("2308")){
                        tvRunStatus.setText(R.string.waiting);
                        spUtils.putString("str",getString(R.string.waiting));
                    }else if (data.substring(42,46).equals("230C")){
                        tvRunStatus.setText(R.string.stand);
                        spUtils.putString("str",getString(R.string.stand));
                    }
                }else if (data.length() == 68){
                    if (data.substring(60,66).equals("002300")){
                        tvRunStatus.setText(getString(R.string.running));
                        spUtils.putString("str",getString(R.string.running));
                    }else if (data.substring(60,66).equals("002301")){
                        tvRunStatus.setText(R.string.home);
                        spUtils.putString("str",getString(R.string.home));
                    }else if (data.substring(60,66).equals("00230C")){
                        tvRunStatus.setText(R.string.stand);
                        spUtils.putString("str",getString(R.string.stand));
                    }
                }else if (data.length()==60){
                    if (data.substring(52,58).equals("002300")){
                        tvRunStatus.setText(getString(R.string.running));
                        spUtils.putString("str",getString(R.string.running));
                    }else if (data.substring(52,58).equals("002301")){
                        tvRunStatus.setText(R.string.home);
                        spUtils.putString("str",getString(R.string.home));
                    }else if (data.substring(52,58).equals("00230C")){
                        tvRunStatus.setText(R.string.stand);
                        spUtils.putString("str",getString(R.string.stand));
                    }
                }
            }
        });
//        handler  = new Handler(getActivity().getMainLooper()){
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                String data = msg.obj.toString();
//                if(msg.what == 1){
//                    if (data.length()>=12 && data.length()<=24) {
//                        if (data.substring(6,12).equals("002300")) {
////                            if (!easyFlipView.isFlipEnabled()){
////                                easyFlipView.setFlipEnabled(true);
////                                easyFlipView.flipTheView();
////                            }
////                            rockerView.setVisibility(View.VISIBLE);
////                            btnSensor.setVisibility(View.VISIBLE);
//                            btnHome.setEnabled(true);
//                            tvRunStatus.setText(getString(R.string.running));
//                            spUtils.putString("str",getString(R.string.running));
////                            spUtils.putInt("num",1);
//                        }else if (data.substring(6,10).equals("0026")){
//
//                            tvRunStatus.setText(R.string.stop);
//                            spUtils.putString("str",getString(R.string.stop));
//                        }
//                        else if (data.substring(6,10).equals("0005")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
////                            if (rockerView.getVisibility() == View.VISIBLE) {
////                                rockerView.setVisibility(View.GONE);
////                                btnSensor.setVisibility(View.GONE);
////                                btnHome.setEnabled(false);
////                            }
////                            spUtils.putInt("num",2);
//                        }else  if (data.substring(6,10).equals("0022")) {
//                                UiUtils.showSackBar(getActivity(), getString(R.string.closeCover), R.color.green, R.color.white);
//                            fragmentCallback.getHomeactivity().changeData();
//                        }else if (data.substring(6, 12).equals("00230A")) {
////                            tvRunStatus.setText(R.string.closeCover);
////                            spUtils.putString("str",getString(R.string.closeCover));
////                            UiUtils.showSackBar(getActivity(), getString(R.string.closeCover), R.color.green, R.color.white);
//                        }else if (data.substring(6,12).equals("002301")){
//                            tvRunStatus.setText(R.string.home);
//                            spUtils.putString("str",getString(R.string.home));
//                        }
//                        else if (data.substring(6, 12).equals("002302")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.outside);
//                            spUtils.putString("str",getString(R.string.outside));
//                        } else if (data.substring(6, 12).equals("002303")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.noSignal);
//                            spUtils.putString("str",getString(R.string.noSignal));
//                        } else if (data.substring(6, 12).equals("002304")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.lift);
//                            spUtils.putString("str",getString(R.string.lift));
//                        } else if (data.substring(6, 12).equals("002305")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.tilt);
//                            spUtils.putString("str",getString(R.string.tilt));
//                        } else if (data.substring(6, 12).equals("002306")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.pit);
//                            spUtils.putString("str",getString(R.string.pit));
//                        } else if (data.substring(6, 12).equals("002307")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.lowVoltage);
//                            spUtils.putString("str",getString(R.string.lowVoltage));
//                        } else if (data.substring(6, 12).equals("002308")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.waiting);
//                            spUtils.putString("str",getString(R.string.waiting));
//                        } else if (data.substring(6, 12).equals("002309")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.spareTime);
//                            spUtils.putString("str",getString(R.string.spareTime));
//                        } else if (data.substring(6, 12).equals("00230B")) {
////                            easyFlipView.flipTheView();
////                            easyFlipView.setFlipEnabled(false);
//                            tvRunStatus.setText(R.string.chargeNow);
//                            spUtils.putString("str",getString(R.string.chargeNow));
//                        }else if (data.substring(6,12).equals("00230C")){
//                            tvRunStatus.setText(R.string.stand);
//                            spUtils.putString("str",getString(R.string.stand));
//                        } else if (data.substring(6, 10).equals("0025")) {
//                            if (data.length()>=16) {
//                                int strPower = BleUtils.HexToInt(data.substring(14, 16));
//                                bar.setCurrentValues(strPower);
//                                tvPower.setText(strPower + "%");
//                                spUtils.putInt("powerNum", strPower);
//                                if (data.substring(10,12).equals("01")){
//                                    tvRunStatus.setText(getString(R.string.chargeNow));
//                                    spUtils.putString("str",getString(R.string.chargeNow));
//                                }
//                            }
//                        }else if (data.substring(6,10).equals("001F")){
//                            if (flag==0) {
//                                UiUtils.showSackBar(getActivity(), getString(R.string.installok), R.color.green, R.color.white);
//                                flag=1;
//                            }
//                        }else if (data.substring(4,10).equals("03000A")){
//                            String strSignal = data.substring(10, 12);
//                            if (strSignal.equals("01")) {
//                                tvSignal.setText("S1");
//                            } else if (strSignal.equals("02")) {
//                                tvSignal.setText("S2");
//                            }else {
//                                tvSignal.setText(R.string.noSignal);
//                            }
//                        }
//                    }else if (data.length()>29 && data.length()<=30){
//                        if (data.substring(24,28).equals("2308")){
//                            tvRunStatus.setText(R.string.waiting);
//                            spUtils.putString("str",getString(R.string.waiting));
//                        }
//                    }else if (data.length()>=31 && data.length()<=32){
//                        if (data.substring(26,30).equals("230A")){
//                            tvRunStatus.setText(R.string.closeCover);
//                            spUtils.putString("str",getString(R.string.closeCover));
//                        }else if (data.substring(26,30).equals("2301")){
//                            tvRunStatus.setText(R.string.home);
//                            spUtils.putString("str",getString(R.string.home));
//                        }else if (data.substring(26,30).equals("2308")){
//                            tvRunStatus.setText(R.string.waiting);
//                            spUtils.putString("str",getString(R.string.waiting));
//                        }else if (data.substring(26,30).equals("2300")){
//                            tvRunStatus.setText(getString(R.string.running));
//                            spUtils.putString("str",getString(R.string.running));
//                        }
//                    }else if (data.length()>=49 && data.length()<=50){
//                        if (data.substring(42,48).equals("002301")){
//                            tvRunStatus.setText(R.string.home);
//                            spUtils.putString("str",getString(R.string.home));
//                        }else if (data.substring(42,48).equals("002300")){
//                            tvRunStatus.setText(getString(R.string.running));
//                            spUtils.putString("str",getString(R.string.running));
//                        }
//                    }else if (data.length()>=47 && data.length()<=48){
//                        if (data.substring(42,46).equals("2308")){
//                            tvRunStatus.setText(R.string.waiting);
//                            spUtils.putString("str",getString(R.string.waiting));
//                        }
//                    }else if (data.length() == 68){
//                        if (data.substring(60,66).equals("002300")){
//                            tvRunStatus.setText(getString(R.string.running));
//                            spUtils.putString("str",getString(R.string.running));
//                        }else if (data.substring(60,66).equals("002301")){
//                            tvRunStatus.setText(R.string.home);
//                            spUtils.putString("str",getString(R.string.home));
//                        }
//                    }
//                }
//            }
//        };
        checkNum = spUtils.getInt("num",0);
        int strPower= spUtils.getInt("powerNum",0);
        String str = spUtils.getString("str","str");
        if (!TextUtils.isEmpty(str)){
            tvRunStatus.setText(str);
        }

        if (strPower!=0) {
            bar.setCurrentValues(strPower);
            tvPower.setText(strPower + "%");
        }


        if (checkNum == 1){
//            if (!easyFlipView.isFlipEnabled()){
//                easyFlipView.setFlipEnabled(true);
//                easyFlipView.flipTheView();
//            }
//            rockerView.setVisibility(View.VISIBLE);
//            btnSensor.setVisibility(View.VISIBLE);
//            btnHome.setEnabled(true);
        }else if (checkNum==2){
//            easyFlipView.setFlipEnabled(true);
//            easyFlipView.flipTheView();
//            bar.setCurrentValues(83);
//            if (rockerView.getVisibility() == View.VISIBLE) {
                rockerView.setVisibility(View.GONE);
                btnSensor.setVisibility(View.GONE);
//            btnHome.setEnabled(false);
//            }
        }


        fragmentCallback
                .getHomeactivity().setOnClickDisConnectListener(new HomeActivity.OnDisConnectListener() {
            @Override
            public void setDisConnect(String result) {
                mDeviceName = result;
                tvRunStatus.setText(R.string.nostatus);
                spUtils.putString("str",getString(R.string.nostatus));
            }
        });

        fragmentCallback.getHomeactivity().setOnConnectListener(new HomeActivity.OnConnectListener() {
            @Override
            public void setConnect() {
//                if (!TextUtils.isEmpty(mDeviceName) && mDeviceName!=null && !mDeviceName.equals("")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String single = "02000A";
                        fragmentCallback.getHomeactivity().sendData(getActivity(), single);
                        fragmentCallback.getHomeactivity().dismissDialog();
                    }
                },500);

//                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentCallback.getHomeactivity().sendData(getActivity(),"020025");
                        tvRunStatus.setText(R.string.getStatus);
                        spUtils.putString("str",getString(R.string.getStatus));
                        fragmentCallback.getHomeactivity().dismissDialog();
                    }
                },1000);

            }
        });
    }

    private void clearData() {
        fragmentCallback.getHomeactivity().sendData(getActivity(),"1234");
        fragmentCallback.getHomeactivity().changeData();
        fragmentCallback.getHomeactivity().dismissDialog();
    }


    @Override
    public void initData() {
        rockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_4_ROTATE_45, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(RockerView.Direction direction) {
                tvRunStatus.setText(getDirection(direction));
                String directionStr = getDirection(direction);
                if (directionStr.equals("left")){
                    fragmentCallback.getHomeactivity().sendData(getActivity(),"01");
                }
                if (directionStr.equals("right")){
                    fragmentCallback.getHomeactivity().sendData(getActivity(),"02");
                }
                if (directionStr.equals("up")){
                    fragmentCallback.getHomeactivity().sendData(getActivity(),"03");
                }
                if (directionStr.equals("down")){
                    fragmentCallback.getHomeactivity().sendData(getActivity(),"04");
                }
                HomeActivity.dismissDialog();
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private String getDirection(RockerView.Direction direction) {
        String message = null;
        switch (direction) {
            case DIRECTION_LEFT:
                message = "left";
                break;
            case DIRECTION_RIGHT:
                message = "right";
                break;
            case DIRECTION_UP:
                message = "up";
                break;
            case DIRECTION_DOWN:
                message = "down";
                break;
            default:
                break;
        }
        return message;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentCallback = (FragmentCallback) activity;
    }


    @Override
    public void onResume() {
        super.onResume();
//        startThread =   new StartThread();
//        startThread.start();


    }

    @Override
    public void onPause() {
        super.onPause();

//        startThread.close();
    }

    @Override
    public void onStop() {
        super.onStop();
//        startThread.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (!TextUtils.isEmpty(mDeviceName) && mDeviceName!=null && !mDeviceName.equals("")) {
                    String startStr = "020004";
                    fragmentCallback.getHomeactivity().sendData(getActivity(), startStr);
                }else {
                    ToastUtil.showToast(getActivity(),getString(R.string.connectDevice));
                }
                break;
            case R.id.btn_home:
                if (!TextUtils.isEmpty(mDeviceName) && mDeviceName!=null && !mDeviceName.equals("")) {
                    String homeStr = "020005";
                    fragmentCallback.getHomeactivity().sendData(getActivity(), homeStr);
                }else {
                    ToastUtil.showToast(getActivity(),getString(R.string.connectDevice));
                }
                break;
            case R.id.btn_install:
                if (!TextUtils.isEmpty(mDeviceName) && mDeviceName!=null && !mDeviceName.equals("")) {
                    String installStr = "020021";
                    fragmentCallback.getHomeactivity().sendData(getActivity(), installStr);
                    flag=0;
                }else {
                    ToastUtil.showToast(getActivity(),getString(R.string.connectDevice));
                }

                break;
            case R.id.btn_stop:
                if (!TextUtils.isEmpty(mDeviceName) && mDeviceName!=null && !mDeviceName.equals("")) {
                    String stopStr = "020024";
                    fragmentCallback.getHomeactivity().sendData(getActivity(), stopStr);
                }else {
                    ToastUtil.showToast(getActivity(),getString(R.string.connectDevice));
                }
                break;
            case R.id.btn_sensor:
                startActivity(getActivity(), OperatingActivity.class);
                break;
        }
    }

//    private static class StartThread extends Thread{
//
//        private boolean mRunning = false;
//        @Override
//        public void run() {
//            mRunning = true;
//            while (mRunning) {
//                callData = fragmentCallback.getHomeactivity().callBackData();
//                try {
//                    Thread.sleep(10);
//                    if (!TextUtils.isEmpty(callData)) {
//
//                        Message msg = new Message();
//                        msg.what = 1;
//                        msg.obj = callData;
//                        handler.sendMessage(msg);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//
//        }
//        public void close() {
//            mRunning = false;
//        }
//
//    }

}
