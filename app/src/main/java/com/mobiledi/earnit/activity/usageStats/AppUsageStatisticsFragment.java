package com.mobiledi.earnit.activity.usageStats;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;


import com.mobiledi.earnit.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AppUsageStatisticsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = AppUsageStatisticsFragment.class.getSimpleName();


    UsageStatsManager mUsageStatsManager;
    UsageListAdapter mUsageListAdapter;
    @BindView(R.id.recyclerview_app_usage)
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.button_open_usage_setting)
    Button button_open_usage;
    @BindView(R.id.spinner_time_span)
    Spinner mSpinner;


    public static AppUsageStatisticsFragment newInstance() {
        AppUsageStatisticsFragment fragment = new AppUsageStatisticsFragment();
        return fragment;
    }

    public AppUsageStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE ); //Context.USAGE_STATS_SERVICE // "usagestats"
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_usage_statistics, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        ButterKnife.bind(this , rootView);

        mUsageListAdapter = new UsageListAdapter(getActivity());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mUsageListAdapter);

        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.action_list, R.layout.item_spinner);
        mSpinner.setAdapter(spinnerAdapter);
        if(checkUsageStatEnable())
        {
            button_open_usage.setVisibility(View.GONE);
        }
        else
        {
            button_open_usage.setVisibility(View.VISIBLE);
        }
        mSpinner.setOnItemSelectedListener(this);

        String[] strings = getResources().getStringArray(R.array.action_list);

        StatsUsageInterval statsUsageInterval = StatsUsageInterval
                .getValue(strings[0]);

        if (statsUsageInterval != null) {
            List<UsageStats> usageStatsList =
                    getUsageStatistics(statsUsageInterval.mInterval);

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            updateAppsList(usageStatsList);
        }

    }

    private boolean checkUsageStatEnable() {

        try {
            PackageManager packageManager = getActivity().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getActivity().getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @OnClick(R.id.button_open_usage_setting)
    void usageStatSetting()
    {
        Log.e(TAG, "Clicked");
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public List<UsageStats> getUsageStatistics2(int intervalType) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void updateAppsList(List<UsageStats> usageStatsList) {
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            customUsageStats.usageStats = usageStatsList.get(i);
            customUsageStatsList.add(customUsageStats);
        }
        mUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
        mUsageListAdapter.notifyDataSetChanged();
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
