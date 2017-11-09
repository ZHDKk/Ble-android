package com.example.bluetooth.le.base;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.activity.BluetoothLeService;
import com.example.bluetooth.le.activity.DeviceControlActivity;
import com.example.bluetooth.le.db.bean.DaoMaster;
import com.example.bluetooth.le.db.bean.DaoSession;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;


/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/3.
 * Time:15:12.
 */

public abstract class BaseActivity extends AppCompatActivity{
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    public static final String LIST_NAME = "NAME";
    public static  final String LIST_UUID = "UUID";
    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setWindowFeature();
        super.onCreate(savedInstanceState);
        //设置页面布局
        setContentView(getContentView());
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
//透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        initView();

        initDbHelp();

//        UiUtils.hideBottomUIMenu(this);

    }

    //添加fragment
    public void addFragment(BaseFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
                   ft.replace(R.id.fragment_content, fragment, fragment.getClass().getSimpleName()).commit();
            ft.addToBackStack(null);
        }
    }

    //移除fragment
    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public DaoSession initDbHelp() {
        //创建数据库
        DaoMaster.DevOpenHelper  helper = new DaoMaster.DevOpenHelper(this,"robot.db",null);
        //获取可写数据库
       SQLiteDatabase database = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(database);
        //获取Dao对象管理
         daoSession = daoMaster.newSession();
        return daoSession;
    }

    //子类必须实现此方法
    public abstract int getContentView();

    public abstract void initView();
    public abstract void setWindowFeature();
    public void startActivity(Context context,Class clazz){
        startActivity(new Intent(context,clazz));
    }
    public void startActivityParam(Context context,Class clazz,String key,String param){
        Intent intent = new Intent(context,clazz);
        Bundle bundle = new Bundle();
        bundle.putString(key,param);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void startActivityParam(Context context,Class clazz,String titleKey,String titleParam,String contentKey,String contentParam){
        Intent intent = new Intent(context,clazz);
        Bundle bundle = new Bundle();
        bundle.putString(titleKey,titleParam);
        bundle.putString(contentKey,contentParam);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void hideStatusbar(){
        //隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public ArrayList<ArrayList<HashMap<String, String>>> displayGattServices(BluetoothLeService mBluetoothLeService,List<BluetoothGattService> gattServices) {
        if (gattServices == null) return null;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // 遍历 GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // 遍历 Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                int charaProp = gattCharacteristic.getProperties();
//                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                     Log.e("nihao","gattCharacteristic的UUID为:"+gattCharacteristic.getUuid());
//                     Log.e("nihao","gattCharacteristic的属性为:  可读");
//                }
//                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                     Log.e("nihao","gattCharacteristic的UUID为:"+gattCharacteristic.getUuid());
//                     Log.e("nihao","gattCharacteristic的属性为:  可写");
//                }
//                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                     Log.e("nihao","gattCharacteristic的UUID为:"+gattCharacteristic.getUuid()+gattCharacteristic);
//                     Log.e("nihao","gattCharacteristic的属性为:  具备通知属性");
//                }
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
//                mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        return gattCharacteristicData;
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.RSSI);
        intentFilter.addAction(BluetoothLeService.WRITE_SUCCESS);
        return intentFilter;
    }

}
