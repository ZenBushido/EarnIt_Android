package com.mobiledi.earnit.activity;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.RestCall;
import com.mobiledi.earnit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import id.zelory.compressor.Compressor;

/**
 * Created by mobile-di on 23/8/17.
 */

public class ChildRequestTaskApproval extends UploadRuntimePermission implements View.OnClickListener {
    final int PERMISSIONS_REQUEST = 12;
    public Intent in = null;
    @BindView(R.id.task_name) TextView taskName;
    @BindView(R.id.task_description) TextView taskDetails;
    @BindView(R.id.upload_task_image_text) TextView uploadImageTextHeader;
    @BindView(R.id.task_due_date) TextView taskDueDate;
    @BindView(R.id.task_repeat) TextView repeats;
    @BindView(R.id.comment_box) EditText taskComments;
    @BindView(R.id.child_avatar) CircularImageView childAvatar;
    @BindView(R.id.back_arrow) ImageButton backArrow;
    @BindView(R.id.upload_task_image) ImageView uploadImage;
    @BindView(R.id.request_approval) Button submit;
    final String TAG = "ChildReqTaskApproval";
    ChildRequestTaskApproval requestTaskApproval;
    Child child;
    Tasks task;
    Goal goal;
    @BindView(R.id.loadingPanel) RelativeLayout progress;

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
        goal = (Goal) intent.getSerializableExtra(AppConstant.GOAL_OBJECT);



        setViewData();
        requestRequiredApplicationPermission(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, R.string.msg, PERMISSIONS_REQUEST);
    }

    private void setViewData() {
        taskName.setText(task.getName());
        taskDetails.setText(task.getDetails());
        taskDueDate.setText(DateTimeFormat.forPattern(AppConstant.DATE_FORMAT).print(new DateTime(task.getDueDate())));
        if(goal!=null)
        {
            Log.e(TAG, "Goal is not null");
            tv_applies_to.setText(goal.getGoalName());
        }
        // tv_applies_to.setText(goal.getGoalName());
        // taskAmount.setText("$ " + String.valueOf(Utils.roundOff(task.getAllowance(), 2)));
        if (task.getPictureRequired()) {
            uploadImageTextHeader.setVisibility(View.VISIBLE);
            uploadImage.setVisibility(View.VISIBLE);
            //requiredPhoto.setText(AppConstant.YES);
        } else {
            uploadImage.setVisibility(View.GONE);
            //requiredPhoto.setText(AppConstant.NO);}

            if (task.getRepititionSchedule() != null) {
                String repeatFrequency = task.getRepititionSchedule().getRepeat();

                if (repeatFrequency.isEmpty()) {
                    Toast.makeText(this, "Submit task for Approval.", Toast.LENGTH_SHORT).show();
                } else {
                    repeats.setText(repeatFrequency.substring(0, 1).toUpperCase() + repeatFrequency.substring(1));
                }

            } else repeats.setText(AppConstant.NO);

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
                new RestCall(requestTaskApproval).authenticateUser(child.getEmail(), child.getPassword(), null, AppConstant.CHILD_DASHBOARD_SCREEN, progress);
                break;

            case R.id.request_approval:
                if (task.getPictureRequired()) {
                    if (UrlOfImage != null) {
                        progress.setVisibility(View.VISIBLE);
                        new ProfileAsyncTask().execute(gFileName);
                    } else {
                        showToast(getResources().getString(R.string.please_upload_picture));
                    }
                } else if (taskComments.getText().toString().equals(""))
                    showToast("Write  comment !");
                else {
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

    @Override
    public void onBackPressed() {
        new RestCall(requestTaskApproval).authenticateUser(child.getEmail(), child.getPassword(), null, AppConstant.CHILD_DASHBOARD_SCREEN, progress);
    }

    private void updateTaskStatus(Tasks selectedTask, String uploadedPicture) {
        progress.setVisibility(View.GONE);
        JSONObject taskJson = new JSONObject();
        try {
            taskJson.put(AppConstant.CHILDREN, new JSONObject().put(AppConstant.ID, selectedTask.getChildId()));
            taskJson.put(AppConstant.ID, selectedTask.getId());
            taskJson.put(AppConstant.NAME, selectedTask.getName());
            taskJson.put(AppConstant.DUE_DATE, selectedTask.getDueDate());
            taskJson.put(AppConstant.CREATE_DATE, selectedTask.getCreateDate());
            taskJson.put(AppConstant.DESCRIPTION, selectedTask.getDetails());
            taskJson.put(AppConstant.STATUS, AppConstant.COMPLETED);
            taskJson.put(AppConstant.UPDATE_DATE, new DateTime().getMillis());
            taskJson.put(AppConstant.ALLOWANCE, selectedTask.getAllowance());

            if (selectedTask.getGoal() == null)
                Utils.logDebug(TAG, "Goal is not available");
            else
            {
                taskJson.put(AppConstant.GOAL, new JSONObject().put(AppConstant.ID, selectedTask.getGoal().getId()));
                Utils.logDebug(TAG, "Goal is available");
                Utils.logDebug(TAG, "Goal ID= "+selectedTask.getGoal().getId());
                Utils.logDebug(TAG, "Goal ID= "+selectedTask.getGoal().getGoalName());
            }


            if (selectedTask.getRepititionSchedule() == null)
                Utils.logDebug(TAG, "repeat is none");
            else {
                JSONObject repeatSchedule = new JSONObject();
                repeatSchedule.put(AppConstant.ID, selectedTask.getRepititionSchedule().getId());
                repeatSchedule.put(AppConstant.REPEAT, selectedTask.getRepititionSchedule().getRepeat());
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
            taskCommentObject.put(AppConstant.CREATE_DATE, new DateTime().getMillis());
            taskCommentObject.put(AppConstant.UPDATE_DATE, new DateTime().getMillis());
            taskCommentObject.put(AppConstant.READ_STATUS, 0);
            taskCommentObject.put(AppConstant.PICTURE_URL, UrlOfImage);
            taskCommentArray.put(taskCommentObject);
            taskJson.put(AppConstant.TASK_COMMENTS, taskCommentArray);

            Utils.logDebug(TAG, " child-update-task : "+  taskJson.toString());
            StringEntity entity = new StringEntity(taskJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AppConstant.APPLICATION_JSON));
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setBasicAuth(child.getEmail(), child.getPassword());

            httpClient.put(requestTaskApproval, AppConstant.BASE_URL + AppConstant.TASKS_API, entity, AppConstant.APPLICATION_JSON, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Utils.logDebug(TAG, " onSuccess : "+  response.toString());
                        new RestCall(requestTaskApproval).authenticateUser(child.getEmail(), child.getPassword(),null, AppConstant.CHILD_DASHBOARD_SCREEN, progress);

                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Utils.logDebug(TAG, " onFailure : "+  errorResponse.toString());
                    unLockScreen();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Utils.logDebug(TAG, " onFailure : "+  errorResponse.toString());
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
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){e.printStackTrace();}
    }

    public class ProfileAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                AmazonS3Client s3 = new AmazonS3Client(new BasicAWSCredentials(AppConstant.ACCESS_KEY_KEY, AppConstant.SECRET_ACCESS_KEY));
                s3.setRegion(Region.getRegion(Regions.US_WEST_2));

                File filePath = new File(params[0]);
                File compressedImageFile = new Compressor(requestTaskApproval).compressToFile(filePath);
                String fileName = AppConstant.TASK_IMAGE_FOLDER + AppConstant.SUFFIX + String.valueOf(new SimpleDateFormat(AppConstant.IMAGE_DATE_FORMAT).format(new Date()));
                s3.putObject(new PutObjectRequest(AppConstant.BUCKET_NAME, fileName, compressedImageFile).withCannedAcl(CannedAccessControlList.PublicRead));
                String profileUrl = String.valueOf(s3.getResourceUrl(AppConstant.BUCKET_NAME, fileName));
                return profileUrl;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String uploadedPicture) {
            updateTaskStatus(task,uploadedPicture);
        }

        @Override
        protected void onPreExecute() {

        }

    }

}
