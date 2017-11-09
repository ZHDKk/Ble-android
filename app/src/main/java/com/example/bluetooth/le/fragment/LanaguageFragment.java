package com.example.bluetooth.le.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.adapter.LanaguageAdapter;
import com.example.bluetooth.le.base.BaseFragment;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.view.recycleview.PullLoadMoreRecyclerView;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/8.
 * Time:9:05.
 */

public class LanaguageFragment extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener {
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private LanaguageAdapter adapter;

    private static FragmentCallback fragmentCallback;
    private TextView tvLanguage;
    private String str;


    private static String callData;
    private LanguageThread languageThread;
    private static Handler handler;

    public static String PERSONAL_FRAGMENT = "personal_fragment";

    public static LanaguageFragment newInstance(String msg) {
        LanaguageFragment personalFragment = new LanaguageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PERSONAL_FRAGMENT, msg);
        personalFragment.setArguments(bundle);
        return personalFragment;
    }

    @Override
    public View initView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_lanaguage,null);
    }

    @Override
    protected void initFindViewById(View view) {
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.mPullLoadMoreRecyclerView);
        tvLanguage= (TextView) view.findViewById(R.id.tvLanguage);
    }

    @Override
    public void initData() {

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
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        //设置上拉刷新文字颜色
        //mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        //mPullLoadMoreRecyclerView.setFooterViewBackgroundColor(R.color.colorBackground);
        mPullLoadMoreRecyclerView.setLinearLayout();

        getData();
//        adapter = new LanaguageAdapter(getActivity(),getResources().getStringArray(R.array.countries));
        String[] datas = {getString(R.string.language1),
                getString(R.string.language2),
                getString(R.string.language3),
                getString(R.string.language4),
                getString(R.string.language5),
                getString(R.string.language6),
                getString(R.string.language7),
                getString(R.string.language8),
                getString(R.string.language9),
                getString(R.string.language10)};
        adapter = new LanaguageAdapter(getActivity(),datas);
        mPullLoadMoreRecyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new LanaguageAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position){
                    case 0:
                         str="03002000";
                        setDialog(getString(R.string.language1),str);
                        break;
                    case 1:
                        str="03002001";
                        setDialog(getString(R.string.language2),str);
                        break;
                    case 2:
                        str="03002002";
                        setDialog(getString(R.string.language3),str);
                        break;
                    case 3:
                        str="03002003";
                        setDialog(getString(R.string.language4),str);
                        break;
                    case 4:
                        str="03002004";
                        setDialog(getString(R.string.language5),str);
                        break;
                    case 5:
                        str="03002005";
                        setDialog(getString(R.string.language6),str);
                        break;
                    case 6:
                        str="03002006";
                        setDialog(getString(R.string.language7),str);
                        break;
                    case 7:
                        str="03002007";
                        setDialog(getString(R.string.language8),str);
                        break;
                    case 8:
                        str="03002008";
                        setDialog(getString(R.string.language9),str);
                        break;
                    case 9:
                        str="03002009";
                        setDialog(getString(R.string.language10),str);
                        break;
                }
            }
        });



    }

    private  void setDialog(final String language, final String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.prompt));
        builder.setMessage(getString(R.string.switch_language));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragmentCallback.getHomeactivity().sendData(getActivity(),text);
                tvLanguage.setText(getString(R.string.currentLanguage)+language);
            }
        });
        builder.setNegativeButton(R.string.no,null);
        builder.create().show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentCallback = (FragmentCallback) activity;
    }

    private void getData() {
        String languageStr = "02001F";
        fragmentCallback.getHomeactivity().sendData(getActivity(),languageStr);
        /*
            TODO:返回的数据
         */
       String data =  fragmentCallback.getHomeactivity().callBackData();
        fragmentCallback.getHomeactivity().dismissDialog();


        handler  = new Handler(getActivity().getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.obj.toString();
                if(msg.what == 1) {
                    if (data.length() >= 10) {
                        if (data.substring(0, 10).equals(SampleGattAttributes.BLE_PROTOCOL + "03001D")) {
                            String language = data.substring(10, 12);
                            if (language.equals("00")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "English");
                            } else if (language.equals("01")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "Swedish");
                            } else if (language.equals("02")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "German");
                            } else if (language.equals("03")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "Danish");
                            } else if (language.equals("04")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "Spanish");
                            } else if (language.equals("05")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "Finnish");
                            } else if (language.equals("06")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "French");
                            } else if (language.equals("07")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "Ltalian");
                            } else if (language.equals("08")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "Dutch");
                            } else if (language.equals("09")) {
                                tvLanguage.setText(getString(R.string.currentLanguage) + "Norwegian");
                            }


                            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();

                            fragmentCallback.getHomeactivity().changeData();
                        }
                    }
                }
            }
        };


    }


    @Override
    public void onRefresh() {
        getData();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onPause() {
        super.onPause();
        languageThread.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        languageThread = new LanguageThread();
        languageThread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        languageThread.close();
    }

    private static class LanguageThread extends Thread{

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
