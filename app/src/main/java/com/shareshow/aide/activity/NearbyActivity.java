package com.shareshow.aide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.services.core.PoiItem;
import com.shareshow.aide.R;
import com.shareshow.airpc.util.AMapLocationManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import qiu.niorgai.StatusBarCompat;

/**
 * 更多地址选择
 * Created by xiongchengguang on 2017/12/21.
 */

public class NearbyActivity extends AppCompatActivity {
    @BindView(R.id.map)
    public MapView mapView;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    private List<String> addressList = new ArrayList<>();
    private NearbyAdapter adapter;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.toolbar_background));
        setContentView(R.layout.activity_nearby);
        ButterKnife.bind(this);
        initToolbar();
        mapView.onCreate(savedInstanceState);
        intiMap();
        initAdapter();

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


    private void initAdapter() {
        adapter = new NearbyAdapter();
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);
    }


    private void intiMap() {
        AMap aMap = mapView.getMap();//初始化地图控制器对象
        AMapLocationManager.get().startLocation(aMap);
        AMapLocationManager.get().setManagerOnPoiSearchListener(new AMapLocationManager.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(List<PoiItem> list) {
                addressList.clear();
                for (PoiItem poiItem : list) {
                    addressList.add(poiItem.getTitle());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
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

    class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.NearbyHolder> implements View.OnClickListener {

        @Override
        public NearbyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_address, parent, false);
            NearbyHolder holder = new NearbyHolder(view);
            view.setOnClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(NearbyHolder holder, int position) {
            holder.itemView.setTag(position);
            holder.address.setText(addressList.get(position));
        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Intent intent = new Intent();
            intent.putExtra("newLocation", addressList.get(position));
            setResult(100, intent);
            finish();
        }

        class NearbyHolder extends RecyclerView.ViewHolder {

            private TextView address;

            public NearbyHolder(View itemView) {
                super(itemView);
                address = itemView.findViewById(R.id.tv_address);
            }
        }
    }
}
