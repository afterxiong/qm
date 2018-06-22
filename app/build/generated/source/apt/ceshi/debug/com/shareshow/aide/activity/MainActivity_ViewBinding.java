// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  private View view2131296575;

  private View view2131296655;

  private View view2131296582;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(final MainActivity target, View source) {
    this.target = target;

    View view;
    target.tabLayout = Utils.findRequiredViewAsType(source, R.id.tablayout, "field 'tabLayout'", TabLayout.class);
    target.pager = Utils.findRequiredViewAsType(source, R.id.viewpager, "field 'pager'", ViewPager.class);
    view = Utils.findRequiredView(source, R.id.im_ind, "field 'imInd' and method 'openInd'");
    target.imInd = Utils.castView(view, R.id.im_ind, "field 'imInd'", ImageView.class);
    view2131296575 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.openInd();
      }
    });
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    view = Utils.findRequiredView(source, R.id.menu, "field 'menu' and method 'openMenu'");
    target.menu = view;
    view2131296655 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.openMenu();
      }
    });
    target.title = Utils.findRequiredViewAsType(source, R.id.title, "field 'title'", TextView.class);
    view = Utils.findRequiredView(source, R.id.in_visit, "field 'visit' and method 'openVisitMap'");
    target.visit = view;
    view2131296582 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.openVisitMap();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tabLayout = null;
    target.pager = null;
    target.imInd = null;
    target.toolbar = null;
    target.menu = null;
    target.title = null;
    target.visit = null;

    view2131296575.setOnClickListener(null);
    view2131296575 = null;
    view2131296655.setOnClickListener(null);
    view2131296655 = null;
    view2131296582.setOnClickListener(null);
    view2131296582 = null;
  }
}
