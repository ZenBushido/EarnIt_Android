package com.mobiledi.earnit;

/**
 * Created by amitshekhar on 28/04/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {
    public static final String LOCKED_APP = "locked_app";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveLocked(Context context, List<String> lockedApp) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(AppLockConstants.MyPREFERENCES,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonLockedApp = gson.toJson(lockedApp);
        editor.putString(LOCKED_APP, jsonLockedApp);
        editor.commit();
    }

    public void addLocked(Context context, String app) {
        Log.d("dsfsdh", "addLocked; app = " + app);
        List<String> locked = getLocked(context);
        Log.d("dsfsdh", "before");
        for (String name : locked) {
            Log.d("dsfsdh", "name = " + name);
        }

        List<String> lockedApp = getLocked(context);
        if (lockedApp == null)
            lockedApp = new ArrayList<>();
        lockedApp.add(app);
        saveLocked(context, lockedApp);

        List<String> locked2 = getLocked(context);
        Log.d("dsfsdh", "after");
        for (String name : locked2) {
            Log.d("dsfsdh", "name = " + name);
        }
    }

    public void removeLocked(Context context, String app) {
        ArrayList<String> locked = getLocked(context);
        if (locked != null) {
            locked.remove(app);
            saveLocked(context, locked);
        }
    }

    public ArrayList<String> getLocked(Context context) {
        SharedPreferences settings;
        List<String> locked;

        settings = context.getSharedPreferences(AppLockConstants.MyPREFERENCES,
                Context.MODE_PRIVATE);

        if (settings.contains(LOCKED_APP)) {
            String jsonLocked = settings.getString(LOCKED_APP, null);
            Gson gson = new Gson();
            String[] lockedItems = gson.fromJson(jsonLocked,
                    String[].class);

            locked = Arrays.asList(lockedItems);
            locked = new ArrayList<>(locked);
        } else
            return null;
        return (ArrayList<String>) locked;
    }

    public String getPassword(Context context) {
        SharedPreferences passwordPref;
        passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        if (passwordPref.contains(AppLockConstants.PASSWORD)) {
            return passwordPref.getString(AppLockConstants.PASSWORD, "");
        }
        return "";
    }
}
