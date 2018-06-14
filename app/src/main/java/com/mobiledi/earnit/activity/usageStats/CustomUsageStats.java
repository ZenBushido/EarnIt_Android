package com.mobiledi.earnit.activity.usageStats;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.mobiledi.earnit.model.AppUsageResponse;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by user5 on 13/2/18.
 */
public class CustomUsageStats {

    private final static String API_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";

    private String appName;
    private long firstTimeStamp;
    private long lastTimeStamp;
    private long lastTimeUsed;
    private long totalTimeInForeground;

    public CustomUsageStats() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomUsageStats(Context context, UsageStats usageStats) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        this.usageStats = usageStats;
        this.appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(usageStats.getPackageName(), PackageManager.GET_META_DATA));;
        this.firstTimeStamp = usageStats.getFirstTimeStamp();
        this.lastTimeStamp = usageStats.getLastTimeStamp();
        this.lastTimeUsed = usageStats.getLastTimeUsed();
        this.totalTimeInForeground = usageStats.getTotalTimeInForeground();
        DateTime first = new DateTime(lastTimeUsed);
        DateTime second = new DateTime(lastTimeUsed + totalTimeInForeground);
        Log.d("dfkjsdhfkjh", "Array = " + Arrays.toString(new String[] {first.toString(API_TIME_FORMAT), second.toString(API_TIME_FORMAT)}));
    }

    public UsageStats usageStats;
    // public Drawable appIcon;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getFirstTimeStamp() {
        return firstTimeStamp;
    }

    public void setFirstTimeStamp(long firstTimeStamp) {
        this.firstTimeStamp = firstTimeStamp;
    }

    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }

    public void setTotalTimeInForeground(long totalTimeInForeground) {
        this.totalTimeInForeground = totalTimeInForeground;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public void setUsageStats(UsageStats usageStats) {
        this.usageStats = usageStats;
    }

    public String[] getUsingTimeAsString(){
        DateTime first = new DateTime(lastTimeUsed);
        DateTime second = new DateTime(lastTimeUsed + totalTimeInForeground);
        //2018-05-31T10:15:30+01:00
        return new String[] {first.toString(API_TIME_FORMAT), second.toString(API_TIME_FORMAT)};
    }

    public CustomUsageStats from(AppUsageResponse appUsageResponse){
        Log.d("sdlfkjslk", "from() MINUTES: " + appUsageResponse.toString());
        appName = appUsageResponse.getAppName();
        totalTimeInForeground = TimeUnit.MINUTES.toMillis(appUsageResponse.getTimeUsedMinutes());
        Log.d("sdlfkjslk", "from() MILLIS: " + totalTimeInForeground);
//        totalTimeInForeground = appUsageResponse.getTimeUsedMinutes();
        return this;
    }
}
