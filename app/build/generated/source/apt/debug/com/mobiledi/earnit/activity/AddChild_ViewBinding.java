// Generated code from Butter Knife. Do not modify!
package com.mobiledi.earnit.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mobiledi.earnit.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AddChild_ViewBinding implements Unbinder {
  private AddChild target;

  @UiThread
  public AddChild_ViewBinding(AddChild target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AddChild_ViewBinding(AddChild target, View source) {
    this.target = target;

    target.firstName = Utils.findRequiredViewAsType(source, R.id.child_first_name, "field 'firstName'", EditText.class);
    target.email = Utils.findRequiredViewAsType(source, R.id.child_email, "field 'email'", EditText.class);
    target.phone = Utils.findRequiredViewAsType(source, R.id.child_phone, "field 'phone'", EditText.class);
    target.password = Utils.findRequiredViewAsType(source, R.id.child_password, "field 'password'", EditText.class);
    target.save = Utils.findRequiredViewAsType(source, R.id.save_button, "field 'save'", Button.class);
    target.cancel = Utils.findRequiredViewAsType(source, R.id.cancel_button, "field 'cancel'", Button.class);
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.loadingPanel, "field 'progressBar'", RelativeLayout.class);
    target.childAvatar = Utils.findRequiredViewAsType(source, R.id.child_user_image, "field 'childAvatar'", CircularImageView.class);
    target.addChildHeader = Utils.findRequiredViewAsType(source, R.id.add_child_header_id, "field 'addChildHeader'", TextView.class);
    target.ivBackButton = Utils.findRequiredViewAsType(source, R.id.ivBackArrow, "field 'ivBackButton'", ImageButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AddChild target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.firstName = null;
    target.email = null;
    target.phone = null;
    target.password = null;
    target.save = null;
    target.cancel = null;
    target.progressBar = null;
    target.childAvatar = null;
    target.addChildHeader = null;
    target.ivBackButton = null;
  }
}
