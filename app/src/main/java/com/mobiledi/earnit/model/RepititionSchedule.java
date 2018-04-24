package com.mobiledi.earnit.model;

import com.mobiledi.earnit.utils.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by praks on 07/07/17.
 */

public class RepititionSchedule implements Serializable {
    private int id;
    private Long expiryDate;
    private String repeat;
    public String startTime ;
    public String endTime ;
    public int everyNRepeat = 0;
    public List<String> specificDays ;
    public List<DayTaskStatus> dayTaskStatuses ;

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

    public int getEveryNRepeat() {
        return everyNRepeat;
    }

    public void setEveryNRepeat(int everyNRepeat) {
        this.everyNRepeat = everyNRepeat;
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

    public RepititionSchedule(){}

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;

    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRepeat() {
        return Utils.checkIsNUll(repeat);
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    @Override
    public String toString() {
        return "RepititionSchedule{" +
                "id=" + id +
                ", expiryDate=" + expiryDate +
                ", repeat='" + repeat + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", everyNRepeat=" + everyNRepeat +
                ", specificDays=" + specificDays +
                ", dayTaskStatuses=" + dayTaskStatuses +
                '}';
    }
}
