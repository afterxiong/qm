package com.shareshow.airpc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shareshow.aide.R;
import com.shareshow.airpc.socket.common.HelpItem;
import com.shareshow.airpc.widget.BaseActivity;
import com.shareshow.airpc.adapter.HelpAdapter;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qiu.niorgai.StatusBarCompat;

/**
 * Created by TEST on 2017/9/18.
 */

public class UserHelpActivity extends BaseActivity implements ExpandableListView.OnGroupExpandListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.phone)
    ImageView phone;
    @BindView(R.id.pc)
    ImageView pc;
    @BindView(R.id.tv)
    ImageView tv;
    @BindView(R.id.help_list)
    ExpandableListView helpList;
    @BindView(R.id.phone_item)
    LinearLayout phoneItem;
    @BindView(R.id.pc_2)
    ImageView pc2;
    @BindView(R.id.tv_2)
    ImageView tv2;
    @BindView(R.id.propose)
    ImageView propose;
    private ArrayList<HelpItem> phoneItems;
    private HelpAdapter adapter;
    private ArrayList<HelpItem> pcItems;
    private ArrayList<HelpItem> tvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_help);
        ButterKnife.bind(this);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        initData();
    }


    @OnClick(R.id.phone)
    public void phone(View v){
        pc.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        pc2.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.VISIBLE);
        adapter.setData(phoneItems,0);
        setSelect(-1);
    }

    @OnClick(R.id.pc)
    public void pc(View v){
        tv.setVisibility(View.GONE);
        pc2.setVisibility(View.GONE);
        tv2.setVisibility(View.VISIBLE);
        adapter.setData(pcItems,1);
        setSelect(-1);
    }

    @OnClick(R.id.tv)
    public void tv(View v){
        tv2.setVisibility(View.GONE);
        adapter.setData(tvItems,2);
        setSelect(-1);
    }

    @OnClick(R.id.pc_2)
    public void pc2(View v){
        tv.setVisibility(View.GONE);
        pc.setVisibility(View.VISIBLE);
        pc2.setVisibility(View.GONE);
        tv2.setVisibility(View.VISIBLE);
        adapter.setData(pcItems,1);
        setSelect(-1);
    }

    @OnClick(R.id.tv_2)
    public void tv2(View v){
        tv.setVisibility(View.VISIBLE);
        pc.setVisibility(View.VISIBLE);
        pc2.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        adapter.setData(tvItems,2);
        setSelect(-1);
    }

    @OnClick(R.id.back)
    public void back(View v){
        finish();
    }


    private void initData(){
        getPhoneData();
        getPcData();
        getTvData();
        adapter = new HelpAdapter(phoneItems,this);
        adapter.setData(phoneItems,0);
        helpList.setAdapter(adapter);
        helpList.setOnGroupExpandListener(this);
        helpList.setGroupIndicator(null);
        pc.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        pc2.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.VISIBLE);
    }

    private void getPcData() {
        pcItems = new ArrayList<HelpItem>();
        ArrayList<String> items=new ArrayList<String>();
        items.add(getResources().getString(R.string.pc1_1));
        items.add(getResources().getString(R.string.pc1_2));
        pcItems.add(new HelpItem(getResources().getString(R.string.pc1),items));
        ArrayList<String> items1=new ArrayList<String>();
        items1.add(getResources().getString(R.string.pc2_1));
        pcItems.add(new HelpItem(getResources().getString(R.string.pc2),items1));
        ArrayList<String> items2=new ArrayList<String>();
        items2.add(getResources().getString(R.string.pc3_1));
        items2.add(getResources().getString(R.string.pc3_2));
        items2.add(getResources().getString(R.string.pc3_3));
        pcItems.add(new HelpItem(getResources().getString(R.string.pc3),items2));
        ArrayList<String> items3=new ArrayList<String>();
        items3.add(getResources().getString(R.string.pc4_1));
        items3.add(getResources().getString(R.string.pc4_2));
        pcItems.add(new HelpItem(getResources().getString(R.string.pc4),items3));

    }

    private void getTvData(){
        tvItems = new ArrayList<HelpItem>();
        ArrayList<String> items=new ArrayList<String>();
        items.add(getResources().getString(R.string.tv1_1));
        items.add(getResources().getString(R.string.tv1_2));
        items.add(getResources().getString(R.string.tv1_3));
        tvItems.add(new HelpItem(getResources().getString(R.string.tv1),items));
        ArrayList<String> items1=new ArrayList<String>();
        items1.add(getResources().getString(R.string.tv2_1));
        tvItems.add(new HelpItem(getResources().getString(R.string.tv2),items1));
        ArrayList<String> items2=new ArrayList<String>();
        items2.add(getResources().getString(R.string.tv3_1));
        tvItems.add(new HelpItem(getResources().getString(R.string.tv3),items2));
        ArrayList<String> items3=new ArrayList<String>();
        items3.add(getResources().getString(R.string.tv4_1));
        items3.add(getResources().getString(R.string.tv4_2));
        tvItems.add(new HelpItem(getResources().getString(R.string.tv4),items3));
        ArrayList<String> items4=new ArrayList<String>();
        items4.add(getResources().getString(R.string.tv5_1));
        tvItems.add(new HelpItem(getResources().getString(R.string.tv5),items4));

    }

    private void getPhoneData(){
        phoneItems = new ArrayList<HelpItem>();
        ArrayList<String> items=new ArrayList<String>();
        items.add(getResources().getString(R.string.phone1_1));
        items.add(getResources().getString(R.string.phone1_2));
        items.add(getResources().getString(R.string.phone1_3));
        phoneItems.add(new HelpItem(getResources().getString(R.string.phone1),items));
        ArrayList<String> items1=new ArrayList<String>();
        items1.add(getResources().getString(R.string.phone2_1));
        items1.add(getResources().getString(R.string.phone2_2));
        phoneItems.add(new HelpItem(getResources().getString(R.string.phone2),items1));
        ArrayList<String> items2=new ArrayList<String>();
        items2.add(getResources().getString(R.string.phone3_1));
        phoneItems.add(new HelpItem(getResources().getString(R.string.phone3),items2));
        ArrayList<String> items3=new ArrayList<String>();
        items3.add(getResources().getString(R.string.phone4_1));
        items3.add(getResources().getString(R.string.phone4_2));
        phoneItems.add(new HelpItem(getResources().getString(R.string.phone4),items3));
        ArrayList<String> items4=new ArrayList<String>();
        items4.add(getResources().getString(R.string.phone5_1));
        items4.add(getResources().getString(R.string.phone5_2));
        items4.add(getResources().getString(R.string.phone5_3));
        phoneItems.add(new HelpItem(getResources().getString(R.string.phone5),items4));
        ArrayList<String> items5=new ArrayList<String>();
        items5.add(getResources().getString(R.string.phone6_1));
        phoneItems.add(new HelpItem(getResources().getString(R.string.phone6),items5));
        ArrayList<String> items6=new ArrayList<String>();
        items6.add(getResources().getString(R.string.phone7_1));
        items6.add(getResources().getString(R.string.phone7_2));
        items6.add(getResources().getString(R.string.phone7_3));
        phoneItems.add(new HelpItem(getResources().getString(R.string.phone7),items6));
    }

    @Override
    public void onGroupExpand(int groupPosition){
        setSelect(groupPosition);
    }

    private void setSelect(int groupPosition){
        for (int i = 0; i < adapter.getGroupCount(); i++){
            if(i!=groupPosition){
                helpList.collapseGroup(i);
            }
        }
    }

    @OnClick(R.id.propose)
    public void propose(View v){
        startActivity(new Intent(this,ProposeActivity.class));
        finish();
    }
}
