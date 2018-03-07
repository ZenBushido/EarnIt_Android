package com.mobiledi.earnit.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.adapter.ChildViewDateAdapter;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.TaskV2Model;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.retrofit.RetrofitClient;
import com.mobiledi.earnit.service.UpdateFcmToken;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.RestCall;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChildDashboard extends BaseActivity {
    private static final int FEB_ICON_SIZE = 15;
    ChildDashboard childDashboard;
    private Child childObject;
    private RecyclerView childTaskDateList;
    private CircularImageView childImage;
    private ChildViewDateAdapter childViewDateAdapter;
    private RelativeLayout progress;
    private TextView childDashboardHeader;
    int bCount = 0;
    long time;
    Handler handler;
    Runnable runnable;
    private Parent parentObject;
    String TAG = ChildDashboard.class.getSimpleName();
    ArrayList<Tasks> taskList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_dashboard);
        childDashboard = this;
        taskList = new ArrayList<>();
        //SERIALIZE OBJECT FROM INTENT OBJECT
        Intent intent = getIntent();
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        //SET PROFILE IMAGE
        childImage = (CircularImageView) findViewById(R.id.child_dashboard_avatar);
        childDashboardHeader = (TextView) findViewById(R.id.child_dashboard_header);
        childDashboardHeader.setText(getResources().getString(R.string.my_task));
        try {
            Picasso.with(childDashboard.getApplicationContext()).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(childImage);
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(childDashboard.getApplicationContext()).load(R.drawable.default_avatar).into(childImage);
        }

       // callRetrofit();
        progress = (RelativeLayout) findViewById(R.id.loadingPanel);
        childTaskDateList = (RecyclerView) findViewById(R.id.child_task_date_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        childTaskDateList.setLayoutManager(mLayoutManager);
        childTaskDateList.setItemAnimator(new DefaultItemAnimator());
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(childObject.getEmail(), childObject.getPassword());
        client.get(AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + childObject.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {


                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);


                        //TASKS
                        Tasks task = new GetObjectFromResponse().getTaskObject(object, childObject.getId());
                        taskList.add(task);


                    } catch (Exception e) {
                        Log.e(TAG, "Error: "+e.getLocalizedMessage());

                    }
                    childObject.setTasksArrayList(taskList);
                }
            }
        });
        List<ChildsTaskObject> childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(childObject, AppConstant.CHILD, AppConstant.CHECKED_IN_SCREEN);

        if (childTaskObjects.size() > 0) {
            childViewDateAdapter = new ChildViewDateAdapter(childTaskObjects,parentObject,childObject,"child");
            childTaskDateList.setAdapter(childViewDateAdapter);
        } else showToast(getResources().getString(R.string.please_ask_parent_to_add_task));
        childImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FloatingMenu(childDashboard).fetchAvatarDimension(childImage, childObject, parentObject, AppConstant.CHILD_DASHBOARD_SCREEN, progress);
            }
        });
        callApi();

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
                    new RestCall(childDashboard).authenticateUser(childObject.getEmail(), childObject.getPassword()
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
