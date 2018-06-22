// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.dialog;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VisitAudioDialog_ViewBinding implements Unbinder {
  private VisitAudioDialog target;

  private View view2131297050;

  private View view2131297046;

  private View view2131297047;

  @UiThread
  public VisitAudioDialog_ViewBinding(final VisitAudioDialog target, View source) {
    this.target = target;

    View view;
    target.visit_audio_time = Utils.findRequiredViewAsType(source, R.id.visit_audio_time, "field 'visit_audio_time'", TextView.class);
    view = Utils.findRequiredView(source, R.id.visit_audio_pause, "field 'visit_audio_pause' and method 'visit_audio_pause'");
    target.visit_audio_pause = Utils.castView(view, R.id.visit_audio_pause, "field 'visit_audio_pause'", TextView.class);
    view2131297050 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.visit_audio_pause();
      }
    });
    view = Utils.findRequiredView(source, R.id.visit_audio_close, "method 'visit_audio_close'");
    view2131297046 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.visit_audio_close();
      }
    });
    view = Utils.findRequiredView(source, R.id.visit_audio_compile, "method 'visit_audio_compile'");
    view2131297047 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.visit_audio_compile();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    VisitAudioDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.visit_audio_time = null;
    target.visit_audio_pause = null;

    view2131297050.setOnClickListener(null);
    view2131297050 = null;
    view2131297046.setOnClickListener(null);
    view2131297046 = null;
    view2131297047.setOnClickListener(null);
    view2131297047 = null;
  }
}
