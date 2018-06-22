// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MouseControl_ViewBinding implements Unbinder {
  private MouseControl target;

  @UiThread
  public MouseControl_ViewBinding(MouseControl target, View source) {
    this.target = target;

    target.mouseFy = Utils.findRequiredViewAsType(source, R.id.mouse_fy, "field 'mouseFy'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MouseControl target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mouseFy = null;
  }
}
