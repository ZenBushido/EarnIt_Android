package com.mobiledi.earnit.activity.usageStats;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.NavigationDrawer;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AppUsageStatisticsActivity extends AppCompatActivity {

    @BindView(R.id.drawerBtn)
    ImageButton drawerToggle;
    @BindView(R.id.toolbar)
    Toolbar goalToolbar;
    @BindView(R.id.addtask_back_arrow)
    ImageButton backbtn;
    @BindView(R.id.add_task_header)
    TextView add_task_header;
    @BindView(R.id.add_task_avatar)
    CircularImageView childAvatar;
    Parent parent;
    Child childObject;
    @BindView(R.id.loadingPanel)
    RelativeLayout progressBar;
    String TAG = AppUsageStatisticsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_usage_statistics);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, AppUsageStatisticsFragment.newInstance())
                    .commit();
        }
        Intent intent = getIntent();
         parent = (Parent)intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
         childObject = (Child)intent.getSerializableExtra(AppConstant.CHILD_OBJECT);


        if (childObject != null) {

                Glide.with(this).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).into(childAvatar);



        }

        backbtn.setVisibility(View.GONE);
        NavigationDrawer navigationDrawer = new NavigationDrawer(AppUsageStatisticsActivity.this, parent, goalToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, 0);
        add_task_header.setText(parent.getFirstName());
    }

    @OnClick(R.id.add_task_avatar)
    void floatingMenu()
    {

        new FloatingMenu(this).fetchAvatarDimension(childAvatar, childObject, childObject, parent, AppConstant.ADD_TASK, progressBar, null);

    }
}