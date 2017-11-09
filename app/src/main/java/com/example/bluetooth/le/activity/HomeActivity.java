package com.example.bluetooth.le.activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SampleGattAttributes;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.db.bean.Signal;
import com.example.bluetooth.le.db.bean.SignalDao;
import com.example.bluetooth.le.fragment.FragmentCallback;
import com.example.bluetooth.le.fragment.HistoryFragment;
import com.example.bluetooth.le.fragment.InstallFragment;
import com.example.bluetooth.le.fragment.LanaguageFragment;
import com.example.bluetooth.le.fragment.StartFragment;
import com.example.bluetooth.le.fragment.TimerFragment;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.LogUtil;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.example.bluetooth.le.view.ChartViewPopu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/3.
 * Time:16:12.
 */

public  class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener ,FragmentCallback {
    private static byte[] writeBytes;
    private boolean mConnected = false;
    private String mDeviceName;
    private static String mDeviceAddress;
    public static BluetoothLeService mBluetoothLeService;
    private String uuid;//读写特征uuid
    private int rssi;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ArrayList<ArrayList<HashMap<String, String>>> uuids;
    private Toolbar toolbar;
    private boolean fabopened =false;
    private TextView tvCloud;
    private Fragment mContent;
    private String characterUuid;//读写特征uuid
    private String serviceUuids;
    private BluetoothGattService mnotyGattService;
    public static BluetoothGattCharacteristic characteristic;
    public static String callData = "";
    private Dialog sportDialog;

    private String notifyUuid;
    public static BluetoothGattCharacteristic notifyCharacteristic;
    public static BluetoothGattCharacteristic changeNameCharacteristic;
    private Handler handler1;

    public static  int writeStatus=0;

    public static Dialog dialog;

    private RelativeLayout layoutHome;

    public static HomeActivity instance = null;

    private SignalDao signalDao;

    private ChartViewPopu chartViewPopu;


    private StartFragment startFragment;
    private TimerFragment timerFragment;
    private HistoryFragment historyFragment;
    private LanaguageFragment lanaguageFragment;
    private InstallFragment installFragment;
    private FragmentManager fm;

    private SPUtils spUtils;

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    @Override
    public int getContentView() {
        return R.layout.home_layout;
    }

    @Override
    public void initView() {


        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        instance = this;
//设置toolbar
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.start));
        layoutHome = (RelativeLayout) findViewById(R.id.layout_home);
        drawerLayout = (DrawerLayout) findViewById(R.id.dll);
        navigationView = (NavigationView) findViewById(R.id.navigationview);
        tvCloud = (TextView) findViewById(R.id.cloud);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        startFragment = new StartFragment();
//        timerFragment = new TimerFragment();
//        lanaguageFragment = new LanaguageFragment();
//        historyFragment = new HistoryFragment();
        if (startFragment == null){
            startFragment = StartFragment.newInstance(callData);
        }
        addFragment(startFragment);
        toolbar.setTitle(getString(R.string.start));

        navigationView.getMenu().getItem(0).setChecked(true);
        handler1 = new Handler();
        sportDialog = new SpotsDialog(this);
//        SPUtils spUtils = new SPUtils(this,"fileName");
//        mDeviceAddress=spUtils.getString(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,"address");
//        mDeviceName= spUtils.getString(DeviceControlActivity.EXTRAS_DEVICE_NAME,"name");
        mDeviceAddress = getIntent().getStringExtra(SampleGattAttributes.DEVICE_ADDRESS);
        if (!TextUtils.isEmpty(mDeviceAddress) && mDeviceAddress!=null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            sportDialog.show();

            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sportDialog.isShowing()) {
                        sportDialog.dismiss();
                        UiUtils.showSackBar(HomeActivity.this, getString(R.string.connectedFail), R.color.green, R.color.white);
                    }
                }
            },10000);
        }else {
            setSnacbar();
        }

        signalDao = initDbHelp().getSignalDao(); //数据库使用

        spUtils = new SPUtils(HomeActivity.this,"fileName");
        spUtils.putInt("num",0);
        spUtils.putInt("powerNum",0);
        spUtils.putString("str","");
        spUtils.putString("changeName","");

    }

    private void setSnacbar() {
        Snackbar sb =  Snackbar.make(layoutHome, R.string.connectDevice,Snackbar.LENGTH_INDEFINITE);
        View view = sb.getView();
        view.setBackgroundColor(getResources().getColor(R.color.green));
        TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.white));

        sb.setAction(R.string.menu_connect, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(HomeActivity.this,DeviceScanActivity.class);
            }
        });
        sb.setActionTextColor(getResources().getColor(R.color.red));
        sb.show();
    }


    private  ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };



    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                sportDialog.dismiss();
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                if (dconnectLister!=null)
                dconnectLister.setDConnect();
                rssi = 0;
                UiUtils.showSackBar(HomeActivity.this,getString(R.string.disconnected),R.color.green,R.color.white);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                uuids = displayGattServices(mBluetoothLeService,mBluetoothLeService.getSupportedGattServices());
                serviceUuids = mBluetoothLeService.getSupportedGattServices().get(uuids.size() - 1).getUuid().toString();
                characterUuid = uuids.get(uuids.size() - 1).get(0).get(LIST_UUID);
                notifyUuid= uuids.get(uuids.size() -1).get(1).get(LIST_UUID);
                mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString(serviceUuids));
                notifyCharacteristic= mnotyGattService.getCharacteristic(UUID.fromString(notifyUuid));
                characteristic = mnotyGattService.getCharacteristic(UUID.fromString(characterUuid));

                changeNameCharacteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb"));
                mBluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true);
                connectLister.setConnect();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                    callData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (dialog != null) {
                    dialog.dismiss();
                }
//                checkError(BleUtils.bin2hex(callData));
                dataListener.callData(callData);
                LogUtil.fussenLog().d("返回" + BleUtils.bin2hex(callData));
            }else if (BluetoothLeService.RSSI.equals(action)){
                 rssi =  intent.getIntExtra(BluetoothLeService.EXTRA_DATA,0);
                invalidateOptionsMenu();
            }else if (BluetoothLeService.WRITE_SUCCESS.equals(action)){
                 writeStatus = intent.getIntExtra(BluetoothLeService.EXTRA_DATA,0);
            }
        }
    };


    private void checkError(String data) {
        if (data.length()>=10) {
            if (data.substring(6, 10).equals("0024")) {
                final Snackbar sb = Snackbar.make(layoutHome, "error", Snackbar.LENGTH_INDEFINITE);
                View view = sb.getView();
                view.setBackgroundColor(getResources().getColor(R.color.green));
                TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
                tv.setTextColor(getResources().getColor(R.color.white));

                sb.setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sb.dismiss();
                    }
                });
                sb.setActionTextColor(getResources().getColor(R.color.red));
                sb.show();
                dismissDialog();
            }
        }
    }


    @Override
    public void setWindowFeature() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        int rssiStr = -rssi;
        if (rssiStr>20 && rssiStr<70){
            menu.findItem(R.id.menu_signal).setIcon(getResources().getDrawable(R.drawable.signal6));
        }else if (rssiStr>70 && rssiStr<76){
            menu.findItem(R.id.menu_signal).setIcon(getResources().getDrawable(R.drawable.signal5));
        }else if (rssiStr > 76 && rssiStr<81){
            menu.findItem(R.id.menu_signal).setIcon(getResources().getDrawable(R.drawable.signal4));
        }else if (rssiStr>81 && rssiStr<86){
            menu.findItem(R.id.menu_signal).setIcon(getResources().getDrawable(R.drawable.signal3));
        }else if (rssiStr>86 && rssiStr<90){
            menu.findItem(R.id.menu_signal).setIcon(getResources().getDrawable(R.drawable.signal2));
        }else {
            menu.findItem(R.id.menu_signal).setIcon(getResources().getDrawable(R.drawable.signal1));
        }
//        menu.findItem(R.id.menu_signal).setTitle(getString(R.string.signalStrength)+rssiStr);
        SimpleDateFormat sDateFormat    =   new    SimpleDateFormat("HH:mm:ss");
        String    date    =    sDateFormat.format(new Date());
        Signal data = new Signal(null,rssiStr,date);
        signalDao.insert(data);

        List<Signal> signals = signalDao.loadAll();
        if (signals.size()==50){
            signalDao.deleteAll();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                if(!TextUtils.isEmpty(mDeviceAddress) && mDeviceAddress!=null) {
                    sportDialog.show();
                    mBluetoothLeService.connect(mDeviceAddress);
                }else {
//                    UiUtils.showSackBar(HomeActivity.this,getString(R.string.notFindDevice),R.color.green,R.color.white);
                    startActivity(HomeActivity.this,DeviceScanActivity.class);
                }
                return true;
            case R.id.menu_disconnect:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.prompt);
                builder.setMessage(R.string.disconnectMsg);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mBluetoothLeService.disconnect();
                        mDeviceAddress = "";
                        listener.setDisConnect(mDeviceAddress);
                        if (startFragment == null) {
                            startFragment = StartFragment.newInstance(callData);
                        }
                        addFragment(startFragment);
                        toolbar.setTitle(getString(R.string.start));
                        navigationView.getMenu().getItem(0).setChecked(true);
                        setSnacbar();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel),null);
                builder.create().show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_signal:
                UiUtils.backgroundAlpha(0.5f,HomeActivity.this);
                chartViewPopu = new ChartViewPopu(HomeActivity.this,signalDao);
                chartViewPopu.showAtLocation(HomeActivity.this.findViewById(R.id.dll), Gravity.CENTER,0,0);

                chartViewPopu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        UiUtils.backgroundAlpha(1f,HomeActivity.this);
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        if(!TextUtils.isEmpty(mDeviceAddress) && mDeviceAddress!=null) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//            if (mBluetoothLeService != null)
//                mBluetoothLeService.connect(mDeviceAddress);
        }else {
            setSnacbar();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mGattUpdateReceiver!=null)
//        unregisterReceiver(mGattUpdateReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        removeFragment();
        spUtils.putInt("num",0);
        spUtils.putInt("powerNum",0);
        spUtils.putString("str","");
        spUtils.putString("changeName","");
//        unbindService(mServiceConnection);
//        mBluetoothLeService = null;
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.g1_i1:
                if (mConnected) {
                    if (startFragment == null) {
                        startFragment = StartFragment.newInstance(callData);
                    }
                    addFragment(startFragment);
                    toolbar.setTitle(getString(R.string.start));
                    menuItem.setChecked(true);
                }else {
                    ToastUtil.showToast(HomeActivity.this,getString(R.string.connectDevice));
                }
                break;
            case R.id.g1_i3:
                if (mConnected) {
                    if (timerFragment == null) {
                        timerFragment = TimerFragment.newInstance(callData);
                    }
                    addFragment(timerFragment);
                    toolbar.setTitle(getString(R.string.timer));
                    menuItem.setChecked(true);
                }else {
                    ToastUtil.showToast(HomeActivity.this,getString(R.string.connectDevice));
                    menuItem.setCheckable(false);
                }
                break;
            case R.id.g1_i4:
                if (mConnected) {
                    if (historyFragment == null) {
                        historyFragment = HistoryFragment.newInstance(callData);
                    }
                    addFragment(historyFragment);
                    toolbar.setTitle(getString(R.string.history));
                    menuItem.setChecked(true);
                }else {
                    ToastUtil.showToast(HomeActivity.this,getString(R.string.connectDevice));
                    menuItem.setCheckable(false);
                }
                break;
            case R.id.g1_i5:
                if (mConnected) {
                    if (lanaguageFragment == null) {
                        lanaguageFragment = LanaguageFragment.newInstance(callData);
                    }
                    addFragment(lanaguageFragment);
                    toolbar.setTitle(getString(R.string.language));
                    menuItem.setChecked(true);
                }else {
                    ToastUtil.showToast(HomeActivity.this,getString(R.string.connectDevice));
                    menuItem.setCheckable(false);
                }
                break;

            case R.id.g2_i1:
                if (mConnected) {
                    startActivity(HomeActivity.this, DateActivity.class);
                }else {
                    ToastUtil.showToast(HomeActivity.this,getString(R.string.connectDevice));
                    menuItem.setCheckable(false);
                }
                break;
            case R.id.g2_i2:
                if (mConnected) {
                    startActivity(HomeActivity.this, SettingActivity.class);
                }else {
                    ToastUtil.showToast(HomeActivity.this,getString(R.string.connectDevice));
                    menuItem.setCheckable(false);
                }
                break;
            case R.id.g3_i3:
                    startActivity(HomeActivity.this, AboutActivity.class);
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-firstTime>2000){
//                UiUtils.showSackBar(HomeActivity.this,getString(R.string.exit),R.color.green,R.color.white);
                ToastUtil.showToast(HomeActivity.this,getString(R.string.exit));
                firstTime=System.currentTimeMillis();
            }else{
//                if (mGattUpdateReceiver!=null) {
//                    unregisterReceiver(mGattUpdateReceiver);
//                    unbindService(mServiceConnection);
//                }
                finish();
                System.exit(0);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void changeNameData(String strName){

        changeNameCharacteristic.setValue(BleUtils.hex2byte(BleUtils.bin2hex2(strName).getBytes()));
        mBluetoothLeService.writeCharacteristic(changeNameCharacteristic);
    }
    @Override
    public HomeActivity getHomeactivity() {
        return this;
    }
    //通过蓝牙向主板发送命令
    public static void sendData(final Context context, String text){
        if (characteristic!=null) {
            dialog = new SpotsDialog(context);
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()){
                        dialog.dismiss();
//                        UiUtils.showSackBar(context,context.getString(R.string.dataError),R.color.green,R.color.white);
                    }
                }
            },15000);

            writeBytes = new byte[20];
            String t  = "";
            if (!TextUtils.isEmpty(text)&&!text.equals("1234")){
                String str1 = SampleGattAttributes.BLE_PROTOCOL + text;
                String checkNum1 = BleUtils.makeChecksum(str1);
                t = str1 + checkNum1;
            }else if (text.equals("1234")){
                t="00";
            }
            writeBytes = (BleUtils.hex2byte(t.getBytes()));
            new Thread(new HomeRunnable()).start();
        }

    }



    private static void setDataSubcontractor(byte[] writeBytes) {
        int tmpLen = writeBytes.length;
        int start=0;
        int end=0;
        while (tmpLen>0){
             byte[] sendData = new byte[20];
            if (tmpLen>=20){
                end+=20;
                sendData = Arrays.copyOfRange(writeBytes, start, end);
                start+=20;
                tmpLen -= 20;
            }else {
                end+=tmpLen;
                sendData = Arrays.copyOfRange(writeBytes, start, end);
                tmpLen = 0;
            }


            characteristic.setValue(sendData);
            mBluetoothLeService.writeCharacteristic(characteristic);
            SystemClock.sleep(100);



        }
    }

    public static void dismissDialog(){
        if (dialog!=null)
        dialog.dismiss();
    }

    public static String strDevice(){
        return mDeviceAddress;
    }

    //主板返回的数据
    public  static String callBackData(){
        return BleUtils.bin2hex(callData);
    }

    public static void changeData(){
        callData="";
    }
    public static  int writeStatus(){
        return writeStatus;
    }

    public static class  HomeRunnable implements Runnable{

        @Override
        public void run() {
                //数据分包
                setDataSubcontractor(writeBytes);
                //读
//                mBluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true);
                mBluetoothLeService.readCharacteristic(notifyCharacteristic);

        }
    }

    //手动断开连接接口回调
    public OnDisConnectListener listener;
    public interface OnDisConnectListener{
        void setDisConnect(String result);
    }

    public void setOnClickDisConnectListener(OnDisConnectListener listener){
        this.listener = listener;
    }


    //连接成功之后的回调
    public interface OnConnectListener{
        void setConnect();
    }
    public OnConnectListener connectLister;

    public void setOnConnectListener(OnConnectListener connectListener){
        this.connectLister = connectListener;
    }


    public OnDConnectListener dconnectLister;
    //断开连接之后的回调
    public interface OnDConnectListener{
        void setDConnect();
    }
    public void setOnDConnectListener(OnDConnectListener dconnectListener){
        this.dconnectLister = dconnectListener;
    }


    public OnCallDataListener dataListener;
    //数据接收回调
    public interface OnCallDataListener{
        void callData(String data);
    }

    public void setOnCallDataListener(OnCallDataListener dataListener){
        this.dataListener = dataListener;
    }


}
