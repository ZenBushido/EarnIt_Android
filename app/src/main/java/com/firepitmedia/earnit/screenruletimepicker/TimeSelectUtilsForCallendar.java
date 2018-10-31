package com.firepitmedia.earnit.screenruletimepicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firepitmedia.earnit.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.firepitmedia.earnit.screenruletimepicker.TimeUtils.getStatetime;


/**
 * Created by Okamiy on 2017/6/22.
 * Email: 542839122@qq.com
 * <p>
 * 时间选择器核心代码块
 */

public class TimeSelectUtilsForCallendar implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    private final TextView selectTime;
    private String initDateTime;
    private Context activity;
    private Calendar calendar;
    private CustomNumberPicker hourpicker;
    private CustomNumberPicker minutepicker;
    private CustomNumberPicker datepicker;
    private String[] minuteArrs;
    private String hourStr;
    private String minuteStr;
    private String dateStr;
    private Dialog dialog;
    private String[] dayArrays;
    private long currentTimeMillis;
    private RadioButton rgIn;
    private boolean goIn = true;
    private String startTime = "";
    private String endTime = "";
    private GetSubmitTime mSubTime;

    public TimeSelectUtilsForCallendar(Context activity, String initDateTime, TextView selectTime, GetSubmitTime subTime) {
        this.selectTime = selectTime;
        this.activity = activity;
        this.initDateTime = initDateTime;
        this.mSubTime = subTime;
    }

    public void initPicker() {
        Calendar calendar = Calendar.getInstance();

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        if (45 <= minutes)
            minutes = 0;

        else if (30 <= minutes)
            minutes = 3;

        else if (15 <= minutes)
            minutes = 2;

        else
            minutes = 1;

        dayArrays = new String[2];
        dayArrays[0] = "AM";
        dayArrays[1] = "PM";
        switch (dayArrays.length) {
            case 0:
                getStatetime();
                break;
            case 1:
                getStatetime(1);
                break;
        }
        currentTimeMillis = System.currentTimeMillis();
        datepicker.setOnValueChangedListener(this);
        datepicker.setDisplayedValues(dayArrays);
        datepicker.setMinValue(0);
        datepicker.setMaxValue(dayArrays.length - 1);
        datepicker.setValue(0);
        dateStr = dayArrays[0];

        hourpicker.setOnValueChangedListener(this);
        hourpicker.setMaxValue(23);
        hourpicker.setMinValue(0);
        if (minutes == 0) {
            hourpicker.setValue(hours + 1);
            hourStr = hours + 1 + "";

        } else {
            hourpicker.setValue(hours);
            hourStr = hours + "";

        }
        minuteArrs = new String[]{"00", "15", "30", "45"};
        minutepicker.setOnValueChangedListener(this);
        minutepicker.setDisplayedValues(minuteArrs);
        minutepicker.setMinValue(0);
        minutepicker.setMaxValue(minuteArrs.length - 1);
        minutepicker.setValue(minutes);
        minuteStr = minuteArrs[minutes];
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public Dialog dateTimePicKDialog() {
        View dateTimeLayout = View.inflate(activity, R.layout.screenrule_select_calendar,
                null);
        dateTimeLayout.findViewById(R.id.tv_cancel).setOnClickListener(this);
        dateTimeLayout.findViewById(R.id.tv_confirm).setOnClickListener(this);
        rgIn = (RadioButton) dateTimeLayout.findViewById(R.id.rb_go_in);
        rgIn.setOnClickListener(this);

        datepicker = (CustomNumberPicker) dateTimeLayout.findViewById(R.id.datepicker);
        hourpicker = (CustomNumberPicker) dateTimeLayout.findViewById(R.id.hourpicker);
        minutepicker = (CustomNumberPicker) dateTimeLayout.findViewById(R.id.minuteicker);
        datepicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hourpicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutepicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        datepicker.setNumberPickerDividerColor(datepicker);
        hourpicker.setNumberPickerDividerColor(hourpicker);
        minutepicker.setNumberPickerDividerColor(minutepicker);
        initPicker();
        dialog = new Dialog(activity, R.style.dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dateTimeLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = ((Activity) activity).getWindowManager().getDefaultDisplay().getHeight();
        wl.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        wl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        onDateChanged();
        return dialog;
    }

    public void onDateChanged() {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTimeMillis));
        Date date = calendar.getTime();
        date.setHours(Integer.parseInt(hourStr));
        date.setMinutes(Integer.parseInt(minuteStr));
        calendar.setTime(date);
        initDateTime =  hourStr + ":" + minuteStr;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.datepicker:
                currentTimeMillis = System.currentTimeMillis() + newVal * 24 * 3600 * 1000;
                dateStr = dayArrays[newVal];
                onDateChanged();
                break;
            case R.id.hourpicker:
                hourStr = newVal + "";
                onDateChanged();
                break;
            case R.id.minuteicker:
                minuteStr = minuteArrs[newVal];
                onDateChanged();
                break;
            default:
                break;
        }
    }
    public interface GetSubmitTime {
        void selectTime(String startDate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dialog.dismiss();
                break;
            case R.id.tv_confirm:
                selectTimes();
                dialog.dismiss();
                break;
            case R.id.rb_go_in:
                if (goIn == false) {
                    endTime = initDateTime;
                    if (startTime != null && !startTime.equals("")) {
                        setTimes(startTime);
                    }
                    goIn = true;
                }
                break;
            case R.id.rb_go_out:
                if (goIn) {
                    startTime = initDateTime;
                    if (!TimeUtils.compareNowTime(startTime)) {
                        Toast.makeText(activity, "Please select the correct time for admission", Toast.LENGTH_SHORT).show();
                    } else {
                        goIn = false;
                    }
                }
                if (endTime != null && !endTime.equals("")) {
                    setTimes(endTime);
                }
                break;
            default:
                break;
        }
    }

    private void setTimes(String startTime) {
        try {
            Date date = TimeUtils.stringToDate(startTime + ":00", "yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            String time = calendar.get(Calendar.YEAR) + "-" + formatTime(calendar.get(Calendar.MONTH) + 1) + "-" + formatTime(calendar.get(Calendar.DAY_OF_MONTH));
            Log.i("TAG", time + ",000000," + getStatetime());
            if (time.equals(getStatetime())) {
                datepicker.setValue(0);
            } else {
                datepicker.setValue(1);
            }
            if (45 <= minutes)
                minutes = 3;

            else if (30 <= minutes)
                minutes = 2;

            else if (15 <= minutes)
                minutes = 1;

            else
                minutes = 0;
            hourpicker.setValue(hours);
            minutepicker.setValue(minutes);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private String formatTime(int t) {
        return t >= 10 ? "" + t : "0" + t;//三元运算符 t>10时取 ""+t
    }

    private void selectTimes() {
            startTime = initDateTime;
            if (calendar.getTimeInMillis() <= System.currentTimeMillis() || calendar.getTimeInMillis() > System.currentTimeMillis()
                    + 2 * 24 * 3600 * 1000 || startTime.equals("") || startTime == null) {
                Toast.makeText(activity, "Please select from the current 15 minutes after the effective time", Toast.LENGTH_SHORT).show();
            } else {
                startTime = initDateTime;
                goIn = false;
            }
        mSubTime.selectTime(startTime + ":00"+" "+ dateStr);

    }

    private void setTextTime(String startTime) {
        if (startTime.equals("") || startTime == null ) {
            return;
        }
        String s = TimeUtils.formatDateTime(startTime);
        String s1 = startTime.substring(0, 10).trim().equals(getStatetime()) ? "AM" : "PM";
        String s2 = s.substring(3, s.length()).trim();
        String result = TimeUtils.getTimeDifference(startTime, endTime);
            selectTime.setText(s2 + " " + s1);
        }
}
