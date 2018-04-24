package com.mobiledi.earnit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.telecom.Call;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobiledi.earnit.MyApplication;
import com.mobiledi.earnit.MyUtils;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.activity.ChildCalendarActivity;
import com.mobiledi.earnit.activity.LoginScreen;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.TaskV2Model;
import com.mobiledi.earnit.model.Tasks;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import retrofit.ServiceGenerator;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mobiledi.earnit.activity.ParentDashboard.parentObject;

/**
 * Created by mobile-di on 22/9/17.
 */

public class FloatingMenu {
    private final String TAG = "FloatingMenu";
    int OFFSET_X = 0;
    int OFFSET_Y = 0;
    Activity activity;
    ArrayList<ChildsTaskObject> childTaskObjects;
    ScreenSwitch screenSwitch;
    public Parent parent;

    public FloatingMenu(Activity activity) {
        this.activity = activity;
        screenSwitch = new ScreenSwitch(activity);
    }

    public static Rect locateView(View v)
    {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try
        {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe)
        {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    public void fetchAvatarDimension(CircularImageView view, Child child, Child childWithAllTask, Parent parent, String fromScreen, RelativeLayout progressBar, Tasks tasks) {

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point p = new Point();
        p.x = location[0];
        p.y = location[1] - 25;
        showPopup(activity, p, child, childWithAllTask, parent, fromScreen, progressBar, tasks);
     //   Rect loation = locateView(view);
      //  popupShow(activity, loation, child, childWithAllTask, parent, fromScreen, progressBar, tasks);
    }

    /*public void popupShow(Activity view, Rect location, final Child child, final Child childWithAllTask, final Parent parent, final String fromScreen, final RelativeLayout progressBar, final Tasks tasks)
    {

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) view.findViewById(R.id.feb_menu_layout);
        LayoutInflater layoutInflater = (LayoutInflater) view.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.feb_menu_layout, viewGroup);

        checkDeviceResolution(fromScreen);
        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(view);
        popup.setContentView(layout);
        popup.setWidth(ListPopupWindow.WRAP_CONTENT);
        popup.setHeight(ListPopupWindow.WRAP_CONTENT);
        popup.setFocusable(true);

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
       // popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x - 6 + OFFSET_X, p.y - 6 + OFFSET_Y);
        popup.showAtLocation(layout, Gravity.TOP|Gravity.LEFT, location.left, location.centerY());

        TextView addTask = (TextView) layout.findViewById(R.id.fb_add_task);
        TextView allTask = (TextView) layout.findViewById(R.id.fb_a11_task);
        TextView approvalTask = (TextView) layout.findViewById(R.id.fb_approve_task);
        TextView balance = (TextView) layout.findViewById(R.id.fb_balance);
        TextView goal = (TextView) layout.findViewById(R.id.fb_goal);
        TextView message = (TextView) layout.findViewById(R.id.fb_message);
        TextView app_monitor =  layout.findViewById(R.id.app_monitor);
        addTask.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_plus_circle)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        allTask.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_file_text_o)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        approvalTask.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_eye)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        balance.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_usd)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        goal.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_star)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        message.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_comment)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        app_monitor.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_eye)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);

        int i = 0;

        for (Tasks tasks1 : childWithAllTask.getTasksArrayList()) {

            if (tasks1.getStatus().equalsIgnoreCase("Completed")) {

                i = i + 1;
            }


        }
        approvalTask.setText(" Approval Pending (" + i + ")");


        // try {
        //   Picasso.with(activity).load(child.getAvatar()).error(R.drawable.default_avatar).into(febIcon);
        //  } catch (Exception e) {
        //    e.printStackTrace();
        //  }
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenSwitch.moveToAddTask(child, childWithAllTask, parent, fromScreen, null);

                popup.dismiss();
            }
        });
        allTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                popup.dismiss();
                final AsyncHttpClient client = new AsyncHttpClient();
                client.setBasicAuth(parent.getEmail(), parent.getPassword());
                client.get(AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + child.getId(), null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {



                        ArrayList<Tasks> taskList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                //TASKS
                                Tasks task = new GetObjectFromResponse().getTaskObject(object, child.getId());
                                taskList.add(task);


                            } catch (Exception e) {

                                Log.e(TAG, "Error: "+e.getLocalizedMessage());

                            }
                            child.setTasksArrayList(taskList);

                        }

                        List<String> listStatus = new ArrayList<>();

                        for(int j=0; j< taskList.size(); j++)
                        {
                            if(taskList.get(j).getStatus().equalsIgnoreCase("Created"))
                                listStatus.add(taskList.get(j).getStatus());

                        }


                        Log.e(TAG, "Task List size= "+taskList.size());
                        if(listStatus.size()!=0)
                        {
                            progressBar.setVisibility(View.GONE);
                            screenSwitch.moveToAllTaskScreen(child, child, fromScreen, parentObject, fromScreen);
                        }


                        else

                        {
                            progressBar.setVisibility(View.GONE);
                            Utils.showToast(activity, activity.getResources().getString(R.string.no_task_schedule));
                        }





                    }
                });
            }
        });

        approvalTask.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                if (child.getTasksArrayList().size() > 0)
                    screenSwitch.moveToTaskApproval(child, childWithAllTask, FloatingMenu.this.parent, fromScreen, tasks);
                else
                    Utils.showToast(FloatingMenu.this.activity, FloatingMenu.this.activity.getResources().getString(R.string.no_task_for_approval));
                popup.dismiss();
            }
        });
        balance.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                //screenSwitch.moveToBalance(child, childWithAllTask, parent, fromScreen, tasks, null, AppConstant.PARENT);
                screenSwitch.moveToBalanceScreen(child, childWithAllTask, FloatingMenu.this.parent, fromScreen, tasks, null, AppConstant.PARENT);
                popup.dismiss();
            }
        });
        goal.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                screenSwitch.isGoalExists(child, childWithAllTask, FloatingMenu.this.parent, progressBar, fromScreen, tasks);
                popup.dismiss();
            }
        });
        message.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                screenSwitch.sendMessage(v, childWithAllTask);
                popup.dismiss();
            }
        });

        app_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenSwitch.usageStats( parent,   AppConstant.PARENT, child);
                popup.dismiss();
            }
        });
        //   febIcon.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //   public void onClick(View v) {
        //     popup.dismiss();
        //      }
        //    });
    }
*/
    private void showPopup(Activity view, Point p, final Child child, final Child childWithAllTask, final Parent parent, final String fromScreen, final RelativeLayout progressBar, final Tasks tasks) {

        // Inflate the popup_layout.xml
        LinearLayout viewGroup =  view.findViewById(R.id.feb_menu_layout);
        LayoutInflater layoutInflater = (LayoutInflater) view.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.feb_menu_layout, viewGroup);

        checkDeviceResolution(fromScreen);
        //Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(view);
        popup.setContentView(layout);
        popup.setWidth(ListPopupWindow.WRAP_CONTENT);
        popup.setHeight(ListPopupWindow.WRAP_CONTENT);

        popup.setFocusable(true);

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x - 6 + OFFSET_X, p.y - 6 + OFFSET_Y);

        TextView addTask =  layout.findViewById(R.id.fb_add_task);
        TextView allTask =  layout.findViewById(R.id.fb_a11_task);
        TextView approvalTask =  layout.findViewById(R.id.fb_approve_task);
        TextView balance = layout.findViewById(R.id.fb_balance);
        TextView goal =  layout.findViewById(R.id.fb_goal);
        TextView message =  layout.findViewById(R.id.fb_message);
        TextView app_monitor =  layout.findViewById(R.id.app_monitor);
        addTask.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_plus_circle)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        allTask.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_file_text_o)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        approvalTask.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_eye)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        balance.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_usd)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        goal.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_star)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        message.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_comment)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
        app_monitor.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_eye)
                .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);


        int i = 0;

        for (Tasks tasks1 : childWithAllTask.getTasksArrayList()) {

            if (tasks1.getStatus().equalsIgnoreCase("Completed")) {

                i = i + 1;
            }


        }
        approvalTask.setText(" Approval Pending (" + i + ")");


        // try {
        //   Picasso.with(activity).load(child.getAvatar()).error(R.drawable.default_avatar).into(febIcon);
        //  } catch (Exception e) {
        //    e.printStackTrace();
        //  }
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenSwitch.moveToAddTask(child, childWithAllTask, parent, fromScreen, null);
                popup.dismiss();
            }
        });
        allTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                popup.dismiss();
                final AsyncHttpClient client = new AsyncHttpClient();
                client.setBasicAuth(parent.getEmail(), parent.getPassword());
                client.get(AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + child.getId(), null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {



                            ArrayList<Tasks> taskList = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    //TASKS
                                    Tasks task = new GetObjectFromResponse().getTaskObject(object, child.getId());
                                    taskList.add(task);


                                } catch (Exception e) {

                                    Log.e(TAG, "Error: "+e.getLocalizedMessage());

                                }
                                child.setTasksArrayList(taskList);

                            }

                        List<String> listStatus = new ArrayList<>();

                        for(int j=0; j< taskList.size(); j++)
                        {
                            if(taskList.get(j).getStatus().equalsIgnoreCase("Created"))
                                listStatus.add(taskList.get(j).getStatus());

                        }


                        Log.e(TAG, "Task List size= "+taskList.size());
                        if(listStatus.size()!=0)
                        {
                            progressBar.setVisibility(View.GONE);
                            screenSwitch.moveToAllTaskScreen(child, child, fromScreen, parentObject, fromScreen);
                        }


                        else

                        {
                            progressBar.setVisibility(View.GONE);
                            Utils.showToast(activity, activity.getResources().getString(R.string.no_task_schedule));
                        }





                    }
                });
            }
        });

        approvalTask.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        if (child.getTasksArrayList().size() > 0)
            screenSwitch.moveToTaskApproval(child, childWithAllTask, parent, fromScreen, tasks);
        else
            Utils.showToast(activity, activity.getResources().getString(R.string.no_task_for_approval));
        popup.dismiss();
    }
    });
        balance.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        //screenSwitch.moveToBalance(child, childWithAllTask, parent, fromScreen, tasks, null, AppConstant.PARENT);
            screenSwitch.moveToBalanceScreen(child, childWithAllTask, parent, fromScreen, tasks, null, AppConstant.PARENT);
        popup.dismiss();
    }
    });
        goal.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        screenSwitch.isGoalExists(child, childWithAllTask, parent, progressBar, fromScreen, tasks);
        popup.dismiss();
    }
    });
        message.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        screenSwitch.sendMessage(v, childWithAllTask);
        popup.dismiss();
    }
    });

        app_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenSwitch.usageStats( parent,   AppConstant.PARENT, child);
                popup.dismiss();
            }
        });
    //   febIcon.setOnClickListener(new View.OnClickListener() {
    //     @Override
    //   public void onClick(View v) {
    //     popup.dismiss();
    //      }
    //    });

}

    public void fetchAvatarDimension(ArrayList<ChildsTaskObject> childTaskObjects, CircularImageView view, Child childObject, Parent parentObject, String onScreen, RelativeLayout progress) {

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Point p = new Point();
        p.x = location[0];
        p.y = location[1];
        showChildPopUp(activity, p, childObject, parentObject, onScreen, progress);
        this.childTaskObjects = childTaskObjects;
    }

    public void showChildPopUp(final Activity view, Point p, final Child childObject, final Parent parentObject, final String onScreen, final RelativeLayout progress) {


        // Inflate the popup_layout.xml
        LinearLayout viewGroup = view.findViewById(R.id.child_feb_menu_layout);
        LayoutInflater layoutInflater = (LayoutInflater) view.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.child_feb_menu_layout, viewGroup);

        checkDeviceResolutionChild();
        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(view);
        popup.setContentView(layout);
        popup.setWidth(ListPopupWindow.WRAP_CONTENT);
        popup.setHeight(ListPopupWindow.WRAP_CONTENT);

        //popup.setWidth(popupWidth);
       // popup.setHeight(popupHeight);
        popup.setFocusable(true);


        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        TextView viewTask = layout.findViewById(R.id.child_fb_view_task);
        TextView goalBalance =  layout.findViewById(R.id.child_fb_balance);
        TextView calendar =  layout.findViewById(R.id.child_fb_calendar);
     /* TextView calendar =layout.findViewById(R.id.child_fb_calendar);
       */
        TextView profile =  layout.findViewById(R.id.child_fb_profile);
        TextView logout =  layout.findViewById(R.id.child_fb_logout);
        try {
            viewTask.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_file_text_o)
                    .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
            goalBalance.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_usd)
                    .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
            calendar.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_calendar)
                    .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
            /*calendar.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_calendar)
                    .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
           */
            profile.setCompoundDrawablesRelativeWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_user)
                    .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);
            logout.setCompoundDrawablesWithIntrinsicBounds((new IconDrawable(layout.getContext(), FontAwesomeIcons.fa_sign_out)
                    .colorRes(R.color.check_in).sizeDp(AppConstant.FEB_ICON_SIZE)), null, null, null);


        } catch (Exception e) {
            e.printStackTrace();
        }
        //  try {
        //    Picasso.with(view).load(childObject.getAvatar()).error(R.drawable.default_avatar).into(febIcon);
        //  } catch (Exception e) {
        //    e.printStackTrace();
        //   }
        viewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RestCall(view).authenticateUser(childObject.getEmail(), childObject.getPassword()
                        , null, onScreen, progress);
            }
        });
        goalBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  if (parentObject != null){
                popup.dismiss();
              /*  }else {
                    Toast.makeText(activity.getBaseContext(),"Sorry, Parent is not available",Toast.LENGTH_SHORT).show();
                }*/
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyApplication.getInstance().isCalendarIsOpen()) {
                    Utils.showToast(activity, "ChildCalendar");
                    Intent childcalendar = new Intent(activity, ChildCalendarActivity.class);
                    childcalendar.putExtra(AppConstant.CHILD_OBJECT, childObject);
                    childcalendar.putExtra(AppConstant.PARENT_OBJECT, parentObject);
                    childcalendar.putExtra(AppConstant.FROM_SCREEN, onScreen);
                    childcalendar.putExtra(AppConstant.CHILD_TASKS_OBJECT, childTaskObjects);
                    childcalendar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(childcalendar);
                }
                popup.dismiss();

            }
        });
      /*  calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lMessage.showToast("TODO: Calendar");
                popup.dismiss();

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lMessage.showToast("TODO :Profile");
            }
        });*/
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(activity, "logout");
                SharedPreferences sharedPreferences = activity.getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                updateDeviceFCM();
                Intent intentLogout = new Intent(activity, LoginScreen.class);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Utils.showToast(activity, activity.getResources().getString(R.string.logout));
                activity.startActivity(intentLogout);
                popup.dismiss();

            }
        });
        //   febIcon.setOnClickListener(new View.OnClickListener() {
        // @Override
        //    public void onClick(View v) {
        //      popup.dismiss();
        //     }
        // });
    }

    public void checkDeviceResolution(String fromScreen) {
        switch (activity.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                Utils.logDebug(TAG, "dpi : ldpi");
                if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
                    OFFSET_X = -300;
                    OFFSET_Y = -20;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK)) {
                    OFFSET_X = -290;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                    OFFSET_X = -300;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN)) {
                    OFFSET_X = -290;
                    OFFSET_Y = -15;
                }
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                Utils.logDebug(TAG, "dpi : mdpi");
                if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
                    OFFSET_X = -300;
                    OFFSET_Y = -20;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK)) {
                    OFFSET_X = -290;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                    OFFSET_X = -300;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN)) {
                    OFFSET_X = -290;
                    OFFSET_Y = -15;
                }
                break;
            case DisplayMetrics.DENSITY_HIGH:
                Utils.logDebug(TAG, "dpi : hdpi");
                if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
                    OFFSET_X = -260;
                    OFFSET_Y = -8;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK)) {
                    OFFSET_X = -255;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                    OFFSET_X = -260;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN)) {
                    OFFSET_X = -255;
                    OFFSET_Y = -15;
                }
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                Utils.logDebug(TAG, "dpi : xhdpi");
                if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
                    OFFSET_X = -300;
                    OFFSET_Y = -20;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK)) {
                    OFFSET_X = -290;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                    OFFSET_X = -300;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN)) {
                    OFFSET_X = -290;
                    OFFSET_Y = -15;
                }
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                Utils.logDebug(TAG, "dpi : xxhdpi");
                if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
                    OFFSET_X = -370;
                    OFFSET_Y = -20;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK)) {
                    OFFSET_X = -370;
                    OFFSET_Y = -15;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                    OFFSET_X = -360;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN)) {
                    OFFSET_X = -370;
                    OFFSET_Y = -15;
                }
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                Utils.logDebug(TAG, "dpi : xxxhdpi");
                if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
                    OFFSET_X = -500;
                    OFFSET_Y = -20;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK)) {
                    OFFSET_X = -480;
                    OFFSET_Y = -15;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                    OFFSET_X = -500;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN)) {
                    OFFSET_X = -490;
                    OFFSET_Y = -15;
                }
                break;
            case DisplayMetrics.DENSITY_420:
                Utils.logDebug(TAG, "dpi : 420dpi");
                if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
                    OFFSET_X = -330;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK)) {
                    OFFSET_X = -340;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                    OFFSET_X = -325;
                    OFFSET_Y = -3;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN)) {
                    OFFSET_X = -340;
                    OFFSET_Y = -10;
                }
                break;
            case DisplayMetrics.DENSITY_560:
                Utils.logDebug(TAG, "dpi : 560dpi");
                if (fromScreen.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD)) {
                    OFFSET_X = -400;
                    OFFSET_Y = -10;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.ADD_TASK)) {
                    OFFSET_X = -420;
                    OFFSET_Y = -5;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || fromScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                    OFFSET_X = -400;
                    OFFSET_Y = -3;
                } else if (fromScreen.equalsIgnoreCase(AppConstant.GOAL_SCREEN)) {
                    OFFSET_X = -420;
                    OFFSET_Y = -5;
                }
                break;
        }
    }

    public void checkDeviceResolutionChild() {
        switch (activity.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                Utils.logDebug(TAG, "dpi : ldpi");

                OFFSET_X = -295;
                OFFSET_Y = -10;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                Utils.logDebug(TAG, "dpi : mdpi");
                OFFSET_X = -295;
                OFFSET_Y = -10;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                Utils.logDebug(TAG, "dpi : hdpi");
                OFFSET_X = -255;
                OFFSET_Y = -15;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                Log.d(TAG, "dpi : xhdpi");
                OFFSET_X = -295;
              //  OFFSET_Y = -10;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                Utils.logDebug(TAG, "dpi : xxhdpi");
                OFFSET_X = -370;
                OFFSET_Y = -15;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                Utils.logDebug(TAG, "dpi : xxxhdpi");
                OFFSET_X = -490;
                OFFSET_Y = -15;
                break;
            case DisplayMetrics.DENSITY_420:
                Utils.logDebug(TAG, "dpi : 420dpi");
                OFFSET_X = -325;
                OFFSET_Y = -10;
                break;
            case DisplayMetrics.DENSITY_560:
                Utils.logDebug(TAG, "dpi : 560dpi");
                OFFSET_X = -420;
                OFFSET_Y = -10;
                break;
        }
    }

    public void updateDeviceFCM() {
         new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            }
        }.execute();
    }


}
