package com.firepitmedia.earnit.model.getChild;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.firepitmedia.earnit.model.goal.Adjustment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by mac on 13/03/18.
 */

public class Goal {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("createDate")
    @Expose
    private String createDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("updateDate")
    @Expose
    private String updateDate;
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
    private List<Adjustment> adjustments = null;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
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

    public List<Adjustment> getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(List<Adjustment> adjustments) {
        this.adjustments = adjustments;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Goal from(com.firepitmedia.earnit.model.Goal goal){
        if  (goal != null) {
            DateTimeFormatter format = DateTimeFormat.forPattern("MMM dd, yyyy hh:mm:ss a");
            setId(goal.getId());
            setAmount(goal.getAmount());

            DateTime dueDate = new DateTime(goal.getCreateDate());
            setCreateDate(format.print(dueDate));

            setName(goal.getGoalName());

            DateTime updateDate = new DateTime(goal.getUpdateDate());
            setUpdateDate(format.print(updateDate));

            setTally(goal.getTally());
            setTallyPercent((int) goal.getTallyPercent());
            setCash(goal.getCash());
            setAdjustments(goal.getAdjustments());
        }
        return this;
    }
}
