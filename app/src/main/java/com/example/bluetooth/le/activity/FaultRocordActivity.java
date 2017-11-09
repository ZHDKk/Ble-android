package com.example.bluetooth.le.activity;

import android.app.Dialog;
import android.app.Fragment;
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
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.adapter.BaseViewAdapter;
import com.example.bluetooth.le.adapter.BaseViewHolder;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.db.bean.Fault;
import com.example.bluetooth.le.fragment.FragmentCallback;
import com.example.bluetooth.le.fragment.TimerFragment;
import com.example.bluetooth.le.model.FaultBean;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.example.bluetooth.le.view.SetDayPopuWindow;
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
 * Time:8:50.
 */

public class FaultRocordActivity extends BaseActivity implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private Toolbar toolbar;
    private FaultAdapter adapter;
    private FaultBean bean;
    private List<FaultBean> datas;
    private static String callData;
    private static Handler handler;
    private FaultThread faultThread;


    @Override
    public int getContentView() {
        return R.layout.faultrocord_layout;
    }

    @Override
    public void initView() {
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        setRecyclerView();
        getData();

        new HomeActivity().setOnDConnectListener(new HomeActivity.OnDConnectListener() {
            @Override
            public void setDConnect() {
                UiUtils.showSackBar(FaultRocordActivity.this,getString(R.string.disconnected),R.color.green,R.color.white);
            }
        });
    }


    private void getData() {
        datas = new ArrayList<FaultBean>();
        String text = "020017";
        HomeActivity.sendData(FaultRocordActivity.this,text);
        HomeActivity.dismissDialog();

        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1) {
                    if (data.length() >= 10) {
                        if (data.substring(6, 10).equals("0015")) {

                            String strFault = data.substring(10, data.length() - 2);
                            String str = BleUtils.hexStr2Str(strFault);
                            String[] temp1 = str.split(",");

                            for (int i = 0; i < temp1.length; i++) {
                                bean = new FaultBean();
                                String strF = temp1[i];
                                if (strF.length()>=14) {
                                    String date = strF.substring(0, 11);
                                    String statue = strF.substring(12, 14);

                                    bean.setTime(date);
                                    bean.setStatus(statue);
                                    datas.add(bean);
                                }
                            }
                            adapter.clearData();
                            adapter.addAllData(datas);

                            faultThread.close();
//                        ToastUtil.showToast(FaultRocordActivity.this, BleUtils.hexStr2Str(strFault));
                            HomeActivity.changeData();
                        }
                    }
                }
            }
        };
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();



    }


    private void setRecyclerView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.fault_record);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        adapter = new FaultAdapter(this,R.layout.fault_item);
        mPullLoadMoreRecyclerView.setAdapter(adapter);
    }


    @Override
    public void setWindowFeature() {

    }

    @Override
    public void onRefresh() {
        adapter.clearData();
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        faultThread = new FaultThread();
        faultThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        faultThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        faultThread.close();
    }

    @Override
    public void onLoadMore() {
        adapter.clearData();
        getData();
    }

    class FaultAdapter extends BaseViewAdapter<FaultBean>{

        public FaultAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindData(BaseViewHolder holder, int position) {
            FaultBean data = datas.get(position);
            holder.setText(R.id.tv_fault_time,data.getTime());
            String strRecord = data.getStatus();
            if (strRecord.equals("00")){
                holder.setText(R.id.tv_fault_status,"NULL");
            }else if (strRecord.equals("01")){
                holder.setText(R.id.tv_fault_status,"Low Bat");
            }else if (strRecord.equals("02")){
                holder.setText(R.id.tv_fault_status,"No Signal");
            }else if (strRecord.equals("03")){
                holder.setText(R.id.tv_fault_status,"Out Side");
            }else if (strRecord.equals("04")){
                holder.setText(R.id.tv_fault_status,"Pit");
            }else if (strRecord.equals("05")){
                holder.setText(R.id.tv_fault_status,"Slope");
            }else if (strRecord.equals("06")){
                holder.setText(R.id.tv_fault_status,"Lift");
            }else if (strRecord.equals("07")){
                holder.setText(R.id.tv_fault_status,"M1 Brake");
            }else if (strRecord.equals("08")){
                holder.setText(R.id.tv_fault_status,"M2 Brake");
            }else if (strRecord.equals("09")){
                holder.setText(R.id.tv_fault_status,"Turn Over");
            }else if (strRecord.equals("10")){
                holder.setText(R.id.tv_fault_status,"ERROR1");
            }else if (strRecord.equals("11")){
                holder.setText(R.id.tv_fault_status,"ERROR20L");
            }else if (strRecord.equals("12")){
                holder.setText(R.id.tv_fault_status,"ERROR21");
            }else if (strRecord.equals("13")){
                holder.setText(R.id.tv_fault_status,"ERROR22");
            }else if (strRecord.equals("14")){
                holder.setText(R.id.tv_fault_status,"ERROR23");
            }else if (strRecord.equals("15")){
                holder.setText(R.id.tv_fault_status,"ERROR24");
            }else if (strRecord.equals("16")){
                holder.setText(R.id.tv_fault_status,"ERROR3");
            }else if (strRecord.equals("17")){
                holder.setText(R.id.tv_fault_status,"ERROR4");
            }else if (strRecord.equals("18")){
                holder.setText(R.id.tv_fault_status,"Trapped");
            }else if (strRecord.equals("19")){
                holder.setText(R.id.tv_fault_status,"ERROR20M");
            }else if (strRecord.equals("20")){
                holder.setText(R.id.tv_fault_status,"12C ERROR");
            }else if (strRecord.equals("28")){
                holder.setText(R.id.tv_fault_status,"ERROR20R");
            }


        }
    }

    private static class FaultThread extends Thread{

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
