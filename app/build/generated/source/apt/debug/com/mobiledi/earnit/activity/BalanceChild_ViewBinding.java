// Generated code from Butter Knife. Do not modify!
package com.mobiledi.earnit.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BalanceChild_ViewBinding implements Unbinder {
  private BalanceChild target;

  @UiThread
  public BalanceChild_ViewBinding(BalanceChild target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public BalanceChild_ViewBinding(BalanceChild target, View source) {
    this.target = target;

    target.headerBalance = Utils.findRequiredViewAsType(source, R.id.goal_header, "field 'headerBalance'", TextView.class);
    target.avatar = Utils.findRequiredViewAsType(source, R.id.goal_avatar, "field 'avatar'", CircularImageView.class);
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.loadingPanel, "field 'progressBar'", RelativeLayout.class);
    target.addToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar_goal, "field 'addToolbar'", Toolbar.class);
    target.drawerToggle = Utils.findRequiredViewAsType(source, R.id.drawerBtn, "field 'drawerToggle'", ImageButton.class);
    target.save_btn = Utils.findRequiredViewAsType(source, R.id.save, "field 'save_btn'", Button.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.balanceChildList, "field 'recyclerView'", RecyclerView.class);
    target.totalBalance = Utils.findRequiredViewAsType(source, R.id.totalbalance, "field 'totalBalance'", TextView.class);
    target.totalGoal = Utils.findRequiredViewAsType(source, R.id.tv_goal_amount, "field 'totalGoal'", TextView.class);
    target.back = Utils.findRequiredViewAsType(source, R.id.addtask_back_arrow, "field 'back'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BalanceChild target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.headerBalance = null;
    target.avatar = null;
    target.progressBar = null;
    target.addToolbar = null;
    target.drawerToggle = null;
    target.save_btn = null;
    target.recyclerView = null;
    target.totalBalance = null;
    target.totalGoal = null;
    target.back = null;
  }
}
