// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.amap.api.maps.MapView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VisitTrackListActivity_ViewBinding implements Unbinder {
  private VisitTrackListActivity target;

  private View view2131297005;

  private View view2131296576;

  @UiThread
  public VisitTrackListActivity_ViewBinding(VisitTrackListActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public VisitTrackListActivity_ViewBinding(final VisitTrackListActivity target, View source) {
    this.target = target;

    View view;
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.mapView = Utils.findRequiredViewAsType(source, R.id.map, "field 'mapView'", MapView.class);
    target.smartRefreshLayout = Utils.findRequiredViewAsType(source, R.id.smart_refresh_layout, "field 'smartRefreshLayout'", SmartRefreshLayout.class);
    target.recycler = Utils.findRequiredViewAsType(source, R.id.recycler, "field 'recycler'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.tv_select_time, "field 'tv_select_time' and method 'selectTime'");
    target.tv_select_time = Utils.castView(view, R.id.tv_select_time, "field 'tv_select_time'", TextView.class);
    view2131297005 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.selectTime();
      }
    });
    view = Utils.findRequiredView(source, R.id.im_select_user, "field 'im_select_user' and method 'selectUser'");
    target.im_select_user = Utils.castView(view, R.id.im_select_user, "field 'im_select_user'", ImageView.class);
    view2131296576 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.selectUser();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    VisitTrackListActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.mapView = null;
    target.smartRefreshLayout = null;
    target.recycler = null;
    target.tv_select_time = null;
    target.im_select_user = null;

    view2131297005.setOnClickListener(null);
    view2131297005 = null;
    view2131296576.setOnClickListener(null);
    view2131296576 = null;
  }
}
