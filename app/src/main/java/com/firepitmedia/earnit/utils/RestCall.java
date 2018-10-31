package com.firepitmedia.earnit.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.model.AppUsage;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.retrofit.RetroInterface;
import com.firepitmedia.earnit.service.UpdateFcmToken;
import com.firepitmedia.earnit.service.applock_service.AppCheckServices;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;
import okhttp3.ResponseBody;
import retrofit.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mobile-di on 12/8/17.
 */

public class RestCall {
    private static Activity activity;
    String token;
    final String TAG = "RestCall";
    ScreenSwitch screenSwitch;
    Parent parent;
    private static SharedPreferences sp;

    private OnAuthorizedListener authorizedListener;

    public interface OnAuthorizedListener{
        void onAuthorizeSuccessful();
        void onAuthorizeFailed();
    }

    public void setAuthorizedListener(OnAuthorizedListener authorizedListener) {
        this.authorizedListener = authorizedListener;
    }

    public RestCall(Activity activity) {
        this.activity = activity;
        screenSwitch = new ScreenSwitch(this.activity);
    }

    public void authenticateUser(final String username, final String password, final EditText editPassword, final String from, final RelativeLayout progressBar) {
        Utils.logDebug(TAG, "authenticateUser: username = " + username + "; password = " + password);
        Log.d("UpdateFCMToken", "authenticateUser");

        sp = activity.getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        Utils.logDebug(TAG, " GeneratedTokenI " + sp.getString(AppConstant.TOKEN_ID, null));
        token = sp.getString(AppConstant.TOKEN_ID, null);

        try {
            AsyncHttpClient httpClient = new AsyncHttpClient();
            PersistentCookieStore myCookieStore = new PersistentCookieStore(activity);
            httpClient.setCookieStore(myCookieStore);
            String namePassword = username.trim() + ":" + password.trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            httpClient.addHeader("Authorization", basicAuth);
            httpClient.setMaxRetriesAndTimeout(3, 3000);
            Utils.logDebug(TAG, " login-Rquest " + AppConstant.BASE_URL + AppConstant.LOGIN_API);
            Utils.logDebug(TAG, " login-Rquest Header: " + namePassword);
            Utils.logDebug(TAG, " login-Rquest encoded Header: " + basicAuth);
            httpClient.get(activity, AppConstant.BASE_URL + AppConstant.LOGIN_API, null, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (authorizedListener != null){
                        authorizedListener.onAuthorizeSuccessful();
                    }
                    try {
                        Utils.logDebug(TAG, " login-Rquest onSuccess" + response.toString());
                        Log.d("UpdateFCMToken", "authenticateUser onSuccess");

                        MyApplication.getInstance().setPassword(password);
                        MyApplication.getInstance().setEmail(username);
                        sp.edit()
                                .putString(AppConstant.EMAIL, response.getString(AppConstant.EMAIL))
                                .putString(AppConstant.PASSWORD, password)
                                .apply();

                        Intent updateToken = new Intent(activity, UpdateFcmToken.class);
                        updateToken.putExtra(AppConstant.IS_LOGOUT, false);
                        if (response.getString(AppConstant.TYPE).equals(AppConstant.PARENT)) {
                            if (activity != null &&
                                    MyApplication.getInstance().isMyServiceRunning(AppCheckServices.class) &&
                                    MyApplication.getInstance().imRealParent(response.getInt(AppConstant.ID))) {
                                activity.stopService(new Intent(activity, AppCheckServices.class));
                            }
                            MyApplication.getInstance().setUserType(AppConstant.PARENT);
                            parent = new GetObjectFromResponse().getParentObject(response);
                            MyApplication.getInstance().setUserType(AppConstant.PARENT);

                            // token update for child
                            updateToken.putExtra(AppConstant.PARENT_OBJECT, parent);
                            updateToken.putExtra(AppConstant.MODE, AppConstant.PARENT);
                            if (parent.getFcmToken().isEmpty()) {
                                Utils.logDebug(TAG, " parent-FCM not available");
                                updateToken.putExtra(AppConstant.FCM_TOKEN, token);
                                activity.startService(updateToken);
                            } else {
                                if (!parent.getFcmToken().equalsIgnoreCase(token)) {
                                    Utils.logDebug(TAG, "parent-FCM updating");
                                    updateToken.putExtra(AppConstant.FCM_TOKEN, token);
                                    activity.startService(updateToken);
                                }
                            }

                            if (parent.getFirstName().isEmpty() || parent.getPhone().isEmpty()) {
                                screenSwitch.moveToInitialParentProfile(parent, password);
                            } else {
                                screenSwitch.moveToParentDashboard(parent);
                            }

                        } else if (response.getString(AppConstant.TYPE).equals(AppConstant.CHILD)) {
                            if (!MyApplication.getInstance().isMyServiceRunning(AppCheckServices.class) &&
                                    activity != null) {
                                MyApplication.getInstance().setChildId(response.getInt(AppConstant.ID));
                                activity.startService(new Intent(activity, AppCheckServices.class));
                            }
                            MyApplication.getInstance().setUserType(AppConstant.CHILD);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && !isAlreadySentAppUsage()) {
                                Log.d("sadasd", "http://159.65.239.6:8080/earnit-api/mobileapplicationsdasdasdassdasdasd@@@@@@@@@@@@@@@@@@@@@@@@@");
                                updateAppsUsage();
                            }

                            Child child = new GetObjectFromResponse().getChildObject(response);
                            MyApplication.getInstance().setChildId(child.getId());
                            //TASKS
                            ArrayList<Tasks> taskList = new ArrayList<>();

                            JSONArray taskArray = response.getJSONArray(AppConstant.TASKS);
                            for (int taskIndex = 0; taskIndex < taskArray.length(); taskIndex++) {
                                JSONObject taskObject = taskArray.getJSONObject(taskIndex);
                                Tasks task = new GetObjectFromResponse().getTaskObject(taskObject, child.getId());
                                taskList.add(task);
                            }

                            child.setTasksArrayList(taskList);
                            if (child.getMessage().isEmpty()) {
                                updateToken.putExtra(AppConstant.PARENT_OBJECT, parent);
                                updateToken.putExtra(AppConstant.CHILD_OBJECT, child);
                                updateToken.putExtra(AppConstant.MODE, AppConstant.CHILD);
                                if (child.getFcmToken().isEmpty()) {
                                    Utils.logDebug(TAG, "child-FCM not available");
                                    updateToken.putExtra(AppConstant.FCM_TOKEN, token);
                                    activity.startService(updateToken);
                                } else {
                                    if (!child.getFcmToken().equalsIgnoreCase(token)) {
                                        Utils.logDebug(TAG, "child-FCM updating");
                                        updateToken.putExtra(AppConstant.FCM_TOKEN, token);
                                        activity.startService(updateToken);
                                    }
                                }
                                //LOAD CHILD ACTIVITY
                                screenSwitch.moveTOChildDashboard(child, false);
                            } else {
                                if (AppConstant.MESSAGE_STATUS) {
                                    //LOAD message ACTIVITY
                                    screenSwitch.moveToMessage(child);
                                    AppConstant.MESSAGE_STATUS = false;
                                } else {
                                    screenSwitch.moveTOChildDashboard(child, false);
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e("dkdslfkj", "ErrorAuthenticate: " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (authorizedListener != null){
                        authorizedListener.onAuthorizeFailed();
                    }
                    Utils.logDebug(TAG, " login-Rquest onFailure. JSONObject errorResponse: " + errorResponse);
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable: " + throwable.getLocalizedMessage());
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable.toString(): " + throwable.toString());
                    Utils.logDebug(TAG, " login-Rquest onFailure. Throwable.getMessage(): " + throwable.getMessage());
                    Utils.logDebug(TAG, " login-Rquest onFailure. statusCode: " + statusCode);
                    clearEdittext(from, editPassword);
                    Utils.unLockScreen(activity.getWindow());


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    if (authorizedListener != null){
                        authorizedListener.onAuthorizeFailed();
                    }
                    Utils.logDebug(TAG, " login-Rquest onFailureA" + errorResponse.toString());
                    clearEdittext(from, editPassword);
                    Utils.unLockScreen(activity.getWindow());

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (authorizedListener != null){
                        authorizedListener.onAuthorizeSuccessful();
                    }
                    Utils.logDebug(TAG, " login-Rquest onSuccessA" + response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (authorizedListener != null){
                        authorizedListener.onAuthorizeFailed();
                    }
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
                    clearEdittext(from, editPassword);
                }

                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    Utils.lockScreen(activity.getWindow());
                }

                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                    Utils.unLockScreen(activity.getWindow());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAlreadySentAppUsage() {
        if (sp == null) {
            sp = activity.getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        }
        sp = activity.getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        long lastUpdate = sp.getLong(AppConstant.APP_USAGE_LAST_UPDATE, -1);
        Log.d("sdjfhkj", "isAlreadySentAppUsage; lastUpdate = " + lastUpdate + "; isToday = " + isToday(lastUpdate));
        if (lastUpdate == -1)
            return false;
        else
            return isToday(lastUpdate);
    }

    private static boolean isToday(long lastUpdateMillis) {
        DateTime today = new DateTime();
        DateTime lastUpdate = new DateTime(lastUpdateMillis);
        return today.getDayOfMonth() == lastUpdate.getDayOfMonth()
                && today.getMonthOfYear() == lastUpdate.getMonthOfYear()
                && today.getYear() == lastUpdate.getYear();
    }

    public void clearEdittext(String from, EditText editPassword) {
        if (from.equalsIgnoreCase(AppConstant.LOGIN_SCREEN) && editPassword != null) {
            editPassword.setText("");
        }
        Utils.showToast(activity, activity.getResources().getString(R.string.login_failed));
    }

    public void fetchUpdatedChild(final Parent parentObject, final String childEmail, final RelativeLayout progressBar, final String onScreen) {

        final AsyncHttpClient client = new AsyncHttpClient();
        String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
        final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
        client.addHeader("Authorization", basicAuth);
        client.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        client.get(AppConstant.BASE_URL + AppConstant.CHILDREN_API + parentObject.getAccount().getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        if (object.getString(AppConstant.EMAIL).equals(childEmail)) {
                            Child child = new GetObjectFromResponse().getChildObject(object);
                            Child otherChild = new GetObjectFromResponse().getChildObject(object);

                            //TASKS
                            ArrayList<Tasks> taskList = new ArrayList<>();
                            ArrayList<Tasks> otherTaskList = new ArrayList<>();
                            JSONArray taskArray = object.getJSONArray(AppConstant.TASKS);
                            Utils.logDebug(TAG, "Fetch child list for " + onScreen);
                            if (onScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN) || onScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
                                for (int taskIndex = 0; taskIndex < taskArray.length(); taskIndex++) {
                                    JSONObject taskObject = taskArray.getJSONObject(taskIndex);
                                    if (!taskObject.getString(AppConstant.STATUS).equals(AppConstant.APPROVED)) {
                                        Tasks task = new GetObjectFromResponse().getTaskObject(taskObject, child.getId());
                                        taskList.add(task);
                                    }

                                    JSONObject othertaskObject = taskArray.getJSONObject(taskIndex);
                                    if (othertaskObject.getString(AppConstant.STATUS).equals(AppConstant.COMPLETED)) {
                                        Tasks task = new GetObjectFromResponse().getTaskObject(othertaskObject, child.getId());
                                        otherTaskList.add(task);
                                    }
                                }
                            }
                            child.setTasksArrayList(taskList);
                            otherChild.setTasksArrayList(otherTaskList);
                            screenSwitch.moveToAllTaskScreen(child, otherChild, onScreen, parentObject, onScreen);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null)
                    Utils.logDebug(TAG, " Child error response:" + errorResponse.toString());
                else
                    Utils.logDebug(TAG, " Child error response = null. Throwable: " + throwable.getLocalizedMessage());

            }

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
//    public static void updateAppsUsage() {
//        Log.d("sdjfhkj", "updateAppsUsage()");
//        AppsUsageHelper appsUsageHelper = new AppsUsageHelper(activity);
//        List<AppUsage> list = appsUsageHelper.getAppUsages();
//        Log.d("kdfjhkjh", "Credentials: \nEmail: '" + sp.getString(AppConstant.EMAIL, "") + "'\nPassword: '" + sp.getString(AppConstant.PASSWORD, "") + "'");
//        for (AppUsage appUsage : list) {
//            Log.d("kdfjhkjh", "appUsage: " + appUsage.toString());
//        }
//        RetroInterface retroInterface = ServiceGenerator.createService(RetroInterface.class, sp.getString(AppConstant.EMAIL, ""), sp.getString(AppConstant.PASSWORD, ""));
//        Call<Response<ResponseBody>> createAppsUsage = retroInterface.createAppUsages(list);
//        createAppsUsage.enqueue(new Callback<Response<ResponseBody>>() {
//            @Override
//            public void onResponse(@NonNull Call<Response<ResponseBody>> call, @NonNull Response<Response<ResponseBody>> response) {
//                Log.d("sdjfhkj", "Response body: " + response.body());
//                Log.d("sdjfhkj", "Response code: " + response.code());
//                sp.edit().putLong(AppConstant.APP_USAGE_LAST_UPDATE, new DateTime().getMillis()).apply();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Response<ResponseBody>> call, @NonNull Throwable t) {
//                // TODO: 23.06.2018
//                // return: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $
//                Log.d("sdjfhkj", "onFailure: " + t.getMessage());
//            }
//        });
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static void updateAppsUsage() {
        if (sp == null) {
            sp = activity.getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        }
        Log.d("sdjfhkj", "updateAppsUsage()");
        AppsUsageHelper appsUsageHelper = new AppsUsageHelper(activity);
        List<AppUsage> list = appsUsageHelper.getAppUsages();
        Log.d("kdfjhkjh", "Credentials: \nEmail: '" + sp.getString(AppConstant.EMAIL, "") + "'\nPassword: '" + sp.getString(AppConstant.PASSWORD, "") + "'");
        for (AppUsage appUsage : list) {
            Log.d("kdfjhkjh", "appUsage: " + appUsage.toString());
        }
        RetroInterface retroInterface = ServiceGenerator.createService(RetroInterface.class, sp.getString(AppConstant.EMAIL, ""), sp.getString(AppConstant.PASSWORD, ""));
        Call<ResponseBody> createAppsUsage = retroInterface.createAppUsages(list);
        createAppsUsage.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d("sdjfhkj", "Response body: " + response.message());
                Log.d("sdjfhkj", "Response code: " + response.code());
                sp.edit().putLong(AppConstant.APP_USAGE_LAST_UPDATE, new DateTime().getMillis()).apply();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // TODO: 23.06.2018
                // return: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $
                Log.d("sdjfhkj", "onFailure: " + t.getMessage());
            }
        });
    }
}
