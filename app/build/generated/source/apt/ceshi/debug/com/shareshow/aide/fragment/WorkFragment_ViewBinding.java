// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class WorkFragment_ViewBinding implements Unbinder {
  private WorkFragment target;

  private View view2131296663;

  private View view2131296836;

  private View view2131297083;

  private View view2131296805;

  @UiThread
  public WorkFragment_ViewBinding(final WorkFragment target, View source) {
    this.target = target;

    View view;
    target.flipper = Utils.findRequiredViewAsType(source, R.id.flipper, "field 'flipper'", ViewFlipper.class);
    view = Utils.findRequiredView(source, R.id.more_affiche_notify, "field 'moreAfficheNotify' and method 'moreNotification'");
    target.moreAfficheNotify = view;
    view2131296663 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.moreNotification();
      }
    });
    target.refreshLayout = Utils.findRequiredViewAsType(source, R.id.refreshLayout, "field 'refreshLayout'", SwipeRefreshLayout.class);
    target.recycler = Utils.findRequiredViewAsType(source, R.id.recycler, "field 'recycler'", RecyclerView.class);
    target.emputyState = Utils.findRequiredView(source, R.id.emputy_state, "field 'emputyState'");
    target.visitCountText = Utils.findRequiredViewAsType(source, R.id.tv_visit_count, "field 'visitCountText'", TextView.class);
    target.morningTrack = Utils.findRequiredViewAsType(source, R.id.tv_morning_track, "field 'morningTrack'", TextView.class);
    target.deviceGroupView = Utils.findRequiredView(source, R.id.device_group_view, "field 'deviceGroupView'");
    target.deviceStateText = Utils.findRequiredViewAsType(source, R.id.my_device_state, "field 'deviceStateText'", TextView.class);
    target.guideText = Utils.findRequiredViewAsType(source, R.id.guide_text, "field 'guideText'", TextView.class);
    view = Utils.findRequiredView(source, R.id.screen_projection, "field 'screenProjection' and method 'openScreenProjection'");
    target.screenProjection = view;
    view2131296836 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.openScreenProjection();
      }
    });
    view = Utils.findRequiredView(source, R.id.work_file, "field 'workFile' and method 'openWorkFile'");
    target.workFile = view;
    view2131297083 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.openWorkFile();
      }
    });
    view = Utils.findRequiredView(source, R.id.remote_control, "field 'remoteControl' and method 'openRemoteControl'");
    target.remoteControl = view;
    view2131296805 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.openRemoteControl();
      }
    });
    target.screenProjectionStateText = Utils.findRequiredViewAsType(source, R.id.screen_projection_state_text, "field 'screenProjectionStateText'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    WorkFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.flipper = null;
    target.moreAfficheNotify = null;
    target.refreshLayout = null;
    target.recycler = null;
    target.emputyState = null;
    target.visitCountText = null;
    target.morningTrack = null;
    target.deviceGroupView = null;
    target.deviceStateText = null;
    target.guideText = null;
    target.screenProjection = null;
    target.workFile = null;
    target.remoteControl = null;
    target.screenProjectionStateText = null;

    view2131296663.setOnClickListener(null);
    view2131296663 = null;
    view2131296836.setOnClickListener(null);
    view2131296836 = null;
    view2131297083.setOnClickListener(null);
    view2131297083 = null;
    view2131296805.setOnClickListener(null);
    view2131296805 = null;
  }
}
