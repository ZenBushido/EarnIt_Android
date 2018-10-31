package com.firepitmedia.earnit.dialogfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.activity.UploadRuntimePermission;


public class UploadTaskImageFragment extends UploadRuntimePermission {

    private final String LOG_TAG = UploadTaskImageFragment.class.getSimpleName();
    TextView allowance;
    TextView requred;
    TextView photo;
    TextView repeats;
    TextView comment;
    ImageView photolink;
    Button buttonPos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState );
        setContentView(R.layout.upload_task_image);

        allowance = (TextView) findViewById(R.id.allowance);
        requred = (TextView) findViewById(R.id.requred);
        photo = (TextView) findViewById(R.id.photo_yes);
        repeats = (TextView) findViewById(R.id.repeats);
        comment = (TextView) findViewById(R.id.comment_box);
        photolink = (ImageView) findViewById(R.id.user_image);
        buttonPos = (Button) findViewById(R.id.repeatdaily_ok);

        photolink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vRuntimePermission(photolink);
                selectImage();
            }
        });
        buttonPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // If shown as dialog, set the width of the dialog window
    // onCreateView --> onActivityCreated -->  onViewStateRestored --> onStart --> onResume
    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
    }


}
