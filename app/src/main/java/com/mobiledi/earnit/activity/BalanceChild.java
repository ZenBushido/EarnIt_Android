package com.mobiledi.earnit.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.adapter.ItemAdapter;
import com.mobiledi.earnit.adapter.MyRecyclerViewAdapter;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.model.Item;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.NavigationDrawer;
import com.mobiledi.earnit.utils.ScreenSwitch;
import com.mobiledi.earnit.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mobile-di on 7/10/17.
 */

public class BalanceChild extends BaseActivity implements View.OnClickListener, NavigationDrawer.OnDrawerToggeled {

    @BindView(R.id.goal_header) TextView headerBalance;
    @BindView(R.id.goal_avatar) CircularImageView avatar;
    Balance balance;
    public Parent parentObject;
    public Child childObject, otherChild;
    Tasks tasks;
    String fromScreen, userType;
    private final String TAG = "BalanceActivity";
    Intent intent;
    @BindView(R.id.loadingPanel) RelativeLayout progressBar;
    ScreenSwitch screenSwitch;
    @BindView(R.id.toolbar_goal)  Toolbar addToolbar;
    @BindView(R.id.drawerBtn) ImageButton drawerToggle;
    @BindView(R.id.save) Button save_btn;
    String add = "Add";
    List<Goal> listGoal = new ArrayList<>();
    @BindView(R.id.balanceChildList)  RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
   @BindView(R.id.totalbalance)  TextView totalBalance;
    @BindView(R.id.tv_goal_amount)  TextView totalGoal;
    @BindView(R.id.addtask_back_arrow) ImageView back;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_screen_layout);
        ButterKnife.bind(this);


        drawerToggle.setVisibility(View.GONE);


        setSupportActionBar(addToolbar);
        getSupportActionBar().setTitle(null);
        intent = getIntent();
        screenSwitch = new ScreenSwitch(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenSwitch.moveToChildDashboard(childObject, progressBar);

            }
        });
        final BalanceChild balanceChild = this;

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FloatingMenu(balanceChild).fetchAvatarDimension(avatar, childObject, parentObject, AppConstant.CHILD_DASHBOARD_SCREEN, progressBar);

            }
        });
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        // listGoal = (List<Goal>) intent.getSerializableExtra(AppConstant.GOAL_OBJECT);
        tasks = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);
        fromScreen = intent.getStringExtra(AppConstant.FROM_SCREEN);
        userType = intent.getStringExtra(AppConstant.TYPE);
        headerBalance.setText(childObject.getFirstName());

        if (isDeviceOnline())
            isGoalExists();
        NavigationDrawer navigationDrawer = new NavigationDrawer(this, parentObject, addToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, childObject.getId());
        navigationDrawer.setOnDrawerToggeled(this);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenSwitch.moveToChildDashboard(childObject, progressBar);

            }
        });

        try {
            Picasso.with(balance).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(avatar);
        } catch (Exception e) {
            Picasso.with(balance).load(R.drawable.default_avatar).into(avatar);
            e.printStackTrace();
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.goal_avatar:
                if (userType.equalsIgnoreCase(AppConstant.PARENT))
                    new FloatingMenu(balance).fetchAvatarDimension(avatar, childObject, otherChild, parentObject, AppConstant.BALANCE_SCREEN, progressBar, null);
                else
                    new FloatingMenu(balance).fetchAvatarDimension(avatar, childObject, parentObject, AppConstant.BALANCE_SCREEN, progressBar);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        screenSwitch.moveToChildDashboard(childObject, progressBar);
    }

    @Override
    public void onDrawerToggeled() {

    }

    public void isGoalExists() {

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBasicAuth(childObject.getEmail(), childObject.getPassword());
            client.get(AppConstant.BASE_URL + AppConstant.GOAL_API + childObject.getId(), null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.e(TAG, "JSONARRAY: "+response);

                    try {
                        if (response.length() > 0 && (response.getJSONObject(0).get(AppConstant.ID) instanceof Integer)) {

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    Goal goal = new GetObjectFromResponse().getGoalObject(object);
                                    listGoal.add(goal);
                                    Log.i("goal-responsel1", goal.getGoalName());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Integer balancesTotalGoals = 0;

                    for (int i = 0; i < listGoal.size(); i++)
                        balancesTotalGoals += listGoal.get(i).getCash();

                    totalBalance.setText("$"+balancesTotalGoals.toString());
                    totalGoal.setText("$" + balancesTotalGoals.toString());

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new MyRecyclerViewAdapter(getApplicationContext(), listGoal);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
