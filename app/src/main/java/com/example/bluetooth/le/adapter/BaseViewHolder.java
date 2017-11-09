package com.example.bluetooth.le.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/14.
 * Time:9:12.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> views;
    private View convertView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        views = new SparseArray<>();
        convertView = itemView;
    }

    protected <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    //显示文字信息的方法
    public void setText(int resId, String text) {
        TextView view = getView(resId);
        view.setText(text);
    }

    public  void setTextColorResource(int resId,int color){
        TextView view  = getView(resId);
        view.setTextColor(color);
    }
    public void setCompoundDrawablesWithIntrinsicBounds(int resId,int flagId){
        TextView view  = getView(resId);
        view.setCompoundDrawablesWithIntrinsicBounds(flagId,0,0,0);
    }
}
