package com.firepitmedia.earnit.utils;

import android.util.Log;

import com.firepitmedia.earnit.model.Account;
import com.firepitmedia.earnit.model.BlockingApp;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.ChildsTaskObject;
import com.firepitmedia.earnit.model.DayTaskStatus;
import com.firepitmedia.earnit.model.Goal;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.RepititionSchedule;
import com.firepitmedia.earnit.model.TaskComment;
import com.firepitmedia.earnit.model.Tasks;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private static DateTime PENDING_APPROVAL_DATE_TIME;
    private static DateTime PAST_DUE_DATE_TIME;

    private ArrayList<ChildsTaskObject> childsTaskObjectList = new ArrayList<>();

    private Map<String, ArrayList<Tasks>> map = new TreeMap<>();

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
            Log.d("sdkfjdsl", "avatar = " + response.getString(AppConstant.AVATAR));
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

            DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy hh:mm:ss aa").withLocale(Locale.US);
            Log.e("RestCall", "Due_Date = " + taskObject.getString(AppConstant.DUE_DATE));
            Log.e("RestCall", "Due_Date Millis= " + formatter.parseDateTime(taskObject.getString(AppConstant.DUE_DATE)));
            Log.e("RestCall", "Due_Date Millis To Date= " + new DateTime(formatter.parseDateTime(taskObject.getString(AppConstant.DUE_DATE))).toString());
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

            task.setShouldLockAppsIfTaskOverdue(taskObject.getBoolean(AppConstant.SHOULD_LOCK_APPS));
            JSONArray appsToBeBlockedObject = taskObject.getJSONArray(AppConstant.APPS_TO_BE_BLOCKED);
            List<BlockingApp> appsToBeBlockedList = new ArrayList<>();
            if (appsToBeBlockedObject != null) {
                for (int j = 0; j < appsToBeBlockedObject.length(); j++) {
                    JSONObject blockingAppObject = appsToBeBlockedObject.getJSONObject(j);
                    BlockingApp blockingApp = new BlockingApp(
                            blockingAppObject.getLong("id"),
                            blockingAppObject.getString("name"),
                            blockingAppObject.getString("createdDate"),
                            blockingAppObject.getBoolean("ignoredByParent"));
                    appsToBeBlockedList.add(blockingApp);
                }
            }
            task.setAppsToBeBlockedOnOverdue(appsToBeBlockedList);

            JSONArray taskCommentArray = taskObject.getJSONArray(AppConstant.TASK_COMMENTS);
            ArrayList<TaskComment> comments = new ArrayList<>();
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
            schedule.setPerformTaskOnTheNSpecifiedDay(scheduleObject.getString("performTaskOnTheNSpecifiedDay"));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return schedule;
    }

    private Account getAccountObject(JSONObject account) {
        Account childAccount = new Account();
        try {

            childAccount.setId(account.getInt(AppConstant.ID));
            childAccount.setAccountCode(account.getString(AppConstant.ACCOUNT_CODE));

            long milliseconds = DateTime.now().getMillis();
            childAccount.setCreateDate(milliseconds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return childAccount;
    }

    private TaskComment getCommentObject(JSONObject singleComment) {
        TaskComment comment = new TaskComment();
        try {
            comment.setId(singleComment.getInt(AppConstant.ID));
            comment.setComment(singleComment.getString(AppConstant.COMMENT));
            comment.setPictureUrl(singleComment.getString(AppConstant.PICTURE_URL));
            comment.setReadStatus(singleComment.getInt(AppConstant.READ_STATUS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comment;
    }

    public List<Tasks> getTasks(Child child, DateTime month) {
        List<Tasks> tasks = new ArrayList<>();
        List<Tasks> childTasks = child.getTasksArrayList();
        for (Tasks task : childTasks) {
            if (task.getRepititionSchedule() == null) {
                if (sameMonthAndYear(month, new DateTime(task.getDueDate()))) {
                    tasks.add(task);
                }
            } else if (!new DateTime(task.getDueDate()).withTimeAtStartOfDay().isAfter(month.withTimeAtStartOfDay())) {
                if (task.getRepititionSchedule().getRepeat().equalsIgnoreCase("monthly")) {
                    if (task.getRepititionSchedule().monthlyRepeatHasNumbers()) {

                    } else {

                    }
                }
            }
        }
        return tasks;
    }

    private boolean sameMonthAndYear(DateTime monthDate, DateTime taskDate) {
        return monthDate.getYear() == taskDate.getYear() && monthDate.getMonthOfYear() == taskDate.getMonthOfYear();
    }

    /**
     * Method return a list of the tasks for RecyclerView(old ListView) adapter;
     * Adapter should sort tasks by status or dates.
     * Previous developer sorted tasks by date;
     * So i need for all tasks set some dates
     * 1. Pending Approval (today minus day)
     * 2. Today (dueDate of this Tasks)
     * 3. Past Due (fakeDate = new DateTime().withYear(1980).withTimeAtStartOfDay()) This date means that task is past due
     * 4. Future dates in order (dueDate of this Tasks)
     *
     * @param /*childObject. This object has all data of the child.
     * @return List for tasks adapter
     */
    public ArrayList<ChildsTaskObject> getChildTaskListObject(Child childObject) {

        //create date for tasks pending approval
        PENDING_APPROVAL_DATE_TIME = new DateTime(Tasks.fakeDate);

        //create date for past due tasks
        PAST_DUE_DATE_TIME = new DateTime().plusDays(-1).withTimeAtStartOfDay();

        //plusMonth need to increase month if you created a task with a date before today.
        int plusMonth = 0;

        for (Tasks task : childObject.getTasksArrayList()) {
            //check. if this task not repetitive and has status completed just add this task with fakeDate
            //All tasks with status complete must added with fakeDate
            //AppConstant.COMPLETED is a String = 'completed'
            if (task.getRepititionSchedule() == null && task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                //task.getFakeDate() return fakeDate(with 1980 year) if task is completed/pending approval
                //or dueDate of this task if status not completed
                addToMap(task, new DateTime(task.getFakeDate()));
            } else {
                if (task.getRepititionSchedule() != null) {
                    if (Objects.equals(task.getRepititionSchedule().getRepeat(), "monthly")) {
                        //startDate - when to create first instance of the task
                        DateTime startDate = new DateTime(task.getDueDate());
                        //endDate - last day to show Task
                        //AppConstant.MONTHLY_NUM_REPETITIONS is Integer = 7
                        DateTime endDate = new DateTime().plusMonths(AppConstant.MONTHLY_NUM_REPETITIONS);
                        int monthsInterval = Months.monthsBetween(startDate, endDate).getMonths();
                        for (int j = 0; j < monthsInterval; j++) {
                            int plusMonts = j * task.getRepititionSchedule().getEveryNRepeat();
                            //check if monthly task has specific days of the Month (1, 3, 8, 22, 31)
                            if (task.getRepititionSchedule().monthlyRepeatHasNumbers()) {
                                for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                    //clone task
                                    Tasks newTask = Tasks.from(task);
                                    int day = Integer.parseInt(task.getRepititionSchedule().getSpecificDays().get(i));
                                    //If i change dueDate for task instance, i must know real dueDate(when this task are started)
                                    //So i created startDate for Task object to save this field(real dueDate)
                                    newTask.setStartDate(newTask.getDueDate());
                                    DateTime fakeDate = new DateTime(newTask.getStartDate()).withDayOfMonth(day).plusMonths(plusMonts);
                                    newTask.setDueDate(fakeDate.getMillis());
                                    //setStatusForTask just check dayTaskStatus for this task
                                    //and change status for this task if this needed
                                    setStatusForTask(newTask);
                                    if (!new DateTime(newTask.getDueDate()).plusMonths(j * newTask.getRepititionSchedule().everyNRepeat).withDayOfMonth(day).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))
                                        if (!newTask.isApproved()) {
                                            if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                                                addToMap(newTask, PENDING_APPROVAL_DATE_TIME);
                                            } else {
                                                addToMap(newTask, new DateTime(newTask.getFakeDate()));
                                            }
                                        }
                                }
                            } else {
                                //if monthly task has specific days of the Week (monday, saturday, friday)
                                Tasks newTask = Tasks.from(task);
                                newTask.setStartDate(newTask.getDueDate());

                                //FIRST week, SECOND week .... (1, 2, 3, 4)
                                int week = newTask.getWeekAsInt(newTask.getRepititionSchedule().getSpecificDays().get(0));
                                //repeat every N times
                                int performTaskOnTheNSpecifiedDay = newTask.getPerformTaskOnTheNSpecifiedDay();

                                //verify that the instance of the task date is not set before real dueDate
                                if (j == 0) {
                                    DateTime dayOfWeek = new DateTime(newTask.getStartDate()).withDayOfMonth(1).plusWeeks(performTaskOnTheNSpecifiedDay).withDayOfWeek(week).withTimeAtStartOfDay();
                                    if (dayOfWeek.isBefore(new DateTime(task.getStartDate()).withTimeAtStartOfDay())) {
                                        plusMonth++;
                                    }
                                }

                                DateTime fakeDate = new DateTime(newTask.getStartDate())
                                        .plusMonths((j + plusMonth) * newTask.getRepititionSchedule().everyNRepeat)
                                        .withDayOfMonth(1)
                                        .plusWeeks(performTaskOnTheNSpecifiedDay)
                                        .withDayOfWeek(week);
                                if (performTaskOnTheNSpecifiedDay == -1) {
                                    fakeDate = fakeDate.withDayOfMonth(1).minusDays(-1 - 7).withDayOfWeek(week);
                                }
                                newTask.setDueDate(fakeDate.getMillis());
                                setStatusForTask(newTask);
                                if (!new DateTime(newTask.getDueDate()).withDayOfWeek(week).plusWeeks(j * newTask.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))

                                    if (!newTask.isApproved()) {
                                        if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                                            addToMap(newTask, PENDING_APPROVAL_DATE_TIME);
                                        } else {
                                            addToMap(newTask, new DateTime(newTask.getFakeDate()));
                                        }
                                    }

                            }
                        }
                    } else if (Objects.equals(task.getRepititionSchedule().getRepeat(), "weekly")) {
                        DateTime startDate = new DateTime(task.getDueDate());
                        DateTime endDate = new DateTime().plusWeeks(AppConstant.WEEKLY_NUM_REPETITIONS);
                        int weeksInterval = Weeks.weeksBetween(startDate, endDate).getWeeks();
                        for (int j = 0; j < weeksInterval; j++) {
                            for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                Tasks newTask = Tasks.from(task);
                                int week;

                                switch (task.getRepititionSchedule().getSpecificDays().get(i)) {
                                    case "Sunday":
                                        week = 6;
                                        break;
                                    case "monday":
                                        week = 1;
                                        break;
                                    case "tuesday":
                                        week = 2;
                                        break;
                                    case "wednesday":
                                        week = 3;
                                        break;
                                    case "thursday":
                                        week = 4;
                                        break;
                                    case "friday":
                                        week = 5;
                                        break;
                                    case "saturday":
                                        week = 6;
                                        break;
                                    default:
                                        week = 1;
                                }
                                newTask.setStartDate(newTask.getDueDate());
                                DateTime fakeDate = new DateTime(newTask.getStartDate()).withDayOfWeek(week).plusWeeks(j*task.getRepititionSchedule().getEveryNRepeat());
                                if (!fakeDate.isBefore(new DateTime(newTask.getStartDate()))) {
                                    if (fakeDate.withTimeAtStartOfDay().isBefore(new DateTime().withTimeAtStartOfDay())) {
                                        fakeDate.plusWeeks(1);
                                    }
                                    newTask.setDueDate(fakeDate.getMillis());
                                    setStatusForTask(newTask);
                                    if (!new DateTime(newTask.getDueDate()).withDayOfWeek(week).plusWeeks(j * newTask.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime(task.getStartDate()))) {
                                        if (!newTask.isApproved()) {
                                            if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                                                addToMap(newTask, PENDING_APPROVAL_DATE_TIME);
                                            } else {
                                                addToMap(newTask, new DateTime(newTask.getFakeDate()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (Objects.equals(task.getRepititionSchedule().getRepeat(), "daily")) {
                        DateTime startDate = new DateTime(task.getDueDate());
                        DateTime endDate = new DateTime().plusDays(AppConstant.DAILY_NUM_REPETITIONS);
                        int daysInterval = Days.daysBetween(startDate, endDate).getDays();
                        for (int i = 0; i < daysInterval; i++) {
                            Tasks newTask = Tasks.from(task);
                            newTask.setStartDate(newTask.getDueDate());
                            Integer repeat = newTask.getRepititionSchedule().getEveryNRepeat();
                            DateTime fakeDate = new DateTime(newTask.getStartDate()).plusDays(i * repeat);
                            newTask.setDueDate(fakeDate.getMillis());
                            setStatusForTask(newTask);
                            if (!newTask.isApproved()) {
                                if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                                    addToMap(newTask, PENDING_APPROVAL_DATE_TIME);
                                } else {
                                    addToMap(newTask, new DateTime(newTask.getFakeDate()));
                                }
                            }
                        }
                    } else {
                        String key = new DateTime(task.getDueDate()).toString();

                        if (!task.getStatus().equals(AppConstant.APPROVED)) {
                            if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                                addToMap(task, PENDING_APPROVAL_DATE_TIME);
                            addToMap(task, new DateTime(task.getDueDate()));
                        }
                    }
                } else {
                    String key = new DateTime(task.getDueDate()).toString();

                    if (!task.getStatus().equals(AppConstant.APPROVED)) {
                        if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                            addToMap(task, PENDING_APPROVAL_DATE_TIME);
                        addToMap(task, new DateTime(task.getDueDate()));
                    }
                }
            }
        }
        map = sortingMap(map);
        for (String key : map.keySet()) {
            childsTaskObjectList.add(new ChildsTaskObject(key, map.get(key)));
        }
        return childsTaskObjectList;
    }
    public ArrayList<ChildsTaskObject> getChildExpiredTaskListObject(Child childObject) {

        //create date for tasks pending approval
        PENDING_APPROVAL_DATE_TIME = new DateTime(Tasks.fakeDate);

        //create date for past due tasks
        PAST_DUE_DATE_TIME = new DateTime().plusDays(-1).withTimeAtStartOfDay();

        //plusMonth need to increase month if you created a task with a date before today.
        int plusMonth = 0;

        for (Tasks task : childObject.getTasksArrayList()) {
            //check. if this task not repetitive and has status completed just add this task with fakeDate
            //All tasks with status complete must added with fakeDate
            //AppConstant.COMPLETED is a String = 'completed'
            if (task.getRepititionSchedule() == null && task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)
                    && new DateTime(task.getDueDate()).isBefore(DateTime.now())) {
                //task.getFakeDate() return fakeDate(with 1980 year) if task is completed/pending approval
                //or dueDate of this task if status not completed
                addToMap(task, new DateTime(task.getFakeDate()));
            } else {
                if (task.getRepititionSchedule() != null) {
                    if (Objects.equals(task.getRepititionSchedule().getRepeat(), "monthly")) {
                        //startDate - when to create first instance of the task
                        DateTime startDate = new DateTime(task.getDueDate());
                        //endDate - last day to show Task
                        //AppConstant.MONTHLY_NUM_REPETITIONS is Integer = 7
                        DateTime endDate = new DateTime().plusMonths(AppConstant.MONTHLY_NUM_REPETITIONS);
                        int monthsInterval = Months.monthsBetween(startDate, endDate).getMonths();
                        for (int j = 0; j < monthsInterval; j++) {
                            int plusMonts = j * task.getRepititionSchedule().getEveryNRepeat();
                            //check if monthly task has specific days of the Month (1, 3, 8, 22, 31)
                            if (task.getRepititionSchedule().monthlyRepeatHasNumbers()) {
                                for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                    //clone task
                                    Tasks newTask = Tasks.from(task);
                                    int day = Integer.parseInt(task.getRepititionSchedule().getSpecificDays().get(i));
                                    //If i change dueDate for task instance, i must know real dueDate(when this task are started)
                                    //So i created startDate for Task object to save this field(real dueDate)
                                    newTask.setStartDate(newTask.getDueDate());
                                    DateTime fakeDate = new DateTime(newTask.getStartDate()).withDayOfMonth(day).plusMonths(plusMonts);
                                    newTask.setDueDate(fakeDate.getMillis());
                                    //setStatusForTask just check dayTaskStatus for this task
                                    //and change status for this task if this needed
                                    setStatusForTask(newTask);
                                    if (!new DateTime(newTask.getDueDate()).plusMonths(j * newTask.getRepititionSchedule().everyNRepeat).withDayOfMonth(day).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))
                                        if (!newTask.isApproved() && new DateTime(newTask.getDueDate()).isBefore(DateTime.now())) {
                                            if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                                                addToMap(newTask, PENDING_APPROVAL_DATE_TIME);
                                            } else {
                                                addToMap(newTask, new DateTime(newTask.getFakeDate()));
                                            }
                                        }
                                }
                            } else {
                                //if monthly task has specific days of the Week (monday, saturday, friday)
                                Tasks newTask = Tasks.from(task);
                                newTask.setStartDate(newTask.getDueDate());

                                //FIRST week, SECOND week .... (1, 2, 3, 4)
                                int week = newTask.getWeekAsInt(newTask.getRepititionSchedule().getSpecificDays().get(0));
                                //repeat every N times
                                int performTaskOnTheNSpecifiedDay = newTask.getPerformTaskOnTheNSpecifiedDay();

                                //verify that the instance of the task date is not set before real dueDate
                                if (j == 0) {
                                    DateTime dayOfWeek = new DateTime(newTask.getStartDate()).withDayOfMonth(1).plusWeeks(performTaskOnTheNSpecifiedDay).withDayOfWeek(week).withTimeAtStartOfDay();
                                    if (dayOfWeek.isBefore(new DateTime(task.getStartDate()).withTimeAtStartOfDay())) {
                                        plusMonth++;
                                    }
                                }

                                DateTime fakeDate = new DateTime(newTask.getStartDate())
                                        .plusMonths((j + plusMonth) * newTask.getRepititionSchedule().everyNRepeat)
                                        .withDayOfMonth(1)
                                        .plusWeeks(performTaskOnTheNSpecifiedDay)
                                        .withDayOfWeek(week);
                                if (performTaskOnTheNSpecifiedDay == -1) {
                                    fakeDate = fakeDate.withDayOfMonth(1).minusDays(-1 - 7).withDayOfWeek(week);
                                }
                                newTask.setDueDate(fakeDate.getMillis());
                                setStatusForTask(newTask);
                                if (!new DateTime(newTask.getDueDate()).withDayOfWeek(week).plusWeeks(j * newTask.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))

                                    if (!newTask.isApproved() && new DateTime(newTask.getDueDate()).isBefore(DateTime.now())) {
                                        if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                                            addToMap(newTask, PENDING_APPROVAL_DATE_TIME);
                                        } else {
                                            addToMap(newTask, new DateTime(newTask.getFakeDate()));
                                        }
                                    }

                            }
                        }
                    } else if (Objects.equals(task.getRepititionSchedule().getRepeat(), "weekly")) {
                        DateTime startDate = new DateTime(task.getDueDate());
                        DateTime endDate = new DateTime().plusWeeks(AppConstant.WEEKLY_NUM_REPETITIONS);
                        int weeksInterval = Weeks.weeksBetween(startDate, endDate).getWeeks();
                        for (int j = 0; j < weeksInterval; j++) {
                            for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                Tasks newTask = Tasks.from(task);
                                int week;

                                switch (task.getRepititionSchedule().getSpecificDays().get(i)) {
                                    case "Sunday":
                                        week = 6;
                                        break;
                                    case "monday":
                                        week = 1;
                                        break;
                                    case "tuesday":
                                        week = 2;
                                        break;
                                    case "wednesday":
                                        week = 3;
                                        break;
                                    case "thursday":
                                        week = 4;
                                        break;
                                    case "friday":
                                        week = 5;
                                        break;
                                    case "saturday":
                                        week = 6;
                                        break;
                                    default:
                                        week = 1;
                                }
                                newTask.setStartDate(newTask.getDueDate());
                                DateTime fakeDate = new DateTime(newTask.getStartDate()).withDayOfWeek(week).plusWeeks(j/* * week*/);
                                if (!fakeDate.isBefore(new DateTime(newTask.getStartDate()))) {
                                    if (fakeDate.withTimeAtStartOfDay().isBefore(new DateTime().withTimeAtStartOfDay())) {
                                        fakeDate.plusWeeks(1);
                                    }
                                    newTask.setDueDate(fakeDate.getMillis());
                                    setStatusForTask(newTask);
                                    if (!new DateTime(newTask.getDueDate()).withDayOfWeek(week).plusWeeks(j * newTask.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime(task.getStartDate()))) {
                                        if (!newTask.isApproved() && new DateTime(newTask.getDueDate()).isBefore(DateTime.now())) {
                                            if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                                                addToMap(newTask, PENDING_APPROVAL_DATE_TIME);
                                            } else {
                                                addToMap(newTask, new DateTime(newTask.getFakeDate()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (Objects.equals(task.getRepititionSchedule().getRepeat(), "daily")) {
                        DateTime startDate = new DateTime(task.getDueDate());
                        DateTime endDate = new DateTime().plusDays(AppConstant.DAILY_NUM_REPETITIONS);
                        int daysInterval = Days.daysBetween(startDate, endDate).getDays();
                        for (int i = 0; i < daysInterval; i++) {
                            Tasks newTask = Tasks.from(task);
                            newTask.setStartDate(newTask.getDueDate());
                            Integer repeat = newTask.getRepititionSchedule().getEveryNRepeat();
                            DateTime fakeDate = new DateTime(newTask.getStartDate()).plusDays(i * repeat);
                            newTask.setDueDate(fakeDate.getMillis());
                            setStatusForTask(newTask);
                            if (!newTask.isApproved() && new DateTime(newTask.getDueDate()).isBefore(DateTime.now())) {
                                if (newTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                                    addToMap(newTask, PENDING_APPROVAL_DATE_TIME);
                                } else {
                                    addToMap(newTask, new DateTime(newTask.getFakeDate()));
                                }
                            }
                        }
                    } else {
                        String key = new DateTime(task.getDueDate()).toString();

                        if (!task.getStatus().equals(AppConstant.APPROVED) && new DateTime(task.getDueDate()).isBefore(DateTime.now())) {
                            if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                                addToMap(task, PENDING_APPROVAL_DATE_TIME);
                            addToMap(task, new DateTime(task.getDueDate()));
                        }
                    }
                } else {
                    String key = new DateTime(task.getDueDate()).toString();

                    if (!task.getStatus().equals(AppConstant.APPROVED) && new DateTime(task.getDueDate()).isBefore(DateTime.now())) {
                        if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                            addToMap(task, PENDING_APPROVAL_DATE_TIME);
                        addToMap(task, new DateTime(task.getDueDate()));
                    }
                }
            }
        }
        map = sortingMap(map);
        for (String key : map.keySet()) {
            childsTaskObjectList.add(new ChildsTaskObject(key, map.get(key)));
        }
        return childsTaskObjectList;
    }

    private Map<String, ArrayList<Tasks>> sortingMap(Map<String, ArrayList<Tasks>> map) {
        Map<String, ArrayList<Tasks>> newMap = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            if (new DateTime(key).getYear() == 1980) {
                newMap.put(key, map.get(key));
            }
        }
        for (String key : map.keySet()) {
            if (new DateTime(key).withTimeAtStartOfDay().isEqual(new DateTime().withTimeAtStartOfDay())) {
                newMap.put(key, map.get(key));
            }
        }
        for (String key : map.keySet()) {
            DateTime before = new DateTime(key).withTimeAtStartOfDay();
            if (before.isBefore(new DateTime().withTimeAtStartOfDay()) && before.getYear() > 1980) {
                newMap.put(key, map.get(key));
            }
        }
        for (String key : map.keySet()) {
            if (new DateTime(key).withTimeAtStartOfDay().isAfter(new DateTime().withTimeAtStartOfDay())) {
                newMap.put(key, map.get(key));
            }
        }
        return newMap;
    }

    public static void setStatusForTask(Tasks task) {
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
                    Log.d("fslkdjfls", "------------------------------------------------------------------------------------------------------------------");
                    Log.d("fslkdjfls", "task: " + task.toString());
                }
            }
        }
    }

    /**
     * All tasks that should appear in the list should be added to the HashMap. The HashMap makes it easier.
     *
     * @param task     - the task that should be added to the HashMap
     * @param dateTime must be converted to String, and putting to the HashMap as a key.
     * @object map is Map<String, ArrayList<Tasks>> map = new TreeMap<>()
     * <String>This is key(timestamp of the date). We sorted all task by this key. One key can contain a list of tasks.</String>
     * <ArrayList<Tasks>>All tasks of key<ArrayList<Tasks>>
     */
    private void addToMap(Tasks task, DateTime dateTime) {
        String key;
        //check if task is before TODAY, so we needed to add this task as past due
        //if not, just add to HashMap with this task dueDate
        if (dateTime.withTimeAtStartOfDay().isBefore(new DateTime().withTimeAtStartOfDay()) && !task.isCompleted()) {
            key = PAST_DUE_DATE_TIME.withTimeAtStartOfDay().toString();
        } else {
            key = dateTime.withTimeAtStartOfDay().toString();
        }
        //really not remember what is this. But i scare to touch this)))))
        if (!dateTime.isEqual(PENDING_APPROVAL_DATE_TIME) && task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
            return;
        }

        //if HashMap contains this key, just add to this key new task
        //if not create new key, and add this task to this key
        if (!map.containsKey(key))
            map.put(key, new ArrayList<Tasks>());
        map.get(key).add(task);
    }
}
