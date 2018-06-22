package com.shareshow.aide.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.aide.dialog.LoadingProgress;
import com.shareshow.aide.mvp.presenter.LoginPresnter;
import com.shareshow.aide.mvp.view.LoginView;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiongchengguang on 2018/1/2.
 */

public class LoginActivity extends BaseActivity<LoginView, LoginPresnter> implements LoginView {

    @BindView(R.id.ed_code)
    public EditText edCode;

    @BindView(R.id.ed_phone)
    public EditText edPhone;

    @BindView(R.id.gain_code)
    public Button gainCode;

    @BindView(R.id.login)
    public Button login;

    @BindView(R.id.login_layout)
    public LinearLayout loginLayout;
    @BindView(R.id.entry_name_layout)
    public LinearLayout entryNameLayout;

    @BindView(R.id.entry_name)
    public EditText entryName;
    private String defaultCode = "123456";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        edPhone.setText(CacheConfig.get().getLastUserPhone());
        edPhone.setSelection(edPhone.getText().toString().length());
    }

    @OnClick(R.id.gain_code)
    public void gainCode() {
        String phone = edPhone.getText().toString();
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
            return;
        }
        presenter.gainCode(gainCode, phone);
    }


    @OnClick(R.id.login)
    public void btnLogin() {
        String phone = checkPhone();
        if (!phone.isEmpty()) {
            presenter.userLoing(phone);
        }
    }

    public String checkPhone() {
        String phone = edPhone.getText().toString();
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return "";
        }
        if (!defaultCode.equals(edCode.getText().toString())) {
            Toast.makeText(this, "验证码不正确", Toast.LENGTH_SHORT).show();
            return "";
        }
        if (phone.length() != 11) {
            Toast.makeText(this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
            return "";
        }

        return phone;
    }

    @OnClick(R.id.enter)
    public void enter() {
        String name = entryName.getText().toString();
        String phone = edPhone.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        presenter.commitUserName(phone, name);
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    public LoginPresnter createPresenter() {
        return new LoginPresnter();
    }


    @Override
    public void setTestCode(String code) {
        defaultCode = code;
    }

    @Override
    public void showLoading() {
        LoadingProgress.newInstance("登录中...").show(getFragmentManager(), "");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void hideLoading() {
        if (!isFinishing()) {
            LoadingProgress.newInstance().dismissAllowingStateLoss();
        }
    }

    @Override
    public void setLoginCompile(UserInfo userInfo) {
        if (userInfo == null) {
            edCode.setText("");
            Toast.makeText(this, "登录失败，请重试！", Toast.LENGTH_SHORT).show();
        } else {
            loginSuccess(userInfo);
        }
    }

    public void loginSuccess(UserInfo userInfo) {
        if (userInfo.getReturnCode() == 1) {
            CacheUserInfo.get().setUserInfo(userInfo);
            startMainActivity();
        } else if (userInfo.getReturnCode() == 2) {
            loginLayout.setVisibility(View.GONE);
            entryNameLayout.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "登录失败，请重试！", Toast.LENGTH_SHORT).show();
        }
    }


    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPrssed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long lastTime = 0;

    public void onBackPrssed() {
        long currenTime = System.currentTimeMillis();
        if (currenTime - lastTime < 2 * 1000) {
            finish();
        } else {
            lastTime = currenTime;
            Toast.makeText(this, getResources().getString(R.string.exit_tip), Toast.LENGTH_SHORT).show();
        }
    }
}

