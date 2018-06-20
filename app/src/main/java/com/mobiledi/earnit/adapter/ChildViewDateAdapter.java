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
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;
import com.mobiledi.earnit.utils.GetObjectFromResponse;
import com.mobiledi.earnit.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChildViewDateAdapter extends ExpandableRecyclerAdapter<TaskGroupViewHolder, TaskChildViewHolder> {
    Parent parent;
    Child child;
    private String screenName;
//    private String datedue;
    String TAG = ChildViewDateAdapter.class.getSimpleName();
    List<Tasks> tasksList = new ArrayList<>();
    public List<ChildsTaskObject> groups;
    private DateTimeFormatter fmt;

    public ChildViewDateAdapter(@NonNull List<? extends ParentListItem> groups, Parent parent, Child child, String name) {
        super(groups);
        Log.d("dasfgrrs", "ChildViewDateAdapter");
        this.parent = parent;
        this.child = child;
        screenName = name;
        fmt = DateTimeFormat.forPattern("EEE MM/dd");

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
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-ddTHH:mm:ss.SSS+Z").withLocale(Locale.US);
//        DateTime dt = formatter.parseDateTime(childsTaskObject.getDueDate());
        DateTime dateTime = new DateTime(childsTaskObject.getDueDate()).withTimeAtStartOfDay();
        Log.d("dasfgrrsdsds", "ChildsTaskObject: " + dateTime.toString());
        if (dateTime.isEqual(new DateTime(Tasks.fakeDate).withTimeAtStartOfDay())) {
            Log.d("fsdkjhfkj", "NON_COMPLETED_APPROVED: " + dateTime.toString("dd.MM.hh HH:mm:ss"));
            taskGroupViewHolder.setDateHeader(AppConstant.NON_COMPLETED_APPROVED);
        } else if (isToday(dateTime)) {
            Log.d("fsdkjhfkj", "Today: " + dateTime.toString("dd.MM.hh HH:mm:ss"));
            taskGroupViewHolder.setDateHeader("Today");
        } else if (dateTime.isBefore(new DateTime().withTimeAtStartOfDay())) {
            Log.d("fsdkjhfkj", "PAST_DUE: " + dateTime.toString("dd.MM.hh HH:mm:ss"));
            taskGroupViewHolder.setDateHeader(AppConstant.PAST_DUE);
        } else {
            String toPrintDate = fmt.print(dateTime);
            Log.d("fsdkjhfkj", "Today: " + dateTime.toString("dd.MM.hh HH:mm:ss") + " toPrintDate: " + toPrintDate);
            taskGroupViewHolder.setDateHeader(toPrintDate);
        }
    }

    private boolean isToday(DateTime first){
        DateTime today = new DateTime();
        return first.getYear() == today.getYear() && first.getMonthOfYear() == today.getMonthOfYear() && first.getDayOfMonth() == today.getDayOfMonth();
    }

    @Override
    public void onBindChildViewHolder(TaskChildViewHolder taskChildViewHolder, int position, Object childListItem) {
        Tasks childTask = (Tasks) childListItem;
        Log.d("dasagsdg", "childTask: " + childTask);
        Utils.logDebug(TAG, "&!!! Task == " + childTask);
        taskChildViewHolder.onBind(childTask, childTask.getDueDate(), parent, child, screenName);
    }
}