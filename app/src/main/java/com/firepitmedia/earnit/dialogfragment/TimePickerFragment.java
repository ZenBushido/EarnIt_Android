package com.firepitmedia.earnit.dialogfragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firepitmedia.earnit.R;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private int mNumberPickerInputId = 0;

    private TextView startTimer;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(getActivity(), R.style.TimePickerTheme
                , this, hour, minute, false);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setMinimumWidth(0);
        linearLayout.setMinimumHeight(0);
        tpd.setCustomTitle(linearLayout);
        return tpd;
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String aMpM = "AM";
        if (hourOfDay > 11) {
            aMpM = "PM";
        }

        int currentHour;
        if (hourOfDay > 11) {

            currentHour = hourOfDay - 12;

            if (currentHour == 0)
                currentHour = 12;


        } else {
            currentHour = hourOfDay;
            if (currentHour == 0)
                currentHour = 12;
        }


        startTimer.setText(String.valueOf(currentHour)
                + ":" + String.valueOf(minute) + ":" + "00" + " " + aMpM);

    }

    public void setTextView(TextView startTimer){
        this.startTimer = startTimer;
    }

}