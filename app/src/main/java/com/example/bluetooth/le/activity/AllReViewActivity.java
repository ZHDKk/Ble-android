package com.example.bluetooth.le.activity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.model.ChargeBean;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;

public class AllReViewActivity extends BaseActivity {


    private Toolbar toolbar;
    private String deviceName;
    private EditText edChangeName;
    private CardView changeName_btn;
    private String deviceAddress;

    private BluetoothManager mBluetoothManager;  //蓝牙管理器
    private BluetoothAdapter mBluetoothAdapter;  //蓝牙适配器
    private SPUtils spUtils;

    @Override
    public int getContentView() {
        return R.layout.activity_all_re_view;
    }

    @Override
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.review);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edChangeName = (EditText) findViewById(R.id.ed_ChangeName);
        changeName_btn = (CardView) findViewById(R.id.cardview_changeName);

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
         spUtils = new SPUtils(AllReViewActivity.this,"fileName");
        deviceAddress = spUtils.getString("deviceAddress", "");
        getData();
    }

    private void getData() {
        deviceName = mBluetoothAdapter.getRemoteDevice(deviceAddress).getName();

        String changeName = spUtils.getString("changeName","");
        if (!TextUtils.isEmpty(changeName) && !changeName.equals(deviceName)){
            edChangeName.setText(changeName.toCharArray(),0,changeName.length());
        }else {
            edChangeName.setText(deviceName.toCharArray(),0,deviceName.length());
        }



        changeName_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edChangeName.getText().toString().trim())) {
                    HomeActivity.changeNameData(edChangeName.getText().toString().trim());
                    spUtils.putString("changeName", edChangeName.getText().toString().trim());
                    ToastUtil.showToast(AllReViewActivity.this, getString(R.string.success));
                    finish();
                }else {
                    ToastUtil.showToast(AllReViewActivity.this, getString(R.string.nameNotEmpty));
                }
            }
        });
    }


    @Override
    public void setWindowFeature() {

    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }





}
