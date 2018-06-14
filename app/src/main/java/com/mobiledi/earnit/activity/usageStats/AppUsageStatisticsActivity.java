package com.mobiledi.earnit.activity.usageStats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.MyApplication;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.AppUsageResponse;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.NavigationDrawer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AppUsageStatisticsActivity extends AppCompatActivity implements AppUsageStatisticsFragment.OnViewReady {

    @BindView(R.id.drawerBtn)
    ImageButton drawerToggle;
    @BindView(R.id.toolbar)
    Toolbar goalToolbar;
    @BindView(R.id.addtask_back_arrow)
    ImageButton backbtn;
    @BindView(R.id.add_task_header)
    TextView add_task_header;
    @BindView(R.id.add_task_avatar)
    CircularImageView childAvatar;
    Parent parent;
    Child childObject;
    @BindView(R.id.loadingPanel)
    RelativeLayout progressBar;
    String TAG = AppUsageStatisticsActivity.class.getSimpleName();
    private AppUsageStatisticsFragment appUsageStatisticsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("sdlfkjslk", "onCreate");
        appUsageStatisticsFragment = AppUsageStatisticsFragment.newInstance(this);

        setContentView(R.layout.activity_app_usage_statistics);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, AppUsageStatisticsFragment.newInstance(this))
                    .commit();
        }
        Intent intent = getIntent();
        parent = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        if (childObject != null)
        MyApplication.getInstance().setChildId(childObject.getId());
        Log.d("jdsahdkjh", "AppUsageStatisticsActivity. Child: " + childObject);


        if (childObject != null) {

            Glide.with(this).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).into(childAvatar);


        }

        backbtn.setVisibility(View.GONE);
        NavigationDrawer navigationDrawer = new NavigationDrawer(AppUsageStatisticsActivity.this, parent, goalToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, 0);
        add_task_header.setText(parent.getFirstName());
    }

    @OnClick(R.id.add_task_avatar)
    void floatingMenu() {
        new FloatingMenu(this).fetchAvatarDimension(childAvatar, childObject, childObject, parent, AppConstant.ADD_TASK, progressBar, null);
    }

//    private void updateRecyclerView() {
//        SharedPreferences sp = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
//        RetroInterface retroInterface = ServiceGenerator.createService(RetroInterface.class, sp.getString(AppConstant.EMAIL, ""), sp.getString(AppConstant.PASSWORD, ""));
//        Map<String, Integer> options = new HashMap<>();
//        options.put("childid", childObject.getId());
//        options.put("days", 7);
////        Call<ResponseBody> getAppUsage = retroInterface.getAppsUsage(options);
////        getAppUsage.enqueue(new Callback<ResponseBody>() {
////            @Override
////            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
////                Log.d("sdlfkjslk", "onResponse");
////                if (response.body() != null) {
////                    try {
////                        Log.d("sdlfkjslk", "body != null. size = " + response.body().string());
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                } else {
////                    Log.d("sdlfkjslk", "body == null");
////                }
////            }
////
////            @Override
////            public void onFailure(Call<ResponseBody> call, Throwable t) {
////                Log.d("sdlfkjslk", "Throwable = " + t.getLocalizedMessage());
////            }
////        });
//        Call<List<AppUsageResponse>> getAppsUsage = retroInterface.getAppsUsage(options);
//        getAppsUsage.enqueue(new Callback<List<AppUsageResponse>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<AppUsageResponse>> call, @NonNull Response<List<AppUsageResponse>> response) {
//                if (response.body() != null) {
//                    Log.d("sdlfkjslk", "body != null. size = " + response.body().size());
//                    for (AppUsageResponse appUsageResponse : response.body()) {
//                        Log.d("sdlfkjslk", "appUsageResponse: " + appUsageResponse.toString());
//                    }
//                    appUsageStatisticsFragment.updateRecyclerView(response.body());
//                } else {
//                    Log.d("sdlfkjslk", "body == null");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<AppUsageResponse>> call, @NonNull Throwable t) {
//
//            }
//        });
//    }

    @Override
    public void onReady() {
//        updateRecyclerView();
    }
}