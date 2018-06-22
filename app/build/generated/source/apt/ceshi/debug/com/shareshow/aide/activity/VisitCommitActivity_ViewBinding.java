// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VisitCommitActivity_ViewBinding implements Unbinder {
  private VisitCommitActivity target;

  private View view2131297048;

  private View view2131297054;

  private View view2131296444;

  private View view2131296364;

  @UiThread
  public VisitCommitActivity_ViewBinding(VisitCommitActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public VisitCommitActivity_ViewBinding(final VisitCommitActivity target, View source) {
    this.target = target;

    View view;
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.visit_name = Utils.findRequiredViewAsType(source, R.id.visit_name, "field 'visit_name'", TextView.class);
    target.visit_address = Utils.findRequiredViewAsType(source, R.id.visit_address, "field 'visit_address'", TextView.class);
    target.visit_time = Utils.findRequiredViewAsType(source, R.id.visit_time, "field 'visit_time'", TextView.class);
    target.visit_content = Utils.findRequiredViewAsType(source, R.id.visit_content, "field 'visit_content'", EditText.class);
    view = Utils.findRequiredView(source, R.id.visit_audio_content_layout, "field 'visit_audio_content_layout' and method 'visit_audio_content_layout'");
    target.visit_audio_content_layout = Utils.castView(view, R.id.visit_audio_content_layout, "field 'visit_audio_content_layout'", FrameLayout.class);
    view2131297048 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.visit_audio_content_layout();
      }
    });
    target.recycler = Utils.findRequiredViewAsType(source, R.id.photo_recycler, "field 'recycler'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.visit_start_audio, "method 'visit_start_audio'");
    view2131297054 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.visit_start_audio();
      }
    });
    view = Utils.findRequiredView(source, R.id.delete, "method 'delete'");
    view2131296444 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.delete();
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_visit_commit, "method 'btn_visit_commit'");
    view2131296364 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btn_visit_commit();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    VisitCommitActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.visit_name = null;
    target.visit_address = null;
    target.visit_time = null;
    target.visit_content = null;
    target.visit_audio_content_layout = null;
    target.recycler = null;

    view2131297048.setOnClickListener(null);
    view2131297048 = null;
    view2131297054.setOnClickListener(null);
    view2131297054 = null;
    view2131296444.setOnClickListener(null);
    view2131296444 = null;
    view2131296364.setOnClickListener(null);
    view2131296364 = null;
  }
}
