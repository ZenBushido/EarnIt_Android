package com.firepitmedia.earnit.activity;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Country;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.retrofit.RetroInterface;
import com.firepitmedia.earnit.retrofit.RetrofitClient;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.GetObjectFromResponse;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.firepitmedia.earnit.utils.Utils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import id.zelory.compressor.Compressor;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mradul on 8/4/17.
 */

public class AddChild extends UploadRuntimePermission implements View.OnClickListener, Validator.ValidationListener {

    final int PERMISSIONS_REQUEST = 13;
    public Intent in = null;

    @Pattern(regex = "[a-zA-Z ]+(\\\\s+[a-zA-Z ]+)*", message = "Please enter valid username")
    @Length(max = 12, min = 2, message = "")
    @BindView(R.id.child_first_name)
    EditText firstName;

    @NotEmpty
    @Email
    @BindView(R.id.child_email)
    EditText email;
    @BindView(R.id.child_phone)
    EditText phone;

    @Password(min = 6, scheme = Password.Scheme.ANY)
    @BindView(R.id.child_password)
    EditText password;

  /*  @ConfirmPassword
    @BindView(R.id.child_confirm_password)
    EditText confirmPassword;*/

    @BindView(R.id.save_button)
    Button save;
    @BindView(R.id.cancel_button)
    Button cancel;
    AddChild addChild;
    Parent parentObject;
    Child child;
    Validator validator;
    @BindView(R.id.loadingPanel)
    RelativeLayout progressBar;

    @BindView(R.id.child_user_image)
    CircularImageView childAvatar;
    @BindView(R.id.add_child_header_id)
    TextView addChildHeader;
    String mode, switchFrom;
    int childId;
    public final String TAG = "AddChild";
    ScreenSwitch screenSwitch;
    ArrayList<Country> countries;
    //TextView countryName, countryDial;
    @BindView(R.id.ivBackArrow)
    ImageButton ivBackButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_child_layout);
        ButterKnife.bind(this);
        addChild = this;
        setViewId();
        screenSwitch = new ScreenSwitch(addChild);
        parentObject = (Parent) getIntent().getSerializableExtra(AppConstant.PARENT_OBJECT);
        child = (Child) getIntent().getSerializableExtra(AppConstant.CHILD_OBJECT);
        if (child != null) {
            childId = child.getId();
        }
        Log.d("sdfksjdh", "childId = " + childId);
        Log.d("sdfksjdh", "child = " + child);
        mode = getIntent().getStringExtra(AppConstant.MODE);
        switchFrom = getIntent().getStringExtra(AppConstant.SCREEN);


        countries = new ArrayList<>();
        countries = Utils.loadCountryData(TAG);
        if (mode.equalsIgnoreCase(AppConstant.UPDATE)) {
            addChildHeader.setText(AppConstant.EDIT + " Child");
            save.setText(AppConstant.UPDATE);


            RequestOptions requestOptions = new RequestOptions();
            requestOptions.override(350, 350);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.placeholder(R.drawable.default_avatar);
            requestOptions.error(R.drawable.default_avatar);

            /*try {
                Picasso.with(addChild).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + child.getAvatar()).error(R.drawable.default_avatar).into(childAvatar);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(addChild).load(R.drawable.default_avatar).into(childAvatar);
            }*/
            email.setText(child.getEmail());
            firstName.setText(child.getFirstName().substring(0, 1) + child.getFirstName().substring(1));
            password.setText(child.getPassword());
            // confirmPassword.setText(child.getPassword());

            if (!child.getPhone().isEmpty()) {
                if (child.getPhone().length() > 10) {
                    String childPhone = child.getPhone();
                    Utils.logDebug(TAG, String.valueOf(childPhone.length()));
                    String cPhone = childPhone.substring(childPhone.length() - 10, childPhone.length());
                    String cCountryCode = childPhone.substring(0, childPhone.length() - 10);
                    for (Country country : countries) {
                        if (country.getCountryDialCode().equalsIgnoreCase(cCountryCode)) {
//                                countryName.setText(country.getCountryCode());
//                                countryDial.setText(country.getCountryDialCode());
                            break;
                        } else {
//                                countryName.setText(AppConstant.D_COUNTRIES_CODE);
//                                countryDial.setText(AppConstant.D_COUNTRIES_DIAL);
                        }
                    }
                    phone.setText(cPhone);

                } else {
//                        countryName.setText(AppConstant.D_COUNTRIES_CODE);
//                        countryDial.setText(AppConstant.D_COUNTRIES_DIAL);
                    phone.setText(child.getPhone());
                }
            }
        } else save.setText(AppConstant.SAVE);
        requestRequiredApplicationPermission(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, R.string.msg, PERMISSIONS_REQUEST);

        validator = new Validator(addChild);
        validator.setValidationListener(addChild);
        setCursorPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gFileName == null && child != null) {
            updateAvatar();
        }
    }

    private void updateAvatar() {
        String url = AppConstant.BASE_URL + "/" + child.getAvatar();
        Log.d("fsdfhkj", "updateAvatar. url = " + url);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        String emailPassword = MyApplication.getInstance().getEmail() + ":" + MyApplication.getInstance().getPassword();
                        String basic = "Basic " + Base64.encodeToString(emailPassword.getBytes(), Base64.NO_WRAP);
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", basic)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client))
                .build();
        picasso
                .load(url)
                .error(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.default_avatar)))
                .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.default_avatar)))
                .into(childAvatar);
    }

//    private void updateAvatar() {
//        String url = AppConstant.AMAZON_URL + child.getAvatar();
//        Log.d("fsdfhkj", "updateAvatar: " + url);
//        Log.d("fsdfhkj", "child id: " + child.getId());
//
//        Picasso
//                .get()
//                .load(url)
//                .error(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.default_avatar)))
//                .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.default_avatar)))
//                .into(childAvatar);
//    }

    private void setViewId() {

        //countryName = (TextView) findViewById(R.id.countryCode);
        // countryDial = (TextView) findViewById(R.id.country_dial_code);
//        countryName.setCompoundDrawablesWithIntrinsicBounds(null, null, (new IconDrawable(addChild, FontAwesomeIcons.fa_caret_down)
//                .colorRes(R.color.edit_text_hint).sizeDp(AppConstant.FEB_ICON_SIZE)), null);
        save.setOnClickListener(addChild);
        cancel.setOnClickListener(addChild);
        childAvatar.setOnClickListener(addChild);
        ivBackButton.setOnClickListener(addChild);
        //countryName.setOnClickListener(addChild);
    }

    private void setCursorPosition() {
        Utils.SetCursorPosition(firstName);
        Utils.SetCursorPosition(email);
        Utils.SetCursorPosition(phone);
        Utils.SetCursorPosition(password);
        // Utils.SetCursorPosition(confirmPassword);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.cancel_button:

                Log.e(TAG, "Switchfrom: " + switchFrom);
                screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);

                break;

            case R.id.save_button:
                validator.validate();
                break;

            case R.id.child_user_image:
                vRuntimePermission(childAvatar);
                selectImage();
                break;

            case R.id.ivBackArrow:
                onBackPressed();
                break;


//            case R.id.countryCode:
//                Utils.showCountryDialog(countries, addChild, countryDial, countryName);
//                break;


        }
    }

    @Override
    public void onBackPressed() {
        screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);
    }

    @Override
    public void onValidationSucceeded() {
        if (Utils.validatePhoneNumber(phone.getText().toString()) && phone.getText().toString().trim().length() == 10) {
            addChild(null);
        } else {
            phone.setError("Please enter 10 digit number only");
        }
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


    private void addChildNew(String childImage) {

        final JSONObject signInJson = new JSONObject();
        try {
            signInJson.put(AppConstant.ACCOUNT, new JSONObject().put(AppConstant.ID, parentObject.getAccount().getId()));
            signInJson.put(AppConstant.EMAIL, email.getText().toString().trim());
            signInJson.put(AppConstant.FIRST_NAME, firstName.getText().toString().trim());
            signInJson.put(AppConstant.LAST_NAME, null);
            signInJson.put(AppConstant.PASSWORD, password.getText().toString().trim());
            signInJson.put(AppConstant.PHONE, phone.getText().toString().trim());
            signInJson.put(AppConstant.CREATE_DATE, new DateTime().getMillis());
            signInJson.put(AppConstant.UPDATE_DATE, new DateTime().getMillis());

            if (mode.equalsIgnoreCase(AppConstant.UPDATE)) {
                signInJson.put(AppConstant.ID, child.getId());
                signInJson.put(AppConstant.FCM_TOKEN, child.getFcmToken());
            }
            if (UrlOfImage != null)
                signInJson.put(AppConstant.AVATAR, UrlOfImage);

            Utils.logDebug(TAG + " add-child-json", signInJson.toString());
            StringEntity entity = new StringEntity(signInJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            httpClient.addHeader("Authorization", basicAuth);
            httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
            PersistentCookieStore myCookieStore = new PersistentCookieStore(addChild);
            httpClient.setCookieStore(myCookieStore);
            if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                httpClient.put(addChild, AppConstant.BASE_URL + AppConstant.UPDATE_CHILD, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Utils.logDebug(TAG + " onSuccess", response.toString());
                        if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                            showToast(firstName.getText() + " updated");
                        else
                            showToast(firstName.getText() + " added");

                        screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Utils.logDebug(TAG + " onSuccess", response.toString());

                        if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                            showToast(firstName.getText() + " updated");
                        else
                            showToast(firstName.getText() + " added");

                        screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Utils.logDebug(TAG + " onFailure", throwable.toString());
                        unLockScreen();
                        josnError(errorResponse);

                    }

                    @Override
                    public void onStart() {
                        progressBar.setVisibility(View.VISIBLE);
                        lockScreen();

                    }

                    @Override
                    public void onFinish() {
                        progressBar.setVisibility(View.GONE);
                        unLockScreen();

                    }
                });
            else
                httpClient.post(addChild, AppConstant.BASE_URL + AppConstant.ADD_CHILD_PARENT, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Utils.logDebug(TAG + " onSuccess", response.toString());
                        if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                            showToast(firstName.getText() + " updated");
                        else
                            showToast(firstName.getText() + " added");

                        screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Utils.logDebug(TAG + " onSuccess", response.toString());

                        if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                            showToast(firstName.getText() + " updated");
                        else
                            showToast(firstName.getText() + " added");

                        screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Utils.logDebug(TAG + " onFailure", throwable.toString());
                        Toast.makeText(addChild, getResources().getString(R.string.user_already_exists), Toast.LENGTH_LONG).show();
                        unLockScreen();
                        josnError(errorResponse);
                    }

                    @Override
                    public void onStart() {
                        progressBar.setVisibility(View.VISIBLE);
                        lockScreen();

                    }

                    @Override
                    public void onFinish() {
                        progressBar.setVisibility(View.GONE);
                        unLockScreen();

                    }
                });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void updateChildProfile(Child child, String imageUrl){
        Log.d("ldsfjjlk", "updateChildProfile");
        final JSONObject signInJson = new JSONObject();
        try {
            if (child.getAccount() != null) {
                signInJson.put(AppConstant.ACCOUNT, child.getAccount().getId());
            }
            signInJson.put(AppConstant.EMAIL, child.getEmail());
            signInJson.put(AppConstant.FIRST_NAME, child.getEmail());
            signInJson.put(AppConstant.LAST_NAME, child.getLastName());
            signInJson.put(AppConstant.PASSWORD, child.getPassword());
            signInJson.put(AppConstant.PHONE, child.getPhone());
            signInJson.put(AppConstant.CREATE_DATE, child.getCreateDate());
            signInJson.put(AppConstant.UPDATE_DATE, child.getUpdateDate());

            if (mode.equalsIgnoreCase(AppConstant.UPDATE)) {
                signInJson.put(AppConstant.ID, child.getId());
                signInJson.put(AppConstant.FCM_TOKEN, child.getFcmToken());
            }

                signInJson.put(AppConstant.AVATAR, imageUrl);

            Utils.logDebug(TAG + " add-child-json", signInJson.toString());
            StringEntity entity = new StringEntity(signInJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            httpClient.addHeader("Authorization", basicAuth);
            httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
            PersistentCookieStore myCookieStore = new PersistentCookieStore(addChild);
            httpClient.setCookieStore(myCookieStore);
            if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                httpClient.put(addChild, AppConstant.BASE_URL + AppConstant.UPDATE_CHILD, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("ldsfjjlk", "onSuccess. Response: " + response.toString());
                        Utils.logDebug(TAG + " onSuccess", response.toString());
                        if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                            showToast(firstName.getText() + " updated");
                        else
                            showToast(firstName.getText() + " added");

                        screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d("ldsfjjlk", "updateChildProfile onSuccess. Response: " + response.toString());
                        Utils.logDebug(TAG + " onSuccess", response.toString());
                        if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                            showToast(firstName.getText() + " updated");
                        else
                            showToast(firstName.getText() + " added");

                        screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("ldsfjjlk", "updateChildProfile onFailure: " + throwable.toString());
                        unLockScreen();

                    }

                    @Override
                    public void onStart() {
                        Log.d("ldsfjjlk", "updateChildProfile onStart");
                        progressBar.setVisibility(View.VISIBLE);
                        lockScreen();

                    }

                    @Override
                    public void onFinish() {
                        Log.d("ldsfjjlk", "updateChildProfile onFinish");
                        progressBar.setVisibility(View.GONE);
                        unLockScreen();

                    }
                });
        } catch (JSONException | UnsupportedEncodingException e){
            Log.d("ldsfjjlk", "Exception e: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addChild(final String childImage) {
        Log.d("ldsfjjlkd", "addChild");
        final JSONObject signInJson = new JSONObject();
        try {
            signInJson.put(AppConstant.ACCOUNT, new JSONObject().put(AppConstant.ID, parentObject.getAccount().getId()));
            signInJson.put(AppConstant.EMAIL, email.getText().toString().trim());
            signInJson.put(AppConstant.FIRST_NAME, firstName.getText().toString().trim());
            signInJson.put(AppConstant.LAST_NAME, null);
            signInJson.put(AppConstant.PASSWORD, password.getText().toString().trim());
            signInJson.put(AppConstant.PHONE, phone.getText().toString().trim());
            signInJson.put(AppConstant.CREATE_DATE, new DateTime().getMillis());
            signInJson.put(AppConstant.UPDATE_DATE, new DateTime().getMillis());

            if (mode.equalsIgnoreCase(AppConstant.UPDATE)) {
                signInJson.put(AppConstant.ID, child.getId());
                signInJson.put(AppConstant.FCM_TOKEN, child.getFcmToken());
            }
            if (UrlOfImage != null)
                signInJson.put(AppConstant.AVATAR, UrlOfImage);

            Utils.logDebug(TAG + " add-child-json", signInJson.toString());
            StringEntity entity = new StringEntity(signInJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            httpClient.addHeader("Authorization", basicAuth);
            httpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
            PersistentCookieStore myCookieStore = new PersistentCookieStore(addChild);
            httpClient.setCookieStore(myCookieStore);
            if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                httpClient.put(addChild, AppConstant.BASE_URL + AppConstant.UPDATE_CHILD, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("ldsfjjlk", "onSuccess 1");
                        if (gFileName != null) {
                            Child child = new GetObjectFromResponse().getChildObject(response);
                            childId = child.getId();
                            sendAvatar(child);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            showToast(firstName.getText() + " updated");
                        }
                        Utils.logDebug(TAG + " onSuccess", response.toString());

                        screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("ldsfjjlk", "onFailure throwable: " + throwable.getLocalizedMessage());
                        Log.d("ldsfjjlk", "onFailure errorResponse: " + errorResponse.toString());
                        Utils.logDebug(TAG + " onFailure", throwable.toString());
                        unLockScreen();
                        josnError(errorResponse);
                        showToast("Error: " + throwable.getLocalizedMessage());
                    }

                    @Override
                    public void onStart() {
                        Log.d("ldsfjjlk", "onStart");
                        progressBar.setVisibility(View.VISIBLE);
                        lockScreen();

                    }

                    @Override
                    public void onFinish() {
                        Log.d("ldsfjjlk", "onFinish");
                        if (gFileName == null) {
                            progressBar.setVisibility(View.GONE);
                            unLockScreen();
                        }

                    }
                });
            else
                httpClient.post(addChild, AppConstant.BASE_URL + AppConstant.ADD_CHILD_PARENT, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("ldsfjjlk", "2 onSuccess 1");
                        try {
                            childId = response.getInt("id");
                            Log.d("ldsfjjlk", "childId 1 = " + childId);
                        } catch (JSONException e) {
                            Log.d("ldsfjjlk", "childId 1  e = " + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                        if (gFileName != null){
                            Child child = new GetObjectFromResponse().getChildObject(response);
                            sendAvatar(child);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Utils.logDebug(TAG + " onSuccess", response.toString());
                            if (mode.equalsIgnoreCase(AppConstant.UPDATE))
                                showToast(firstName.getText() + " updated");
                            else
                                showToast(firstName.getText() + " added");
                            screenSwitch.moveToParentProfile(childId, parentObject, switchFrom);
                        }

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("ldsfjjlk", "2 onFailure throwable: " + throwable.getLocalizedMessage());
                        Utils.logDebug(TAG + " onFailure", throwable.toString());
                        Toast.makeText(addChild, getResources().getString(R.string.user_already_exists), Toast.LENGTH_LONG).show();
                        unLockScreen();
                        josnError(errorResponse);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onStart() {
                        Log.d("ldsfjjlk", "2 onStart");
                        progressBar.setVisibility(View.VISIBLE);
                        lockScreen();

                    }

                    @Override
                    public void onFinish() {
                        Log.d("ldsfjjlk", "2 onFinish");
                        if (gFileName == null){
                            progressBar.setVisibility(View.GONE);
                        }
                        unLockScreen();

                    }
                });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void sendAvatar(final Child child1) {
        Log.d("ldsfjjlk", "sendAvatar");
        if (gFileName == null) {
            Log.d("ldsfjjlk", "gFileName == null");
            return;
        }
        Log.d("ldsfjjlk", "gFileName != null");
        File file = Utils.compressImage(this, new File(gFileName));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        RetroInterface retroInterface = RetrofitClient.getApiServices(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());
        Call<String> call = retroInterface.uploadChildrenProfilePictureByParent(childId, filePart);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    if (mode.equalsIgnoreCase(AppConstant.UPDATE)) {
                        showToast(firstName.getText() + " updated");
                    }
                    else {
                        showToast(firstName.getText() + " added");
                    }
                    Child ch = child1 == null ? child : child1;
                    Log.d("ldsfjjlk", "child: " + ch);
                    updateChildProfile(ch, response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(addChild, "Child is added. But We have trouble with loading " +
                        "photo. Reason: "+ t.getLocalizedMessage() +
                        ".\nYou can add photo later", Toast.LENGTH_LONG).show();
                Log.d("ldsfjjlk", "Throwable: " + t.getLocalizedMessage());
            }
        });
    }

    public class ProfileAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(AppConstant.ACCESS_KEY_KEY, AppConstant.SECRET_ACCESS_KEY));
                s3.setRegion(Region.getRegion(Regions.US_WEST_2));

                File filePath = new File(params[0]);
                File compressedImageFile = new Compressor(addChild).compressToFile(filePath);
                String fileName = AppConstant.CHILD_IMAGE_FOLDER + AppConstant.SUFFIX + String.valueOf(new SimpleDateFormat(AppConstant.IMAGE_DATE_FORMAT).format(new Date()));
                s3.putObject(new PutObjectRequest(AppConstant.BUCKET_NAME, fileName, compressedImageFile).withCannedAcl(CannedAccessControlList.PublicRead));
                String profileUrl = String.valueOf(s3.getResourceUrl(AppConstant.BUCKET_NAME, fileName));
                Utils.logDebug(TAG + "return image url", profileUrl);
                return profileUrl;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String userImage) {
            gFileName = null;
            addChild(userImage);
        }

        @Override
        protected void onPreExecute() {

        }

    }


}