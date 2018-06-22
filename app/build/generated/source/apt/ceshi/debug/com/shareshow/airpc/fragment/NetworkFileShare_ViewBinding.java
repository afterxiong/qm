// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NetworkFileShare_ViewBinding implements Unbinder {
  private NetworkFileShare target;

  private View view2131296631;

  private View view2131296468;

  private View view2131296861;

  private View view2131296816;

  private View view2131296489;

  @UiThread
  public NetworkFileShare_ViewBinding(final NetworkFileShare target, View source) {
    this.target = target;

    View view;
    target.lastDirectory = Utils.findRequiredViewAsType(source, R.id.lastDirectory, "field 'lastDirectory'", LinearLayout.class);
    target.swipeRefresh = Utils.findRequiredViewAsType(source, R.id.swipeRefresh, "field 'swipeRefresh'", SwipeRefreshLayout.class);
    view = Utils.findRequiredView(source, R.id.listView, "field 'listView', method 'onItemClick', and method 'onItemLongClick'");
    target.listView = Utils.castView(view, R.id.listView, "field 'listView'", ListView.class);
    view2131296631 = view;
    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {
        target.onItemClick(p0, p1, p2, p3);
      }
    });
    ((AdapterView<?>) view).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> p0, View p1, int p2, long p3) {
        return target.onItemLongClick(p0, p1, p2, p3);
      }
    });
    target.buttom = Utils.findRequiredViewAsType(source, R.id.buttom, "field 'buttom'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.download, "field 'download' and method 'download'");
    target.download = Utils.castView(view, R.id.download, "field 'download'", ImageView.class);
    view2131296468 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.download(p0);
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
    view = Utils.findRequiredView(source, R.id.rollBack, "field 'rollBack' and method 'rollBack'");
    target.rollBack = Utils.castView(view, R.id.rollBack, "field 'rollBack'", TextView.class);
    view2131296816 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.rollBack(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.error_show, "field 'error_show' and method 'errorShow'");
    target.error_show = Utils.castView(view, R.id.error_show, "field 'error_show'", TextView.class);
    view2131296489 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.errorShow(p0);
      }
    });
    target.showProgress = Utils.findRequiredViewAsType(source, R.id.showProgress, "field 'showProgress'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NetworkFileShare target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.lastDirectory = null;
    target.swipeRefresh = null;
    target.listView = null;
    target.buttom = null;
    target.download = null;
    target.selectAll = null;
    target.rollBack = null;
    target.error_show = null;
    target.showProgress = null;

    ((AdapterView<?>) view2131296631).setOnItemClickListener(null);
    ((AdapterView<?>) view2131296631).setOnItemLongClickListener(null);
    view2131296631 = null;
    view2131296468.setOnClickListener(null);
    view2131296468 = null;
    view2131296861.setOnClickListener(null);
    view2131296861 = null;
    view2131296816.setOnClickListener(null);
    view2131296816 = null;
    view2131296489.setOnClickListener(null);
    view2131296489 = null;
  }
}
