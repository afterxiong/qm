// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.dialog;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ShareFileDialog_ViewBinding implements Unbinder {
  private ShareFileDialog target;

  @UiThread
  public ShareFileDialog_ViewBinding(ShareFileDialog target, View source) {
    this.target = target;

    target.cancell = Utils.findRequiredView(source, R.id.cancel, "field 'cancell'");
    target.shareWeChatBut = Utils.findRequiredView(source, R.id.share_we_chat_but, "field 'shareWeChatBut'");
    target.shareQQBut = Utils.findRequiredView(source, R.id.share_qq_but, "field 'shareQQBut'");
  }

  @Override
  @CallSuper
  public void unbind() {
    ShareFileDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.cancell = null;
    target.shareWeChatBut = null;
    target.shareQQBut = null;
  }
}
