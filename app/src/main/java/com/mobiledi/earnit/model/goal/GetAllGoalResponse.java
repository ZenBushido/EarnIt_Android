package com.mobiledi.earnit.model.goal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 09/03/18.
 */

public class GetAllGoalResponse {

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
    private Object updateDate;
    @SerializedName("tally")
    @Expose
    private Integer tally;
    @SerializedName("tallyPercent")
    @Expose
    private Double tallyPercent;
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

    public Double getTallyPercent() {
        return tallyPercent;
    }

    public void setTallyPercent(Double tallyPercent) {
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

    @Override
    public String toString() {
        return "GetAllGoalResponse{" +
                "id=" + id +
                ", amount=" + amount +
                ", createDate='" + createDate + '\'' +
                ", name='" + name + '\'' +
                ", updateDate=" + updateDate +
                ", tally=" + tally +
                ", tallyPercent=" + tallyPercent +
                ", cash=" + cash +
                ", adjustments=" + adjustments +
                ", deleted=" + deleted +
                '}';
    }
}
