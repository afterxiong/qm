// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ControlActivity_ViewBinding implements Unbinder {
  private ControlActivity target;

  private View view2131296341;

  private View view2131296807;

  private View view2131296668;

  @UiThread
  public ControlActivity_ViewBinding(ControlActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ControlActivity_ViewBinding(final ControlActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.back, "field 'back' and method 'back'");
    target.back = Utils.castView(view, R.id.back, "field 'back'", LinearLayout.class);
    view2131296341 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.back(p0);
      }
    });
    target.text = Utils.findRequiredViewAsType(source, R.id.text, "field 'text'", TextView.class);
    target.remoutImg = Utils.findRequiredViewAsType(source, R.id.remout_img, "field 'remoutImg'", ImageView.class);
    target.remoutText = Utils.findRequiredViewAsType(source, R.id.remout_text, "field 'remoutText'", TextView.class);
    target.mouseImg = Utils.findRequiredViewAsType(source, R.id.mouse_img, "field 'mouseImg'", ImageView.class);
    target.mouseText = Utils.findRequiredViewAsType(source, R.id.mouse_text, "field 'mouseText'", TextView.class);
    view = Utils.findRequiredView(source, R.id.remount, "field 'remount' and method 'remout'");
    target.remount = Utils.castView(view, R.id.remount, "field 'remount'", LinearLayout.class);
    view2131296807 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.remout();
      }
    });
    view = Utils.findRequiredView(source, R.id.mouse, "method 'mouse'");
    view2131296668 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.mouse();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    ControlActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.back = null;
    target.text = null;
    target.remoutImg = null;
    target.remoutText = null;
    target.mouseImg = null;
    target.mouseText = null;
    target.remount = null;

    view2131296341.setOnClickListener(null);
    view2131296341 = null;
    view2131296807.setOnClickListener(null);
    view2131296807 = null;
    view2131296668.setOnClickListener(null);
    view2131296668 = null;
  }
}
