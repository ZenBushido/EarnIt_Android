package com.mobiledi.earnit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.model.goal.GetAllGoalResponse;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.retrofit.RetrofitClient;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.blackbox_vision.materialcalendarview.view.CalendarView;
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

    @BindView(R.id.bottom2) TextView bottom_2;
    Integer taskCOunter=0;
    List<Tasks> tasksList= new ArrayList<>();
    @BindView(R.id.drawer_layout) RelativeLayout drawer;
    @BindView(R.id.calendar_view) CalendarView calendarView;
    @BindView(R.id.back_to_tasklist) Button backlisttask;
    @BindView(R.id.childcaledar_hearback) ImageButton headerbackBtn;
    @BindView(R.id.childcalendar_backarrow)ImageButton backarrow;
    @BindView(R.id.childcalendar_forward_arrow) ImageButton forwardarrow;
    @BindView(R.id.add_task_avatar) CircularImageView childAvatar;
    Tasks currentTask;
    String screen_name;
    public Parent parentObject;
    public Child childObject, otherChild;
    Intent intent;
    @BindView(R.id.loadingPanel) RelativeLayout progressBar;
    private String finalDate;

    String TAG = ChildCalendarActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.childcalendar);
        childCalendarActivity = this;
        ButterKnife.bind(this);
        presenter.addCalendarView();
        presenter.animate();
        bottom_2.setText("Chose Goal Name");
        intent = getIntent();
        screen_name = intent.getStringExtra(AppConstant.FROM_SCREEN);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);

        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        Log.e(TAG, "Child ID= "+childObject.getId());

        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        Log.e(TAG, "URL= "+"https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/"+childObject.getAvatar());


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(350,350);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.placeholder(R.drawable.default_avatar);
        requestOptions.error(R.drawable.default_avatar);

        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL+childObject.getAvatar()).into(childAvatar);


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
                new FloatingMenu(childCalendarActivity).fetchAvatarDimension(childAvatar, childObject, parentObject, AppConstant.CHILD_DASHBOARD_SCREEN, progressBar);

            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MM/dd");
        finalDate = sdf.format(new Date());
        tasksList.clear();
        List<ChildsTaskObject> childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(childObject, AppConstant.CHILD, AppConstant.CHECKED_IN_SCREEN);
        for (int i = 0; i < childTaskObjects.size(); i++) {
            DateTime dateTime = new DateTime(childTaskObjects.get(i).getDueDate()).withTimeAtStartOfDay();
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            if (toPrintDate.equals(finalDate))
                tasksList.addAll(childTaskObjects.get(i).getChildItemList());


        }

        getAllGoals();

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
                            bottom_2.setText("Goal name:"+tasksList.get(taskCOunter).getGoal().getGoalName());
                            taskCOunter++;
                        }
                break;
            case R.id.childcalendar_backarrow:
                if (taskCOunter > 1) {
                    --taskCOunter;
                    bottom_2.setText("Goal name:"+tasksList.get(taskCOunter).getGoal().getGoalName());
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
