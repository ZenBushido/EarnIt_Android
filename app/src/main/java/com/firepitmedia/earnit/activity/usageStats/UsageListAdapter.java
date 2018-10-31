package com.firepitmedia.earnit.activity.usageStats;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.utils.AppsUsageHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {

    private List<CustomUsageStats> mCustomUsageStatsList;
    private Context context;

    private long totalTime;

    public UsageListAdapter(Context context, List<CustomUsageStats> appUsages, long totalTime) {
        this.context = context;
        mCustomUsageStatsList = appUsages;
        Log.d("sdlfkjslk", "UsageListAdapter totalTime: " + totalTime);
        AppsUsageHelper appsUsageHelper = new AppsUsageHelper(context);
        if (totalTime == 0) {
            Log.d("sdlfkjslk", "1");
            this.totalTime = appsUsageHelper.getTotalTime() == 0 ? 1 : appsUsageHelper.getTotalTime();
        } else {
            Log.d("sdlfkjslk", "2");
            this.totalTime = totalTime;
        }
        if (appsUsageHelper.isUsageStatEnable() && mCustomUsageStatsList == null) {
            initializeMapList(appsUsageHelper);
        }
        Log.d("sdlfkjslk", "UsageListAdapter totalTime: " + totalTime);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.usage_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        CustomUsageStats customUsageStats = mCustomUsageStatsList.get(position);
        String[] packageAndName = customUsageStats.getAppName().split("#");
        String appName = packageAndName[1];
        int percent = (int) (100 * customUsageStats.getTotalTimeInForeground() / (totalTime == 0 ? 1 : totalTime));
        Log.d("sdlfkjslk", "1 getTotalTimeInForeground: " + customUsageStats.getTotalTimeInForeground());
        Log.d("sdlfkjslk", "1 totalTime: " + totalTime);
        Log.d("sdlfkjslk", "1 percent: " + percent);
        viewHolder.rl_main.setVisibility(View.VISIBLE);
        viewHolder.getPackageName().setText(appName);
        viewHolder.getLastTimeUsed().setText(percent + "%");
        viewHolder.mProgressBar.setProgress(percent);
    }

    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    private void initializeMapList(AppsUsageHelper appsUsageHelper) {
        mCustomUsageStatsList = appsUsageHelper.getCustomUsageStatsList();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_package_name)
        TextView mPackageName;
        @BindView(R.id.textview_last_time_used)
        TextView mLastTimeUsed;
        @BindView(R.id.progressBar)
        ProgressBar mProgressBar;
        @BindView(R.id.rl_main)
        RelativeLayout rl_main;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

//            int max = getMax(mCustomUsageStatsList);
//            Log.e(TAG, "Max = " + max);

//            mProgressBar.setMax(max / 1000);
        }

        public TextView getLastTimeUsed() {
            return mLastTimeUsed;
        }

        public TextView getPackageName() {
            return mPackageName;
        }


    }

}