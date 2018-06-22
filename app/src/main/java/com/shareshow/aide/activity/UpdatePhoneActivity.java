package com.shareshow.aide.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.dialog.LoadingProgress;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.airpc.util.Cache;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shareshow.aide.mvp.model.SimpleModel.TAG_PROOF_CODE;
import static com.shareshow.aide.mvp.model.SimpleModel.TAG_UPDATE_PHONE_RESULT;

/**
 * Created by xiongchengguang on 2018/3/30.
 */

public class UpdatePhoneActivity extends SimpleActivity {
    @BindView(R.id.update_phone_tip)
    public TextView update_phone_tip;
    @BindView(R.id.binding_phone)
    public TextView binding_phone;
    @BindView(R.id.update_phone1)
    public RelativeLayout update_phone1;
    @BindView(R.id.update_phone2)
    public LinearLayout update_phone2;
    @BindView(R.id.update_edit_phone)
    public EditText editValue;
    @BindView(R.id.countdown)
    public TextView countdown;
    @BindView(R.id.update_phone_step)
    public Button update_phone_step;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    private static String defaultCode = "123456";
    private String newPhone = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone);
        ButterKnife.bind(this);
        initToolbar();
        String userPhone = CacheUserInfo.get().getUserPhone();
        binding_phone.setText(userPhone);
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

    @OnClick(R.id.update_phone_btn)
    public void updatePhone() {
        update_phone1.setVisibility(View.GONE);
        update_phone2.setVisibility(View.VISIBLE);
        editValue.setHint("请输入手机号码");
    }

    @OnClick(R.id.update_phone_step)
    public void updateStep(){
        if (update_phone_step.getText().equals(getResources().getString(R.string.update_phone_text))) {
            if (!checkPhone()) {
                Toast.makeText(App.getApp(), "请输入正确的手机号码", Toast.LENGTH_LONG).show();
            }
        } else if (update_phone_step.getText().equals(getResources().getString(R.string.update_phone_compile))) {
            String proolCode = editValue.getText().toString();
            if (defaultCode.equals(proolCode)) {
                LoadingProgress.newInstance("加载中...").show(getFragmentManager(), "1");
                presenter.updateUserPhone(newPhone);
            } else {
                Toast.makeText(App.getApp(), "验证码错误", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPhone(){
        String phone = editValue.getText().toString();
        if (phone.length() == 11) {
            editValue.setHint("请输入验证码");
            newPhone = phone;
            LoadingProgress.newInstance("加载中...").show(getFragmentManager(), "1");
            presenter.getProofCode(phone);
            presenter.getCountdown(countdown);
            return true;
        }
        return false;
    }

    @OnClick(R.id.countdown)
    public void countdown(){
        presenter.getCountdown(countdown);
    }


    @Override
    public void getResult(int tag, Object obj) {
        LoadingProgress.newInstance().dismiss();
        if (tag == TAG_PROOF_CODE) {
            countdown.setVisibility(View.VISIBLE);
            update_phone_tip.setText("验证码已发送到"+editValue.getText().toString());
            editValue.setText("");
            update_phone_step.setText(getResources().getString(R.string.update_phone_compile));
            defaultCode = (String) obj;
        } else if (tag == TAG_UPDATE_PHONE_RESULT) {
            finish();
        }
    }

    @Override
    public void error(int tag) {
        super.error(tag);
        LoadingProgress.newInstance().dismiss();
        if (tag == TAG_PROOF_CODE) {
            Toast.makeText(App.getApp(), "验证码获取失败,请重新获取", Toast.LENGTH_LONG).show();
        } else if (tag == TAG_UPDATE_PHONE_RESULT) {
            finish();
        }
    }

}
