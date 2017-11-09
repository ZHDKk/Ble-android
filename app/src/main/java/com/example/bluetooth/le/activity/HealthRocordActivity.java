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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.adapter.BaseViewAdapter;
import com.example.bluetooth.le.adapter.BaseViewHolder;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.db.bean.Health;
import com.example.bluetooth.le.model.ChargeBean;
import com.example.bluetooth.le.model.HealthBean;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.example.bluetooth.le.view.recycleview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/14.
 * Time:8:52.
 */

public class HealthRocordActivity extends BaseActivity implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private Toolbar toolbar;
    private HealthAdapter adapter;
    private HealthBean bean;
    private List<HealthBean> datas;

    private static String callData;
    private static Handler handler;
    private HealthThread healthThread;

    byte[] writeBytes = new byte[20];
    @Override
    public int getContentView() {
        return R.layout.healthrocord_layout;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.healthRecord));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        setRecyclerView();
        getData();
        new HomeActivity().setOnDConnectListener(new HomeActivity.OnDConnectListener() {
            @Override
            public void setDConnect() {
                UiUtils.showSackBar(HealthRocordActivity.this,getString(R.string.disconnected),R.color.green,R.color.white);
            }
        });

    }
    private void getData() {
        datas=new ArrayList<>();
        String text = "02001A";
        HomeActivity.sendData(HealthRocordActivity.this,text);
        HomeActivity.dismissDialog();
        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1) {
                    if (data.length() >= 10) {
                        if (data.substring(data.length() - 6, data.length()).equals("EFBFBD")) {
                            if (data.substring(6, 10).equals("0018")) {
                                String strHealth = data.substring(10, data.length() - 6);
                                String str = BleUtils.hexStr2Str(strHealth);
                                bean = new HealthBean();
                                bean.setTimes(str);
                                datas.add(bean);
                                adapter.clearData();
                                adapter.addAllData(datas);

                                healthThread.close();
//                        ToastUtil.showToast(HealthRocordActivity.this, BleUtils.hexStr2Str(strHealth));
                                HomeActivity.changeData();
                            }
                        }else {
                            if (data.substring(6, 10).equals("0018")) {
                                String strHealth = data.substring(10, data.length() - 2);
                                String str = BleUtils.hexStr2Str(strHealth);
                                bean = new HealthBean();
                                bean.setTimes(str);
                                datas.add(bean);
                                adapter.clearData();
                                adapter.addAllData(datas);

                                healthThread.close();
//                        ToastUtil.showToast(HealthRocordActivity.this, BleUtils.hexStr2Str(strHealth));
                                HomeActivity.changeData();
                            }
                        }
                    }
                }
            }
        };
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();


    }

    private void setRecyclerView() {
        //获取mRecyclerView对象
        RecyclerView recyclerView = mPullLoadMoreRecyclerView.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        recyclerView.setVerticalScrollBarEnabled(true);
        //设置下拉刷新是否可见
        //mPullLoadMoreRecyclerView.setRefreshing(true);
        //设置是否可以下拉刷新
        mPullLoadMoreRecyclerView.setPullRefreshEnable(true);
        //设置是否可以上拉刷新
        mPullLoadMoreRecyclerView.setPushRefreshEnable(false);
        //显示下拉刷新
        mPullLoadMoreRecyclerView.setRefreshing(true);
        //设置上拉刷新文字
        mPullLoadMoreRecyclerView.setFooterViewText(getString(R.string.load_more_text));
        //设置上拉刷新文字颜色
        //mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        //mPullLoadMoreRecyclerView.setFooterViewBackgroundColor(R.color.colorBackground);
        mPullLoadMoreRecyclerView.setLinearLayout();
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        adapter = new HealthAdapter(this,R.layout.health_item);
        mPullLoadMoreRecyclerView.setAdapter(adapter);


    }

    @Override
    public void setWindowFeature() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        healthThread = new HealthThread();
        healthThread.start();
    }

    @Override
    public void onRefresh() {
        adapter.clearData();
        getData();
    }

    @Override
    public void onLoadMore() {
        adapter.clearData();
        getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        healthThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        healthThread.close();
    }

    class HealthAdapter extends BaseViewAdapter<HealthBean> {

        public HealthAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindData(BaseViewHolder holder, int position) {
            HealthBean bean = datas.get(position);
            String str = bean.getTimes();
            String[] temp = str.split(",");
            for (int i=0;i<temp.length;i++){
                String strH = temp[0];
                String strM = temp[1];
                String strT = temp[2];
                holder.setText(R.id.tv_health_times,strH+"h"+strM+"min");
                holder.setText(R.id.tv_health_charges,strT);

            }
        }
    }

    private static class HealthThread extends Thread{

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
