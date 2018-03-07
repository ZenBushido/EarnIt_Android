package com.mobiledi.earnit.activity.usageStats;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.NavigationDrawer;

import butterknife.BindView;
import butterknife.ButterKnife;



public class AppUsageStatisticsActivity extends AppCompatActivity {

    @BindView(R.id.drawerBtn)
    ImageButton drawerToggle;
    @BindView(R.id.toolbar_add)
    Toolbar goalToolbar;
    @BindView(R.id.addtask_back_arrow)
    ImageButton backbtn;
    @BindView(R.id.add_task_header)
    TextView add_task_header;

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
        Parent parent = (Parent)intent.getSerializableExtra(AppConstant.PARENT_OBJECT);

        backbtn.setVisibility(View.GONE);
        NavigationDrawer navigationDrawer = new NavigationDrawer(AppUsageStatisticsActivity.this, parent, goalToolbar, drawerToggle, AppConstant.PARENT_DASHBOARD, 0);
        add_task_header.setText(parent.getFirstName());
    }
}