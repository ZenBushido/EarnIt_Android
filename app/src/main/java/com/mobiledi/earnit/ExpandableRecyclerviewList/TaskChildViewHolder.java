package com.mobiledi.earnit.ExpandableRecyclerviewList;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.mobiledi.earnit.R;
import com.mobiledi.earnit.activity.ChildRequestTaskApproval;
import com.mobiledi.earnit.activity.EditTask;
import com.mobiledi.earnit.activity.ParentTaskApproval;
import com.mobiledi.earnit.libmoduleExpandable.ViewHolder.ChildViewHolder;
import com.mobiledi.earnit.model.Child;
import com.mobiledi.earnit.model.Parent;
import com.mobiledi.earnit.model.Tasks;
import com.mobiledi.earnit.utils.AppConstant;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * Created by adox on 01.02.2018..
 */

public class TaskChildViewHolder extends ChildViewHolder {
    TextView childTaskDetail, childTaskDueDate, childTaskCheckbox;
    Button childTaskStatus;
    ImageView thumbUp, right_arrow;
    LinearLayout task_details_layout, task_description_layout;
    public Tasks task;

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

    public void onBind(final Tasks currentTask, String title, final Parent parent, final Child child, final String isParentChild) {

        task = currentTask;
        childTaskDetail.setText(currentTask.getName());
        DateTime dt = new DateTime(currentTask.getDueDate());
        String toPrintDate = title.substring(title.length() - 5);
        DateTime dts = new DateTime(currentTask.getDueDate());
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("@ h:mm a");
        String toPrintDate2 = fmt2.print(dts);
        childTaskDueDate.setText(toPrintDate + toPrintDate2);
        DateTime currentDate = new DateTime();
        task_description_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isParentChild.equals("Parent") && !currentTask.getStatus().equals(AppConstant.COMPLETED)) {

                    Intent addTask = new Intent(itemView.getContext(), EditTask.class);
                    addTask.putExtra("title", "" + task.getName());
                    addTask.putExtra("ID", task.getId());
                    addTask.putExtra(AppConstant.CHILD_OBJECT, child);
                    addTask.putExtra(AppConstant.OTHER_CHILD_OBJECT, child);
                    addTask.putExtra(AppConstant.PARENT_OBJECT, parent);
                    addTask.putExtra(AppConstant.TO_EDIT, (Serializable) task);
                    addTask.putExtra(AppConstant.TASK_STATUS, AppConstant.EDIT);
                    itemView.getContext().startActivity(addTask);
                } else if (isParentChild.equals("Parent") && currentTask.getStatus().equals(AppConstant.COMPLETED)) {

                    Intent moveToTaskApproval = new Intent(itemView.getContext(), ParentTaskApproval.class);
                    // moveToTaskApproval.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    moveToTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
                    moveToTaskApproval.putExtra(AppConstant.OTHER_CHILD_OBJECT, child);
                    moveToTaskApproval.putExtra(AppConstant.PARENT_OBJECT, parent);
                    moveToTaskApproval.putExtra(AppConstant.FROM_SCREEN, AppConstant.CHECKED_IN_SCREEN);
                    moveToTaskApproval.putExtra(AppConstant.TASK_OBJECT, (Serializable) task);
                    itemView.getContext().startActivity(moveToTaskApproval);


                } else if (!currentTask.getStatus().equals(AppConstant.COMPLETED)) {
                    Intent requestTaskApproval = new Intent(itemView.getContext(), ChildRequestTaskApproval.class);
                    requestTaskApproval.putExtra(AppConstant.CHILD_OBJECT, child);
                    requestTaskApproval.putExtra(AppConstant.TASK_OBJECT, (Serializable) task);
                    //requestTaskApproval.putExtra(AppConstant.GOAL_OBJECT,  task.getGoal());

                    Log.e(TAG, "Child Object: " + child.getFirstName());
                    Log.e(TAG, "Task Object: " + task.getName());
                    Log.e(TAG, "Task Object: " + task.getDetails());
                    Log.e(TAG, "Task Object: " + task.getGoal().getGoalName());
                    Log.e(TAG, "Task Object: " + task.getGoal().getId());

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

