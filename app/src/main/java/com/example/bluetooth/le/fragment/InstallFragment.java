package com.example.bluetooth.le.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseFragment;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/8.
 * Time:9:05.
 */

public class InstallFragment extends BaseFragment{
    public static String PERSONAL_FRAGMENT = "install_fragment";

    public static InstallFragment newInstance(String msg) {
        InstallFragment personalFragment = new InstallFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PERSONAL_FRAGMENT, msg);
        personalFragment.setArguments(bundle);
        return personalFragment;
    }
    @Override
    public View initView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_install,null);
    }

    @Override
    protected void initFindViewById(View view) {

    }

    @Override
    public void initData() {

    }
}
