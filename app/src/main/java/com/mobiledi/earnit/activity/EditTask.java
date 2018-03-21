package com.mobiledi.earnit.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.adapter.ItemAdapter;
import com.mobiledi.earnit.model.AddTaskModel;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.model.Item;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.TaskV2Model;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.stickyEvent.MessageEvent;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.NavigationDrawer;
import com.mobiledi.earnit.utils.ScreenSwitch;
import com.mobiledi.earnit.utils.Utils;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobiledi.earnit.activity.ParentDashboard.parentObject;

public class EditTask extends BaseActivity implements View.OnClickListener, NavigationDrawer.OnDrawerToggeled {

    public Parent parentObject;
    public Child childObject, otherChild;
    public Goal goalObject;
    @BindView(R.id.save)Button save;
    @BindView(R.id.cancel) Button cancel;
    @BindView(R.id.newtask_requirephoto) Button checkbox;
    @BindView(R.id.newtask_screenlockcheck) Button screenlockBtn;
    @BindView(R.id.task_name) EditText taskName;
    @BindView(R.id.task_detail) EditText taskDetails;
    @BindView(R.id.task_amount) EditText amountTxt;

    int childID;

    //    EditText amount;
    @BindView(R.id.date_time_textview) TextView date_time_textview;
    @BindView(R.id.add_task_header) TextView childName;
    @BindView(R.id.assign_to_id) TextView assignTo;
    @BindView(R.id.add_task_avatar) CircularImageView childAvatar;
    int childsCounter = 0;


    //    String actionBarText = "New Task for ";
    String NONE = "None";
    EditTask addTask;
    Map<Integer, String> childs;
    List<String> assignChild;
    int dom = 0;
    int month = 0;
    int year = 0;
    int hour = 00;
    int min = 00;
    final int TASK_NAME_LENGTH = 40;
    boolean checkboxStatus = false;
    boolean IS_EDITING_TASK = false;
    private final String TAG = "EditTask";
    @BindView(R.id.apply_to_goal_spinner) TextView repeatSpinner;
   // @BindView(R.id.assign_to_id) TextView assignSpinner;
    @BindView(R.id.loadingPanel) RelativeLayout progressBar;
    String screen_name;
    private String goalName;
    private String repeat;
    ArrayList<String> list;
    ArrayList<Goal> goalList = new ArrayList<>();
    Tasks currentTask;
    DateTimeFormatter fmt;
    ScreenSwitch screenSwitch;
    ArrayList<Item> repeatList, goalsList;
    private BottomSheetDialog mBottomSheetDialog;
    int fetchGoalId = 0;
    @BindView(R.id.toolbar_add) Toolbar goalToolbar;
    @BindView(R.id.drawerBtn) ImageButton drawerToggle;
    List<Child> childList = new ArrayList<>();


    @BindView(R.id.addtask_helpicon) ImageButton  addTask_help;
    @BindView(R.id.addtask_back_arrow) ImageButton back;
    @BindView(R.id.btnDelete)
    Button deleteButton;
    private String currentTaskStatus;
    int fetchCHildId = 0;
    @BindView(R.id.btnApprove)
    ImageButton approveButton;
    @BindView(R.id.add_task_header2)
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_layout);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        textView.setText(intent.getStringExtra("title"));


        setSupportActionBar(goalToolbar);
        getSupportActionBar().setTitle(null);

        addTask = this;
        screenSwitch = new ScreenSwitch(addTask);

        this.currentTaskStatus = intent.getStringExtra(AppConstant.TASK_STATUS);


        //---------- Intent Get Extra
        screen_name = intent.getStringExtra(AppConstant.FROM_SCREEN);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        goalObject = (Goal) intent.getSerializableExtra(AppConstant.GOAL_OBJECT);

        fetchCHildId = childObject.getId();
        NavigationDrawer navigationDrawer = new NavigationDrawer(addTask, parentObject, goalToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, 0);
        navigationDrawer.setOnDrawerToggeled(this);
        fmt = DateTimeFormat.forPattern(AppConstant.DATE_FORMAT);
        list = new ArrayList<>();
        childID = childObject.getId();

        list.add(NONE);
        repeatList = new ArrayList<>();
        goalsList = new ArrayList<>();
        goalsList.add(new Item(0, NONE));

        if(isDeviceOnline())
        callGoalService(parentObject.getEmail(), parentObject.getPassword(), childID);



        try {
            Picasso.with(addTask).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(childAvatar);
        } catch (Exception e) {
            Picasso.with(addTask).load(R.drawable.default_avatar).into(childAvatar);
            e.printStackTrace();
        }

        childs = (Map<Integer, String>) intent.getSerializableExtra(AppConstant.CHILD_MAP);
        assignChild = new LinkedList<>(Arrays.asList(childObject.getFirstName()));
        assignTo.setText(childObject.getFirstName());
        save.setOnClickListener(addTask);
        cancel.setOnClickListener(addTask);

        checkbox.setOnClickListener(addTask);
        childAvatar.setOnClickListener(addTask);
        assignTo.setOnClickListener(addTask);
        repeatSpinner.setOnClickListener(addTask);
        addTask_help.setOnClickListener(addTask);
        back.setOnClickListener(addTask);
        screenlockBtn.setOnClickListener(addTask);
        deleteButton.setOnClickListener(addTask);
        fetchChildList();
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogOnTaskAdded(childObject, childObject);

            }
        });
        repeatSpinner.setText(NONE);

        taskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > TASK_NAME_LENGTH) {
                    taskName.setError(getResources().getString(R.string.task_name_length));
                    taskName.setFilters(new InputFilter[]{
                            new InputFilter.LengthFilter(TASK_NAME_LENGTH)
                    });
                } else {
                    taskName.setFilters(new InputFilter[]{});
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        currentTask = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);
        fetchCHildId = childObject.getId();
        autoFill(currentTask);
        if (currentTask.getGoal() != null) {
            fetchGoalId = currentTask.getGoal().getId();
            fetchCHildId = childObject.getId();
            goalName = currentTask.getGoal().getGoalName();
            repeatSpinner.setText(goalName.substring(0, 1).toUpperCase() + goalName.substring(1));
        } else {
            repeatSpinner.setText(NONE);
        }
        if (currentTask.getRepititionSchedule() != null) {
            repeat = currentTask.getRepititionSchedule().getRepeat();
        } else {
            repeat = NONE;
        }
        amountTxt.setText(currentTask.getAllowance() + "");


        setCursorPosition();
        taskDetails.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskDetails.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);

        taskDetails.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    taskDetails.setCursorVisible(false);
                }
                return false;
            }
        });
    }

    private void setCursorPosition() {
        Utils.SetCursorPosition(taskName);
        Utils.SetCursorPosition(taskDetails);
    }

    private void autoFill(Tasks currentTask) {

        childName.setText(currentTask.getName());
        taskDetails.setText(currentTask.getDetails());
        taskName.setText(currentTask.getName());
        DateTime dt = new DateTime(currentTask.getDueDate());
        String toPrintDate = fmt.print(dt);
        date_time_textview.setText(toPrintDate);

        if (currentTask.getPictureRequired()) {
            checkbox.setText("✔");
            checkboxStatus = true;
        } else {
            checkboxStatus = false;
        }

    }

    /*-------------------------TODO ----- OnClick Methods-----------*/

    @OnClick(R.id.due_date)
    void dueDate()
    {
        Intent parentCalendarAcitivity = new Intent(EditTask.this, ParentCalendarActivity.class);
        parentCalendarAcitivity.putExtra(AppConstant.PARENT_OBJECT, parentObject);
        parentCalendarAcitivity.putExtra(AppConstant.FROM_SCREEN, screen_name);
        parentCalendarAcitivity.putExtra(AppConstant.CHILD_OBJECT, childObject);
        parentCalendarAcitivity.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
        parentCalendarAcitivity.putExtra("title", taskName.getText().toString());
        parentCalendarAcitivity.putExtra("title2", childName.getText().toString());
        parentCalendarAcitivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(parentCalendarAcitivity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.cancel:
                onCancelAndBack(childObject, otherChild);
                break;
            case R.id.newtask_requirephoto:
                if (checkboxStatus) {
                    checkbox.setText("");
                    checkboxStatus = false;
                } else {
                    checkbox.setText("✔");
                    checkboxStatus = true;
                }
                break;
            case R.id.newtask_screenlockcheck:
                Intent screenrule = new Intent(EditTask.this, ScreenRules.class);
                screenrule.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                screenrule.putExtra(AppConstant.FROM_SCREEN, screen_name);
                screenrule.putExtra(AppConstant.PARENT_OBJECT, parentObject);
                screenrule.putExtra(AppConstant.CHILD_OBJECT, childObject);
                screenrule.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
                startActivity(screenrule);
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.addtask_framelayout, new ScreenRuleFragment());
//                ft.commit();
                break;
            case R.id.assign_to_id:
                showBottomSheetAssignDialog(repeatList, assignTo, AppConstant.CHILD);

                break;
            case R.id.save:
                if (taskName.getText().toString().trim().length() > 0) {
                    if (taskName.getText().toString().trim().length() <= TASK_NAME_LENGTH) {
                        saveTask();

                    } else
                        showToast(getResources().getString(R.string.task_name_too_long));
                } else
                    showToast(getResources().getString(R.string.please_enter_task_name));
                break;

            case R.id.add_task_avatar:
                if (currentTask != null)
                    new FloatingMenu(addTask).fetchAvatarDimension(childAvatar, childObject, otherChild, parentObject, AppConstant.ADD_TASK, progressBar, currentTask);
                else
                    new FloatingMenu(addTask).fetchAvatarDimension(childAvatar, childObject, otherChild, parentObject, AppConstant.ADD_TASK, progressBar, null);
                break;

            case R.id.apply_to_goal_spinner:
                showBottomSheetDialog(goalsList, repeatSpinner, AppConstant.GOAL);

                break;

            case R.id.addtask_back_arrow:
                onCancelAndBack(childObject, otherChild);
                break;

            case R.id.addtask_helpicon:
                Toast.makeText(addTask, getResources().getString(R.string.addtask_help), Toast.LENGTH_LONG).show();
                break;

            case R.id.btnDelete:
                deleteTaskDialog();
                break;
        }

    }

    public void deleteTaskDialog() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        deleteTask(currentTask.getId());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteTask(int id) {

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        asyncHttpClient.delete(addTask, AppConstant.BASE_URL + AppConstant.TASKS_API_DELETE + id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Success Code", String.valueOf(statusCode));
                Log.d("Success Response", response.toString());

                try {
                    String taskResponse = response.getString("message");
                    String finalTaskResponse = taskResponse.substring(2, taskResponse.length() - 2);

                    Toast.makeText(addTask.getBaseContext(), finalTaskResponse + " successfully.", Toast.LENGTH_SHORT).show();


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

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                chechUserTasks();

            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    private void saveTask() {
        AddTaskModel.repititionSchedule repititionSchedule = null;

        JSONObject signInJson = new JSONObject();
        try {
            signInJson.put(AppConstant.ID, currentTask.getId());
            signInJson.put(AppConstant.CHILDREN, new JSONObject().put(AppConstant.ID, childID));
            if (fetchGoalId > 0)
                signInJson.put(AppConstant.GOAL, new JSONObject().put(AppConstant.ID, fetchGoalId));
            signInJson.put(AppConstant.CREATE_DATE, new DateTime().getMillis());


            signInJson.put(AppConstant.NAME, taskName.getText().toString().trim());
            signInJson.put(AppConstant.DESCRIPTION, taskDetails.getText());
            signInJson.put(AppConstant.PICTURE_REQUIRED, checkboxStatus ? 1 : 0);
            signInJson.put(AppConstant.STATUS, AppConstant.DUE);
            signInJson.put(AppConstant.UPDATE_DATE, new DateTime().getMillis());
            signInJson.put(AppConstant.TASK_COMMENTS, new JSONArray());
            signInJson.put(AppConstant.EXPIRY_DATE, new DateTime().getMillis());
            JSONObject repSchedule = new JSONObject();
            MessageEvent m = EventBus.getDefault().getStickyEvent(MessageEvent.class);
            EventBus.getDefault().removeAllStickyEvents();
            if (m != null) {
                repititionSchedule = m.getResponse();
                EventBus.getDefault().removeAllStickyEvents();


            }
            if (repititionSchedule != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/DD/yyyy hh:mm:ss aaa");
                Date date = (Date) simpleDateFormat.parse(repititionSchedule.getDate() + " " + repititionSchedule.getEndTime());
                DateTime dateTime = new DateTime(date);
                signInJson.put(AppConstant.DUE_DATE, dateTime.getMillis());

                repSchedule.put(AppConstant.REPEAT, repititionSchedule.getRepeat());
                repSchedule.put(AppConstant.START_TIME, repititionSchedule.getStartTime());
                repSchedule.put(AppConstant.END_TIME, repititionSchedule.getEndTime());
                repSchedule.put("everyNRepeat", repititionSchedule.getEveryNday());
                for (int i = 0; i < goalList.size(); i++)
                    if (goalList.get(i).getGoalName().equals(goalName))
                        signInJson.put(AppConstant.GOAL, goalList.get(i).getId());


                if (Objects.equals(repititionSchedule.getRepeat(), "weekly")) {


                    JSONArray array = new JSONArray();
                    for (int i = 0; i < repititionSchedule.getSpecificDays().size(); i++)
                        array.put(repititionSchedule.getSpecificDays().get(i));
                    repSchedule.put(AppConstant.SPECIFIC_DAYS, array);
                    signInJson.put(AppConstant.REPITITION_SCHEDULE, repSchedule);


                } else if (Objects.equals(repititionSchedule.getRepeat(), "daily")) {
                    signInJson.put(AppConstant.REPITITION_SCHEDULE, repSchedule);


                } else if (Objects.equals(repititionSchedule.getRepeat(), "monthly")) {

                    JSONArray array = new JSONArray();
                    for (int i = 0; i < repititionSchedule.getSpecificDays().size(); i++)
                        array.put(repititionSchedule.getSpecificDays().get(i));
                    repSchedule.put(AppConstant.SPECIFIC_DAYS, array);

                    signInJson.put(AppConstant.REPITITION_SCHEDULE, repSchedule);
                    Utils.logDebug(TAG, "add_task_json_test :" + String.valueOf(signInJson));


                }
            } else {
                    JSONObject repeatObject = new JSONObject();
                signInJson.put(AppConstant.DUE_DATE, currentTask.getDueDate());

                if (currentTask.getRepititionSchedule() != null) {

                            repeatObject.put(AppConstant.REPEAT, currentTask.getRepititionSchedule().getRepeat());
                            repeatObject.put(AppConstant.START_TIME, currentTask.getRepititionSchedule().getStartTime());
                            repeatObject.put(AppConstant.END_TIME, currentTask.getRepititionSchedule().getEndTime());
                            repeatObject.put("everyNRepeat", currentTask.getRepititionSchedule().getEveryNRepeat());
                            for (int i = 0; i < goalList.size(); i++)
                                if (goalList.get(i).getGoalName().equals(goalName))
                                    signInJson.put(AppConstant.GOAL, goalList.get(i).getId());


                            if (Objects.equals(currentTask.getRepititionSchedule().getRepeat(), "weekly")) {


                                JSONArray array = new JSONArray();
                                for (int i = 0; i < currentTask.getRepititionSchedule().getSpecificDays().size(); i++)
                                    array.put(currentTask.getRepititionSchedule().getSpecificDays().get(i));
                                repeatObject.put(AppConstant.SPECIFIC_DAYS, array);
                                signInJson.put(AppConstant.REPITITION_SCHEDULE, repeatObject);


                            } else if (Objects.equals(currentTask.getRepititionSchedule().getRepeat(), "daily")) {
                                signInJson.put(AppConstant.REPITITION_SCHEDULE, repeatObject);

                            } else if (Objects.equals(currentTask.getRepititionSchedule().getRepeat(), "monthly")) {

                                JSONArray array = new JSONArray();
                                for (int i = 0; i < currentTask.getRepititionSchedule().getSpecificDays().size(); i++)
                                    array.put(currentTask.getRepititionSchedule().getSpecificDays().get(i));
                                repeatObject.put(AppConstant.SPECIFIC_DAYS, array);

                                signInJson.put(AppConstant.REPITITION_SCHEDULE, repeatObject);

                            }
                        } else {
                            signInJson.put(AppConstant.EXPIRY_DATE, new DateTime().getMillis());

                        }

                }
            Utils.logDebug(TAG, "add_task_json :" + String.valueOf(signInJson));

            StringEntity entity = new StringEntity(signInJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
            PersistentCookieStore myCookieStore = new PersistentCookieStore(addTask);
            httpClient.setCookieStore(myCookieStore);
            Utils.logDebug(TAG, "calling task edit api");
            httpClient.put(addTask, AppConstant.BASE_URL + AppConstant.TASKS_API, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Utils.logDebug(TAG, "calling success : " + response);
                    chechUserTasks();

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Utils.logDebug(TAG, "calling success : " + response);
                    chechUserTasks();
                    screenSwitch.moveToAllTaskScreen(childObject, childObject, AppConstant.CHECKED_IN_SCREEN, parentObject, screen_name);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Utils.logDebug(TAG, "calling onFailure : " + errorResponse.toString());
                    unLockScreen();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Utils.logDebug(TAG, "calling onFailure : " + errorResponse.toString());
                    unLockScreen();

                }

                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    lockScreen();
                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                    unLockScreen();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // onCancelAndBack(childObject, otherChild);
    }

    public void onCancelAndBack(Child child, Child otherChild) {
        screenSwitch.moveToAllTaskScreen(child, child, AppConstant.CHECKED_IN_SCREEN, parentObject, screen_name);

    }

    private void chechUserTasks() {

        final AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        client.get(AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + childObject.getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tasks> taskList = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        //TASKS
                        Tasks task = new GetObjectFromResponse().getTaskObject(object, childObject.getId());
                        taskList.add(task);


                    } catch (Exception e) {

                    }
                    childObject.setTasksArrayList(taskList);
                    if (childObject.getTasksArrayList().size() > 0)
                        screenSwitch.moveToAllTaskScreen(childObject, childObject, AppConstant.CHECKED_IN_SCREEN, parentObject, screen_name);
                    else {
                        Toast.makeText(addTask, getResources().getString(R.string.no_task_available), Toast.LENGTH_LONG).show();
                        screenSwitch.moveToParentDashboard(parentObject);
                    }
                }
            }
        });
    }


    public void callGoalService(String email, String password, int childId) {

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(3, 3000);
            client.setBasicAuth(email, password);
            client.get(AppConstant.BASE_URL + AppConstant.GOAL_API + childId, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    goalsList = new ArrayList<>();
                    goalList = new ArrayList<>();
                    goalsList.add(new Item(0, NONE));

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            Goal goal = new GetObjectFromResponse().getGoalObject(object);

                            goalList.add(goal);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    //UPDATE GOAL SPINNER IF IT IS A TASK EDIT
                    for (Goal goal : goalList) {
                        goalsList.add(new Item(goal.getId(), goal.getGoalName()));
                    }

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    unLockScreen();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    unLockScreen();
                }

                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    lockScreen();
                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);

                    unLockScreen();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialogOnTaskAdded(final Child child, final Child otherChild) {
        final Dialog dialog = new Dialog(addTask);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.9f;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_box);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
        message.setText("Are you sure you want to approve this task? Any credit toward this task will be applied");
        Button declineButton = (Button) dialog.findViewById(R.id.cancel);
        declineButton.setText(AppConstant.NO);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


            }
        });
        Button acceptButton = (Button) dialog.findViewById(R.id.ok);
        acceptButton.setText(AppConstant.YES);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                updateTaskStatus(currentTask, AppConstant.APPROVED);


            }
        });
        dialog.show();
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
                    for (int i = 0; i < goalList.size(); i++) {
                        if (item.getId() != 0) {
                            if (item.getId() - 1 == goalList.get(i).getId()) {
                                fetchGoalId = goalList.get(i).getId();
                            }
                        }

                    }
                } else {
                    for (int i = 0; i < childList.size(); i++) {

                        if (item.getId() != 0) {
                            if (item.getId() - 1 == i) {
                                childID = childList.get(i).getId();
                                repeat = childList.get(i).getFirstName();

                            }
                        }
                    }


                }
                showToast(item.getTitle());
                dropDownView.setText(item.getTitle());
                if (mBottomSheetDialog != null) {
                    mBottomSheetDialog.dismiss();
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

    private void showBottomSheetAssignDialog(ArrayList<Item> items, final TextView dropDownView, final String type) {
        mBottomSheetDialog = new BottomSheetDialog(this);
        final View view = getLayoutInflater().inflate(R.layout.sheet, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemAdapter(items, new ItemAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {

                if (type.equalsIgnoreCase(AppConstant.GOAL)) {

                    for (int i = 0; i < goalList.size(); i++) {
                        if (item.getId() != 0) {
                            if (item.getId() - 1 == goalList.get(i).getId()) {
                                fetchGoalId = goalList.get(i).getId();
                            }
                        }

                    }
                } else {
                    repeat = item.getmTitle();
                    for (int i = 0; i < childList.size(); i++) {
                        if (item.getId() == i) {
                            childID = childList.get(i).getId();
                            if(isDeviceOnline())
                            callGoalService(parentObject.getEmail(), parentObject.getPassword(), childID);

                        }

                    }

                }
                showToast(item.getTitle());
                dropDownView.setText(item.getTitle());
                if (mBottomSheetDialog != null) {
                    mBottomSheetDialog.dismiss();
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

    public void fetchChildList() {

        final AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        client.get(AppConstant.BASE_URL + AppConstant.CHILDREN_API + parentObject.getAccount().getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                childList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject childObjectResponse = response.getJSONObject(i);
                        Child cList = new GetObjectFromResponse().getChildObject(childObjectResponse);
                        if (parentObject.getId() == childObjectResponse.getInt(AppConstant.ID)) {
                            Child childFromResponse = new GetObjectFromResponse().getChildObject(childObjectResponse);
                            Child otherChildFromResponse = new GetObjectFromResponse().getChildObject(childObjectResponse);

                            ArrayList<Tasks> taskList = new ArrayList<>();
                            ArrayList<Tasks> otherTaskList = new ArrayList<>();
                            JSONArray taskArray = childObjectResponse.getJSONArray(AppConstant.TASKS);
                            for (int taskIndex = 0; taskIndex < taskArray.length(); taskIndex++) {
                                JSONObject taskObject = taskArray.getJSONObject(taskIndex);
                                if (taskObject.getString(AppConstant.STATUS).equals(AppConstant.COMPLETED)) {
                                    Tasks task = new GetObjectFromResponse().getTaskObject(taskObject, childFromResponse.getId());
                                    taskList.add(task);
                                }
                                chechUserTasks();

                                JSONObject othertaskObject = taskArray.getJSONObject(taskIndex);
                                if (!othertaskObject.getString(AppConstant.STATUS).equals(AppConstant.APPROVED)) {
                                    Tasks task = new GetObjectFromResponse().getTaskObject(othertaskObject, otherChildFromResponse.getId());
                                    otherTaskList.add(task);
                                }
                            }
                            childFromResponse.setTasksArrayList(taskList);
                            otherChildFromResponse.setTasksArrayList(otherTaskList);
                        }
                        childList.add(cList);
                        Log.i("child -> ", cList.getId() + cList.getFirstName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < childList.size(); i++) {
                    repeatList.add(new Item(childsCounter++, childList.get(i).getFirstName()));

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

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


    @Override
    public void onDrawerToggeled() {

    }

    private void updateTaskStatus(Tasks selectedTask, String changedStatus) {
        JSONObject taskJson = new JSONObject();
        try {
            taskJson.put(AppConstant.CHILDREN, new JSONObject().put(AppConstant.ID, selectedTask.getChildId()));
            taskJson.put(AppConstant.ID, selectedTask.getId());
            taskJson.put(AppConstant.NAME, selectedTask.getName());
            taskJson.put(AppConstant.DUE_DATE, selectedTask.getDueDate());
            taskJson.put(AppConstant.CREATE_DATE, selectedTask.getCreateDate());
            taskJson.put(AppConstant.DESCRIPTION, selectedTask.getDetails());
            taskJson.put(AppConstant.STATUS, changedStatus);
            taskJson.put(AppConstant.UPDATE_DATE, new DateTime().getMillis());
            taskJson.put(AppConstant.ALLOWANCE, selectedTask.getAllowance());

            if (selectedTask.getRepititionSchedule() == null)
                Utils.logDebug(TAG, "repeat is none");
            else {
                JSONObject repeatSchedule = new JSONObject();
                repeatSchedule.put(AppConstant.ID, selectedTask.getRepititionSchedule().getId());
                repeatSchedule.put(AppConstant.REPEAT, selectedTask.getRepititionSchedule().getRepeat());
                taskJson.put(AppConstant.REPITITION_SCHEDULE, repeatSchedule);

            }

            if (selectedTask.getPictureRequired())
                taskJson.put(AppConstant.PICTURE_REQUIRED, selectedTask.getPictureRequired());
            else {
                taskJson.put(AppConstant.PICTURE_REQUIRED, 0);
                Utils.logDebug(TAG, "picture required not checked");
            }

            if (selectedTask.getGoal() != null) {
                taskJson.put(AppConstant.GOAL, new JSONObject().put(AppConstant.ID, selectedTask.getGoal().getId()));

            } Utils.logDebug(TAG, " child-update-task : " + taskJson.toString());
            StringEntity entity = new StringEntity(taskJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());

            httpClient.put(addTask, AppConstant.BASE_URL + AppConstant.TASKS_API, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Utils.logDebug(TAG, " onSuccessOU : " + response.toString());
                    progressBar.setVisibility(View.GONE);
                    lockScreen();

                    final AsyncHttpClient client = new AsyncHttpClient();
                    client.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
                    client.get(AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + childObject.getId(), null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            ArrayList<Tasks> taskList = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    //TASKS
                                    Tasks task = new GetObjectFromResponse().getTaskObject(object, childObject.getId());
                                    taskList.add(task);


                                } catch (Exception e) {

                                }
                                childObject.setTasksArrayList(taskList);
                                screenSwitch.moveToAllTaskScreen(childObject, childObject, AppConstant.CHECKED_IN_SCREEN, parentObject, screen_name);
                                //screenSwitch.moveToAllTaskScreen(child, child, fromScreen, parentObject, fromScreen);
                            }
                        }
                    });

                    //if(isDeviceOnline())
                      //  fetchChildTaskList();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Utils.logDebug(TAG, " onSuccessAU : " + response.toString());
                    progressBar.setVisibility(View.GONE);
                    lockScreen();
                    screenSwitch.moveToAllTaskScreen(childObject, childObject, AppConstant.CHECKED_IN_SCREEN, parentObject, screen_name);
                  //  if(isDeviceOnline())
                    //    fetchChildTaskList();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    showToast(getResources().getString(R.string.api_calling_failed));
                    Utils.logDebug(TAG, " onFailureU : " + errorResponse.toString());
                    unLockScreen();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    showToast(getResources().getString(R.string.api_calling_failed));
                    Utils.logDebug(TAG, " onFailureU : " + errorResponse.toString());
                    unLockScreen();

                }

                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    lockScreen();


                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                    unLockScreen();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}