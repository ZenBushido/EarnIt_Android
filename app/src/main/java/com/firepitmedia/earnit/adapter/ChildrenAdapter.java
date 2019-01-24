package com.firepitmedia.earnit.adapter;

/**
 * Created by praks on 07/07/17.
 */

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firepitmedia.earnit.model.DayTaskStatus;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.customcontrol.ExpandableHeightListView;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.FloatingMenu;
import com.firepitmedia.earnit.utils.Utils;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.extras.Base64;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;


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
        public TextView firstName,pastdue_txt,pending_approvel_txt;
        public CircularImageView profileImage;
        public ExpandableHeightListView taskListView;
        public LinearLayout rootLayout,tasklist_lyt;
        ImageView dot_img;


        public MyViewHolder(View view) {
            super(view);
            firstName =  view.findViewById(R.id.fName);
            profileImage =  view.findViewById(R.id.user_image);
            taskListView = view.findViewById(R.id.task_list);
            rootLayout =  view.findViewById(R.id.parent_child_single_view);
            tasklist_lyt= view.findViewById(R.id.tasklist_lyt);
            dot_img= view.findViewById(R.id.dot_img);
            pastdue_txt= view.findViewById(R.id.pastdue_txt);
            pending_approvel_txt= view.findViewById(R.id.pending_approvel_txt);
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
                .inflate(R.layout.parent_child_view_new, parent, false);

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
            requestOptions.placeholder(R.drawable.icon);
            requestOptions.error(R.drawable.icon);

//            Glide.with(activity).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL+child.getAvatar())
//                    .into(holder.profileImage);
            updateAvatar(child, holder.profileImage);
          /*  try {
                Picasso.with(activity.getApplicationContext()).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + child.getAvatar()).error(R.drawable.default_avatar).into(holder.profileImage);
            }catch (Exception e){
                Picasso.with(activity.getApplicationContext()).load(R.drawable.default_avatar).into(holder.profileImage);
            }*/
            holder.firstName.setText(child.getFirstName());
            holder.firstName.setOnClickListener(onChildClick(holder, child));
            holder.dot_img.setOnClickListener(onChildClick(holder, child));
            //TaskLIST
        if(child.getTasksArrayList().size()>0) {
            holder.tasklist_lyt.setVisibility(View.GONE);
            holder.pastdue_txt.setText(""+child.getTasksArrayList().size());
        }
        else {
            holder.tasklist_lyt.setVisibility(View.GONE);
            holder.pastdue_txt.setText("None");
        }

        getApprovalCount(holder.pending_approvel_txt, child);

            taskAdapter = new TaskAdapter(activity, sortingTasks(child.getTasksArrayList()), child, child, parent);
            holder.taskListView.setAdapter(taskAdapter);
            holder.taskListView.setDivider(null);
    }

    private void updateAvatar(Child child, ImageView imageView) {
        String url = AppConstant.BASE_URL + "/" + child.getAvatar();
        Log.d("fsdfhkj", "list updateAvatar. url = " + url);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        String emailPassword = MyApplication.getInstance().getEmail() + ":" + MyApplication.getInstance().getPassword();
                        String basic = "Basic " + Base64.encodeToString(emailPassword.getBytes(), Base64.NO_WRAP);
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", basic)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(activity)
                .downloader(new OkHttp3Downloader(client))
                .build();
        picasso
                .load(url)
                .error(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.icon)))
                .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.icon)))
                .into(imageView);
    }

    private ArrayList<Tasks> sortingTasks(ArrayList<Tasks> inputTasks){
        Collections.sort(inputTasks, new CustomComparator());
        return inputTasks;
    }

    public class CustomComparator implements Comparator<Tasks> {
        @Override
        public int compare(Tasks o1, Tasks o2) {
            Long dueDate1 = o1.getDueDate();
            Long dueDate2 = o2.getDueDate();
            return dueDate2.compareTo(dueDate1);
        }
    }

    private View.OnClickListener onChildClick(final MyViewHolder holder, final Child child){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("jdsahdkjh", "Open FloatingMenu. Child: " + child);
                new FloatingMenu(activity).fetchAvatarDimension(holder.dot_img, child, child, parent, AppConstant.PARENT_DASHBOARD, progressBar,null);
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

    private void getApprovalCount(TextView txt, Child child){
        int i = 0;
        for (Tasks tasks1 : child.getTasksArrayList()) {
            Utils.logDebug(TAG, "taskl: " + tasks1.toString());
            if  (tasks1.getRepititionSchedule() != null && tasks1.getRepititionSchedule().getDayTaskStatuses() != null){
                List<DayTaskStatus> dayTaskStatuses = tasks1.getRepititionSchedule().getDayTaskStatuses();
                for (DayTaskStatus dayTaskStatus : dayTaskStatuses){
                    if (dayTaskStatus.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)){
                        i ++;
                    }
                }
            } else if (tasks1.getStatus().equalsIgnoreCase("Completed")) {
                i++;
            }


        }
        if(i==0)
            txt.setText("None");
        else
        txt.setText(""+i);

    }



}