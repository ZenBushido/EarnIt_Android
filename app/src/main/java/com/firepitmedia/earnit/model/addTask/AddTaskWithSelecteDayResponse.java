package com.firepitmedia.earnit.model.addTask;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddTaskWithSelecteDayResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("allowance")
    @Expose
    private Integer allowance;
    @SerializedName("createDate")
    @Expose
    private String createDate;
    @SerializedName("dueDate")
    @Expose
    private String dueDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("pictureRequired")
    @Expose
    private Boolean pictureRequired;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("updateDate")
    @Expose
    private Object updateDate;
    @SerializedName("taskComments")
    @Expose
    private Object taskComments;
    @SerializedName("goal")
    @Expose
    private Goal goal;
    @SerializedName("repititionSchedule")
    @Expose
    private RepititionSchedule repititionSchedule;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("shouldLockAppsIfTaskOverdue")
    @Expose
    private Boolean shouldLockAppsIfTaskOverdue;
    @SerializedName("deleted")
    @Expose
    private Boolean deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAllowance() {
        return allowance;
    }

    public void setAllowance(Integer allowance) {
        this.allowance = allowance;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public Boolean getPictureRequired() {
        return pictureRequired;
    }

    public void setPictureRequired(Boolean pictureRequired) {
        this.pictureRequired = pictureRequired;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Object updateDate) {
        this.updateDate = updateDate;
    }

    public Object getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(Object taskComments) {
        this.taskComments = taskComments;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public RepititionSchedule getRepititionSchedule() {
        return repititionSchedule;
    }

    public void setRepititionSchedule(RepititionSchedule repititionSchedule) {
        this.repititionSchedule = repititionSchedule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getShouldLockAppsIfTaskOverdue() {
        return shouldLockAppsIfTaskOverdue;
    }

    public void setShouldLockAppsIfTaskOverdue(Boolean shouldLockAppsIfTaskOverdue) {
        this.shouldLockAppsIfTaskOverdue = shouldLockAppsIfTaskOverdue;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public class Goal {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("amount")
        @Expose
        private Integer amount;
        @SerializedName("createDate")
        @Expose
        private Object createDate;
        @SerializedName("name")
        @Expose
        private Object name;
        @SerializedName("updateDate")
        @Expose
        private Object updateDate;
        @SerializedName("tally")
        @Expose
        private Integer tally;
        @SerializedName("tallyPercent")
        @Expose
        private Integer tallyPercent;
        @SerializedName("cash")
        @Expose
        private Integer cash;
        @SerializedName("adjustments")
        @Expose
        private Object adjustments;
        @SerializedName("deleted")
        @Expose
        private Boolean deleted;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Object getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Object createDate) {
            this.createDate = createDate;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }

        public Object getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(Object updateDate) {
            this.updateDate = updateDate;
        }

        public Integer getTally() {
            return tally;
        }

        public void setTally(Integer tally) {
            this.tally = tally;
        }

        public Integer getTallyPercent() {
            return tallyPercent;
        }

        public void setTallyPercent(Integer tallyPercent) {
            this.tallyPercent = tallyPercent;
        }

        public Integer getCash() {
            return cash;
        }

        public void setCash(Integer cash) {
            this.cash = cash;
        }

        public Object getAdjustments() {
            return adjustments;
        }

        public void setAdjustments(Object adjustments) {
            this.adjustments = adjustments;
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }

    }


    public class RepititionSchedule {

        @SerializedName("id")
        @Expose
        private Integer id;
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
        @SerializedName("dayTaskStatuses")
        @Expose
        private Object dayTaskStatuses;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
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

        public Object getDayTaskStatuses() {
            return dayTaskStatuses;
        }

        public void setDayTaskStatuses(Object dayTaskStatuses) {
            this.dayTaskStatuses = dayTaskStatuses;
        }

    }

}





