package com.firepitmedia.earnit.activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.adapter.ItemAdapter;
import com.firepitmedia.earnit.dialogfragment.MonthlyDialogFragment;
import com.firepitmedia.earnit.dialogfragment.MyDialogFragment;
import com.firepitmedia.earnit.dialogfragment.TimePickerFragment;
import com.firepitmedia.earnit.dialogfragment.WeeklyDialogFragment;
import com.firepitmedia.earnit.model.AddTaskModel;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Item;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.stickyEvent.MessageEvent;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.FloatingMenu;
import com.firepitmedia.earnit.utils.NavigationDrawer;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.firepitmedia.earnit.utils.Utils;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.extras.Base64;
import io.blackbox_vision.materialcalendarview.view.CalendarView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;


import java.util.Objects;

/**
 * Created by GreenLose on 12/8/2017.
 */

public class ParentCalendarActivity extends BaseActivity implements View.OnClickListener, NavigationDrawer.OnDrawerToggeled, MainView, WeeklyDialogFragment.WeeklyDialogListener, MonthlyDialogFragment.MonthlyDialogListener, MyDialogFragment.DailyDialogListener {

    public String checkValue = "none";
    public String days;
    private final MainPresenter presenter = new MainPresenter(this);
    private static final String MONTH_TEMPLATE = "MMMM yyyy";
    ParentCalendarActivity parentCalendarActivity;
    public Parent parentObject;
    @BindView(R.id.addtask_helpicon)
    ImageButton helpIcon;
    ImageButton goback;
    @BindView(R.id.save)
    Button saveBtn;
    @BindView(R.id.cancel)
    Button cancelBtn;
    CircularImageView childAvatar;
    TextView childName;
    public Child childObject, otherChild;
    Intent intent;
    public List<String> monthList;
    @BindView(R.id.loadingPanel)
    RelativeLayout progressBar;
    Tasks currentTask;
    String screen_name;
    boolean IS_EDITING_TASK = false;
    int fetchGoalId = 0;
    String repeats[];
    int repeatCount = 0;
    ArrayList<Item> repeatList;
    ArrayList<String> list;
    String NONE = "None";
    Map<Integer, String> childs;
   /* @BindView(R.id.toolbar_add)
    Toolbar goalToolbar;*/
    String onFirst, onDay;
    private final String TAG = "ParentCalendarActivity";
    ScreenSwitch screenSwitch;
    TextView repeatTask;
    private BottomSheetDialog mBottomSheetDialog;
    private String repeat;
    @BindView(R.id.drawer_layout)
    RelativeLayout drawer;
    @BindView(R.id.calendar_view)
    CalendarView calendarView;
    @BindView(R.id.addtask_back_arrow)
    ImageButton headerBack;
    public static Boolean buttonClicked;
    List<String> weeklist = new ArrayList<>();
    @BindView(R.id.rule_apply_from)
    TextView startTimer;
    String dayOfWeek;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/DD/yyyy");
    String finalDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_calendar);
        ButterKnife.bind(this);
        buttonClicked = false;
        parentCalendarActivity = this;
        screenSwitch = new ScreenSwitch(parentCalendarActivity);

        //setSupportActionBar(goalToolbar);
        //getSupportActionBar().setTitle(null);

        finalDate = new DateTime().toString("MM/dd/yyyy", Locale.US);
        Log.e("AddTaskk", "1 final date = " + finalDate);
        presenter.addCalendarView();
        presenter.animate();

        startTimer.setText("8:30:00 AM");
        drawer.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);

        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setTextView(startTimer);
                newFragment.show(getFragmentManager(), "TimePicker");

                /*TimeSelectUtilsForCallendar timeSelectUtils = new TimeSelectUtilsForCallendar(ParentCalendarActivity.this, null, startTimer, new TimeSelectUtilsForCallendar.GetSubmitTime() {
                    @Override
                    public void selectTime(String startDate) {
                        startTimer.setText(startDate.toString());


                    }
                });
                timeSelectUtils.dateTimePicKDialog();*/
            }
        });


        intent = getIntent();
        String name = intent.getStringExtra("title");
        intent.getStringExtra("image");
        TextView textView = (TextView) findViewById(R.id.add_task_header2);
        textView.setText(name);
        TextView textView1 = (TextView) findViewById(R.id.add_task_header1);
        textView1.setText(name);
        TextView textView2 = (TextView) findViewById(R.id.add_task_header3);
        textView2.setText(name);

        screen_name = intent.getStringExtra(AppConstant.FROM_SCREEN);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        childName = (TextView) findViewById(R.id.add_task_header);
        if (childObject != null)
            childName.setText(childObject.getFirstName());
        list = new ArrayList<>();
        list.add(NONE);
        repeatList = new ArrayList<>();
        repeats = getResources().getStringArray(R.array.repeat_frequency);
        for (String s : repeats) {
            repeatList.add(new Item(repeatCount, s));
            repeatCount++;
        }

        helpIcon.setOnClickListener(parentCalendarActivity);
        saveBtn.setOnClickListener(parentCalendarActivity);
        cancelBtn.setOnClickListener(parentCalendarActivity);
        headerBack.setOnClickListener(parentCalendarActivity);

//        callGoalService(parentObject.getEmail(), parentObject.getPassword(), childObject.getId());
        childAvatar = (CircularImageView) findViewById(R.id.add_task_avatar);


        if (childObject != null) {

            updateAvatar(childObject, childAvatar);


        }

        repeatTask = (TextView) findViewById(R.id.parentrepeat_frequency);
        repeatTask.setOnClickListener(parentCalendarActivity);
        childAvatar.setOnClickListener(parentCalendarActivity);

        childs = (Map<Integer, String>) intent.getSerializableExtra(AppConstant.CHILD_MAP);
        //CHECK IF IT IS A EDIT TASK REQUEST
        if (intent.getSerializableExtra(AppConstant.TO_EDIT) != null && !screen_name.equalsIgnoreCase(AppConstant.ADD_TASK)) {
            currentTask = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);
            IS_EDITING_TASK = true;
            if (currentTask.getGoal() != null) {
                fetchGoalId = currentTask.getGoal().getId();
            } else {
                fetchGoalId = 0;
            }
            if (currentTask.getRepititionSchedule() != null) {
                repeat = currentTask.getRepititionSchedule().getRepeat();
                repeatTask.setText(repeat.substring(0, 1).toUpperCase() + repeat.substring(1));
            } else {
                repeat = NONE;
                repeatTask.setText(NONE);
            }

        } else {
            currentTask = null;
            repeatTask.setText(NONE);
        }

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
                .error(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.icon)))
                .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.icon)))
                .into(imageView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addtask_helpicon:
                showToast(getResources().getString(R.string.parentcalendar));
                break;
            case R.id.save:
                Date date = new Date();

                //      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy  hh:mm:ss a");

                Log.e("testing final date", "" + finalDate);
                //  AppConstant.addTaskModel.setDueDate(finalDate);
                buttonClicked = true;
                if (checkValue.equalsIgnoreCase("daily")) {
                    if (dayOfWeek != null) {
                        AddTaskModel.repititionSchedule response = new AddTaskModel.repititionSchedule();
                        //  response.endTime = "11:00";
                        //  response.startTime = "10:00";
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                        sdf.format(new Date());
                        response.endTime = startTimer.getText().toString();
                        response.startTime = sdf.format(new Date());
                        response.repeat = "daily";
                        if (dayOfWeek != null)
                            response.setEveryNday(Integer.parseInt(dayOfWeek));
                        else
                            response.setEveryNday(0);

                        response.setDate(finalDate);
                        //AppConstant.addTaskModel.setRepititionSchedule(response);

                        response.setEveryNday(Integer.parseInt(dayOfWeek));
                        EventBus.getDefault().postSticky(new MessageEvent(response));

                        finish();
                    } else {
                        showToast("Enter Number of days");
                    }


                } else if (checkValue.equalsIgnoreCase("week")) {

                    AddTaskModel.repititionSchedule response = new AddTaskModel.repititionSchedule();
                    //     response.endTime = "11:00";
                    //      response.startTime = "10:00";
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");

                    sdf.format(new Date());
                    response.endTime = startTimer.getText().toString();
                    response.startTime = sdf.format(new Date());
                    response.repeat = "weekly";
                    response.setSpecificDays(weeklist);
                    response.setDate(finalDate);
                    if (dayOfWeek != null)
                        response.setEveryNday(Integer.parseInt(dayOfWeek));
                    else
                        response.setEveryNday(0);

                    //  AppConstant.addTaskModel.setRepititionSchedule(response);
                    EventBus.getDefault().postSticky(new MessageEvent(response));
                    finish();


                } else if (checkValue.equalsIgnoreCase("month")) {

                    AddTaskModel.repititionSchedule response = new AddTaskModel.repititionSchedule();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                    sdf.format(new Date());
                    response.endTime = startTimer.getText().toString();
//                    response.startTime = sdf.format(new Date());
                    response.startTime = new DateTime().toString("HH:mm:ss a");

                    if (onFirst != null){
                        Log.e("AddTaskk", "On first is not null");
                        Log.e("AddTaskk", onFirst);
                        Log.e("AddTaskk", onDay);
                        response.onFirst = onFirst;
                        response.onDay = onDay;
                    } else {
                        Log.e("AddTaskk", "On first is null");
                        //Log.e(TAG, onFirst);
                        // Log.e(TAG, onDay);
                        response.onFirst = "";
                        response.onDay = "";

                    }


                    response.repeat = "monthly";
                    response.setDate(finalDate);
                    if (dayOfWeek != null)
                        response.setEveryNday(Integer.parseInt(dayOfWeek));
                    else
                        response.setEveryNday(0);
                    response.setSpecificDays(monthList);

                    // AppConstant.addTaskModel.setRepititionSchedule(response);
                    EventBus.getDefault().postSticky(new MessageEvent(response));
                    finish();

                } else {
                    AddTaskModel.repititionSchedule response = new AddTaskModel.repititionSchedule();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                    sdf.format(new Date());
                    response.endTime = startTimer.getText().toString();
                    response.startTime = sdf.format(new Date());

                    response.setDate(finalDate);
                    Log.d("dsiudi", "save click. startTime = " + response.startTime);
                    Log.d("dsiudi", "save click. endTime = " + response.endTime);
                    Log.d("dsiudi", "save click. date = " + finalDate);


                    // AppConstant.addTaskModel.setRepititionSchedule(response);
                    EventBus.getDefault().postSticky(new MessageEvent(response));
                    finish();

                }

                Log.d("dosidoi", "date = " + finalDate + "getStickyEvent: " + EventBus.getDefault().getStickyEvent(MessageEvent.class).toString());
                break;
            case R.id.cancel:
                onBackPressed();
                break;
            case R.id.addtask_back_arrow:
                onBackPressed();
                break;
            case R.id.add_task_avatar:

                new FloatingMenu(this).fetchAvatarDimension(childAvatar, childObject, otherChild, parentObject, AppConstant.PARENT_CALENDAR_SCREEN, progressBar, currentTask);

                break;
            case R.id.parentrepeat_frequency:
                showBottomSheetDialog(repeatList, repeatTask, AppConstant.REPEAT);
                break;
        }
    }

    private void showBottomSheetDialog(ArrayList<Item> repeatList, final TextView dropDownView, final String type) {
        mBottomSheetDialog = new BottomSheetDialog(this);
        final View view = getLayoutInflater().inflate(R.layout.sheet, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemAdapter(repeatList, new ItemAdapter.ItemListener() {
            @Override
            public void onItemClick(Item item) {
                if (type.equalsIgnoreCase(AppConstant.GOAL)) {
                    fetchGoalId = item.getId();
                } else {
                    repeat = item.getmTitle();
                }
                //   showToast(item.getTitle());
                switch (item.getId()) {
                    case 0:
                        checkValue = "none";
                        break;
                    case 1: {
                        checkValue = "daily";
                        dayOfWeek = null;
                        MyDialogFragment dialogFrag = new MyDialogFragment();
                        FragmentManager fm = getFragmentManager();
                        dialogFrag.show(fm, getString(R.string.dialog_tag));
                    }
                    break;
                    case 2: {
                        checkValue = "week";
                        dayOfWeek = null;

                        WeeklyDialogFragment dialogFrag = new WeeklyDialogFragment();
                        FragmentManager fm = getFragmentManager();
                        dialogFrag.show(fm, getString(R.string.dialog_tag));
                    }
                    break;
                    case 3: {
                        checkValue = "month";
                        dayOfWeek = null;

                        MonthlyDialogFragment dialogFrag = new MonthlyDialogFragment();
                        FragmentManager fm = getFragmentManager();
                        dialogFrag.show(fm, getString(R.string.dialog_tag));
                    }
                    break;

                }
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

    public void onCancelAndBack() {
        if (screen_name.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
            screenSwitch.moveToParentDashboard(parentObject);
        } else if (screen_name.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN)) {
            if (otherChild.getTasksArrayList().size() > 0)
                screenSwitch.moveToAllTaskScreen(childObject, otherChild, screen_name, parentObject, AppConstant.BALANCE_SCREEN);
            else
                Utils.showToast(parentCalendarActivity, getResources().getString(R.string.no_task_available));
        } else if (screen_name.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
            if (childObject.getTasksArrayList().size() > 0)
                screenSwitch.moveToAllTaskScreen(childObject, otherChild, screen_name, parentObject, AppConstant.BALANCE_SCREEN);
            else
                Utils.showToast(parentCalendarActivity, getResources().getString(R.string.no_task_for_approval));
        } else if (screen_name.equalsIgnoreCase(AppConstant.ADD_TASK))
            screenSwitch.moveToAddTask(childObject, otherChild, parentObject, screen_name, currentTask);
        else if (screen_name.equalsIgnoreCase(AppConstant.GOAL_SCREEN))
            screenSwitch.isGoalExists(childObject, otherChild, parentObject, progressBar, screen_name, currentTask);
        else
            screenSwitch.moveToAllTaskScreen(childObject, otherChild, AppConstant.CHECKED_IN_SCREEN, parentObject, AppConstant.BALANCE_SCREEN);

    }

    @Override
    public void onBackPressed() {
        finish();

    }

    @Override
    public void onDrawerToggeled() {

    }

    @Override
    public void prepareCalendarView() {
        Calendar disabledCal = Calendar.getInstance();
        disabledCal.set(Calendar.DATE, disabledCal.get(Calendar.DATE) - 1);

        calendarView.setFirstDayOfWeek(Calendar.SUNDAY).setOnDateClickListener(new CalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(@NonNull Date selectedDate) {
                Log.d("dosidoi", "onDateClick date = " + new DateTime(selectedDate).toString("MM/dd/yyyy"));
                finalDate = new DateTime(selectedDate).toString("MM/dd/yyyy", Locale.US);
                Log.e("AddTaskk", "2 final date = " + finalDate);

            }
        });
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY).setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(@NonNull Date monthDate) {

                Log.d("dosidoi", "onMonthChange date = " + new DateTime(monthDate).toString("MM/dd/yyyy"));
                finalDate = new DateTime(monthDate).toString("MM/dd/yyyy", Locale.US);
                Log.e("AddTaskk", "3 final date = " + finalDate);
            }
        });
        calendarView.setOnDateLongClickListener(new CalendarView.OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull Date selectedDate) {

            }
        });


        calendarView.update(Calendar.getInstance(Locale.ROOT));
    }


    @Override
    public void animateViews() {
        calendarView.shouldAnimateOnEnter(true);
    }

    @Override
    public void updateResult(List<String> inputText, String s) {
        weeklist = inputText;
        dayOfWeek = s;
    }

    @Override
    public void updateResult2(List<String> inputText, String s) {
        for (int i = 0; i < inputText.size(); i++) {
            Log.e(TAG, "Input text= " + inputText.get(i));
        }
        monthList = inputText;
        dayOfWeek = s;
    }

    @Override
    public void updateOnDay(String onFirst, String onDay, String s) {

        Log.e(TAG, "Upate on day method");
        this.onFirst = onFirst;
        this.onDay = onDay;
        dayOfWeek = s;

    }

    @Override
    public void updateResult(String inputText, String s) {
        days = inputText;
        dayOfWeek = s;
    }

    /*@SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private int mNumberPickerInputId = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog tpd = new TimePickerDialog(getActivity(), R.style.TimePickerTheme
                    , this, hour, minute, false);
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setMinimumWidth(0);
            linearLayout.setMinimumHeight(0);
            tpd.setCustomTitle(linearLayout);
            return tpd;
        }


        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String aMpM = "AM";
            if (hourOfDay > 11) {
                aMpM = "PM";
            }

            int currentHour;
            if (hourOfDay > 11) {

                currentHour = hourOfDay - 12;

                if (currentHour == 0)
                    currentHour = 12;


            } else {
                currentHour = hourOfDay;
                if (currentHour == 0)
                    currentHour = 12;
            }


            startTimer.setText(String.valueOf(currentHour)
                    + ":" + String.valueOf(minute) + ":" + "00" + " " + aMpM);

        }

    }*/
}
