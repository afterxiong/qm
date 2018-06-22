// Generated code from Butter Knife. Do not modify!
package com.shareshow.airpc.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.shareshow.aide.R;
import com.shareshow.airpc.menu.SwipeMenuListView;
import com.shareshow.airpc.widget.ProgressWheel;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LocalFileShare_ViewBinding implements Unbinder {
  private LocalFileShare target;

  private View view2131296463;

  private View view2131296305;

  private View view2131297037;

  private View view2131296730;

  private View view2131296468;

  private View view2131296629;

  @UiThread
  public LocalFileShare_ViewBinding(final LocalFileShare target, View source) {
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
    view = Utils.findRequiredView(source, R.id.album, "field 'album' and method 'album'");
    target.album = Utils.castView(view, R.id.album, "field 'album'", LinearLayout.class);
    view2131296305 = view;
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
    view = Utils.findRequiredView(source, R.id.other, "field 'other' and method 'other'");
    target.other = Utils.castView(view, R.id.other, "field 'other'", LinearLayout.class);
    view2131296730 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.other(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.download, "field 'download' and method 'download'");
    target.download = Utils.castView(view, R.id.download, "field 'download'", LinearLayout.class);
    view2131296468 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.download(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.list, "field 'list' and method 'onItemClick'");
    target.list = Utils.castView(view, R.id.list, "field 'list'", SwipeMenuListView.class);
    view2131296629 = view;
    ((AdapterView<?>) view).setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> p0, View p1, int p2, long p3) {
        target.onItemClick(p0, p1, p2, p3);
      }
    });
    target.without_share = Utils.findRequiredViewAsType(source, R.id.without_share, "field 'without_share'", TextView.class);
    target.wheel = Utils.findRequiredViewAsType(source, R.id.wheel, "field 'wheel'", ProgressWheel.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LocalFileShare target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.document = null;
    target.album = null;
    target.video = null;
    target.other = null;
    target.download = null;
    target.list = null;
    target.without_share = null;
    target.wheel = null;

    view2131296463.setOnClickListener(null);
    view2131296463 = null;
    view2131296305.setOnClickListener(null);
    view2131296305 = null;
    view2131297037.setOnClickListener(null);
    view2131297037 = null;
    view2131296730.setOnClickListener(null);
    view2131296730 = null;
    view2131296468.setOnClickListener(null);
    view2131296468 = null;
    ((AdapterView<?>) view2131296629).setOnItemClickListener(null);
    view2131296629 = null;
  }
}
