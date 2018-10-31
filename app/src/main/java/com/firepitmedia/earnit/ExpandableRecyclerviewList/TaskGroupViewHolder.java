package com.firepitmedia.earnit.ExpandableRecyclerviewList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.libmoduleExpandable.ViewHolder.ParentViewHolder;


public class TaskGroupViewHolder extends ParentViewHolder {

    public TextView dateHeader;
    public ImageView expendCollapse;
    public TextView expendCollapseStatus;
    public boolean expanded = false;

    public void setDateHeader(String dateHeader) {
        this.dateHeader.setText(dateHeader);

    }


    public TaskGroupViewHolder(View view) {
        super(view);
        dateHeader = (TextView) view.findViewById(R.id.dateHeader);
        expendCollapse = (ImageView) view.findViewById(R.id.expend_collapse_id);
        expendCollapse.setImageDrawable((new IconDrawable(itemView.getContext(), FontAwesomeIcons.fa_angle_down)
                .colorRes(R.color.main_font).sizeDp(30)));
        expendCollapseStatus = (TextView) view.findViewById(R.id.expent_collapse_status_id);

    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (expanded) {
            expendCollapse.setImageDrawable((new IconDrawable(itemView.getContext(), FontAwesomeIcons.fa_angle_down)
                    .colorRes(R.color.main_font).sizeDp(30)));
        } else {
            expendCollapse.setImageDrawable((new IconDrawable(itemView.getContext(), FontAwesomeIcons.fa_angle_right)
                    .colorRes(R.color.main_font).sizeDp(30)));
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
    }
}
