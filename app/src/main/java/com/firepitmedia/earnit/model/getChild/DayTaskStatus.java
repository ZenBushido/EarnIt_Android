package com.firepitmedia.earnit.model.getChild;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DayTaskStatus {

    @SerializedName("id")
    @Expose
    long id;

    @SerializedName("createdDateTime")
    @Expose
    String createdDateTime;

    @SerializedName("status")
    @Expose
    String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DayTaskStatus{" +
                "id=" + id +
                ", createdDateTime='" + createdDateTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public DayTaskStatus from(com.firepitmedia.earnit.model.DayTaskStatus status){
        setId(Long.parseLong(status.getStatus()));
        setCreatedDateTime(status.getCreatedDateTime());
        setStatus(status.getStatus());
        return this;
    }
}
