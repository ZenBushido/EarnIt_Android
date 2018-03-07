package com.mobiledi.earnit.dialogfragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiledi.earnit.R;

import java.util.List;
import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class MyDialogFragment extends DialogFragment {

    private final String LOG_TAG = MyDialogFragment.class.getSimpleName();
    TextView repeat_daily_text;
    EditText textEdit;
    // onCreate --> (onCreateDialog) --> onCreateView --> onActivityCreated
DailyDialogListener dailyDialogListener;
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        dailyDialogListener = (DailyDialogListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");

        View dialogView = inflater.inflate(R.layout.repeat_daily, container, false);

        repeat_daily_text= (TextView) dialogView.findViewById(R.id.repeat_daily_frequency);
        repeat_daily_text.setText("Daily");
textEdit = (EditText) dialogView.findViewById(R.id.repeat_daily_checkbox);
        final EditText repeat_daily_check = (EditText) dialogView.findViewById(R.id.repeat_daily_checkbox);
        repeat_daily_check.setText("");
        // "Got it" button

        Button buttonPos = (Button) dialogView.findViewById(R.id.repeatdaily_ok);
        buttonPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(repeat_daily_check.getText().toString(), "00") || Objects.equals(repeat_daily_check.getText().toString(), "0") ||Objects.equals(repeat_daily_check.getText().toString(), ""))
                    Toast.makeText(getActivity(),"Insert number greater than 0",Toast.LENGTH_LONG).show();
                else
                {  dailyDialogListener.updateResult(textEdit.getText().toString(),repeat_daily_check.getText().toString());

                dismiss();}
            }
        });

        // "Cancel" button
        Button buttonNeg = (Button) dialogView.findViewById(R.id.repeatdaily_cancel);
        buttonNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If shown as dialog, cancel the dialog (cancel --> dismiss)
                if (getShowsDialog())
                    getDialog().cancel();
                // If shown as Fragment, dismiss the DialogFragment (remove it from view)
                else
                    dismiss();
            }
        });

        return dialogView;
    }

    // If shown as dialog, set the width of the dialog window
    // onCreateView --> onActivityCreated -->  onViewStateRestored --> onStart --> onResume
    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
        if (getShowsDialog()) {
            // Set the width of the dialog to the width of the screen in portrait mode
            DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
            int dialogWidth = Math.min(metrics.widthPixels, metrics.heightPixels);
            getDialog().getWindow().setLayout(dialogWidth, WRAP_CONTENT);
        }
    }

    private void showToast(String buttonName) {
    }

    // If dialog is cancelled: onCancel --> onDismiss
    @Override
    public void onCancel(DialogInterface dialog) {
        Log.v(LOG_TAG, "onCancel");
    }

    // If dialog is cancelled: onCancel --> onDismiss
    // If dialog is dismissed: onDismiss
    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.v(LOG_TAG,"onDismiss");
    }

    public void show(FragmentManager fm, String string) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*@Override
    protected float getDownScaleFactor() {

        return 5.0f;
    }

    @Override
    protected int getBlurRadius() {
        // Allow to customize the blur radius factor.
        return 5;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return true;
    }

    @Override
    protected boolean isDimmingEnable() {
        return true;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return true;
    }

    @Override
    protected boolean isDebugEnable() {
        return false;
    }*/
    public interface DailyDialogListener {
        void updateResult(String inputText,String s);
    }
}
