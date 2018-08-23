package com.mobiledi.earnit.model.newModels;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task {

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
    private String updateDate;
    @SerializedName("taskComments")
    @Expose
    private List<TaskComment> taskComments = null;
    @SerializedName("goal")
    @Expose
    private Object goal;
    @SerializedName("repititionSchedule")
    @Expose
    private Object repititionSchedule;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("shouldLockAppsIfTaskOverdue")
    @Expose
    private Boolean shouldLockAppsIfTaskOverdue;
    @SerializedName("appsToBeBlockedOnOverdue")
    @Expose
    private List<AppsToBeBlockedOnOverdue> appsToBeBlockedOnOverdue = null;
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

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public List<TaskComment> getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(List<TaskComment> taskComments) {
        this.taskComments = taskComments;
    }

    public Object getGoal() {
        return goal;
    }

    public void setGoal(Object goal) {
        this.goal = goal;
    }

    public Object getRepititionSchedule() {
        return repititionSchedule;
    }

    public void setRepititionSchedule(Object repititionSchedule) {
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

    public List<AppsToBeBlockedOnOverdue> getAppsToBeBlockedOnOverdue() {
        return appsToBeBlockedOnOverdue;
    }

    public void setAppsToBeBlockedOnOverdue(List<AppsToBeBlockedOnOverdue> appsToBeBlockedOnOverdue) {
        this.appsToBeBlockedOnOverdue = appsToBeBlockedOnOverdue;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}