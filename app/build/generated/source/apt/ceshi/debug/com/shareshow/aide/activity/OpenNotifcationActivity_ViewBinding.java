// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class OpenNotifcationActivity_ViewBinding implements Unbinder {
  private OpenNotifcationActivity target;

  @UiThread
  public OpenNotifcationActivity_ViewBinding(OpenNotifcationActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public OpenNotifcationActivity_ViewBinding(OpenNotifcationActivity target, View source) {
    this.target = target;

    target.title = Utils.findRequiredViewAsType(source, R.id.notif_title, "field 'title'", TextView.class);
    target.content = Utils.findRequiredViewAsType(source, R.id.notif_content, "field 'content'", TextView.class);
    target.parentLayout = Utils.findRequiredViewAsType(source, R.id.parent_layout, "field 'parentLayout'", LinearLayout.class);
    target.studyTime = Utils.findRequiredViewAsType(source, R.id.notif_study_time, "field 'studyTime'", TextView.class);
    target.studyDate = Utils.findRequiredViewAsType(source, R.id.notif_study_date, "field 'studyDate'", TextView.class);
    target.issueDdate = Utils.findRequiredViewAsType(source, R.id.notif_issue_date, "field 'issueDdate'", TextView.class);
    target.dept = Utils.findRequiredViewAsType(source, R.id.notif_dept, "field 'dept'", TextView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    OpenNotifcationActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.title = null;
    target.content = null;
    target.parentLayout = null;
    target.studyTime = null;
    target.studyDate = null;
    target.issueDdate = null;
    target.dept = null;
    target.toolbar = null;
  }
}
