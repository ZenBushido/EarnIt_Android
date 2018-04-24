package com.mobiledi.earnit.model.getChild;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RepititionSchedule {


    @SerializedName("id")
    @Expose
    long id;

    @SerializedName("startTime")
    @Expose
    String startTime;

    @SerializedName("endTime")
    @Expose
    String endTime;

    @SerializedName("repeat")
    @Expose
    String repeat;

    @SerializedName("repeaeveryNRepeatt")
    @Expose
    int everyNRepeat;

    @SerializedName("performTaskOnTheNSpecifiedDay")
    @Expose
    PerformTaskOnTheNSpecifiedDay performTaskOnTheNSpecifiedDay;

    @SerializedName("specificDays")
    @Expose
    List<String> specificDays;

    @SerializedName("dayTaskStatuses")
    @Expose
    List<DayTaskStatus> dayTaskStatuses;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getEveryNRepeat() {
        return everyNRepeat;
    }

    public void setEveryNRepeat(int everyNRepeat) {
        this.everyNRepeat = everyNRepeat;
    }

    public PerformTaskOnTheNSpecifiedDay getPerformTaskOnTheNSpecifiedDay() {
        return performTaskOnTheNSpecifiedDay;
    }

    public void setPerformTaskOnTheNSpecifiedDay(PerformTaskOnTheNSpecifiedDay performTaskOnTheNSpecifiedDay) {
        this.performTaskOnTheNSpecifiedDay = performTaskOnTheNSpecifiedDay;
    }

    public List<String> getSpecificDays() {
        return specificDays;
    }

    public void setSpecificDays(List<String> specificDays) {
        this.specificDays = specificDays;
    }

    public List<DayTaskStatus> getDayTaskStatuses() {
        return dayTaskStatuses;
    }

    public void setDayTaskStatuses(List<DayTaskStatus> dayTaskStatuses) {
        this.dayTaskStatuses = dayTaskStatuses;
    }

    public RepititionSchedule from(com.mobiledi.earnit.model.RepititionSchedule schedule){
        if  (schedule != null) {
            setId(schedule.getId());
            setStartTime(schedule.getStartTime());
            setEndTime(schedule.getEndTime());
            setRepeat(schedule.getRepeat());
            ArrayList<DayTaskStatus> statuses = new ArrayList<>();
            for (com.mobiledi.earnit.model.DayTaskStatus status : schedule.getDayTaskStatuses()) {
                statuses.add(new DayTaskStatus().from(status));
            }
            setDayTaskStatuses(statuses);
            setEveryNRepeat(schedule.getEveryNRepeat());
//            setPerformTaskOnTheNSpecifiedDay(schedule.getP);
            setSpecificDays(schedule.getSpecificDays());
        }
        return this;
    }

    @Override
    public String toString() {
        return "RepititionSchedule{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", repeat='" + repeat + '\'' +
                ", everyNRepeat=" + everyNRepeat +
                ", performTaskOnTheNSpecifiedDay=" + performTaskOnTheNSpecifiedDay +
                ", specificDays=" + specificDays +
                ", dayTaskStatuses=" + dayTaskStatuses +
                '}';
    }
}
