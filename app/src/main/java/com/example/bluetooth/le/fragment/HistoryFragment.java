package com.example.bluetooth.le.fragment;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.activity.ChargingRocordActivity;
import com.example.bluetooth.le.activity.CuttingRocordActivity;
import com.example.bluetooth.le.activity.FaultRocordActivity;
import com.example.bluetooth.le.activity.HealthRocordActivity;
import com.example.bluetooth.le.base.BaseFragment;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/8.
 * Time:9:05.
 */

public class HistoryFragment extends BaseFragment implements View.OnClickListener {
    private CardView layoutFault,layoutCutting,layoutCharge,layoutHealth;

    public static String PERSONAL_FRAGMENT = "personal_fragment";

    public static HistoryFragment newInstance(String msg) {
        HistoryFragment personalFragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PERSONAL_FRAGMENT, msg);
        personalFragment.setArguments(bundle);
        return personalFragment;
    }

    @Override
    public View initView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_history,null);
    }

    @Override
    protected void initFindViewById(View view) {
        layoutFault = (CardView) view.findViewById(R.id.layout_fault);
        layoutCutting = (CardView) view.findViewById(R.id.layout_cutting);
        layoutCharge = (CardView) view.findViewById(R.id.layout_charge);
        layoutHealth = (CardView) view.findViewById(R.id.layout_health);
    }

    @Override
    public void initData() {
        layoutFault.setOnClickListener(this);
        layoutCutting.setOnClickListener(this);
        layoutCharge.setOnClickListener(this);
        layoutHealth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_fault:
                startActivity(getActivity(), FaultRocordActivity.class);
                break;
            case R.id.layout_cutting:
                startActivity(getActivity(), CuttingRocordActivity.class);
                break;
            case R.id.layout_charge:
                startActivity(getActivity(), ChargingRocordActivity.class);
                break;
            case R.id.layout_health:
                startActivity(getActivity(), HealthRocordActivity.class);
                break;
        }

    }
}
