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
import android.widget.TextView;
import android.widget.Toast;
import com.bigkoo.pickerview.TimePickerView;
import com.shareshow.aide.R;
import com.shareshow.aide.adapter.VisitRecordFilterAdapter;
import com.shareshow.aide.dialog.LoadingProgress;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.mvp.presenter.VisitRecordFilterPresenter;
import com.shareshow.aide.mvp.view.VisitRecordFilterView;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.aide.util.ArrayUnique;
import com.shareshow.aide.util.Fixed;
import com.shareshow.db.AdFileDao;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.VisitDataDao;
import com.socks.library.KLog;
import org.greenrobot.greendao.query.QueryBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiongchengguang on 2018/3/16.
 */

public class VisitRecordFilterActivity extends BaseActivity<VisitRecordFilterView, VisitRecordFilterPresenter> implements VisitRecordFilterView, View.OnClickListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    public TextView toolbarTitle;
    @BindView(R.id.start_date)
    public TextView tvStartDate;
    @BindView(R.id.end_date)
    public TextView tvEndDate;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    private String userId;
    private String userName;
    private List<VisitData> visitDataList = new ArrayList<>();
    private VisitRecordFilterAdapter adapter;
    private LinearLayoutManager manager;
    private VisitDataDao dataDao = GreenDaoManager.getDaoSession().getVisitDataDao();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_record_filter);
        ButterKnife.bind(this);
        initData();
        initAdapter();
        query();
        presenter.getSyncVisitRecord();
        initToolbar();
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

    private void initData() {
        userId = getIntent().getStringExtra(Fixed.USER_ID);
        userName = getIntent().getStringExtra(Fixed.USER_NAME);
        String temp = String.format(getResources().getString(R.string.boolbar_title), userName);
        toolbarTitle.setText(temp);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        tvStartDate.setText(getPastDate(7));
        tvEndDate.setText(getPastDate(0));
    }

    /**
     * 获取过去几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    private void initAdapter(){
        recycler = findViewById(R.id.recycler);
        adapter = new VisitRecordFilterAdapter(visitDataList, this);
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object obj) {
                VisitData data = (VisitData) obj;
                Intent intent = new Intent(VisitRecordFilterActivity.this, ReadVisitRecordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("VisitData", data);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }



    @Override
    public void onDateFilterVisitRecordListener(VisitRecord record) {
        if (record == null) {
            return;
        }
        List<VisitData> visitData = record.getDatas();
        if (visitData != null) {
            List<VisitData> temp = ArrayUnique.unique(visitData);
            Collections.sort(temp, new ArrayUnique.VisitTrackVid());
            visitDataList.clear();
            visitDataList.addAll(temp);
            adapter.notifyDataSetChanged();
        }
    }

    public void query() {
        visitDataList.clear();
        String tvStartDateText = tvStartDate.getText().toString();
        String tvEndDateText = tvEndDate.getText().toString();
        QueryBuilder builder = dataDao.queryBuilder();
        builder.orderDesc(VisitDataDao.Properties.VrTimestart);
        builder.where(VisitDataDao.Properties.VrUrId.eq(userId));
        builder.where(VisitDataDao.Properties.VrDate.ge(tvStartDateText));
        builder.where(VisitDataDao.Properties.VrDate.le(tvEndDateText));
        List<VisitData> daoVisit = builder.list();
        for (VisitData data : daoVisit) {
            if (!visitDataList.contains(data)) {
                visitDataList.add(data);
            }
        }
        Collections.sort(visitDataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewSyncVisitData(List<VisitData> datas) {
        query();
    }

    @Override
    public void showLoading() {
        LoadingProgress.newInstance().show(getFragmentManager(), "1");
    }

    @Override
    public void hideLoading() {
        LoadingProgress.newInstance().dismiss();
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public VisitRecordFilterPresenter createPresenter() {
        return new VisitRecordFilterPresenter();
    }


    @Override
    public void onClick(View v) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        int year = endDate.get(Calendar.YEAR);
        int month = endDate.get(Calendar.MONTH);
        int day = endDate.get(Calendar.DAY_OF_MONTH);
        startDate.set(2017, 11, 01);
        endDate.set(year, month, day);
        if (v.getId() == R.id.start_date) {
            TimePickerView timePicker = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    try {
                        String startValue = getDateFormat().format(date.getTime());
                        String endValue = tvEndDate.getText().toString();
                        Date date1 = getDateFormat().parse(startValue);
                        Date date2 = getDateFormat().parse(endValue);
                        int flag = date1.compareTo(date2);
                        if (flag == -1 || flag == 0) {
                            if (dateDiff(date1.getTime(), date2.getTime())) {
                                tvStartDate.setText(startValue);
                                query();
                            } else {
                                Toast.makeText(VisitRecordFilterActivity.this, "最多只能查询90天数据", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(VisitRecordFilterActivity.this, "开始日期不能大于结束日期", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).setRangDate(startDate, endDate)
                    .isCenterLabel(false)
                    .setType(new boolean[]{true, true, true, false, false, false})
                    .build();
            timePicker.setDate(Calendar.getInstance());
            timePicker.show();
        } else if (v.getId() == R.id.end_date) {
            TimePickerView timePicker = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    try {
                        String startValue = tvStartDate.getText().toString();
                        String endValue = getDateFormat().format(date.getTime());
                        Date date1 = getDateFormat().parse(startValue);
                        Date date2 = getDateFormat().parse(endValue);
                        int flag = date1.compareTo(date2);
                        if (flag == -1 || flag == 0) {
                            if (dateDiff(date1.getTime(), date2.getTime())) {
                                tvEndDate.setText(endValue);
                                query();
                            } else {
                                Toast.makeText(VisitRecordFilterActivity.this, "最多只能查询90天数据", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(VisitRecordFilterActivity.this, "结束日期不能小于开始日期", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).setRangDate(startDate, endDate)
                    .isCenterLabel(false)
                    .setType(new boolean[]{true, true, true, false, false, false})
                    .build();
            timePicker.setDate(Calendar.getInstance());
            timePicker.show();
        }
    }


    public SimpleDateFormat getDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat;
    }

    public boolean dateDiff(long start, long end) {
        long diff = end - start;
        int day = (int) (diff / (1000 * 60 * 60 * 24));
        if (day < Fixed.MAX_DAY) {
            return true;
        } else {
            return false;
        }
    }
}

