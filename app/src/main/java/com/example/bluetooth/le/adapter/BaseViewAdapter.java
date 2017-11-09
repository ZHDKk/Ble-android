package com.example.bluetooth.le.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * sumec
 * User:zhdk1996
 * Date: 2017/7/14.
 * Time:9:17.
 */

public abstract class BaseViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> list = new ArrayList<>();
    protected Context context;
    protected LayoutInflater inflater;
    int layoutId;

    public void addAllData(List<T> dataList) {
        this.list.addAll(dataList);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list.clear();
    }

    public BaseViewAdapter(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(inflater.inflate(layoutId, parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        onBindData(holder, position);
    }

    public abstract void onBindData(BaseViewHolder holder, int position);


    @Override
    public int getItemCount() {
        return list.size();
    }

}
