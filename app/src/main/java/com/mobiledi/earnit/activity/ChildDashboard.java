package com.mobiledi.earnit.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobiledi.earnit.MyApplication;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.adapter.ChildViewDateAdapter;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.DayTaskStatus;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.RestCall;
import com.mobiledi.earnit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;


public class ChildDashboard extends BaseActivity {
    private static final int FEB_ICON_SIZE = 15;
    ChildDashboard childDashboard;
    private Child childObject;
    @BindView(R.id.child_task_date_list)
    RecyclerView childTaskDateList;
    @BindView(R.id.child_dashboard_avatar)
    CircularImageView childImage;
    private ChildViewDateAdapter childViewDateAdapter;
    @BindView(R.id.loadingPanel)
    RelativeLayout progress;
    @BindView(R.id.child_dashboard_header)
    TextView childDashboardHeader;
    int bCount = 0;
    long time;
    Handler handler;
    Runnable runnable;
    private Parent parentObject;
    String TAG = ChildDashboard.class.getSimpleName();
    ArrayList<Tasks> taskList;
    private ArrayList<ChildsTaskObject> childTaskObjects;
    private boolean openFromCalendar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_dashboard);
        ButterKnife.bind(this);
        childDashboard = this;
        taskList = new ArrayList<>();
        //SERIALIZE OBJECT FROM INTENT OBJECT
        Intent intent = getIntent();
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        openFromCalendar = intent.getBooleanExtra("openFromCalendar", false);
        //SET PROFILE IMAGE

        childDashboardHeader.setText(getResources().getString(R.string.my_task));
        Log.e(TAG, "Child objcet getting");
        Log.e(TAG, "CHILD ID= " + childObject.getId());

        if (childObject != null) {

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.override(350, 350);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.placeholder(R.drawable.default_avatar);
            requestOptions.error(R.drawable.default_avatar);
            Log.e(TAG, AppConstant.AMAZON_URL + childObject.getAvatar());

            Glide.with(this).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL + childObject.getAvatar())
                    .into(childImage);

            Log.e(TAG, "Child objcet is not null");
            Log.e(TAG, AppConstant.AMAZON_URL + childObject.getAvatar());
            Log.e(TAG, AppConstant.AMAZON_URL + childObject);
            Log.e(TAG, childObject.getAvatar());
        } else
            Log.e(TAG, "Child objcet is null.......");

    /*    try {
            Picasso.with(childDashboard.getApplicationContext()).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(childImage);
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(childDashboard.getApplicationContext()).load(R.drawable.default_avatar).into(childImage);
        }*/

        // callRetrofit();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        childTaskDateList.setLayoutManager(mLayoutManager);
        childTaskDateList.setItemAnimator(new DefaultItemAnimator());
        final AsyncHttpClient client = new AsyncHttpClient();
        String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
        final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
        client.addHeader("Authorization", basicAuth);
        client.setBasicAuth(childObject.getEmail(), childObject.getPassword());
        Log.e(TAG, "API getChildTasks: " + AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + childObject.getId());
        client.get(AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + childObject.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("fksdjhfo", "response = " + response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Log.e(TAG, "OBJECT= " + object);
                        //TASKS
                        Tasks task = new GetObjectFromResponse().getTaskObject(object, childObject.getId());
                        Log.e(TAG, "GetObjectFromResponse task: " + task.toString());
                        taskList.add(task);
                    } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getLocalizedMessage());

                    }
                    childObject.setTasksArrayList(taskList);
                }
            }
        });
        if (openFromCalendar) {
            Log.d("fdslfjj", "openFromCalendar");
            childTaskObjects = new ArrayList<>();
            ChildsTaskObject childsTaskObject = new ChildsTaskObject();
            ArrayList<Tasks> tasksList = new ArrayList<>();
            for (Tasks task : childObject.getTasksArrayList()) {
                Log.d("fdslfjj", "addTask: " + task.toString());
                tasksList.add(task);
                childsTaskObject.setDueDate(new DateTime(task.getDueDate()).toString());
            }
            childsTaskObject.setTasks(tasksList);
            childTaskObjects.add(childsTaskObject);
        } else {
            Log.d("fdslfjj", "open NE FromCalendar");
            childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(childObject);
        }
        for (ChildsTaskObject childsTaskObject : childTaskObjects) {
            Utils.logDebug("aslkdjlk", "childTaskObjects = " + childsTaskObject.toString());
        }
        MyApplication.getInstance().setChildsTaskObjects(childTaskObjects);

        if (childTaskObjects.size() > 0) {
            childViewDateAdapter = new ChildViewDateAdapter(childTaskObjects, parentObject, childObject, "child");
            Log.d("dasagsdg", "childTaskObjects: " + childTaskObjects);
            childTaskDateList.setAdapter(childViewDateAdapter);
        } else showToast(getResources().getString(R.string.please_ask_parent_to_add_task));
        childImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FloatingMenu(childDashboard).fetchAvatarDimension253(childTaskObjects, childImage, childObject, parentObject, AppConstant.CHILD_DASHBOARD_SCREEN, progress, null);
            }
        });
        callApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("sdjfhkj", "onResume(); isAlreadySentAppUsage = " + RestCall.isAlreadySentAppUsage() + "; isPermissionEnabled = " + isPermissionEnabled());
            if (!isPermissionEnabled()) {
                Log.d("sdjfhkj", "showAlertDialog()");
                showAlertDialog();
            }
//            if (!RestCall.isAlreadySentAppUsage() && isPermissionEnabled()) {
//                Log.d("sdjfhkj", "dddddd()");
//                RestCall.updateAppsUsage();
//            }
            RestCall.updateAppsUsage();
        }
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.enable_apps_usage_permission);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    private boolean isPermissionEnabled() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        assert appOps != null;
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    public void onBackPressed() {
        bCount++;


        if (bCount == 1) {
            time = System.currentTimeMillis();
            showToast(getResources().getString(R.string.back_to_exit));
        } else {
            if (System.currentTimeMillis() - time > 4000) {

                bCount = 0;
                showToast(getResources().getString(R.string.back_to_exit));
            } else {
                finish();
            }

        }
    }


    public void callApi() {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                try {
                    new RestCall(childDashboard).authenticateUser(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword()
                            , null, AppConstant.CHILD_DASHBOARD_SCREEN, progress);
                } catch (Exception e) {
                }
            }
        };
        handler.postDelayed(runnable, AppConstant.REFRESH_INTERVAL);
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    /*private void callRetrofit() {

        RetroInterface retroInterface = RetrofitClient.getApiServices(childObject.getEmail(), childObject.getPassword());
        Call<GetTaskResponse> responseCall = retroInterface.getTasks();

        responseCall.enqueue(new Callback<GetTaskResponse>() {
            @Override
            public void onResponse(Call<GetTaskResponse> call, Response<GetTaskResponse> response) {
                Log.e(TAG, "Success");
                Log.e(TAG, "Response= "+response.message());
                Log.e(TAG, "Response= "+response.code());
                Log.e(TAG, "Response= "+response.body().getEmbedded().getDayTaskStatuses().get(0).getCreatedDateTime());
                Log.e(TAG, "Response= "+response.body().getEmbedded().getDayTaskStatuses().get(0).getStatus());

            }

            @Override
            public void onFailure(Call<GetTaskResponse> call, Throwable t) {
                Log.e(TAG, "Failure");

            }
        });


    }*/


}
