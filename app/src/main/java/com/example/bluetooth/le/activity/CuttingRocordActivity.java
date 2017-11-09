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
import com.example.bluetooth.le.model.CuttingBean;
import com.example.bluetooth.le.model.FaultBean;
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
 * Time:8:51.
 */

public class CuttingRocordActivity extends BaseActivity implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private Toolbar toolbar;
    private CuttingAdapter adapter;
    private CuttingBean bean;
    private List<CuttingBean> datas;

    byte[] writeBytes = new byte[20];
    private static String callData;
    private static Handler handler;
    private CutThread cutThread;

    @Override
    public int getContentView() {
        return R.layout.cuttingrocord_layout;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.cuttingrecord));
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
        adapter = new CuttingAdapter(this,R.layout.cutting_item);
        mPullLoadMoreRecyclerView.setAdapter(adapter);

        new HomeActivity().setOnDConnectListener(new HomeActivity.OnDConnectListener() {
            @Override
            public void setDConnect() {
                UiUtils.showSackBar(CuttingRocordActivity.this,getString(R.string.disconnected),R.color.green,R.color.white);
            }
        });

    }

    private void getData() {
        datas=new ArrayList<CuttingBean>();
        String text = "020018";
        HomeActivity.sendData(CuttingRocordActivity.this,text);
        HomeActivity.dismissDialog();

        handler  = new Handler(getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1) {
                    if (data.length() >= 10) {
                        if (data.substring(6, 10).equals("0016")) {
                            String strCut = data.substring(10, data.length() - 2);
                            String str = BleUtils.hexStr2Str(strCut);
                            String[] temp = str.split(",");
                            for (int i = 0; i < temp.length; i++) {
                                bean = new CuttingBean();
                                String strDate = temp[i];
                                if (strDate.length()>=23) {
                                    String startTime = strDate.substring(0, 11);
                                    String endTime = strDate.substring(12, 23);

                                    bean.setStartTime(startTime);
                                    bean.setEndTime(endTime);
                                    datas.add(bean);
                                }

                            }
                            adapter.clearData();
                            adapter.addAllData(datas);
                            cutThread.close();
//                        ToastUtil.showToast(CuttingRocordActivity.this, BleUtils.hexStr2Str(strCut));
                            HomeActivity.changeData();
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
    public void onLoadMore() {
        adapter.clearData();
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cutThread = new CutThread();
        cutThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cutThread.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cutThread.close();
    }

    class CuttingAdapter extends BaseViewAdapter<CuttingBean> {

        public CuttingAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindData(BaseViewHolder holder, int position) {
            holder.setText(R.id.tv_cut_startTime,datas.get(position).getStartTime());
            holder.setText(R.id.tv_cut_endTime,datas.get(position).getEndTime());
        }
    }

    private static class CutThread extends Thread{

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
