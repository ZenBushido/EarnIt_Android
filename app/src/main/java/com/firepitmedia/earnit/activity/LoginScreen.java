package com.firepitmedia.earnit.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.RestCall;
import com.firepitmedia.earnit.utils.Utils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

/**
 * Created by mradul on 7/4/17.
 */

public class LoginScreen extends BaseActivity implements View.OnClickListener, Validator.ValidationListener, RestCall.OnAuthorizedListener {

    @NotEmpty
    @Email
    EditText username;
    @Password(min = 6, scheme = Password.Scheme.ANY)

    EditText password;
    Button loginButton; TextView sign_up;
    CheckBox chRemember;
    LoginScreen loginScreen;
    RelativeLayout progressBar;
    SharedPreferences preferences;
    Validator validator;
    private final String TAG = "LoginScreen";
    Resources resources;
    TextView forgot;

    private RestCall restCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_layout);
//        ArrayList

        /*if (!TextUtils.isEmpty(MyApplication.getInstance().getEmail())
                && !TextUtils.isEmpty(MyApplication.getInstance().getPassword())
                && !TextUtils.isEmpty(MyApplication.getInstance().getUserType()))
            startService(new Intent(LoginScreen.this, AppCheckServices.class));*/

        Log.d("ldfgl", "LoginScreen.onCreate()");
//        startService(new Intent(LoginScreen.this, AppCheckServices.class));
        /*This method is used to catch the exception in an Application.*/
        FirebaseCrash.report(new Exception());

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SYSTEM_ALERT_WINDOW}, MY_REQUEST_CODE);
            }
        }*/

        preferences = getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        loginScreen = this;
        chRemember = findViewById(R.id.chRemember);
        forgot = (TextView) findViewById(R.id.sign_forgot);
        username = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.login);
        sign_up = (TextView) findViewById(R.id.sign_up);
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        username.setOnClickListener(loginScreen);
        password.setOnClickListener(loginScreen);
        loginButton.setOnClickListener(loginScreen);
        sign_up.setOnClickListener(loginScreen);
        forgot.setOnClickListener(loginScreen);
        chRemember.setOnClickListener(this);
        resources = getResources();
        setCursorPosition();
        updateToken();
        
        restCall = new RestCall(this);
        restCall.setAuthorizedListener(this);
        if (!preferences.getBoolean(AppConstant.NEED_TO_SAVE_CREDENTIALS, false)){
            username.setText(preferences.getString(AppConstant.EMAIL, ""));
            password.setText(preferences.getString(AppConstant.PASSWORD, ""));
            chRemember.setChecked(true);
            if (!TextUtils.isEmpty(username.getText().toString().trim()) || !TextUtils.isEmpty(password.getText().toString().trim())) {
                restCall.authenticateUser(preferences.getString(AppConstant.EMAIL, ""), preferences.getString(AppConstant.PASSWORD, ""), password, AppConstant.LOGIN_SCREEN, progressBar);
            }
        }
        chRemember.setOnCheckedChangeListener((compoundButton, b) ->
                preferences.edit().putBoolean(AppConstant.NEED_TO_SAVE_CREDENTIALS, b).apply());

        validator = new Validator(loginScreen);
        validator.setValidationListener(loginScreen);

    }

    private void updateToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult ->
                getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE).edit().
                putString(AppConstant.TOKEN_ID, instanceIdResult.getToken()).apply());
    }

    private void setCursorPosition() {
        Utils.SetCursorPosition(username);
        Utils.SetCursorPosition(password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                validator.validate();
                break;
            case R.id.sign_up:
                showDialogOnCheckBox();
                break;
            case R.id.sign_forgot:
                moveToPasswordReminder();
                break;
            case R.id.chRemember:
                if (chRemember.isChecked()){
                    showRemeberMeDialog();
                }
                break;
        }
    }

    public void showRemeberMeDialog() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Confirmation")
                .setMessage("Do you want the EarnIt to save your password")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        chRemember.setChecked(false);
                    }
                })
                .show();

    }

    private void moveToPasswordReminder() {
        Intent moveToPasswordReminder = new Intent(LoginScreen.this, PasswordReminder.class);
        moveToPasswordReminder.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(moveToPasswordReminder);
    }

    private void moveToSignUp() {
        Intent moveToSignUp = new Intent(LoginScreen.this, SignUp.class);
        moveToSignUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(moveToSignUp);
    }

    public void showDialogOnCheckBox() {
        final Dialog dialog = new Dialog(loginScreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.9f;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_box);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
        message.setText(resources.getString(R.string.ask_user_type));
        TextView declineButton = (TextView) dialog.findViewById(R.id.cancel);
        declineButton.setText(AppConstant.NO);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(resources.getString(R.string.child_sign_up_message));
                dialog.dismiss();
            }
        });
        TextView acceptButton = dialog.findViewById(R.id.ok);
        acceptButton.setText(AppConstant.YES);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSignUp();
            }
        });
        dialog.show();
    }

    @Override
    public void onValidationSucceeded() {
        Utils.logDebug(TAG, "onValidationSucceeded: username = " + username.getText().toString().trim() + "; password = " + password.getText().toString().trim());
        restCall.authenticateUser(username.getText().toString().trim(), password.getText().toString().trim(), password, AppConstant.LOGIN_SCREEN, progressBar);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                showToast(message);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onAuthorizeSuccessful() {
        if (preferences.getBoolean(AppConstant.NEED_TO_SAVE_CREDENTIALS, false)){
            preferences.edit().putString(AppConstant.EMAIL, username.getText().toString())
                    .putString(AppConstant.PASSWORD, password.getText().toString()).apply();
        }
    }

    @Override
    public void onAuthorizeFailed(){
        preferences.edit().remove(AppConstant.EMAIL).remove(AppConstant.PASSWORD).apply();
        password.setText("");
    }
}
