package com.mobiledi.earnit.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.adapter.ChildViewDateAdapter;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.TaskV2Model;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.NavigationDrawer;
import com.mobiledi.earnit.utils.RestCall;
import com.mobiledi.earnit.utils.ScreenSwitch;
import com.mobiledi.earnit.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        Glide.with(this).applyDefaultRequestOptions(requestOptions)
                .load(AppConstant.AMAZON_URL+childObject.getAvatar()).into(tChildImage);
      /*  try {
            Picasso.with(getApplicationContext()).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(tChildImage);
        } catch (Exception e) {
            Picasso.with(getApplicationContext()).load(R.drawable.default_avatar).into(tChildImage);
            e.printStackTrace();
        }*/
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
        List<ChildsTaskObject> childTaskObjects = new GetObjectFromResponse().getChildTaskListObject(otherChild, AppConstant.PARENT, onScreen);

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
                new FloatingMenu(parentCheckInChildDashboard).fetchAvatarDimension(tChildImage, childObject, otherChild, parentObject, onScreen, progressBar, null);
                break;

            case R.id.ivBackArrow:
//                screenSwitch.moveToParentDashboard(parentObject);
//                finish();
                onBackPressed();
                break;
        }
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
        List<ChildsTaskObject> fetchTaskList = new GetObjectFromResponse().getChildTaskListObject(childObject, AppConstant.PARENT, onScreen);
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
