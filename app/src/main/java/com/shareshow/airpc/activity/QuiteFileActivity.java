package com.shareshow.airpc.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shareshow.aide.R;
import com.shareshow.airpc.fragment.BaseWQFragment;
import com.shareshow.airpc.fragment.LocalFileShare;
import com.shareshow.airpc.fragment.QQFragment;
import com.shareshow.airpc.fragment.WxFragment;
import com.shareshow.airpc.model.RootPoint;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by TEST on 2017/12/16.
 */

public class QuiteFileActivity extends ConnectLancherActivity implements BaseWQFragment.callBackActivity {

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.local_file)
    TextView localFile;
    @BindView(R.id.weixin)
    TextView weixin;
    @BindView(R.id.QQ)
    TextView QQ;
    @BindView(R.id.frame)
    FrameLayout frame;
    @BindView(R.id.popup_bg)
    TextView popupBg;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.weixin_line)
    TextView weixinLine;
    @BindView(R.id.qq_line)
    TextView qqLine;

    private GoogleApiClient client;
    private int index;
    private FragmentManager fragmentManager;
    private LocalFileShare fileShare;
    private QQFragment qqFile;
    private WxFragment wxFile;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quitefile);
        //	x.view().inject(this);
        ButterKnife.bind(this);
        initData();
        if (savedInstanceState != null) {
            fileShare = (LocalFileShare) fragmentManager.findFragmentByTag("fileShare");
            qqFile = (QQFragment) fragmentManager.findFragmentByTag("qqFile");
            wxFile = (WxFragment) fragmentManager.findFragmentByTag("wxFile");
            Log.i("test", "index:" + index);
            index = savedInstanceState.getInt("index");
        }
        showFragment(index);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void showFragment(int i) {
        index = i;
        // 更改底部控件状态
        colorSelection(i);
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (i) {
            case 0:
                if (fileShare == null) {
                    // 如果LocalFileFragment为空，则创建一个并添加到界面上
                    fileShare = new LocalFileShare();
                    transaction.add(R.id.frame, fileShare, "fileShare");
                } else {
                    // 如果LocalFileFragment不为空，则直接将它显示出来
                    transaction.show(fileShare);
                }
                fragment = fileShare;
                break;

            case 2:
                if (qqFile == null) {
                    // 如果LocalFileFragment为空，则创建一个并添加到界面上
                    qqFile = new QQFragment();
                    transaction.add(R.id.frame, qqFile, "qqFile");
                } else {
                    // 如果LocalFileFragment不为空，则直接将它显示出来
                    transaction.show(qqFile);
                }
                fragment = qqFile;
                break;


            case 1:
                if (wxFile == null) {
                    // 如果LocalFileFragment为空，则创建一个并添加到界面上
                    wxFile = new WxFragment();
                    transaction.add(R.id.frame, wxFile, "wxFile");
                } else {
                    // 如果LocalFileFragment不为空，则直接将它显示出来
                    transaction.show(wxFile);
                }
                fragment = wxFile;
                break;
        }
        transaction.commit();
    }

    private void colorSelection(int i) {
        localFile.setTextColor(getResources().getColor(R.color.black));
        weixin.setTextColor(getResources().getColor(R.color.black));
        QQ.setTextColor(getResources().getColor(R.color.black));
        switch (i) {
            case 0:
                localFile.setTextColor(getResources().getColor(R.color.click_font));
                break;
            case 1:
                weixin.setTextColor(getResources().getColor(R.color.click_font));
                break;
            case 2:
                QQ.setTextColor(getResources().getColor(R.color.click_font));
                break;

        }
    }

    private void hideFragments(FragmentTransaction transaction) {

        if (fileShare != null) {
            transaction.hide(fileShare);
        }
        if (wxFile != null) {
            transaction.hide(wxFile);
        }
        if (qqFile != null) {
            transaction.hide(qqFile);
        }
    }


    private void initData() {
        fragmentManager = getFragmentManager();
        if (isWeixinAvilible(this)) {
            weixin.setVisibility(View.VISIBLE);
            weixinLine.setVisibility(View.VISIBLE);
        } else {
            weixin.setVisibility(View.GONE);
            weixinLine.setVisibility(View.GONE);
        }

        if (isQQClientAvailable(this)) {
            QQ.setVisibility(View.VISIBLE);
            qqLine.setVisibility(View.VISIBLE);
        } else {
            QQ.setVisibility(View.GONE);
            qqLine.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.local_file)
    public void local() {
        showFragment(0);
    }

    @OnClick(R.id.weixin)
    public void Weixin() {
        showFragment(1);
    }

    @OnClick(R.id.QQ)
    public void QQ() {
        showFragment(2);
    }


    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                } else if (pn.equals("com.tencent.minihd.qq")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("index", index);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void passwdVertifyCallBack(RootPoint rp) {

    }


    public void back(View view) {
        onBackPressed();
    }


    @Override
    public void onDeviceName(RootPoint rp) {

    }

    public void hiddenCancel(int a) {
        //这里报空指针
        cancel.setVisibility(a);
    }

    @OnClick(R.id.cancel)
    public void cancel(View v) {

        if (wxFile != null) {
            wxFile.cancelSelect();
            cancel.setVisibility(View.GONE);
        }

        if (qqFile != null) {
            qqFile.cancelSelect();
            cancel.setVisibility(View.GONE);
        }
    }

}
