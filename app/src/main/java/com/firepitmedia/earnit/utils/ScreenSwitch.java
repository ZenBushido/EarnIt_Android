package com.firepitmedia.earnit.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.activity.AddChild;
import com.firepitmedia.earnit.activity.AddTask;
import com.firepitmedia.earnit.activity.BalanceAdjustment;
import com.firepitmedia.earnit.activity.BalanceActivity;
import com.firepitmedia.earnit.activity.ChildCalendarActivity;
import com.firepitmedia.earnit.activity.ChildDashboard;
import com.firepitmedia.earnit.activity.ChildMessage;

import com.firepitmedia.earnit.activity.EditTask;
import com.firepitmedia.earnit.activity.GoalActivity;
import com.firepitmedia.earnit.activity.InitialParentProfile;
import com.firepitmedia.earnit.activity.LoginScreen;
import com.firepitmedia.earnit.activity.ParentCalendarActivity;
import com.firepitmedia.earnit.activity.ParentCheckInChildDashboard;
import com.firepitmedia.earnit.activity.ParentDashboard;
import com.firepitmedia.earnit.activity.ParentProfile;
import com.firepitmedia.earnit.activity.ParentTaskApproval;
import com.firepitmedia.earnit.activity.RequestTransfer;
import com.firepitmedia.earnit.activity.usageStats.AppUsageStatisticsActivity;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Goal;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.service.SendMessageService;
import com.firepitmedia.earnit.service.UpdateFcmToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;
import retrofit.GetGoalsFromChildInterface;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mobile-di on 12/10/17.
 */

public class ScreenSwitch {
    private final String TAG = "ScreenSwitch";
    Activity activity;
    ArrayList<Goal> goalList = new ArrayList<>();

    public ScreenSwitch(Activity activity) {
        this.activity = activity;
    }


    public void sendMessage(View view, final Child childObject) {
        final Dialog dialog = new Dialog(view.getRootView().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.9f;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.message_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView messageTitle = (TextView) dialog.findViewById(R.id.message_title_id);
        messageTitle.setText("Message to " + childObject.getFirstName() + ":");
        final EditText messageBox = (EditText) dialog.findViewById(R.id.message_box);
        Button declineButton = (Button) dialog.findViewById(R.id.message_cancel);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        Button acceptButton = (Button) dialog.findViewById(R.id.message_send);
        acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (messageBox.getText().toString().trim().length() > 0) {
                    AppConstant.MESSAGE_STATUS = true;
                    Intent sendMessage = new Intent(activity, SendMessageService.class);
                    sendMessage.putExtra(AppConstant.CHILD_OBJECT, childObject);
                    sendMessage.putExtra("mStatus", true);
                    sendMessage.putExtra(AppConstant.MESSAGE, messageBox.getText().toString());
                    activity.startService(sendMessage);
                    dialog.dismiss();
                } else
                    Toast.makeText(activity, "Please enter message first", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

    public void moveToAllTaskScreen(Child child, Child otherChild, String onScreen, Parent parent, String fromScreen) {
        Intent parentCheckinChildDashboard = new Intent(activity, ParentCheckInChildDashboard.class);
       // parentCheckinChildDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        parentCheckinChildDashboard.putExtra(AppConstant.CHILD_OBJECT, child);
        parentCheckinChildDashboard.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
        parentCheckinChildDashboard.putExtra(AppConstant.PARENT_OBJECT, parent);
        parentCheckinChildDashboard.putExtra(AppConstant.SCREEN, onScreen);
        parentCheckinChildDashboard.putExtra(AppConstant.FROM_SCREEN, fromScreen);
        activity.startActivity(parentCheckinChildDashboard);
    }

    public void moveToAddTask(Child child, Child childWithAllTask, Parent parent, String screen, Tasks tasks) {

        if (tasks != null) {
            Intent addTask = new Intent(activity, EditTask.class);
            addTask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            addTask.putExtra(AppConstant.CHILD_OBJECT, child);
            addTask.putExtra(AppConstant.OTHER_CHILD_OBJECT, childWithAllTask);
            addTask.putExtra(AppConstant.FROM_SCREEN, screen);
            addTask.putExtra(AppConstant.PARENT_OBJECT, parent);
            addTask.putExtra(AppConstant.TO_EDIT, (Serializable) tasks);
            addTask.putExtra(AppConstant.REPITITION_SCHEDULE, tasks.getRepititionSchedule());
            addTask.putExtra(AppConstant.TASK_STATUS, AppConstant.EDIT);
            addTask.putExtra("testing", "Edit");

            activity.startActivity(addTask);

        } else {
            Intent addTask = new Intent(activity, AddTask.class);
            addTask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            addTask.putExtra(AppConstant.CHILD_OBJECT, child);
            addTask.putExtra(AppConstant.OTHER_CHILD_OBJECT, childWithAllTask);
            addTask.putExtra(AppConstant.FROM_SCREEN, screen);
            addTask.putExtra(AppConstant.PARENT_OBJECT, parent);
            addTask.putExtra(AppConstant.TASK_STATUS, AppConstant.ADD);
            activity.startActivity(addTask);
        }
    }

    public void isGoalExists(final Child child, final Child otherChild, final Parent parent, final RelativeLayout progressBar, final String fromScreen, final Tasks tasks) {

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            client.addHeader("Authorization", basicAuth);
            client.setBasicAuth(parent.getEmail(), parent.getPassword());
            client.get(AppConstant.BASE_URL + AppConstant.GOAL_API + child.getId(), null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Intent goalDashboard = new Intent(activity, GoalActivity.class);
                    goalDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    goalDashboard.putExtra(AppConstant.CHILD_OBJECT, child);
                    goalDashboard.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
                    goalDashboard.putExtra(AppConstant.PARENT_OBJECT, parent);
                    goalDashboard.putExtra(AppConstant.FROM_SCREEN, fromScreen);
                    goalDashboard.putExtra(AppConstant.TO_EDIT, (Serializable) tasks);


                    try {
                        if (response.length() > 0 && (response.getJSONObject(0).get(AppConstant.ID) instanceof Integer)) {

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    if (object.has(AppConstant.ID)) {
                                        Goal goal = new GetObjectFromResponse().getGoalObject(object);
                                        goalList.add(goal);
                                        Log.i("goal-responsel1", String.valueOf(goalList.size()));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            goalDashboard.putExtra(AppConstant.MODE, AppConstant.UPDATE);
                            goalDashboard.putExtra(AppConstant.GOAL, goalList.get(0));
                            goalDashboard.putExtra("GoalList", goalList);
                        } else {
                            goalDashboard.putExtra(AppConstant.MODE, AppConstant.SAVE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    activity.startActivity(goalDashboard);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkBalance(final Child child, final Child otherChild, final Parent parent, final String fromScreen, final Tasks tasks, final RelativeLayout progressBar, final String userType) {
        GetGoalsFromChildInterface getTaskFromChildInterface = ServiceGenerator.createService(GetGoalsFromChildInterface.class, parent.getEmail(), parent.getPassword());
        retrofit2.Call<List<Goal>> taskV2Models = getTaskFromChildInterface.listRepos(child.getId());
        taskV2Models.enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Goal>> call, Response<List<Goal>> response) {
                ArrayList<Goal> goals = new ArrayList<>();
                for (int i = 0; i < response.body().size(); i++) {

                    Goal goal = new Goal();
                    goal.setId(response.body().get(i).getId());
                    goal.setAmount(response.body().get(i).getAmount());
                    goal.setTally(response.body().get(i).getTally());
                    goal.setCash(response.body().get(i).getCash());
                    goal.setCreateDate(response.body().get(i).getCreateDate());
                    goal.setUpdateDate(response.body().get(i).getUpdateDate());
                    goal.setTallyPercent(response.body().get(i).getTallyPercent());
                    goal.setGoalName(response.body().get(i).getGoalName());
goals.add(goal);

                }
                goalList=goals;
                moveToBalance(child, otherChild, parent, fromScreen, tasks, goalList, userType);


            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable throwable) {

            }
        });
    }

    public void moveToBalance(Child child, Child otherChild, Parent parent, String fromScreen, Tasks tasks, List<Goal> goal, String userType) {
        Intent moveToBalance = new Intent(activity, BalanceAdjustment.class);
        moveToBalance.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        moveToBalance.putExtra(AppConstant.CHILD_OBJECT, child);
        moveToBalance.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
        moveToBalance.putExtra(AppConstant.PARENT_OBJECT, parent);
        moveToBalance.putExtra(AppConstant.FROM_SCREEN, fromScreen);
        moveToBalance.putExtra(AppConstant.TO_EDIT, (Serializable) tasks);
        moveToBalance.putExtra(AppConstant.GOAL_OBJECT, (Serializable) goal);
        moveToBalance.putExtra(AppConstant.TYPE, userType);
        activity.startActivity(moveToBalance);
    }

    public void moveToRequestTransfer(Child child, Child otherChild, Parent parent, String fromScreen, Tasks tasks, List<Goal> goal, String userType) {
        Intent moveToBalance = new Intent(activity, RequestTransfer.class);
        moveToBalance.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        moveToBalance.putExtra(AppConstant.CHILD_OBJECT, child);
        moveToBalance.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
        moveToBalance.putExtra(AppConstant.PARENT_OBJECT, parent);
        moveToBalance.putExtra(AppConstant.FROM_SCREEN, fromScreen);
        moveToBalance.putExtra(AppConstant.TO_EDIT, (Serializable) tasks);
        moveToBalance.putExtra(AppConstant.GOAL_OBJECT, (Serializable) goal);
        moveToBalance.putExtra(AppConstant.TYPE, userType);
        activity.startActivity(moveToBalance);
    }

    public void moveToBalanceScreen(Child child, Child otherChild, Parent parent, String fromScreen, Tasks tasks, List<Goal> goal, String userType) {
        Intent moveToBalance = new Intent(activity, BalanceActivity.class);
        moveToBalance.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        moveToBalance.putExtra(AppConstant.CHILD_OBJECT, child);
        moveToBalance.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
        moveToBalance.putExtra(AppConstant.PARENT_OBJECT, parent);
        moveToBalance.putExtra(AppConstant.FROM_SCREEN, fromScreen);
        moveToBalance.putExtra(AppConstant.TO_EDIT, (Serializable) tasks);
        moveToBalance.putExtra(AppConstant.GOAL_OBJECT, (Serializable) goal);
        moveToBalance.putExtra(AppConstant.TYPE, userType);
        activity.startActivity(moveToBalance);
    }
    public void moveToParentDashboard(Parent parent) {
        Intent intent = new Intent(activity, ParentDashboard.class);
        intent.putExtra(AppConstant.PARENT_OBJECT, parent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public void moveToParentCalendar(Parent parent) {
        Intent intent = new Intent(activity, ParentCalendarActivity.class);
        intent.putExtra(AppConstant.PARENT_OBJECT, parent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public void moveToChildCalendar(Parent parent) {
        Intent intent = new Intent(activity, ChildCalendarActivity.class);
        intent.putExtra(AppConstant.PARENT_OBJECT, parent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }


    public void moveToChildDashboard(Child childObject, RelativeLayout progressBar) {
        new RestCall(activity).authenticateUser(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword(), null, AppConstant.CHILD_DASHBOARD_SCREEN, progressBar);

    }


    public void moveToParentProfile(int childId, Parent parentObject, String switchFrom) {
        Intent moveToLogin = new Intent(activity, ParentProfile.class);
        moveToLogin.putExtra(AppConstant.PARENT_OBJECT, parentObject);
        moveToLogin.putExtra(AppConstant.SCREEN, switchFrom);
        moveToLogin.putExtra(AppConstant.CHILD_ID, childId);
        moveToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(moveToLogin);
    }

    public void updateFCMToken(Child child, String token) {
        Intent updateToken = new Intent(activity, UpdateFcmToken.class);
        updateToken.putExtra(AppConstant.CHILD_OBJECT, child);
        updateToken.putExtra(AppConstant.MODE, AppConstant.CHILD);
        updateToken.putExtra(AppConstant.FCM_TOKEN, token);
        activity.startService(updateToken);
    }

    public void moveTOChildDashboard(Child child, boolean openFromCalendar) {
        Intent childDashboard = new Intent(activity, ChildDashboard.class);
        childDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        childDashboard.putExtra(AppConstant.CHILD_OBJECT, child);
        childDashboard.putExtra("openFromCalendar", openFromCalendar);
        activity.startActivity(childDashboard);
    }

    public void sendMessageToChild(String message, Child childObject) {
        Intent sendMessage = new Intent(activity, SendMessageService.class);
        sendMessage.putExtra(AppConstant.CHILD_OBJECT, childObject);
        sendMessage.putExtra(AppConstant.MESSAGE, message);
        activity.startService(sendMessage);
    }


    public void moveToAddChild(Parent parentObject, int childID, String switchFrom, String mode, Child child) {
        Intent moveToAddChild = new Intent(activity, AddChild.class);
        moveToAddChild.putExtra(AppConstant.MODE, mode);
        moveToAddChild.putExtra(AppConstant.PARENT_OBJECT, parentObject);
        moveToAddChild.putExtra(AppConstant.SCREEN, switchFrom);
        moveToAddChild.putExtra(AppConstant.CHILD_OBJECT, child);
        moveToAddChild.putExtra(AppConstant.CHILD_ID, childID);
        moveToAddChild.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(moveToAddChild);
    }


    public void moveToLogin() {
        Intent moveToLogin = new Intent(activity, LoginScreen.class);
        moveToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(moveToLogin);
    }

    public void moveToAddTask() {
        Intent moveToaddtask = new Intent(activity, AddTask.class);
        moveToaddtask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(moveToaddtask);
    }

    public void moveToInitialParentProfile(Parent parent, String password) {
        Intent intent = new Intent(activity, InitialParentProfile.class);
        intent.putExtra(AppConstant.PARENT_OBJECT, parent);
        intent.putExtra(AppConstant.PASSWORD, password);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }


    public void moveToMessage(Child child) {
        Intent childDashboard = new Intent(activity, ChildMessage.class);
        childDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        childDashboard.putExtra(AppConstant.CHILD_OBJECT, child);
        activity.startActivity(childDashboard);
    }


    public void moveToTaskApproval(Child child, Child otherChild, Parent parent, String fromScreen, Tasks tasks) {


        Intent moveToTaskApproval = new Intent(activity, ParentTaskApproval.class);
       // moveToTaskApproval.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        moveToTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
        moveToTaskApproval.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
        moveToTaskApproval.putExtra(AppConstant.PARENT_OBJECT, parent);
        moveToTaskApproval.putExtra(AppConstant.FROM_SCREEN, fromScreen);
        moveToTaskApproval.putExtra(AppConstant.TASK_OBJECT, (Serializable) tasks);
        if(tasks!=null)
        {
            Log.e(TAG, "Tasks is not null....");
            moveToTaskApproval.putExtra(AppConstant.REPITITION_SCHEDULE, tasks.getRepititionSchedule());
            moveToTaskApproval.putExtra(AppConstant.TASK_COMMENTS, tasks.getTaskComments().get(0));
        }
        else {
            Log.e(TAG, "TASK IS NULL.....");
        }

        activity.startActivity(moveToTaskApproval);
    }


    public void usageStats(Parent parent, String fromScreen, Child child) {

        //activity.startActivity(new Intent(activity, AppUsageStatisticsActivity.class));

        Intent moveToTaskApproval = new Intent(activity, AppUsageStatisticsActivity.class);
        // moveToTaskApproval.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.d("jdsahdkjh", "ScreenSwitch. Child: " + child);
        moveToTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
        moveToTaskApproval.putExtra(AppConstant.PARENT_OBJECT, parent);
        moveToTaskApproval.putExtra(AppConstant.FROM_SCREEN, fromScreen);

        activity.startActivity(moveToTaskApproval);
    }
}