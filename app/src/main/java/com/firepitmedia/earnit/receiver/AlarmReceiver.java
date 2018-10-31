package com.firepitmedia.earnit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.service.applock_service.AppCheckServices;

/**
 * Created by amitshekhar on 02/05/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.isEmpty(MyApplication.getInstance().getEmail())
                && !TextUtils.isEmpty(MyApplication.getInstance().getPassword())
                && !TextUtils.isEmpty(MyApplication.getInstance().getUserType()))
            context.startService(new Intent(context, AppCheckServices.class));
    }
}
