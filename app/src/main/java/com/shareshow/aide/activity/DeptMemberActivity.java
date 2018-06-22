package com.shareshow.aide.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.adapter.DeptMemberAdapter;
import com.shareshow.aide.impl.OnUserCheckListener;
import com.shareshow.aide.mvp.presenter.DeptMemberPresenter;
import com.shareshow.aide.mvp.view.DeptMemberView;
import com.shareshow.aide.retrofit.entity.DeptMember;
import com.shareshow.aide.retrofit.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by xiongchengguang on 2018/3/20.
 */

public class DeptMemberActivity extends BaseActivity<DeptMemberView, DeptMemberPresenter> implements DeptMemberView, SwipeRefreshLayout.OnRefreshListener, OnUserCheckListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.title)
    public TextView title;
    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    private List<DeptMember.DeptMemberInfo> deptMemberList = new ArrayList<>();
    private DeptMemberAdapter adapter;
    private LinearLayoutManager manager;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept_member_info);
        unbinder=  ButterKnife.bind(this);
        initToolbar();
        initView();
        autoRefreshing();
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


    private void autoRefreshing() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                presenter.getDeptMemberList();
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        presenter.getDeptMemberList();
    }

    private void initView() {
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(this);

        String deptName = getIntent().getStringExtra("deptName");
        if (deptName != null) {
            title.setText(deptName);
        }
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);
        adapter = new DeptMemberAdapter(this, deptMemberList);
        recycler.setAdapter(adapter);
        adapter.setOnExamineListener(this);
    }



    @Override
    public void agree(String urbrId, int position) {
        presenter.userCheck(urbrId,1);
    }

    @Override
    public void refuse(String urbrId, int position) {
        presenter.userCheck(urbrId,0);
    }


    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public DeptMemberPresenter createPresenter() {
        return new DeptMemberPresenter();
    }

    @Override
    public void setOnDeptMemberList(DeptMember deptMember) {
        refreshLayout.setRefreshing(false);
        if (deptMember != null) {
            deptMemberList.clear();
            List<DeptMember.DeptMemberInfo> list = deptMember.getDatas();
            deptMemberList.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserCheckListener(UserInfo userInfo) {
       presenter.getDeptMemberList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
