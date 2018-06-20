package com.mobiledi.earnit.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.MyApplication;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.adapter.MyRecyclerViewAdapter;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.model.goal.GetAllGoalResponse;
import com.mobiledi.earnit.retrofit.RetroInterface;
import com.mobiledi.earnit.retrofit.RetrofitClient;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.ScreenSwitch;
import com.mobiledi.earnit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mobile-di on 7/10/17.
 */

public class BalanceActivity extends BaseActivity   {

    @BindView(R.id.goal_header) TextView headerBalance;
    @BindView(R.id.goal_avatar) CircularImageView avatar;
    @BindView(R.id.btn_adjust)  Button btn_adjust;
    @BindView(R.id.tv_cash) TextView tv_cash;
    BalanceAdjustment balance;
    public Parent parentObject;
    public Child childObject, otherChild;
    Tasks tasks;
    String fromScreen, userType;
    private final String TAG = BalanceActivity.class.getSimpleName();
    Intent intent;
    @BindView(R.id.loadingPanel)
    RelativeLayout progressBar;
    ScreenSwitch screenSwitch;
    @BindView(R.id.toolbar_goal)
    Toolbar addToolbar;
    @BindView(R.id.drawerBtn)
    ImageButton drawerToggle;
    String add = "Add";
    List<Goal> listGoal = new ArrayList<>();
    @BindView(R.id.balanceChildList)
    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    @BindView(R.id.totalbalance)
    TextView totalBalance;
    @BindView(R.id.tv_goal_amount)
    TextView totalGoal;
    @BindView(R.id.view)
    View view;
    BalanceActivity balanceChild;
    boolean isGoalAvailable = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_screen_layout);
        ButterKnife.bind(this);

        balanceChild = this;
        drawerToggle.setVisibility(View.GONE);

        setSupportActionBar(addToolbar);
        getSupportActionBar().setTitle(null);
        intent = getIntent();
        screenSwitch = new ScreenSwitch(this);
        listGoal = new ArrayList<>();

        parentObject = (Parent) intent.getSerializableExtra(AppConstant.PARENT_OBJECT);
        childObject = (Child) intent.getSerializableExtra(AppConstant.CHILD_OBJECT);
        otherChild = (Child) intent.getSerializableExtra(AppConstant.OTHER_CHILD_OBJECT);
        //listGoal = (List<Goal>) intent.getSerializableExtra(AppConstant.GOAL_OBJECT);
        tasks = (Tasks) intent.getSerializableExtra(AppConstant.TO_EDIT);

        Log.e(TAG, "Child Object= "+childObject.getAvatar());
        progressBar.setVisibility(View.VISIBLE);



        fromScreen = intent.getStringExtra(AppConstant.FROM_SCREEN);
        userType = intent.getStringExtra(AppConstant.TYPE);

        if (userType.equals(AppConstant.CHILD)) {

            btn_adjust.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        } else {

            btn_adjust.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        }
        headerBalance.setText(childObject.getFirstName());

        if (isDeviceOnline())
        getAllGoals();


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(350,350);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.placeholder(R.drawable.default_avatar);
        requestOptions.error(R.drawable.default_avatar);
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL+childObject.getAvatar()).into(avatar);

     /*   try {
            Picasso.with(balance).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + childObject.getAvatar()).error(R.drawable.default_avatar).into(avatar);
        } catch (Exception e) {
            Picasso.with(balance).load(R.drawable.default_avatar).into(avatar);
            e.printStackTrace();
        }*/


    }

    /*-----------------TODO OnClickMethods -------------------------*/

    @OnClick(R.id.btn_adjust)
    void adjustBalance() {

        if(isGoalAvailable)
        screenSwitch.moveToBalance(childObject, otherChild, parentObject, AppConstant.BALANCE_SCREEN, tasks, listGoal, userType);
        else
            Utils.showToast(this, "Add Gaols First to Adjust");
    }

    @OnClick(R.id.goal_avatar)
    void floatingMenu() {
        if (userType.equalsIgnoreCase(AppConstant.PARENT))

            new FloatingMenu(balanceChild).fetchAvatarDimension(avatar, childObject, otherChild, parentObject, AppConstant.BALANCE_SCREEN, progressBar, tasks);
        else

            new FloatingMenu(balanceChild).fetchAvatarDimension253(null, avatar, childObject, parentObject, AppConstant.BALANCE_SCREEN, progressBar, tasks);

    }

    @OnClick(R.id.addtask_back_arrow)
    void back() {
        if (userType.equalsIgnoreCase(AppConstant.PARENT))
            screenSwitch.moveToParentDashboard(parentObject);
        else
            screenSwitch.moveToChildDashboard(childObject, progressBar);
    }

    @OnClick(R.id.save)
    void home()

    {
        if (userType.equalsIgnoreCase(AppConstant.PARENT))
            screenSwitch.moveToParentDashboard(parentObject);
        else
            screenSwitch.moveToChildDashboard(childObject, progressBar);
    }

    //--------------------------------- TODO End OnClick-------------------------------------


    @Override
    public void onBackPressed() {
        ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);

        int sizeStack =  am.getRunningTasks(2).size();
        Log.d("lksjdfjlkj", " back pressed: " + userType + "; sizeStack: " + sizeStack);
        super.onBackPressed();
//        moveTaskToBack(true);
//        if (userType.equalsIgnoreCase(AppConstant.PARENT))
//            screenSwitch.moveToParentDashboard(parentObject);
//        else
//            screenSwitch.moveToChildDashboard(childObject, progressBar);
    }


    private void getAllGoals() {

        RetroInterface retroInterface = RetrofitClient.getApiServices(MyApplication.getInstance().getEmail(), MyApplication.getInstance().getPassword());
        Call<List<GetAllGoalResponse>> response = retroInterface.getGoals(childObject.getId());

        response.enqueue(new Callback<List<GetAllGoalResponse>>() {
            @Override
            public void onResponse(Call<List<GetAllGoalResponse>> call, Response<List<GetAllGoalResponse>> response) {

                if (response.body() != null) {

                    if (response.body().isEmpty())
                        isGoalAvailable = false;
                    else
                        isGoalAvailable = true;

                    Integer cashTotal = 0;
                    Integer goalTotal = 0;
                    Integer totalAccountBalance = 0;

                    for (int i = 0; i < response.body().size(); i++) {
                        cashTotal += response.body().get(i).getCash();

                        for (int j = 0; j < response.body().get(i).getAdjustments().size(); j++) {
                            goalTotal += response.body().get(i).getAdjustments().get(j).getAmount();
                        }
                        goalTotal += response.body().get(i).getAmount();

                    }

                    totalAccountBalance += cashTotal + goalTotal;
                    tv_cash.setText("$" + cashTotal.toString());


                    totalBalance.setText("$" + totalAccountBalance.toString());
                    totalGoal.setText("$" + goalTotal.toString());

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

                    recyclerView.setLayoutManager(mLayoutManager);
                    adapter = new MyRecyclerViewAdapter(getApplicationContext(), response.body());
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.e(TAG, "Balance response null");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<GetAllGoalResponse>> call, Throwable t) {
                Log.e(TAG, "Throwable t: " + t.getLocalizedMessage());
            }
        });
    }




}
