package com.example.bluetooth.le.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.utils.BleUtils;
import com.example.bluetooth.le.utils.SPUtils;
import com.example.bluetooth.le.utils.ToastUtil;

import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.List;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/13.
 * Time:10:00.
 */

public class SetDayPopuWindow extends PopupWindow implements View.OnClickListener{
    private View view;
    private CheckBox cbMon,cbTue,cbWed,cbThu,cbFri,cbSat,cbSun;
    private TextView tvNo,tvYes;
    private LinearLayout layoutMon,layoutTue,layoutWed,layoutThu,layoutFri,layoutSat,layoutSun;
    private Activity context;
    private SPUtils spUtils;
    public static final  String ISCBMON="isCbMon";
    public static final  String ISCBTUE="isCbTue";
    public static final  String ISCBWEB="isCbWeb";
    public static final  String ISCBTHU="isCbThu";
    public static final  String ISCBFRI="isCbFri";
    public static final  String ISCBSAT="isCbSat";
    public static final  String ISCBSUN="isCbSun";

    private String callData ;

    public SetDayPopuWindow(Activity context,String data){
            super(context);
        this.context = context;
        this.callData = BleUtils.hexString2binaryString(data);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.setdaypopuwindow,null);
        cbMon= (CheckBox) view.findViewById(R.id.cb_mon);
        cbTue= (CheckBox) view.findViewById(R.id.cb_tue);
        cbWed= (CheckBox) view.findViewById(R.id.cb_wed);
        cbThu= (CheckBox) view.findViewById(R.id.cb_thu);
        cbFri= (CheckBox) view.findViewById(R.id.cb_fri);
        cbSat= (CheckBox) view.findViewById(R.id.cb_sat);
        cbSun= (CheckBox) view.findViewById(R.id.cb_sun);
        tvNo= (TextView) view.findViewById(R.id.popu_no);
        tvYes= (TextView) view.findViewById(R.id.popu_yes);
        tvYes.setOnClickListener(this);
        layoutMon = (LinearLayout) view.findViewById(R.id.layout_mon);
        layoutMon.setOnClickListener(this);
        layoutTue = (LinearLayout) view.findViewById(R.id.layout_tue);
        layoutTue.setOnClickListener(this);
        layoutWed = (LinearLayout) view.findViewById(R.id.layout_wed);
        layoutWed.setOnClickListener(this);
        layoutThu = (LinearLayout) view.findViewById(R.id.layout_thu);
        layoutThu.setOnClickListener(this);
        layoutFri = (LinearLayout) view.findViewById(R.id.layout_fri);
        layoutFri.setOnClickListener(this);
        layoutSat = (LinearLayout) view.findViewById(R.id.layout_sat);
        layoutSat.setOnClickListener(this);
        layoutSun = (LinearLayout) view.findViewById(R.id.layout_sun);
        layoutSun.setOnClickListener(this);
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
//        设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.take_photo_anim);
        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.popu_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });
        spUtils = new SPUtils(context,"fileName");
        if (callData.substring(6,7).equals("1")){
            cbMon.setChecked(true);
        }else {
            cbMon.setChecked(false);
        }

        if (callData.substring(5,6).equals("1")){
            cbTue.setChecked(true);
        }else {
            cbTue.setChecked(false);
        }

        if (callData.substring(4,5).equals("1")){
            cbWed.setChecked(true);
        }else {
            cbWed.setChecked(false);
        }



        if (callData.substring(3,4).equals("1")){
            cbThu.setChecked(true);
        }else {
            cbThu.setChecked(false);
        }

        if (callData.substring(2,3).equals("1")){
            cbFri.setChecked(true);
        }else {
            cbFri.setChecked(false);
        }

        if (callData.substring(1,2).equals("1")){
            cbSat.setChecked(true);
        }else {
            cbSat.setChecked(false);
        }
        if (callData.substring(7,8).equals("1")){
            cbSun.setChecked(true);
        }else {
            cbSun.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_mon:
                if (cbMon.isChecked()){
                    cbMon.setChecked(false);
                }else {
                    cbMon.setChecked(true);
                }
                break;
            case R.id.layout_tue:
                if (cbTue.isChecked()){
                    cbTue.setChecked(false);
                }else {
                    cbTue.setChecked(true);
                }
                break;
            case R.id.layout_wed:
                if (cbWed.isChecked()){
                    cbWed.setChecked(false);
                }else {
                    cbWed.setChecked(true);
                }
                break;
            case R.id.layout_thu:
                if (cbThu.isChecked()){
                    cbThu.setChecked(false);
                }else {
                    cbThu.setChecked(true);
                }
                break;
            case R.id.layout_fri:
                if(cbFri.isChecked()){
                    cbFri.setChecked(false);
                }else {
                    cbFri.setChecked(true);
                }
                break;
            case R.id.layout_sat:
                if (cbSat.isChecked()){
                    cbSat.setChecked(false);
                }else {
                    cbSat.setChecked(true);
                }
                break;
            case R.id.layout_sun:
                if (cbSun.isChecked()) {
                    cbSun.setChecked(false);
                }else {
                    cbSun.setChecked(true);
                }
                break;
            case R.id.popu_yes:
                String text = "";
                int num  = 0;
                if (cbMon.isChecked()){
                    num += 2;
                    spUtils.putBoolean(ISCBMON,true);
                }else {
                    spUtils.putBoolean(ISCBMON,false);
                }
                if (cbSat.isChecked()){
                    num += 64;
                    spUtils.putBoolean(ISCBSAT, true);
                }else {
                    spUtils.putBoolean(ISCBSAT, false);
                }
                if (cbFri.isChecked()){
                    num += 32;
                    spUtils.putBoolean(ISCBFRI, true);
                }else {
                    spUtils.putBoolean(ISCBFRI, false);
                }
                if (cbThu.isChecked()){
                    num += 16;
                    spUtils.putBoolean(ISCBTHU, true);
                }else {
                    spUtils.putBoolean(ISCBTHU, false);
                }
                if (cbSun.isChecked()){
                    num += 1;
                    spUtils.putBoolean(ISCBSUN, true);
                }else {
                    spUtils.putBoolean(ISCBSUN, false);
                }
                if (cbTue.isChecked()){
                    num += 4;
                    spUtils.putBoolean(ISCBTUE, true);
                }else {
                    spUtils.putBoolean(ISCBTUE, false);
                }
                if (cbWed.isChecked()){
                    num += 8;
                    spUtils.putBoolean(ISCBWEB, true);
                }else {
                    spUtils.putBoolean(ISCBWEB,false);
                }

                //十进制转二进制
               String tex =  BleUtils.tenString2binaryString(num);
               String bl =  String.format("%08d",Integer.valueOf(tex));

                //二进制转十六进制
                String tenToHex = BleUtils.binaryString2hexString(bl);
                listener.setCommand("030012"+tenToHex);
                dismiss();
                break;
        }
    }

    private OnCheckListener listener;
    public interface OnCheckListener{
        void setCommand(String text);
    }
    public void setOnCheckListener(OnCheckListener listener){
        this.listener = listener;
    }

}
