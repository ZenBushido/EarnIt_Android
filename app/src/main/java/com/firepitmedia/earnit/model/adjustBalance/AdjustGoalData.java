package com.firepitmedia.earnit.model.adjustBalance;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 13/03/18.
 */

public class AdjustGoalData {

    public AdjustGoalData(Double amount, String reason, Goal goal) {
        this.amount = amount;
        this.reason = reason;
        this.goal = goal;
    }

    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("goal")
    @Expose
    private Goal goal;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
