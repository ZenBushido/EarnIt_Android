package com.firepitmedia.earnit.service;

/**
 * Created by praks on 18/08/17.
 */

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.firepitmedia.earnit.utils.Utils;


public class CustomFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "CustomFIIDS";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Utils.logDebug(TAG, "NEW TOKEN: " + token);
        startService(new Intent(CustomFirebaseInstanceIDService.this, CustomFirebaseMessagingService.class));
        // TODO: Implement this method to send token to your app server.
    }
}