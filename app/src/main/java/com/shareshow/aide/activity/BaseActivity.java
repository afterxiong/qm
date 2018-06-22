package com.shareshow.aide.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.shareshow.aide.R;
import com.shareshow.aide.mvp.presenter.BasePresenter;
import com.shareshow.aide.mvp.view.BaseView;
import com.shareshow.aide.widget.LoadingDialog;
import com.socks.library.KLog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import qiu.niorgai.StatusBarCompat;

/**
 * Created by xiongchengguang on 2017/12/5.
 */

public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity {

    protected P presenter;
    protected RxPermissions permissions;
    public LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        permissions = new RxPermissions(this);
        presenter = createPresenter();
        presenter.attachView((V) this);
        getPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(loadingDialog==null){
            loadingDialog=new LoadingDialog(this,"网络加载中");
        }
    }

    public abstract Toolbar getToolbar();

    public void initToolbar(){
        Toolbar toolbar=getToolbar();
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    public void getPermission(){
        permissions.requestEach(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                        Manifest.permission.WRITE_SETTINGS,
                        Manifest.permission.BLUETOOTH_ADMIN
                )
                .subscribe(permission -> {
                    if (permission.granted){
                        // 用户已经同意该权限
                        //KLog.d(permission.name + " is granted.");
                    }else if (permission.shouldShowRequestPermissionRationale){
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        KLog.d(permission.name + " is denied. More info should be provided.");
                        getPermission();
                    }else{
                        // 用户拒绝了该权限，并且选中『不再询问』
                        KLog.d(permission.name + " is denied.");
//                        Toast.makeText(BaseActivity.this, "拒绝后无法使用App", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    public abstract P createPresenter();

}
