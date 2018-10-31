package com.firepitmedia.earnit.model;

import com.firepitmedia.earnit.model.goal.Adjustment;
import com.firepitmedia.earnit.utils.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mradul on 8/1/17.
 */

public class Goal implements Serializable{
    private int id;
    private int amount;
    private int tally;
    private int cash;
    private float tallyPercent;
    private String goal;
    private long createDate;
    private long updateDate;

    private List<Adjustment> adjustments = null;

    public Goal(){}


    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getTally() {
        return tally;
    }

    public void setTally(int tally) {
        this.tally = tally;
    }

    public float getTallyPercent() {
        return tallyPercent;
    }

    public void setTallyPercent(float tallyPercent) {
        this.tallyPercent = tallyPercent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getGoalName() {
        return Utils.checkIsNUll(goal);
    }

    public void setGoalName(String goalName) {
        this.goal = goalName;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public List<Adjustment> getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(List<Adjustment> adjustments) {
        this.adjustments = adjustments;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", amount=" + amount +
                ", cash=" + cash +
                ", goal='" + goal + '\'' +
                ", adjustments=" + adjustments +
                '}';
    }
}
