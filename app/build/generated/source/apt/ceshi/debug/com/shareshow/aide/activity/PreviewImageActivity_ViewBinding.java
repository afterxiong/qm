// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PreviewImageActivity_ViewBinding implements Unbinder {
  private PreviewImageActivity target;

  @UiThread
  public PreviewImageActivity_ViewBinding(PreviewImageActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public PreviewImageActivity_ViewBinding(PreviewImageActivity target, View source) {
    this.target = target;

    target.image = Utils.findRequiredViewAsType(source, R.id.photo_preview, "field 'image'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PreviewImageActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.image = null;
  }
}
