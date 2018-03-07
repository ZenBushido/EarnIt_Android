package com.mobiledi.earnit.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiledi.earnit.ConnectivityReceiver;
import com.mobiledi.earnit.MyApplication;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.utils.Utils;

import org.json.JSONObject;

/**
 * Created by mobile-di on 27/10/17.
 */

public class BaseActivity extends AppCompatActivity   {

    public void showToast(String message){
        Utils.showToast(BaseActivity.this, message);
    }


    public void lockScreen(){
        Utils.lockScreen(getWindow());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void unLockScreen(){
        Utils.unLockScreen(getWindow());
    }
    public void josnError(JSONObject message){
        Utils.JsonError(message, BaseActivity.this );
    }


    public  boolean isDeviceOnline( ) {

        ConnectivityManager connMgr = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isOnline = (networkInfo != null && networkInfo.isConnected());
        if(!isOnline)
            showDialogOnTaskAdded(this);
        return isOnline;
    }
    public   void showDialogOnTaskAdded(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.9f;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_box_no_internet);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
        message.setText("NO internet connection");
        Button declineButton = (Button) dialog.findViewById(R.id.close);
        declineButton.setText("Close app");
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
                recreate();
            }
        });
        Button acceptButton = (Button) dialog.findViewById(R.id.Retry);
        acceptButton.setText("Retry");
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isDeviceOnline();
            }
        });
        dialog.show();
    }

}
