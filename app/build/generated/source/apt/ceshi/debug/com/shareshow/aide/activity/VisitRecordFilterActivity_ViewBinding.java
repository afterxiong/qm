// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VisitRecordFilterActivity_ViewBinding implements Unbinder {
  private VisitRecordFilterActivity target;

  @UiThread
  public VisitRecordFilterActivity_ViewBinding(VisitRecordFilterActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public VisitRecordFilterActivity_ViewBinding(VisitRecordFilterActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.toolbarTitle = Utils.findRequiredViewAsType(source, R.id.toolbar_title, "field 'toolbarTitle'", TextView.class);
    target.tvStartDate = Utils.findRequiredViewAsType(source, R.id.start_date, "field 'tvStartDate'", TextView.class);
    target.tvEndDate = Utils.findRequiredViewAsType(source, R.id.end_date, "field 'tvEndDate'", TextView.class);
    target.recycler = Utils.findRequiredViewAsType(source, R.id.recycler, "field 'recycler'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    VisitRecordFilterActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.toolbarTitle = null;
    target.tvStartDate = null;
    target.tvEndDate = null;
    target.recycler = null;
  }
}
