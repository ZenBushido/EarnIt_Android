package com.firepitmedia.earnit.model;

import java.util.List;

/**
 * Created by adox on 24.01.2018..
 */

public class RepSchedule {
    public int id ;
    public String startTime ;
    public String endTime ;
    public String repeat;
    public int everyNRepeat ;
    public List<String> specificDays ;
    public List<DayTaskStatus> dayTaskStatuses ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public RepSchedule() {
    }
}
