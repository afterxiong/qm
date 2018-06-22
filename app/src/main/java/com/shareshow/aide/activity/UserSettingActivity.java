package com.shareshow.aide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.db.GreenDaoManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiongchengguang on 2018/3/30.
 */

public class UserSettingActivity extends SimpleActivity {
    @BindView(R.id.user_phone)
    public TextView user_phone;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.user_edit)
    public Button edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        ButterKnife.bind(this);
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

    @Override
    protected void onResume(){
        super.onResume();
        String phone = CacheUserInfo.get().getUserPhone();
        String formatPhone = String.format(getResources().getString(R.string.user_phonenumer), phone);
        user_phone.setText(formatPhone);
    }

    @OnClick(R.id.user_phone)
    public void updatePhone() {
        Intent intent = new Intent(this, UpdatePhoneActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.user_edit)
    public void edit() {
        GreenDaoManager.deleteSQL();
        CacheUserInfo.get().clear();
        VisitCacheData.get().clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
