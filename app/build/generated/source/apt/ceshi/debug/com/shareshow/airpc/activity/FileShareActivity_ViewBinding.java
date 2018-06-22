// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FileShareActivity_ViewBinding implements Unbinder {
  private FileShareActivity target;

  private View view2131296760;

  private View view2131296640;

  private View view2131296806;

  @UiThread
  public FileShareActivity_ViewBinding(FileShareActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public FileShareActivity_ViewBinding(final FileShareActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.popup, "field 'cancel' and method 'cancel'");
    target.cancel = Utils.castView(view, R.id.popup, "field 'cancel'", Button.class);
    view2131296760 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.cancel(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.local_file, "field 'local_file' and method 'localFile'");
    target.local_file = Utils.castView(view, R.id.local_file, "field 'local_file'", TextView.class);
    view2131296640 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.localFile(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.remote_device, "field 'remote_device' and method 'remoteDevice'");
    target.remote_device = Utils.castView(view, R.id.remote_device, "field 'remote_device'", TextView.class);
    view2131296806 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.remoteDevice(p0);
      }
    });
    target.show_arrow = Utils.findRequiredViewAsType(source, R.id.show_arrow, "field 'show_arrow'", LinearLayout.class);
    target.popup_bg = Utils.findRequiredViewAsType(source, R.id.popup_bg, "field 'popup_bg'", TextView.class);
    target.arrow_popup = Utils.findRequiredViewAsType(source, R.id.arrow_popup, "field 'arrow_popup'", TextView.class);
    target.text = Utils.findRequiredViewAsType(source, R.id.text, "field 'text'", TextView.class);
    target.show_local = Utils.findRequiredViewAsType(source, R.id.show_local, "field 'show_local'", LinearLayout.class);
    target.arrow_local = Utils.findRequiredViewAsType(source, R.id.arrow_local, "field 'arrow_local'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FileShareActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.cancel = null;
    target.local_file = null;
    target.remote_device = null;
    target.show_arrow = null;
    target.popup_bg = null;
    target.arrow_popup = null;
    target.text = null;
    target.show_local = null;
    target.arrow_local = null;

    view2131296760.setOnClickListener(null);
    view2131296760 = null;
    view2131296640.setOnClickListener(null);
    view2131296640 = null;
    view2131296806.setOnClickListener(null);
    view2131296806 = null;
  }
}
