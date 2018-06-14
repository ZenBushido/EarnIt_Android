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
//    private List<Child> childApprovalList;
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
//        getDashBoardData();

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
        final Child child = childList.get(position);
//        if(childApprovalList!=null)
//        {
//            if( childApprovalList.get(position)==null)
//                childApprovalList=new ArrayList<>();
//            final Child child = childApprovalList.get(position);
//            final Child childWithAllTask = childList.get(position);
//            Log.e(TAG, "ID: : "+child.getId());
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
            holder.firstName.setOnClickListener(onChildClick(holder, child));
            holder.profileImage.setOnClickListener(onChildClick(holder, child));
            //TaskLIST
            taskAdapter = new TaskAdapter(activity, child.getTasksArrayList(), child, child, parent);
            holder.taskListView.setAdapter(taskAdapter);
            holder.taskListView.setDivider(null);
    }

    private View.OnClickListener onChildClick(final MyViewHolder holder, final Child child){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("jdsahdkjh", "Open FloatingMenu. Child: " + child);
                new FloatingMenu(activity).fetchAvatarDimension(holder.profileImage, child, child, parent, AppConstant.PARENT_DASHBOARD, progressBar,null);
            }
        };
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



}