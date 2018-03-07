package com.mobiledi.earnit.dialogfragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiledi.earnit.R;
import com.mobiledi.earnit.activity.UploadRuntimePermission;

import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


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
