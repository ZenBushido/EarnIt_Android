package com.mobiledi.earnit.model;

import java.util.ArrayList;

/**
 * Created by adox on 24.01.2018..
 */

public class TaskWithTag {
    private int id;
    private double allowance;

    private Long createDate;
    private Long dueDate;
    private int childId;
    private String name;

    public boolean pictureRequired;
    private String updateDate;

    Goal goal;
    public int everyNRepeat;
    public String repeat;

    private RepititionSchedule tag;

    public TaskWithTag() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public double getAllowance() {
        return allowance;
    }

    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }


    public void setName(String name) {
        this.name = name;
    }



    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public boolean isPictureRequired() {
        return pictureRequired;
    }

    public void setPictureRequired(boolean pictureRequired) {
        this.pictureRequired = pictureRequired;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public int getEveryNRepeat() {
        return everyNRepeat;
    }

    public void setEveryNRepeat(int everyNRepeat) {
        this.everyNRepeat = everyNRepeat;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public RepititionSchedule getTag() {
        return tag;
    }

    public void setTag(RepititionSchedule tag) {
        this.tag = tag;
    }
}
