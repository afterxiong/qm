// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DeptMemberActivity_ViewBinding implements Unbinder {
  private DeptMemberActivity target;

  @UiThread
  public DeptMemberActivity_ViewBinding(DeptMemberActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public DeptMemberActivity_ViewBinding(DeptMemberActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.title = Utils.findRequiredViewAsType(source, R.id.title, "field 'title'", TextView.class);
    target.refreshLayout = Utils.findRequiredViewAsType(source, R.id.swipe_refresh, "field 'refreshLayout'", SwipeRefreshLayout.class);
    target.recycler = Utils.findRequiredViewAsType(source, R.id.recycler, "field 'recycler'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    DeptMemberActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.title = null;
    target.refreshLayout = null;
    target.recycler = null;
  }
}
