package com.firepitmedia.earnit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.DayTaskStatus;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.RepititionSchedule;
import com.firepitmedia.earnit.model.TaskComment;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.model.getChild.Task;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.GetObjectFromResponse;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.firepitmedia.earnit.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit.GetGoalsFromChildInterface;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Response;

public class ParentTaskApproval extends BaseActivity implements View.OnClickListener {


    TextView tTaskName, taskDetails, taskDueDate, taskAllowance, taskRepeat, taskPicture, commentLabel;
    Child childObject, otherChildObject;
    @NonNull
    Tasks taskObject;
    Parent parentObject;
    ParentTaskApproval parentTaskApproval;
    ImageView postedImage;
    EditText commentBox;
    ImageButton cancel;
    Button approval, declined;
    RelativeLayout progressBar;
    private final String TAG = "ParentTaskApproval";
    String fromScreen;
    ScreenSwitch screenSwitch;
    ArrayList<Tasks> completedTaskList;
    TaskComment taskComment;
    RepititionSchedule repititionSchedule;
    private long dueDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_approval_view);
        parentTaskApproval = this;
        screenSwitch = new ScreenSwitch(parentTaskApproval);
        settingViewIds();

        //SERIALIZE OBJECT FROM INTENT OBJECT
        Intent intent = getIntent();
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChildObject = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        fromScreen = intent.getStringExtra(AppConstant.FROM_SCREEN);
        taskComment = (TaskComment) intent.getSerializableExtra(AppConstant.TASK_COMMENTS);
        repititionSchedule = (RepititionSchedule) intent.getSerializableExtra(AppConstant.REPITITION_SCHEDULE);
        Utils.logDebug(TAG, "comming from >" + fromScreen);
        dueDate = intent.getLongExtra(AppConstant.DUE_DATE_STRING, 0);

        taskObject = (Tasks) intent.getSerializableExtra(AppConstant.TASK_OBJECT);
        if (repititionSchedule != null) {
            taskObject.setRepititionSchedule(repititionSchedule);
        }
        dueDate = taskObject.getDueDate();
        Log.d("asdljlahsdkj", "ParentTaskApproval task from getIntent = " + taskObject.toString());

        //   Log.e(TAG, "Task Object: "+taskObject.getName());
        //    Log.e(TAG, "Task Object: "+taskObject.getGoal().getGoalName());

        autoFillTaskDetails(taskObject);


//        if (intent.getSerializableExtra(AppConstant.TASK_OBJECT) != null) {
//
//            taskObject = (Tasks) intent.getSerializableExtra(AppConstant.TASK_OBJECT);
//            dueDate = taskObject.getDueDate();
//
////            Log.d("asdljlahsdkj", "\nnput Task: " + new Task().from(taskObject).toString() + "\ntaskObject: " + taskObject.toString());
//
//            //   Log.e(TAG, "Task Object: "+taskObject.getName());
//            //    Log.e(TAG, "Task Object: "+taskObject.getGoal().getGoalName());
//
//            autoFillTaskDetails(taskObject);
//        } else {
//            try {
//                completedTaskList = fetchComletedTaskList(childObject);
//
//                if (completedTaskList.size() > 0) {
//                    taskObject = completedTaskList.get(0);
//                    autoFillTaskDetails(taskObject);
//                } else
//                    screenSwitch.moveToParentDashboard(parentObject);
//            } catch (NullPointerException ignored) {
//            }
//        }
    }

    public void settingViewIds() {
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        tTaskName = (TextView) findViewById(R.id.task_name);
        taskDetails = (TextView) findViewById(R.id.task_description);
        taskDueDate = (TextView) findViewById(R.id.task_due_date);
        commentBox = (EditText) findViewById(R.id.comment_box);
        postedImage = (ImageView) findViewById(R.id.posted_imageview);
        declined = (Button) findViewById(R.id.decline);
        approval = (Button) findViewById(R.id.approve);
        cancel = (ImageButton) findViewById(R.id.back_arrow);
        taskAllowance = (TextView) findViewById(R.id.task_allowance);
        taskPicture = (TextView) findViewById(R.id.task_photo_required);
        taskRepeat = (TextView) findViewById(R.id.task_repeat);
        commentLabel = (TextView) findViewById(R.id.comment_label);
        declined.setOnClickListener(parentTaskApproval);
        approval.setOnClickListener(parentTaskApproval);
        cancel.setOnClickListener(parentTaskApproval);


    }

    private void autoFillTaskDetails(Tasks task) {
        if (!task.getName().isEmpty())
            tTaskName.setText(taskObject.getName());

        if (taskObject.getDetails().isEmpty())
            taskDetails.setText(getResources().getString(R.string.task_despription));
        else
            taskDetails.setText(taskObject.getDetails());


        taskAllowance.setText("$ " + String.valueOf(Utils.roundOff(task.getAllowance(), 2)));
        if (!task.getPictureRequired()) {
            taskPicture.setText(AppConstant.NO);
            postedImage.setVisibility(View.GONE);
        } else {
            postedImage.setVisibility(View.VISIBLE);
            taskPicture.setText(AppConstant.YES);
        }

        if (task.getRepititionSchedule() != null) {
            String repeatFrequency = task.getRepititionSchedule().getRepeat();

            if (TextUtils.isEmpty(repeatFrequency)) {
                Toast.makeText(this, "Pending for approval.", Toast.LENGTH_SHORT).show();
            } else {
                taskRepeat.setText(repeatFrequency.substring(0, 1).toUpperCase() + repeatFrequency.substring(1));
            }
        } else
            taskRepeat.setText(AppConstant.NO);

        if (task.getTaskComments() != null) {
            Log.e(TAG, "Task comment is not null");
            if (task.getTaskComments().size() > 0) {
                TaskComment comment = task.getTaskComments().get(task.getTaskComments().size() - 1);
                if (comment.getPictureUrl().isEmpty()) {
                    postedImage.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loadPhoto(comment.getPictureUrl(), postedImage);
                }

                if (comment.getComment().isEmpty()) {
                    commentBox.setVisibility(View.GONE);
                    commentLabel.setVisibility(View.GONE);
                } else {
                    commentBox.setText(comment.getComment());
                    commentBox.setKeyListener(null);
                }
            }
        } else {

            if (taskComment != null) {
                if (taskComment.getPictureUrl().isEmpty()) {
                    postedImage.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loadPhoto(taskComment.getPictureUrl(), postedImage);
                }

                if (taskComment.getComment().isEmpty()) {
                    commentBox.setVisibility(View.GONE);
                    commentLabel.setVisibility(View.GONE);
                } else {
                    commentBox.setText(taskComment.getComment());
                    commentBox.setKeyListener(null);
                }

            }


        }


        DateTime dt = new DateTime(taskObject.getDueDate());
        DateTimeFormatter fmt = DateTimeFormat.forPattern(AppConstant.DATE_FORMAT);
        String toPrintDate = fmt.print(dt);
        taskDueDate.setText(toPrintDate);

    }

    private void loadPhoto(String url, ImageView imageView) {
        Log.d("fsdfhkj", "list updateAvatar. url = " + url);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        String emailPassword = MyApplication.getInstance().getEmail() + ":" + MyApplication.getInstance().getPassword();
                        String basic = "Basic " + Base64.encodeToString(emailPassword.getBytes(), Base64.NO_WRAP);
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", basic)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client))
                .build();
        picasso
                .load(AppConstant.BASE_URL + "/" + url)
//                .error(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.default_avatar)))
//                .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.default_avatar)))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
//                goBack();
                onBackPressed();
                break;

            case R.id.decline:
                updateTaskStatus(taskObject, AppConstant.DECLINED);
                break;

            case R.id.approve:
                //       addEditGoal(taskObject);
                updateTaskStatus(taskObject, AppConstant.APPROVED);
        }
    }

    private void updateTask(Tasks h, String status) {
        Task task = new Task().from(h);
        if (task.isRepeat()) {
            if (task.isLastTask())
                task.setStatus(AppConstant.TASK_CLOSED);
            else {

            }
        } else {
            task.setStatus(AppConstant.TASK_CLOSED);
        }
        SharedPreferences preferences = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        String email = preferences.getString(AppConstant.EMAIL, null);
        String password = preferences.getString(AppConstant.PASSWORD, null);
        GetGoalsFromChildInterface getTaskFromChildInterface = ServiceGenerator.createService(GetGoalsFromChildInterface.class, email, password);
        retrofit2.Call<Task> taskV2Models = getTaskFromChildInterface.updateTask(task);
        Log.d("asdljlahsdkj", "before send request:\nEmail: " + email + "\nPassword: " + password + "\nTask: \n" + task.toString());
        taskV2Models.enqueue(new retrofit2.Callback<Task>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Task> call, @NonNull Response<Task> response) {
                if (response.body() != null) {
                    Log.d("asdljlahsdkj", response.body().toString());
                } else {
                    Log.d("asdljlahsdkj", "response.body() is null. Response = \n" + response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Task> call, @NonNull Throwable throwable) {

            }
        });
    }

    private boolean isLastTask(Tasks task) {
        if (task.getRepititionSchedule() != null && task.getRepititionSchedule().getSpecificDays() != null
                && !task.getRepititionSchedule().getSpecificDays().isEmpty()) {
            int i = -1;
            try {
                i = Integer.parseInt(task.getRepititionSchedule().getSpecificDays().get(task.getRepititionSchedule().getSpecificDays().size() - 1));
            } catch (NumberFormatException ignored) {
            }
            if (i == new DateTime(dueDate).getDayOfMonth()) {
                return true;
            }
        }
        return false;
    }

    private void updateTaskStatus(Tasks selectedTask, String changedStatus) {
        Utils.logDebug(TAG, "updateTaskStatus. Task == " + selectedTask.toString());
        JSONObject taskJson = new JSONObject();
        boolean isLastTask = isLastTask(selectedTask);
        try {
            taskJson.put(AppConstant.CHILDREN, new JSONObject().put(AppConstant.ID, selectedTask.getChildId()));
            taskJson.put(AppConstant.ID, selectedTask.getId());
            taskJson.put(AppConstant.ALLOWANCE, selectedTask.getAllowance());
            taskJson.put(AppConstant.UPDATE_DATE, new DateTime().toString("MMM dd, yyyy HH:mm:ss a"));
            taskJson.put(AppConstant.NAME, selectedTask.getName());
            taskJson.put(AppConstant.DUE_DATE, selectedTask.getRepititionSchedule() == null ? selectedTask.getDueDate() : selectedTask.getStartDate());
            taskJson.put(AppConstant.CREATE_DATE, selectedTask.getCreateDate());
            taskJson.put(AppConstant.DESCRIPTION, selectedTask.getDetails());
            if (selectedTask.getRepititionSchedule() == null/* || isLastTask*/) {
                taskJson.put(AppConstant.STATUS, changedStatus);
                Utils.logDebug(TAG, "1 getRepititionSchedule() == " + selectedTask.getRepititionSchedule() + ".  isLastTask = " + isLastTask);
            } else {
                taskJson.put(AppConstant.STATUS, "Created");
                Utils.logDebug(TAG, "2 getRepititionSchedule() == " + selectedTask.getRepititionSchedule() + ".  isLastTask = " + isLastTask);
            }
            taskJson.put(AppConstant.UPDATE_DATE, new DateTime().getMillis());
            taskJson.put(AppConstant.ALLOWANCE, selectedTask.getAllowance());

            if (selectedTask.getRepititionSchedule() == null)
                Utils.logDebug(TAG, "repeat is none");
            else {
                JSONObject repeatSchedule = new JSONObject();
                repeatSchedule.put(AppConstant.ID, selectedTask.getRepititionSchedule().getId());
                repeatSchedule.put(AppConstant.REPEAT, selectedTask.getRepititionSchedule().getRepeat());
                List<DayTaskStatus> dayTaskStatusesList = selectedTask.getRepititionSchedule().getDayTaskStatuses();
                JSONArray dayTaskStatuses = new JSONArray();
                for (DayTaskStatus dayStatus : dayTaskStatusesList){
                    JSONObject dayTaskStatus = new JSONObject();
                    dayTaskStatus.put("createdDateTime", dayStatus.getCreatedDateTime());
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy HH:mm:ss a").withLocale(Locale.US);
                    DateTime dt = formatter.parseDateTime(dayStatus.getCreatedDateTime());
                    if (dt.withTimeAtStartOfDay().isEqual(new DateTime(selectedTask.getDueDate()).withTimeAtStartOfDay())){
                        dayTaskStatus.put("status", changedStatus);
                    } else {
                        dayTaskStatus.put("status", dayStatus.getStatus());
                    }
                    dayTaskStatus.put("id", dayStatus.getId());
                    dayTaskStatuses.put(dayTaskStatus);
                }
                repeatSchedule.put("dayTaskStatuses", dayTaskStatuses);
                taskJson.put(AppConstant.REPITITION_SCHEDULE, repeatSchedule);
            }

            if (selectedTask.getPictureRequired())
                taskJson.put(AppConstant.PICTURE_REQUIRED, selectedTask.getPictureRequired());
            else {
                taskJson.put(AppConstant.PICTURE_REQUIRED, 0);
                Utils.logDebug(TAG, "picture required not checked");
            }

            if (selectedTask.getGoal() != null) {
                taskJson.put(AppConstant.GOAL, new JSONObject().put(AppConstant.ID, selectedTask.getGoal().getId()));

            }
            Utils.logDebug(TAG, "child-update-task : " + taskJson.toString());
            StringEntity entity = new StringEntity(taskJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            httpClient.addHeader("Authorization", basicAuth);
            httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());

            httpClient.put(parentTaskApproval, AppConstant.BASE_URL + AppConstant.TASKS_API, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Utils.logDebug(TAG, " onSuccessOU : " + response.toString());
                    progressBar.setVisibility(View.GONE);
                    lockScreen();
                    if (isDeviceOnline())
                        fetchChildTaskList();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Utils.logDebug(TAG, " onSuccessAU : " + response.toString());
                    progressBar.setVisibility(View.GONE);
                    lockScreen();
                    if (isDeviceOnline())
                        fetchChildTaskList();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    showToast(getResources().getString(R.string.api_calling_failed));
                    Utils.logDebug(TAG, " onFailureU : " + errorResponse.toString());
                    unLockScreen();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    showToast(getResources().getString(R.string.api_calling_failed));
                    Utils.logDebug(TAG, " onFailureU : " + errorResponse.toString());
                    unLockScreen();
                }

                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    lockScreen();
                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                    unLockScreen();
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        goBack();
    }


    public void fetchChildTaskList() {
        AsyncHttpClient client = new AsyncHttpClient();
        String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
        final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
        client.addHeader("Authorization", basicAuth);
        client.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        client.setMaxRetriesAndTimeout(3, 3000);

        client.get(AppConstant.BASE_URL + AppConstant.CHILDREN_API + parentObject.getAccount().getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                unLockScreen();
                Utils.logDebug(TAG, "Child Success response: " + response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject childObj = response.getJSONObject(i);

                        //child with non-approval task
                        if (childObj.getInt(AppConstant.ID) == childObject.getId()) {
                            Child child = new GetObjectFromResponse().getChildObject(childObj);
                            Child otherChild = new GetObjectFromResponse().getChildObject(childObj);

                            //TASKS
                            ArrayList<Tasks> taskList = new ArrayList<>();
                            ArrayList<Tasks> otherTaskList = new ArrayList<>();
                            JSONArray taskArray = childObj.getJSONArray(AppConstant.TASKS);
                            for (int taskIndex = 0; taskIndex < taskArray.length(); taskIndex++) {
                                JSONObject taskObject = taskArray.getJSONObject(taskIndex);
                                if (taskObject.getString(AppConstant.STATUS).equals(AppConstant.COMPLETED)) {
                                    Tasks task = new GetObjectFromResponse().getTaskObject(taskObject, child.getId());
                                    taskList.add(task);
                                }

                                JSONObject othertaskObject = taskArray.getJSONObject(taskIndex);
                                if (!othertaskObject.getString(AppConstant.STATUS).equals(AppConstant.APPROVED)) {
                                    Tasks task = new GetObjectFromResponse().getTaskObject(othertaskObject, child.getId());
                                    otherTaskList.add(task);
                                }
                            }
                            child.setTasksArrayList(taskList);
                            otherChild.setTasksArrayList(otherTaskList);
//                            new ScreenSwitch(parentTaskApproval).moveToTaskApproval(child, otherChild, parentObject, fromScreen, null);
                            screenSwitch.moveToAllTaskScreen(childObject, otherChild, AppConstant.CHECKED_IN_SCREEN, parentObject, AppConstant.BALANCE_SCREEN);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                unLockScreen();
            }

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
                lockScreen();
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                unLockScreen();
            }
        });

    }

    private ArrayList<Tasks> fetchComletedTaskList(Child child) {
        ArrayList<Tasks> toReturn = new ArrayList<>();
        Log.d("sdfksdkfjh", "tasks size: " + child.getTasksArrayList().size());
        for (Tasks task : child.getTasksArrayList()) {
            task.setStartDate(task.getDueDate());
            if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                toReturn.add(task);
            if (task.getRepititionSchedule() != null && task.getRepititionSchedule().getDayTaskStatuses() != null) {
                List<DayTaskStatus> dayTaskStatuses = task.getRepititionSchedule().getDayTaskStatuses();
                for (DayTaskStatus dayTaskStatus : dayTaskStatuses) {
                    if (dayTaskStatus.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                        Tasks newTask = Tasks.from(task);
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy HH:mm:ss a").withLocale(Locale.US);
                        DateTime dt = formatter.parseDateTime(dayTaskStatus.getCreatedDateTime());
                        newTask.setDueDate(dt.getMillis());
                        toReturn.add(newTask);
                    }
                }
            }
        }
        Log.d("sdfksdkfjh", "return size: " + toReturn.size());
        return toReturn;
    }

    private void goBack() {
        childObject.setTasksArrayList(fetchComletedTaskList(childObject));
        screenSwitch.moveToAllTaskScreen(childObject, childObject, fromScreen, parentObject, AppConstant.TASK_APPROVAL_SCREEN);

    }

    private void addEditGoal(Tasks taskObject) {

        JSONObject signInJson = new JSONObject();
        try {
            signInJson.put(AppConstant.CREATE_DATE, new DateTime().getMillis());

            signInJson.put(AppConstant.CHILDREN, new JSONObject().put(AppConstant.ID, childObject.getId()));
            signInJson.put(AppConstant.NAME, taskObject.getGoal().toString().trim());
            signInJson.put(AppConstant.AMOUNT, taskObject.getAllowance() + taskObject.getGoal().getAmount());
            StringEntity entity = new StringEntity(signInJson.toString());
            Utils.logDebug("GoalJson", signInJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            httpClient.addHeader("Authorization", basicAuth);
            httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
            PersistentCookieStore myCookieStore = new PersistentCookieStore(parentTaskApproval);
            httpClient.setCookieStore(myCookieStore);
            httpClient.post(parentTaskApproval, AppConstant.BASE_URL + AppConstant.ADD_GOAL, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    showToast("Goal updated for " + childObject.getFirstName());

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    showToast("Goal updated for " + childObject.getFirstName());

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    //   unLockScreen();
                    //  showToast("Goal updated for " + childObject.getFirstName());
                    showToast("Goal updated for " + statusCode);


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    //   unLockScreen();
                    showToast("Goal updated for " + statusCode);

                }

            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
