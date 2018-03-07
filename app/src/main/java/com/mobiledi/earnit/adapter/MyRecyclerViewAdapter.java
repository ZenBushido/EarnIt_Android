package com.mobiledi.earnit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.utils.Utils;

import java.util.Collections;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Goal> mData ;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<Goal> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        String.valueOf(data.size());
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.simple_list_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String goal = mData.get(position).getGoalName();
        String amount = mData.get(position).getAmount()+"";
        holder.myTextView.setText(goal+":");

        double value = (double)mData.get(position).getCash()/(double)mData.get(position).getAmount();

        double percentage =  value * 100;

        percentage = Utils.roundOff(percentage, 1);
        String goalPercentageStr = "$"+mData.get(position).getCash()+" of " + "$"+mData.get(position).getAmount()
                +" / " +percentage+"%";

        holder.tv_amount.setText(goalPercentageStr);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myTextView;
        public TextView tv_amount;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.tvGoalName);
            tv_amount = (TextView) itemView.findViewById(R.id.tv_amount);
        }

    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id).getGoalName();
    }


}