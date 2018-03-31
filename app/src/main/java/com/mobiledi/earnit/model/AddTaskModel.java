package com.mobiledi.earnit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ashishkumar on 30/12/17.
 */

public class AddTaskModel {


    @SerializedName("allowance")
    public double allowance;
    @SerializedName("dueDate")
    public String dueDate;
    @SerializedName("name")
    public String name;
    @SerializedName("children")
    public Children children;
    @SerializedName("startTime")
    public String startTime;
    @SerializedName("endTime")
    public String endTime;
    @SerializedName("repeat")
    public String repeat;




    public AddTaskModel.repititionSchedule getRepititionSchedule() {
        return repititionSchedule;
    }

    public void setRepititionSchedule(AddTaskModel.repititionSchedule repititionSchedule) {
        this.repititionSchedule = repititionSchedule;
    }

    @SerializedName("repititionSchedule")
    public repititionSchedule repititionSchedule;
    @SerializedName("goal")
    public Goal goal;
    @SerializedName("description")
    public String description;
    @SerializedName("isDeleted")
    public boolean isDeleted;
    @SerializedName("shouldLockAppsIfTaskOverdue")
    public boolean shouldLockAppsIfTaskOverdue;


    public static class Children {
        @SerializedName("id")
        public int id;
    }



    public static class Goal {
        @SerializedName("id")
        public int id;
    }


    public static class repititionSchedule {
        @SerializedName("startTime")
        public String startTime;
        @SerializedName("endTime")
        public String endTime;
        @SerializedName("repeat")
        public String repeat;
        public Integer everyNday;
        public String date;
        public String onFirst;
        public String onDay;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getEveryNday() {
            return everyNday;
        }

        public void setEveryNday(Integer everyNday) {
            this.everyNday = everyNday;
        }

        public List<String> getSpecificDays() {
            return specificDays;
        }

        public void setSpecificDays(List<String> specificDays) {
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

        @SerializedName("specificDays")
        public List<String> specificDays;
    }


    public double getAllowance() {
        return allowance;
    }

    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Children getChildren() {
        return children;
    }

    public void setChildren(Children children) {
        this.children = children;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isShouldLockAppsIfTaskOverdue() {
        return shouldLockAppsIfTaskOverdue;
    }

    public void setShouldLockAppsIfTaskOverdue(boolean shouldLockAppsIfTaskOverdue) {
        this.shouldLockAppsIfTaskOverdue = shouldLockAppsIfTaskOverdue;
    }
}
