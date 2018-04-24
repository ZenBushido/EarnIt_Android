package com.mobiledi.earnit.model.getChild;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mobiledi.earnit.model.Tasks;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 13/03/18.
 */

public class Task implements Serializable, Parcelable {

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
    private List<Object> taskComments = null;
    @SerializedName("goal")
    @Expose
    private Goal goal;
    @SerializedName("repititionSchedule")
    @Expose
    private RepititionSchedule repititionSchedule;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("shouldLockAppsIfTaskOverdue")
    @Expose
    private Boolean shouldLockAppsIfTaskOverdue;
    @SerializedName("deleted")
    @Expose
    private Boolean deleted;

    public Task(){

    }

    public Task(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            allowance = null;
        } else {
            allowance = in.readInt();
        }
        createDate = in.readString();
        dueDate = in.readString();
        name = in.readString();
        byte tmpPictureRequired = in.readByte();
        pictureRequired = tmpPictureRequired == 0 ? null : tmpPictureRequired == 1;
        status = in.readString();
        updateDate = in.readString();
        description = in.readString();
        byte tmpShouldLockAppsIfTaskOverdue = in.readByte();
        shouldLockAppsIfTaskOverdue = tmpShouldLockAppsIfTaskOverdue == 0 ? null : tmpShouldLockAppsIfTaskOverdue == 1;
        byte tmpDeleted = in.readByte();
        deleted = tmpDeleted == 0 ? null : tmpDeleted == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (allowance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(allowance);
        }
        dest.writeString(createDate);
        dest.writeString(dueDate);
        dest.writeString(name);
        dest.writeByte((byte) (pictureRequired == null ? 0 : pictureRequired ? 1 : 2));
        dest.writeString(status);
        dest.writeString(updateDate);
        dest.writeString(description);
        dest.writeByte((byte) (shouldLockAppsIfTaskOverdue == null ? 0 : shouldLockAppsIfTaskOverdue ? 1 : 2));
        dest.writeByte((byte) (deleted == null ? 0 : deleted ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

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

    public List<Object> getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(List<Object> taskComments) {
        this.taskComments = taskComments;
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

    public Boolean getShouldLockAppsIfTaskOverdue() {
        return shouldLockAppsIfTaskOverdue;
    }

    public void setShouldLockAppsIfTaskOverdue(Boolean shouldLockAppsIfTaskOverdue) {
        this.shouldLockAppsIfTaskOverdue = shouldLockAppsIfTaskOverdue;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isRepeat() {
        return repititionSchedule != null;
    }

    public boolean isLastTask(){
        boolean notRepeat = repititionSchedule == null;
        boolean lastTask = true;
        if (repititionSchedule != null){
            DateTime dueDate = new DateTime(getDueDate());
            DateTime endTime = DateTime.parse(repititionSchedule.getDayTaskStatuses().get(repititionSchedule.getDayTaskStatuses().size()).getCreatedDateTime(), DateTimeFormat.forPattern("hh:mm:ss a"));
            lastTask = dueDate.getDayOfMonth() == endTime.getDayOfMonth() && dueDate.getMonthOfYear() == endTime.getMonthOfYear() &&  dueDate.getYear() == endTime.getYear();
        }
        return lastTask && notRepeat;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", allowance=" + allowance +
                ", createDate='" + createDate + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", name='" + name + '\'' +
                ", pictureRequired=" + pictureRequired +
                ", status='" + status + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", taskComments=" + taskComments +
                ", goal=" + goal +
                ", repititionSchedule=" + repititionSchedule +
                ", description='" + description + '\'' +
                ", shouldLockAppsIfTaskOverdue=" + shouldLockAppsIfTaskOverdue +
                ", deleted=" + deleted +
                '}';
    }

    public Task from(Tasks h) {
        DateTimeFormatter format = DateTimeFormat.forPattern("MMM dd, yyyy hh:mm:ss a");

        setId(h.getId());

        setAllowance((int) h.getAllowance());

        DateTime createDate = new DateTime(h.getCreateDate());

        setCreateDate(format.print(createDate));

        DateTime dueDate = new DateTime(h.getCreateDate());
        setDueDate(format.print(dueDate));

        setName(h.getName());

        setPictureRequired(h.getPictureRequired());

        setStatus(h.getStatus());

        DateTime updateDate = new DateTime(h.getCreateDate());
        setDueDate(format.print(updateDate));

        if (h.getTaskComments() != null) {
            List<Object> objects = new ArrayList<>();
            objects.addAll(h.getTaskComments());
            setTaskComments(objects);
        }


        setGoal(new Goal().from(h.getGoal()));

        setRepititionSchedule(new RepititionSchedule().from(h.getRepititionSchedule()));

        setDescription(h.getDetails());

//        setShouldLockAppsIfTaskOverdue(h.get);

//        setDeleted(h.getDe);
        return this;
    }
}
