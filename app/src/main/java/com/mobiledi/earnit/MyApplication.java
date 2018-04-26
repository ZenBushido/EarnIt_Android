package com.mobiledi.earnit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.rollbar.android.Rollbar;

import java.util.ArrayList;

/**
 * Created by mobile-di on 4/10/17.
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    private ArrayList<ChildsTaskObject> childsTaskObjects;
    @Override
    public void onCreate() {
        super.onCreate();
       instance = this;
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

    public static synchronized MyApplication getInstance(){
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
}
