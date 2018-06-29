package com.mobiledi.earnit.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by praks on 07/07/17.
 */

public class Tasks implements Serializable, Parcelable, Cloneable {
    private int id;
    private int childId;
    private double allowance;
    private long createDate;
    private long dueDate;
    private long startDate;
    private String name;
    private ArrayList taskComments;
    private String details;
    private boolean pictureRequired;
    private String status;
    private long updateDate;

    private Goal goal;

    private RepititionSchedule repititionSchedule;

    private ArrayList<DateTime> datesRepetitions;

    public static long fakeDate = new DateTime().withYear(1980).withTimeAtStartOfDay().getMillis();

    public boolean hasFewApprovalTasks() {
        return repititionSchedule != null && repititionSchedule.getDayTaskStatuses() != null &&
                repititionSchedule.getDayTaskStatuses().size() > 1 && fewDayTaskStatusesIsComplete();
    }

    private boolean fewDayTaskStatusesIsComplete() {
        int i = 0;
        for (DayTaskStatus dayTaskStatus : repititionSchedule.getDayTaskStatuses()) {
            if (dayTaskStatus.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                i++;
        }
        return i > 1;
    }

    public int getWeekAsInt(String dayOfTheWeek){
        int week = -1;
        if (getRepititionSchedule() != null && !getRepititionSchedule().monthlyRepeatHasNumbers()){
            switch (dayOfTheWeek) {
                case "Sunday":
                    week = DateTimeConstants.SUNDAY;
                    break;
                case "sunday":
                    week = DateTimeConstants.SUNDAY;
                    break;
                case "monday":
                    week = DateTimeConstants.MONDAY;
                    break;
                case "tuesday":
                    week = DateTimeConstants.TUESDAY;
                    break;
                case "wednesday":
                    week = DateTimeConstants.WEDNESDAY;
                    break;
                case "thursday":
                    week = DateTimeConstants.THURSDAY;
                    break;
                case "friday":
                    week = DateTimeConstants.FRIDAY;
                    break;
                case "saturday":
                    week = DateTimeConstants.SATURDAY;
                    break;
                default:
                    week = 1;
            }
        }
        return week;
    }

    public int getPerformTaskOnTheNSpecifiedDay(){
        int performTaskOnTheNSpecifiedDay = -1;
        if (getRepititionSchedule() != null && !getRepititionSchedule().monthlyRepeatHasNumbers()) {
            switch (getRepititionSchedule().getPerformTaskOnTheNSpecifiedDay()) {
                case "First":
                    performTaskOnTheNSpecifiedDay = 1;
                    break;
                case "Second":
                    performTaskOnTheNSpecifiedDay = 2;
                    break;
                case "Third":
                    performTaskOnTheNSpecifiedDay = 3;
                    break;
                case "Fourth":
                    performTaskOnTheNSpecifiedDay = 4;
                    break;
                case "Fifth":
                    performTaskOnTheNSpecifiedDay = 5;
                    break;
                case "Last":
                    performTaskOnTheNSpecifiedDay = -1;
                    break;
                default:
                    performTaskOnTheNSpecifiedDay = 1;
            }
        }
        return performTaskOnTheNSpecifiedDay;
    }

    public Tasks getApprovalTask() {
        Tasks newTask = null;
        if (repititionSchedule == null || repititionSchedule.getDayTaskStatuses() == null ||
                repititionSchedule.getDayTaskStatuses().size() == 0) {
            return status.equalsIgnoreCase(AppConstant.COMPLETED) ? this : null;
        } else {
            if (hasFewApprovalTasks()) {
                return null;
            } else {
                List<DayTaskStatus> dayTaskStatuses = getRepititionSchedule().getDayTaskStatuses();
                for (DayTaskStatus dayTaskStatus : dayTaskStatuses) {
                    if (dayTaskStatus.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                        newTask = Tasks.from(this);
                        newTask.setStartDate(newTask.getDueDate());
                        newTask.setStatus(dayTaskStatus.getStatus());
                    }
                }
            }
        }
        return newTask;
    }

    public Tasks() {
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

    public boolean isCompleted() {
        return status.equals(AppConstant.COMPLETED);
    }

    public boolean isApproved() {
        return status.equalsIgnoreCase(AppConstant.APPROVED);
    }

    public ArrayList<TaskComment> getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(ArrayList<TaskComment> taskComments) {
        this.taskComments = taskComments;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getFakeDate() {
        if (status.equalsIgnoreCase(AppConstant.COMPLETED)) {
            return fakeDate;
        } else {
            return dueDate;
        }
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
                /*", allowance=" + allowance +*/
                ", createDate=" + new DateTime(createDate).toString("dd.MM.yyyy HH:mm:ss") +
                ", dueDate=" + new DateTime(dueDate).toString("dd.MM.yyyy HH:mm:ss") +
                ", startDate=" + new DateTime(startDate).toString("dd.MM.yyyy HH:mm:ss") +
                ", name='" + name + '\'' +
                /*", taskComments=" + taskComments +
                ", details='" + details + '\'' +
                ", pictureRequired=" + pictureRequired +*/
                ", status='" + status + '\'' +
                ", updateDate=" + new DateTime(updateDate).toString("dd.MM.yyyy HH:mm:ss") +
                /*", goal=" + goal +*/
                ", repititionSchedule=" + repititionSchedule +
                /*", datesRepetitions=" + datesRepetitions +
                ", datenew='" + datenew + '\'' +*/
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
        parcel.writeLong(startDate);
        parcel.writeString(name);
        parcel.writeList(taskComments);
        parcel.writeString(details);
        parcel.writeByte((byte) (pictureRequired ? 1 : 0));
        parcel.writeString(status);
        parcel.writeLong(updateDate);
        parcel.writeString(datenew);
    }

    protected Tasks(Parcel in) {
        id = in.readInt();
        childId = in.readInt();
        allowance = in.readDouble();
        createDate = in.readLong();
        dueDate = in.readLong();
        startDate = in.readLong();
        name = in.readString();
        taskComments = in.readArrayList(TaskComment.class.getClassLoader());
        details = in.readString();
        pictureRequired = in.readByte() != 0;
        status = in.readString();
        updateDate = in.readLong();
        datenew = in.readString();
    }

    public boolean datesEquals(DateTime dueDate) {
        DateTime thisDate = new DateTime(getDueDate());
        return thisDate.getDayOfMonth() == dueDate.getDayOfMonth() && thisDate.getMonthOfYear() == dueDate.getMonthOfYear() && thisDate.getYear() == dueDate.getYear();
    }

    public boolean containsDateRepitiions(DateTime dateTime) {
        if (repititionSchedule == null) {
            return false;
        }
        for (DateTime date : datesRepetitions) {
            if (date.getDayOfMonth() == dateTime.getDayOfMonth() && date.getMonthOfYear() == dateTime.getMonthOfYear() && date.getYear() == dateTime.getYear()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<DateTime> getDatesRepetitions() {
        if (repititionSchedule == null) {
            return null;
        }
        ArrayList<DateTime> repetitionsDates = new ArrayList<>();
        if (repititionSchedule.getRepeat().equalsIgnoreCase("daily")) {
            for (int i = 0; i <= AppConstant.DAILY_NUM_REPETITIONS; i++) {
                DateTime repeatDate = new DateTime(dueDate).plusDays(repititionSchedule.getEveryNRepeat() == 0 ? (i + 1) : repititionSchedule.everyNRepeat);
                datesRepetitions.add(repeatDate);
            }
        }
        if (repititionSchedule.getRepeat().equalsIgnoreCase("weekly")) {
            for (int i = 0; i <= AppConstant.WEEKLY_NUM_REPETITIONS; i++) {
                DateTime repeatDate = new DateTime(dueDate).plusWeeks(repititionSchedule.getEveryNRepeat() == 0 ? (i + 1) : repititionSchedule.everyNRepeat);
                datesRepetitions.add(repeatDate);
            }
        }
        if (repititionSchedule.getRepeat().equalsIgnoreCase("monthly")) {

        }
        return repetitionsDates;
    }

    private void setStatusForTask(Tasks task) {
        Log.d("skdjfhjk", "setStatusForTask");
        if (task.getRepititionSchedule() != null && task.getRepititionSchedule().getDayTaskStatuses() != null) {
            DateTime fakeDate = new DateTime(task.getDueDate());
            List<DayTaskStatus> dayTaskStatuses = task.getRepititionSchedule().getDayTaskStatuses();
            for (DayTaskStatus dayTaskStatus : dayTaskStatuses) {
                if (!dayTaskStatus.getCreatedDateTime().equalsIgnoreCase("null")) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy HH:mm:ss a").withLocale(Locale.US);
                    DateTime dt = formatter.parseDateTime(dayTaskStatus.getCreatedDateTime());
                    Log.d("askjdlk", "1: " + fakeDate.withTimeAtStartOfDay().toString());
                    Log.d("askjdlk", "2: " + dt.withTimeAtStartOfDay().toString());
                    if (fakeDate.withTimeAtStartOfDay().isEqual(dt.withTimeAtStartOfDay()))
                        if (dayTaskStatus.getStatus().equals(AppConstant.COMPLETED))
                            task.setStatus(AppConstant.COMPLETED);
                        else if (dayTaskStatus.getStatus().equals(AppConstant.APPROVED))
                            task.setStatus(AppConstant.APPROVED);
                    Log.d("skdjfhjk", "task: " + task.toString());
                }
            }
        }
    }

    public static Tasks from(Tasks task) {
        Tasks newTask = new Tasks();
        newTask.setDueDate(task.getDueDate());
        newTask.setStartDate(task.getStartDate());
        newTask.setAllowance(task.getAllowance());
        newTask.setChildId(task.getChildId());
        newTask.setCreateDate(task.getCreateDate());
        newTask.setDatenew(task.getDatenew());
        newTask.setDetails(task.getDetails());
        newTask.setGoal(task.getGoal());
        newTask.setId(task.getId());
        newTask.setName(task.getName());
        newTask.setPictureRequired(task.getPictureRequired());
        newTask.setRepititionSchedule(task.getRepititionSchedule());
        newTask.setStatus(task.getStatus());
        newTask.setTaskComments(task.getTaskComments());
        newTask.setUpdateDate(task.getUpdateDate());
        return newTask;
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
