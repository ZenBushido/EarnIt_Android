package com.mobiledi.earnit.service.applock_service;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobiledi.earnit.MyApplication;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.SharedPreference;
import com.mobiledi.earnit.activity.ChildDashboard;
import com.mobiledi.earnit.activity.applock.MainActivity;
import com.mobiledi.earnit.activity.applock.PasswordRecoveryActivity;
import com.mobiledi.earnit.model.BlockingApp;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.retrofit.RetrofitClient;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.takwolf.android.lock9.Lock9View;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ashishkumar on 5/1/18.
 */

public class AppCheckServices extends Service {

    private long UPDATE_TASKS_MILLIS = 60 * 1000;
    private long ON_TICK_MILLIS = 1000;

    private boolean dialogIsShowing = false;

    public static final String TAG = "AppCheckServices";
    private Context context = null;
    private Timer timer;
    private WindowManager windowManager;
    private Dialog dialog;
    public static String currentApp = "";
    public static String previousApp = "";
    SharedPreference sharedPreference;
    List<String> pakageName;
    private CountDownTimer tasksUpdateTimer;
    private SharedPreferences sp;
    private List<String> appsToBeBlocked = new ArrayList<>();
    private FrameLayout frameLayout;
    private RetroInterface retroInterface;
    private List<Tasks> tasksList;
    private boolean mScreenIsBlocked;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AppCheckServices.onCreate()");
        tasksList = new ArrayList<>();
        context = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences sp = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        String email = sp.getString(AppConstant.EMAIL, "");
        String password = sp.getString(AppConstant.PASSWORD, "");
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            email = sp.getString(AppConstant.EMAIL, "");
            password = sp.getString(AppConstant.PASSWORD, "");
        }
        Log.d("fsdfksjdhf", "email = " + MyApplication.getInstance().getEmail() + "; password = " + MyApplication.getInstance().getPassword());
        retroInterface = RetrofitClient.getApiServices(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());

        sharedPreference = new SharedPreference();
        pakageName = sharedPreference.getLocked(context);
        for (String packageName : pakageName) {
            Log.d(TAG, "packageName = " + packageName);
        }
        timer = new Timer("AppCheckServices");
        timer.schedule(updateTask, 1000L, 1000L);

        tasksUpdateTimer = new CountDownTimer(UPDATE_TASKS_MILLIS, ON_TICK_MILLIS) {
            @Override
            public void onTick(long l) {
                boolean isConcernedAppIsInForeground = isConcernedAppIsInForeground();
                Log.d("sdjhfksjhls", "ConcernedAppIsInForeground = " + isConcernedAppIsInForeground);
                if (isConcernedAppIsInForeground) {
                    Log.d("sdjhfksjhls", "isConcernedAppIsInForeground = true");
                    //blockScreen();
                    showDialog();
                } else {
                    Log.d("sdjhfksjhls", "isConcernedAppIsInForeground = false");
                    hideUnlockDialog();
                }
            }

            @Override
            public void onFinish() {
                updateTasks();
            }
        };
        updateTasks();
    }

    private void updateTasks() {
        Log.d("sdjhfksjhls", "updateTasks()");
        final int childId = MyApplication.getInstance().getChildId();
        tasksUpdateTimer.cancel();
        final AsyncHttpClient client = new AsyncHttpClient();
        String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
        final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
        client.addHeader("Authorization", basicAuth);
        client.setBasicAuth(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());
        Log.e("sdjhfksjhls", "API getChildTasks: " + AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + childId);
        client.get(AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + childId, null, new JsonHttpResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
                tasksUpdateTimer.start();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                appsToBeBlocked.clear();
                Log.d("sdjhfksjhl", "response = " + response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Log.e(TAG, "OBJECT= " + object);
                        //TASKS
                        Tasks task = new GetObjectFromResponse().getTaskObject(object, childId);
                        Log.e(TAG, "GetObjectFromResponse task: " + task.toString());
                        if (!task.getStatus().equalsIgnoreCase("Closed") &&
                                isTaskOverdue(task) &&
                                task.getAppsToBeBlockedOnOverdue() != null) {
                            tasksList.add(task);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getLocalizedMessage());

                    }
                }
                for (Tasks task1 : tasksList) {
                    for (BlockingApp app : task1.getAppsToBeBlockedOnOverdue()) {
                        if (app.getName().contains("#")) {
                            Log.d("sdjhfksjhl", "appsToBeBlocked.add(" + app.getName() + ");");
                            if (!appsToBeBlocked.contains(app.getName())) {
                                appsToBeBlocked.add(app.getName());
                            }
                        }
                    }
                }
                Log.d("sdjhfksjhl", "appsToBeBlocked: " + Arrays.toString(new List[]{appsToBeBlocked}));
            }
        });
    }

    private void updateParentsList() {
        Log.d("fsdfksjdhf", "updateParentsList()");
        Call<Set<String>> parentsIds = retroInterface.getParents();
        parentsIds.enqueue(new Callback<Set<String>>() {
            @Override
            public void onResponse(@NonNull Call<Set<String>> call, @NonNull Response<Set<String>> response) {
                Log.d("fsdfksjdhf", "response = " + response.body());
                sp.edit().putStringSet(AppConstant.PARENTS_IDS, response.body()).apply();
                Set<String> parentsIds = sp.getStringSet(AppConstant.PARENTS_IDS, null);
                Log.d("fsdfksjdhf", "get parentsIds = " + parentsIds);
            }

            @Override
            public void onFailure(@NonNull Call<Set<String>> call, @NonNull Throwable t) {
                Log.d("fsdfksjdhf", "error = " + t.getLocalizedMessage());
            }
        });
    }

    private boolean isTaskOverdue(Tasks task) {
        return new DateTime(task.getDueDate()).isBefore(DateTime.now());
    }

    private void blockScreen() {
        if (!mScreenIsBlocked) {
            mScreenIsBlocked = true;
            Log.d("sdjhfksjhls", "blockScreen");
            try {
                windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                frameLayout = new FrameLayout(this);
                frameLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                TextView textView = new TextView(this);
                textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                textView.setTextColor(ContextCompat.getColor(this, R.color.white));
                textView.setGravity(Gravity.CENTER);
                frameLayout.addView(textView);

                final WindowManager.LayoutParams params;
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        getLayoutParamsType(),
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);

                params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
                params.x = 0;
                params.y = 100;

                windowManager.addView(frameLayout, params);
                Log.d(TAG, "image added");
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getLocalizedMessage());
            }
        }
    }

    private void unlockScreen() {
        Log.d("sdjhfksjhls", "unlockScreen");
        if (mScreenIsBlocked) {
            mScreenIsBlocked = false;
            if (windowManager == null) {
                windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            }
            assert windowManager != null;
            windowManager.removeView(frameLayout);
        }
    }

    private int getLayoutParamsType() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    }

    private TimerTask updateTask = new TimerTask() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        public void run() {
        }
    };

    void hideUnlockDialog() {
        if (dialogIsShowing) {
            previousApp = "";
            try {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialogIsShowing = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void showDialog() {
        if (!dialogIsShowing) {
            if (context == null)
                context = getApplicationContext();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptsView = layoutInflater.inflate(R.layout.dialog_blocking_app, null);
            promptsView.findViewById(R.id.btnShowTasks).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent (AppCheckServices.this, ChildDashboard.class);
                    intent.putExtra(AppConstant.SHOW_EXPIRED_TASKS, true);
                    startActivity(intent);
                }
            });
//            Lock9View lock9View = (Lock9View) promptsView.findViewById(R.id.lock_9_view);
//            Button forgetPassword = (Button) promptsView.findViewById(R.id.forgetPassword);
//            lock9View.setCallBack(new Lock9View.CallBack() {
//                @Override
//                public void onFinish(String password) {
//                    if (password.matches(sharedPreference.getPassword(context))) {
//                        dialog.dismiss();
//                        dialogIsShowing = false;
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Wrong Pattern Try Again", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//            forgetPassword.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogIsShowing = false;
//                    Intent i = new Intent(AppCheckServices.this, PasswordRecoveryActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(i);
//                    dialog.dismiss();
//                }
//            });

            dialog = new Dialog(context/*, android.R.style.Theme_Black_NoTitleBar_Fullscreen*/);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setType(getLayoutParamsType());
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setContentView(promptsView);
            dialog.getWindow().setGravity(Gravity.CENTER);

            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == KeyEvent.ACTION_UP) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                    }
                    return true;
                }
            });

            dialog.show();
            dialogIsShowing = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand. id = " + startId);
        sendNotification("Ticker", "Title", "Text");
        if (MyApplication.getInstance().getUserType() != null &&
                MyApplication.getInstance().getUserType().equalsIgnoreCase(AppConstant.CHILD)) {
            updateParentsList();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            blockScreen();
//            showDialog();
        }
        if (intent != null) {

        }
        /* We want this service to continue running until it is explicitly
         * stopped, so return sticky.
         */
        return START_STICKY;
    }

    private void sendNotification(String Ticker,String Title,String Text) {

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1333");
        builder.setContentIntent(contentIntent)
                .setOngoing(true)   //Can't be swiped out
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.large))   // большая картинка
                .setTicker(Ticker)
                .setContentTitle(Title) //Заголовок
                .setContentText(Text) // Текст уведомления
                .setWhen(System.currentTimeMillis());

        Notification notification;
        notification = builder.build();

        startForeground(1333, notification);
    }

    public boolean isConcernedAppIsInForeground() {
        for(String app : appsToBeBlocked){
            Log.d("sdjhfksjhls", "app should be blocked: " + app);
        }


        String topPackageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60, time);
            // Sort the stats by the last time used
            if (stats != null) {
                Log.v("sdjhfksjhls", "stats.size() " + stats.size());
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    Log.v("sdjhfksjhls", "package name is " + topPackageName);
                    for (String app : appsToBeBlocked){
                        if (app.split("#")[0].equalsIgnoreCase(topPackageName)){
                            return true;
                        }
                    }
                }
            }
        } else {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
            ComponentName componentInfo = task.get(0).topActivity;
            for (String app : appsToBeBlocked) {
                if (componentInfo.getPackageName().equals(app.split("#")[0])) {
                    currentApp = componentInfo.getPackageName();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("sdjhfksjhls", "onDestroy!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        tasksUpdateTimer.cancel();
        tasksUpdateTimer = null;
        timer.cancel();
        timer = null;
//        if (imageView != null) {
//            windowManager.removeView(imageView);
//        }
        /** added to fix the bug of view not attached to window manager ****/
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
