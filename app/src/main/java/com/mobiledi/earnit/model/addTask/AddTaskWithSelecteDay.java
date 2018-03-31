package com.mobiledi.earnit.model.addTask;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 31/03/18.
 */

public class AddTaskWithSelecteDay {

    public AddTaskWithSelecteDay(Double allowance, String dueDate, String name, Boolean pictureRequired, Children children, Goal goal, RepititionSchedule repititionSchedule, String description, Boolean isDeleted, Boolean shouldLockAppsIfTaskOverdue) {

        this.allowance = allowance;
        this.dueDate = dueDate;
        this.name = name;
        this.pictureRequired = pictureRequired;
        this.children = children;
        this.goal = goal;
        this.repititionSchedule = repititionSchedule;
        this.description = description;
        this.isDeleted = isDeleted;
        this.shouldLockAppsIfTaskOverdue = shouldLockAppsIfTaskOverdue;
    }

    @SerializedName("allowance")
    @Expose
    private Double allowance;
    @SerializedName("dueDate")
    @Expose
    private String dueDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("pictureRequired")
    @Expose
    private Boolean pictureRequired;
    @SerializedName("children")
    @Expose
    private Children children;
    @SerializedName("goal")
    @Expose
    private Goal goal;
    @SerializedName("repititionSchedule")
    @Expose
    private RepititionSchedule repititionSchedule;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("shouldLockAppsIfTaskOverdue")
    @Expose
    private Boolean shouldLockAppsIfTaskOverdue;

    public Double getAllowance() {
        return allowance;
    }

    public void setAllowance(Double allowance) {
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

    public Boolean getPictureRequired() {
        return pictureRequired;
    }

    public void setPictureRequired(Boolean pictureRequired) {
        this.pictureRequired = pictureRequired;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getShouldLockAppsIfTaskOverdue() {
        return shouldLockAppsIfTaskOverdue;
    }

    public void setShouldLockAppsIfTaskOverdue(Boolean shouldLockAppsIfTaskOverdue) {
        this.shouldLockAppsIfTaskOverdue = shouldLockAppsIfTaskOverdue;
    }

}
