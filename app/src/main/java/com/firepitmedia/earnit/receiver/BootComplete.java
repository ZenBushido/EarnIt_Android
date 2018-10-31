package com.firepitmedia.earnit.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.service.applock_service.AppCheckServices;

/**
 * Created by amitshekhar on 28/04/15.
 */
public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("football ", "BootComplete");

        if (!TextUtils.isEmpty(MyApplication.getInstance().getEmail())
                && !TextUtils.isEmpty(MyApplication.getInstance().getPassword())
                && !TextUtils.isEmpty(MyApplication.getInstance().getUserType()))
            context.startService(new Intent(context, AppCheckServices.class));

        /*-------alarm setting after boot again--------*/
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = (86400 * 1000) / 4;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }
}
