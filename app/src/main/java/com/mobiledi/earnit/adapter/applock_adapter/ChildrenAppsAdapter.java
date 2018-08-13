package com.mobiledi.earnit.adapter.applock_adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mobiledi.earnit.R;
import com.mobiledi.earnit.SharedPreference;
import com.mobiledi.earnit.model.newModels.AppsToBeBlockedOnOverdue;

import java.util.List;

public class ChildrenAppsAdapter extends RecyclerView.Adapter<ChildrenAppsAdapter.ViewHolder> {

    private List<AppsToBeBlockedOnOverdue> installedApps;
    private Context context;
    private SharedPreference sharedPreference;

    public ChildrenAppsAdapter(List<AppsToBeBlockedOnOverdue> appInfoList, Context context) {
        installedApps = appInfoList;
        this.context = context;
        sharedPreference = new SharedPreference();
    }

    @Override
    public ChildrenAppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AppsToBeBlockedOnOverdue appInfo = installedApps.get(position);
        String[] packageAndName = appInfo.getName().split("#");
        String appName = packageAndName[1];
        holder.applicationName.setText(appName);

        holder.switchView.setOnCheckedChangeListener(null);
        holder.cardView.setOnClickListener(null);
        if (appIsLocked(appInfo.getName())) {
            holder.switchView.setChecked(true);
        } else {
            holder.switchView.setChecked(false);
        }

        holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreference.addLocked(context, appInfo);
                } else {
                    sharedPreference.removeLocked(context, appInfo);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.switchView.performClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    private boolean appIsLocked(String checkApp) {
        boolean check = false;
        List<String> locked = sharedPreference.getLocked(context);
        for (String name : locked) {
            Log.d("dsfsdh", "name = " + name);
        }
        for (String lock : locked) {
            if (lock != null)
                if (lock.equals(checkApp)) {
                    check = true;
                    break;
                }
        }
        return check;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView applicationName;
        private CardView cardView;
        public Switch switchView;

        public ViewHolder(View v) {
            super(v);
            applicationName = (TextView) v.findViewById(R.id.applicationName);
            cardView = (CardView) v.findViewById(R.id.card_view);
            switchView = (Switch) v.findViewById(R.id.switchView);
        }
    }
}