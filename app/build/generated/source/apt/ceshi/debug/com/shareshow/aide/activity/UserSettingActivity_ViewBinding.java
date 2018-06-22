// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UserSettingActivity_ViewBinding implements Unbinder {
  private UserSettingActivity target;

  private View view2131297033;

  private View view2131297031;

  @UiThread
  public UserSettingActivity_ViewBinding(UserSettingActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public UserSettingActivity_ViewBinding(final UserSettingActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.user_phone, "field 'user_phone' and method 'updatePhone'");
    target.user_phone = Utils.castView(view, R.id.user_phone, "field 'user_phone'", TextView.class);
    view2131297033 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.updatePhone();
      }
    });
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    view = Utils.findRequiredView(source, R.id.user_edit, "field 'edit' and method 'edit'");
    target.edit = Utils.castView(view, R.id.user_edit, "field 'edit'", Button.class);
    view2131297031 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.edit();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    UserSettingActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.user_phone = null;
    target.toolbar = null;
    target.edit = null;

    view2131297033.setOnClickListener(null);
    view2131297033 = null;
    view2131297031.setOnClickListener(null);
    view2131297031 = null;
  }
}
