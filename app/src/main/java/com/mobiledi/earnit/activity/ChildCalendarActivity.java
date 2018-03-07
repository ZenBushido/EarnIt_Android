package com.mobiledi.earnit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
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

import io.blackbox_vision.materialcalendarview.view.CalendarView;

/**
 * Created by GreenLose on 12/8/2017.
 */

public class ChildCalendarActivity extends AppCompatActivity implements View.OnClickListener, MainView {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    ChildCalendarActivity childCalendarActivity;
    private final MainPresenter presenter = new MainPresenter(this);
    private static final String MONTH_TEMPLATE = "MMMM yyyy";
    TextView textView, bottom_2;
    Integer taskCOunter=0;
    List<Tasks> tasksList= new ArrayList<>();
     DrawerLayout drawer;
    CalendarView calendarView;
    Button backlisttask;
    ImageButton headerbackBtn, backarrow, forwardarrow;
    CircularImageView childAvatar;
    Tasks currentTask;
    String screen_name;
    public Parent parentObject;
    public Child childObject, otherChild;
    Intent intent;
    RelativeLayout progressBar;
    private String finalDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.childcalendar);

        childCalendarActivity = this;
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        backlisttask = (Button) findViewById(R.id.back_to_tasklist);
        backarrow = (ImageButton) findViewById(R.id.childcalendar_backarrow);
        forwardarrow = (ImageButton) findViewById(R.id.childcalendar_forward_arrow);
        headerbackBtn = (ImageButton) findViewById(R.id.childcaledar_hearback);
        bottom_2 = (TextView) findViewById(R.id.bottom2);
        presenter.addCalendarView();
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        presenter.animate();
        childAvatar = (CircularImageView) findViewById(R.id.add_task_avatar);
        bottom_2.setText("Chose Goal Name");
        intent = getIntent();
        screen_name = intent.getStringExtra(AppConstant.FROM_SCREEN);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);

        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);

        try {
            Picasso.with(childCalendarActivity).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(childAvatar);

        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(childCalendarActivity).load(R.drawable.default_avatar).into(childAvatar);
        }

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
                            bottom_2.setText("Goal name:"+tasksList.get(taskCOunter).getName());
                            taskCOunter++;
                        }
                break;
            case R.id.childcalendar_backarrow:
                if (taskCOunter > 1) {
                    --taskCOunter;
                    bottom_2.setText("Goal name:"+tasksList.get(taskCOunter).getName());
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
