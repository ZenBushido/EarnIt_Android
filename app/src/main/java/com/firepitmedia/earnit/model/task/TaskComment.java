package com.firepitmedia.earnit.model.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 14/03/18.
 */

public class TaskComment {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("createDate")
    @Expose
    private String createDate;
    @SerializedName("pictureUrl")
    @Expose
    private Object pictureUrl;
    @SerializedName("readStatus")
    @Expose
    private Integer readStatus;
    @SerializedName("updateDate")
    @Expose
    private String updateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Object getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(Object pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
