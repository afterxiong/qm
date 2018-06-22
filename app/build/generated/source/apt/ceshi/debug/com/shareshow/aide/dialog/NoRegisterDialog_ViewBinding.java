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

public class NoRegisterDialog_ViewBinding implements Unbinder {
  private NoRegisterDialog target;

  private View view2131296614;

  private View view2131296687;

  @UiThread
  public NoRegisterDialog_ViewBinding(final NoRegisterDialog target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.iv_scan_code, "method 'scnCode'");
    view2131296614 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.scnCode(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.next_use, "method 'nextUse'");
    view2131296687 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.nextUse(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    target = null;


    view2131296614.setOnClickListener(null);
    view2131296614 = null;
    view2131296687.setOnClickListener(null);
    view2131296687 = null;
  }
}
