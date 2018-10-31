package com.firepitmedia.earnit.activity.usageStats;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.model.AppUsageResponse;
import com.firepitmedia.earnit.retrofit.RetroInterface;
import com.firepitmedia.earnit.utils.AppConstant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class AppUsageStatisticsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = AppUsageStatisticsFragment.class.getSimpleName();

    UsageStatsManager mUsageStatsManager;
    UsageListAdapter mUsageListAdapter;
    @BindView(R.id.recyclerview_app_usage)
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.spinner_time_span)
    Spinner mSpinner;
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.tvListIsEmpty)
    TextView tvListIsEmpty;

    private int mChildId;

    public interface OnViewReady{
        void onReady();
    }

    private static OnViewReady onViewReady;

    private static Context sContext;

    public static AppUsageStatisticsFragment newInstance(OnViewReady onViewReadys, int childId) {
        onViewReady = onViewReadys;
        if (onViewReadys instanceof AppUsageStatisticsActivity) {
            sContext = (Context) onViewReadys;
        }
        AppUsageStatisticsFragment fragment = new AppUsageStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt("childId", childId);
        fragment.setArguments(args);
        return fragment;
    }

    public AppUsageStatisticsFragment() {
        // Required empty public constructor
    }

//    public void updateRecyclerView(List<AppUsageResponse> appsUsageResponse){
//        List<CustomUsageStats> appUsages = new ArrayList<>();
//        for (AppUsageResponse appUsageResponse : appsUsageResponse){
//            appUsages.add(new CustomUsageStats().from(appUsageResponse));
//        }
//        mUsageListAdapter = new UsageListAdapter(sContext, appUsages);
//        mRecyclerView.setAdapter(mUsageListAdapter);
//    }

    public void updateRecyclerView(){
        SharedPreferences sp = getActivity().getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        RetroInterface retroInterface = ServiceGenerator.createService(RetroInterface.class, sp.getString(AppConstant.EMAIL, ""), sp.getString(AppConstant.PASSWORD, ""));
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
                    for (AppUsageResponse appUsageResponse : response.body()){
                        totalTime += appUsageResponse.getTimeUsedMinutes();
                        Log.d("sdlfkjslk", "appUsageResponse: " + appUsageResponse.toString());
                        appUsages.add(new CustomUsageStats().from(appUsageResponse));
                    }
                    if (appUsages.isEmpty()){
                        tvListIsEmpty.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("sdlfkjslk", "totalTime minutes: " + totalTime);
                        totalTime = TimeUnit.MINUTES.toMillis(totalTime);
                        Log.d("sdlfkjslk", "totalTime millis: " + totalTime);
                        mUsageListAdapter = new UsageListAdapter(sContext, sortingList(appUsages), totalTime);
                        mRecyclerView.setAdapter(mUsageListAdapter);
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

    public static List<CustomUsageStats> sortingList(List<CustomUsageStats> list) {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsageStatsManager = (UsageStatsManager) sContext
                .getSystemService(Context.USAGE_STATS_SERVICE); //Context.USAGE_STATS_SERVICE // "usagestats"
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_usage_statistics, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        ButterKnife.bind(this, rootView);

        Log.d("sdlfkjslk", "onViewCreated");

        assert getArguments() != null;
        mChildId = getArguments().getInt("childId", -1);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);
//        mRecyclerView.setAdapter(mUsageListAdapter);

        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.action_list, R.layout.item_spinner);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);

//        String[] strings = getResources().getStringArray(R.array.action_list);
//
//        StatsUsageInterval statsUsageInterval = StatsUsageInterval
//                .getValue(strings[0]);
//
//        if (statsUsageInterval != null) {
//            List<UsageStats> usageStatsList =
//                    getUsageStatistics(statsUsageInterval.mInterval);
//
//            updateAppsList(usageStatsList);
//        }

        if (onViewReady!= null) {
            Log.d("sdlfkjslk", "onViewReady!= null");
            onViewReady.onReady();
        }
        updateRecyclerView();
    }


    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                    getString(R.string.explanation_access_to_appusage_is_not_enabled),
                    Toast.LENGTH_LONG).show();

        }
        return queryUsageStats;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String[] strings = getResources().getStringArray(R.array.action_list);

        StatsUsageInterval statsUsageInterval = StatsUsageInterval
                .getValue(strings[position]);

        if (statsUsageInterval != null) {
            List<UsageStats> usageStatsList =
                    getUsageStatistics(statsUsageInterval.mInterval);

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            updateAppsList(usageStatsList);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    void updateAppsList(List<UsageStats> usageStatsList) {
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats;
            try {
                customUsageStats = new CustomUsageStats(getActivity(), usageStatsList.get(i));
                customUsageStats.usageStats = usageStatsList.get(i);
                customUsageStatsList.add(customUsageStats);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
//        mUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
//        mUsageListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }


    enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }
}
