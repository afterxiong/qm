// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import com.shareshow.airpc.widget.ProgressWheel;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BaseWQFragment_ViewBinding implements Unbinder {
  private BaseWQFragment target;

  private View view2131296463;

  private View view2131296581;

  private View view2131297037;

  private View view2131296870;

  private View view2131297019;

  private View view2131296861;

  @UiThread
  public BaseWQFragment_ViewBinding(final BaseWQFragment target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.document, "field 'document' and method 'document'");
    target.document = Utils.castView(view, R.id.document, "field 'document'", LinearLayout.class);
    view2131296463 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.document(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.img, "field 'img' and method 'album'");
    target.img = Utils.castView(view, R.id.img, "field 'img'", LinearLayout.class);
    view2131296581 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.album(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.video, "field 'video' and method 'video'");
    target.video = Utils.castView(view, R.id.video, "field 'video'", LinearLayout.class);
    view2131297037 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.video(p0);
      }
    });
    target.wheel = Utils.findRequiredViewAsType(source, R.id.progress_wheel, "field 'wheel'", ProgressWheel.class);
    target.list = Utils.findRequiredViewAsType(source, R.id.list, "field 'list'", ListView.class);
    target.bottom = Utils.findRequiredViewAsType(source, R.id.buttom, "field 'bottom'", LinearLayout.class);
    target.textName = Utils.findRequiredViewAsType(source, R.id.textname, "field 'textName'", TextView.class);
    view = Utils.findRequiredView(source, R.id.share, "field 'share' and method 'share'");
    target.share = Utils.castView(view, R.id.share, "field 'share'", ImageView.class);
    view2131296870 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.share(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.up_load, "field 'up_load' and method 'up_load'");
    target.up_load = Utils.castView(view, R.id.up_load, "field 'up_load'", ImageView.class);
    view2131297019 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.up_load(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.selectAll, "field 'selectAll' and method 'selectAll'");
    target.selectAll = Utils.castView(view, R.id.selectAll, "field 'selectAll'", ImageView.class);
    view2131296861 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.selectAll(p0);
      }
    });
    target.refresh = Utils.findRequiredViewAsType(source, R.id.swipeRefresh, "field 'refresh'", SwipeRefreshLayout.class);
    target.text = Utils.findRequiredViewAsType(source, R.id.text, "field 'text'", TextView.class);
    target.showUpLoad = Utils.findRequiredViewAsType(source, R.id.show_upLoad, "field 'showUpLoad'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BaseWQFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.document = null;
    target.img = null;
    target.video = null;
    target.wheel = null;
    target.list = null;
    target.bottom = null;
    target.textName = null;
    target.share = null;
    target.up_load = null;
    target.selectAll = null;
    target.refresh = null;
    target.text = null;
    target.showUpLoad = null;

    view2131296463.setOnClickListener(null);
    view2131296463 = null;
    view2131296581.setOnClickListener(null);
    view2131296581 = null;
    view2131297037.setOnClickListener(null);
    view2131297037 = null;
    view2131296870.setOnClickListener(null);
    view2131296870 = null;
    view2131297019.setOnClickListener(null);
    view2131297019 = null;
    view2131296861.setOnClickListener(null);
    view2131296861 = null;
  }
}
