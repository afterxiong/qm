// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.dialog;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoginAbnormalDialog_ViewBinding implements Unbinder {
  private LoginAbnormalDialog target;

  private View view2131297004;

  @UiThread
  public LoginAbnormalDialog_ViewBinding(final LoginAbnormalDialog target, View source) {
    this.target = target;

    View view;
    target.tvAbnorml = Utils.findRequiredViewAsType(source, R.id.tv_abnorml, "field 'tvAbnorml'", TextView.class);
    view = Utils.findRequiredView(source, R.id.tv_roger, "method 'tvRoger'");
    view2131297004 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.tvRoger(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginAbnormalDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tvAbnorml = null;

    view2131297004.setOnClickListener(null);
    view2131297004 = null;
  }
}
