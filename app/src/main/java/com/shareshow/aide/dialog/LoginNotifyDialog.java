package com.shareshow.aide.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shareshow.aide.R;

/**
 * Created by xiongchengguang on 2018/1/2.
 */

public class LoginNotifyDialog extends BaseDialog {
    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_dialog_notify, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void tvRoger(View view) {
        this.dismiss();
    }
}

