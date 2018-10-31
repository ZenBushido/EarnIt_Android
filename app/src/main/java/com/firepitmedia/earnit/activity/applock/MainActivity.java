package com.firepitmedia.earnit.activity.applock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firepitmedia.earnit.AppLockConstants;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.activity.usageStats.CustomUsageStats;
import com.firepitmedia.earnit.activity.usageStats.UsageListAdapter;
import com.firepitmedia.earnit.adapter.applock_adapter.ChildrenAppsAdapter;
import com.firepitmedia.earnit.model.AppUsageResponse;
import com.firepitmedia.earnit.model.Data.AppInfo;
import com.firepitmedia.earnit.model.newModels.AppsToBeBlockedOnOverdue;
import com.firepitmedia.earnit.retrofit.RetroInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class MainActivity extends AppCompatActivity {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    long numOfTimesAppOpened = 0;
    boolean isRated = false;

    private RecyclerView mRecyclerView;
    private ChildrenAppsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String requiredTypes;
    private Toolbar toolbar;
    private UsageListAdapter mUsageListAdapter;
    private List<CustomUsageStats> mCustomUsageStats;

    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.tvListIsEmpty)
    TextView tvListIsEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();
        new com.firepitmedia.earnit.SharedPreference().clearLockApps(this);
        new com.firepitmedia.earnit.SharedPreference().clearLockAppsObjects(this);
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        numOfTimesAppOpened = sharedPreferences.getLong(AppLockConstants.NUM_OF_TIMES_APP_OPENED, 0) + 1;
        isRated = sharedPreferences.getBoolean(AppLockConstants.IS_RATED, false);
        editor.putLong(AppLockConstants.NUM_OF_TIMES_APP_OPENED, numOfTimesAppOpened);
        editor.apply();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        setSupportActionBar(toolbar);

        requiredTypes = AppLockConstants.ALL_APPS;

        /*if ((MyUtils.isInternetConnected(getApplicationContext()))) {
            Toast.makeText(this,"Network is available",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Network is not available",Toast.LENGTH_SHORT).show();
        }*/

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new ApplicationListAdapter(MainActivity.getListOfInstalledApp(this), this, requiredTypes);
//        mRecyclerView.setAdapter(mAdapter);
//        updateRecyclerView();
        updateList();
    }

    private List<AppInfo> convertList(List<CustomUsageStats> customUsageStats) {
        List<AppInfo> returnList = new ArrayList<>();
        for (CustomUsageStats cus : customUsageStats) {
            Log.d("dsfsdh", "cus: " + cus.toString());
            returnList.add(AppInfo.from(cus));
        }
        return returnList;
    }

    public void updateList(){
        pb.setVisibility(View.VISIBLE);
        RetroInterface retroInterface = ServiceGenerator.createService(RetroInterface.class, MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());
        retroInterface.getChildrenApps(MyApplication.getInstance().getChildId()).enqueue(new Callback<List<AppsToBeBlockedOnOverdue>>() {
            @Override
            public void onResponse(@NonNull Call<List<AppsToBeBlockedOnOverdue>> call, @NonNull Response<List<AppsToBeBlockedOnOverdue>> response) {
                pb.setVisibility(View.GONE);
                if (response.body() != null){
                    List<AppsToBeBlockedOnOverdue> filterList = new ArrayList<>();
                    if (response.body().size() > 0) {
                        for (AppsToBeBlockedOnOverdue app : response.body()) {
                            if (app.getName().contains("#")){
                                filterList.add(app);
                            }
                        }
                        if (filterList.size() > 0) {
                            mAdapter = new ChildrenAppsAdapter(filterList, context);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            tvListIsEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvListIsEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AppsToBeBlockedOnOverdue>> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
                Toast.makeText(context, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("daskjdl", "Throwable: " + t.getLocalizedMessage());
            }
        });

    }

    @Deprecated
    public void updateRecyclerView() {
        pb.setVisibility(View.VISIBLE);
        RetroInterface retroInterface = ServiceGenerator.createService(RetroInterface.class, MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());
        Map<String, Integer> options = new HashMap<>();
        options.put("childid", MyApplication.getInstance().getChildId());
        options.put("days", 7);
        Call<List<AppUsageResponse>> getAppsUsage = retroInterface.getAppsUsage(options);
        getAppsUsage.enqueue(new Callback<List<AppUsageResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AppUsageResponse>> call, @NonNull Response<List<AppUsageResponse>> response) {
                pb.setVisibility(View.GONE);
                if (response.body() != null) {
                    long totalTime = 0;
                    Log.d("sdlfkjslk", "body != null. size = " + response.body().size());
                    List<CustomUsageStats> appUsages = new ArrayList<>();
                    for (AppUsageResponse appUsageResponse : response.body()) {
                        totalTime += appUsageResponse.getTimeUsedMinutes();
                        Log.d("sdlfkjslk", "appUsageResponse: " + appUsageResponse.toString());
                        if (!appUsageResponse.getAppName().contains("EarnIt")) {
                            appUsages.add(new CustomUsageStats().from(appUsageResponse));
                        }
                    }
                    if (appUsages.isEmpty()) {
                        tvListIsEmpty.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("sdlfkjslk", "totalTime minutes: " + totalTime);
                        totalTime = TimeUnit.MINUTES.toMillis(totalTime);
                        Log.d("sdlfkjslk", "totalTime millis: " + totalTime);
//                        mAdapter = new ApplicationListAdapter(convertList(AppUsageStatisticsFragment.sortingList(appUsages)), MainActivity.this, requiredTypes);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    tvListIsEmpty.setVisibility(View.VISIBLE);
                    Log.d("sdlfkjslk", "body == null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AppUsageResponse>> call, @NonNull Throwable t) {
                tvListIsEmpty.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
        });
    }

    public static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> installedApps = new ArrayList<>();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.SIGNATURE_MATCH);
        if (apps != null && !apps.isEmpty()) {

            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                ApplicationInfo appInfo = null;
                try {
                    appInfo = packageManager.getApplicationInfo(p.packageName, 0);
                    AppInfo app = new AppInfo();
                    app.setName(p.applicationInfo.loadLabel(packageManager).toString());
                    app.setPackageName(p.packageName);
                    app.setVersionName(p.versionName);
                    app.setVersionCode(p.versionCode);
                    app.setIcon(p.applicationInfo.loadIcon(packageManager));

                    installedApps.add(app);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return installedApps;
        }
        return null;
    }
}
