package com.example.bluetooth.le.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.base.BaseActivity;
import com.example.bluetooth.le.utils.ToastUtil;
import com.example.bluetooth.le.utils.UiUtils;
import com.kongqw.rockerlibrary.view.RockerView;

/**
 * Created by zhdk on 2017/8/1.
 */

public class OperatingActivity extends BaseActivity {
    private TextView tvX;
    private Button btnOpen,btnClose;
    private SensorManager sensorManager;
    private Sensor sensor;
    private OperatListener operatListener;
    private Toolbar toolbar;
    private RockerView rockerView;
    private int left=0,right = 0,up=0,down=0;

    @Override
    public int getContentView() {
        return R.layout.operating_layout;
    }

    @Override
    public void initView() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle("感应测试");
//        toolbar.setNavigationIcon(R.drawable.back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        tvX = (TextView) findViewById(R.id.sensorX);
//        btnOpen = (Button) findViewById(R.id.btnOpen);
//        btnOpen.setOnClickListener(this);
//        btnClose = (Button) findViewById(R.id.btnClose);
//        btnClose.setOnClickListener(this);
//        rockerView = (RockerView) findViewById(R.id.rockerView);
//        rockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
        operatListener = new OperatListener();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        rockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_4_ROTATE_45, new RockerView.OnShakeListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void direction(RockerView.Direction direction) {
//                ToastUtil.showToast(OperatingActivity.this,"摇动方向："+getDirection(direction));
//                String directionStr = getDirection(direction);
//                if (directionStr.equals("left")){
//                    HomeActivity.sendData(OperatingActivity.this,"0x01");
//                }else if (directionStr.equals("right")){
//                    HomeActivity.sendData(OperatingActivity.this,"0x02");
//                }else if (directionStr.equals("up")){
//                    HomeActivity.sendData(OperatingActivity.this,"0x03");
//                }else if (directionStr.equals("down")){
//                    HomeActivity.sendData(OperatingActivity.this,"0x04");
//                }
//                HomeActivity.dismissDialog();
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
    }

//    private String getDirection(RockerView.Direction direction) {
//        String message = null;
//        switch (direction) {
//            case DIRECTION_LEFT:
//                message = "left";
//                break;
//            case DIRECTION_RIGHT:
//                message = "right";
//                break;
//            case DIRECTION_UP:
//                message = "up";
//                break;
//            case DIRECTION_DOWN:
//                message = "down";
//                break;
//            default:
//                break;
//        }
//        return message;
//    }

    @Override
    public void setWindowFeature() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(operatListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager!=null)
        sensorManager.unregisterListener(operatListener);
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.btnOpen:
//                sensorManager.registerListener(operatListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
//                break;
//            case R.id.btnClose:
//                sensorManager.unregisterListener(operatListener);
//                tvX.setText("");
//                break;
//        }
//    }
    class OperatListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float xValue = sensorEvent.values[0];
            float yValue = sensorEvent.values[1];
            float zValue = sensorEvent.values[2];

            if (yValue<-5 && zValue <10){
                if (left == 0) {
                    HomeActivity.sendData(OperatingActivity.this, "01");
                    tvX.setText(getString(R.string.left));
                    left=1;
                    right = 0;
                    up=0;
                    down=0;
                }
            }else  if (yValue>5 && zValue<10){
                if (right == 0) {
                    HomeActivity.sendData(OperatingActivity.this, "02");
                    tvX.setText(getString(R.string.right));
                    right = 1;
                    left = 0;
                    up=0;
                    down=0;
                }
            }else if (xValue>5 && zValue<10){
                if (down == 0) {
                    HomeActivity.sendData(OperatingActivity.this, "03");
                    tvX.setText(getString(R.string.backward));
                    down = 1;
                    right = 0;
                    up=0;
                    left=0;
                }
            }else  if (xValue<-3 && zValue <10){
                if (up==0) {
                    HomeActivity.sendData(OperatingActivity.this, "04");
                    tvX.setText(getString(R.string.forward));
                    up=1;
                    right = 0;
                    left=0;
                    down=0;
                }
            }
            HomeActivity.dismissDialog();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
