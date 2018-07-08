package com.mobiledi.earnit.model.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.mobiledi.earnit.activity.usageStats.CustomUsageStats;

/**
 * Created by ashishkumar on 5/1/18.
 */

public class AppInfo {
    private String name;
    private String packageName;
    private String versionName;
    private int versionCode = 0;
    private Drawable icon;

    public AppInfo() {
    }

    public Intent getLaunchIntent(Context context) {
        return context.getPackageManager().getLaunchIntentForPackage(this.packageName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public static AppInfo from(CustomUsageStats customUsageStats){
        AppInfo appInfo = new AppInfo();
        appInfo.setName(customUsageStats.getAppName());
        appInfo.setPackageName(customUsageStats.getPackageName());
        return appInfo;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", icon=" + icon +
                '}';
    }
}
