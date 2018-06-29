package com.mobiledi.earnit.utils;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.mobiledi.earnit.R;
import com.mobiledi.earnit.activity.usageStats.CustomUsageStats;
import com.mobiledi.earnit.model.AppUsage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class AppsUsageHelper {

    private Context mContext;
    private UsageStatsManager mUsageStatsManager;
    private List<CustomUsageStats> mCustomUsageStatsList = new ArrayList<>();
    private List<AppUsage> mAppUsages;

    private long totalTime;

    public AppsUsageHelper(Context context) {

        mContext = context;
        mAppUsages = new ArrayList<>();
        mUsageStatsManager = (UsageStatsManager) mContext
                .getSystemService(Context.USAGE_STATS_SERVICE);

        String[] strings = mContext.getResources().getStringArray(R.array.action_list);

        StatsUsageInterval statsUsageInterval = StatsUsageInterval
                .getValue(strings[0]);

        if (statsUsageInterval != null) {
            List<UsageStats> usageStatsList =
                    getUsageStatistics(statsUsageInterval.mInterval);

            setTotalTime(usageStatsList);

            updateAppsList(usageStatsList);
        }
    }

    private LinkedHashMap<String, Long> mergeSameApps(List<CustomUsageStats> usageStats) throws PackageManager.NameNotFoundException {
        LinkedHashMap<String, Long> hashMap = new LinkedHashMap<>();
        PackageManager packageManager = mContext.getPackageManager();
        for (CustomUsageStats customUsageStats : usageStats) {
            String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(customUsageStats.usageStats.getPackageName(), PackageManager.GET_META_DATA));
            if (!hashMap.containsKey(appName)) {
                hashMap.put(appName, customUsageStats.usageStats.getTotalTimeInForeground());
            } else {
                hashMap.put(appName, hashMap.get(appName) + customUsageStats.usageStats.getTotalTimeInForeground());
            }
        }
        return hashMap;
    }

    public boolean isUsageStatEnable() {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(mContext.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
            return appOpsManager != null && appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName) == AppOpsManager.MODE_ALLOWED;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private List<UsageStats> getUsageStatistics(int intervalType) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return mUsageStatsManager.queryUsageStats(intervalType, cal.getTimeInMillis(), System.currentTimeMillis());
    }

    private void updateAppsList(List<UsageStats> usageStatsList) {
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats;
            try {
                customUsageStats = new CustomUsageStats(mContext, usageStatsList.get(i));
                customUsageStats.usageStats = usageStatsList.get(i);
                int percent = (int) (100 * customUsageStats.getTotalTimeInForeground() / totalTime);
                if (customUsageStats.usageStats.getTotalTimeInForeground() > 0 && percent > 0)
                    customUsageStatsList.add(customUsageStats);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        setCustomUsageStatsList(customUsageStatsList);
    }

    private void setAppUsages(List<CustomUsageStats> customUsageStatsList){
        for (CustomUsageStats customUsageStats : customUsageStatsList){
            AppUsage appUsage = new AppUsage().from(customUsageStats);
            mAppUsages.add(appUsage);
        }
    }

    public List<AppUsage> getAppUsages(){
        return mAppUsages;
    }

    private void setTotalTime(List<UsageStats> usageStatsList) {
        for (UsageStats usageStats : usageStatsList) {
            totalTime += usageStats.getTotalTimeInForeground();
        }
    }

    private void setCustomUsageStatsList(List<CustomUsageStats> customUsageStats) {
        mCustomUsageStatsList = sortingList(customUsageStats);
    }

    private List<CustomUsageStats> sortingList(List<CustomUsageStats> list) {
        Collections.sort(list, new Comparator<CustomUsageStats>() {
            public int compare(CustomUsageStats obj1, CustomUsageStats obj2) {
                // ## Ascending order
                return Long.compare(obj1.getTotalTimeInForeground(), obj2.getTotalTimeInForeground());
            }
        });
        List<CustomUsageStats> sortedList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            CustomUsageStats customUsageStats = list.get(i);
            if (customUsageStats.getTotalTimeInForeground() > 0)
                sortedList.add(customUsageStats);
        }
        setAppUsages(sortedList);
        return sortedList;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public List<CustomUsageStats> getCustomUsageStatsList() {
        return mCustomUsageStatsList;
    }

    enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }
}
