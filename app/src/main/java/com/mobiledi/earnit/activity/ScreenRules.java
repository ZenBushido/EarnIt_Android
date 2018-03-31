package com.mobiledi.earnit.activity;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.activity.applock.SplashActivity;
import com.mobiledi.earnit.dialogfragment.ScreenRuleFragment;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.screenruletimepicker.TimeSelectUtils;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.NavigationDrawer;
import com.mobiledi.earnit.utils.ScreenSwitch;
import com.mobiledi.earnit.utils.Utils;
import com.squareup.picasso.Picasso;

import static com.mobiledi.earnit.activity.applock.SplashActivity.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE;

/**
 * Created by GreenLose on 12/10/2017.
 */

public class ScreenRules extends AppCompatActivity implements NavigationDrawer.OnDrawerToggeled, View.OnClickListener {

    private final String TAG = "ScreenRules";
    String screen_name;
    Intent intent;
    ScreenRules screenRules;
    public Parent parentObject;
    public Child childObject, otherChild;
    private BottomSheetDialog mBottomSheetDialog;
    RelativeLayout progressBar;
    Tasks currentTask;
    ScreenSwitch screenSwitch;
    ScreenRuleFragment screenRuleFragment;
    private Toolbar goalToolbar;
    private ImageButton drawerToggle, headerback;
    int fetchGoalId = 0;

    boolean IS_EDITING_TASK = false;


    Boolean checkButton1 = false;
    Boolean checkButton2 = false;
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
    CircularImageView childAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_rules);

        screenRules = this;
        intent = getIntent();
        screen_name = intent.getStringExtra(AppConstant.FROM_SCREEN);
        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        screenSwitch = new ScreenSwitch(screenRules);
        progressBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        goalToolbar = (Toolbar) findViewById(R.id.toolbar_add);
        drawerToggle = (ImageButton) findViewById(R.id.drawerBtn);
        headerback = (ImageButton) findViewById(R.id.addtask_back_arrow);
        setSupportActionBar(goalToolbar);
        getSupportActionBar().setTitle(null);
        headerback.setOnClickListener(screenRules);
        Button okBtn = (Button) findViewById(R.id.screenrule_save);
        childAvatar = (CircularImageView) findViewById(R.id.add_task_avatar);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_avatar);
        requestOptions.error(R.drawable.default_avatar);

        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL+childObject.getAvatar())
                .into(childAvatar);

     /*   try {
            Picasso.with(ScreenRules.this).load(childObject.getAvatar()).error(R.drawable.default_avatar).into(childAvatar);
        } catch (Exception e) {
            Picasso.with(ScreenRules.this).load(R.drawable.default_avatar).into(childAvatar);
            e.printStackTrace();
        }*/

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(screenRules, "Pressed Ok key!!!", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
        Button calcelBtn = (Button) findViewById(R.id.screenrule_cancel);
        calcelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(screenRules, "Pressed Cancel key!!!", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
        final EditText newRule = (EditText) findViewById(R.id.newRule_edit);
        TextView ruleApplyfrom = (TextView) findViewById(R.id.rule_apply_from);
        TextView ruleApplyto = (TextView) findViewById(R.id.rule_apply_to);
        ruleApplyfrom.setText("8:30am");
        ruleApplyto.setText("4:30pm");
        TextView ruleApplyfrom1 = (TextView) findViewById(R.id.rule_apply_from1);
        TextView ruleApplyto1 = (TextView) findViewById(R.id.rule_apply_to1);
        TextView ruleApplyfrom2 = (TextView) findViewById(R.id.rule_apply_from2);
        TextView ruleApplyto2 = (TextView) findViewById(R.id.rule_apply_to2);
        ruleApplyfrom2.setText("8:30am");
        ruleApplyto2.setText("4:30pm");
        ruleApplyfrom1.setText("10:00pm");
        ruleApplyto1.setText("6:00am");
        ruleApplyfrom.setOnClickListener(screenRules);
        ruleApplyto.setOnClickListener(screenRules);

        currentTask = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);

        ImageButton delBtn1 = (ImageButton) findViewById(R.id.del_list1);
        ImageButton delBtn2 = (ImageButton) findViewById(R.id.del_list2);
        delBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTaskDialog();

            }
        });
        delBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTaskDialog();

            }
        });
        daybutton1 = (Button) findViewById(R.id.repeat_weekly_san);
        daybutton1.setText("S");
        daybutton2 = (Button) findViewById(R.id.repeat_weekly_mon);
        daybutton2.setText("M");
        daybutton3 = (Button) findViewById(R.id.repeat_weekly_tue);
        daybutton3.setText("T");
        daybutton4 = (Button) findViewById(R.id.repeat_weekly_wen);
        daybutton4.setText("W");
        daybutton5 = (Button) findViewById(R.id.repeat_weekly_thu);
        daybutton5.setText("T");
        daybutton6 = (Button) findViewById(R.id.repeat_weekly_fri);
        daybutton6.setText("F");
        daybutton7 = (Button) findViewById(R.id.repeat_weekly_sat);
        daybutton7.setText("S");
        daybutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sanButton) {
                    daybutton1.setBackgroundResource(R.drawable.repeatweekly_trans);
                    sanButton = false;
                } else {
                    daybutton1.setBackgroundResource(R.drawable.repeatweekly_pink);
                    sanButton = true;
                }
            }
        });

        daybutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monButton) {
                    daybutton2.setBackgroundResource(R.drawable.repeatweekly_trans);
                    monButton = false;
                } else {
                    daybutton2.setBackgroundResource(R.drawable.repeatweekly_pink);
                    monButton = true;
                }
            }
        });

        daybutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tueButton) {
                    daybutton3.setBackgroundResource(R.drawable.repeatweekly_trans);
                    tueButton = false;
                } else {
                    daybutton3.setBackgroundResource(R.drawable.repeatweekly_pink);
                    tueButton = true;
                }
            }
        });

        daybutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wenButton) {
                    daybutton4.setBackgroundResource(R.drawable.repeatweekly_trans);
                    wenButton = false;
                } else {
                    daybutton4.setBackgroundResource(R.drawable.repeatweekly_pink);
                    wenButton = true;
                }
            }
        });

        daybutton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thuButton) {
                    daybutton5.setBackgroundResource(R.drawable.repeatweekly_trans);
                    thuButton = false;
                } else {
                    daybutton5.setBackgroundResource(R.drawable.repeatweekly_pink);
                    thuButton = true;
                }
            }
        });

        daybutton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friButton) {
                    daybutton6.setBackgroundResource(R.drawable.repeatweekly_trans);
                    friButton = false;
                } else {
                    daybutton6.setBackgroundResource(R.drawable.repeatweekly_pink);
                    friButton = true;
                }
            }
        });

        daybutton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (satButton) {
                    daybutton7.setBackgroundResource(R.drawable.repeatweekly_trans);
                    satButton = false;
                } else {
                    daybutton7.setBackgroundResource(R.drawable.repeatweekly_pink);
                    satButton = true;
                }
            }
        });

        final Button rule1 = (Button) findViewById(R.id.rule_check1);
        rule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkButton1) {
                    rule1.setText("");
                    checkButton1 = false;
                } else {
                    rule1.setText("✔");
                    checkButton1 = true;

                    showDialog();
                }
            }
        });
        final Button rule2 = (Button) findViewById(R.id.rule_check2);
        rule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkButton2) {
                    rule2.setText("");
                    checkButton2 = false;
                } else {
                    rule2.setText("✔");
                    checkButton2 = true;
                }
            }
        });
        final Button rule3 = (Button) findViewById(R.id.rule_check3);
        rule3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkButton3) {
                    rule3.setText("");
                    checkButton3 = false;
                } else {
                    rule3.setText("✔");
                    checkButton3 = true;
                }
            }
        });
        final Button rule4 = (Button) findViewById(R.id.rule_check4);
        rule4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkButton4) {
                    rule4.setText("");
                    checkButton4 = false;
                } else {
                    rule4.setText("✔");
                    checkButton4 = true;
                }
            }
        });
        final Button rule5 = (Button) findViewById(R.id.rule_check5);
        rule5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkButton5) {
                    rule5.setText("");
                    checkButton5 = false;
                } else {
                    rule5.setText("✔");
                    checkButton5 = true;
                }
            }
        });
        final Button rule6 = (Button) findViewById(R.id.rule_check6);
        rule6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkButton6) {
                    rule6.setText("");
                    checkButton6 = false;
                } else {
                    rule6.setText("✔");
                    checkButton6 = true;
                }
            }
        });
    }

    public void deleteTaskDialog() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this tasks")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    public void onCancelAndBack(Child child, Child otherChild) {
        Utils.logDebug(TAG, "calling onCancelAndBack method ");
        if (screen_name.equalsIgnoreCase(AppConstant.CHECKED_IN_SCREEN)) {
            if (otherChild.getTasksArrayList().size() > 0)
                screenSwitch.moveToAllTaskScreen(child, otherChild, screen_name, parentObject, AppConstant.ADD_TASK);
            else
                Toast.makeText(screenRules, getResources().getString(R.string.no_task_available), Toast.LENGTH_LONG).show();
        } else if (screen_name.equalsIgnoreCase(AppConstant.CHECKED_IN_TASK_APPROVAL__SCREEN)) {
            if (child.getTasksArrayList().size() > 0)
                screenSwitch.moveToAllTaskScreen(child, otherChild, screen_name, parentObject, AppConstant.ADD_TASK);
            else
                Toast.makeText(screenRules, getResources().getString(R.string.no_task_for_approval), Toast.LENGTH_LONG).show();
        } else if (screen_name.equalsIgnoreCase(AppConstant.PARENT_DASHBOARD))
            screenSwitch.moveToParentDashboard(parentObject);
        else if (screen_name.equalsIgnoreCase(AppConstant.GOAL_SCREEN))
            screenSwitch.isGoalExists(child, otherChild, parentObject, progressBar, screen_name, currentTask);
        else if (screen_name.equalsIgnoreCase(AppConstant.BALANCE_SCREEN))
            screenSwitch.checkBalance(child, otherChild, parentObject, AppConstant.ADD_TASK, currentTask, progressBar, AppConstant.PARENT);
        else {
            screen_name = AppConstant.PARENT_DASHBOARD;
            screenSwitch.moveToParentDashboard(parentObject);
        }
    }

    @Override
    public void onDrawerToggeled() {
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addtask_back_arrow:
                Toast.makeText(screenRules, getResources().getString(R.string.no_task_available), Toast.LENGTH_LONG).show();
                onBackPressed();
                break;
            case R.id.rule_apply_from:
                final TextView from = (TextView) findViewById(R.id.rule_apply_from);
                final TextView to = (TextView) findViewById(R.id.rule_apply_to);

                TimeSelectUtils timeSelectUtils = new TimeSelectUtils(ScreenRules.this, null, from, to, new TimeSelectUtils.GetSubmitTime() {
                    @Override
                    public void selectTime(String startDate, String entDate) {
                    }
                });
                timeSelectUtils.dateTimePicKDialog();
                break;
            case R.id.rule_apply_to:
                final TextView from1 = (TextView) findViewById(R.id.rule_apply_from);
                final TextView to1 = (TextView) findViewById(R.id.rule_apply_to);

                TimeSelectUtils timeSelectUtils1 = new TimeSelectUtils(ScreenRules.this, null, from1, to1, new TimeSelectUtils.GetSubmitTime() {
                    @Override
                    public void selectTime(String startDate, String entDate) {
                    }
                });
                timeSelectUtils1.dateTimePicKDialog();
                break;
        }
    }


    public void showDialog() {
        AlertDialog.Builder alertDialogBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alertDialogBuilder = new AlertDialog.Builder(this);
        }

        alertDialogBuilder.setTitle("Confirm Permission")
                .setMessage("Enable permission for App Usage and Draw Overlay Setting's.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // This method is used to set permission for App Usage and Draw Overlay Setting.
                        setAllPermission();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private void setAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }

        checkPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

            int mode = 0;

            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
            else{

                startActivity(new Intent(ScreenRules.this, SplashActivity.class));
            }
        }
         else{

            startActivity(new Intent(ScreenRules.this, SplashActivity.class));

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {

                checkPermission();
            }
            else{

                startActivity(new Intent(ScreenRules.this, SplashActivity.class));
            }

        }
    }
}
