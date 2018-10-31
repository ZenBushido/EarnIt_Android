package com.firepitmedia.earnit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.dialogfragment.PasswordReminderResultFragment;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class PasswordReminder extends BaseActivity implements View.OnClickListener, Validator.ValidationListener {

    PasswordReminder passwordReminder;
    ScreenSwitch screenSwitch;
    Button sendButton;
    Validator validator;
    RelativeLayout progressBar;
    ImageButton ivBackArrow;
    EditText user_email;
    TextView passwordremindertxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reminder_sign);

        passwordReminder = this;
        screenSwitch = new ScreenSwitch(passwordReminder);
        sendButton = (Button) findViewById(R.id.passwordreminder_send);
        user_email = (EditText) findViewById(R.id.user_email);
        passwordremindertxt = (TextView) findViewById(R.id.passwordreminder_text_id);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*This method is used to check to reset password.*/

                String emailAddress = user_email.getText().toString();

                if (validateEmail(emailAddress)) {
                    try {
                        resetPassWordAndCheckOnYourEmail(emailAddress);
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                else
                {


                }

            }
        });
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        ivBackArrow = (ImageButton) findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(passwordReminder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.ivBackArrow:
                startActivity(new Intent(passwordReminder, LoginScreen.class));
                finish();
                break;
        }
    }

    private boolean validateEmail(String emailAddress) {
        if (emailAddress.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches())) {
            if (emailAddress.isEmpty()) {
                user_email.setError("Enter email address");
            } else {
                user_email.setError("Sorry, we don’t have that username, or account in our system. Please verify the information, or contact support at support@myearnitapp.com");
            }

            return false;
        } else {
            user_email.setError(null);

        }

        return true;
    }

    private void resetPassWordAndCheckOnYourEmail(final String emailAddress) throws JSONException, UnsupportedEncodingException {

        JSONObject passwordReminderInJson = new JSONObject();
        passwordReminderInJson.put(AppConstant.EMAIL, emailAddress);
        StringEntity entity = new StringEntity(passwordReminderInJson.toString());
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
        final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
        httpClient.addHeader("Authorization", basicAuth);

        httpClient.post(this, AppConstant.BASE_URL + AppConstant.PASSWORD_REMINDER, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] header, JSONObject response) {
                Log.d("Success Code", String.valueOf(statusCode));
                Log.d("Success Response", response.toString());
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                PasswordReminderResultFragment passwordReminderResultFragment = new PasswordReminderResultFragment().getInstance(emailAddress);
                ft.replace(R.id.password_reminderid_sign, passwordReminderResultFragment);
                ft.commit();
                try {
                    String passowrdFinal = response.getString("message");
                    String finalPasswordFinal = passowrdFinal.substring(2, passowrdFinal.length() - 2);

                    Toast.makeText(PasswordReminder.this, finalPasswordFinal + "successfully.", Toast.LENGTH_SHORT).show();



                    //finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Failure Code", String.valueOf(statusCode));
                Log.d("Failure Response", throwable.toString());

                Log.d("BaseURL", AppConstant.BASE_URL + AppConstant.PASSWORD_REMINDER);
                user_email.setError("Sorry, we don’t have that username, or account in our system. Please verify the information, or contact support at support@myearnitapp.com");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failure Code", String.valueOf(statusCode));
                Log.d("Failure Response", throwable.toString());

                Log.d("BaseURL", AppConstant.BASE_URL + AppConstant.PASSWORD_REMINDER);
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
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
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        screenSwitch.moveToLogin();
    }
}
