// Generated code from Butter Knife. Do not modify!
package com.shareshow.aide.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TeamMenberActivity_ViewBinding implements Unbinder {
  private TeamMenberActivity target;

  private View view2131296945;

  @UiThread
  public TeamMenberActivity_ViewBinding(TeamMenberActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public TeamMenberActivity_ViewBinding(final TeamMenberActivity target, View source) {
    this.target = target;

    View view;
    target.swipe_refresh = Utils.findRequiredViewAsType(source, R.id.swipe_refresh, "field 'swipe_refresh'", SwipeRefreshLayout.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.recycler = Utils.findRequiredViewAsType(source, R.id.recycler, "field 'recycler'", RecyclerView.class);
    target.title = Utils.findRequiredViewAsType(source, R.id.title, "field 'title'", TextView.class);
    view = Utils.findRequiredView(source, R.id.team_show_window, "field 'team_show_window' and method 'showTeamWindow'");
    target.team_show_window = Utils.castView(view, R.id.team_show_window, "field 'team_show_window'", ImageView.class);
    view2131296945 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.showTeamWindow();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    TeamMenberActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.swipe_refresh = null;
    target.toolbar = null;
    target.recycler = null;
    target.title = null;
    target.team_show_window = null;

    view2131296945.setOnClickListener(null);
    view2131296945 = null;
  }
}
