package com.example.bluetooth.le.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.base.BaseWebActivity;
import com.example.bluetooth.le.utils.BleUtils;

/**
 * Created by zhdk on 2017/8/21.
 */

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private CardView layoutWebsite;
    private TextView tvSoftWare,tvSn;
    private static String callData;
    private static Handler handler;
    private AboutThread aboutThread;
    private String mDeviceAdress;

    @Override
    public int getContentView() {
        return R.layout.about_layout;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.About));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutWebsite = (CardView) findViewById(R.id.layout_website);
        layoutWebsite.setOnClickListener(this);
        tvSoftWare = (TextView) findViewById(R.id.tvSoftWare);
        tvSn = (TextView) findViewById(R.id.tvSn);
        mDeviceAdress = HomeActivity.strDevice();
        if (!TextUtils.isEmpty(mDeviceAdress))
        sendData();
        HomeActivity.dismissDialog();
    }

    private void sendData() {
        String strSoft = "020001";
        final String strSn = "020002";

        HomeActivity.sendData(AboutActivity.this,strSoft);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeActivity.sendData(AboutActivity.this,strSn);
            }
        },1000);

        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1){
                    if (data.length()>=14){
                        if (data.substring(6,10).equals("0002") && data.length()<45 && data.length()>30){
                            if (data.substring(data.length()-6,data.length()).equals("EFBFBD")) {
                                String sn = data.substring(10, data.length() - 6);
                                String snStr = BleUtils.hexStr2Str(sn);
                                tvSn.setText(snStr);
                            }else {
                                String sn = data.substring(10, data.length() - 2);
                                String snStr = BleUtils.hexStr2Str(sn);
                                tvSn.setText(snStr);
                            }
                        }else if (data.substring(0,14).equals("55AAEFBFBD0001")){
                            if (data.substring(data.length()-6,data.length()).equals("EFBFBD")) {
                                String sv = data.substring(14, data.length() - 6);
                                String svStr = BleUtils.hexStr2Str(sv);
                                tvSoftWare.setText(svStr);
                            }else {
                                String sv = data.substring(14, data.length() - 2);
                                String svStr = BleUtils.hexStr2Str(sv);
                                tvSoftWare.setText(svStr);
                            }
                        }else if (data.substring(6,10).equals("0001")){
                           if (data.substring(data.length()-6,data.length()).equals("EFBFBD")) {
                                String sv = data.substring(10, data.length() - 6);
                                String svStr = BleUtils.hexStr2Str(sv);
                                tvSoftWare.setText(svStr);
                            }else {
                                String sv = data.substring(10, data.length() - 2);
                                String svStr = BleUtils.hexStr2Str(sv);
                                tvSoftWare.setText(svStr);
                            } if (data.substring(data.length()-6,data.length()).equals("EFBFBD")) {
                                String sv = data.substring(10, data.length() - 6);
                                String svStr = BleUtils.hexStr2Str(sv);
                                tvSoftWare.setText(svStr);
                            }else {
                                String sv = data.substring(10, data.length() - 2);
                                String svStr = BleUtils.hexStr2Str(sv);
                                tvSoftWare.setText(svStr);
                            }
                        }
                    }
                    HomeActivity.changeData();
                }
            }
        };
        aboutThread =  new AboutThread();
        aboutThread.start();
    }

    @Override
    public void setWindowFeature() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (aboutThread!=null)
        aboutThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aboutThread!=null)
        aboutThread.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_website:
                startActivityParam(AboutActivity.this, AboutWebActivity.class,"title","www.gforce-tools.com","url","http://www.gforce-tools.com");
                break;
        }
    }

    private static class AboutThread extends Thread{

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
