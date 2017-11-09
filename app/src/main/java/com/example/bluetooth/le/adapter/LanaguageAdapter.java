package com.example.bluetooth.le.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bluetooth.le.R;

/**
 * Created by zhdk on 2017/7/22.
 */

public  class LanaguageAdapter extends RecyclerView.Adapter<BaseViewHolder>{
    private final Context mContext;
    private final String[] mDataset;
    private LayoutInflater inflater;

    public LanaguageAdapter(Context context, String[] dataset) {
        mContext = context;
        mDataset = dataset;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(inflater.inflate(R.layout.language_item,null));
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
//        String[] values = mDataset[position].split(",");
//        String countryName = values[0];
//        int flagResId = mContext.getResources().getIdentifier(values[1], "drawable", mContext.getPackageName());
//        holder.setText(R.id.tv_language,countryName);
//        holder.setCompoundDrawablesWithIntrinsicBounds(R.id.tv_language,flagResId);
        holder.setText(R.id.tv_language,mDataset[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onClick(int position);
    }
    public void setOnClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
