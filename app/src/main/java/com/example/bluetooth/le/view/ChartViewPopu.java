package com.example.bluetooth.le.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.db.bean.Signal;
import com.example.bluetooth.le.db.bean.SignalDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by zhdk on 2017/8/17.
 */

public class ChartViewPopu extends PopupWindow {
    private View contentView;
    private LineChartView chartView;
    private QueryBuilder qb;
    private List<Signal> list;
    private String[] yData = {"20","40","60","80","100"};
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private List<AxisValue> axisValuesY = new ArrayList<AxisValue>();
    private CardView cvClear;
    private SignalDao signal;
    private Activity context;
    public ChartViewPopu(Activity context, SignalDao signalDao){
        this.context = context;
        this.signal=signalDao;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pincode_dialog,null);
        chartView = (LineChartView) contentView.findViewById(R.id.line_chart);
        cvClear = (CardView) contentView.findViewById(R.id.cardview_clear);
        initData(signalDao);
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化
        setPopuWindow();
    }

    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#458b00"));  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setStrokeWidth(1);// 设置折线宽度
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setPointColor(Color.RED);// 设置节点颜色
        line.setPointRadius(5);// 设置节点半径
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
//        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        axisX.setName(context.getString(R.string.time));  //表格名称
        axisX.setTextSize(12);//设置字体大小
        axisX.setMaxLabelChars(2); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName(context.getString(R.string.range));//y轴标注
        axisY.setTextSize(12);//设置字体大小
        axisY.setTextColor(Color.BLACK);
        axisY.setMaxLabelChars(3);
        axisY.setHasLines(false);
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边
        for (int j = 0; j < 110; j+=20) {//循环为节点、X、Y轴添加数据
            axisValuesY.add(new AxisValue(j).setValue(j));// 添加Y轴显示的刻度值
        }
        axisY.setValues(axisValuesY);

        //设置行为属性，支持缩放、滑动以及平移
        chartView.setInteractive(true);
        chartView.setZoomEnabled(false);
        chartView.setZoomType(ZoomType.HORIZONTAL);
        chartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chartView.setLineChartData(data);
        chartView.setVisibility(View.VISIBLE);

        Viewport v = new Viewport(chartView.getMaximumViewport());
        v.bottom= 0f;
        v.top= 100f;
        chartView.setMaximumViewport(v);
        v.left = 0;
        v.right= 5;
        chartView.setCurrentViewport(v);

    }

    //设置点的显示
    private void getAxisPoints() {
        for (int i = 0; i < list.size(); i++) {
            mPointValues.add(new PointValue(i, list.get(i).getSingal()));
        }

    }

    //设置x坐标轴显示
    private void getAxisXLables() {
        for (int i = 0; i < list.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(list.get(i).getTime()));
        }
    }

    private void initData(SignalDao signalDao) {
        list = signalDao.loadAll();

    }

    private void setPopuWindow() {
        this.setContentView(contentView);// 设置View
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.AnimationFade);// 设置动画
//        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        contentView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = contentView.findViewById(R.id.id_pop_layout).getTop();
                int bottom = contentView.findViewById(R.id.id_pop_layout).getBottom();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height || y>bottom) {
                        dismiss();
//                        onDismissClick.setOnDismissClick();
                    }
                }
                return true;
            }
        });

        cvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.prompt);
                builder.setMessage(R.string.clear_ok);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signal.deleteAll();
                        dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no,null);
                builder.create().show();

            }
        });
    }


    private OnDismissClick onDismissClick;
    public  interface OnDismissClick{
        void setOnDismissClick();
    }
    public void setOnDismissClickListener(OnDismissClick onDismissClick){
        this.onDismissClick = onDismissClick;
    }
}
