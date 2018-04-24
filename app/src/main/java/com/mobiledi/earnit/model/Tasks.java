package com.mobiledi.earnit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by praks on 07/07/17.
 */

public class Tasks implements Serializable, Parcelable {
    private int id;
    private int childId;
    private double allowance;
    private long createDate;
    private long dueDate;
    private String name;
    private ArrayList<TaskComment> taskComments;
    private String details;
    private boolean pictureRequired;
    private String status;
    private long updateDate;

    private Goal goal;

    private RepititionSchedule repititionSchedule;

    private ArrayList<DateTime> datesRepetitions;


    public Tasks() {
    }


    protected Tasks(Parcel in) {
        id = in.readInt();
        childId = in.readInt();
        allowance = in.readDouble();
        createDate = in.readLong();
        dueDate = in.readLong();
        name = in.readString();
        details = in.readString();
        pictureRequired = in.readByte() != 0;
        status = in.readString();
        updateDate = in.readLong();
        datenew = in.readString();
    }

    public static final Creator<Tasks> CREATOR = new Creator<Tasks>() {
        @Override
        public Tasks createFromParcel(Parcel in) {
            return new Tasks(in);
        }

        @Override
        public Tasks[] newArray(int size) {
            return new Tasks[size];
        }
    };

    public ArrayList<TaskComment> getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(ArrayList<TaskComment> taskComments) {
        this.taskComments = taskComments;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }


    public String getDetails() {
        return Utils.checkIsNUll(details);
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public RepititionSchedule getRepititionSchedule() {
        return repititionSchedule;
    }

    public void setRepititionSchedule(RepititionSchedule repititionSchedule) {
        this.repititionSchedule = repititionSchedule;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAllowance() {
        return allowance;
    }

    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getDueDate() {
        return dueDate;
    }

    public String getDueDateAsString() {
        if (getDueDate() == 0) {
            return null;
        } else {
            DateTime dt = new DateTime(getDueDate());
            DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mm a");
            return fmt.print(dt);
        }
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return Utils.checkIsNUll(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getPictureRequired() {
        return pictureRequired;
    }

    public void setPictureRequired(boolean pictureRequired) {
        this.pictureRequired = pictureRequired;
    }

    public String getStatus() {
        return Utils.checkIsNUll(status);
    }

    String datenew;

    public String getDatenew() {
        return datenew;
    }

    public void setDatenew(String datenew) {
        this.datenew = datenew;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    @Override
    public String toString() {
        return "Tasks{" +
                "id=" + id +
                ", childId=" + childId +
                ", allowance=" + allowance +
                ", createDate=" + new DateTime(createDate).toString() +
                ", dueDate=" + new DateTime(dueDate).toString() +
                ", name='" + name + '\'' +
                ", taskComments=" + taskComments +
                ", goal=" + goal +
                ", repititionSchedule=" + repititionSchedule +
                ", details='" + details + '\'' +
                ", pictureRequired=" + pictureRequired +
                ", status='" + status + '\'' +
                ", updateDate=" + new DateTime(updateDate).toString() +
                ", datenew='" + datenew + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(childId);
        parcel.writeDouble(allowance);
        parcel.writeLong(createDate);
        parcel.writeLong(dueDate);
        parcel.writeString(name);
        parcel.writeString(details);
        parcel.writeByte((byte) (pictureRequired ? 1 : 0));
        parcel.writeString(status);
        parcel.writeLong(updateDate);
        parcel.writeString(datenew);
    }

    public boolean datesEquals(DateTime dueDate){
        DateTime thisDate = new DateTime(getDueDate());
        return thisDate.getDayOfMonth() == dueDate.getDayOfMonth() &&thisDate.getMonthOfYear() == dueDate.getMonthOfYear() && thisDate.getYear() == dueDate.getYear();
    }

    public boolean containsDateRepitiions(DateTime dateTime){
        if (repititionSchedule == null){
            return false;
        }
        for (DateTime date : datesRepetitions){
            if (date.getDayOfMonth() == dateTime.getDayOfMonth() && date.getMonthOfYear() == dateTime.getMonthOfYear() && date.getYear() == dateTime.getYear()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<DateTime> getDatesRepetitions(){
        if (repititionSchedule == null) {
            return null;
        }
        ArrayList<DateTime> repetitionsDates = new ArrayList<>();
        if (repititionSchedule.getRepeat().equalsIgnoreCase("daily")){
            for (int i = 0; i <= AppConstant.DAILY_NUM_REPETITIONS; i++){
                DateTime repeatDate = new DateTime(dueDate).plusDays(repititionSchedule.getEveryNRepeat() == 0 ? (i + 1) : repititionSchedule.everyNRepeat);
                datesRepetitions.add(repeatDate);
            }
        }
        if (repititionSchedule.getRepeat().equalsIgnoreCase("weekly")){
            for (int i = 0; i <= AppConstant.WEEKLY_NUM_REPETITIONS; i++){
                DateTime repeatDate = new DateTime(dueDate).plusWeeks(repititionSchedule.getEveryNRepeat() == 0 ? (i + 1) : repititionSchedule.everyNRepeat);
                datesRepetitions.add(repeatDate);
            }
        }
        if (repititionSchedule.getRepeat().equalsIgnoreCase("monthly")){

        }
        return repetitionsDates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tasks tasks = (Tasks) o;
        return id == tasks.id &&
                childId == tasks.childId &&
                Double.compare(tasks.allowance, allowance) == 0 &&
                createDate == tasks.createDate &&
                dueDate == tasks.dueDate &&
                pictureRequired == tasks.pictureRequired &&
                updateDate == tasks.updateDate &&
                Objects.equals(name, tasks.name) &&
                Objects.equals(taskComments, tasks.taskComments) &&
                Objects.equals(details, tasks.details) &&
                Objects.equals(status, tasks.status) &&
                Objects.equals(goal, tasks.goal) &&
                Objects.equals(repititionSchedule, tasks.repititionSchedule) &&
                Objects.equals(datenew, tasks.datenew);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, childId, allowance, createDate, dueDate, name, taskComments, details, pictureRequired, status, updateDate, goal, repititionSchedule, datenew);
    }
}
