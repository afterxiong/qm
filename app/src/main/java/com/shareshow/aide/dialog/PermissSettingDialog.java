package com.shareshow.aide.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.util.PermissionUtil;
import com.shareshow.airpc.widget.BaseDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by xiongchengguang on 2017/11/13.
 */

public class PermissSettingDialog extends BaseDialog implements View.OnClickListener {

    public TextView content;
    public TextView confirm;
    public TextView cancel;
    private static String contentValue;

    public static PermissSettingDialog get(String contentValue, FragmentManager fragmentManager) {
        PermissSettingDialog.contentValue=contentValue;
        PermissSettingDialog dialog = new PermissSettingDialog();
        return dialog;
    }


    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_permiss_setting, container, false);
    }

    @Override
    public void initView(View view){
        content = view.findViewById(R.id.permission_content);
        confirm = view.findViewById(R.id.permission_confirm);
        cancel = view.findViewById(R.id.permission_cancel);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        String settingContetn = getResources().getString(R.string.permission_setting);
        contentValue = String.format(settingContetn,contentValue,contentValue);
        content.setText(contentValue);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.permission_cancel) {
               dismiss();
        } else if (v.getId() == R.id.permission_confirm) {
               dismiss();
               EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_PERMISSION_SETTING));
        }
    }



}
