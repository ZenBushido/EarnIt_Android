package com.mobiledi.earnit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.mobiledi.earnit.MyApplication;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.adapter.GoalListAdapter;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.NavigationDrawer;
import com.mobiledi.earnit.utils.ScreenSwitch;
import com.mobiledi.earnit.utils.Utils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by mobile-di on 11/8/17.
 */

public class GoalActivity extends BaseActivity implements View.OnClickListener, Validator.ValidationListener, NavigationDrawer.OnDrawerToggeled {

    GoalActivity goalActivity;
    public Parent parentObject;
    public Child childObject, otherChild;
    Button save, cancel;
    Tasks tasks;
    EditText goalName;

    @Length(min = 2, max = 10)
    EditText goalValue;
    final int GOAL_MAX_LENGTH = 20;
    String valueType = AppConstant.CASH;
    Intent intent;
    CircularImageView childAvatar;
    TextView toolbarHeader;
    Validator validator;
    RelativeLayout progressBar;
    Goal goal;
    String goalMode, fromScreen;
    private final String TAG = GoalActivity.class.getSimpleName();
    ScreenSwitch screenSwitch;
    private Toolbar goalToolbar;
    private ImageButton drawerToggle;
    ImageView back;
    ImageButton goalHelp;

    RecyclerView rvGoalList;
    GoalListAdapter goalListAdapter;
    List<Goal> listGoal = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_layout);

        goalHelp = (ImageButton) findViewById(R.id.goal_helpicon);
        goalToolbar = (Toolbar) findViewById(R.id.toolbar_goal);
        drawerToggle = (ImageButton) findViewById(R.id.drawerBtn);

        rvGoalList = (RecyclerView) findViewById(R.id.rvGoalList);

        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        setSupportActionBar(goalToolbar);
        getSupportActionBar().setTitle(null);

        back = (ImageView) findViewById(R.id.addtask_back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelAndBack();
            }
        });

        goalActivity = this;
        screenSwitch = new ScreenSwitch(goalActivity);
        setViewsId();
        intent = getIntent();
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        tasks = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);
        goalMode = intent.getStringExtra(AppConstant.MODE);
        fromScreen = intent.getStringExtra(AppConstant.FROM_SCREEN);

        this.listGoal = (List<Goal>) intent.getSerializableExtra("GoalList");

        if (goalMode.equalsIgnoreCase(AppConstant.UPDATE)) {
            /*save.setText(AppConstant.UPDATE);*/
            /*goal = (Goal) intent.getSerializableExtra(AppConstant.GOAL);
            goalName.setText(goal.getGoalName());
            goalValue.setText(String.valueOf(goal.getAmount()));
            toolbarHeader.setText(childObject.getFirstName() + "'s " + getResources().getString(R.string.edit_goal));*/
        } else {
            save.setText(AppConstant.SAVE);
            toolbarHeader.setText(getResources().getString(R.string.add_goal));
        }
        if (listGoal != null) {
            if (listGoal.size() > 0) {
                goalListAdapter = new GoalListAdapter(this, listGoal,
                        parentObject,
                        childObject,
                        otherChild,
                        tasks,
                        goalMode,
                        fromScreen);
                rvGoalList.setLayoutManager(new LinearLayoutManager(this));
                rvGoalList.setAdapter(goalListAdapter
                );
                rvGoalList.setItemAnimator(new DefaultItemAnimator());
                rvGoalList.setHasFixedSize(false);
            }

        }


        NavigationDrawer navigationDrawer = new NavigationDrawer(goalActivity, parentObject, goalToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, 0);
        navigationDrawer.setOnDrawerToggeled(this);

        updateAvatar(childObject, childAvatar);

        goalName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > GOAL_MAX_LENGTH) {
                    goalName.setError(getResources().getString(R.string.goal_name_length));
                    goalName.setFilters(new InputFilter[]{
                            new InputFilter.LengthFilter(GOAL_MAX_LENGTH)
                    });
                } else {
                    goalName.setFilters(new InputFilter[]{});
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        validator = new Validator(goalActivity);
        validator.setValidationListener(goalActivity);
        setCursorPosition();

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

    private void setCursorPosition() {
        Utils.SetCursorPosition(goalName);
        Utils.SetCursorPosition(goalValue);
    }

    private void setViewsId() {
        childAvatar = (CircularImageView) findViewById(R.id.goal_avatar);
        toolbarHeader = (TextView) findViewById(R.id.goal_header);
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        save = (Button) findViewById(R.id.goalsave);
        cancel = (Button) findViewById(R.id.cancel);
        goalName = (EditText) findViewById(R.id.goal_name);
        goalValue = (EditText) findViewById(R.id.goal_value);

        goalHelp.setOnClickListener(goalActivity);
        save.setOnClickListener(goalActivity);
        cancel.setOnClickListener(goalActivity);
        childAvatar.setOnClickListener(goalActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goalsave:
                if (goalName.getText().toString().trim().length() > 0) {
                    if (goalName.getText().toString().trim().length() <= GOAL_MAX_LENGTH)
                        validator.validate();
                    else
                        goalName.setError(getResources().getString(R.string.goal_too_long));
                } else
                    goalName.setError(getResources().getString(R.string.enter_goal));
                break;
            case R.id.cancel:
                onCancelAndBack();
                break;
            case R.id.goal_avatar:
                new FloatingMenu(goalActivity).fetchAvatarDimension(childAvatar, childObject, otherChild, parentObject, AppConstant.GOAL_SCREEN, progressBar, null);
                break;
            case R.id.goal_helpicon:
                Toast.makeText(goalActivity, getResources().getString(R.string.goal_help), Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void deleteGoal(int ids) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
        final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
        asyncHttpClient.addHeader("Authorization", basicAuth);
        asyncHttpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        asyncHttpClient.delete(this, AppConstant.BASE_URL + AppConstant.GOAL_DELETE + ids, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Success Code", String.valueOf(statusCode));
                Log.d("Success Response", response.toString());

                try {
                    String taskResponse = response.getString("message");
                    String finalTaskResponse = taskResponse.substring(2, taskResponse.length() - 2);

                    Toast.makeText(GoalActivity.this, finalTaskResponse + " successfully.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    //childObject.getTasksArrayList()

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Failure Code", String.valueOf(statusCode));
                Log.d("Failure Response", errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public void onBackPressed() {
        onCancelAndBack();
    }

    public void onCancelAndBack() {
        Intent intent;
        intent = new Intent(goalActivity, ParentDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(AppConstant.PARENT_OBJECT, parentObject);
        startActivity(intent);
    }

    @Override
    public void onValidationSucceeded() {
        addEditGoal();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addEditGoal() {

        JSONObject signInJson = new JSONObject();
        try {
            if (goalMode.equalsIgnoreCase(AppConstant.UPDATE)) {
                signInJson.put(AppConstant.CREATE_DATE, new DateTime().getMillis());
                //  signInJson.put(AppConstant.ID, goal.getId());
                // signInJson.put(AppConstant.UPDATE_DATE, new DateTime().getMillis());
            } else signInJson.put(AppConstant.CREATE_DATE, new DateTime().getMillis());

            signInJson.put(AppConstant.CHILDREN, new JSONObject().put(AppConstant.ID, childObject.getId()));
            signInJson.put(AppConstant.NAME, goalName.getText().toString().trim());
            signInJson.put(AppConstant.AMOUNT, goalValue.getText().toString().trim());
            StringEntity entity = new StringEntity(signInJson.toString());
            Utils.logDebug("GoalJson", signInJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            httpClient.addHeader("Authorization", basicAuth);
            httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
            PersistentCookieStore myCookieStore = new PersistentCookieStore(goalActivity);
            httpClient.setCookieStore(myCookieStore);
            httpClient.post(goalActivity, AppConstant.BASE_URL + AppConstant.ADD_GOAL, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (goalMode.equalsIgnoreCase(AppConstant.UPDATE))
                        showToast("Goal updated for " + childObject.getFirstName());
                    else
                        showToast("Goal added for " + childObject.getFirstName());
                    screenSwitch.isGoalExists(childObject, otherChild, parentObject, progressBar, AppConstant.GOAL_SCREEN, tasks);


                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (goalMode.equalsIgnoreCase(AppConstant.UPDATE))
                        showToast("Goal updated for " + childObject.getFirstName());
                    else
                        showToast("Goal added for " + childObject.getFirstName());
                    screenSwitch.isGoalExists(childObject, otherChild, parentObject, progressBar, AppConstant.GOAL_SCREEN, tasks);



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
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawerToggeled() {
    }


}
