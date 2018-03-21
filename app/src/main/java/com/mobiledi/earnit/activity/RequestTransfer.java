package com.mobiledi.earnit.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.ScreenSwitch;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestTransfer extends AppCompatActivity {

    Child childObject;
    @BindView(R.id.add_task_avatar)
    CircularImageView avatar;
    ScreenSwitch screenSwitch;
    Intent intent;
    Parent parentObject;
    Child otherChild;
    Tasks tasks;
    String fromScreen, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_transfer);
        ButterKnife.bind(this);
        screenSwitch = new ScreenSwitch(this);

        intent = getIntent();
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        // listGoal = (List<Goal>) intent.getSerializableExtra(AppConstant.GOAL_OBJECT);
        tasks = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);
        fromScreen = intent.getStringExtra(AppConstant.FROM_SCREEN);
        userType = intent.getStringExtra(AppConstant.TYPE);

        try {
            Picasso.with(this).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(avatar);
        } catch (Exception e) {
            Picasso.with(this).load(R.drawable.default_avatar).into(avatar);
            e.printStackTrace();
        }

    }
}
