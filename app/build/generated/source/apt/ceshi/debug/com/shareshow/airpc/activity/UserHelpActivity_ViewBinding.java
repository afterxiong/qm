// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UserHelpActivity_ViewBinding implements Unbinder {
  private UserHelpActivity target;

  private View view2131296745;

  private View view2131296739;

  private View view2131296980;

  private View view2131296740;

  private View view2131296984;

  private View view2131296778;

  private View view2131296341;

  @UiThread
  public UserHelpActivity_ViewBinding(UserHelpActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public UserHelpActivity_ViewBinding(final UserHelpActivity target, View source) {
    this.target = target;

    View view;
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    view = Utils.findRequiredView(source, R.id.phone, "field 'phone' and method 'phone'");
    target.phone = Utils.castView(view, R.id.phone, "field 'phone'", ImageView.class);
    view2131296745 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.phone(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.pc, "field 'pc' and method 'pc'");
    target.pc = Utils.castView(view, R.id.pc, "field 'pc'", ImageView.class);
    view2131296739 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.pc(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tv, "field 'tv' and method 'tv'");
    target.tv = Utils.castView(view, R.id.tv, "field 'tv'", ImageView.class);
    view2131296980 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.tv(p0);
      }
    });
    target.helpList = Utils.findRequiredViewAsType(source, R.id.help_list, "field 'helpList'", ExpandableListView.class);
    target.phoneItem = Utils.findRequiredViewAsType(source, R.id.phone_item, "field 'phoneItem'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.pc_2, "field 'pc2' and method 'pc2'");
    target.pc2 = Utils.castView(view, R.id.pc_2, "field 'pc2'", ImageView.class);
    view2131296740 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.pc2(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tv_2, "field 'tv2' and method 'tv2'");
    target.tv2 = Utils.castView(view, R.id.tv_2, "field 'tv2'", ImageView.class);
    view2131296984 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.tv2(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.propose, "field 'propose' and method 'propose'");
    target.propose = Utils.castView(view, R.id.propose, "field 'propose'", ImageView.class);
    view2131296778 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.propose(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.back, "method 'back'");
    view2131296341 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.back(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    UserHelpActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.phone = null;
    target.pc = null;
    target.tv = null;
    target.helpList = null;
    target.phoneItem = null;
    target.pc2 = null;
    target.tv2 = null;
    target.propose = null;

    view2131296745.setOnClickListener(null);
    view2131296745 = null;
    view2131296739.setOnClickListener(null);
    view2131296739 = null;
    view2131296980.setOnClickListener(null);
    view2131296980 = null;
    view2131296740.setOnClickListener(null);
    view2131296740 = null;
    view2131296984.setOnClickListener(null);
    view2131296984 = null;
    view2131296778.setOnClickListener(null);
    view2131296778 = null;
    view2131296341.setOnClickListener(null);
    view2131296341 = null;
  }
}
