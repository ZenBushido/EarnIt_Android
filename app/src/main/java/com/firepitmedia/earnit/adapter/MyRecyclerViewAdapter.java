package com.firepitmedia.earnit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.model.goal.GetAllGoalResponse;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<GetAllGoalResponse> mData ;
    private LayoutInflater mInflater;
    String TAG = MyRecyclerViewAdapter.class.getSimpleName();
    String goalPercentageStr;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<GetAllGoalResponse> data) {
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
        String goal = mData.get(position).getName();
        String amount = mData.get(position).getAmount()+"";
        holder.myTextView.setText(goal+":");
        Integer goalTotal = 0;

        if(!mData.get(position).getAdjustments().isEmpty())
        {
            for(int j=0; j<mData.get(position).getAdjustments().size(); j++)
            {
                goalTotal+= mData.get(position).getAdjustments().get(j).getAmount();
                Log.d("dlfjhsdkhj", "amount: " + goalTotal);
            }

            goalTotal = goalTotal+ mData.get(position).getAmount();
            Log.d("dlfjhsdkhj", "total amount: " + goalTotal);
            goalPercentageStr = "$"+goalTotal+ " of " +"$"+ mData.get(position).getAmount();
            holder.tv_amount.setText(goalPercentageStr);

        }
        else
        {

            goalTotal = goalTotal+ mData.get(position).getAmount();
            String goalPercentageStr = "$"+goalTotal+" of " + "$"+mData.get(position).getAmount();

            holder.tv_amount.setText(goalPercentageStr);
        }

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
            myTextView = itemView.findViewById(R.id.tvGoalName);
            tv_amount = itemView.findViewById(R.id.tv_amount);
        }

    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id).getName();
    }


}