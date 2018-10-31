package com.firepitmedia.earnit.model;

import com.firepitmedia.earnit.utils.Utils;

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
    public String performTaskOnTheNSpecifiedDay ;
    public int everyNRepeat = 0;
    public List<String> specificDays ;
    public List<DayTaskStatus> dayTaskStatuses ;

    public String getPerformTaskOnTheNSpecifiedDay() {
        return performTaskOnTheNSpecifiedDay;
    }

    public void setPerformTaskOnTheNSpecifiedDay(String performTaskOnTheNSpecifiedDay) {
        this.performTaskOnTheNSpecifiedDay = performTaskOnTheNSpecifiedDay;
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

    public boolean monthlyRepeatHasNumbers(){
        if (specificDays == null || specificDays.size() == 0) return false;
        try{
            Integer.parseInt(specificDays.get(0));
        } catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RepititionSchedule{" +
                "id=" + id +
                ", expiryDate=" + expiryDate +
                ", repeat='" + repeat + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", performTaskOnTheNSpecifiedDay='" + performTaskOnTheNSpecifiedDay + '\'' +
                ", everyNRepeat=" + everyNRepeat +
                ", specificDays=" + specificDays +
                ", dayTaskStatuses=" + dayTaskStatuses +
                '}';
    }
}
