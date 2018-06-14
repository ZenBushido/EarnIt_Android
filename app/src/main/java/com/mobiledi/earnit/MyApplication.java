package com.mobiledi.earnit;

import android.app.Application;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.mobiledi.earnit.model.AppUsage;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.AppsUsageHelper;
import com.mobiledi.earnit.utils.Utils;
import com.rollbar.android.Rollbar;

import org.joda.time.DateTime;

import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mobile-di on 4/10/17.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    private ArrayList<ChildsTaskObject> childsTaskObjects;
    private String userType;
    private String password;
    private String email;
    private SharedPreferences sp;
    private int childId;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Log.d("fjdjhf", "MyApplication onCreate()");
        instance = this;
        sp = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
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
}
