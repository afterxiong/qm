package com.shareshow.aide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.shareshow.aide.R;
import com.shareshow.aide.adapter.NotifyAdapter;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.mvp.presenter.MorePresenter;
import com.shareshow.aide.mvp.view.MoreView;
import com.shareshow.aide.retrofit.entity.AmplyNotify;
import com.shareshow.aide.widget.DefaultItemTouchHelpCallback;
import com.shareshow.aide.widget.DefaultItemTouchHelper;
import com.shareshow.db.AmplyNotifyDao;
import com.shareshow.db.GreenDaoManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通知
 * Created by xiongchengguang on 2017/12/12.
 */

public class NotificationActivity extends BaseActivity<MoreView, MorePresenter> implements MoreView, OnItemClickListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    private NotifyAdapter adapter;
    private List<AmplyNotify> listAns = new ArrayList<>();
    public static final int REQUEST_CODE = 666;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        initToolbar();
        initView();
        updateData();
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

    private void initView() {
        adapter = new NotifyAdapter(listAns);
        recycler.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycler.setLayoutManager(manager);
        adapter.setOnItemClickListener(this);
        DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(itemTouchHelpCallback);
        itemTouchHelper.attachToRecyclerView(recycler);
        itemTouchHelpCallback.setSwipeEnable(true);
    }


    private void updateData() {
        List<AmplyNotify> daoAns = GreenDaoManager
                .getDaoSession()
                .getAmplyNotifyDao()
                .queryBuilder()
                .orderDesc(AmplyNotifyDao.Properties.NosCreatetime)
                .list();
        listAns.clear();
        listAns.addAll(daoAns);
        adapter.notifyDataSetChanged();
    }

    private DefaultItemTouchHelpCallback itemTouchHelpCallback = new DefaultItemTouchHelpCallback(new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            AmplyNotify notify = listAns.get(adapterPosition);
            GreenDaoManager.getDaoSession().getAmplyNotifyDao().deleteByKey(notify.getNosId());
            listAns.remove(adapterPosition);
            adapter.notifyItemRemoved(adapterPosition);
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            return false;
        }
    });


    @Override
    public Toolbar getToolbar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public MorePresenter createPresenter() {
        return new MorePresenter();
    }


    @Override
    public void onItemClick(int position, Object obj) {
        AmplyNotify notify = (AmplyNotify) obj;
        Intent intent = new Intent(NotificationActivity.this, OpenNotifcationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("AmplyNotify", notify);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            updateData();
        }
    }
}
