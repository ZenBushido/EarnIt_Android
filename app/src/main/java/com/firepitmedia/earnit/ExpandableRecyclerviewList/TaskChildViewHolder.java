package com.firepitmedia.earnit.ExpandableRecyclerviewList;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.activity.ChildRequestTaskApproval;
import com.firepitmedia.earnit.activity.EditTask;
import com.firepitmedia.earnit.activity.ParentTaskApproval;
import com.firepitmedia.earnit.libmoduleExpandable.ViewHolder.ChildViewHolder;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Goal;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.Utils;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by adox on 01.02.2018..
 */

public class TaskChildViewHolder extends ChildViewHolder {
    private TextView childTaskDetail, childTaskDueDate, childTaskCheckbox;
    private Button childTaskStatus;
    private ImageView thumbUp, right_arrow;
    private LinearLayout task_details_layout, task_description_layout;
    public Goal goal;

    String TAG = TaskChildViewHolder.class.getSimpleName();

    public TaskChildViewHolder(View item) {
        super(item);
        childTaskDetail = (TextView) item.findViewById(R.id.child_task_detail);
        childTaskDueDate = (TextView) item.findViewById(R.id.child_task_due);
        childTaskCheckbox = (TextView) item.findViewById(R.id.child_task_checkbox);
        childTaskStatus = (Button) item.findViewById(R.id.child_task_staus);
        thumbUp = (ImageView) item.findViewById(R.id.thumb_up_id);
        right_arrow = (ImageView) item.findViewById(R.id.child_task_right);
        task_details_layout = (LinearLayout) item.findViewById(R.id.task_details_layout);
        task_description_layout = (LinearLayout) item.findViewById(R.id.task_description_layout);
    }

    public void onBind(final Tasks currentTask, final long title, final Parent parent, final Child child, final String isParentChild) {
        Utils.logDebug("sadakjh", "!!!! Task == " + currentTask.toString());
        childTaskDetail.setText(currentTask.getName());
        childTaskDueDate.setText(new DateTime(currentTask.getDueDate()).toString("MM/dd@ hh:mm aa", Locale.US));
        DateTime currentDate = new DateTime();
        task_description_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isParentChild.equals("Parent") && !currentTask.getStatus().equals(AppConstant.COMPLETED)) {

                    Intent addTask = new Intent(itemView.getContext(), EditTask.class);
                    addTask.putExtra("title", "" + currentTask.getName());
                    addTask.putExtra("ID", currentTask.getId());
                    addTask.putExtra(AppConstant.CHILD_OBJECT, child);
                    addTask.putExtra(AppConstant.OTHER_CHILD_OBJECT, child);
                    addTask.putExtra(AppConstant.PARENT_OBJECT, parent);
                    addTask.putExtra(AppConstant.TO_EDIT, (Serializable) currentTask);
                    addTask.putExtra(AppConstant.REPITITION_SCHEDULE, currentTask.getRepititionSchedule());
                    Utils.logDebug(TAG, "1 Task == " + currentTask);
                    addTask.putExtra(AppConstant.GOAL_OBJECT, (Serializable) currentTask.getGoal() );
                    addTask.putExtra(AppConstant.TASK_STATUS, AppConstant.EDIT);
                    itemView.getContext().startActivity(addTask);
                } else if (isParentChild.equals("Parent") && currentTask.getStatus().equals(AppConstant.COMPLETED)) {
                    Intent moveToTaskApproval = new Intent(itemView.getContext(), ParentTaskApproval.class);
                    // moveToTaskApproval.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    moveToTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
                    moveToTaskApproval.putExtra(AppConstant.OTHER_CHILD_OBJECT, child);
                    moveToTaskApproval.putExtra(AppConstant.PARENT_OBJECT, (Serializable)parent);
                    moveToTaskApproval.putExtra(AppConstant.FROM_SCREEN, AppConstant.CHECKED_IN_SCREEN);
                    moveToTaskApproval.putExtra(AppConstant.TASK_OBJECT, (Serializable) currentTask);
                    moveToTaskApproval.putExtra(AppConstant.REPITITION_SCHEDULE, currentTask.getRepititionSchedule());
                    Utils.logDebug(TAG, "2 Task == " + currentTask);
                    if(currentTask.getTaskComments()!=null)
                    moveToTaskApproval.putExtra(AppConstant.TASK_COMMENTS, (Serializable) currentTask.getTaskComments().get(0));
                    itemView.getContext().startActivity(moveToTaskApproval);


                } else if (!currentTask.getStatus().equals(AppConstant.COMPLETED)) {
                    Intent requestTaskApproval = new Intent(itemView.getContext(), ChildRequestTaskApproval.class);
                    requestTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
                    requestTaskApproval.putExtra(AppConstant.TASK_OBJECT, (Serializable) currentTask);
                    Utils.logDebug(TAG, "3! Task == " + currentTask);
                    requestTaskApproval.putExtra(AppConstant.GOAL_OBJECT, currentTask.getGoal());
                    requestTaskApproval.putExtra(AppConstant.REPETITION_SCHEDULE, currentTask.getRepititionSchedule());
                    requestTaskApproval.putExtra(AppConstant.PARENT_OBJECT, parent);
                    requestTaskApproval.putExtra(AppConstant.DUE_DATE_STRING, title);




                    itemView.getContext().startActivity(requestTaskApproval);

                } else {
                    Toast.makeText(task_description_layout.getContext(), "Requested already", Toast.LENGTH_LONG).show();

                }
            }
        });
        //   if (userType.equals(AppConstant.CHILD)) {
        //     mViewHolder.childTaskCheckbox.setVisibility(View.GONE);
        //   mViewHolder.thumbUp.setVisibility(View.GONE);
        //   } else {
        childTaskCheckbox.setVisibility(View.GONE);
        thumbUp.setBackground(new IconDrawable(itemView.getContext(), FontAwesomeIcons.fa_eye)
                .colorRes(R.color.main_font)
                .actionBarSize());
        right_arrow.setVisibility(View.INVISIBLE);
        if (new DateTime(currentTask.getDueDate()).withTimeAtStartOfDay().equals(currentDate.withTimeAtStartOfDay())) {
            if (currentTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                childTaskStatus.setBackgroundResource(R.drawable.completed_status);
                //  if (userType.equals(AppConstant.PARENT))
                thumbUp.setVisibility(View.VISIBLE);
            } else {
                if (currentDate.withTimeAtStartOfDay().isAfter(new DateTime(currentTask.getDueDate()).withTimeAtStartOfDay())) {
                    childTaskStatus.setBackgroundResource(R.drawable.pink_status);
                    // if (userType.equals(AppConstant.PARENT))
                    //       mViewHolder.thumbUp.setVisibility(View.GONE);
                } else {
                    childTaskStatus.setBackgroundResource(R.drawable.yellow_status);
                    //   if (userType.equals(AppConstant.PARENT))
                    //       mViewHolder.thumbUp.setVisibility(View.GONE);
                }
            }
        } else if (currentDate.isAfter(currentTask.getDueDate())) {
            if (currentTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                childTaskStatus.setBackgroundResource(R.drawable.completed_status);
                //   if (userType.equals(AppConstant.PARENT))
                //     mViewHolder.thumbUp.setVisibility(View.VISIBLE);
            } else {
                childTaskStatus.setBackgroundResource(R.drawable.pink_status);
                // if (userType.equals(AppConstant.PARENT))
                //   mViewHolder.thumbUp.setVisibility(View.GONE);
            }
        } else {
            if (currentTask.getStatus().equalsIgnoreCase(AppConstant.COMPLETED)) {
                childTaskStatus.setBackgroundResource(R.drawable.completed_status);
                //  if (userType.equals(AppConstant.PARENT))
                //    mViewHolder.thumbUp.setVisibility(View.VISIBLE);
            } else {
                childTaskStatus.setBackgroundResource(R.drawable.gray_status);
                //if (userType.equals(AppConstant.PARENT))
                //  mViewHolder.thumbUp.setVisibility(View.GONE);

            }
            thumbUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // callTaskApproval(currentTask);

                }
            });
        }
    }

}

