// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.amap.api.maps.MapView;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NearbyActivity_ViewBinding implements Unbinder {
  private NearbyActivity target;

  @UiThread
  public NearbyActivity_ViewBinding(NearbyActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public NearbyActivity_ViewBinding(NearbyActivity target, View source) {
    this.target = target;

    target.mapView = Utils.findRequiredViewAsType(source, R.id.map, "field 'mapView'", MapView.class);
    target.recycler = Utils.findRequiredViewAsType(source, R.id.recycler, "field 'recycler'", RecyclerView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NearbyActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mapView = null;
    target.recycler = null;
    target.toolbar = null;
  }
}
