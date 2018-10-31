package com.firepitmedia.earnit.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.ChildsTaskObject;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.model.goal.GetAllGoalResponse;
import com.firepitmedia.earnit.retrofit.RetroInterface;
import com.firepitmedia.earnit.retrofit.RetrofitClient;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.FloatingMenu;
import com.firepitmedia.earnit.utils.GetObjectFromResponse;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.extras.Base64;
import io.blackbox_vision.materialcalendarview.view.CalendarView;
import io.blackbox_vision.materialcalendarview.view.DayView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by GreenLose on 12/8/2017.
 */

public class ChildCalendarActivity extends AppCompatActivity implements View.OnClickListener, MainView {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    ChildCalendarActivity childCalendarActivity;
    private final MainPresenter presenter = new MainPresenter(this);
    private static final String MONTH_TEMPLATE = "MMMM yyyy";

    @BindView(R.id.bottom2)
    TextView bottom_2;
    Integer taskCOunter = 0;
    List<Tasks> tasksList = new ArrayList<>();
    @BindView(R.id.drawer_layout)
    RelativeLayout drawer;
    @BindView(R.id.calendar_view)
    CalendarView calendarView;
    @BindView(R.id.back_to_tasklist)
    Button backlisttask;
    @BindView(R.id.childcaledar_hearback)
    ImageButton headerbackBtn;
    @BindView(R.id.childcalendar_backarrow)
    ImageButton backarrow;
    @BindView(R.id.childcalendar_forward_arrow)
    ImageButton forwardarrow;
    @BindView(R.id.add_task_avatar)
    CircularImageView childAvatar;
    Tasks currentTask;
    String screen_name;
    public Parent parentObject;
    public Child childObject, otherChild;
    Intent intent;
    @BindView(R.id.loadingPanel)
    RelativeLayout progressBar;
    private String finalDate;
    private Map<String, Integer> badges;
    private List<TextView> badgesTextViews;
    private ArrayList<ChildsTaskObject> childTasksObject;
    private int calendarMonth;

    private ArrayList<Tasks> allTasksForCurrentMonth;

    String TAG = ChildCalendarActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.childcalendar);
        childCalendarActivity = this;
        ButterKnife.bind(this);
        badges = new HashMap<>();
        badgesTextViews = new ArrayList<>();
        presenter.addCalendarView();
        presenter.animate();
        bottom_2.setText("Chose Goal Name");
        intent = getIntent();
        calendarMonth = new DateTime().getMonthOfYear();
        Log.d("dbopc", "\nonCreate. calendarMonth = " + calendarMonth);
        screen_name = intent.getStringExtra(AppConstant.FROM_SCREEN);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
//        childTasksObject = (ArrayList<ChildsTaskObject>) intent.getSerializableExtra(AppConstant.CHILD_TASKS_OBJECT);
        childTasksObject = MyApplication.getInstance().getChildsTaskObjects();
        Log.d("dsaldasluidj", "childTasksObject: " + childTasksObject.toString().replace("'", "\""));

        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        Log.d("dsjfhkj", "childObject tasks: " + childObject.toString());
        Log.e(TAG, "Child ID= " + childObject.getId());

        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        Log.e(TAG, "URL= " + "https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar());

        updateAvatar(childObject, childAvatar);


     /*   try {
            Picasso.with(childCalendarActivity).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(childAvatar);

        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(childCalendarActivity).load(R.drawable.default_avatar).into(childAvatar);
        }*/

        backarrow.setOnClickListener(childCalendarActivity);
        backlisttask.setOnClickListener(childCalendarActivity);
        forwardarrow.setOnClickListener(childCalendarActivity);
        headerbackBtn.setOnClickListener(childCalendarActivity);
        currentTask = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);
        childAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FloatingMenu(childCalendarActivity).fetchAvatarDimension253(null, childAvatar, childObject, parentObject, AppConstant.CHILD_DASHBOARD_SCREEN, progressBar, null);

            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MM/dd", Locale.getDefault());
        finalDate = sdf.format(new Date());
        tasksList.clear();
        final List<ChildsTaskObject> childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(childObject);
        for (int i = 0; i < childTaskObjects.size(); i++) {
            DateTime dateTime = new DateTime(childTaskObjects.get(i).getDueDate()).withTimeAtStartOfDay();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            if (toPrintDate.equals(finalDate)) {
                tasksList.addAll(childTaskObjects.get(i).getChildItemList());
            }


        }
        DateTime today = DateTime.now();
        setBadges(today.getMonthOfYear(), today.getYear());
        calendarView.setOnMonthChangeListener(calendarMonthListener());

        calendarView.setOnDateClickListener(new CalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(@NonNull Date selectedDate) {
                ArrayList<Tasks> tasks = new ArrayList<>();
                DateTime dateTime = new DateTime(selectedDate);
                ScreenSwitch screenSwitch = new ScreenSwitch(ChildCalendarActivity.this);
                Child child = getChild(childObject, dateTime);
                for (Tasks task : allTasksForCurrentMonth){
                    if (isToday(task, dateTime)){
                        tasks.add(task);
                    }
                }
                if (tasks.size() != 0) {
                    Log.d("dsjfhkj", "size() != 0");
                    if (tasks.size() > 1) {
                        Log.d("dsjfhkj", "size() > 1");
                        screenSwitch.moveTOChildDashboard(getChild(childObject, new DateTime(selectedDate)), true);
                    } else {
                        Log.d("dsjfhkj", "size() == 1");
                        Tasks task = tasks.get(0);
                        Intent requestTaskApproval = new Intent(ChildCalendarActivity.this, ChildRequestTaskApproval.class);
                        requestTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
                        if (child != null)
                            Log.d("sdasdvfdgdfh", "child = " + childObject.toString());
                        else
                            Log.d("sdasdvfdgdfh", "child = null");
                        requestTaskApproval.putExtra("previousActivityIsCalendar", true);
                        requestTaskApproval.putExtra(AppConstant.TASK_OBJECT, (Serializable) task);
                        requestTaskApproval.putExtra(AppConstant.GOAL_OBJECT, task.getGoal());
                        requestTaskApproval.putExtra(AppConstant.REPETITION_SCHEDULE, task.getRepititionSchedule());
                        requestTaskApproval.putExtra(AppConstant.PARENT_OBJECT, parentObject);
                        requestTaskApproval.putExtra(AppConstant.DUE_DATE_STRING, task.getDueDate());
                        startActivity(requestTaskApproval);
                    }
                } else {
                    showDialogOnTaskAdded(new DateTime(selectedDate));
                }
            }
        });
        getAllGoals();
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

    private boolean isToday(Tasks task, DateTime selectedDay){
        DateTime taskDate = new DateTime(task.getDueDate());
        return taskDate.withTimeAtStartOfDay().isEqual(selectedDay.withTimeAtStartOfDay());
    }

    private CalendarView.OnMonthChangeListener calendarMonthListener() {
        return new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(@NonNull Date monthDate) {
                DateTime currentMonth = new DateTime(monthDate);
                calendarMonth = currentMonth.getMonthOfYear();
                setBadges(currentMonth.getMonthOfYear(), currentMonth.getYear());
            }
        };
    }

    private void clearBadges() {
        badges.clear();
        for (TextView tv : badgesTextViews) {
            RelativeLayout rl = ((RelativeLayout) tv.getParent());
            if (rl != null)
                rl.removeView(tv);
        }
    }

    private boolean dateNear(int month, int year, DateTime dateTime) {
        return dateTime.getYear() == year && dateTime.getMonthOfYear() == month;
    }

    private void setBadges(int month, int year) {
        clearBadges();

        allTasksForCurrentMonth = new ArrayList<>();

        for (Tasks task : childObject.getTasksArrayList()) {
            if (!task.getStatus().equalsIgnoreCase(AppConstant.APPROVED)
                    && !task.getStatus().equalsIgnoreCase("deleted")) {
                Log.d("fdksjhflkj", "Task: " + task.toString());
                DateTime taskDate = new DateTime(task.getDueDate());
                if (task.getRepititionSchedule() == null) {
                    if (task.getStatus().equalsIgnoreCase("created") &&
                            dateNear(month, year, taskDate)) {
                        allTasksForCurrentMonth.add(task);
                    }
                } else {
                    DateTime endDate = new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(1).plusMonths(1).withTimeAtStartOfDay();
                    DateTime fakeDate = new DateTime(task.getDueDate());
                    Log.d("dkfjhdkj", "endDate = " + endDate.toString("dd.MM.yyyy HH:mm:ss"));
                    Log.d("dkfjhdkj", "fakeDate = " + fakeDate.toString("dd.MM.yyyy HH:mm:ss"));

                    if (fakeDate.isBefore(endDate)) {
                        int interval = 0;

                        do {
                            switch (task.getRepititionSchedule().getRepeat()) {
                                case "daily":
                                    fakeDate = fakeDate.plusDays(interval * task.getRepititionSchedule().getEveryNRepeat());
                                    Log.d("dkfjhdkj", "daily date: " + fakeDate.toString("dd.MM.yyyy HH:mm:ss") + "; (" + interval + " * " + task.getRepititionSchedule().getEveryNRepeat() + ")");
                                    break;
                                case "weekly":
                                    if (task.getRepititionSchedule().getSpecificDays() != null &&
                                            task.getRepititionSchedule().getSpecificDays().size() > 0) {
                                        for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                            Tasks newTask = Tasks.from(task);
                                            int day = newTask.getWeekAsInt(task.getRepititionSchedule().getSpecificDays().get(i));
                                            newTask.setStartDate(newTask.getDueDate());
                                            fakeDate = fakeDate.plusWeeks(i * task.getRepititionSchedule().getEveryNRepeat()).withDayOfWeek(day);
                                            if (!fakeDate.isBefore(new DateTime(newTask.getStartDate()))) {
                                                newTask.setDueDate(fakeDate.getMillis());
                                                GetObjectFromResponse.setStatusForTask(newTask);
                                                if (newTask.getStatus().equalsIgnoreCase("created") && dateNear(month, year, fakeDate)) {
                                                    allTasksForCurrentMonth.add(newTask);
                                                }
                                            }
                                        }
                                    }
                                    Log.d("dkfjhdkj", "weekly date: " + fakeDate.toString("dd.MM.yyyy HH:mm:ss"));
                                    break;
                                case "monthly":
                                    if (task.getRepititionSchedule().monthlyRepeatHasNumbers()) {
                                        Log.d("dkfjhdkj", "monthlyRepeatHasNumbers");
                                        fakeDate = fakeDate.plusMonths(interval * task.getRepititionSchedule().getEveryNRepeat());
                                        for (int i = 0; i < task.getRepititionSchedule().getSpecificDays().size(); i++) {
                                            Tasks newTask = Tasks.from(task);
                                            int day = Integer.parseInt(task.getRepititionSchedule().getSpecificDays().get(i));
                                            newTask.setStartDate(newTask.getDueDate());
                                            fakeDate = fakeDate.withDayOfMonth(day);
                                            newTask.setDueDate(fakeDate.getMillis());
                                            GetObjectFromResponse.setStatusForTask(newTask);
                                            if (newTask.getStatus().equalsIgnoreCase("created")
                                                    && dateNear(month, year, fakeDate)
                                                    && task.getDueDate() < newTask.getDueDate())
                                                allTasksForCurrentMonth.add(newTask);
                                        }
                                    } else {
                                        Log.d("dkfjhdkj", "!!!!monthlyRepeatHasNumbers");
                                        Tasks newTask = Tasks.from(task);
                                        newTask.setStartDate(newTask.getDueDate());

                                        int week = newTask.getWeekAsInt(newTask.getRepititionSchedule().getSpecificDays().get(0));
                                        int performTaskOnTheNSpecifiedDay = newTask.getPerformTaskOnTheNSpecifiedDay();

//                                    if (interval == 0) {
//                                        DateTime dayOfWeek = new DateTime(newTask.getStartDate()).withDayOfMonth(1).plusWeeks(performTaskOnTheNSpecifiedDay).withDayOfWeek(week).withTimeAtStartOfDay();
//                                        Log.d("weekDSKASDK", "dayOfWeek = " + dayOfWeek.toString("dd.MM.yyyy HH:mm:ss"));
//                                        Log.d("weekDSKASDK", "getStartDate = " + new DateTime(newTask.getStartDate()).withTimeAtStartOfDay().toString("dd.MM.yyyy HH:mm:ss"));
//                                        if (dayOfWeek.isBefore(new DateTime(task.getStartDate()).withTimeAtStartOfDay())
//                                                || dayOfWeek.isEqual(new DateTime(task.getStartDate()).withTimeAtStartOfDay())) {
//                                            Log.d("weekDSKASDK", "plusMonth++");
//                                            interval++;
//                                        }
//                                    }

                                        LocalTime localTime = new LocalTime(fakeDate.getMillis());

                                        fakeDate = fakeDate.plusMonths(interval * newTask.getRepititionSchedule().getEveryNRepeat())
                                                .withDayOfMonth(1)
                                                .minusDays(1)
                                                .withDayOfWeek(week)
                                                .plusWeeks(performTaskOnTheNSpecifiedDay);
                                        Log.d("sdlkjfhskj", "1 fakeDate = " + fakeDate.toString("dd.MM.yyyy HH:mm:ss") + "; performTaskOnTheNSpecifiedDay = " + performTaskOnTheNSpecifiedDay);
                                        if (performTaskOnTheNSpecifiedDay == -1) {
                                            Log.d("sdlkjfhskj", "2 fakeDate = " + fakeDate.toString("dd.MM.yyyy HH:mm:ss"));
                                            fakeDate = fakeDate.withDayOfMonth(1).minusDays(-1 - 7).withDayOfWeek(week);
                                        }
                                        newTask.setDueDate(fakeDate.getMillis());
                                        GetObjectFromResponse.setStatusForTask(newTask);
                                        Log.d("weekDSKASDK", "newTask = " + newTask);
                                        if (newTask.getStatus().equalsIgnoreCase("created")
                                                && dateNear(month, year, fakeDate)
                                                && task.getDueDate() < newTask.getDueDate())
                                            allTasksForCurrentMonth.add(newTask);
                                    }
                                    Log.d("dkfjhdkj", "monthly date: " + fakeDate.toString("dd.MM.yyyy HH:mm:ss"));
                                    break;
                            }
                            interval++;
                            if (interval > 1)
                                interval = 1;
                            if (dateNear(month, year, fakeDate) && !task.getRepititionSchedule().getRepeat().equalsIgnoreCase("monthly")
                                    && !task.getRepititionSchedule().getRepeat().equalsIgnoreCase("weekly")) {

                                Tasks newTask = Tasks.from(task);
                                newTask.setStartDate(task.getDueDate());
                                newTask.setDueDate(fakeDate.getMillis());
                                allTasksForCurrentMonth.add(newTask);
                            }
                        } while (fakeDate.isBefore(endDate));
                    }
                }
            }
        }

        addViews(allTasksForCurrentMonth);
    }

    private void addViews(ArrayList<Tasks> t) {
        if (t.size() > 0) {
            for (Tasks task : t) {
                int i = 1;
                DateTime taskDate = new DateTime(task.getDueDate());
                Log.d("dsjfhkj", "taskDate.getMonthOfYear() = " + taskDate.getMonthOfYear());
                TextView tvCount;
                Log.d("dsjfhkj", "Task: " + task.getName() + "; taskDate = " + taskDate.toString());
                DayView dayView = calendarView.findViewByDate(taskDate.toDate());
                RelativeLayout parent = (RelativeLayout) dayView.getParent();
                if (badges.containsKey(taskDate.toString("dd/MM/yyyy"))) {
                    Log.d("dsjfhkj", "Contains");
                    tvCount = parent.findViewWithTag("tvCount");
                    i = badges.get(taskDate.toString("dd/MM/yyyy")) + 1;
                    badges.put(taskDate.toString("dd/MM/yyyy"), i);
                } else {
                    Log.d("dsjfhkj", "NOT Contains");
                    tvCount = createNewTextView();
                    badges.put(taskDate.toString("dd/MM/yyyy"), i);
                }
                tvCount.setText(String.valueOf(i));
                try {
                    parent.addView(tvCount);
                } catch (IllegalStateException e) {
                    parent.removeView(tvCount);
                    parent.addView(tvCount);
                }
                badgesTextViews.add(tvCount);
            }
        }
        Log.d("dsjfhkj", "badges size = " + badges.size());
    }

    private boolean datesEquals(DateTime firstDate, DateTime secondDate) {
        return secondDate.getDayOfMonth() == firstDate.getDayOfMonth() && secondDate.getMonthOfYear() == firstDate.getMonthOfYear() && secondDate.getYear() == firstDate.getYear();
    }

    public void showDialogOnTaskAdded(DateTime date) {
        final Dialog dialog = new Dialog(this);
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
        message.setText(getResources().getString(R.string.no_tasks, date.toString("MM/dd")));
        Button declineButton = (Button) dialog.findViewById(R.id.cancel);
        declineButton.setVisibility(View.GONE);
        Button acceptButton = (Button) dialog.findViewById(R.id.ok);
        acceptButton.setText(getString(R.string.ok));
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private Child getChild(Child child, DateTime taskDate) {
        Child returnChild = new Child();
        returnChild.setAccount(child.getAccount());
        returnChild.setAvatar(child.getAvatar());
        returnChild.setCreateDate(child.getCreateDate());
        returnChild.setEmail(child.getEmail());
        returnChild.setFcmToken(child.getFcmToken());
        returnChild.setFirstName(child.getFirstName());
        returnChild.setId(child.getId());
        returnChild.setLastName(child.getLastName());
        returnChild.setMessage(child.getMessage());
        returnChild.setPassword(child.getPassword());
        returnChild.setPhone(child.getPhone());
        returnChild.setUpdateDate(child.getUpdateDate());
        returnChild.setUserType(child.getUserType());
        ArrayList<Tasks> emptyTasks = new ArrayList<>();
        for (Tasks task : allTasksForCurrentMonth) {
            if (isToday(task, taskDate)) {
                Log.d("absdnb", "add task: " + new DateTime(task.getDueDate()).toString("dd.MM.yyyy HH:mm:ss"));
                emptyTasks.add(task);
            }
        }
        if (emptyTasks.size() != 0) {
            returnChild.setTasksArrayList(emptyTasks);
        } else {
            return null;
        }
        return returnChild;
    }

    private ArrayList<Tasks> addRepeatedTasks(ArrayList<Tasks> baseTasks) {
        ArrayList<Tasks> t = new ArrayList<>();
        for (Tasks task : baseTasks) {
            if (!task.getStatus().equals("Closed")) {
                Log.d("dbopc", "\nsetBadges. task status = " + task.getStatus() + ";    task.getDueDate() = " + new DateTime(task.getDueDate()).getMonthOfYear() + ";     calendarMonth = " + calendarMonth);
                if (task.getRepititionSchedule() == null && new DateTime(task.getDueDate()).getMonthOfYear() == calendarMonth) {
                    t.add(task);
                }
                if (task.getRepititionSchedule() != null) {
                    //ADD DAILY TASKS
                    if (task.getRepititionSchedule().getRepeat().equalsIgnoreCase("daily")) {
                        int repeatDays = getRepeatDays(task.getRepititionSchedule().getRepeat());
                        int everyNDays = task.getRepititionSchedule().getEveryNRepeat();
                        for (int i = 0; i < 8; i++) {
                            Tasks monthlyTask = new Tasks();
                            DateTime dateTime = new DateTime(task.getDueDate());
                            dateTime.plusDays(repeatDays * everyNDays);
                            monthlyTask.setDueDate(dateTime.getMillis());
                            if (new DateTime(task.getDueDate()).getMonthOfYear() == calendarMonth) {
                                t.add(monthlyTask);
                            }
                        }
                    }
                    //ADD WEEKLY TASKS
                    if (task.getRepititionSchedule().getRepeat().equalsIgnoreCase("weekly")) {

                    }
                }
            }
        }
        return t;
    }

    private int getRepeatDays(String repeatType) {
        if (repeatType.equalsIgnoreCase("daily")) {
            return 1;
        }
        if (repeatType.equalsIgnoreCase("weekly")) {
            return 7;
        }
        return -1;
    }

    private TextView createNewTextView() {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        TextView tvCount = new TextView(this);
        tvCount.setGravity(Gravity.CENTER);
        tvCount.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_background));
        tvCount.setTextColor(Color.WHITE);
        tvCount.setTag("tvCount");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.setMargins(0, size, 0, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tvCount.setLayoutParams(params);
        return tvCount;
    }

    private void getAllGoals() {

        RetroInterface retroInterface = RetrofitClient.getApiServices(childObject.getEmail(), childObject.getPassword());
        Call<List<GetAllGoalResponse>> response = retroInterface.getGoals(childObject.getId());


        response.enqueue(new Callback<List<GetAllGoalResponse>>() {
            @Override
            public void onResponse(Call<List<GetAllGoalResponse>> call, Response<List<GetAllGoalResponse>> response) {
                // Log.e(TAG, "response = "+response.body().get(0).getName());
            }

            @Override
            public void onFailure(Call<List<GetAllGoalResponse>> call, Throwable t) {

            }
        });
    }


    @Override
    public void prepareCalendarView() {
        Calendar disabledCal = Calendar.getInstance();
        disabledCal.set(Calendar.DATE, disabledCal.get(Calendar.DATE) - 1);

        calendarView.setFirstDayOfWeek(Calendar.SUNDAY).setOnDateClickListener(new CalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(@NonNull Date selectedDate) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MM/dd");
                finalDate = sdf.format(selectedDate);
                tasksList.clear();
                List<ChildsTaskObject> childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(childObject);
                for (int i = 0; i < childTaskObjects.size(); i++) {
                    DateTime dateTime = new DateTime(childTaskObjects.get(i).getDueDate()).withTimeAtStartOfDay();
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
                    String toPrintDate = fmt.print(dateTime);
                    if (toPrintDate.equals(finalDate))
                        tasksList.addAll(childTaskObjects.get(i).getChildItemList());
                }
            }
        });
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY).setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(@NonNull Date monthDate) {
            }
        });
        calendarView.setOnDateLongClickListener(new CalendarView.OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull Date selectedDate) {

            }
        });


        calendarView.update(Calendar.getInstance(Locale.getDefault()));

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void animateViews() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_to_tasklist:
                onBackPressed();
                break;
            case R.id.childcaledar_hearback:
                onBackPressed();
                break;
            case R.id.childcalendar_forward_arrow:
                if (taskCOunter < tasksList.size()) {
                    bottom_2.setText("Goal name:" + tasksList.get(taskCOunter).getGoal().getGoalName());
                    taskCOunter++;
                }
                break;
            case R.id.childcalendar_backarrow:
                if (taskCOunter > 1) {
                    --taskCOunter;
                    bottom_2.setText("Goal name:" + tasksList.get(taskCOunter).getGoal().getGoalName());
                }
                break;
            case R.id.add_task_avatar:
                if (currentTask != null)
                    new FloatingMenu(childCalendarActivity).fetchAvatarDimension(childAvatar, childObject, otherChild, parentObject, AppConstant.ADD_TASK, progressBar, currentTask);
                else
                    new FloatingMenu(childCalendarActivity).fetchAvatarDimension(childAvatar, childObject, otherChild, parentObject, AppConstant.ADD_TASK, progressBar, null);
                break;


        }
    }
}
