package com.mobiledi.earnit.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.MyApplication;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.model.getChild.Task;
import com.mobiledi.earnit.model.goal.GetAllGoalResponse;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.retrofit.RetrofitClient;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.ScreenSwitch;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.blackbox_vision.materialcalendarview.view.CalendarView;
import io.blackbox_vision.materialcalendarview.view.DayView;
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

    String TAG = ChildCalendarActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.childcalendar);
        MyApplication.getInstance().setCalendarIsOpen(true);
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
        childTasksObject = (ArrayList<ChildsTaskObject>) intent.getSerializableExtra(AppConstant.CHILD_TASKS_OBJECT);
        Log.d("dsaldasluidj", "childTasksObject: " + childTasksObject.toString().replace("'", "\""));

        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        Log.d("dsjfhkj", "childObject tasks: " + childObject.toString());
        Log.e(TAG, "Child ID= " + childObject.getId());

        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        Log.e(TAG, "URL= " + "https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar());


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(350, 350);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.placeholder(R.drawable.default_avatar);
        requestOptions.error(R.drawable.default_avatar);

        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL + childObject.getAvatar()).into(childAvatar);


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
                new FloatingMenu(childCalendarActivity).fetchAvatarDimension(null, childAvatar, childObject, parentObject, AppConstant.CHILD_DASHBOARD_SCREEN, progressBar);

            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MM/dd", Locale.getDefault());
        finalDate = sdf.format(new Date());
        tasksList.clear();
        final List<ChildsTaskObject> childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(childObject, AppConstant.CHILD, AppConstant.CHECKED_IN_SCREEN);
        for (int i = 0; i < childTaskObjects.size(); i++) {
            DateTime dateTime = new DateTime(childTaskObjects.get(i).getDueDate()).withTimeAtStartOfDay();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            if (toPrintDate.equals(finalDate)) {
                tasksList.addAll(childTaskObjects.get(i).getChildItemList());
            }


        }
        setBadges(true, -1);
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(@NonNull Date monthDate) {
                calendarMonth = new DateTime(monthDate).getMonthOfYear();
                Log.d("dbopc", "\nonMonthChange. calendarMonth = " + calendarMonth);
                setBadges(false, calendarMonth);
            }
        });

        calendarView.setOnDateClickListener(new CalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(@NonNull Date selectedDate) {
//                Log.d("dsds", "onDateClick. size = " + childObject.getTasksArrayList().size());
//                Child child = getChild(childObject, new DateTime(selectedDate));
//                ScreenSwitch screenSwitch = new ScreenSwitch(ChildCalendarActivity.this);
//                if (child != null) {
//                    Log.d("dsjfhkj", "size() != 0");
//                    if (child.getTasksArrayList().size() > 1) {
//                        Log.d("dsjfhkj", "size() > 1");
//                        screenSwitch.moveTOChildDashboard(getChild(childObject, new DateTime(selectedDate)));
//                    } else {
//                        Log.d("dsjfhkj", "size() == 1");
//                        Tasks task = child.getTasksArrayList().get(0);
//                        Intent requestTaskApproval = new Intent(ChildCalendarActivity.this, ChildRequestTaskApproval.class);
//                        requestTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
//                        requestTaskApproval.putExtra(AppConstant.TASK_OBJECT, (Serializable) task);
//                        requestTaskApproval.putExtra(AppConstant.GOAL_OBJECT, task.getGoal());
//                        requestTaskApproval.putExtra(AppConstant.REPETITION_SCHEDULE, task.getRepititionSchedule());
//                        requestTaskApproval.putExtra(AppConstant.PARENT_OBJECT, parentObject);
//                        startActivity(requestTaskApproval);
//                    }
//                } else {
//                    showDialogOnTaskAdded(new DateTime(selectedDate));
//                }
                ArrayList<Tasks> tasks = new ArrayList<>();
                DateTime dateTime = new DateTime(selectedDate);
                ScreenSwitch screenSwitch = new ScreenSwitch(ChildCalendarActivity.this);
                Child child = getChild(childObject, new DateTime(selectedDate));
                for (ChildsTaskObject taskObject : childTaskObjects){
                    DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    DateTime dateTime2 = dtf.parseDateTime(taskObject.getDueDate());
                    if (datesEquals(dateTime, dateTime2)){
                        tasks.addAll(taskObject.getTasks());
                    }
                }
                if (tasks.size() != 0) {
                    Log.d("dsjfhkj", "size() != 0");
                    if (tasks.size() > 1) {
                        Log.d("dsjfhkj", "size() > 1");
                        screenSwitch.moveTOChildDashboard(getChild(childObject, new DateTime(selectedDate)));
                    } else {
                        Log.d("dsjfhkj", "size() == 1");
                        Tasks task = tasks.get(0);
                        Intent requestTaskApproval = new Intent(ChildCalendarActivity.this, ChildRequestTaskApproval.class);
//                        requestTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
                        requestTaskApproval.putExtra(AppConstant.CHILD_OBJECT, childObject);
                        requestTaskApproval.putExtra(AppConstant.TASK_OBJECT, (Serializable) task);
                        requestTaskApproval.putExtra(AppConstant.GOAL_OBJECT, task.getGoal());
                        requestTaskApproval.putExtra(AppConstant.REPETITION_SCHEDULE, task.getRepititionSchedule());
                        requestTaskApproval.putExtra(AppConstant.PARENT_OBJECT, parentObject);
                        startActivity(requestTaskApproval);
                    }
                } else {
                    showDialogOnTaskAdded(new DateTime(selectedDate));
                }
            }
        });
        getAllGoals();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().setCalendarIsOpen(false);
    }

    private boolean datesEquals(DateTime firstDate, DateTime secondDate){
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
        ArrayList<Tasks> emptyTasks = new ArrayList<>();
        for (Tasks task : child.getTasksArrayList()) {
            DateTime datesArray = new DateTime(task.getDueDate());
            Log.d("dsdsd", taskDate.toString("dd/MM") + " AND " + datesArray.toString("dd/MM"));
            if (datesArray.getMonthOfYear() == taskDate.getMonthOfYear() &&
                    datesArray.getYear() == taskDate.getYear() &&
                    datesArray.getDayOfMonth() == taskDate.getDayOfMonth()) {
                Log.d("dsjfhkj", "emptyTasks.add(task);");
                emptyTasks.add(task);
            }
        }
        if (emptyTasks.size() != 0) {
            child.setTasksArrayList(emptyTasks);
        } else {
            return null;
        }
        return child;
    }

    private void setBadges(boolean firstCall, int month) {
        badges.clear();
        for (TextView tv : badgesTextViews) {
            RelativeLayout rl = ((RelativeLayout) tv.getParent());
            if (rl != null)
                rl.removeView(tv);
        }
        //add badges not repeat

//        ArrayList<Tasks> t = addRepeatedTasks(childObject.getTasksArrayList());
        ArrayList<Tasks> t = new ArrayList<>();

//        for (Tasks task : childObject.getTasksArrayList()){
//            Log.d("dbopc", "\nsetBadges. task status = " + task.getStatus() + ";    task.getDueDate() = " + new DateTime(task.getDueDate()).getMonthOfYear() + ";     calendarMonth = " + calendarMonth);
//            if (!task.getStatus().equals("Closed") && new DateTime(task.getDueDate()).getMonthOfYear() == calendarMonth) {
//                t.add(task);
////                addRepeatedTasks(t, childObject.getTasksArrayList());
//            }
//        }

        for (ChildsTaskObject taskObject : childTasksObject){
            //2018-04-21T00:00:00.000Z
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DateTime dateTime = dtf.parseDateTime(taskObject.getDueDate());
            for (Tasks task : taskObject.getTasks()){
                Log.d("dbopc", "\nsetBadges. task status = " + task.getStatus() + ";    task.getDueDate() = " + new DateTime(task.getDueDate()).getMonthOfYear() + ";     calendarMonth = " + calendarMonth);
                if (!task.getStatus().equals("Closed") && new DateTime(task.getDueDate()).getMonthOfYear() == calendarMonth) {

                    task.setDueDate(dateTime.getMillis());
                    t.add(task);
                }
            }
        }

        if (t.size() > 0) {
            for (Tasks task : t) {
                int i = 1;
                DateTime taskDate = new DateTime(task.getDueDate());
                Log.d("dsjfhkj", "firsstCall = " + firstCall + "; taskDate.getMonthOfYear() = " + taskDate.getMonthOfYear() + "; month = " + month);
                if (firstCall || taskDate.getMonthOfYear() == month) {
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
        }
        Log.d("dsjfhkj", "badges size = " + badges.size());
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
                            if (new DateTime(task.getDueDate()).getMonthOfYear() == calendarMonth){
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
                List<ChildsTaskObject> childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(childObject, AppConstant.CHILD, AppConstant.CHECKED_IN_SCREEN);
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
