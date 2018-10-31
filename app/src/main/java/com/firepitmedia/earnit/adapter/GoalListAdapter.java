package com.firepitmedia.earnit.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firepitmedia.earnit.AppLockConstants;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.activity.EditGoalActivity;
import com.firepitmedia.earnit.activity.GoalActivity;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Goal;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.model.Tasks;
import com.firepitmedia.earnit.utils.AppConstant;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ashishkumar on 30/12/17.
 */

public class GoalListAdapter extends RecyclerView.Adapter<GoalListAdapter.GoalListViewHolder> {

    private GoalActivity context;
    private List<Goal> listGoal;
    private Parent parentObject;
    Child childObject;
    Child otherChild;
    Tasks tasks;
    String goalMode;
    String fromScreen;
    public GoalListAdapter(GoalActivity context, List<Goal> listGoal,Parent parentObject, Child childObject, Child otherChild, Tasks tasks, String goalMode, String fromScreen) {
        this.listGoal = listGoal;
        this.context = context;
        this.parentObject = parentObject;
        this.childObject = childObject;
        this.otherChild = otherChild;
        this.tasks = tasks;
        this.goalMode = goalMode;
        this.fromScreen = fromScreen;
    }

    @Override
    public GoalListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_child_goallist, parent, false);
        return new GoalListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalListViewHolder holder, int position) {
        final Goal goal = listGoal.get(position);

        int goalAmount = goal.getAmount();
        String goalName = goal.getGoalName();

        holder.tvGoalName.setText(goalName);
        holder.tvGoalValue.setText("$" + goalAmount);
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteGoalDialog(goal.getId());
            }
        });
        holder.tvGoalName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdittextOpen(goal);
            }
        });
        holder.tvGoalValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdittextOpen(goal);
            }
        });

    }

    private void EdittextOpen(Goal goal) {
        Intent intent = new Intent(context, EditGoalActivity.class).putExtra(AppLockConstants.GoalEdit, goal);
         intent.putExtra(AppConstant.PARENT_OBJECT, parentObject);
         intent.putExtra(AppConstant.CHILD_OBJECT,childObject);
         intent.putExtra(AppConstant.OTHER_CHILD_OBJECT,otherChild);
        intent.putExtra(AppConstant.TO_EDIT, (Serializable) tasks);
        intent.putExtra(AppConstant.MODE,goalMode);
         intent.putExtra(AppConstant.FROM_SCREEN,fromScreen);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return listGoal.size();
    }

    static class GoalListViewHolder extends RecyclerView.ViewHolder {

        TextView tvGoalName, tvGoalValue;
        ImageButton ibDelete;

        GoalListViewHolder(final View itemView) {
            super(itemView);

            tvGoalName = (TextView) itemView.findViewById(R.id.tvGoalName);
            tvGoalValue = (TextView) itemView.findViewById(R.id.tvGoalValue);

            ibDelete = (ImageButton) itemView.findViewById(R.id.ibDelete);


        }
    }

    public void deleteGoalDialog(final int id) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this goal")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        context.deleteGoal(id);
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

}
