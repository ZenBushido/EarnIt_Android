package com.mobiledi.earnit.activity.usageStats;


import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.os.Build;

import java.util.Comparator;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
  class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {


    @Override
    public int compare(UsageStats left, UsageStats right) {

        return (int) (right.getLastTimeUsed() - left.getLastTimeUsed());
        // return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
    }
}