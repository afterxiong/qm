// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UpdatePhoneActivity_ViewBinding implements Unbinder {
  private UpdatePhoneActivity target;

  private View view2131296425;

  private View view2131297027;

  private View view2131297025;

  @UiThread
  public UpdatePhoneActivity_ViewBinding(UpdatePhoneActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public UpdatePhoneActivity_ViewBinding(final UpdatePhoneActivity target, View source) {
    this.target = target;

    View view;
    target.update_phone_tip = Utils.findRequiredViewAsType(source, R.id.update_phone_tip, "field 'update_phone_tip'", TextView.class);
    target.binding_phone = Utils.findRequiredViewAsType(source, R.id.binding_phone, "field 'binding_phone'", TextView.class);
    target.update_phone1 = Utils.findRequiredViewAsType(source, R.id.update_phone1, "field 'update_phone1'", RelativeLayout.class);
    target.update_phone2 = Utils.findRequiredViewAsType(source, R.id.update_phone2, "field 'update_phone2'", LinearLayout.class);
    target.editValue = Utils.findRequiredViewAsType(source, R.id.update_edit_phone, "field 'editValue'", EditText.class);
    view = Utils.findRequiredView(source, R.id.countdown, "field 'countdown' and method 'countdown'");
    target.countdown = Utils.castView(view, R.id.countdown, "field 'countdown'", TextView.class);
    view2131296425 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.countdown();
      }
    });
    view = Utils.findRequiredView(source, R.id.update_phone_step, "field 'update_phone_step' and method 'updateStep'");
    target.update_phone_step = Utils.castView(view, R.id.update_phone_step, "field 'update_phone_step'", Button.class);
    view2131297027 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.updateStep();
      }
    });
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    view = Utils.findRequiredView(source, R.id.update_phone_btn, "method 'updatePhone'");
    view2131297025 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.updatePhone();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    UpdatePhoneActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.update_phone_tip = null;
    target.binding_phone = null;
    target.update_phone1 = null;
    target.update_phone2 = null;
    target.editValue = null;
    target.countdown = null;
    target.update_phone_step = null;
    target.toolbar = null;

    view2131296425.setOnClickListener(null);
    view2131296425 = null;
    view2131297027.setOnClickListener(null);
    view2131297027 = null;
    view2131297025.setOnClickListener(null);
    view2131297025 = null;
  }
}
