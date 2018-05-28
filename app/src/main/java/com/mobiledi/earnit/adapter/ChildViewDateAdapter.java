package com.mobiledi.earnit.adapter;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiledi.earnit.ExpandableRecyclerviewList.TaskChildViewHolder;
import com.mobiledi.earnit.ExpandableRecyclerviewList.TaskGroupViewHolder;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.libmoduleExpandable.Adapter.ExpandableRecyclerAdapter;
import com.mobiledi.earnit.libmoduleExpandable.Model.ParentListItem;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.ChildsTaskObject;
import com.mobiledi.earnit.model.DayTaskStatus;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChildViewDateAdapter extends ExpandableRecyclerAdapter<TaskGroupViewHolder, TaskChildViewHolder> {
    Parent parent;
    Child child;
    private String screenName;
//    private String datedue;
    private long dateLong;
    String TAG = ChildViewDateAdapter.class.getSimpleName();
    List<Tasks> tasksList = new ArrayList<>();
    public List<ChildsTaskObject> groups;

    public ChildViewDateAdapter(@NonNull List<? extends ParentListItem> groups, Parent parent, Child child, String name) {
        super(groups);
        this.parent = parent;
        this.child = child;
        screenName = name;

    }


    @Override
    public TaskGroupViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = LayoutInflater.from(parentViewGroup.getContext()).inflate(R.layout.detail_task_view_header, parentViewGroup, false);
        return new TaskGroupViewHolder(view);
    }

    @Override
    public TaskChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = LayoutInflater.from(childViewGroup.getContext()).inflate(R.layout.single_task_view_detail, childViewGroup, false);
        return new TaskChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(TaskGroupViewHolder taskGroupViewHolder, int position, ParentListItem parentListItem) {
        ChildsTaskObject childsTaskObject = (ChildsTaskObject) parentListItem;
//        setTasksStartDate(childsTaskObject);
        Log.d("dasfgrrs", "ChildsTaskObject: " + childsTaskObject.toString());
        DateTime dateTime = new DateTime(childsTaskObject.getDueDate()).withTimeAtStartOfDay();
        dateLong = dateTime.getMillis();
        if (dateTime.isEqual(Tasks.fakeDate)) {
            Log.d("fsdkjhfkj", "PENDING_APPROVAL_DATE");
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
//            datedue = toPrintDate;
            taskGroupViewHolder.setDateHeader(AppConstant.NON_COMPLETED_APPROVED);
        } else if (dateTime.equals(new DateTime(GetObjectFromResponse.PAST_DUE_DATE).withTimeAtStartOfDay())) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            Log.d("fsdkjhfkj", "NON_COMPLETED_APPROVED: " + toPrintDate);
//            datedue = toPrintDate;
            taskGroupViewHolder.setDateHeader(AppConstant.PAST_DUE);
        } else if (dateTime.equals(new DateTime().withTimeAtStartOfDay())) {
            Log.d("fsdkjhfkj", "PAST_DUE");
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
//            datedue = toPrintDate;
            taskGroupViewHolder.setDateHeader("Today");
        } else {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            Log.d("fsdkjhfkj", "else: " + toPrintDate);
//            datedue = toPrintDate;
            taskGroupViewHolder.setDateHeader(toPrintDate);
        }
    }

    private void setTasksStartDate(ChildsTaskObject childsTaskObject){
        List<Tasks> tasks = childsTaskObject.getTasks();
        for (Tasks task : tasks){
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DateTime dateTime = dtf.parseDateTime(childsTaskObject.getDueDate());
            task.setStartDate(dateTime.getMillis());
        }
    }

    @Override
    public void onBindChildViewHolder(TaskChildViewHolder taskChildViewHolder, int position, Object childListItem) {
        Tasks childTask = (Tasks) childListItem;
        Log.d("dasagsdg", "childTask: " + childTask);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
        String toPrintDate = fmt.print(childTask.getDueDate());
        Log.d("fsdkjhfkj", "else: " + toPrintDate);
//        Log.d("fsdkjhfkj", "everyNRepeat: " + childTask.getRepititionSchedule().getEveryNRepeat());
        Log.e(TAG, "Child Task= " + childTask.getName());
        Log.e(TAG, "Child Task= " + childTask.getStatus());
        //        Log.e(TAG, "Child Task= "+childTask.getGoal().getId());
        //     Log.e(TAG, "Child Task= "+childTask.getGoal().getGoalName())
        Utils.logDebug(TAG, "&!!! Task == " + childTask);
        taskChildViewHolder.onBind(childTask, dateLong, parent, child, screenName);
    }
}