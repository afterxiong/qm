// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ScreenShareActivity_ViewBinding implements Unbinder {
  private ScreenShareActivity target;

  private View view2131296341;

  private View view2131296655;

  private View view2131296400;

  @UiThread
  public ScreenShareActivity_ViewBinding(ScreenShareActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ScreenShareActivity_ViewBinding(final ScreenShareActivity target, View source) {
    this.target = target;

    View view;
    target.show_head = Utils.findRequiredViewAsType(source, R.id.show_head, "field 'show_head'", RelativeLayout.class);
    view = Utils.findRequiredView(source, R.id.back, "field 'back' and method 'back'");
    target.back = Utils.castView(view, R.id.back, "field 'back'", LinearLayout.class);
    view2131296341 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.back(p0);
      }
    });
    target.text = Utils.findRequiredViewAsType(source, R.id.text, "field 'text'", TextView.class);
    view = Utils.findRequiredView(source, R.id.menu, "field 'menu' and method 'menu'");
    target.menu = Utils.castView(view, R.id.menu, "field 'menu'", Button.class);
    view2131296655 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.menu(p0);
      }
    });
    target.show_close = Utils.findRequiredViewAsType(source, R.id.show_close, "field 'show_close'", RelativeLayout.class);
    target.end_play = Utils.findRequiredViewAsType(source, R.id.end_play, "field 'end_play'", TextView.class);
    view = Utils.findRequiredView(source, R.id.close, "field 'close' and method 'close'");
    target.close = Utils.castView(view, R.id.close, "field 'close'", TextView.class);
    view2131296400 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.close(p0);
      }
    });
    target.show_loading = Utils.findRequiredViewAsType(source, R.id.show_loading, "field 'show_loading'", RelativeLayout.class);
    target.loadingImage = Utils.findRequiredViewAsType(source, R.id.loadingImage, "field 'loadingImage'", ImageView.class);
    target.play_fame = Utils.findRequiredViewAsType(source, R.id.play_fame, "field 'play_fame'", TextView.class);
    target.light_bg = Utils.findRequiredViewAsType(source, R.id.light_bg, "field 'light_bg'", LinearLayout.class);
    target.light_count = Utils.findRequiredViewAsType(source, R.id.light_count, "field 'light_count'", TextView.class);
    target.show_audio = Utils.findRequiredViewAsType(source, R.id.show_audio, "field 'show_audio'", LinearLayout.class);
    target.vpb_right = Utils.findRequiredViewAsType(source, R.id.vpb_right, "field 'vpb_right'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ScreenShareActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.show_head = null;
    target.back = null;
    target.text = null;
    target.menu = null;
    target.show_close = null;
    target.end_play = null;
    target.close = null;
    target.show_loading = null;
    target.loadingImage = null;
    target.play_fame = null;
    target.light_bg = null;
    target.light_count = null;
    target.show_audio = null;
    target.vpb_right = null;

    view2131296341.setOnClickListener(null);
    view2131296341 = null;
    view2131296655.setOnClickListener(null);
    view2131296655 = null;
    view2131296400.setOnClickListener(null);
    view2131296400 = null;
  }
}
