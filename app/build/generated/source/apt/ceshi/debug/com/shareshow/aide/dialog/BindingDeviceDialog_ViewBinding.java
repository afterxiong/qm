// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.dialog;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BindingDeviceDialog_ViewBinding implements Unbinder {
  private BindingDeviceDialog target;

  private View view2131296363;

  private View view2131296534;

  @UiThread
  public BindingDeviceDialog_ViewBinding(final BindingDeviceDialog target, View source) {
    this.target = target;

    View view;
    target.tvOrg = Utils.findRequiredViewAsType(source, R.id.tv_org, "field 'tvOrg'", TextView.class);
    target.tvPhone = Utils.findRequiredViewAsType(source, R.id.tv_phone, "field 'tvPhone'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btn_binding, "field 'btnBinding' and method 'btnBinding'");
    target.btnBinding = Utils.castView(view, R.id.btn_binding, "field 'btnBinding'", Button.class);
    view2131296363 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnBinding();
      }
    });
    target.tvIdsTip = Utils.findRequiredViewAsType(source, R.id.tv_ids_tip, "field 'tvIdsTip'", TextView.class);
    target.tvIdsValue = Utils.findRequiredViewAsType(source, R.id.tv_ids_value, "field 'tvIdsValue'", TextView.class);
    view = Utils.findRequiredView(source, R.id.gain_code, "field 'gainCode' and method 'gainCode'");
    target.gainCode = Utils.castView(view, R.id.gain_code, "field 'gainCode'", TextView.class);
    view2131296534 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.gainCode();
      }
    });
    target.edCode = Utils.findRequiredViewAsType(source, R.id.test_code_value, "field 'edCode'", EditText.class);
    target.testCodeLayout = Utils.findRequiredViewAsType(source, R.id.test_code_layout, "field 'testCodeLayout'", LinearLayout.class);
    target.testCodeTip = Utils.findRequiredViewAsType(source, R.id.test_code_tip, "field 'testCodeTip'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BindingDeviceDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tvOrg = null;
    target.tvPhone = null;
    target.btnBinding = null;
    target.tvIdsTip = null;
    target.tvIdsValue = null;
    target.gainCode = null;
    target.edCode = null;
    target.testCodeLayout = null;
    target.testCodeTip = null;

    view2131296363.setOnClickListener(null);
    view2131296363 = null;
    view2131296534.setOnClickListener(null);
    view2131296534 = null;
  }
}
