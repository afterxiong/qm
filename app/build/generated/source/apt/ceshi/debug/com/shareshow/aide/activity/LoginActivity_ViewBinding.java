// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoginActivity_ViewBinding implements Unbinder {
  private LoginActivity target;

  private View view2131296534;

  private View view2131296644;

  private View view2131296484;

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public LoginActivity_ViewBinding(final LoginActivity target, View source) {
    this.target = target;

    View view;
    target.edCode = Utils.findRequiredViewAsType(source, R.id.ed_code, "field 'edCode'", EditText.class);
    target.edPhone = Utils.findRequiredViewAsType(source, R.id.ed_phone, "field 'edPhone'", EditText.class);
    view = Utils.findRequiredView(source, R.id.gain_code, "field 'gainCode' and method 'gainCode'");
    target.gainCode = Utils.castView(view, R.id.gain_code, "field 'gainCode'", Button.class);
    view2131296534 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.gainCode();
      }
    });
    view = Utils.findRequiredView(source, R.id.login, "field 'login' and method 'btnLogin'");
    target.login = Utils.castView(view, R.id.login, "field 'login'", Button.class);
    view2131296644 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnLogin();
      }
    });
    target.loginLayout = Utils.findRequiredViewAsType(source, R.id.login_layout, "field 'loginLayout'", LinearLayout.class);
    target.entryNameLayout = Utils.findRequiredViewAsType(source, R.id.entry_name_layout, "field 'entryNameLayout'", LinearLayout.class);
    target.entryName = Utils.findRequiredViewAsType(source, R.id.entry_name, "field 'entryName'", EditText.class);
    view = Utils.findRequiredView(source, R.id.enter, "method 'enter'");
    view2131296484 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.enter();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.edCode = null;
    target.edPhone = null;
    target.gainCode = null;
    target.login = null;
    target.loginLayout = null;
    target.entryNameLayout = null;
    target.entryName = null;

    view2131296534.setOnClickListener(null);
    view2131296534 = null;
    view2131296644.setOnClickListener(null);
    view2131296644 = null;
    view2131296484.setOnClickListener(null);
    view2131296484 = null;
  }
}
