// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.dialog;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BindingSuccessDialog_ViewBinding implements Unbinder {
  private BindingSuccessDialog target;

  private View view2131297004;

  @UiThread
  public BindingSuccessDialog_ViewBinding(final BindingSuccessDialog target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.tv_roger, "method 'tvRoger'");
    view2131297004 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.tvRoger();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    target = null;


    view2131297004.setOnClickListener(null);
    view2131297004 = null;
  }
}
