// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import com.shareshow.aide.widget.DirectionKeyView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RemoteControl_ViewBinding implements Unbinder {
  private RemoteControl target;

  private View view2131296456;

  private View view2131297088;

  private View view2131297089;

  private View view2131297090;

  private View view2131297058;

  private View view2131297056;

  private View view2131297057;

  @UiThread
  public RemoteControl_ViewBinding(final RemoteControl target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.direction_center, "field 'direction_center' and method 'directionCenter'");
    target.direction_center = view;
    view2131296456 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.directionCenter(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.yaokong_back, "field 'yaokong_back' and method 'yaokongBack'");
    target.yaokong_back = view;
    view2131297088 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.yaokongBack(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.yaokong_home, "field 'yaokong_home' and method 'yaokongHome'");
    target.yaokong_home = view;
    view2131297089 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.yaokongHome(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.yaokong_memu, "field 'yaokong_memu' and method 'yaokongMemu'");
    target.yaokong_memu = view;
    view2131297090 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.yaokongMemu(p0);
      }
    });
    target.directionKeyView = Utils.findRequiredViewAsType(source, R.id.direction_key, "field 'directionKeyView'", DirectionKeyView.class);
    view = Utils.findRequiredView(source, R.id.voice_static, "method 'jingYin'");
    view2131297058 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.jingYin(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.voice_minus, "method 'minusYinliang'");
    view2131297056 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.minusYinliang(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.voice_pluse, "method 'addYinliang'");
    view2131297057 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.addYinliang(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    RemoteControl target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.direction_center = null;
    target.yaokong_back = null;
    target.yaokong_home = null;
    target.yaokong_memu = null;
    target.directionKeyView = null;

    view2131296456.setOnClickListener(null);
    view2131296456 = null;
    view2131297088.setOnClickListener(null);
    view2131297088 = null;
    view2131297089.setOnClickListener(null);
    view2131297089 = null;
    view2131297090.setOnClickListener(null);
    view2131297090 = null;
    view2131297058.setOnClickListener(null);
    view2131297058 = null;
    view2131297056.setOnClickListener(null);
    view2131297056 = null;
    view2131297057.setOnClickListener(null);
    view2131297057 = null;
  }
}
