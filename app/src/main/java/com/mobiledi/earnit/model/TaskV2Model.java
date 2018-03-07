package com.mobiledi.earnit.model;

import java.util.List;

/**
 * Created by adox on 20.01.2018..
 */


public class TaskV2Model
{
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    int id;

    public double getAllowance() {
        return this.allowance;
    }
    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }
    double allowance;

    public String getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    String createDate;

    public String getDueDate() {
        return this.dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    String dueDate;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    String name;

    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    String status;

    public List<String> getTaskComments() {
        return this.taskComments;
    }
    public void setTaskComments(List<String> taskComments) {
        this.taskComments = taskComments;
    }
    List<String > taskComments;

    public GoalV2Model getGoal() {
        return this.goal;
    }
    public void setGoal(GoalV2Model goal) {
        this.goal = goal;
    }
    GoalV2Model goal;

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    String description;
RepititionSchedule repititionSchedule;

    public RepititionSchedule getRepititionSchedule() {
        return repititionSchedule;
    }

    public void setRepititionSchedule(RepititionSchedule repititionSchedule) {
        this.repititionSchedule = repititionSchedule;
    }

    public boolean isShouldLockAppsIfTaskOverdue() {
        return shouldLockAppsIfTaskOverdue;
    }

    public boolean getShouldLockAppsIfTaskOverdue() {
        return this.shouldLockAppsIfTaskOverdue;
    }
    public void setShouldLockAppsIfTaskOverdue(boolean shouldLockAppsIfTaskOverdue) {
        this.shouldLockAppsIfTaskOverdue = shouldLockAppsIfTaskOverdue;
    }
    boolean shouldLockAppsIfTaskOverdue;}


