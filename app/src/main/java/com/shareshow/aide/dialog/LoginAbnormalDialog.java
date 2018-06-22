package com.shareshow.aide.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.shareshow.aide.R;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.Fixed;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.Buffer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录后异常情况
 * Created by xiongchengguang on 2018/1/2.
 */

public class LoginAbnormalDialog extends BaseDialog {
    @BindView(R.id.tv_abnorml)
    public TextView tvAbnorml;

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_dialog_login_abnormal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.7);
        int height = (int) (dm.heightPixels * 0.4);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            FileInputStream in = getActivity().openFileInput(Fixed.USER_LOGIN);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            UserInfo userInfo = new GsonBuilder().serializeNulls().create().fromJson(sb.toString(), UserInfo.class);
            if (userInfo.getReturnCode() == 7 || userInfo.getReturnCode() == 6 || userInfo.getReturnCode() == 5 || userInfo.getReturnCode() == 4 || userInfo.getReturnCode() == 3) {
                tvAbnorml.setText(userInfo.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tv_roger)
    public void tvRoger(View view) {
        this.dismiss();
    }
}

