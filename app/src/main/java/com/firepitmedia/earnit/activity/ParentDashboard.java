package com.firepitmedia.earnit.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.adapter.ChildrenAdapter;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.service.applock_service.AppCheckServices;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.GetObjectFromResponse;
import com.firepitmedia.earnit.utils.NavigationDrawer;
import com.firepitmedia.earnit.utils.RestCall;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.firepitmedia.earnit.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;

/**
 * Created by mradul on 7/5/17.
 */

public class ParentDashboard extends BaseActivity implements NavigationDrawer.OnDrawerToggeled {


    public final String TAG = "ParentDashboard";
    public static Parent parentObject;
    private ParentDashboard parentDashboard;

    ScreenSwitch screenSwitch;

    private TextView headerText;

    private Toolbar toolbar;
    //    private Button tipsBtn;
    private ImageButton drawerToggle;
    private AlphaAnimation buttonClickAnimation;
    private RelativeLayout progressBar;
    private List<Child> childList = new ArrayList<>();
    private List<Child> childApprovalList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChildrenAdapter mAdapter;

    //    public static Map<Integer, String> childs;
    int bCount = 0;
    long time;
    Handler handler;
    Runnable runnable;
    int childID = 0;
    String switchFrom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get the Parent Object

        if (isMyServiceRunning(AppCheckServices.class) && imRealParent()){
            stopServise();
        }

        Intent intent = getIntent();
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);


        screenSwitch = new ScreenSwitch( this);

        //Initialize FontAwesome
        Iconify.with(new FontAwesomeModule());
        //Animation for buttons
        buttonClickAnimation = new AlphaAnimation(1F, 0.1F);

        setContentView(R.layout.parent_landing_layout);
        parentDashboard = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerToggle = (ImageButton) findViewById(R.id.drawerBtn);
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        switchFrom = getIntent().getStringExtra(AppConstant.SCREEN);



        if(switchFrom == null)
            switchFrom = AppConstant.PARENT_DASHBOARD;

        recyclerView = (RecyclerView) findViewById(R.id.children_list);
        mAdapter = new ChildrenAdapter(this, childList, parentObject, progressBar);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        headerText = (TextView) toolbar.findViewById(R.id.textView);



        if (parentObject.getFirstName() != null) {
            if (parentObject.getFirstName().isEmpty())
                headerText.setText("Hi");
            else
                headerText.setText("Hi " + parentObject.getFirstName());
        }

        NavigationDrawer navigationDrawer = new NavigationDrawer(parentDashboard, parentObject, toolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, 0);
        if (isDeviceOnline()){
            getDashBoardData();
       // callApi();
        }

        navigationDrawer.setOnDrawerToggeled(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getDashBoardData() {

        Log.e(TAG, "Getting Dashboard Data");
        AsyncHttpClient client = new AsyncHttpClient();
        String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
        final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
        Utils.logDebug(TAG, "namePassword: " + namePassword);
        Utils.logDebug(TAG, "basicAuth: " + basicAuth);
        client.addHeader("Authorization", basicAuth);
        client.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        client.setMaxRetriesAndTimeout(3, 3000);
        Utils.logDebug(TAG, "Child response request: " +
                AppConstant.BASE_URL + AppConstant.CHILDREN_API + parentObject.getAccount().getId());
        client.get(AppConstant.BASE_URL + AppConstant.CHILDREN_API + parentObject.getAccount().getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Utils.logDebug(TAG, "Child Success response: " + response.toString());

                Log.e(TAG, "Child Respo: " + AppConstant.BASE_URL + AppConstant.CHILDREN_API + parentObject.getAccount().getId());

                childList.clear();
                childApprovalList.clear();

                if(response.length() != 0){
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject childObject = response.getJSONObject(i);
                            //child with non-approval task
                            Child child = new GetObjectFromResponse().getChildObject(childObject);

                            //child with approval task
                            Child childApprovalTask = new GetObjectFromResponse().getChildObject(childObject);
                            ArrayList<Tasks> taskList = new ArrayList<>();
                            ArrayList<Tasks> taskApprovalList = new ArrayList<>();

                            JSONArray taskArray = response.getJSONObject(i).getJSONArray(AppConstant.TASKS);


                            for (int taskIndex = 0; taskIndex < taskArray.length(); taskIndex++) {
                                JSONObject taskObject = taskArray.getJSONObject(taskIndex);
                                if (!taskObject.getString(AppConstant.STATUS).equals(AppConstant.APPROVED)) {
                                    Tasks task = new GetObjectFromResponse().getTaskObject(taskObject, childObject.getInt(AppConstant.ID));
                                    taskList.add(task);
                                }
                                if (taskObject.getString(AppConstant.STATUS).equals(AppConstant.COMPLETED)) {
                                    Tasks task = new GetObjectFromResponse().getTaskObject(taskObject, childObject.getInt(AppConstant.ID));
                                    taskApprovalList.add(task);
                                }
                            }
                            for (Tasks task : taskList){
                                Log.e(TAG, "task = " + task.toString());
                            }
                            child.setTasksArrayList(taskList);
                            childApprovalTask.setTasksArrayList(taskApprovalList);
                            childList.add(child);
                            Log.e(TAG, "child = " + child.toString());
                            childApprovalList.add(childApprovalTask);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }

                else
                {
                    Log.e(TAG, "List is empty");
                   // startActivity(new Intent(getApplicationContext(), AddChild.class));

                    screenSwitch.moveToAddChild(parentObject, childID, switchFrom, AppConstant.ADD, null);
                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Utils.logDebug(TAG, "Child JSONObject response" + response.toString());

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
    }

    private void stopServise(){
        Log.d("fsdfksjdhf", "stopService() = "
        );
        Intent myService = new Intent(ParentDashboard.this, AppCheckServices.class);
        stopService(myService);
    }

    private boolean imRealParent(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> parentsIds = sp.getStringSet(AppConstant.PARENTS_IDS, null);
        if (parentObject == null){
            return false;
        }
        Log.d("fsdfksjdhf", "parentsIds = " + parentsIds + "; my id = " + parentObject.getId());
        if (parentsIds != null && parentsIds.size() > 0){
            for (String parentId : parentsIds){
                if (parentObject.getId() == Integer.parseInt(parentId)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
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
                    new RestCall(parentDashboard).authenticateUser(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword()
                            , null, AppConstant.PARENT_SCREEN, progressBar);
                } catch (Exception e) {
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (handler!=null)
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public void onDrawerToggeled() {
        if (mAdapter != null)
            mAdapter.closeFebMenu();
    }
}
