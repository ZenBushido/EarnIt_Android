package com.mobiledi.earnit.activity;

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
import com.loopj.android.http.PersistentCookieStore;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.adapter.ItemAdapter;
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

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mobile-di on 7/10/17.
 */

public class Balance extends BaseActivity implements View.OnClickListener, NavigationDrawer.OnDrawerToggeled {

    TextView headerBalance;
    CircularImageView avatar;
    Balance balance;
    public Parent parentObject;
    public Child childObject, otherChild;
    Goal goal;
    Tasks tasks;
    String fromScreen, userType;
    private final String TAG = "BalanceActivity";
    Intent intent;
    RelativeLayout progressBar;
    ScreenSwitch screenSwitch;
    private Toolbar addToolbar;
    private ImageButton drawerToggle;
    ImageView back;
    Button cancel_btn;
    Button save_btn;
    TextView balanceSetting;
    ArrayList<Item> goalsList;
    private BottomSheetDialog mBottomSheetDialog;
    private String repeat;
    int fetchGoalId = 0;
    String subtract = "Subtract";
    String add = "Add";
    TextView currentBalance, balance_Header;
    EditText task_detailedt, adjustmentedt;
    ImageButton helpIcon, forward_arrow, backward_arrow;
    int count = 0;

    List<Goal> listGoal = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_layout);

        balance_Header = (TextView) findViewById(R.id.balance_header);
        forward_arrow = (ImageButton) findViewById(R.id.addtask_forward_arrow);
        backward_arrow = (ImageButton) findViewById(R.id.addtask_backarrow);
        helpIcon = (ImageButton) findViewById(R.id.helpicon);
        adjustmentedt = (EditText) findViewById(R.id.adjustment);
        task_detailedt = (EditText) findViewById(R.id.task_detail);
        currentBalance = (TextView) findViewById(R.id.current_balance);
        balanceSetting = (TextView) findViewById(R.id.add_substract);
        cancel_btn = (Button) findViewById(R.id.cancel);
        save_btn = (Button) findViewById(R.id.save);
        headerBalance = (TextView) findViewById(R.id.goal_header);
        addToolbar = (Toolbar) findViewById(R.id.toolbar_goal);
        drawerToggle = (ImageButton) findViewById(R.id.drawerBtn);
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        setSupportActionBar(addToolbar);
        getSupportActionBar().setTitle(null);
        goalsList = new ArrayList<>();
        goalsList.add(new Item(0, add));
        goalsList.add(new Item(1, subtract));
        setViewIds();
        screenSwitch = new ScreenSwitch(balance);
        intent = getIntent();
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        // listGoal = (List<Goal>) intent.getSerializableExtra(AppConstant.GOAL_OBJECT);
        tasks = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);
        fromScreen = intent.getStringExtra(AppConstant.FROM_SCREEN);
        userType = intent.getStringExtra(AppConstant.TYPE);
        headerBalance.setText("Adjust" + " " + childObject.getFirstName() + " " + "Balance");
        balanceSetting.setText("Add");
        if (isDeviceOnline())
            isGoalExists();
        if (parentObject != null) {
            NavigationDrawer navigationDrawer = new NavigationDrawer(this, parentObject, addToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, childObject.getId());
            navigationDrawer.setOnDrawerToggeled(this);

        } else {
            NavigationDrawer navigationDrawer = new NavigationDrawer(this, parentObject, addToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, childObject.getId());
            navigationDrawer.setOnDrawerToggeled(this);


        }
        if (isDeviceOnline())
            isGoalExists();
        if (listGoal == null || listGoal.size() < 1) {
            currentBalance.setText("Chose Ballance");
            balance_Header.setText("Chose Goal Name");

            forward_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count < listGoal.size()) {
                        balance_Header.setText(listGoal.get(count).getGoalName());
                        currentBalance.setText(listGoal.get(count).getAmount() + "");
                        count++;

                    }
                }
            });

            backward_arrow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (count > 0) {
                        --count;
                        balance_Header.setText(listGoal.get(count).getGoalName());
                        currentBalance.setText(listGoal.get(count).getAmount() + "");
                    }
                }
            });

            balanceSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialog(goalsList, balanceSetting, AppConstant.GOAL);
                }
            });

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userType.equalsIgnoreCase(AppConstant.PARENT))
                        onCancelAndBack();
                    else
                        screenSwitch.moveToChildDashboard(childObject, progressBar);
                }
            });
            save_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (adjustmentedt.getText().toString().length() == 0) {

                                                    Utils.showToast(balance, getResources().getString(R.string.no_adjustment));
                                                } else if (task_detailedt.getText().toString().length() == 0) {
                                                    Utils.showToast(balance, getResources().getString(R.string.no_balance_reason));
                                                } else {

                                                    int finalBalance;

                                                    String adjustBalance = adjustmentedt.getText().toString();
                                                    int mBalance = Integer.parseInt(adjustBalance);

                                                    if (TextUtils.isEmpty(adjustBalance)) {
                                                        adjustmentedt.setError("Please enter adjustment balance.");
                                                    } else {
                                                        adjustmentedt.setError(null);
                                                    }

                                                    addEditGoal();

                                                }
                                            }
                                        }
            );

            helpIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showToast(balance, getResources().getString(R.string.balance_help));
                }
            });

            try {
                Picasso.with(balance).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(avatar);
            } catch (Exception e) {
                Picasso.with(balance).load(R.drawable.default_avatar).into(avatar);
                e.printStackTrace();
            }

        }
    }

    private void showBottomSheetDialog(ArrayList<Item> items, final TextView dropDownView, final String type) {
        mBottomSheetDialog = new BottomSheetDialog(this);
        final View view = getLayoutInflater().inflate(R.layout.sheet, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemAdapter(items, new ItemAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {
                if (type.equalsIgnoreCase(AppConstant.GOAL)) {
                    fetchGoalId = item.getId();
                } else {
                    repeat = item.getmTitle();
                }
                switch (item.getId()) {
                    case 0: {
                        balanceSetting.setText(add);
                        mBottomSheetDialog.dismiss();
                    }
                    break;
                    case 1: {
                        balanceSetting.setText(subtract);
                        mBottomSheetDialog.dismiss();
                    }
                    break;
                }
            }
        }));

        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    public void setViewIds() {
        balance = this;
        back = (ImageView) findViewById(R.id.addtask_back_arrow);
        avatar = (CircularImageView) findViewById(R.id.goal_avatar);
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);

        back.setOnClickListener(balance);
        avatar.setOnClickListener(balance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addtask_back_arrow:
                if (userType.equalsIgnoreCase(AppConstant.PARENT))
                    onCancelAndBack();
                else
                    screenSwitch.moveToChildDashboard(childObject, progressBar);
                break;

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
        if (userType.equalsIgnoreCase(AppConstant.PARENT))
            onCancelAndBack();
        else
            screenSwitch.moveToChildDashboard(childObject, progressBar);
    }

    public void onCancelAndBack() {
        if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
            screenSwitch.moveToParentDashboard(parentObject);
        } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN)) {
            if (otherChild.getTasksArrayList().size() > 0)
                screenSwitch.moveToAllTaskScreen(childObject, otherChild, fromScreen, parentObject, AppConstant.BALANCE_SCREEN);
            else
                Utils.showToast(balance, getResources().getString(R.string.no_task_available));
        } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
            if (childObject.getTasksArrayList().size() > 0)
                screenSwitch.moveToAllTaskScreen(childObject, otherChild, fromScreen, parentObject, AppConstant.BALANCE_SCREEN);
            else
                Utils.showToast(balance, getResources().getString(R.string.no_task_for_approval));
        } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK))
            screenSwitch.moveToAddTask(childObject, otherChild, parentObject, fromScreen, tasks);
        else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN))
            screenSwitch.isGoalExists(childObject, otherChild, parentObject, progressBar, fromScreen, tasks);
        else
            screenSwitch.moveToAllTaskScreen(childObject, otherChild, AppConstant.CHECKED_IN_SCREEN, parentObject, AppConstant.BALANCE_SCREEN);
    }

    @Override
    public void onDrawerToggeled() {

    }

    public void isGoalExists() {

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
            client.get(AppConstant.BASE_URL + AppConstant.GOAL_API + childObject.getId(), null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    try {
                        if (response.length() > 0 && (response.getJSONObject(0).get(AppConstant.ID) instanceof Integer)) {

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    if (object.has(AppConstant.ID)) {
                                        Goal goal = new GetObjectFromResponse().getGoalObject(object);
                                        listGoal.add(goal);
                                        Log.i("goal-responsel1", String.valueOf(listGoal.size()));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


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

    private void addEditGoal() {

        JSONObject signInJson = new JSONObject();
        try {
            signInJson.put(AppConstant.AMOUNT, adjustmentedt.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            signInJson.put(AppConstant.GOAL, new JSONObject().put(AppConstant.ID, listGoal.get(count - 1).getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            signInJson.put("reason", task_detailedt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(signInJson.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        PersistentCookieStore myCookieStore = new PersistentCookieStore(balance);
        httpClient.setCookieStore(myCookieStore);
        httpClient.post(balance, AppConstant.BASE_URL + "/" + "adjustments", entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                showToast("Adjustment added for " + childObject.getFirstName());
                screenSwitch.moveToParentDashboard(parentObject);


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                showToast("Adjustment added for " + childObject.getFirstName());
                screenSwitch.moveToParentDashboard(parentObject);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                unLockScreen();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                unLockScreen();
            }

        });
    }
}
