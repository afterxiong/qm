// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
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

public class PersonalFragment_ViewBinding implements Unbinder {
  private PersonalFragment target;

  private View view2131296764;

  private View view2131296768;

  private View view2131296769;

  private View view2131296770;

  private View view2131296544;

  private View view2131296543;

  private View view2131296273;

  private View view2131296286;

  private View view2131296275;

  private View view2131296294;

  @UiThread
  public PersonalFragment_ViewBinding(final PersonalFragment target, View source) {
    this.target = target;

    View view;
    target.possess_org_dept_layout = Utils.findRequiredViewAsType(source, R.id.possess_org_dept_layout, "field 'possess_org_dept_layout'", LinearLayout.class);
    target.possess_name = Utils.findRequiredViewAsType(source, R.id.possess_name, "field 'possess_name'", TextView.class);
    target.possess_org_name = Utils.findRequiredViewAsType(source, R.id.possess_org_name, "field 'possess_org_name'", TextView.class);
    target.possess_cut = Utils.findRequiredViewAsType(source, R.id.possess_cut, "field 'possess_cut'", TextView.class);
    view = Utils.findRequiredView(source, R.id.possess_dept_name, "field 'possess_dept_name' and method 'possess_dept_name'");
    target.possess_dept_name = Utils.castView(view, R.id.possess_dept_name, "field 'possess_dept_name'", TextView.class);
    view2131296764 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.possess_dept_name();
      }
    });
    view = Utils.findRequiredView(source, R.id.possess_qr_code, "field 'possess_qr_code' and method 'possess_qr_code'");
    target.possess_qr_code = Utils.castView(view, R.id.possess_qr_code, "field 'possess_qr_code'", ImageView.class);
    view2131296768 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.possess_qr_code();
      }
    });
    target.possess_check = Utils.findRequiredViewAsType(source, R.id.possess_check, "field 'possess_check'", TextView.class);
    view = Utils.findRequiredView(source, R.id.possess_team_name, "field 'possess_team_name' and method 'possess_team_name'");
    target.possess_team_name = Utils.castView(view, R.id.possess_team_name, "field 'possess_team_name'", TextView.class);
    view2131296769 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.possess_team_name();
      }
    });
    view = Utils.findRequiredView(source, R.id.possess_team_qr_code, "field 'possess_team_qr_code' and method 'possess_team_qr_code'");
    target.possess_team_qr_code = Utils.castView(view, R.id.possess_team_qr_code, "field 'possess_team_qr_code'", ImageView.class);
    view2131296770 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.possess_team_qr_code();
      }
    });
    target.not_have_layout = Utils.findRequiredViewAsType(source, R.id.not_have_layout, "field 'not_have_layout'", LinearLayout.class);
    target.have_name = Utils.findRequiredViewAsType(source, R.id.have_name, "field 'have_name'", TextView.class);
    view = Utils.findRequiredView(source, R.id.have_create_team, "field 'have_create_team' and method 'have_create_team'");
    target.have_create_team = Utils.castView(view, R.id.have_create_team, "field 'have_create_team'", TextView.class);
    view2131296544 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.have_create_team();
      }
    });
    view = Utils.findRequiredView(source, R.id.have_add_team, "field 'have_add_team' and method 'have_add_team'");
    target.have_add_team = Utils.castView(view, R.id.have_add_team, "field 'have_add_team'", TextView.class);
    view2131296543 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.have_add_team();
      }
    });
    view = Utils.findRequiredView(source, R.id.aciotn_binding_devices, "field 'aciotn_binding_devices' and method 'aciotn_binding_devices'");
    target.aciotn_binding_devices = Utils.castView(view, R.id.aciotn_binding_devices, "field 'aciotn_binding_devices'", RelativeLayout.class);
    view2131296273 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.aciotn_binding_devices();
      }
    });
    view = Utils.findRequiredView(source, R.id.action_helper_propose, "field 'action_helper_propose' and method 'action_helper_propose'");
    target.action_helper_propose = Utils.castView(view, R.id.action_helper_propose, "field 'action_helper_propose'", RelativeLayout.class);
    view2131296286 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.action_helper_propose();
      }
    });
    view = Utils.findRequiredView(source, R.id.action_about, "field 'action_about' and method 'action_about'");
    target.action_about = Utils.castView(view, R.id.action_about, "field 'action_about'", RelativeLayout.class);
    view2131296275 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.action_about();
      }
    });
    target.action_new_message = Utils.findRequiredViewAsType(source, R.id.action_new_message, "field 'action_new_message'", RelativeLayout.class);
    view = Utils.findRequiredView(source, R.id.action_setting, "field 'action_setting' and method 'action_setting'");
    target.action_setting = Utils.castView(view, R.id.action_setting, "field 'action_setting'", RelativeLayout.class);
    view2131296294 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.action_setting();
      }
    });
    target.bind_device_name = Utils.findRequiredViewAsType(source, R.id.bind_device_name, "field 'bind_device_name'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PersonalFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.possess_org_dept_layout = null;
    target.possess_name = null;
    target.possess_org_name = null;
    target.possess_cut = null;
    target.possess_dept_name = null;
    target.possess_qr_code = null;
    target.possess_check = null;
    target.possess_team_name = null;
    target.possess_team_qr_code = null;
    target.not_have_layout = null;
    target.have_name = null;
    target.have_create_team = null;
    target.have_add_team = null;
    target.aciotn_binding_devices = null;
    target.action_helper_propose = null;
    target.action_about = null;
    target.action_new_message = null;
    target.action_setting = null;
    target.bind_device_name = null;

    view2131296764.setOnClickListener(null);
    view2131296764 = null;
    view2131296768.setOnClickListener(null);
    view2131296768 = null;
    view2131296769.setOnClickListener(null);
    view2131296769 = null;
    view2131296770.setOnClickListener(null);
    view2131296770 = null;
    view2131296544.setOnClickListener(null);
    view2131296544 = null;
    view2131296543.setOnClickListener(null);
    view2131296543 = null;
    view2131296273.setOnClickListener(null);
    view2131296273 = null;
    view2131296286.setOnClickListener(null);
    view2131296286 = null;
    view2131296275.setOnClickListener(null);
    view2131296275 = null;
    view2131296294.setOnClickListener(null);
    view2131296294 = null;
  }
}
