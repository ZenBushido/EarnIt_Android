package com.firepitmedia.earnit.dialogfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.activity.LoginScreen;
import com.firepitmedia.earnit.activity.PasswordReminder;
import com.firepitmedia.earnit.utils.ScreenSwitch;

/**
 * Created by GreenLose on 12/8/2017.
 */

public class PasswordReminderResultFragment extends Fragment {

    ScreenSwitch screenSwitch;
    PasswordReminderResultFragment resultFragment;
    PasswordReminder passwordReminder_;
    private PasswordReminderResultFragment instance;
    TextView textView;
    String textt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View PasswordReminderView = inflater.inflate(R.layout.password_reminder_result, container, false);
        resultFragment = this;
        screenSwitch = new ScreenSwitch(passwordReminder_);
        Button okBtn = (Button) PasswordReminderView.findViewById(R.id.ok_button);
        textView = (TextView) PasswordReminderView.findViewById(R.id.passwordreminder_text3_id);
        textView.setText(textt);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getView().getContext(), "Pressed OK key!!!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), LoginScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        return PasswordReminderView;

    }

    private void onBackPressed() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(String textView) {
        textt= textView;
    }

    public PasswordReminderResultFragment getInstance(String email) {
        instance = new PasswordReminderResultFragment();
        instance.setTextView(email + " and return");
        return instance;
    }
}
