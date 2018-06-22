package com.shareshow.aide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shareshow.aide.R;
import com.shareshow.aide.adapter.VisitAdapter;
import com.shareshow.aide.dialog.FilterVisitDialog;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.mvp.presenter.VisitTrackPresenter;
import com.shareshow.aide.mvp.view.VisitTrackView;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.aide.util.ArrayUnique;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.widget.CustomTimePickerView;
import com.shareshow.airpc.util.AMapLocationManager;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.VisitDataDao;
import com.socks.library.KLog;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 客户拜访记录
 * Created by xiongchengguang on 2017/12/26.
 */

public class VisitTrackListActivity extends BaseActivity<VisitTrackView, VisitTrackPresenter> implements VisitTrackView, OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.map)
    public MapView mapView;
    @BindView(R.id.smart_refresh_layout)
    public SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    public LinearLayoutManager manager;
    public VisitAdapter adapter;
    @BindView(R.id.tv_select_time)
    public TextView tv_select_time;
    @BindView(R.id.im_select_user)
    public ImageView im_select_user;
    private AMap aMap;
    private List<VisitData> visitDataList = new ArrayList<>();
    private String userIds = null;
    private Set<String> defaultUserIds = new HashSet<>();
    private VisitDataDao dataDao = GreenDaoManager.getDaoSession().getVisitDataDao();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        ButterKnife.bind(this);
        initToolbar();
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        intiMap();
        initAdapter();
        initView();
        query();

    }

    public void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void initView() {
        smartRefreshLayout.setDragRate(0.8f);
        smartRefreshLayout.autoRefresh();
        smartRefreshLayout.setOnRefreshListener(this);
        smartRefreshLayout.setOnLoadMoreListener(this);
    }

    /**
     * 同步新的拜访记录
     *
     * @param datas
     */
    @Override
    public void onViewSyncVisitData(List<VisitData> datas) {
        String selectDate = tv_select_time.getText().toString();
        for (VisitData data : datas) {
            if (!visitDataList.contains(data)) {
                if (selectDate.isEmpty()) {
                    visitDataList.add(data);
                } else {
                    if (data.getVrDate().equals(selectDate)) {
                        visitDataList.add(data);
                    }
                }
            }
            if (defaultUserIds.size() > 0) {
                //如果不包含则移除
                if (!defaultUserIds.contains(data.getVrUrId())) {
                    visitDataList.remove(data);
                    KLog.d("移除:" + data.getVrPlanid());
                }
            }
        }
        Collections.sort(visitDataList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 下拉刷新
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(1000);
        presenter.getSyncVisitRecord();
        if (CacheUserInfo.get().getUserTeanCreate()) {
            im_select_user.setVisibility(View.VISIBLE);
        } else {
            im_select_user.setVisibility(View.GONE);
        }
    }

    public void query() {
        QueryBuilder builder = dataDao.queryBuilder();
        String date = tv_select_time.getText().toString();
        if (!date.isEmpty()) {
            builder.where(VisitDataDao.Properties.VrDate.eq(date));
        }
        if (defaultUserIds.size() > 0) {
            builder.where(VisitDataDao.Properties.VrUrId.in(defaultUserIds));
        }
        builder.orderDesc(VisitDataDao.Properties.VrTimestart);
        builder.limit(visitDataList.size() + 2);
        List<VisitData> daoVisit = builder.list();
        for (VisitData data : daoVisit) {
            if (!visitDataList.contains(data)) {
                visitDataList.add(data);
            }
        }
        Collections.sort(visitDataList);
        adapter.notifyDataSetChanged();
    }


    /**
     * 上拉加载更多
     *
     * @param refreshLayout
     */
    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore(1000);
        query();
    }


    private void initAdapter() {
        adapter = new VisitAdapter(visitDataList, this);
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object obj) {
                VisitData data = (VisitData) obj;
                Intent intent = new Intent(VisitTrackListActivity.this, ReadVisitRecordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("VisitData", data);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.im_select_user)
    public void selectUser(){
        FilterVisitDialog dialog = new FilterVisitDialog();
        dialog.show(getFragmentManager(), "1");
        dialog.setOnSelectListener(defaultUserIds, new FilterVisitDialog.OnSelectListener() {
            @Override
            public void select(Set<String> selectUserId, boolean checkAll) {
                getFilterUser(selectUserId, checkAll);
            }

        });
    }

    public void getFilterUser(Set<String> selectUserId, boolean checkAll) {
        visitDataList.clear();
        defaultUserIds = selectUserId;
        userIds = new GsonBuilder().serializeNulls().create().toJson(selectUserId);
        query();
    }


    @OnClick(R.id.tv_select_time)
    public void selectTime() {
        //时间选择器
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        int year = endDate.get(Calendar.YEAR);
        int month = endDate.get(Calendar.MONTH);
        int day = endDate.get(Calendar.DAY_OF_MONTH);
        startDate.set(2017, 11, 01);
        endDate.set(year, month, day);
        CustomTimePickerView timePicker = new CustomTimePickerView.Builder(this, new CustomTimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                visitDataList.clear();
                tv_select_time.setText(getToDay(date.getTime()));
                smartRefreshLayout.autoRefresh();
                query();
            }
        }, new CustomTimePickerView.OnTimeCancelListener() {
            @Override
            public void onTimeCancel(View v) {
                tv_select_time.setText("");
                smartRefreshLayout.autoRefresh();
            }
        }).setRangDate(startDate, endDate)
                .isCenterLabel(false)
                .setType(new boolean[]{true, true, true, false, false, false})
                .build();
        timePicker.setDate(Calendar.getInstance());
        timePicker.show();
    }

    public String getToDay(long currenTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String data = dateFormat.format(currenTime);
        return data;
    }

    private void intiMap() {
        AMapLocationManager.get().startLocation(aMap);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public VisitTrackPresenter createPresenter() {
        return new VisitTrackPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
