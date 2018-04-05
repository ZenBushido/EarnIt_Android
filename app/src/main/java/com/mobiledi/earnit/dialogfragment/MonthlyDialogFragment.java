package com.mobiledi.earnit.dialogfragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiledi.earnit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.mobiledi.earnit.dialogfragment.WeeklyDialogFragment.weekList;


public class MonthlyDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
     EditText repeat_monthly_check;
    String TAG = MonthlyDialogFragment.class.getSimpleName();
    public static List<String> monthList = new ArrayList<>();
    public String onFirst;
    public String onDay;
     RadioButton onButton;
MonthlyDialogListener monthlyDialogListener;
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        monthlyDialogListener = (MonthlyDialogListener) context;
    }

    Button daybutton1;
    Button daybutton2;
    Button daybutton3;
    Button daybutton4;
    Button daybutton5;
    Button daybutton6;
    Button daybutton7;
    Button daybutton8;
    Button daybutton9;
    Button daybutton10;
    Button daybutton11;
    Button daybutton12;
    Button daybutton13;
    Button daybutton14;
    Button daybutton15;
    Button daybutton16;
    Button daybutton17;
    Button daybutton18;
    Button daybutton19;
    Button daybutton20;
    Button daybutton21;
    Button daybutton22;
    Button daybutton23;
    Button daybutton24;
    Button daybutton25;
    Button daybutton26;
    Button daybutton27;
    Button daybutton28;
    Button daybutton29;
    Button daybutton30;
    Button daybutton31;

    Boolean D1 = false;
    Boolean D2 = false;
    Boolean D3 = false;
    Boolean D4 = false;
    Boolean D5 = false;
    Boolean D6 = false;
    Boolean D7 = false;
    Boolean D8 = false;
    Boolean D9 = false;
    Boolean D10 = false;
    Boolean D11 = false;
    Boolean D12 = false;
    Boolean D13 = false;
    Boolean D14 = false;
    Boolean D15 = false;
    Boolean D16 = false;
    Boolean D17 = false;
    Boolean D18 = false;
    Boolean D19 = false;
    Boolean D20 = false;
    Boolean D21 = false;
    Boolean D22 = false;
    Boolean D23 = false;
    Boolean D24 = false;
    Boolean D25 = false;
    Boolean D26 = false;
    Boolean D27 = false;
    Boolean D28 = false;
    Boolean D29 = false;
    Boolean D30 = false;
    Boolean D31 = false;
    Boolean selectSwitch = true;

    private final String LOG_TAG = MyDialogFragment.class.getSimpleName();
    ArrayList<String> onThe;
    ArrayList<String> onTheLast;

    // onCreate --> (onCreateDialog) --> onCreateView --> onActivityCreated
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        onThe = new ArrayList<String>();
        monthList= new ArrayList<String>();
        onThe.add("First");
        onThe.add("Second");
        onThe.add("Third");
        onThe.add("Fourth");
        onThe.add("Fifth");
        onThe.add("Last");

        onTheLast = new ArrayList<String>();
        onTheLast.add("Sunday");
        onTheLast.add("Monday");
        onTheLast.add("Tuesday");
        onTheLast.add("Thursday");
        onTheLast.add("Friday");
        onTheLast.add("Saturday");
        onTheLast.add("Week day");
        onTheLast.add("Weekend Day");


        final View dialogView = inflater.inflate(R.layout.repeat_monthly, container, false);

        TextView repeat_daily_text = (TextView) dialogView.findViewById(R.id.repeat_monthly_frequency);
        repeat_daily_text.setText("Monthly");
        final Spinner repeat_monthly_first = (Spinner) dialogView.findViewById(R.id.repeat_monthly_first);
        repeat_monthly_first.setOnItemSelectedListener(this);

        final Spinner repeat_monthly_last = (Spinner) dialogView.findViewById(R.id.repeat_monthly_last);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, onThe);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        repeat_monthly_first.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, onTheLast);
        adapter2.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        repeat_monthly_last.setAdapter(adapter2);
        repeat_monthly_last.setOnItemSelectedListener(this);
        repeat_monthly_check = (EditText) dialogView.findViewById(R.id.repeat_monthly_checkbox);
        repeat_monthly_check.setText("");


        daybutton1 = (Button) dialogView.findViewById(R.id.d1);
        daybutton1.setText("1");
        daybutton2 = (Button) dialogView.findViewById(R.id.d2);
        daybutton2.setText("2");
        daybutton3 = (Button) dialogView.findViewById(R.id.d3);
        daybutton3.setText("3");
        daybutton4 = (Button) dialogView.findViewById(R.id.d4);
        daybutton4.setText("4");
        daybutton5 = (Button) dialogView.findViewById(R.id.d5);
        daybutton5.setText("5");
        daybutton6 = (Button) dialogView.findViewById(R.id.d6);
        daybutton6.setText("6");
        daybutton7 = (Button) dialogView.findViewById(R.id.d7);
        daybutton7.setText("7");
        daybutton8 = (Button) dialogView.findViewById(R.id.d8);
        daybutton8.setText("8");
        daybutton9 = (Button) dialogView.findViewById(R.id.d9);
        daybutton9.setText("9");
        daybutton10 = (Button) dialogView.findViewById(R.id.d10);
        daybutton10.setText("10");
        daybutton11 = (Button) dialogView.findViewById(R.id.d11);
        daybutton11.setText("11");
        daybutton12 = (Button) dialogView.findViewById(R.id.d12);
        daybutton12.setText("12");
        daybutton13 = (Button) dialogView.findViewById(R.id.d13);
        daybutton13.setText("13");
        daybutton14 = (Button) dialogView.findViewById(R.id.d14);
        daybutton14.setText("14");
        daybutton15 = (Button) dialogView.findViewById(R.id.d15);
        daybutton15.setText("15");
        daybutton16 = (Button) dialogView.findViewById(R.id.d16);
        daybutton16.setText("16");
        daybutton17 = (Button) dialogView.findViewById(R.id.d17);
        daybutton17.setText("17");
        daybutton18 = (Button) dialogView.findViewById(R.id.d18);
        daybutton18.setText("18");
        daybutton19 = (Button) dialogView.findViewById(R.id.d19);
        daybutton19.setText("19");
        daybutton20 = (Button) dialogView.findViewById(R.id.d20);
        daybutton20.setText("20");
        daybutton21 = (Button) dialogView.findViewById(R.id.d21);
        daybutton21.setText("21");
        final Button daybutton22 = (Button) dialogView.findViewById(R.id.d22);
        daybutton22.setText("22");
        daybutton23 = (Button) dialogView.findViewById(R.id.d23);
        daybutton23.setText("23");
        daybutton24 = (Button) dialogView.findViewById(R.id.d24);
        daybutton24.setText("24");
        daybutton25 = (Button) dialogView.findViewById(R.id.d25);
        daybutton25.setText("25");
        daybutton26 = (Button) dialogView.findViewById(R.id.d26);
        daybutton26.setText("26");
        daybutton27 = (Button) dialogView.findViewById(R.id.d27);
        daybutton27.setText("27");
        daybutton28 = (Button) dialogView.findViewById(R.id.d28);
        daybutton28.setText("28");
        daybutton29 = (Button) dialogView.findViewById(R.id.d29);
        daybutton29.setText("29");
        daybutton30 = (Button) dialogView.findViewById(R.id.d30);
        daybutton30.setText("30");
        daybutton31 = (Button) dialogView.findViewById(R.id.d31);
        daybutton31.setText("31");


        final RadioButton eachButton = (RadioButton) dialogView.findViewById(R.id.repeatmonthly_eachradio);
 onButton = (RadioButton) dialogView.findViewById(R.id.repeatmonthly_ontheradio);
        repeat_monthly_first.setEnabled(false);
        repeat_monthly_last.setEnabled(false);
        eachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eachButton.isChecked()) {
                    onButton.setChecked(false);
                    selectSwitch = true;
                    repeat_monthly_first.setEnabled(false);
                    repeat_monthly_last.setEnabled(false);

                }
            }
        });
        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButton.isChecked()) {
                    selectSwitch = false;
                    eachButton.setChecked(false);
                    repeat_monthly_first.setEnabled(true);
                    repeat_monthly_last.setEnabled(true);
                    daybutton1.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton2.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton3.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton4.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton5.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton6.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton7.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton8.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton9.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton10.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton11.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton12.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton13.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton14.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton15.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton16.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton17.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton18.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton19.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton20.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton21.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton22.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton23.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton24.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton25.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton26.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton27.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton28.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton29.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton30.setBackgroundResource(R.drawable.repeat_calendar);
                    daybutton31.setBackgroundResource(R.drawable.repeat_calendar);
                }
            }
        });


        daybutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D1) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("1")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton1.setBackgroundResource(R.drawable.repeat_calendar);
                        D1 = false;
                    } else {
                        monthList.add("1");
                        daybutton1.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D1 = true;
                    }
                }
            }
        });

        daybutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D2) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("2")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton2.setBackgroundResource(R.drawable.repeat_calendar);
                        D2 = false;
                    } else {
                        monthList.add("2");

                        daybutton2.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D2 = true;
                    }
                }
            }
        });

        daybutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {

                    if (D3) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("3")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton3.setBackgroundResource(R.drawable.repeat_calendar);
                        D3 = false;
                    } else {
                        monthList.add("3");

                        daybutton3.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D3 = true;
                    }
                }
            }
        });

        daybutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D4) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("4")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton4.setBackgroundResource(R.drawable.repeat_calendar);
                        D4 = false;
                    } else {
                        monthList.add("4");
                        daybutton4.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D4 = true;
                    }
                }
            }
        });

        daybutton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D5) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("5")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton5.setBackgroundResource(R.drawable.repeat_calendar);
                        D5 = false;
                    } else {
                        monthList.add("5");
                        daybutton5.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D5 = true;
                    }
                }
            }
        });

        daybutton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D6) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("6")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton6.setBackgroundResource(R.drawable.repeat_calendar);
                        D6 = false;
                    } else {
                        monthList.add("6");
                        daybutton6.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D6 = true;
                    }
                }
            }
        });

        daybutton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D7) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("7")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton7.setBackgroundResource(R.drawable.repeat_calendar);
                        D7 = false;
                    } else {
                        monthList.add("7");
                        daybutton7.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D7 = true;
                    }
                }
            }
        });
        daybutton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D8) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("8")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton8.setBackgroundResource(R.drawable.repeat_calendar);
                        D8 = false;
                    } else {
                        monthList.add("8");
                        daybutton8.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D8 = true;
                    }
                }
            }
        });
        daybutton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D9) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("9")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton9.setBackgroundResource(R.drawable.repeat_calendar);
                        D9 = false;
                    } else {
                        monthList.add("9");
                        daybutton9.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D9 = true;
                    }
                }
            }
        });
        daybutton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D10) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("10")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton10.setBackgroundResource(R.drawable.repeat_calendar);
                        D10 = false;
                    } else {
                        monthList.add("10");
                        daybutton10.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D10 = true;
                    }
                }
            }
        });
        daybutton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D11) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("11")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton11.setBackgroundResource(R.drawable.repeat_calendar);
                        D11 = false;
                    } else {
                        monthList.add("11");
                        daybutton11.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D11 = true;
                    }
                }
            }
        });
        daybutton12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D12) {

                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("12")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton12.setBackgroundResource(R.drawable.repeat_calendar);
                        D12 = false;
                    } else {
                        monthList.add("12");
                        daybutton12.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D12 = true;
                    }
                }
            }
        });
        daybutton13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D13) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("13")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton13.setBackgroundResource(R.drawable.repeat_calendar);
                        D13 = false;
                    } else {
                        monthList.add("13");

                        daybutton13.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D13 = true;
                    }
                }
            }
        });
        daybutton14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D14) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("14")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton14.setBackgroundResource(R.drawable.repeat_calendar);
                        D14 = false;
                    } else {
                        monthList.add("14");

                        daybutton14.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D14 = true;
                    }
                }
            }
        });
        daybutton15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D15) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("15")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton15.setBackgroundResource(R.drawable.repeat_calendar);
                        D7 = false;
                    } else {
                        monthList.add("15");

                        daybutton15.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D15 = true;
                    }
                }
            }
        });
        daybutton16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D16) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("16")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton16.setBackgroundResource(R.drawable.repeat_calendar);
                        D16 = false;
                    } else {
                        monthList.add("16");

                        daybutton16.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D16 = true;
                    }
                }
            }
        });
        daybutton17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D17) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("17")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton17.setBackgroundResource(R.drawable.repeat_calendar);
                        D17 = false;
                    } else {
                        monthList.add("17");

                        daybutton17.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D17 = true;
                    }
                }
            }
        });
        daybutton18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D18) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("18")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton18.setBackgroundResource(R.drawable.repeat_calendar);
                        D18 = false;
                    } else {
                        monthList.add("18");

                        daybutton18.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D18 = true;
                    }
                }
            }
        });
        daybutton19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D19) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("19")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton19.setBackgroundResource(R.drawable.repeat_calendar);
                        D19 = false;
                    } else {
                        monthList.add("19");

                        daybutton19.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D19 = true;
                    }
                }
            }
        });
        daybutton20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D20) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("20")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton20.setBackgroundResource(R.drawable.repeat_calendar);
                        D20 = false;
                    } else {
                        monthList.add("20");

                        daybutton20.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D20 = true;
                    }
                }
            }
        });
        daybutton21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D21) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("21")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton21.setBackgroundResource(R.drawable.repeat_calendar);
                        D21 = false;
                    } else {
                        monthList.add("21");

                        daybutton21.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D21 = true;
                    }
                }
            }
        });
        daybutton22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D22) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("22")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton22.setBackgroundResource(R.drawable.repeat_calendar);
                        D22 = false;
                    } else {
                        monthList.add("22");

                        daybutton22.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D22 = true;
                    }
                }
            }
        });
        daybutton23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D23) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("23")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton23.setBackgroundResource(R.drawable.repeat_calendar);
                        D23 = false;
                    } else {
                        monthList.add("23");

                        daybutton23.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D23 = true;
                    }
                }
            }
        });
        daybutton24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D24) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("24")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton24.setBackgroundResource(R.drawable.repeat_calendar);
                        D24 = false;
                    } else {
                        monthList.add("24");

                        daybutton24.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D24 = true;
                    }
                }
            }
        });
        daybutton25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D25) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("25")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton25.setBackgroundResource(R.drawable.repeat_calendar);
                        D25 = false;
                    } else {
                        monthList.add("25");

                        daybutton25.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D25 = true;
                    }
                }
            }
        });
        daybutton26.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D26) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("26")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton26.setBackgroundResource(R.drawable.repeat_calendar);
                        D26 = false;
                    } else {
                        monthList.add("26");

                        daybutton26.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D26 = true;
                    }
                }
            }
        });
        daybutton27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D27) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("27")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton27.setBackgroundResource(R.drawable.repeat_calendar);
                        D27 = false;
                    } else {
                        monthList.add("27");

                        daybutton27.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D27 = true;
                    }
                }
            }
        });
        daybutton28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D28) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("28")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton28.setBackgroundResource(R.drawable.repeat_calendar);
                        D28 = false;
                    } else {
                        monthList.add("28");

                        daybutton28.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D28 = true;
                    }
                }
            }
        });
        daybutton29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D29) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("29")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton29.setBackgroundResource(R.drawable.repeat_calendar);
                        D29 = false;
                    } else {
                        monthList.add("29");

                        daybutton29.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D29 = true;
                    }
                }
            }
        });
        daybutton30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D30) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("30")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton30.setBackgroundResource(R.drawable.repeat_calendar);
                        D30 = false;
                    } else {
                        monthList.add("30");

                        daybutton30.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D30 = true;
                    }
                }
            }
        });
        daybutton31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSwitch) {
                    if (D31) {
                        for (int i = 0; i < monthList.size(); i++) {

                            if (monthList.get(i).equals("31")) {

                                monthList.remove(i);
                            }
                        }
                        daybutton31.setBackgroundResource(R.drawable.repeat_calendar);
                        D31 = false;
                    } else {
                        monthList.add("31");

                        daybutton31.setBackgroundResource(R.drawable.repeat_calendar_pink);
                        D31 = true;
                    }
                }
            }
        });


        // "Got it" button
        Button buttonPos = (Button) dialogView.findViewById(R.id.repeatmonthly_ok);
        buttonPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Testing");
                Log.e(TAG, "check text= "+ repeat_monthly_check.getText().toString());
                if(!onButton.isChecked()) {
                    Log.e(TAG, "on check is false");
                    if (Objects.equals(repeat_monthly_check.getText().toString(), "00") || Objects.equals(repeat_monthly_check.getText().toString(), "0") || Objects.equals(repeat_monthly_check.getText().toString(), "") || monthList.size() < 1)
                        Toast.makeText(getActivity(), "Insert correct data", Toast.LENGTH_LONG).show();
                    else
                    {
                        dismiss();
                        monthlyDialogListener.updateResult2(monthList, repeat_monthly_check.getText().toString());
                    }


                }
                else
                {
                    Log.e(TAG, "on check is true");
                    if (Objects.equals(repeat_monthly_check.getText().toString(), "00") || Objects.equals(repeat_monthly_check.getText().toString(), "0") || Objects.equals(repeat_monthly_check.getText().toString(), "") )
                        Toast.makeText(getActivity(), "Insert correct data", Toast.LENGTH_LONG).show();

                    else

                    {
                        dismiss();
                        monthlyDialogListener.updateOnDay(onFirst, onDay,  repeat_monthly_check.getText().toString());
                    }



                }
            }
        });

        // "Cancel" button
        Button buttonNeg =  dialogView.findViewById(R.id.repeatmonthly_cancel);
        buttonNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Pressed Cancel Button!");
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
        //    Toast.makeText(getActivity(), "Clicked on \"" + buttonName + "\"", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.repeat_monthly_first)
        {
                     onFirst = String.valueOf(parent.getItemAtPosition(position)) ;
            Log.e(TAG, "onDay+ "+onFirst);
        }
        else if(spinner.getId() == R.id.repeat_monthly_last)
        {
            onDay = String.valueOf(parent.getItemAtPosition(position));
            Log.e(TAG, "onDay+ "+onDay);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /* @Override
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
    public interface MonthlyDialogListener {
        void updateResult2(List<String> inputText, String integer);



        void updateOnDay(String onFirst, String onDay, String s);
    }
}
