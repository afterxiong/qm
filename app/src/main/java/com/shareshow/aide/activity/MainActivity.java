package com.shareshow.aide.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.aide.dialog.CreateTeamWindow;
import com.shareshow.aide.dialog.DevConrtolWindow;
import com.shareshow.aide.dialog.HintDevicesDialog;
import com.shareshow.aide.dialog.HintOrgDialog;
import com.shareshow.aide.event.ControlEvent;
import com.shareshow.aide.mvp.presenter.MainPresenter;
import com.shareshow.aide.mvp.view.MainView;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.service.AudioRecordService;
import com.shareshow.aide.service.PlayService;
import com.shareshow.aide.service.HeartBeatServer;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.adapter.MainFragmentPagerAdapter;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.activity.SettingActivity;
import com.shareshow.airpc.ports.PositionListener;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMDialog;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiongchengguang on 2017/12/5.
 */

public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView, TabLayout.OnTabSelectedListener {

    @BindView(R.id.tablayout)
    public TabLayout tabLayout;
    @BindView(R.id.viewpager)
    public ViewPager pager;
    @BindView(R.id.im_ind)
    public ImageView imInd;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.menu)
    public View menu;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.in_visit)
    public View visit;
    private String[] titles;

    private TextView tab_text;
    private ImageView tab_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initView();
        initService();
    }


    private void initService() {
        Intent audioServer = new Intent(this, AudioRecordService.class);
        startService(audioServer);
        Intent palyServer = new Intent(this, PlayService.class);
        startService(palyServer);
    }

    @Override
    public void setUserLogin(UserInfo userInfo) {
        if (userInfo.getData().getOrgId() == null) {
            if (CacheConfig.get().getHitorg()) {
                CacheConfig.get().saveHintOrg(false);
                HintOrgDialog orgDialog = new HintOrgDialog();
                orgDialog.show(getFragmentManager(), "orgDialog");
            }
        } else {
            if (CacheConfig.get().getHintDevice()) {
                CacheConfig.get().saveHintDevice(false);
                HintDevicesDialog bindingDialog = new HintDevicesDialog();
                bindingDialog.show(getFragmentManager(), "bindingDialog");
            }
        }
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public void initView(){
        titles = getResources().getStringArray(R.array.tab_title);
        title.setText(titles[0]);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(new MainFragmentPagerAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(pager);
        tabLayout.addOnTabSelectedListener(this);
        String[] title = getResources().getStringArray(R.array.tab_title);
        tabLayout.getTabAt(0).setCustomView(getTabView(title[0], R.drawable.main_work_selector));
        tabLayout.getTabAt(1).setCustomView(getTabView(title[1], R.drawable.main_morning_selector));
        tabLayout.getTabAt(2).setCustomView(getTabView(title[2], R.drawable.main_custome_selector));
        tabLayout.getTabAt(3).setCustomView(getTabView(title[3], R.drawable.main_personal_selector));
//        tabLayout.getTabAt(0).setIcon(R.drawable.main_work_selector);
//        tabLayout.getTabAt(1).setIcon(R.drawable.main_morning_selector);
//        tabLayout.getTabAt(2).setIcon(R.drawable.main_custome_selector);
//        tabLayout.getTabAt(3).setIcon(R.drawable.main_personal_selector);
        menu.setVisibility(View.VISIBLE);
        //默认第一个不显示颜色，迂回一下
        pager.setCurrentItem(1);
        pager.setCurrentItem(0);
    }

    public View getTabView(String title, int image_src) {
        View v = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);
        tab_text = (TextView) v.findViewById(R.id.tab_text);
        tab_text.setText(title);
        tab_icon = (ImageView) v.findViewById(R.id.tab_icon);
        tab_icon.setImageResource(image_src);
        return v;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        changeTabSelect(tab);
        if (tab == tabLayout.getTabAt(0)) {
            menu.setVisibility(View.VISIBLE);
            visit.setVisibility(View.GONE);
            imInd.setVisibility(View.GONE);
            title.setText(titles[0]);
        } else if (tab == tabLayout.getTabAt(2)){
            menu.setVisibility(View.GONE);
            visit.setVisibility(View.VISIBLE);
            imInd.setVisibility(View.GONE);
            title.setText(titles[2]);
        } else if (tab == tabLayout.getTabAt(3)){
            menu.setVisibility(View.GONE);
            visit.setVisibility(View.GONE);
            imInd.setVisibility(View.VISIBLE);
            title.setText(titles[3]);
        } else {
            menu.setVisibility(View.GONE);
            visit.setVisibility(View.GONE);
            imInd.setVisibility(View.GONE);
            title.setText(titles[1]);
        }
    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        changeTabNormal(tab);
    }

    public void changeTabSelect(TabLayout.Tab tab){
        View v = tab.getCustomView();
        tab_text = (TextView) v.findViewById(R.id.tab_text);
        tab_text.setTextColor(getResources().getColor(R.color.colorPrimary));
    }


    public void changeTabNormal(TabLayout.Tab tab) {
        View v = tab.getCustomView();
        tab_text = (TextView) v.findViewById(R.id.tab_text);
        tab_text.setTextColor(getResources().getColor(R.color.screen_backgroundt));
    }

    @OnClick(R.id.im_ind)
    public void openInd() {
        CreateTeamWindow window = new CreateTeamWindow(getFragmentManager(), this);
        window.showAsDropDown(imInd);
    }

    @OnClick(R.id.menu)
    public void openMenu() {
        DevConrtolWindow window = new DevConrtolWindow(this, new String[]{"投屏设置", "启目终端互动"});
        window.setItemClickListener(new DevConrtolWindow.OnItemClickListener() {
            @Override
            public void OnItemClick(int index, String timeString) {
                if (HeartBeatServer.istp) {
                    new QMDialog(MainActivity.this, R.string.close_tp_tip5, R.string.yes, new PositionListener() {
                        @Override
                        public void method(int position) {
                            ControlEvent controlEvent = new ControlEvent();
                            controlEvent.controlCod = 2;
                            EventBus.getDefault().post(controlEvent);
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                    return;
                }
                if (index == 0) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, SettingActivity.class));
                } else if (index == 1) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, MoreScreenActivity.class));
                }
            }
        });
        window.showAsDropDown(menu);
    }

    @OnClick(R.id.in_visit)
    public void openVisitMap() {
        Intent intent = new Intent(this, VisitTrackListActivity.class);
        startActivity(intent);
    }

//    @OnClick(R.id.im_track)
//    public void imTrack() {
//        Intent intent = new Intent(this, VisitTrackListActivity.class);
//        startActivity(intent);
//    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPrssed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long lastTime = 0;

    public void onBackPrssed() {
        long currenTime = System.currentTimeMillis();
        if (currenTime - lastTime < 2 * 1000) {
            super.onBackPressed();
        } else {
            if (HeartBeatServer.istp) {
                new QMDialog(this, R.string.close_tp_tip5, R.string.yes, new PositionListener() {
                    @Override
                    public void method(int position) {
                        ControlEvent controlEvent = new ControlEvent();
                        controlEvent.controlCod = 2;
                        EventBus.getDefault().post(controlEvent);
                    }

                    @Override
                    public void cancel() {

                    }
                });
                return;
            }
            lastTime = currenTime;
            Toast.makeText(this, getResources().getString(R.string.exit_tip), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent playService = new Intent(this, PlayService.class);
        stopService(playService);
        Intent audioService = new Intent(this, AudioRecordService.class);
        stopService(audioService);
    }
}
