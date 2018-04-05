package com.mobiledi.earnit.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.model.addTask.AddTaskWithSelecteDay;
import com.mobiledi.earnit.model.addTask.AddTaskWithSelecteDayResponse;
import com.mobiledi.earnit.model.addTask.RepititionSchedule;
import com.mobiledi.earnit.model.getChild.GetAllChildResponse;
import com.mobiledi.earnit.model.goal.GetAllGoalResponse;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.retrofit.RetrofitClient;
import com.mobiledi.earnit.stickyEvent.MessageEvent;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.NavigationDrawer;
import com.mobiledi.earnit.utils.ScreenSwitch;
import com.mobiledi.earnit.utils.Utils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import io.reactivex.Observable;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.mobiledi.earnit.R.id.task_name;


public class AddTask extends BaseActivity implements View.OnClickListener, NavigationDrawer.OnDrawerToggeled {

    public Parent parentObject;
    public Child childObject, otherChild;
    @BindView(R.id.save) Button save;
    @BindView(R.id.cancel) Button cancel;
    @BindView(R.id.newtask_requirephoto) Button checkbox;
    @BindView(R.id.newtask_screenlockcheck) Button screenlockBtn;
    @BindView(R.id.task_name) EditText taskName;
    @BindView(R.id.task_detail) EditText taskDetails;
    @BindView(R.id.task_amount) EditText amountTxt;
    @BindView(R.id.date_time_textview) TextView date_time_textview;
    @BindView(R.id.add_task_header) TextView childName;
    @BindView(R.id.assign_to_id) TextView assignTo;
    @BindView(R.id.add_task_avatar) CircularImageView childAvatar;
    @BindView(R.id.due_date) ImageView dueDate;
    String NONE = "None";
    AddTask addTask;
    Map<Integer, String> childs;
    List<String> assignChild;
    int childID;
    Intent intent;
    final int TASK_NAME_LENGTH = 40;
    boolean checkboxStatus = false;
    boolean checkboxStatusLock = false;

    boolean IS_EDITING_TASK = false;
    private final String TAG = "AddTask";
    TextView repeatSpinner, assignSpinner;
    @BindView(R.id.loadingPanel) RelativeLayout progressBar;
    String screen_name;
    private String goalName;
    private String repeat = "";
    ArrayList<String> list;
    String repeats[];
    ArrayList<Goal> goalList = new ArrayList<>();
    Tasks currentTask;
    DateTimeFormatter fmt;
    ScreenSwitch screenSwitch;
    int repeatCount = 0;
    ArrayList<Item> repeatList, goalsList;
    private BottomSheetDialog mBottomSheetDialog;
    int fetchGoalId = 0;
    @BindView(R.id.toolbar_add) Toolbar goalToolbar;
    @BindView(R.id.drawerBtn) ImageButton drawerToggle;
    List<Child> childList = new ArrayList<>();
    int childsCounter = 0;
    AddTaskModel.repititionSchedule repititionSchedule;
    @BindView(R.id.addtask_back_arrow) ImageButton back;
    @BindView(R.id.addtask_helpicon)ImageButton    addTask_help;
    private Calendar c;
    Integer retry = 0;

    MessageEvent m ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task_layout);
        ButterKnife.bind(this);
        setSupportActionBar(goalToolbar);
        //getSupportActionBar().setTitle(null);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelAndBack(childObject, otherChild);
            }
        });
        addTask = this;
        screenSwitch = new ScreenSwitch(addTask);

        intent = getIntent();
        screen_name = intent.getStringExtra(AppConstant.FROM_SCREEN);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        repititionSchedule = null;
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        childID = childObject.getId();
        Log.e(TAG, "Child ID: "+childID);
        amountTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                boolean t = amountTxt.getText().toString().matches("");
                if (!b && !t) {
                    amountTxt.setText(String.format(Locale.US, "%.2f", Double.parseDouble(amountTxt.getText().toString())));

                }
            }
        });
        NavigationDrawer navigationDrawer = new NavigationDrawer(addTask, parentObject, goalToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, 0);
        navigationDrawer.setOnDrawerToggeled(this);
        fmt = DateTimeFormat.forPattern(AppConstant.DATE_FORMAT);
        list = new ArrayList<>();
        list.add(NONE);
        repeatList = new ArrayList<>();
        goalsList = new ArrayList<>();
        goalsList.add(new Item(0, NONE));
        repeatList.add(new Item(childsCounter++, NONE));

        // if(childObject !=null)
//        repeatList.add(new Item(1, childObject.getFirstName()));
        //    if(otherChild != null)
        //      repeatList.add(new Item(2, otherChild.getFirstName()));

        callGoalService(parentObject.getEmail(), parentObject.getPassword(), childObject.getId());

        childName.setText(childObject.getFirstName());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(350,350);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.placeholder(R.drawable.default_avatar);
        requestOptions.error(R.drawable.default_avatar);

        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL+childObject.getAvatar())
                .into(childAvatar);


/*
        try {
            Picasso.with(addTask).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(childAvatar);
        } catch (Exception e) {
            Picasso.with(addTask).load(R.drawable.default_avatar).into(childAvatar);
            e.printStackTrace();
            Log.e(TAG, "Error: "+e.getLocalizedMessage());
        }*/

        repeatSpinner = (TextView) findViewById(R.id.apply_to_goal_spinner);
        assignSpinner = (TextView) findViewById(R.id.assign_to_id);
        childs = (Map<Integer, String>) intent.getSerializableExtra(AppConstant.CHILD_MAP);
        assignChild = new LinkedList<>(Arrays.asList(childObject.getFirstName()));
        assignTo.setText(childObject.getFirstName());
        save.setOnClickListener(addTask);
        cancel.setOnClickListener(addTask);
        dueDate.setOnClickListener(addTask);
        checkbox.setOnClickListener(addTask);
        childAvatar.setOnClickListener(addTask);
        assignTo.setOnClickListener(addTask);
        repeatSpinner.setOnClickListener(addTask);
        addTask_help.setOnClickListener(addTask);
        back.setOnClickListener(addTask);
        screenlockBtn.setOnClickListener(addTask);
        if (isDeviceOnline())
            fetchChildList();
        //fetchAllChildList();

        taskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > TASK_NAME_LENGTH) {
                    taskName.setError(getResources().getString(R.string.task_name_length));
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

        InputFilter filter = new InputFilter() {
            final int maxDigitsBeforeDecimalPoint = 6;
            final int maxDigitsAfterDecimalPoint = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuilder builder = new StringBuilder(dest);
                builder.replace(dstart, dend, source
                        .subSequence(start, end).toString());
                if (!builder.toString().matches(
                        "(([1-9]{1})([0-9]{0," + (maxDigitsBeforeDecimalPoint - 1) + "})?)?(\\.[0-9]{0," + maxDigitsAfterDecimalPoint + "})?"

                )) {
                    if (source.length() == 0)
                        return dest.subSequence(dstart, dend);
                    return "";
                }

                return null;

            }
        };


        //CHECK IF IT IS A EDIT TASK REQUEST
        if (intent.getSerializableExtra(AppConstant.TO_EDIT) != null && !screen_name.equalsIgnoreCase(AppConstant.ADD_TASK)) {
            currentTask = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);
            IS_EDITING_TASK = true;
            autoFill(currentTask);
            if (currentTask.getGoal() != null) {
                fetchGoalId = currentTask.getGoal().getId();
                goalName = currentTask.getGoal().getGoalName();
                repeatSpinner.setText(goalName.substring(0, 1).toUpperCase() + goalName.substring(1));
            } else {
                fetchGoalId = 0;
                repeatSpinner.setText(NONE);
                assignSpinner.setText(assignTo.getText().toString());
            }
            if (currentTask.getRepititionSchedule() != null) {
                repeat = currentTask.getRepititionSchedule().getRepeat();
            } else {
                repeat = NONE;
            }

        } else {
            currentTask = null;
            repeatSpinner.setText(NONE);
        }
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.due_date:
                if (!TextUtils.isEmpty(taskName.getText().toString())) {


                    Intent parentCalendarAcitivity = new Intent(AddTask.this, ParentCalendarActivity.class);

                    parentCalendarAcitivity.putExtra(AppConstant.PARENT_OBJECT, parentObject);
                    parentCalendarAcitivity.putExtra(AppConstant.FROM_SCREEN, screen_name);
                    parentCalendarAcitivity.putExtra(AppConstant.CHILD_OBJECT, childObject);
                    parentCalendarAcitivity.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);

                    parentCalendarAcitivity.putExtra("title", taskName.getText().toString());
                    parentCalendarAcitivity.putExtra("title2", childName.getText().toString());

                    Log.e("testing image", "" + childObject.getAvatar());
                    parentCalendarAcitivity.putExtra(AppConstant.CHILD_OBJECT, childObject);
                    parentCalendarAcitivity.putExtra(AppConstant.CHILD_OBJECT, childObject);

                    parentCalendarAcitivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(parentCalendarAcitivity);
                } else {

                    Toast.makeText(AddTask.this, "Add Task Title", Toast.LENGTH_LONG).show();
                }
                break;
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
                    // Intent intent = new Intent(this, UploadTaskImageFragment.class);
                    //  startActivity(intent);
                }
                break;
            case R.id.newtask_screenlockcheck:


                Intent screenrule = new Intent(AddTask.this, ScreenRules.class);
                screenrule.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                screenrule.putExtra(AppConstant.FROM_SCREEN, screen_name);
                screenrule.putExtra(AppConstant.PARENT_OBJECT, parentObject);
                screenrule.putExtra(AppConstant.CHILD_OBJECT, childObject);
                screenrule.putExtra(AppConstant.OTHER_CHILD_OBJECT, otherChild);
                startActivity(screenrule);


                if (checkboxStatusLock) {
                    screenlockBtn.setText("");
                    checkboxStatusLock = false;
                } else {
                    screenlockBtn.setText("✔");
                    checkboxStatusLock = true;
                }
                /*   Intent screenrule = new Intent(AddTask.this,ScreenRules.class);
                    screenrule.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(screenrule);
                }*/

//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.addtask_framelayout, new ScreenRuleFragment());
//                ft.commit();
                break;
            case R.id.assign_to_id:

                showBottomSheetDialog(repeatList, assignSpinner, AppConstant.CHILD);

                break;
            case R.id.save:
                if (taskName.getText().toString().trim().length() > 0) {
                    if (taskName.getText().toString().trim().length() <= TASK_NAME_LENGTH) {

                        Log.e(TAG, "Amount= "+amountTxt.getText().toString());
                        if (amountTxt.getText().toString().trim().length() > 0)
                        {
                            m = EventBus.getDefault().getStickyEvent(MessageEvent.class);

                            if(m!=null)
                            {
                                if(m.getResponse().onDay!=null)
                                {

                                    if(m.getResponse().onDay.equals("")||m.getResponse().onFirst.equals(""))
                                    {
                                        Log.e(TAG, "Both are empty");
                                        saveTask();
                                    }
                                    else
                                    {
                                        Log.e(TAG, "Both are not empty");
                                        saveTaskWithSelectedDays();
                                    }
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Please add due date", Toast.LENGTH_LONG).show();
                            }





                          //  saveTask();
                        }

                        else
                        Utils.showToast(this, "Add amount");


                    } else
                        showToast(getResources().getString(R.string.task_name_too_long));
                } else
                    showToast(getResources().getString(R.string.please_enter_task_name));
                break;

            case R.id.add_task_avatar:
                if (currentTask != null)
                    new FloatingMenu(addTask).fetchAvatarDimension(childAvatar, childObject, childObject, parentObject, AppConstant.ADD_TASK, progressBar, currentTask);
                else
                    new FloatingMenu(addTask).fetchAvatarDimension(childAvatar, childObject, childObject, parentObject, AppConstant.ADD_TASK, progressBar, null);
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
        }
    }

    private void saveTaskWithSelectedDays() {

        try {
            repititionSchedule = m.getResponse();
            DateTime due = new DateTime();
            DateTimeZone tz = DateTimeZone.getDefault();
            Long instant = DateTime.now().getMillis();
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(m.getResponse().onDay);

            long offsetInMilliseconds = tz.getOffset(instant);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/DD/yyyy hh:mm:ss aaa", Locale.getDefault());

            simpleDateFormat.setTimeZone(TimeZone.getDefault());

            Date date = (Date) simpleDateFormat.parse(repititionSchedule.getDate() + " " + repititionSchedule.getEndTime());
            DateTime dateTime = new DateTime(date);
            long dueTime = dateTime.getMillis() + offsetInMilliseconds;
            //"yyyyy.MMMMM.dd GGG hh:mm aaa"
            //"EEE, d MMM yyyy HH:mm:ss Z"
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy hh:mm:ss aaa");
            String dateString = formatter.format(new Date(dueTime));

            Log.e(TAG, "Date String = "+dateString);
            Log.e(TAG, "EVERY N DAY: "+m.getResponse().getEveryNday());
            RepititionSchedule repititionSchedule = new RepititionSchedule(m.getResponse().startTime,
                    m.getResponse().endTime, m.getResponse().repeat, m.getResponse().everyNday,
                    m.getResponse().onFirst, arrayList );

            com.mobiledi.earnit.model.addTask.Children child = new com.mobiledi.earnit.model.addTask.Children ();
            childID = childObject.getId();
            Log.e(TAG, "ChildID= "+childID);
            child.setId(childID);
            com.mobiledi.earnit.model.addTask.Goal goal = new com.mobiledi.earnit.model.addTask.Goal();
            if(fetchGoalId!=0)
            {
                goal.setId(fetchGoalId);
            }
            else
            goal=null;

            double value = Double.parseDouble(amountTxt.getText().toString());

            AddTaskWithSelecteDay addTaskWithSelecteDay = new AddTaskWithSelecteDay(value,
                    dateString, taskName.getText().toString().trim(), checkboxStatus, child, goal,
                    repititionSchedule, taskDetails.getText().toString(), false,
                    checkboxStatusLock

                    );



            RetroInterface retroInterface = RetrofitClient.getApiServices(parentObject.getEmail(),
                    parentObject.getPassword());


           Call<AddTaskWithSelecteDayResponse> call = retroInterface.addTAskWithSelectedDay(addTaskWithSelecteDay);
           call.enqueue(new Callback<AddTaskWithSelecteDayResponse>() {
               @Override
               public void onResponse(Call<AddTaskWithSelecteDayResponse> call, Response<AddTaskWithSelecteDayResponse> response) {
                  // Log.e(TAG, "Response: "+response.body().getName());

                   if(response.code() ==201)
                   showDialogOnTaskAdded(childObject, otherChild);
                   else
                       showToast("Unexpected Error Occured");
               }

               @Override
               public void onFailure(Call<AddTaskWithSelecteDayResponse> call, Throwable t) {
                   Log.e(TAG, "Error: "+t.getLocalizedMessage());
               }
           });

        }

        catch (Exception e)
        {
                Log.e(TAG, "ERROR: : "+e.getLocalizedMessage());
        }



    }



    private void saveTask() {

        JSONObject addTaskJson = new JSONObject();
        try {

            addTaskJson.put(AppConstant.CHILDREN, new JSONObject().put(AppConstant.ID, childID));
            if (fetchGoalId > 0)
                addTaskJson.put(AppConstant.GOAL, new JSONObject().put(AppConstant.ID, fetchGoalId));
            addTaskJson.put(AppConstant.ALLOWANCE, Double.parseDouble(amountTxt.getText().toString()));


            addTaskJson.put(AppConstant.NAME, taskName.getText().toString().trim());
            addTaskJson.put(AppConstant.DESCRIPTION, taskDetails.getText().toString());
            addTaskJson.put(AppConstant.PICTURE_REQUIRED, checkboxStatus);

            DateTime due = new DateTime();
            DateTimeZone tz = DateTimeZone.getDefault();
            Long instant = DateTime.now().getMillis();

            long offsetInMilliseconds = tz.getOffset(instant);
            addTaskJson.put(AppConstant.UPDATE_DATE, due.getMillis() + offsetInMilliseconds);
            addTaskJson.put(AppConstant.TASK_COMMENTS, new JSONArray());
            addTaskJson.put("shouldLockAppsIfTaskOverdue", checkboxStatusLock);

            Log.e(TAG, "Task full= "+addTaskJson);

            JSONObject repSchedule = new JSONObject();


            if (m != null) {
                repititionSchedule = m.getResponse();
                EventBus.getDefault().removeAllStickyEvents();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/DD/yyyy hh:mm:ss aaa", Locale.getDefault());

                simpleDateFormat.setTimeZone(TimeZone.getDefault());

                Date date = (Date) simpleDateFormat.parse(repititionSchedule.getDate() + " " + repititionSchedule.getEndTime());
                DateTime dateTime = new DateTime(date);
                addTaskJson.put(AppConstant.DUE_DATE, dateTime.getMillis() + offsetInMilliseconds);

            }

            if (repititionSchedule != null) {
                repSchedule.put(AppConstant.REPEAT, repititionSchedule.getRepeat());
                repSchedule.put(AppConstant.START_TIME, repititionSchedule.getStartTime());
                repSchedule.put(AppConstant.END_TIME, repititionSchedule.getEndTime());
                repSchedule.put("everyNRepeat", repititionSchedule.getEveryNday());
                for (int i = 0; i < goalList.size(); i++)
                    if (goalList.get(i).getGoalName().equals(goalName))
                        addTaskJson.put(AppConstant.GOAL, goalList.get(i).getId());


                if (Objects.equals(repititionSchedule.getRepeat(), "weekly")) {


                    JSONArray array = new JSONArray();
                    for (int i = 0; i < repititionSchedule.getSpecificDays().size(); i++)
                        array.put(repititionSchedule.getSpecificDays().get(i));
                    repSchedule.put(AppConstant.SPECIFIC_DAYS, array);
                    addTaskJson.put(AppConstant.REPITITION_SCHEDULE, repSchedule);


                } else if (Objects.equals(repititionSchedule.getRepeat(), "daily")) {
                    addTaskJson.put(AppConstant.REPITITION_SCHEDULE, repSchedule);

                } else if (Objects.equals(repititionSchedule.getRepeat(), "monthly")) {

                    JSONArray array = new JSONArray();
                    for (int i = 0; i < repititionSchedule.getSpecificDays().size(); i++)
                        array.put(repititionSchedule.getSpecificDays().get(i));
                    repSchedule.put(AppConstant.SPECIFIC_DAYS, array);

                    addTaskJson.put(AppConstant.REPITITION_SCHEDULE, repSchedule);

                }

                Log.e(TAG, "Add Task Value= : "+addTaskJson);
                Utils.logDebug(TAG, "add_task_json :" + String.valueOf(addTaskJson));

                StringEntity entity = new StringEntity(addTaskJson.toString());
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
                AsyncHttpClient httpClient = new AsyncHttpClient();
                httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
                PersistentCookieStore myCookieStore = new PersistentCookieStore(addTask);
                httpClient.setCookieStore(myCookieStore);

                if (IS_EDITING_TASK) {
                    Utils.logDebug(TAG, "calling task edit api");
                    Log.e("testing url", "" + AppConstant.BASE_URL + AppConstant.TASKS_API);
                    httpClient.put(this, AppConstant.BASE_URL + AppConstant.TASKS_API, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Utils.logDebug(TAG, "calling success : " + response);
                            showDialogOnTaskAdded(childObject, otherChild);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            Utils.logDebug(TAG, "calling success : " + response);
                            showDialogOnTaskAdded(childObject, otherChild);
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
                } else {
                    Utils.logDebug(TAG, "calling task add api");
                    Log.e(TAG, "testing url "+ "" + AppConstant.BASE_URL + AppConstant.TASKS_API);
                    Log.e(TAG, "JSON: "+ entity);

                    httpClient.post(this, AppConstant.BASE_URL + AppConstant.TASKS_API, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Utils.logDebug(TAG, "calling success : " + response);
                            //    fetchChildTaskList();
                            showDialogOnTaskAdded(childObject, otherChild);


                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            Utils.logDebug(TAG, "calling success : " + response);
                            //   fetchChildTaskList();
                            showDialogOnTaskAdded(childObject, otherChild);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Utils.logDebug(TAG, "calling onFailure : " + errorResponse.toString());
                            Utils.showToast(addTask, "Internal Server Error");
                            unLockScreen();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.i(TAG, "calling onFailure : " + errorResponse.toString());
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

                }


            } else
                Toast.makeText(getApplicationContext(), "Please add due date", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onCancelAndBack(childObject, otherChild);
    }

    public void onCancelAndBack(Child child, Child otherChild) {
        Utils.logDebug(TAG, "calling onCancelAndBack method ");
        if (screen_name.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN)) {
            if (otherChild.getTasksArrayList().size() > 0)
                screenSwitch.moveToAllTaskScreen(child, otherChild, screen_name, parentObject, AppConstant.ADD_TASK);
            else
                Toast.makeText(addTask, getResources().getString(R.string.no_task_available), Toast.LENGTH_LONG).show();
        } else if (screen_name.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
            if (child.getTasksArrayList().size() > 0)
                screenSwitch.moveToAllTaskScreen(child, otherChild, screen_name, parentObject, AppConstant.ADD_TASK);
            else
                Toast.makeText(addTask, getResources().getString(R.string.no_task_for_approval), Toast.LENGTH_LONG).show();
        } else if (screen_name.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD))
            screenSwitch.moveToParentDashboard(parentObject);
        else if (screen_name.equalsIgnoreCase(AppConstant.GOAL_SCREEN))
            screenSwitch.isGoalExists(child, otherChild, parentObject, progressBar, screen_name, currentTask);
        else if (screen_name.equalsIgnoreCase(AppConstant.BALANCE_SCREEN))
            screenSwitch.checkBalance(child, otherChild, parentObject, AppConstant.ADD_TASK, currentTask, progressBar, AppConstant.PARENT);
        else {
            screen_name = AppConstant.PARENT_DASHBOARD;
            screenSwitch.moveToParentDashboard(parentObject);
        }
    }


    public void callGoalService(String email, String password, int childId) {

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBasicAuth(email, password);
            client.get(AppConstant.BASE_URL + AppConstant.GOAL_API + childId, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    Log.e(TAG, "Goal Response= "+response);
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            Goal goal = new GetObjectFromResponse().getGoalObject(object);

                            if (goal.getTally() < goal.getAmount())
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
        message.setText(getResources().getString(R.string.add_another_task));
        Button declineButton = (Button) dialog.findViewById(R.id.cancel);
        declineButton.setText(AppConstant.NO);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                chechUserTasks();
            }
        });
        Button acceptButton = (Button) dialog.findViewById(R.id.ok);
        acceptButton.setText(AppConstant.YES);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                screenSwitch.moveToAddTask(child, otherChild, parentObject, screen_name, null);
            }
        });
        dialog.show();
    }


    private void showBottomSheetDialog(ArrayList<Item> items, final TextView dropDownView, final String type) {


        mBottomSheetDialog = new BottomSheetDialog(this);
        final View view = getLayoutInflater().inflate(R.layout.sheet, null);
        RecyclerView recyclerView =  view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new ItemAdapter(items, new ItemAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {
                if (type.equalsIgnoreCase(AppConstant.GOAL)) {
                    for (int i = 0; i < goalList.size(); i++) {
                        if (item.getId() != 0) {
                            //fetchGoalId = goalList.get(i).getId();
                            Log.e(TAG, "Goal ID= "+ fetchGoalId);
                        }

                    }
                }
                if (type.equals(AppConstant.CHILD)) {
                    for (int i = 0; i < childList.size(); i++) {
                        if (item.getId() != 0) {
                            if (item.getId() - 1 == childList.get(i).getId()) {
                                childObject = childList.get(i);
                                //childID = childList.get(i).getId();
                                Log.e(TAG, "Name= "+childList.get(i).getFirstName());
                            }
                        }

                    }
                }

                showToast(item.getTitle());

                if(type.equalsIgnoreCase(AppConstant.CHILD))
                    childID = item.getId();
                else
                    fetchGoalId = item.getId();
                Log.e(TAG, "GOal id;::= "+childID);
                Log.e(TAG, "GOal id;::= "+fetchGoalId);
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

    @Override
    public void onDrawerToggeled() {

    }

    public void fetchAllChildList()
    {

        RetroInterface retroInterface = RetrofitClient.getApiServices(childObject.getEmail(), childObject.getPassword());
        Call<List<GetAllChildResponse>> response = retroInterface.getAllChild(parentObject.getAccount().getId());

        response.enqueue(new Callback<List<GetAllChildResponse>>() {
            @Override
            public void onResponse(Call<List<GetAllChildResponse>> call, Response<List<GetAllChildResponse>> response) {

            }

            @Override
            public void onFailure(Call<List<GetAllChildResponse>> call, Throwable t) {

            }
        });

    }

    public void fetchChildList() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        client.setMaxRetriesAndTimeout(3, 3000);

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

    private void chechUserTasks() {
        retry = 0;
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
                    screenSwitch.moveToAllTaskScreen(childObject, childObject, screen_name, parentObject, screen_name);
                }
            }
        });
    }

}
