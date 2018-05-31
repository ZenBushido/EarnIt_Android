package com.mobiledi.earnit.utils;

import android.util.Log;

import com.mobiledi.earnit.model.Account;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.DayTaskStatus;
import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.RepSchedule;
import com.mobiledi.earnit.model.RepititionSchedule;
import com.mobiledi.earnit.model.TaskComment;
import com.mobiledi.earnit.model.Tasks;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Created by mobile-di on 26/8/17.
 */

public class GetObjectFromResponse {
    public static final String TAG = "GetObjectFromResponse";
    public static String PENDING_APPROVAL_DATE;
    public static String PAST_DUE_DATE;
    private long dueLongDate;
    private long createdLongDate;

    public Parent getParentObject(JSONObject response) {

        Parent parent = new Parent();
        try {
            parent.setId(response.getInt(AppConstant.ID));
            parent.setAvatar(response.getString(AppConstant.AVATAR));
            parent.setFirstName(response.getString(AppConstant.FIRST_NAME));
            parent.setLastName(response.getString(AppConstant.LAST_NAME));
            parent.setEmail(response.getString(AppConstant.EMAIL));
            parent.setPassword(response.getString(AppConstant.PASSWORD));
            parent.setUserType(response.getString(AppConstant.TYPE));
            parent.setPhone(response.getString(AppConstant.PHONE));
            parent.setFcmToken(response.getString(AppConstant.FCM_TOKEN));


            JSONObject account = response.getJSONObject(AppConstant.ACCOUNT);
            Account parentAcount = getAccountObject(account);
            parent.setAccount(parentAcount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parent;
    }

    public Child getChildObject(JSONObject response) {
        Child child = new Child();
        try {
            child.setId(response.getInt(AppConstant.ID));
            child.setAvatar(response.getString(AppConstant.AVATAR));
            child.setFirstName(response.getString(AppConstant.FIRST_NAME));
            child.setLastName(response.getString(AppConstant.LAST_NAME));
            child.setEmail(response.getString(AppConstant.EMAIL));
            child.setPassword(response.getString(AppConstant.PASSWORD));
            child.setFcmToken(response.getString(AppConstant.FCM_TOKEN));
            child.setMessage(response.getString(AppConstant.MESSAGE));
            child.setPhone(response.getString(AppConstant.PHONE));
            child.setUserType(response.getString(AppConstant.TYPE));
            JSONObject account = response.getJSONObject(AppConstant.ACCOUNT);
            Account childAccount = getAccountObject(account);
            child.setAccount(childAccount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return child;
    }

    public Tasks getTaskObject(JSONObject taskObject, int childId) {
        Tasks task = new Tasks();
        try {
            try {
                if (taskObject.has(AppConstant.REPITITION_SCHEDULE) && taskObject.getJSONObject(AppConstant.REPITITION_SCHEDULE) != null) {
                    JSONObject scheduleObject = taskObject.getJSONObject(AppConstant.REPITITION_SCHEDULE);
                    RepititionSchedule schedule = getRepititionSchedule(scheduleObject);
                    if (taskObject.getJSONObject(AppConstant.REPITITION_SCHEDULE).has(AppConstant.DAY_TASK_STATUSES)) {
                        List<DayTaskStatus> dayTaskStatusList = new ArrayList<>();
                        JSONArray dayTaskStatuses = scheduleObject.getJSONArray(AppConstant.DAY_TASK_STATUSES);
                        for (int i = 0; i < dayTaskStatuses.length(); i++) {
                            JSONObject dayTaskStatus = dayTaskStatuses.getJSONObject(i);
                            DayTaskStatus dayStatus = new DayTaskStatus();
                            dayStatus.setCreatedDateTime(dayTaskStatus.getString("createdDateTime"));
                            dayStatus.setStatus(dayTaskStatus.getString("status"));
                            dayStatus.setId(dayTaskStatus.getInt("id"));
                            dayTaskStatusList.add(dayStatus);
                        }
                        schedule.setDayTaskStatuses(dayTaskStatusList);
                    }
                    task.setRepititionSchedule(schedule);
                }
            } catch (JSONException ignored) {
            }

            task.setId(taskObject.getInt(AppConstant.ID));
            task.setChildId(childId);
            task.setName(taskObject.getString(AppConstant.NAME));

            DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy HH:mm:ss a").withLocale(Locale.US);

            task.setCreateDate(formatter.parseDateTime(taskObject.getString(AppConstant.CREATE_DATE)).getMillis());
            task.setDueDate(formatter.parseDateTime(taskObject.getString(AppConstant.DUE_DATE)).getMillis());

            task.setStatus(taskObject.getString(AppConstant.STATUS));
            task.setAllowance(taskObject.getDouble(AppConstant.ALLOWANCE));
            task.setDetails(taskObject.getString(AppConstant.DESCRIPTION));
            if (taskObject.getBoolean(AppConstant.PICTURE_REQUIRED))
                task.setPictureRequired(taskObject.getBoolean(AppConstant.PICTURE_REQUIRED));
            else
                task.setPictureRequired(false);

            if (taskObject.has(AppConstant.GOAL)) {

                if (!taskObject.isNull(AppConstant.GOAL)) {

                    JSONObject goalObject = taskObject.getJSONObject(AppConstant.GOAL);
                    Goal goal = getGoalObject(goalObject);
                    task.setGoal(goal);
                }

            }

            JSONArray taskCommentArray = taskObject.getJSONArray(AppConstant.TASK_COMMENTS);
            ArrayList<TaskComment> comments = new ArrayList<TaskComment>();
            if (taskCommentArray != null && taskCommentArray.length() > 0) {
                for (int j = 0; j < taskCommentArray.length(); j++) {
                    JSONObject singleComment = taskCommentArray.getJSONObject(j);
                    TaskComment comment = getCommentObject(singleComment);
                    comments.add(comment);
                }
            } else {
                Utils.logDebug(TAG, "Task comment not available");
            }
            task.setTaskComments(comments);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Utils.logDebug(TAG, "getTaskObject: " + task.toString());
        return task;
    }

    public Goal getGoalObject(JSONObject goalObject) {
        Goal goal = new Goal();
        try {
            if (goalObject.get(AppConstant.ID) instanceof Integer)
                goal.setId(goalObject.getInt(AppConstant.ID));
            else
                goal.setId(0);
            goal.setAmount(goalObject.getInt(AppConstant.AMOUNT));
            goal.setGoalName(goalObject.getString(AppConstant.GOAL_NAME));
            goal.setTally(goalObject.getInt(AppConstant.TALLY));
            goal.setTallyPercent(Float.parseFloat(String.valueOf(goalObject.getDouble(AppConstant.TALLY_PERCENT))));
            goal.setCash(goalObject.getInt(AppConstant.CASH_BALANCE));
//            goal.setCreateDate(goalObject.getLong(AppConstant.CREATE_DATE));
//            goal.setUpdateDate(goalObject.getLong(AppConstant.UPDATE_DATE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return goal;
    }

    public RepititionSchedule getRepititionSchedule(JSONObject scheduleObject) {
        RepititionSchedule schedule = new RepititionSchedule();
        try {
            schedule.setId(scheduleObject.getInt(AppConstant.ID));
            schedule.setRepeat(scheduleObject.getString(AppConstant.REPEAT));
            schedule.setEndTime(scheduleObject.getString(AppConstant.END_TIME));
            schedule.setStartTime(scheduleObject.getString(AppConstant.START_TIME));
            schedule.setEveryNRepeat(scheduleObject.getInt("everyNRepeat"));
            List<DayTaskStatus> dayTaskStatuses = new ArrayList<>();
            for (int i = 0; i < scheduleObject.getJSONArray(AppConstant.DAY_TASK_STATUSES).length(); i++) {
                JSONObject dayTaskStatusObject = (JSONObject) scheduleObject.getJSONArray(AppConstant.DAY_TASK_STATUSES).get(i);
                DayTaskStatus dayTaskStatus = new DayTaskStatus();
                dayTaskStatus.setCreatedDateTime(dayTaskStatusObject.getString("createdDateTime"));
                dayTaskStatus.setStatus(dayTaskStatusObject.getString("status"));
                dayTaskStatus.setId(dayTaskStatusObject.getInt("id"));
                dayTaskStatuses.add(dayTaskStatus);
            }
            schedule.setDayTaskStatuses(dayTaskStatuses);
            JSONArray array = scheduleObject.getJSONArray("specificDays");
            List<String> daysSpec = new ArrayList<>();
            for (int i = 0; i < array.length(); i++)
                daysSpec.add(array.getString(i));

            schedule.setSpecificDays(daysSpec);

//            schedule.setExpiryDate(scheduleObject.getLong(AppConstant.EXPIRY_DATE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return schedule;
    }

    public Account getAccountObject(JSONObject account) {
        Account childAccount = new Account();
        try {

            childAccount.setId(account.getInt(AppConstant.ID));
            childAccount.setAccountCode(account.getString(AppConstant.ACCOUNT_CODE));
            //Feb 8, 2018 10:10:50 AM
            // MMM d, yyyy hh:mm:ss a
            //TODO Date from server @dateCreate(or createDate) won't formatted
//            DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM d, yyyy hh:mm:ss a");
//            Date date = DateTime.parse(account.getString(AppConstant.CREATE_DATE), DateTimeFormat.forPattern("MMM d, yyyy hh:mm:ss a").withLocale(Locale.)).toDate();
//            DateTime dt = new DateTime(date);
            long milliseconds = DateTime.now().getMillis();
            childAccount.setCreateDate(milliseconds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return childAccount;
    }

    public RepSchedule getRepObject(JSONObject account) {
        RepSchedule childAccount = new RepSchedule();
        try {
            childAccount.setStartTime(account.getString("startTime"));
            childAccount.setEndTime(account.getString("endTime"));
            childAccount.setRepeat(account.getString("repeat"));
            childAccount.setEveryNRepeat(account.getInt("everyNRepeat"));
            JSONArray taskArray = account.getJSONArray("specificDays");
            ArrayList<String> reps = new ArrayList<>();
            for (int taskIndex = 0; taskIndex < taskArray.length(); taskIndex++) {
                String taskObject = taskArray.getString(taskIndex);
                reps.add(taskObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return childAccount;
    }

    public TaskComment getCommentObject(JSONObject singleComment) {
        TaskComment comment = new TaskComment();
        try {
            comment.setId(singleComment.getInt(AppConstant.ID));
            comment.setComment(singleComment.getString(AppConstant.COMMENT));
            comment.setPictureUrl(singleComment.getString(AppConstant.PICTURE_URL));
            comment.setReadStatus(singleComment.getInt(AppConstant.READ_STATUS));
//            comment.setCreateDate(singleComment.getLong(AppConstant.CREATE_DATE));
//            comment.setUpdateDate(singleComment.getLong(AppConstant.UPDATE_DATE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comment;
    }

    ArrayList<ChildsTaskObject> childsTaskObjectList = new ArrayList<>();

    Map<String, ArrayList<Tasks>> map = new TreeMap<>();

//    private List<Tasks> getCompletedDays(Tasks task){
//        List<Tasks> tasks = new ArrayList<>();
//        if (task.getRepititionSchedule() != null && task.getRepititionSchedule().getDayTaskStatuses() != null){
//            List<DayTaskStatus> dayTaskStatuses = task.getRepititionSchedule().getDayTaskStatuses();
//            for (DayTaskStatus dayTaskStatus : dayTaskStatuses){
//                if (!thisDayIsComplete(task, fakeDate))
//            }
//        }
//        return tasks;
//    }


    public ArrayList<ChildsTaskObject> getChildTaskListObject(Child childObject, String type, String fromScreen) {
        Log.e(TAG, "Child ID= " + childObject.getId());
        //Get Unique Dates
        PENDING_APPROVAL_DATE = new DateTime().withYear(1980).withTimeAtStartOfDay().toString();
        PAST_DUE_DATE = new DateTime().plusDays(-1).withTimeAtStartOfDay().toString();
        for (Tasks task : childObject.getTasksArrayList()) {
            if (task.getRepititionSchedule() == null && task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                addToMap(task, new DateTime(task.getFakeDate()).toString());
            else {
                if (task.getRepititionSchedule() != null) {
                    if (Objects.equals(task.getRepititionSchedule().getRepeat(), "monthly")) {
                        Utils.logDebug(TAG, "MONTHLY TASK: " + task.toString());
                        DateTime startDate = new DateTime(task.getDueDate());
                        DateTime endDate = new DateTime().plusMonths(AppConstant.MONTHLY_NUM_REPETITIONS);
                        int monthsInterval = Months.monthsBetween(startDate, endDate).getMonths();
                        for (int j = 0; j < monthsInterval; j++) {
                            int plusMonts = j * task.getRepititionSchedule().getEveryNRepeat();
                            if (task.getRepititionSchedule().monthlyRepeatHasNumbers()) {
                                for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                    Tasks newTask = Tasks.from(task);
                                    int day = Integer.parseInt(task.getRepititionSchedule().getSpecificDays().get(i));
                                    newTask.setStartDate(newTask.getDueDate());
                                    DateTime fakeDate = new DateTime(newTask.getStartDate()).withDayOfMonth(day).plusMonths(plusMonts);
                                    Log.d("monthDSKASDK", "plusDays(" + j + " * " + task.getRepititionSchedule().getEveryNRepeat() + ")");
                                    Log.d("monthDSKASDK", "fakeDate: " + fakeDate.toString("dd.MM.yyyy HH:mm"));
                                    newTask.setDueDate(fakeDate.getMillis());
                                    setStatusForTask(newTask);
                                    Log.d("monthDSKASDK", "newTask = " + newTask);
                                    if (!new DateTime(newTask.getDueDate()).withDayOfMonth(day).plusMonths(j * newTask.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))
                                        if (!newTask.isApproved()) {
                                            addToMap(newTask, new DateTime(newTask.getFakeDate()).toString());
//                                        if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
//                                            addToMap(newTask, new DateTime(newTask.getFakeDate()).toString());
//                                        addToMap(newTask, key);
                                        }
                                }
                            } else {
                                for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                    Tasks newTask = Tasks.from(task);

                                    Integer month = 1;

                                    switch (task.getRepititionSchedule().getSpecificDays().get(i)) {
                                        case "Sunday":
                                            month = 7;
                                            break;
                                        case "sunday":
                                            month = 7;
                                            break;
                                        case "monday":
                                            month = 1;
                                            break;
                                        case "tuesday":
                                            month = 2;
                                            break;
                                        case "wednesday":
                                            month = 3;
                                            break;
                                        case "thursday":
                                            month = 4;
                                            break;
                                        case "friday":
                                            month = 5;
                                            break;
                                        case "saturday":
                                            month = 6;
                                            break;
                                        default:
                                            month = 1;
                                    }
                                    newTask.setStartDate(newTask.getDueDate());
                                    DateTime fakeDate = new DateTime(newTask.getStartDate()).withDayOfWeek(month).plusWeeks(j * month);
                                    Log.d("weekDSKASDK", "plusDays(" + j + " * " + month + ")");
                                    Log.d("weekDSKASDK", "fakeDate: " + fakeDate.toString("dd.MM.yyyy HH:mm"));
                                    newTask.setDueDate(fakeDate.getMillis());
                                    setStatusForTask(newTask);
                                    Log.d("weekDSKASDK", "newTask = " + newTask);
                                    if (!new DateTime(newTask.getDueDate()).withDayOfWeek(month).plusWeeks(j * newTask.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))
                                        if (!newTask.isApproved()) {
                                            addToMap(newTask, new DateTime(newTask.getFakeDate()).toString());
//                                        if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
//                                            addToMap(newTask, new DateTime(newTask.getFakeDate()).toString());
//                                        addToMap(newTask, key);
                                        }

                                }

//                                Log.e(TAG, "Month= " + month);
//                                String key1 = new DateTime(task.getDueDate()).withDayOfMonth(month).plusMonths(j * task.getRepititionSchedule().getEveryNRepeat()).withTimeAtStartOfDay().toString();
//                                DateTime fakeDate1 = new DateTime(task.getDueDate()).plusDays(i * month).withTimeAtStartOfDay();
//                                setStatusForTask(task, fakeDate1);
//                                if (!new DateTime(task.getDueDate()).withDayOfMonth(month).plusMonths(j * task.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))
//                                    if (!task.getStatus().equals(AppConstant.APPROVED)) {
//                                        if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
//                                            addToMap(task, new DateTime(task.getFakeDate()).toString());
//                                        addToMap(task, key1);
//                                    }
                            }
                        }
                    } else if (Objects.equals(task.getRepititionSchedule().getRepeat(), "weekly")) {
                        DateTime startDate = new DateTime(task.getDueDate());
                        DateTime endDate = new DateTime().plusWeeks(AppConstant.WEEKLY_NUM_REPETITIONS);
                        int weeksInterval = Weeks.weeksBetween(startDate, endDate).getWeeks();
                        for (int j = 0; j < weeksInterval; j++) {
                            for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                Tasks newTask = Tasks.from(task);
                                int month;

                                switch (task.getRepititionSchedule().getSpecificDays().get(i)) {
                                    case "Sunday":
                                        month = 6;
                                        break;
                                    case "monday":
                                        month = 1;
                                        break;
                                    case "tuesday":
                                        month = 2;
                                        break;
                                    case "wednesday":
                                        month = 3;
                                        break;
                                    case "thursday":
                                        month = 4;
                                        break;
                                    case "friday":
                                        month = 5;
                                        break;
                                    case "saturday":
                                        month = 6;
                                        break;
                                    default:
                                        month = 1;
                                }
                                newTask.setStartDate(newTask.getDueDate());
                                DateTime fakeDate = new DateTime(newTask.getStartDate()).withDayOfWeek(month).plusWeeks(j * month);
                                Log.d("weekDSKASDK", "plusDays(" + j + " * " + month + ")");
                                Log.d("weekDSKASDK", "fakeDate: " + fakeDate.toString("dd.MM.yyyy HH:mm"));
                                newTask.setDueDate(fakeDate.getMillis());
                                setStatusForTask(newTask);
                                Log.d("weekDSKASDK", "newTask = " + newTask);
                                if (!new DateTime(newTask.getDueDate()).withDayOfWeek(month).plusWeeks(j * newTask.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime(task.getStartDate())))
                                    if (!newTask.isApproved()) {
                                        addToMap(newTask, new DateTime(newTask.getFakeDate()).toString());
//                                        if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
//                                            addToMap(newTask, new DateTime(newTask.getFakeDate()).toString());
//                                        addToMap(newTask, key);
                                    }
                            }
                        }
                    } else if (Objects.equals(task.getRepititionSchedule().getRepeat(), "daily")) {
                        DateTime startDate = new DateTime(task.getDueDate());
                        DateTime endDate = new DateTime().plusDays(AppConstant.DAILY_NUM_REPETITIONS);
                        int daysInterval = Days.daysBetween(startDate, endDate).getDays();
                        Log.d("skdjfhjk", "daily");
                        for (int i = 0; i < daysInterval; i++) {
                            Tasks newTask = Tasks.from(task);
                            newTask.setStartDate(newTask.getDueDate());
                            Integer repeat = newTask.getRepititionSchedule().getEveryNRepeat();
                            DateTime fakeDate = new DateTime(newTask.getStartDate()).plusDays(i * repeat);
                            Log.d("skdjfhjkd", "newTask before = " + newTask);
                            newTask.setDueDate(fakeDate.getMillis());
                            setStatusForTask(newTask);
                            Log.d("skdjfhjks", "newTask after = " + newTask);
                            if (!newTask.isApproved()) {
                                addToMap(newTask, new DateTime(newTask.getFakeDate()).toString());
                            }
                        }
                    } else {
                        String key = new DateTime(task.getDueDate()).withTimeAtStartOfDay().toString();

                        if (!task.getStatus().equals(AppConstant.APPROVED)) {
                            if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                                addToMap(task, PENDING_APPROVAL_DATE);
                            addToMap(task, key);
                        }
                    }
                } else {
                    String key = new DateTime(task.getDueDate()).withTimeAtStartOfDay().toString();

                    if (!task.getStatus().equals(AppConstant.APPROVED)) {
                        if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                            addToMap(task, PENDING_APPROVAL_DATE);
                        addToMap(task, key);
                    }
                }
            }
        }

        for (String key : map.keySet()) {
            childsTaskObjectList.add(new ChildsTaskObject(key, map.get(key)));
        }
        return childsTaskObjectList;
    }

    @Deprecated
    private void setStatusForTask(Tasks task, DateTime fakeDate) {
        if (task.getRepititionSchedule() != null && task.getRepititionSchedule().getDayTaskStatuses() != null) {
            List<DayTaskStatus> dayTaskStatuses = task.getRepititionSchedule().getDayTaskStatuses();
            for (DayTaskStatus dayTaskStatus : dayTaskStatuses) {
                if (!dayTaskStatus.getCreatedDateTime().equalsIgnoreCase("null")) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy HH:mm:ss a").withLocale(Locale.US);
                    DateTime dt = formatter.parseDateTime(dayTaskStatus.getCreatedDateTime());
//                    Log.d("askjdlk", "1: " + fakeDate.withTimeAtStartOfDay().toString());
//                    Log.d("askjdlk", "2: " + dt.withTimeAtStartOfDay().toString());
                    if (fakeDate.withTimeAtStartOfDay().isEqual(dt.withTimeAtStartOfDay()))
                        if (dayTaskStatus.getStatus().equals(AppConstant.COMPLETED))
                            task.setStatus(AppConstant.COMPLETED);
                        else if (dayTaskStatus.getStatus().equals(AppConstant.APPROVED))
                            task.setStatus(AppConstant.APPROVED);
                }
            }
        }
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

    private void addToMap(Tasks task, String key) {
        Log.d("dkfjhsdkfjh", "addToMap = " + task.toString());
        if (!key.equals(PENDING_APPROVAL_DATE) && task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
            Log.d("dkfjhsdkfjh", "completed (((((((((((( + " + new DateTime(task.getDueDate()));
            return;
        }

        if (!map.containsKey(key))
            map.put(key, new ArrayList<Tasks>());
        map.get(key).add(task);
    }

    public void getObjectList(Tasks task) {
        boolean exists = false;
        for (ChildsTaskObject childsTaskObject : childsTaskObjectList) {
            if (new DateTime().withTimeAtStartOfDay().isAfter(new DateTime(task.getDueDate()).withTimeAtStartOfDay())) {
                ArrayList<Tasks> taskList = childsTaskObject.getTasks();
                taskList.add(task);
                childsTaskObject.setTasks(taskList);
                exists = true;
                break;
            } else if (new DateTime(task.getDueDate()).withTimeAtStartOfDay()
                    .equals(new DateTime(childsTaskObject.getDueDate()).withTimeAtStartOfDay()))

            {
                ArrayList<Tasks> taskList = childsTaskObject.getTasks();
                taskList.add(task);
                childsTaskObject.setTasks(taskList);
                exists = true;
                break;

            }
        }
        if (!exists) {
            ChildsTaskObject childsTaskObject = new ChildsTaskObject();
            childsTaskObject.setDueDate(new DateTime(task.getDueDate()).withTimeAtStartOfDay().toString());
            ArrayList<Tasks> taskList = new ArrayList<>();
            taskList.add(task);
            childsTaskObject.setTasks(taskList);
            childsTaskObjectList.add(childsTaskObject);
        }
    }
}
