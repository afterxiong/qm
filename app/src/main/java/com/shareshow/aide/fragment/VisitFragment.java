package com.shareshow.aide.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.activity.NearbyActivity;
import com.shareshow.aide.activity.VisitCommitActivity;
import com.shareshow.aide.mvp.presenter.VisitPresenter;
import com.shareshow.aide.mvp.view.VisitView;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.VisitDataDao;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 客户拜访
 * Created by xiongchengguang on 2017/12/7.
 */

public class VisitFragment extends BaseFragment<VisitView, VisitPresenter> implements VisitView {

    @BindView(R.id.map)
    public MapView mapView;
    @BindView(R.id.tv_location)
    public TextView tvLocation;
    @BindView(R.id.ed_cline_name)
    public EditText edClineName;
    @BindView(R.id.tv_visit_count)
    public TextView tv_visit_count;
    @BindView(R.id.start_visit)
    public RelativeLayout start_visit;
    @BindView(R.id.visit_action)

    public TextView visit_action;

    public static final int BACK_NEW_ADDRESS = 100;

    private AMapLocation aMapLocation;

    private AMap aMap;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        presenter.startLocation(aMap);
        presenter.getSyncVisitRecord();
    }

    @Override
    public VisitPresenter createPresenter() {
        return new VisitPresenter(getActivity());
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_visit;
    }

    @Override
    public void setLocationText(AMapLocation amapLocation) {
        this.aMapLocation = amapLocation;
        tvLocation.setText(amapLocation.getPoiName());
    }

    @Override
    public void setSelectorContacts(String usernumber) {
        edClineName.setText(usernumber);
    }

    /**
     * 检测拜访是否进行中
     */

    public void checkVisitWay() {
        VisitCacheData cacheData = VisitCacheData.get();
        if (cacheData.isVisitWay()) {
            visit_action.setText("结束拜访");
            tv_visit_count.setText("拜访中...");
            start_visit.setTag(1);
        } else {
            visit_action.setText("签到");
            start_visit.setTag(0);
            int count = GreenDaoManager
                    .getDaoSession()
                    .getVisitDataDao()
                    .queryBuilder()
                    .where(VisitDataDao.Properties.VrDate.eq(Fixed.getToDay()))
                    .where(VisitDataDao.Properties.VrUrId.eq(CacheUserInfo.get().getUserId()))
                    .list()
                    .size();
            tv_visit_count.setText("今日拜访" + count + "次");
        }
//        if (cacheData.isVisitWay()) {
//            VisitWayDialog dialog = new VisitWayDialog();
//            dialog.setOnClickListener(new VisitWayDialog.OnClickListener() {
//                @Override
//                public void confirm() {
//                    dialog.dismiss();
//                    enterVisitCommit();
//                }
//
//                @Override
//                public void cancel() {
//                    dialog.dismiss();
//                    cacheData.clear();
//                }
//            });
//            dialog.setCancelable(false);
//            dialog.show(getActivity().getFragmentManager(), "dialog");
//        }
    }

    @OnClick(R.id.start_visit)
    public void start_visit() {
        int tag = (int) start_visit.getTag();
        if (tag == 1) {
            enterVisitCommit();
        } else {
            String name = edClineName.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(App.getApp(), "客户姓名不能为空", Toast.LENGTH_LONG).show();
                return;
            }

            if (aMapLocation == null) {
                Toast.makeText(App.getApp(), "定位失败,不能签到", Toast.LENGTH_LONG).show();
                return;
            }

            String location = tvLocation.getText().toString();
            if (location.isEmpty()) {
                Toast.makeText(App.getApp(), "定位不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            VisitCacheData data = VisitCacheData.get();
            String vrGuestname = edClineName.getText().toString();

            long currenTime = System.currentTimeMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            data.setVrId(String.valueOf(currenTime));
            data.setVrUrId(CacheUserInfo.get().getUserId());
            data.setVrTimestart(String.valueOf(currenTime));
            data.setVrTimeend(String.valueOf(currenTime + 1));
            data.setVrGuestname(vrGuestname);
            data.setVrAddresss(location);
            data.setVrGps(aMapLocation.getLongitude() + "<#>" + aMapLocation.getLatitude());
            data.setVrContent("");
            data.setVrDate(dateFormat.format(currenTime));
            enterVisitCommit();
        }
    }

    /**
     * 进入拜访记录提交模块
     */
    public void enterVisitCommit() {
        VisitCacheData.get().setVisitWay(true);
        Intent intent = new Intent(getActivity(), VisitCommitActivity.class);
        startActivity(intent);
    }

    /**
     * 选择联系人
     */
    @OnClick(R.id.tv_selector_contacts)
    public void tvSelectorContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Fixed.PICK_CONTACT_REQUEST);
    }

    @OnClick(R.id.tv_update_address)
    public void update() {
        Intent intent = new Intent(getActivity(), NearbyActivity.class);
        startActivityForResult(intent, BACK_NEW_ADDRESS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == BACK_NEW_ADDRESS) {
            String newLocation = data.getStringExtra("newLocation");
            tvLocation.setText(newLocation);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == Fixed.PICK_CONTACT_REQUEST) {
            presenter.onActivityResultContacts(data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkVisitWay();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
