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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class ChildViewDateAdapter extends ExpandableRecyclerAdapter<TaskGroupViewHolder, TaskChildViewHolder> {
    Parent parent;
    Child child;
    private String screenName;
    private String datedue;
    String TAG = ChildViewDateAdapter.class.getSimpleName();
    List<Tasks> tasksList = new ArrayList<>();
    public List<ChildsTaskObject> groups;

    public ChildViewDateAdapter(@NonNull List<? extends ParentListItem>  groups, Parent parent, Child child, String name) {

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
        DateTime dateTime = new DateTime(childsTaskObject.getDueDate()).withTimeAtStartOfDay();
        if (dateTime.equals(new DateTime(GetObjectFromResponse.PENDING_APPROVAL_DATE).withTimeAtStartOfDay())) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            datedue=  toPrintDate;
            taskGroupViewHolder.setDateHeader(AppConstant.NON_COMPLETED_APPROVED);
        } else if (dateTime.equals(new DateTime(GetObjectFromResponse.PAST_DUE_DATE).withTimeAtStartOfDay())) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            datedue=  toPrintDate;
            taskGroupViewHolder.setDateHeader(AppConstant.PAST_DUE);
        } else if (dateTime.equals(new DateTime().withTimeAtStartOfDay())) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            datedue=  toPrintDate;
            taskGroupViewHolder.setDateHeader("Today");
        } else {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MM/dd");
            String toPrintDate = fmt.print(dateTime);
            datedue=  toPrintDate;
            taskGroupViewHolder.setDateHeader(toPrintDate);
        }
    }

    @Override
    public void onBindChildViewHolder(TaskChildViewHolder taskChildViewHolder, int position, Object childListItem) {

        Tasks childTask = (Tasks) childListItem;
        taskChildViewHolder.onBind(childTask, datedue, parent, child, screenName);
    }
}