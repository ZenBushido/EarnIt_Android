package com.mobiledi.earnit.activity.usageStats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.extras.Base64;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    private UsageListAdapter mUsageListAdapter;

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

        backbtn.setVisibility(View.GONE);
        NavigationDrawer navigationDrawer = new NavigationDrawer(AppUsageStatisticsActivity.this, parent, goalToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, 0);
        add_task_header.setText(parent.getFirstName());

        updateAvatar(childObject, childAvatar);
    }

    private void updateAvatar(Child child, ImageView imageView) {
        String url = AppConstant.BASE_URL + "/" + child.getAvatar();
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
                .load(url)
                .error(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.default_avatar)))
                .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.default_avatar)))
                .into(imageView);
    }

    @OnClick(R.id.add_task_avatar)
    void floatingMenu() {
        new FloatingMenu(this).fetchAvatarDimension(childAvatar, childObject, childObject, parent, AppConstant.ADD_TASK, progressBar, null);
    }

    private List<CustomUsageStats> sortingList(List<CustomUsageStats> list) {
        Collections.sort(list, new Comparator<CustomUsageStats>() {
            public int compare(CustomUsageStats obj1, CustomUsageStats obj2) {
                // ## Ascending order
                return Long.compare(obj1.getTotalTimeInForeground(), obj2.getTotalTimeInForeground());
            }
        });
        List<CustomUsageStats> sortedList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            CustomUsageStats customUsageStats = list.get(i);
            if (customUsageStats.getTotalTimeInForeground() > 0)
                sortedList.add(customUsageStats);
        }
        return sortedList;
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