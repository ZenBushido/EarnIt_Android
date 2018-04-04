package com.mobiledi.earnit.model.addTask;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RepititionSchedule {



    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("repeat")
    @Expose
    private String repeat;
    @SerializedName("everyNRepeat")
    @Expose
    private Integer everyNRepeat;
    @SerializedName("performTaskOnTheNSpecifiedDay")
    @Expose
    private String performTaskOnTheNSpecifiedDay;
    @SerializedName("specificDays")
    @Expose
    private List<String> specificDays = null;

    public RepititionSchedule(String startTime, String endTime, String repeat, Integer everyNRepeat, String performTaskOnTheNSpecifiedDay, List<String> specificDays) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeat = repeat;
        this.everyNRepeat = everyNRepeat;
        this.performTaskOnTheNSpecifiedDay = performTaskOnTheNSpecifiedDay;
        this.specificDays = specificDays;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public Integer getEveryNRepeat() {
        return everyNRepeat;
    }

    public void setEveryNRepeat(Integer everyNRepeat) {
        this.everyNRepeat = everyNRepeat;
    }

    public String getPerformTaskOnTheNSpecifiedDay() {
        return performTaskOnTheNSpecifiedDay;
    }

    public void setPerformTaskOnTheNSpecifiedDay(String performTaskOnTheNSpecifiedDay) {
        this.performTaskOnTheNSpecifiedDay = performTaskOnTheNSpecifiedDay;
    }

    public List<String> getSpecificDays() {
        return specificDays;
    }

    public void setSpecificDays(List<String> specificDays) {
        this.specificDays = specificDays;
    }

}
