package com.mobiledi.earnit;

import android.app.ActivityManager;
import android.app.Application;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.mobiledi.earnit.model.AppUsage;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.retrofit.RetrofitClient;
import com.mobiledi.earnit.service.UpdateFcmToken;
import com.mobiledi.earnit.service.applock_service.AppCheckServices;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.AppsUsageHelper;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.Utils;
import com.rollbar.android.Rollbar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;
import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by mobile-di on 4/10/17.
 */

public class MyApplication extends Application {
    private final String TAG = getClass().getSimpleName();
    private static MyApplication instance;
    private ArrayList<ChildsTaskObject> childsTaskObjects;
    private String userType;
    private String password;
    private String email;
    private SharedPreferences sp;
    private int childId;
    private Child child;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Log.d("fjdjhf", "MyApplication onCreate()");
        instance = this;
        sp = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        if (!TextUtils.isEmpty(sp.getString(AppConstant.EMAIL, "")) && !TextUtils.isEmpty(sp.getString(AppConstant.PASSWORD, ""))){
            if (TextUtils.isEmpty(MyApplication.getInstance().getEmail()))
                MyApplication.getInstance().setEmail(sp.getString(AppConstant.EMAIL, ""));
            if (TextUtils.isEmpty(MyApplication.getInstance().getPassword()))
                MyApplication.getInstance().setPassword(sp.getString(AppConstant.PASSWORD, ""));
        }
        Utils.logDebug(TAG, "MyApplication.getInstance().getEmail() = " + MyApplication.getInstance().getEmail());
        Utils.logDebug(TAG, "MyApplication.getInstance().getPassword() = " + MyApplication.getInstance().getPassword());
        if (!TextUtils.isEmpty(MyApplication.getInstance().getEmail()) && !TextUtils.isEmpty(MyApplication.getInstance().getPassword())){
            authenticateUser(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());
        }
        Iconify.with(new FontAwesomeModule());
        Rollbar.init(this, "09e5fe34de7f42cd8afae469f73c4597", "development");
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Rollbar rollbar = Rollbar.instance();
                rollbar.error(new Exception(ex));
            }
        });
    }

    public void authenticateUser(final String username, final String password) {
        Utils.logDebug(TAG, "authenticateUser: username = " + username + "; password = " + password);

        sp = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        Utils.logDebug(TAG, " GeneratedTokenI " + sp.getString(AppConstant.TOKEN_ID, null));

        try {
            AsyncHttpClient httpClient = new AsyncHttpClient();
            PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
            httpClient.setCookieStore(myCookieStore);
            String namePassword = username.trim() + ":" + password.trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            httpClient.addHeader("Authorization", basicAuth);
            httpClient.setMaxRetriesAndTimeout(3, 3000);
            Utils.logDebug(TAG, " login-Rquest " + AppConstant.BASE_URL + AppConstant.LOGIN_API);
            Utils.logDebug(TAG, " login-Rquest Header: " + namePassword);
            Utils.logDebug(TAG, " login-Rquest encoded Header: " + basicAuth);
            httpClient.get(MyApplication.this, AppConstant.BASE_URL + AppConstant.LOGIN_API, null, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Utils.logDebug(TAG, " login-Rquest onSuccess" + response.toString());
                        sp.edit()
                                .putString(AppConstant.EMAIL, response.getString(AppConstant.EMAIL))
                                .putString(AppConstant.PASSWORD, password)
                                .apply();
                        if (response.getString(AppConstant.TYPE).equals(AppConstant.PARENT)) {
                            if (isMyServiceRunning(AppCheckServices.class) && imRealParent(response.getInt(AppConstant.ID))){
                                stopService(new Intent(MyApplication.this, AppCheckServices.class));
                            }
                        } else if (response.getString(AppConstant.TYPE).equals(AppConstant.CHILD)) {
                            child = new GetObjectFromResponse().getChildObject(response);
                            ArrayList<Tasks> taskList = new ArrayList<>();
                            JSONArray taskArray = response.getJSONArray(AppConstant.TASKS);
                            for (int taskIndex = 0; taskIndex < taskArray.length(); taskIndex++) {
                                JSONObject taskObject = taskArray.getJSONObject(taskIndex);
                                Tasks task = new GetObjectFromResponse().getTaskObject(taskObject, response.getInt(AppConstant.ID));
                                taskList.add(task);
                            }
                            child.setTasksArrayList(taskList);
                            Log.d("fjlskdsaj", "Child = " + child);
                            if (!isMyServiceRunning(AppCheckServices.class)){
                                MyApplication.getInstance().setChildId(response.getInt(AppConstant.ID));
                                startService(new Intent(MyApplication.this, AppCheckServices.class));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Utils.logDebug(TAG, " login-Rquest onFailure. JSONObject errorResponse: " + errorResponse);
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable: " + throwable.getLocalizedMessage());
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable.toString(): " + throwable.toString());
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable.getMessage(): " + throwable.getMessage());
                    Utils.logDebug(TAG, " login-Rquest onFailure. statusCode: " + statusCode);


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Utils.logDebug(TAG, " login-Rquest onFailureA" + errorResponse.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Utils.logDebug(TAG, " login-Rquest onSuccessA" + response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Utils.logDebug(TAG, " login-Rquest onFailureS" + responseString);
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable: " + throwable.getLocalizedMessage());
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable.toString(): " + throwable.toString());
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable.getMessage(): " + throwable.getMessage());
                    Utils.logDebug(TAG, " login-Rquest onFailure. statusCode: " + statusCode);
                    int i = 0;
                    for (Header header : headers) {
                        i++;
                        Utils.logDebug(TAG, i + " lHeader: " + header.toString());
                    }
                }

                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean imRealParent(int id){
        SharedPreferences sp = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        Set<String> parentsIds = sp.getStringSet(AppConstant.PARENTS_IDS, null);
        Log.d("fsdfksjdhf", "parentsIds = " + parentsIds + "; my id = " + id);
        if (parentsIds != null && parentsIds.size() > 0){
            for (String parentId : parentsIds){
                if (id == Integer.parseInt(parentId)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getPassword() {
        if (password != null && password.isEmpty()) {
            password = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE).getString(AppConstant.PASSWORD, "");
        }
        return Utils.checkIsNUll(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        if (email != null && email.isEmpty()) {
            email = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE).getString(AppConstant.EMAIL, "");
        }
        return Utils.checkIsNUll(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public void clearPassword() {
        password = "";
    }

    public static synchronized MyApplication getInstance() {
        return instance == null ? new MyApplication() : instance;
    }

    public ArrayList<ChildsTaskObject> getChildsTaskObjects() {
        return childsTaskObjects;
    }

    public void setChildsTaskObjects(ArrayList<ChildsTaskObject> childsTaskObjects) {
        this.childsTaskObjects = childsTaskObjects;
    }

    /*This methods belongs to enable Multidexing for Application and Fixing crashing issue for lollipop and below it.*/
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void setInstance(MyApplication instance) {
        MyApplication.instance = instance;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }
}
