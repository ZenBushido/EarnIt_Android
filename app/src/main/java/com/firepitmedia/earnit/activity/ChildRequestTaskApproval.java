package com.firepitmedia.earnit.activity;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.ChildsTaskObject;
import com.firepitmedia.earnit.model.Goal;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.RepititionSchedule;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.retrofit.RetroInterface;
import com.firepitmedia.earnit.retrofit.RetrofitClient;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.FloatingMenu;
import com.firepitmedia.earnit.utils.GetObjectFromResponse;
import com.firepitmedia.earnit.utils.RestCall;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.firepitmedia.earnit.utils.Utils;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mobile-di on 23/8/17.
 */

public class ChildRequestTaskApproval extends UploadRuntimePermission implements View.OnClickListener {
    final int PERMISSIONS_REQUEST = 12;
    public Intent in = null;
    @BindView(R.id.task_name)
    TextView taskName;
    @BindView(R.id.task_description)
    TextView taskDetails;
    @BindView(R.id.upload_task_image_text)
    TextView uploadImageTextHeader;
    @BindView(R.id.task_due_date)
    TextView taskDueDate;
    @BindView(R.id.task_repeat)
    TextView repeats;
    @BindView(R.id.comment_box)
    EditText taskComments;
    @BindView(R.id.child_avatar)
    CircularImageView childAvatar;
    @BindView(R.id.back_arrow)
    ImageButton backArrow;
    @BindView(R.id.upload_task_image)
    ImageView uploadImage;
    @BindView(R.id.request_approval)
    Button submit;
    final String TAG = "ChildReqTaskApproval";
    ChildRequestTaskApproval requestTaskApproval;
    Child child;
    Tasks task;
    RepititionSchedule repititionSchedule;
    private long dueDate;
    Goal goal;
    Parent parentObject;
    @BindView(R.id.loadingPanel)
    RelativeLayout progress;

    private ArrayList<ChildsTaskObject> childTasksObjects;

    private boolean previousActivityIsCalendar;

    @BindView(R.id.tv_applies_to)
    TextView tv_applies_to;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_request_task_approval);
        ButterKnife.bind(this);
        requestTaskApproval = this;
        setViewIds();
        Intent intent = getIntent();
        child = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        task = (Tasks) intent.getSerializableExtra(AppConstant.TASK_OBJECT);
        task.setRepititionSchedule(repititionSchedule);
        goal = (Goal) intent.getSerializableExtra(AppConstant.GOAL_OBJECT);
        dueDate = intent.getLongExtra(AppConstant.DUE_DATE_STRING, 0);
        childTasksObjects = (ArrayList<ChildsTaskObject>) intent.getSerializableExtra(AppConstant.CHILD_TASKS_OBJECT);
        previousActivityIsCalendar = intent.getBooleanExtra("previousActivityIsCalendar", false);
        repititionSchedule = (RepititionSchedule) intent.getSerializableExtra(AppConstant.REPETITION_SCHEDULE);
        task.setRepititionSchedule(repititionSchedule);
        Utils.logDebug(TAG, "Task == " + task);

        updateAvatar(child, childAvatar);

        setViewData();
        requestRequiredApplicationPermission(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, R.string.msg, PERMISSIONS_REQUEST);
    }

    private void updateAvatar(Child child, ImageView imageView) {
        String url = AppConstant.BASE_URL + "/" + child.getAvatar();
        Log.d("fsdfhkj", "list updateAvatar. url = " + url);
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
                .into(imageView);
    }

    private void setViewData() {
        taskName.setText(task.getName());
        taskDetails.setText(task.getDetails());
        taskDueDate.setText(DateTimeFormat.forPattern(AppConstant.DATE_FORMAT).print(new DateTime(dueDate)));
        if (goal != null)

            tv_applies_to.setText(goal.getGoalName());

        else
            tv_applies_to.setText("NA");

        // tv_applies_to.setText(goal.getGoalName());
        // taskAmount.setText("$ " + String.valueOf(Utils.roundOff(task.getAllowance(), 2)));
        if (task.getPictureRequired()) {
            uploadImageTextHeader.setVisibility(View.VISIBLE);
            uploadImage.setVisibility(View.VISIBLE);
            //requiredPhoto.setText(AppConstant.YES);
        } else {
            uploadImage.setVisibility(View.GONE);
            //requiredPhoto.setText(AppConstant.NO);}


        }


        if (repititionSchedule != null) {
            String repeatFrequency = repititionSchedule.getRepeat();

            Log.e(TAG, "Repeat is not null");
            Log.e(TAG, "Repeat Freq= " + repeatFrequency);
            if (repeatFrequency.isEmpty()) {
                Toast.makeText(this, "Submit task for Approval.", Toast.LENGTH_SHORT).show();
            } else {
                repeats.setText(repeatFrequency.substring(0, 1).toUpperCase() + repeatFrequency.substring(1));
            }

        } else {
            Log.e(TAG, "Repetition is null");
            repeats.setText(AppConstant.NO);
        }
    }

    public void setViewIds() {

        submit.setOnClickListener(requestTaskApproval);
        backArrow.setOnClickListener(requestTaskApproval);
        uploadImage.setOnClickListener(requestTaskApproval);

        taskDetails.setMovementMethod(new ScrollingMovementMethod());

        taskComments.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskComments.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
//                new RestCall(requestTaskApproval).authenticateUser(child.getEmail(), child.getPassword(), null, AppConstant.CHILD_DASHBOARD_SCREEN, progress);
                onBackPressed();
                break;

            case R.id.request_approval:
                Log.d(TAG, "click send");
                if (task.getPictureRequired()) {
                    Log.d(TAG, "getPictureRequired = true");
                    if (gFileName != null) {
                        Log.d(TAG, "gFileName != null");
                        progress.setVisibility(View.VISIBLE);
                        Log.d(TAG, "fileName = " + gFileName);
                        //new ProfileAsyncTask().execute(gFileName);
                        uploadPicture(task.getId());
                    } else {
                        Log.d(TAG, "gFileName !== null");
                        showToast(getResources().getString(R.string.please_upload_picture));
                    }
                } else {
                    Log.d(TAG, "getPictureRequired = false");
                    updateTaskStatus(task, null);
                }
                break;
            case R.id.upload_task_image:
                vRuntimePermission(uploadImage);
                if (progress.getVisibility() == View.GONE)
                    selectImage();
                break;
        }

    }

    private void uploadPicture(int taskId){
        Log.d("ldkfjglkj", "uploadPicture(). id: " + task.getId());
        File file = Utils.compressImage(this, new File(gFileName));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        RetroInterface retroInterface = RetrofitClient.getApiServices(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());
        Call<ResponseBody> call = retroInterface.uploadTaskPicture(taskId, filePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    UrlOfImage = response.body().string();
                    Log.d(TAG, "uploadPicture response: " + UrlOfImage);
                    updateTaskStatus(task, UrlOfImage);
                } catch (Exception e) {
                    Log.d(TAG, "uploadPicture response Exception: " + e.getLocalizedMessage());
                }
//                Log.d("ldkfjglkj", "uploadPicture response: " + response.body());
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("ldkfjglkj", "T1hrowable: " + t.getLocalizedMessage());
            }
        });
    }

    @OnClick(R.id.child_avatar)
    void floatingMenu() {
        new FloatingMenu(requestTaskApproval).fetchAvatarDimension252(childTasksObjects, childAvatar, child, parentObject, AppConstant.CHILD_DASHBOARD_SCREEN, progress, previousActivityIsCalendar, task);
    }

    @Override
    public void onBackPressed() {
//        new RestCall(requestTaskApproval).authenticateUser(child.getEmail(), child.getPassword(), null, AppConstant.CHILD_DASHBOARD_SCREEN, progress);
        super.onBackPressed();
    }

    private boolean isLastTask(Tasks task) {
        if (task.getRepititionSchedule() != null && task.getRepititionSchedule().getSpecificDays() != null
                && !task.getRepititionSchedule().getSpecificDays().isEmpty()) {
            int i = -1;
            try {
                i = Integer.parseInt(task.getRepititionSchedule().getSpecificDays().get(task.getRepititionSchedule().getSpecificDays().size() - 1));
            } catch (NumberFormatException ignored) {
            }
            if (i == new DateTime(dueDate).getDayOfMonth()) {
                return true;
            }
        }
        return false;
    }

    private void updateTaskStatus(Tasks selectedTask, String uploadedPicture) {
        Utils.logDebug(TAG, "updateTaskStatus() Task == " + selectedTask.toString());
        Utils.logDebug(TAG, "updateTaskStatus() uploadedPicture == " + uploadedPicture);
        progress.setVisibility(View.GONE);
        JSONObject taskJson = new JSONObject();
        boolean isLastTask = isLastTask(selectedTask);
        try {

            DateTime due = new DateTime();
            DateTimeZone tz = DateTimeZone.getDefault();
            Long instant = DateTime.now().getMillis();
            long offsetInMilliseconds = tz.getOffset(instant);

            taskJson.put(AppConstant.CHILDREN, new JSONObject().put(AppConstant.ID, selectedTask.getChildId()));
            taskJson.put(AppConstant.ID, selectedTask.getId());
            taskJson.put(AppConstant.NAME, selectedTask.getName());
            taskJson.put(AppConstant.DUE_DATE, selectedTask.getRepititionSchedule() == null ? selectedTask.getDueDate() + offsetInMilliseconds : selectedTask.getStartDate() + offsetInMilliseconds);
            taskJson.put(AppConstant.CREATE_DATE, selectedTask.getCreateDate() + offsetInMilliseconds);
            taskJson.put(AppConstant.DESCRIPTION, selectedTask.getDetails());
            if (selectedTask.getRepititionSchedule() == null /*|| isLastTask*/) {
                taskJson.put(AppConstant.STATUS, AppConstant.COMPLETED);
                Utils.logDebug(TAG, "1 getRepititionSchedule() == " + selectedTask.getRepititionSchedule() + ".  isLastTask = " + isLastTask);
            } else {
                Utils.logDebug(TAG, "2 getRepititionSchedule() == " + selectedTask.getRepititionSchedule() + ".  isLastTask = " + isLastTask);
            }
            taskJson.put(AppConstant.UPDATE_DATE, new DateTime().getMillis() + offsetInMilliseconds);
            taskJson.put(AppConstant.ALLOWANCE, selectedTask.getAllowance());

            if (selectedTask.getGoal() == null)
                Utils.logDebug(TAG, "Goal is not available");
            else {
                taskJson.put(AppConstant.GOAL, new JSONObject().put(AppConstant.ID, selectedTask.getGoal().getId()));
                Utils.logDebug(TAG, "Goal is available");
                Utils.logDebug(TAG, "Goal ID= " + selectedTask.getGoal().getId());
                Utils.logDebug(TAG, "Goal ID= " + selectedTask.getGoal().getGoalName());
            }


            if (selectedTask.getRepititionSchedule() == null/* || isLastTask*/)
                Utils.logDebug(TAG, "repeat is none || ISlASTtASK");
            else {
                Utils.logDebug(TAG, "repeat != N");
                JSONObject repeatSchedule = new JSONObject();
                repeatSchedule.put(AppConstant.ID, selectedTask.getRepititionSchedule().getId());
                repeatSchedule.put(AppConstant.REPEAT, selectedTask.getRepititionSchedule().getRepeat());
                JSONArray dayTaskStatuses = new JSONArray();
                JSONObject dayTaskStatus = new JSONObject();
                Utils.logDebug(TAG, "task.getDueDate() = " + new DateTime(task.getDueDate()));
                Utils.logDebug(TAG, "task.getDueDate() + offsetInMilliseconds = " + new DateTime(task.getDueDate() + offsetInMilliseconds));
                dayTaskStatus.put("createdDateTime", new DateTime(task.getDueDate() + offsetInMilliseconds).toString("MMM dd, yyyy hh:mm:ss a", Locale.US));
                dayTaskStatus.put("status", AppConstant.COMPLETED);
                Utils.logDebug(TAG, "!@dayTaskStatus = " + dayTaskStatus.toString());
                dayTaskStatuses.put(dayTaskStatus);
                repeatSchedule.put("dayTaskStatuses", dayTaskStatuses);
                taskJson.put(AppConstant.REPITITION_SCHEDULE, repeatSchedule);
            }

            if (selectedTask.getPictureRequired())
                taskJson.put(AppConstant.PICTURE_REQUIRED, selectedTask.getPictureRequired());
            else {
                taskJson.put(AppConstant.PICTURE_REQUIRED, 0);
                Utils.logDebug(TAG, "picture required not checked");
            }
            JSONArray taskCommentArray = new JSONArray();
            JSONObject taskCommentObject = new JSONObject();
            taskCommentObject.put(AppConstant.COMMENT, taskComments.getText());
            taskCommentObject.put(AppConstant.CREATE_DATE, new DateTime().getMillis() + offsetInMilliseconds);
            taskCommentObject.put(AppConstant.UPDATE_DATE, new DateTime().getMillis() + offsetInMilliseconds);
            taskCommentObject.put(AppConstant.READ_STATUS, 0);
            taskCommentObject.put(AppConstant.PICTURE_URL, uploadedPicture);
            taskCommentArray.put(taskCommentObject);
            taskJson.put(AppConstant.TASK_COMMENTS, taskCommentArray);

            StringEntity entity = new StringEntity(taskJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
            Utils.logDebug(TAG, "child-update-task: " + taskJson.toString() + "\nCredentials: " + namePassword);
            final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setBasicAuth(child.getEmail(), child.getPassword());
            httpClient.addHeader("Authorization", basicAuth);
            String url = AppConstant.BASE_URL + AppConstant.TASKS_API;

            httpClient.put(requestTaskApproval, url, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Utils.logDebug(TAG, " onSuccess : " + response.toString());
                    new RestCall(requestTaskApproval)
                            .authenticateUser(MyApplication.getInstance().getEmail(),
                                    MyApplication.getInstance().getPassword(),
                                    null,
                                    AppConstant.CHILD_DASHBOARD_SCREEN, progress);


                    Log.d("jdsahdkjh", "click allTask ");

                    progress.setVisibility(View.VISIBLE);
                    final AsyncHttpClient client = new AsyncHttpClient();
                    String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
                    final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
                    client.addHeader("Authorization", basicAuth);
                    client.setBasicAuth(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());
                    Log.e(TAG, "URL = " + AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + child.getId());
                    client.get(AppConstant.BASE_URL + AppConstant.TASKS_API + "/" + child.getId(), null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            Log.e(TAG, "resp d = " + response.toString());


                            ArrayList<Tasks> taskList = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    //TASKS
                                    Tasks task = new GetObjectFromResponse().getTaskObject(object, child.getId());
                                    taskList.add(task);


                                } catch (Exception e) {

                                    Log.e(TAG, "Error: " + e.getLocalizedMessage());

                                }
                                child.setTasksArrayList(taskList);

                            }

                            List<String> listStatus = new ArrayList<>();

                            for (int j = 0; j < taskList.size(); j++) {
                                if (taskList.get(j).getStatus().equalsIgnoreCase("Created"))
                                    listStatus.add(taskList.get(j).getStatus());

                            }


                            Log.e(TAG, "Task List size= " + taskList.size());
                            if (listStatus.size() != 0) {
                                progress.setVisibility(View.GONE);
                                new ScreenSwitch(ChildRequestTaskApproval.this).moveTOChildDashboard(child, false);
                            } else {
                                progress.setVisibility(View.GONE);
                                Utils.showToast(ChildRequestTaskApproval.this, getResources().getString(R.string.no_task_schedule));
                            }


                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Utils.logDebug(TAG, " onFailure errorResponse: " + errorResponse);
                    Utils.logDebug(TAG, " onFailure throwable: " + throwable);
                    unLockScreen();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Utils.logDebug(TAG, " onFailure errorResponse: " + errorResponse);
                    Utils.logDebug(TAG, " onFailure throwable: " + throwable);
                    unLockScreen();
                }

                @Override
                public void onStart() {
                    progress.setVisibility(View.VISIBLE);
                    lockScreen();
                }

                @Override
                public void onFinish() {
                    progress.setVisibility(View.GONE);
                    unLockScreen();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ProfileAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(AppConstant.ACCESS_KEY_KEY, AppConstant.SECRET_ACCESS_KEY));
                s3.setRegion(Region.getRegion(Regions.US_WEST_2));

                Bucket bucket;
                final String bucketName = "earnitapp-dev/new/";
                bucket = s3.createBucket(new CreateBucketRequest(bucketName));

                File filePath = new File(params[0]);
                File compressedImageFile = new Compressor(requestTaskApproval).compressToFile(filePath);
                String fileName = AppConstant.TASK_IMAGE_FOLDER + AppConstant.SUFFIX + String.valueOf(new SimpleDateFormat(AppConstant.IMAGE_DATE_FORMAT).format(new Date()));
                Log.d("ChildReqTaskApproval", "1");
                s3.putObject(new PutObjectRequest(AppConstant.BUCKET_NAME, fileName, compressedImageFile).withCannedAcl(CannedAccessControlList.PublicRead));
                Log.d("ChildReqTaskApproval", "2");
                String profileUrl = String.valueOf(s3.getResourceUrl(AppConstant.BUCKET_NAME, fileName));
                Log.d("ChildReqTaskApproval", "3");
                return profileUrl;

            } catch (Exception e) {
                Log.d("ChildReqTaskApproval", "Error: " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String uploadedPicture) {
            Log.d("ChildReqTaskApproval", "onPostExecute(): " + uploadedPicture);
            updateTaskStatus(task, uploadedPicture);
        }

        @Override
        protected void onPreExecute() {
            Log.d("ChildReqTaskApproval", "onPreExecute()");
        }

    }

}
