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
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.model.FaultBean;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

/**
 * Created by zhdk on 2017/7/28.
 */

public class MultiareaActivity extends BaseActivity implements View.OnClickListener {
    private CardView layoutMultiaA,layoutMultiaB,layoutMultiaC,layoutMultiaD;
    private TextView tvLengthA,tvPercentA,tvLengthB,tvPercentB,tvLengthC,tvPercentC,tvLengthD,tvPercentD;
    private Toolbar toolbar;

    byte[] writeBytes = new byte[20];


    private static Handler handler;
    private static String callData;
    private MultiThread multiThread;
    @Override
    public int getContentView() {
        return R.layout.multiarea_layout;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.multiArea));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layoutMultiaA = (CardView) findViewById(R.id.layout_multiaA);
        layoutMultiaB = (CardView) findViewById(R.id.layout_multiaB);
        layoutMultiaC = (CardView) findViewById(R.id.layout_multiaC);
        layoutMultiaD = (CardView) findViewById(R.id.layout_multiaD);
        tvLengthA= (TextView) findViewById(R.id.tv_lengthA);
        tvLengthB= (TextView) findViewById(R.id.tv_lengthB);
        tvLengthC= (TextView) findViewById(R.id.tv_lengthC);
        tvLengthD= (TextView) findViewById(R.id.tv_lengthD);
        tvPercentA= (TextView) findViewById(R.id.tv_percentA);
        tvPercentB= (TextView) findViewById(R.id.tv_percentB);
        tvPercentC= (TextView) findViewById(R.id.tv_percentC);
        tvPercentD= (TextView) findViewById(R.id.tv_percentD);
        layoutMultiaA.setOnClickListener(this);
        layoutMultiaB.setOnClickListener(this);
        layoutMultiaC.setOnClickListener(this);
        layoutMultiaD.setOnClickListener(this);
        getData();

        new HomeActivity().setOnDConnectListener(new HomeActivity.OnDConnectListener() {
            @Override
            public void setDConnect() {
                UiUtils.showSackBar(MultiareaActivity.this,getString(R.string.disconnected),R.color.green,R.color.white);
            }
        });

    }

    private void getData() {
        String multiaStr = "02000D";
        HomeActivity.sendData(this,multiaStr);


        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1) {
                    if (data.length() >= 10) {
                        if (data.substring(6, 10).equals("000D")) {

                            String str = data.substring(10, data.length() - 2);
                            String strData = BleUtils.hexStr2Str(str);
                            String[] temp = null;
                            temp = strData.split("%");
                            for (int i = 0; i < temp.length; i++) {
                                String lp = temp[i];
                                Log.d("123455", lp);
                                if (lp.substring(0, 1).equals("A")) {
                                    String strLenghtA = lp.substring(2);
                                    tvLengthA.setText(strLenghtA + "%");
                                } else if (lp.substring(1, 2).equals("B")) {
                                    String strLenghtB = lp.substring(3);

                                    tvLengthB.setText(strLenghtB + "%");
                                } else if (lp.substring(1, 2).equals("C")) {
                                    String strLenghtC = lp.substring(3);

                                    tvLengthC.setText(strLenghtC + "%");
                                } else if (lp.substring(1, 2).equals("D")) {
                                    String strLenghtD = lp.substring(3);

                                    tvLengthD.setText(strLenghtD + "%");
                                }
                            }
                            HomeActivity.changeData();
                        }
                    }
                }
            }
        };
        multiThread =  new MultiThread();
        multiThread.start();
    }
    @Override
    public void setWindowFeature() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        multiThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        multiThread.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_multiaA:
                AlertDialog.Builder builderA = new AlertDialog.Builder(MultiareaActivity.this);
                builderA.setTitle("A:");
                View view1
                         = LayoutInflater.from(MultiareaActivity.this).inflate(R.layout.multi_dialog,null);
                final EditText edDistance = (EditText) view1.findViewById(R.id.ed_distance);
                UiUtils.showKeyboard(MultiareaActivity.this,edDistance);
                final EditText edArea = (EditText) view1.findViewById(R.id.ed_area);
                String tvAStr = tvLengthA.getText().toString();
                String[] temp = tvAStr.split(",");
                for (int j = 0; j < temp.length; j++) {
                    String strF = temp[0];
                    String m = strF.substring(0,strF.length()-1);
                    edDistance.setText(m.toCharArray(),0,m.length());

                    String str2 = temp[1];
                    String p = str2.substring(0,str2.length()-1);
                    edArea.setText(p.toCharArray(),0,p.length());
                }
                builderA.setView(view1);
                builderA.setNegativeButton(getString(R.string.no),null);
                builderA.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String distance = edDistance.getText().toString().trim();
                        String area  = edArea.getText().toString().trim();
                        if (distance.equals("") && distance.length()==0){
                            distance = "0";
                        }

                        if (area.equals("") && area.length() == 0){
                            area = "0";
                        }
                        String da = "A:"+distance+"m,"+area+"%";
                        String text = BleUtils.bin2hex2(da);
                        String str = "";
                        if (da.length() == 8){
                            str = "0A000E" + text;
                        }else if (da.length()==9) {
                            str = "0B000E" + text;
                        }else if (da.length() == 10) {
                            str = "0C000E" + text;
                        }else if (da.length() == 11) {
                            str = "0D000E" + text;
                        }else if (da.length() == 12){
                            str = "0E000E" + text;
                        }else if (da.length() == 13){
                            str = "0F000E" + text;
                        }
                        else if (da.length()<8){
                            str= "0"+da.length()+"000E"+text;
                        }
                        HomeActivity.sendData(MultiareaActivity.this,str);
                        tvLengthA.setText(distance+"m,"+area+"%");



                    }
                });
                builderA.create().show();
                break;
            case R.id.layout_multiaB:
                AlertDialog.Builder builderB = new AlertDialog.Builder(MultiareaActivity.this);
                builderB.setTitle("B:");
                View view2
                        = LayoutInflater.from(MultiareaActivity.this).inflate(R.layout.multi_dialog,null);
                final EditText edDistance1 = (EditText) view2.findViewById(R.id.ed_distance);
                UiUtils.showKeyboard(MultiareaActivity.this,edDistance1);
                final EditText edArea1 = (EditText) view2.findViewById(R.id.ed_area);
                String tvAStr1 = tvLengthB.getText().toString();
                String[] temp1 = tvAStr1.split(",");
                for (int j = 0; j < temp1.length; j++) {
                    String strF = temp1[0];
                    String m = strF.substring(0,strF.length()-1);
                    edDistance1.setText(m.toCharArray(),0,m.length());

                    String str2 = temp1[1];
                    String p = str2.substring(0,str2.length()-1);
                    edArea1.setText(p.toCharArray(),0,p.length());
                }
                builderB.setView(view2);
                builderB.setNegativeButton(getString(R.string.no),null);
                builderB.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String distance = edDistance1.getText().toString().trim();
                        String area  = edArea1.getText().toString().trim();
                        if (distance.equals("") && distance.length()==0){
                            distance = "0";
                        }

                        if (area.equals("") && area.length() == 0){
                            area = "0";
                        }
                        String da = "B:"+distance+"m,"+area+"%";
                        String text = BleUtils.bin2hex2(da);
                        String str = "";
                        if (da.length() == 8){
                            str = "0A000E" + text;
                        }else if (da.length()==9) {
                            str = "0B000E" + text;
                        }else if (da.length() == 10) {
                            str = "0C000E" + text;
                        }else if (da.length() == 11) {
                            str = "0D000E" + text;
                        }else if (da.length() == 12){
                            str = "0E000E" + text;
                        }
                        else if (da.length() == 13){
                            str = "0F000E" + text;
                        }else if (da.length()<8){
                            str= "0"+da.length()+"000E"+text;
                        }
                        HomeActivity.sendData(MultiareaActivity.this,str);
                        tvLengthB.setText(distance+"m,"+area+"%");



                    }
                });
                builderB.create().show();
                break;
            case R.id.layout_multiaC:
                AlertDialog.Builder builderC = new AlertDialog.Builder(MultiareaActivity.this);
                builderC.setTitle("C:");
                View view3
                        = LayoutInflater.from(MultiareaActivity.this).inflate(R.layout.multi_dialog,null);
                final EditText edDistance2 = (EditText) view3.findViewById(R.id.ed_distance);
                UiUtils.showKeyboard(MultiareaActivity.this,edDistance2);
                final EditText edArea2 = (EditText) view3.findViewById(R.id.ed_area);
                String tvAStr2 = tvLengthC.getText().toString();
                String[] temp2 = tvAStr2.split(",");
                for (int j = 0; j < temp2.length; j++) {
                    String strF = temp2[0];
                    String m = strF.substring(0,strF.length()-1);
                    edDistance2.setText(m.toCharArray(),0,m.length());

                    String str2 = temp2[1];
                    String p = str2.substring(0,str2.length()-1);
                    edArea2.setText(p.toCharArray(),0,p.length());
                }
                builderC.setView(view3);
                builderC.setNegativeButton(getString(R.string.no),null);
                builderC.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String distance = edDistance2.getText().toString().trim();
                        String area  = edArea2.getText().toString().trim();
                        if (distance.equals("") && distance.length()==0){
                            distance = "0";
                        }

                        if (area.equals("") && area.length() == 0){
                            area = "0";
                        }
                        String da = "C:"+distance+"m,"+area+"%";
                        String text = BleUtils.bin2hex2(da);
                        String str = "";
                        if (da.length() == 8){
                            str = "0A000E" + text;
                        }else if (da.length()==9) {
                            str = "0B000E" + text;
                        }else if (da.length() == 10) {
                            str = "0C000E" + text;
                        }else if (da.length() == 11) {
                            str = "0D000E" + text;
                        }else if (da.length() == 12){
                            str = "0E000E" + text;
                        }else if (da.length() == 13){
                            str = "0F000E" + text;
                        }
                        else if (da.length()<8){
                            str= "0"+da.length()+"000E"+text;
                        }
                        HomeActivity.sendData(MultiareaActivity.this,str);
                        tvLengthC.setText(distance+"m,"+area+"%");



                    }
                });
                builderC.create().show();
                break;
            case R.id.layout_multiaD:
                AlertDialog.Builder builderD = new AlertDialog.Builder(MultiareaActivity.this);
                builderD.setTitle("D:");
                View view4
                        = LayoutInflater.from(MultiareaActivity.this).inflate(R.layout.multi_dialog,null);
                final EditText edDistance3 = (EditText) view4.findViewById(R.id.ed_distance);
                UiUtils.showKeyboard(MultiareaActivity.this,edDistance3);
                final EditText edArea3 = (EditText) view4.findViewById(R.id.ed_area);
                String tvAStr3 = tvLengthD.getText().toString();
                String[] temp3 = tvAStr3.split(",");
                for (int j = 0; j < temp3.length; j++) {
                    String strF = temp3[0];
                    String m = strF.substring(0,strF.length()-1);
                    edDistance3.setText(m.toCharArray(),0,m.length());

                    String str2 = temp3[1];
                    String p = str2.substring(0,str2.length()-1);
                    edArea3.setText(p.toCharArray(),0,p.length());
                }
                builderD.setView(view4);
                builderD.setNegativeButton(getString(R.string.no),null);
                builderD.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String distance = edDistance3.getText().toString().trim();
                        String area  = edArea3.getText().toString().trim();
                        if (distance.equals("") && distance.length()==0){
                            distance = "0";
                        }

                        if (area.equals("") && area.length() == 0){
                            area = "0";
                        }
                        String da  = "D:"+distance+"m,"+area+"%";
                        String text = BleUtils.bin2hex2(da);
                        String str = "";
                        if (da.length() == 8){
                            str = "0A000E" + text;
                        }else if (da.length()==9) {
                            str = "0B000E" + text;
                        }else if (da.length() == 10) {
                            str = "0C000E" + text;
                        }else if (da.length() == 11) {
                            str = "0D000E" + text;
                        }else if (da.length() == 12){
                            str = "0E000E" + text;
                        }else if (da.length() == 13){
                            str = "0F000E" + text;
                        }
                        else if (da.length()<8){
                            str= "0"+da.length()+"000E"+text;
                        }
                        HomeActivity.sendData(MultiareaActivity.this,str);
                        tvLengthD.setText(distance+"m,"+area+"%");



                    }
                });
                builderD.create().show();
                break;
        }
    }


    private static class MultiThread extends Thread{

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
