// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.amap.api.maps.MapView;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class VisitFragment_ViewBinding implements Unbinder {
  private VisitFragment target;

  private View view2131296918;

  private View view2131297007;

  private View view2131297009;

  @UiThread
  public VisitFragment_ViewBinding(final VisitFragment target, View source) {
    this.target = target;

    View view;
    target.mapView = Utils.findRequiredViewAsType(source, R.id.map, "field 'mapView'", MapView.class);
    target.tvLocation = Utils.findRequiredViewAsType(source, R.id.tv_location, "field 'tvLocation'", TextView.class);
    target.edClineName = Utils.findRequiredViewAsType(source, R.id.ed_cline_name, "field 'edClineName'", EditText.class);
    target.tv_visit_count = Utils.findRequiredViewAsType(source, R.id.tv_visit_count, "field 'tv_visit_count'", TextView.class);
    view = Utils.findRequiredView(source, R.id.start_visit, "field 'start_visit' and method 'start_visit'");
    target.start_visit = Utils.castView(view, R.id.start_visit, "field 'start_visit'", RelativeLayout.class);
    view2131296918 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.start_visit();
      }
    });
    target.visit_action = Utils.findRequiredViewAsType(source, R.id.visit_action, "field 'visit_action'", TextView.class);
    view = Utils.findRequiredView(source, R.id.tv_selector_contacts, "method 'tvSelectorContacts'");
    view2131297007 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.tvSelectorContacts();
      }
    });
    view = Utils.findRequiredView(source, R.id.tv_update_address, "method 'update'");
    view2131297009 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.update();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    VisitFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mapView = null;
    target.tvLocation = null;
    target.edClineName = null;
    target.tv_visit_count = null;
    target.start_visit = null;
    target.visit_action = null;

    view2131296918.setOnClickListener(null);
    view2131296918 = null;
    view2131297007.setOnClickListener(null);
    view2131297007 = null;
    view2131297009.setOnClickListener(null);
    view2131297009 = null;
  }
}
