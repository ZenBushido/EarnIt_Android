package com.mobiledi.earnit.utils;

import android.util.Log;

import com.mobiledi.earnit.model.Account;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.RepSchedule;
import com.mobiledi.earnit.model.RepititionSchedule;
import com.mobiledi.earnit.model.TaskComment;
import com.mobiledi.earnit.model.TaskWithTag;
import com.mobiledi.earnit.model.Tasks;

import org.joda.time.DateTime;
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
import java.util.TimeZone;
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
            task.setId(taskObject.getInt(AppConstant.ID));
            task.setChildId(childId);
            task.setName(taskObject.getString(AppConstant.NAME));

            String dateOne = taskObject.getString(AppConstant.CREATE_DATE);
            String dateTwo = taskObject.getString(AppConstant.DUE_DATE);

            SimpleDateFormat ft = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US);

            Date t = null;

            try {
                t = ft.parse(dateOne);

            } catch (ParseException ex) {
                Log.d("Parse Exception", ex.getMessage());
            }
            Date tt = null;

            try {
                tt = ft.parse(dateTwo);

            } catch (ParseException ex) {
                Log.d("Parse Exception", ex.getMessage());
            }


            DateTime dateTime = new DateTime(t);
            DateTime dateTime2 = new DateTime(tt);
            task.setCreateDate(dateTime.getMillis());

            task.setDueDate(dateTime2.getMillis());
            task.setStatus(taskObject.getString(AppConstant.STATUS));
            task.setAllowance(taskObject.getDouble(AppConstant.ALLOWANCE));
            task.setDetails(taskObject.getString(AppConstant.DESCRIPTION));
            if (taskObject.getBoolean(AppConstant.PICTURE_REQUIRED))
                task.setPictureRequired(taskObject.getBoolean(AppConstant.PICTURE_REQUIRED));
            else
                task.setPictureRequired(false);

            if (taskObject.has(AppConstant.GOAL)) {

                if(!taskObject.isNull(AppConstant.GOAL))
                {

                    JSONObject goalObject = taskObject.getJSONObject(AppConstant.GOAL);
                    Goal goal = getGoalObject(goalObject);
                    task.setGoal(goal);
                }

            }

            if (taskObject.has(AppConstant.REPITITION_SCHEDULE)) {
                if (!taskObject.isNull(AppConstant.REPITITION_SCHEDULE)) {
                    JSONObject scheduleObject = taskObject.getJSONObject(AppConstant.REPITITION_SCHEDULE);
                    RepititionSchedule schedule = getRepititionSchedule(scheduleObject);
                    task.setRepititionSchedule(schedule);
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
            DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM d, yyyy hh:mm:ss a");
            DateTime dt = formatter.parseDateTime(account.getString(AppConstant.CREATE_DATE));
            long milliseconds = dt.getMillis();
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

    List<ChildsTaskObject> childsTaskObjectList = new ArrayList<>();

    Map<String, ArrayList<Tasks>> map = new TreeMap<>();


    public List<ChildsTaskObject> getChildTaskListObject(Child childObject, String type,String fromScreen) {
        //Get Unique Dates
        PENDING_APPROVAL_DATE = new DateTime().plusDays(-2).withTimeAtStartOfDay().toString();
        PAST_DUE_DATE = new DateTime().plusDays(-1).withTimeAtStartOfDay().toString();
        for (Tasks task : childObject.getTasksArrayList()) {
            if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED) )
                addToMap(task, PENDING_APPROVAL_DATE);
            else{
            if (task.getRepititionSchedule() != null) {
                if (Objects.equals(task.getRepititionSchedule().getRepeat(), "monthly")) {
                    for (int j = 0; j < 7; j++) {

                        for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {

                            Integer month = Integer.parseInt(task.getRepititionSchedule().getSpecificDays().get(i));
                            String key = new DateTime(task.getDueDate()).withDayOfMonth(month).plusMonths(j * task.getRepititionSchedule().getEveryNRepeat()).withTimeAtStartOfDay().toString();
                            if (!new DateTime(task.getDueDate()).withDayOfMonth(month).plusMonths(j * task.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))
                                if (!task.getStatus().equals(AppConstant.APPROVED)) {
                                    if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED) )
                                        addToMap(task, PENDING_APPROVAL_DATE);
                                    addToMap(task, key);
                                }
                        }
                    }
                } else if (Objects.equals(task.getRepititionSchedule().getRepeat(), "weekly")) {
                    for (int j = 0; j < 7; j++) {
                        for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                            Integer month = 1;

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
                            String key = new DateTime(task.getDueDate()).withDayOfWeek(month).plusWeeks(j * task.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().toString();
                            if (!new DateTime(task.getDueDate()).withDayOfWeek(month).plusWeeks(j * task.getRepititionSchedule().everyNRepeat).withTimeAtStartOfDay().isBefore(new DateTime().plusDays(-1).withTimeAtStartOfDay()))
                                if (!task.getStatus().equals(AppConstant.APPROVED)) {
                                    if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                                        addToMap(task, PENDING_APPROVAL_DATE);
                                    addToMap(task, key);
                                }
                        }
                    }
                } else if (Objects.equals(task.getRepititionSchedule().getRepeat(), "daily")) {
                    for (int i = 0; i < 7; i++) {
                        Integer month = task.getRepititionSchedule().getEveryNRepeat();


                        String key = new DateTime(task.getDueDate()).plusDays(i * month).withTimeAtStartOfDay().toString();
                        if (!task.getStatus().equals(AppConstant.APPROVED)) {
                            if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                                addToMap(task, PENDING_APPROVAL_DATE);
                            addToMap(task, key);
                        }
                    }
                } else {
                    String key = new DateTime(task.getDueDate()).withTimeAtStartOfDay().toString();

                    if (!task.getStatus().equals(AppConstant.APPROVED)) {
                        if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED) )
                            addToMap(task, PENDING_APPROVAL_DATE);
                        addToMap(task, key);
                    }
                }
            } else {
                String key = new DateTime(task.getDueDate()).withTimeAtStartOfDay().toString();

                if (!task.getStatus().equals(AppConstant.APPROVED)) {
                    if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED) )
                        addToMap(task, PENDING_APPROVAL_DATE);
                    addToMap(task, key);
                }
            }
        }}

        for (String key : map.keySet()) {
            childsTaskObjectList.add(new ChildsTaskObject(key, map.get(key)));
        }
        return childsTaskObjectList;
    }

    private void addToMap(Tasks task, String key) {
        if (!key.equals(PENDING_APPROVAL_DATE) && task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED) )
            return;

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
