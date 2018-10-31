package com.firepitmedia.earnit.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.adapter.ChildViewDateAdapter;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.ChildsTaskObject;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.FloatingMenu;
import com.firepitmedia.earnit.utils.GetObjectFromResponse;
import com.firepitmedia.earnit.utils.NavigationDrawer;
import com.firepitmedia.earnit.utils.RestCall;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.firepitmedia.earnit.utils.Utils;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.extras.Base64;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by mradul on 7/18/17.
 */

public class ParentCheckInChildDashboard extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.user_image) CircularImageView tChildImage;
    @BindView(R.id.textView_child) TextView tHeaderName;
    @BindView(R.id.add_task) TextView addTask;
    @BindView(R.id.approval_label) TextView readyLabel;
    @BindView(R.id.toolbar_child) Toolbar parentCheckinToolbar;
    public Child childObject;
    Child otherChild;
    Parent parentObject;
    RecyclerView parentCheckinRecycler;
    ChildViewDateAdapter parentCheckInTaskAdapter;
    ParentCheckInChildDashboard parentCheckInChildDashboard;
    Button notifyChildButton;
    @BindView(R.id.calendarBtn_child) ImageButton drawerToggle;
    @BindView(R.id.loadingPanel) RelativeLayout progressBar;
    Handler handler;
    Runnable runnable;
    String onScreen, fromScreen;
    @BindView(R.id.show_all_block) LinearLayout showAllBlock;
    @BindView(R.id.add_task_block) LinearLayout addTaskBlock;
    TextView addTaskTextview, showAllTextView;
    boolean alreadyClick = false;
    private final String TAG = "ParentCheckInDashboard";
    ScreenSwitch screenSwitch;
    @BindView(R.id.ivBackArrow) ImageButton ivBackArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_checkin_child_layout);
        ButterKnife.bind(this);
        setSupportActionBar(parentCheckinToolbar);
        getSupportActionBar().setTitle(null);

        parentCheckInChildDashboard = this;
        screenSwitch = new ScreenSwitch(parentCheckInChildDashboard);
        drawerToggle.setImageResource(R.drawable.ic_menu_pink_36dp);

        ivBackArrow.setOnClickListener(this);
        //SERIALIZE OBJECT FROM INTENT OBJECT
        Intent intent = getIntent();
        onScreen = intent.getStringExtra(AppConstant.SCREEN);
        fromScreen = intent.getStringExtra(AppConstant.FROM_SCREEN);
        Utils.logDebug(TAG, " onscreen :>" + onScreen + " fromScreen :>" + fromScreen);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        //SET PROFILE IMAGE

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(350,350);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.placeholder(R.drawable.default_avatar);
        requestOptions.error(R.drawable.default_avatar);

        Log.e(TAG, AppConstant.AMAZON_URL+childObject.getAvatar());

        updateAvatar(childObject, tChildImage);

        tHeaderName.setText(childObject.getFirstName().substring(0, 1).toUpperCase() + childObject.getFirstName().substring(1) + "'s Tasks");


        addTaskTextview = (TextView) findViewById(R.id.add_task);
        showAllTextView = (TextView) findViewById(R.id.show_all);
        if (onScreen != null)
            if (onScreen.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN))
                addTaskBlock.setVisibility(View.VISIBLE);
            else
                showAllBlock.setVisibility(View.VISIBLE);

        notifyChildButton = (Button) findViewById(R.id.message_to_child);
        parentCheckinRecycler = (RecyclerView) findViewById(R.id.parent_check_in_child_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        parentCheckinRecycler.setLayoutManager(mLayoutManager);
        parentCheckinRecycler.setItemAnimator(new DefaultItemAnimator());
        List<ChildsTaskObject> childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(otherChild);

        parentCheckInTaskAdapter = new ChildViewDateAdapter(childTaskObjects, parentObject, childObject, "Parent");
        // else
        //   parentCheckInTaskAdapter = new ChildViewDateAdapter(progressBar, parentCheckInChildDashboard, removeDeclineTask, childObject, parentObject, AppConstant.PARENT, onScreen, false);

        parentCheckinRecycler.setAdapter(parentCheckInTaskAdapter);
        notifyChildButton.setText("Send a message to " + childObject.getFirstName());
        notifyChildButton.setOnClickListener(parentCheckInChildDashboard);
        addTaskTextview.setOnClickListener(parentCheckInChildDashboard);
        showAllTextView.setOnClickListener(parentCheckInChildDashboard);
        tChildImage.setOnClickListener(parentCheckInChildDashboard);

        new NavigationDrawer(parentCheckInChildDashboard, parentObject, parentCheckinToolbar, drawerToggle, onScreen, childObject.getId());
        callApi();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        parentCheckInTaskAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        parentCheckInTaskAdapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        screenSwitch.moveToParentDashboard(parentObject);
    }

    public void callApi() {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                try {
                    new RestCall(parentCheckInChildDashboard).fetchUpdatedChild(parentObject, childObject.getEmail(), progressBar, onScreen);
                } catch (Exception e) {
                }
            }
        };
        handler.postDelayed(runnable, AppConstant.REFRESH_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_to_child:
                notifyChildByMessage(v);
                break;
            case R.id.add_task:
                screenSwitch.moveToAddTask(childObject, otherChild, parentObject, onScreen, null);

                break;
//            case R.id.show_all:
//                if (!alreadyClick) {
//                    alreadyClick = true;
//                    List<ChildsTaskObject> childsTaskObjects = removeDeclineTask();
//                    parentCheckInTaskAdapter = new ChildViewDateAdapter(childsTaskObjects, parentObject, childObject, "Parent");
//
//                    parentCheckinRecycler.setAdapter(parentCheckInTaskAdapter);
//
//                    //  }else{
//                    //    showToast(getResources().getString(R.string.no_more_task));
//
//                }
//                break;
            case R.id.show_all:
                screenSwitch.moveToAddTask(childObject, otherChild, parentObject, AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN, null);
                break;

            case R.id.user_image:
                Log.d("jdsahdkjh", "Call AppUsage. Child: " + childObject.toString());
                new FloatingMenu(parentCheckInChildDashboard).fetchAvatarDimension(tChildImage, childObject, otherChild, parentObject, onScreen, progressBar, null);
                break;

            case R.id.ivBackArrow:
//                screenSwitch.moveToParentDashboard(parentObject);
//                finish();
                onBackPressed();
                break;
        }
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

    public void notifyChildByMessage(final View view) {
        final Dialog dialog = new Dialog(view.getRootView().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.9f;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.message_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView messageTitle = (TextView) dialog.findViewById(R.id.message_title_id);
        messageTitle.setText("Message to " + childObject.getFirstName() + ":");
        final EditText messageBox = (EditText) dialog.findViewById(R.id.message_box);
        Button declineButton = (Button) dialog.findViewById(R.id.message_cancel);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        Button acceptButton = (Button) dialog.findViewById(R.id.message_send);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageBox.getText().toString().trim().length() > 0) {
                    screenSwitch.sendMessageToChild(messageBox.getText().toString(), childObject);
                    dialog.dismiss();
                } else
                    showToast("Please enter message first");
            }
        });
        dialog.show();
    }

    public List<ChildsTaskObject> removeDeclineTask() {
        List<ChildsTaskObject> fetchTaskList = new GetObjectFromResponse().getChildTaskListObject(childObject);
        int approvalTaskCount = 0;
        for (int i = 0; i < fetchTaskList.size(); i++) {
            ChildsTaskObject taskObject = fetchTaskList.get(i);
            List<Tasks> tasksList = taskObject.getTasks();
            for (int j = 0; j < tasksList.size(); j++) {
                Tasks task = tasksList.get(j);
                if (task.getStatus().equalsIgnoreCase(AppConstant.DECLINED)) {
                    tasksList.remove(j);
                } else if (task.getStatus().equalsIgnoreCase(AppConstant.COMPLETED))
                    approvalTaskCount++;
            }
            if (tasksList.size() == 0) {
                fetchTaskList.remove(i);
            }
        }
        if (approvalTaskCount > 0)
            readyLabel.setText(getResources().getString(R.string.ready_approval));
        return fetchTaskList;
    }
}
