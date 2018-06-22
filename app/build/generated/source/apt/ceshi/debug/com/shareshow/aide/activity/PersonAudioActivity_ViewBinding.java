// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import com.shareshow.airpc.widget.ProgressWheel;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PersonAudioActivity_ViewBinding implements Unbinder {
  private PersonAudioActivity target;

  private View view2131296916;

  private View view2131296479;

  @UiThread
  public PersonAudioActivity_ViewBinding(PersonAudioActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public PersonAudioActivity_ViewBinding(final PersonAudioActivity target, View source) {
    this.target = target;

    View view;
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    view = Utils.findRequiredView(source, R.id.startTime, "field 'startTime' and method 'startTime'");
    target.startTime = Utils.castView(view, R.id.startTime, "field 'startTime'", TextView.class);
    view2131296916 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.startTime();
      }
    });
    view = Utils.findRequiredView(source, R.id.endTime, "field 'endTime' and method 'endTime'");
    target.endTime = Utils.castView(view, R.id.endTime, "field 'endTime'", TextView.class);
    view2131296479 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.endTime();
      }
    });
    target.trackTotle = Utils.findRequiredViewAsType(source, R.id.track_totle, "field 'trackTotle'", TextView.class);
    target.audioRecylerview = Utils.findRequiredViewAsType(source, R.id.audio_recylerview, "field 'audioRecylerview'", RecyclerView.class);
    target.title = Utils.findRequiredViewAsType(source, R.id.title, "field 'title'", TextView.class);
    target.progressWheel = Utils.findRequiredViewAsType(source, R.id.progress_wheel, "field 'progressWheel'", ProgressWheel.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PersonAudioActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.startTime = null;
    target.endTime = null;
    target.trackTotle = null;
    target.audioRecylerview = null;
    target.title = null;
    target.progressWheel = null;

    view2131296916.setOnClickListener(null);
    view2131296916 = null;
    view2131296479.setOnClickListener(null);
    view2131296479 = null;
  }
}
