package com.shareshow.aide.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shareshow.aide.R;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.Fixed;
import com.socks.library.KLog;
import com.xcg.ScanActivity1;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by xiongchengguang on 2018/1/2.
 */

public class LoginSuccessDialog extends BaseDialog {

    private Unbinder unbinder;

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.7);
        int height = (int) (dm.heightPixels * 0.65);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_dialog_login_success, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.iv_binding)
    public void binding(View view) {
        CacheConfig.get().setBinding(true);
        Intent intent=new Intent(getActivity(), ScanActivity1.class);
        intent.putExtra(Fixed.SCAN_TYPE,Fixed.TAG_BINDING);
        startActivityForResult(intent,Fixed.TAG_BINDING);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Fixed.TAG_BINDING){
            this.dismiss();
            String ids=data.getStringExtra(Fixed.SCAN_RESULT);
            BindingDeviceDialog deviceDialog=new BindingDeviceDialog();
            Bundle args=new Bundle();
            args.putString(Fixed.TAG_PHONE_VALUE,ids);
            deviceDialog.setArguments(args);
            deviceDialog.setCancelable(false);
            deviceDialog.show(getFragmentManager(),"deviceDialog");
        }
    }



    @OnClick(R.id.next_use)
    public void nextUse(View view){
        this.dismiss();
        KLog.d("下次再说");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
