package com.mobiledi.earnit.adapter;

/**
 * Created by praks on 07/07/17.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.customcontrol.ExpandableHeightListView;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.FloatingMenu;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.Utils;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.MyViewHolder> {

    private List<Child> childList;
    private List<Child> childApprovalList;
    private TaskAdapter taskAdapter;
    public Parent parent;
    private Activity activity;
    FloatingActionMenu lastMenu;
    boolean isOpen = false;
    int FAB_ICON_SIZE, FAB_ICON_RADIUS, FAB_ICON_DIMEN;
    RelativeLayout progressBar;
    private final String TAG = "ChildrenAdapter";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView firstName;
        public CircularImageView profileImage;
        public ExpandableHeightListView taskListView;
        public LinearLayout rootLayout;


        public MyViewHolder(View view) {
            super(view);
            firstName =  view.findViewById(R.id.fName);
            profileImage =  view.findViewById(R.id.user_image);
            taskListView = view.findViewById(R.id.task_list);
            rootLayout =  view.findViewById(R.id.parent_child_single_view);
        }
    }


    public ChildrenAdapter(Activity activity, List<Child> childList, Parent parent, RelativeLayout progressBar) {
        this.childList = childList;
        this.activity = activity;
        this.parent = parent;
        this.progressBar = progressBar;
        getDashBoardData();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parent_child_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        checkDeviceResolution();

        if(childApprovalList!=null)
        {
            if( childApprovalList.get(position)==null)
                childApprovalList=new ArrayList<>();
            final Child child = childApprovalList.get(position);
            final Child childWithAllTask = childList.get(position);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.override(350,350);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.placeholder(R.drawable.default_avatar);
            requestOptions.error(R.drawable.default_avatar);

            Glide.with(activity).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL+child.getAvatar())
                    .into(holder.profileImage);
          /*  try {
                Picasso.with(activity.getApplicationContext()).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + child.getAvatar()).error(R.drawable.default_avatar).into(holder.profileImage);
            }catch (Exception e){
                Picasso.with(activity.getApplicationContext()).load(R.drawable.default_avatar).into(holder.profileImage);
            }*/
            holder.firstName.setText(child.getFirstName());
            holder.firstName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FloatingMenu(activity).fetchAvatarDimension(holder.profileImage, child, childWithAllTask, parent, AppConstant.PARENT_DASHBOARD, progressBar, null);
                }
            });

            holder.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FloatingMenu(activity).fetchAvatarDimension(holder.profileImage, child, childWithAllTask, parent, AppConstant.PARENT_DASHBOARD, progressBar,null);
                }
            });

            //TaskLIST
            taskAdapter = new TaskAdapter(activity, child.getTasksArrayList(), child, childWithAllTask, parent);
            holder.taskListView.setAdapter(taskAdapter);
            holder.taskListView.setDivider(null);
        }




    }

    public void closeFebMenu(){
        if(isOpen && lastMenu!=null){
            lastMenu.close(true);
            isOpen = false;
        }
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public void checkDeviceResolution(){
        switch (activity.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                Utils.logDebug(TAG, "dpi : ldpi");
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                Utils.logDebug(TAG, "dpi : mdpi");
                break;
            case DisplayMetrics.DENSITY_HIGH:
                Utils.logDebug(TAG, "dpi : hdpi");
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                FAB_ICON_SIZE = 25;
                FAB_ICON_RADIUS = 190;


                FAB_ICON_DIMEN = activity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);;
                Utils.logDebug(TAG, "dpi : xhdpi");
                break;
            case DisplayMetrics.DENSITY_XXHIGH :
                FAB_ICON_SIZE = 25;
                FAB_ICON_RADIUS = 300;
                FAB_ICON_DIMEN = activity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);;
                Utils.logDebug(TAG, "dpi : xxhdpi");
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                FAB_ICON_SIZE = 30;
                FAB_ICON_RADIUS = 380;
                FAB_ICON_DIMEN = activity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);;
                Utils.logDebug(TAG, "dpi : xxxhdpi");
                break;
            case DisplayMetrics.DENSITY_420:
                FAB_ICON_SIZE = 30;
                FAB_ICON_RADIUS = 270;
                FAB_ICON_DIMEN = activity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);;
                Utils.logDebug(TAG, "dpi : 420dpi");
                break;
            case DisplayMetrics.DENSITY_560:
                FAB_ICON_SIZE = 30;
                FAB_ICON_RADIUS = 310;
                FAB_ICON_DIMEN = activity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);;
                Utils.logDebug(TAG, "dpi : 560dpi");
                break;
        }
    }
    private void getDashBoardData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(parent.getEmail(), parent.getPassword());
        client.setMaxRetriesAndTimeout(3,3000);

        Utils.logDebug(TAG, "Child response request: "+AppConstant.BASE_URL + AppConstant.CHILDREN_API + parent.getId());
        client.get(AppConstant.BASE_URL + AppConstant.CHILDREN_API + parent.getAccount().getId(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Utils.logDebug(TAG, "Child Success response: "+response.toString());

                Log.d("Child Respo", AppConstant.BASE_URL + AppConstant.CHILDREN_API);

                childList= new ArrayList<>();
                childApprovalList= new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject childObject = response.getJSONObject(i);

                        //child with non-approval task
                        Child child = new GetObjectFromResponse().getChildObject(childObject);

                        //child with approval task
                        Child childApprovalTask = new GetObjectFromResponse().getChildObject(childObject);
                        ArrayList<Tasks> taskList = new ArrayList<>();
                        ArrayList<Tasks> taskApprovalList = new ArrayList<>();

                        JSONArray taskArray = response.getJSONObject(i).getJSONArray(AppConstant.TASKS);
                        for (int taskIndex = 0; taskIndex < taskArray.length(); taskIndex++) {
                            JSONObject taskObject = taskArray.getJSONObject(taskIndex);
                            if(!taskObject.getString(AppConstant.STATUS).equals(AppConstant.APPROVED)){
                                Tasks task = new GetObjectFromResponse().getTaskObject(taskObject,childObject.getInt(AppConstant.ID));

                                taskList.add(task);
                            }

                            if(taskObject.getString(AppConstant.STATUS).equals(AppConstant.COMPLETED)){
                                Tasks task = new GetObjectFromResponse().getTaskObject(taskObject,childObject.getInt(AppConstant.ID));

                                taskApprovalList.add(task);
                            }
                        }
                        child.setTasksArrayList(taskList);
                        childApprovalTask.setTasksArrayList(taskApprovalList);
                        childList.add(child);
                        childApprovalList.add(childApprovalTask);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utils.logDebug(TAG, "Child error response: "+ throwable.getLocalizedMessage());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Utils.logDebug(TAG, "Child JSONObject response" + response.toString());

            }

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }



}