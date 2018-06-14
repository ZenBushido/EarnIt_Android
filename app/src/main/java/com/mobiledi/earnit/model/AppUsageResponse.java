package com.mobiledi.earnit.model;

import com.google.gson.annotations.SerializedName;

public class AppUsageResponse {

    @SerializedName("appName")
    private String appName;

    @SerializedName("timeUsed")
    private long timeUsedMinutes;

    public AppUsageResponse(String appName, long timeUsedMinutes) {
        this.appName = appName;
        this.timeUsedMinutes = timeUsedMinutes;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getTimeUsedMinutes() {
        return timeUsedMinutes;
    }

    public void setTimeUsedMinutes(long timeUsedMinutes) {
        this.timeUsedMinutes = timeUsedMinutes;
    }

    @Override
    public String toString() {
        return "AppUsageResponse{" +
                "appName='" + appName + '\'' +
                ", timeUsedMinutes=" + timeUsedMinutes +
                '}';
    }
}
