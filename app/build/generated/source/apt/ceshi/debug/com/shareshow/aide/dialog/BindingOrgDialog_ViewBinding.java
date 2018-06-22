// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.dialog;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BindingOrgDialog_ViewBinding implements Unbinder {
  private BindingOrgDialog target;

  private View view2131296990;

  @UiThread
  public BindingOrgDialog_ViewBinding(final BindingOrgDialog target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.tv_binding_compile, "field 'bindingCompile' and method 'bindingCompile'");
    target.bindingCompile = Utils.castView(view, R.id.tv_binding_compile, "field 'bindingCompile'", TextView.class);
    view2131296990 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.bindingCompile();
      }
    });
    target.spinnerOrg = Utils.findRequiredViewAsType(source, R.id.binding_to_org, "field 'spinnerOrg'", Spinner.class);
    target.spinnerDept = Utils.findRequiredViewAsType(source, R.id.binding_to_dept, "field 'spinnerDept'", Spinner.class);
    target.edName = Utils.findRequiredViewAsType(source, R.id.ed_name, "field 'edName'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BindingOrgDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.bindingCompile = null;
    target.spinnerOrg = null;
    target.spinnerDept = null;
    target.edName = null;

    view2131296990.setOnClickListener(null);
    view2131296990 = null;
  }
}
