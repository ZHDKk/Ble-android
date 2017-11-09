package com.example.bluetooth.le.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseFragment;
import com.shinelw.library.ColorArcProgressBar;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/8.
 * Time:9:05.
 */

public class ChargeFragment extends BaseFragment{
    private ColorArcProgressBar bar;
    @Override
    public View initView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_charge,null);
    }

    @Override
    protected void initFindViewById(View view) {
        bar = (ColorArcProgressBar) view.findViewById(R.id.bar1);
    }

    @Override
    public void initData() {
        bar.setCurrentValues(90);
    }
}
