// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class QuiteFileActivity_ViewBinding implements Unbinder {
  private QuiteFileActivity target;

  private View view2131296640;

  private View view2131297061;

  private View view2131296263;

  private View view2131296368;

  @UiThread
  public QuiteFileActivity_ViewBinding(QuiteFileActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public QuiteFileActivity_ViewBinding(final QuiteFileActivity target, View source) {
    this.target = target;

    View view;
    target.text = Utils.findRequiredViewAsType(source, R.id.text, "field 'text'", TextView.class);
    view = Utils.findRequiredView(source, R.id.local_file, "field 'localFile' and method 'local'");
    target.localFile = Utils.castView(view, R.id.local_file, "field 'localFile'", TextView.class);
    view2131296640 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.local();
      }
    });
    view = Utils.findRequiredView(source, R.id.weixin, "field 'weixin' and method 'Weixin'");
    target.weixin = Utils.castView(view, R.id.weixin, "field 'weixin'", TextView.class);
    view2131297061 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.Weixin();
      }
    });
    view = Utils.findRequiredView(source, R.id.QQ, "field 'QQ' and method 'QQ'");
    target.QQ = Utils.castView(view, R.id.QQ, "field 'QQ'", TextView.class);
    view2131296263 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.QQ();
      }
    });
    target.frame = Utils.findRequiredViewAsType(source, R.id.frame, "field 'frame'", FrameLayout.class);
    target.popupBg = Utils.findRequiredViewAsType(source, R.id.popup_bg, "field 'popupBg'", TextView.class);
    view = Utils.findRequiredView(source, R.id.cancel, "field 'cancel' and method 'cancel'");
    target.cancel = Utils.castView(view, R.id.cancel, "field 'cancel'", Button.class);
    view2131296368 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.cancel(p0);
      }
    });
    target.weixinLine = Utils.findRequiredViewAsType(source, R.id.weixin_line, "field 'weixinLine'", TextView.class);
    target.qqLine = Utils.findRequiredViewAsType(source, R.id.qq_line, "field 'qqLine'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    QuiteFileActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.text = null;
    target.localFile = null;
    target.weixin = null;
    target.QQ = null;
    target.frame = null;
    target.popupBg = null;
    target.cancel = null;
    target.weixinLine = null;
    target.qqLine = null;

    view2131296640.setOnClickListener(null);
    view2131296640 = null;
    view2131297061.setOnClickListener(null);
    view2131297061 = null;
    view2131296263.setOnClickListener(null);
    view2131296263 = null;
    view2131296368.setOnClickListener(null);
    view2131296368 = null;
  }
}
