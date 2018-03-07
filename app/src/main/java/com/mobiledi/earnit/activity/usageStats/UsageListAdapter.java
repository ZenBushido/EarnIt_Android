package com.mobiledi.earnit.activity.usageStats;


import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageManager;
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


import com.mobiledi.earnit.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {

    private List<CustomUsageStats> mCustomUsageStatsList = new ArrayList<>();
    Context context;
    String PackageName = "Nothing";
    String TAG = UsageListAdapter.class.getSimpleName();

    public UsageListAdapter(Context context) {
        this.context = context;
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */

    public  class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_package_name) TextView mPackageName;
        @BindView(R.id.textview_last_time_used) TextView mLastTimeUsed;
        @BindView(R.id.progressBar) ProgressBar mProgressBar;
        @BindView(R.id.rl_main)  RelativeLayout rl_main;



        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            int minute =  (int)mCustomUsageStatsList.get(3).usageStats.getTotalTimeInForeground() /1000 ;

          int max =  getMax(mCustomUsageStatsList);
          Log.e(TAG, "Max = "+max);

            mProgressBar.setMax(max/1000);
        }

        public TextView getLastTimeUsed() {
            return mLastTimeUsed;
        }

        public TextView getPackageName() {
            return mPackageName;
        }


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.usage_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        PackageManager packageManager = context.getPackageManager();
        try {


            if(mCustomUsageStatsList.get(position).usageStats.getTotalTimeInForeground() != 0)
            {
                viewHolder.rl_main.setVisibility(View.VISIBLE);
                String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(mCustomUsageStatsList.get(position).usageStats.getPackageName().toString(), PackageManager.GET_META_DATA));
                viewHolder.getPackageName().setText(appName);
                viewHolder.getLastTimeUsed().setText(getAppUsage(mCustomUsageStatsList.get(position).usageStats));
                Log.e(TAG, "Time: ="+ mCustomUsageStatsList.get(position).usageStats.getTotalTimeInForeground());
                long timeInforground = 500;
                timeInforground = mCustomUsageStatsList.get(position).usageStats.getTotalTimeInForeground();
                int time = (int)timeInforground/1000;
                Log.e(TAG, "Time= "+time);
                viewHolder.mProgressBar.setProgress((int) ((timeInforground / 1000 )));
            }

            else
            {
                //viewHolder.rl_main.setVisibility(View.GONE);
                viewHolder.rl_main.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getAppUsage(UsageStats usageStats) {
        long TimeInforground = 500;
        int minutes = 500, seconds = 500, hours = 500;


        if (usageStats != null) {
            TimeInforground = usageStats.getTotalTimeInForeground();
            PackageName = usageStats.getPackageName();
            minutes = (int) ((TimeInforground / (1000 * 60)) % 60);
            seconds = (int) (TimeInforground / 1000) % 60;
            hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);
            Log.e("TimeInforground", "Time in Foreground = " + TimeInforground);
            Log.e("BAC", "PackageName is" + PackageName + "Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s");
        }

        return hours + " hours " + minutes + " minutes ";

    }

    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    public void setCustomUsageStatsList(List<CustomUsageStats> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getMax(List<CustomUsageStats> list){
        int max = Integer.MIN_VALUE;

        for(int i=0; i<list.size(); i++){
            Log.e(TAG, "Max value= "+list.get(i).usageStats.getTotalTimeInForeground()/1000);

            if((double)list.get(i).usageStats.getTotalTimeInForeground() > max){
                max =  (int)list.get(i).usageStats.getTotalTimeInForeground();
            }
        }
        return max;
    }

}