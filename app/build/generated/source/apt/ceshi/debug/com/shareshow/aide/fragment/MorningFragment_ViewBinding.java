// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MorningFragment_ViewBinding implements Unbinder {
  private MorningFragment target;

  private View view2131297005;

  private View view2131296667;

  private View view2131296664;

  private View view2131297006;

  @UiThread
  public MorningFragment_ViewBinding(final MorningFragment target, View source) {
    this.target = target;

    View view;
    target.startAndCompile = Utils.findRequiredViewAsType(source, R.id.audio_start_and_compile, "field 'startAndCompile'", RelativeLayout.class);
    target.audioTime = Utils.findRequiredViewAsType(source, R.id.audio_time, "field 'audioTime'", TextView.class);
    target.audioTip = Utils.findRequiredViewAsType(source, R.id.audio_tip, "field 'audioTip'", TextView.class);
    target.recycler = Utils.findRequiredViewAsType(source, R.id.recorder_list, "field 'recycler'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.tv_select_time, "field 'tv_select_time' and method 'selectTime'");
    target.tv_select_time = Utils.castView(view, R.id.tv_select_time, "field 'tv_select_time'", TextView.class);
    view2131297005 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.selectTime();
      }
    });
    target.swipeRefresh = Utils.findRequiredViewAsType(source, R.id.swipeRefresh, "field 'swipeRefresh'", SmartRefreshLayout.class);
    view = Utils.findRequiredView(source, R.id.morning_submit, "field 'morningSubmit' and method 'onViewClicked'");
    target.morningSubmit = Utils.castView(view, R.id.morning_submit, "field 'morningSubmit'", ImageView.class);
    view2131296667 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.morning_delete, "field 'morningDelete' and method 'onViewClicked'");
    target.morningDelete = Utils.castView(view, R.id.morning_delete, "field 'morningDelete'", ImageView.class);
    view2131296664 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tv_select_user, "field 'selectUser' and method 'onViewClicked'");
    target.selectUser = Utils.castView(view, R.id.tv_select_user, "field 'selectUser'", ImageView.class);
    view2131297006 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.morningState = Utils.findRequiredViewAsType(source, R.id.morning_state, "field 'morningState'", TextView.class);
    target.audioPause = Utils.findRequiredViewAsType(source, R.id.audio_pause, "field 'audioPause'", LinearLayout.class);
    target.morningLine = Utils.findRequiredView(source, R.id.morning_line, "field 'morningLine'");
  }

  @Override
  @CallSuper
  public void unbind() {
    MorningFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.startAndCompile = null;
    target.audioTime = null;
    target.audioTip = null;
    target.recycler = null;
    target.tv_select_time = null;
    target.swipeRefresh = null;
    target.morningSubmit = null;
    target.morningDelete = null;
    target.selectUser = null;
    target.morningState = null;
    target.audioPause = null;
    target.morningLine = null;

    view2131297005.setOnClickListener(null);
    view2131297005 = null;
    view2131296667.setOnClickListener(null);
    view2131296667 = null;
    view2131296664.setOnClickListener(null);
    view2131296664 = null;
    view2131297006.setOnClickListener(null);
    view2131297006 = null;
  }
}
