package com.firepitmedia.earnit.dialogfragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.utils.ScreenSwitch;

/**
 * Created by GreenLose on 12/8/2017.
 */

public class ScreenRuleFragment extends Fragment {

    ScreenSwitch screenSwitch;
    ScreenRuleFragment screenRuleFragment;
    private BottomSheetDialog mBottomSheetDialog;
    Boolean checkButton1 = false;
    Boolean checkButton2= false;
    Boolean checkButton3 = false;
    Boolean checkButton4 = false;
    Boolean checkButton5 = false;
    Boolean checkButton6 = false;
    Boolean monButton = false;
    Boolean tueButton = false;
    Boolean wenButton = false;
    Boolean thuButton = false;
    Boolean friButton = false;
    Boolean satButton = false;
    Boolean sanButton = false;
    Button daybutton1;
    Button daybutton2;
    Button daybutton3;
    Button daybutton4;
    Button daybutton5;
    Button daybutton6;
    Button daybutton7;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View PasswordReminderView = inflater.inflate(R.layout.screen_rules, container, false);
        screenRuleFragment = this;
        Button okBtn = (Button) PasswordReminderView.findViewById(R.id.screenrule_save);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getView().getContext(), "Pressed OK key!!!", Toast.LENGTH_LONG ).show();
            }
        });
        Button calcelBtn = (Button) PasswordReminderView.findViewById(R.id.screenrule_cancel);
        calcelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getView().getContext(), "Pressed Cancel key!!!", Toast.LENGTH_LONG ).show();
            }
        });
        EditText newRule = (EditText) PasswordReminderView.findViewById(R.id.newRule_edit);
        EditText ruleApplyfrom = (EditText) PasswordReminderView.findViewById(R.id.rule_apply_from);
        EditText ruleApplyto = (EditText) PasswordReminderView.findViewById(R.id.rule_apply_to);
        newRule.setText("School");
        ruleApplyfrom.setText("8:30am");
        ruleApplyto.setText("4:30pm");
        EditText ruleApplyfrom1 = (EditText) PasswordReminderView.findViewById(R.id.rule_apply_from1);
        EditText ruleApplyto1 = (EditText) PasswordReminderView.findViewById(R.id.rule_apply_to1);
        EditText ruleApplyfrom2 = (EditText) PasswordReminderView.findViewById(R.id.rule_apply_from2);
        EditText ruleApplyto2 = (EditText) PasswordReminderView.findViewById(R.id.rule_apply_to2);
        ruleApplyfrom2.setText("8:30am");
        ruleApplyto2.setText("4:30pm");
        ruleApplyfrom1.setText("10:00pm");
        ruleApplyto1.setText("6:00am");
        ImageButton delBtn1 = (ImageButton) PasswordReminderView.findViewById(R.id.del_list1);
        ImageButton delBtn2 = (ImageButton) PasswordReminderView.findViewById(R.id.del_list2);
        delBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getView().getContext(), "Pressed RedIcon1 key!!!", Toast.LENGTH_LONG ).show();
            }
        });
        delBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getView().getContext(), "Pressed RedIcon2 key!!!", Toast.LENGTH_LONG ).show();
            }
        });
        daybutton1 = (Button) PasswordReminderView.findViewById(R.id.repeat_weekly_san);
        daybutton1.setText("S");
        daybutton2 = (Button) PasswordReminderView.findViewById(R.id.repeat_weekly_mon);
        daybutton2.setText("M");
        daybutton3 = (Button) PasswordReminderView.findViewById(R.id.repeat_weekly_tue);
        daybutton3.setText("T");
        daybutton4 = (Button) PasswordReminderView.findViewById(R.id.repeat_weekly_wen);
        daybutton4.setText("W");
        daybutton5 = (Button) PasswordReminderView.findViewById(R.id.repeat_weekly_thu);
        daybutton5.setText("T");
        daybutton6 = (Button) PasswordReminderView.findViewById(R.id.repeat_weekly_fri);
        daybutton6.setText("F");
        daybutton7 = (Button) PasswordReminderView.findViewById(R.id.repeat_weekly_sat);
        daybutton7.setText("S");
        daybutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sanButton) {
                    daybutton1.setBackgroundResource(R.drawable.repeatweekly_trans);
                    sanButton = false;
                }
                else {
                    daybutton1.setBackgroundResource(R.drawable.repeatweekly_pink);
                    sanButton = true;
                }
            }
        });

        daybutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monButton) {
                    daybutton2.setBackgroundResource(R.drawable.repeatweekly_trans);
                    monButton = false;
                }
                else {
                    daybutton2.setBackgroundResource(R.drawable.repeatweekly_pink);
                    monButton = true;
                }
            }
        });

        daybutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tueButton) {
                    daybutton3.setBackgroundResource(R.drawable.repeatweekly_trans);
                    tueButton = false;
                }
                else {
                    daybutton3.setBackgroundResource(R.drawable.repeatweekly_pink);
                    tueButton = true;
                }
            }
        });

        daybutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wenButton) {
                    daybutton4.setBackgroundResource(R.drawable.repeatweekly_trans);
                    wenButton = false;
                }
                else {
                    daybutton4.setBackgroundResource(R.drawable.repeatweekly_pink);
                    wenButton = true;
                }
            }
        });

        daybutton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thuButton) {
                    daybutton5.setBackgroundResource(R.drawable.repeatweekly_trans);
                    thuButton = false;
                }
                else {
                    daybutton5.setBackgroundResource(R.drawable.repeatweekly_pink);
                    thuButton = true;
                }
            }
        });

        daybutton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friButton) {
                    daybutton6.setBackgroundResource(R.drawable.repeatweekly_trans);
                    friButton = false;
                }
                else {
                    daybutton6.setBackgroundResource(R.drawable.repeatweekly_pink);
                    friButton = true;
                }
            }
        });

        daybutton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(satButton) {
                    daybutton7.setBackgroundResource(R.drawable.repeatweekly_trans);
                    satButton = false;
                }
                else {
                    daybutton7.setBackgroundResource(R.drawable.repeatweekly_pink);
                    satButton = true;
                }
            }
        });

        final Button rule1  = (Button) PasswordReminderView.findViewById(R.id.rule_check1);
        rule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkButton1) {
                    rule1.setText("");
                    checkButton1 = false;
                }
                else {
                    rule1.setText("✔");
                    checkButton1 = true;
                }
            }
        });
        final Button rule2  = (Button) PasswordReminderView.findViewById(R.id.rule_check2);
        rule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkButton2) {
                    rule2.setText("");
                    checkButton2 = false;
                }
                else {
                    rule2.setText("✔");
                    checkButton2 = true;
                }
            }
        });
        final Button rule3  = (Button) PasswordReminderView.findViewById(R.id.rule_check3);
        rule3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkButton3) {
                    rule3.setText("");
                    checkButton3 = false;
                }
                else {
                    rule3.setText("✔");
                    checkButton3 = true;
                }
            }
        });
        final Button rule4  = (Button) PasswordReminderView.findViewById(R.id.rule_check4);
        rule4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkButton4) {
                    rule4.setText("");
                    checkButton4 = false;
                }
                else {
                    rule4.setText("✔");
                    checkButton4 = true;
                }
            }
        });
        final Button rule5  = (Button) PasswordReminderView.findViewById(R.id.rule_check5);
        rule5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkButton5) {
                    rule5.setText("");
                    checkButton5 = false;
                }
                else {
                    rule5.setText("✔");
                    checkButton5 = true;
                }
            }
        });
        final Button rule6  = (Button) PasswordReminderView.findViewById(R.id.rule_check6);
        rule6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkButton6) {
                    rule6.setText("");
                    checkButton6 = false;
                }
                else {
                    rule6.setText("✔");
                    checkButton6 = true;
                }
            }
        });



        return PasswordReminderView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
