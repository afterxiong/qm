// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ProposeActivity_ViewBinding implements Unbinder {
  private ProposeActivity target;

  private View view2131296779;

  @UiThread
  public ProposeActivity_ViewBinding(ProposeActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ProposeActivity_ViewBinding(final ProposeActivity target, View source) {
    this.target = target;

    View view;
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.recycler = Utils.findRequiredViewAsType(source, R.id.recycler, "field 'recycler'", RecyclerView.class);
    target.et_su = Utils.findRequiredViewAsType(source, R.id.text_su, "field 'et_su'", EditText.class);
    target.et_tel = Utils.findRequiredViewAsType(source, R.id.text_tel, "field 'et_tel'", EditText.class);
    view = Utils.findRequiredView(source, R.id.propose_commit, "method 'commit'");
    view2131296779 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.commit(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    ProposeActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.recycler = null;
    target.et_su = null;
    target.et_tel = null;

    view2131296779.setOnClickListener(null);
    view2131296779 = null;
  }
}
