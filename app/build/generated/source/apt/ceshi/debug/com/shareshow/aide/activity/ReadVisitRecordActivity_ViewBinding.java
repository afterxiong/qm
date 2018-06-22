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

public class ReadVisitRecordActivity_ViewBinding implements Unbinder {
  private ReadVisitRecordActivity target;

  @UiThread
  public ReadVisitRecordActivity_ViewBinding(ReadVisitRecordActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ReadVisitRecordActivity_ViewBinding(ReadVisitRecordActivity target, View source) {
    this.target = target;

    target.visit_name = Utils.findRequiredViewAsType(source, R.id.visit_name, "field 'visit_name'", TextView.class);
    target.visit_address = Utils.findRequiredViewAsType(source, R.id.visit_address, "field 'visit_address'", TextView.class);
    target.visit_time = Utils.findRequiredViewAsType(source, R.id.visit_time, "field 'visit_time'", TextView.class);
    target.visit_content = Utils.findRequiredViewAsType(source, R.id.visit_content, "field 'visit_content'", TextView.class);
    target.picRecycler = Utils.findRequiredViewAsType(source, R.id.recycler, "field 'picRecycler'", RecyclerView.class);
    target.audioRecycler = Utils.findRequiredViewAsType(source, R.id.audio_recycler, "field 'audioRecycler'", RecyclerView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ReadVisitRecordActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.visit_name = null;
    target.visit_address = null;
    target.visit_time = null;
    target.visit_content = null;
    target.picRecycler = null;
    target.audioRecycler = null;
    target.toolbar = null;
  }
}
