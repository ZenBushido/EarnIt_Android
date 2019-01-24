package com.firepitmedia.earnit.dialogfragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firepitmedia.earnit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class WeeklyDialogFragment extends DialogFragment {

   public static List<String> weekList = new ArrayList<>();
WeeklyDialogListener weeklyDialogListener;


    Button daybutton1;
    Button daybutton2;
    Button daybutton3;
    Button daybutton4;
    Button daybutton5;
    Button daybutton6;
    Button daybutton7;

    private final String LOG_TAG = MyDialogFragment.class.getSimpleName();
    Boolean monButton = false;
    Boolean tueButton = false;
    Boolean wenButton = false;
    Boolean thuButton = false;
    Boolean friButton = false;
    Boolean satButton = false;
    Boolean sanButton = false;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        weeklyDialogListener = (WeeklyDialogListener) context;
    }

    // onCreate --> (onCreateDialog) --> onCreateView --> onActivityCreated
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
weekList= new ArrayList<>();
        View dialogView = inflater.inflate(R.layout.repeat_weekly, container, false);



        final EditText repeat_daily_check = (EditText) dialogView.findViewById(R.id.repeat_weekly_checkbox);
        repeat_daily_check.setText("");
        // "Got it" button

        daybutton1 = (Button) dialogView.findViewById(R.id.repeat_weekly_san);
        daybutton1.setText("SUN");
        daybutton2 = (Button) dialogView.findViewById(R.id.repeat_weekly_mon);
        daybutton2.setText("MON");
        daybutton3 = (Button) dialogView.findViewById(R.id.repeat_weekly_tue);
        daybutton3.setText("TUE");
        daybutton4 = (Button) dialogView.findViewById(R.id.repeat_weekly_wen);
        daybutton4.setText("WED");
        daybutton5 = (Button) dialogView.findViewById(R.id.repeat_weekly_thu);
        daybutton5.setText("THU");
        daybutton6 = (Button) dialogView.findViewById(R.id.repeat_weekly_fri);
        daybutton6.setText("FRI");
        daybutton7 = (Button) dialogView.findViewById(R.id.repeat_weekly_sat);
        daybutton7.setText("SAT");

        daybutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sanButton) {
                    for (int i = 0; i < weekList.size(); i++) {

                        if (weekList.get(i).equalsIgnoreCase("Sunday")) {

                            weekList.remove(i);
                        }
                    }

                    daybutton1.setBackgroundResource(R.drawable.repeatweekly_trans);
                    sanButton = false;
                } else {
                    weekList.add("Sunday");
                    daybutton1.setBackgroundResource(R.drawable.repeatweekly_pink);
                    sanButton = true;
                }
            }
        });

        daybutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monButton) {
                    for (int i = 0; i < weekList.size(); i++) {

                        if (weekList.get(i).equalsIgnoreCase("Monday")) {

                            weekList.remove(i);
                        }
                    }
                    daybutton2.setBackgroundResource(R.drawable.repeatweekly_trans);
                    monButton = false;
                } else {
                    weekList.add("Monday");
                    daybutton2.setBackgroundResource(R.drawable.repeatweekly_pink);
                    monButton = true;
                }
            }
        });

        daybutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tueButton) {
                    for (int i = 0; i < weekList.size(); i++) {

                        if (weekList.get(i).equalsIgnoreCase("Tuesday")) {

                            weekList.remove(i);
                        }
                    }
                    daybutton3.setBackgroundResource(R.drawable.repeatweekly_trans);
                    tueButton = false;
                } else {
                    weekList.add("Tuesday");

                    daybutton3.setBackgroundResource(R.drawable.repeatweekly_pink);
                    tueButton = true;
                }
            }
        });

        daybutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wenButton) {
                    for (int i = 0; i < weekList.size(); i++) {

                        if (weekList.get(i).equalsIgnoreCase("Wednesday")) {

                            weekList.remove(i);
                        }
                    }
                    daybutton4.setBackgroundResource(R.drawable.repeatweekly_trans);
                    wenButton = false;
                } else {
                    weekList.add("Wednesday");
                    daybutton4.setBackgroundResource(R.drawable.repeatweekly_pink);
                    wenButton = true;
                }
            }
        });

        daybutton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thuButton) {
                    for (int i = 0; i < weekList.size(); i++) {

                        if (weekList.get(i).equalsIgnoreCase("Thursday")) {

                            weekList.remove(i);
                        }
                    }
                    daybutton5.setBackgroundResource(R.drawable.repeatweekly_trans);
                    thuButton = false;
                } else {
                    weekList.add("Thursday");
                    daybutton5.setBackgroundResource(R.drawable.repeatweekly_pink);
                    thuButton = true;
                }
            }
        });

        daybutton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friButton) {
                    for (int i = 0; i < weekList.size(); i++) {

                        if (weekList.get(i).equalsIgnoreCase("Friday")) {

                            weekList.remove(i);
                        }
                    }
                    daybutton6.setBackgroundResource(R.drawable.repeatweekly_trans);
                    friButton = false;
                } else {
                    weekList.add("Friday");
                    daybutton6.setBackgroundResource(R.drawable.repeatweekly_pink);
                    friButton = true;
                }
            }
        });

        daybutton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (satButton) {
                    for (int i = 0; i < weekList.size(); i++) {

                        if (weekList.get(i).equalsIgnoreCase("Saturday")) {

                            weekList.remove(i);
                        }
                    }
                    daybutton7.setBackgroundResource(R.drawable.repeatweekly_trans);
                    satButton = false;
                } else {
                    weekList.add("Saturday");
                    daybutton7.setBackgroundResource(R.drawable.repeatweekly_pink);
                    satButton = true;
                }
            }
        });


        // "Got it" button
        Button buttonPos = (Button) dialogView.findViewById(R.id.repeatweekly_ok);
        buttonPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(repeat_daily_check.getText().toString(), "00") || Objects.equals(repeat_daily_check.getText().toString(), "0") ||Objects.equals(repeat_daily_check.getText().toString(), "")|| weekList.size()<1)
                    Toast.makeText(getActivity(),"Insert correct data",Toast.LENGTH_LONG).show();
                else
                {  weeklyDialogListener.updateResult(weekList,repeat_daily_check.getText().toString());
                    dismiss();}
            }
        });


        // "Cancel" button
        Button buttonNeg = (Button) dialogView.findViewById(R.id.repeatweekly_cancel);
        buttonNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showToast("Pressed Cancel Button!");
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
        Toast.makeText(getActivity(), "Clicked on \"" + buttonName + "\"", Toast.LENGTH_SHORT).show();
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
        Log.v(LOG_TAG, "onDismiss");
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
    public interface WeeklyDialogListener {
        void updateResult(List<String> inputText, String s);
    }

}
