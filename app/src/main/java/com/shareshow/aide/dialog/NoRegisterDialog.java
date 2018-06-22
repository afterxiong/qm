package com.shareshow.aide.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shareshow.aide.R;
import com.shareshow.aide.util.Fixed;
import com.socks.library.KLog;
import com.xcg.ScanActivity1;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by xiongchengguang on 2018/1/3.
 */

public class NoRegisterDialog extends BaseDialog {
    private Unbinder unbinder;
    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_dialog_no_register,container,false);
        unbinder=ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick(R.id.iv_scan_code)
    public void scnCode(View view) {
        Intent intent=new Intent(getActivity(), ScanActivity1.class);
        intent.putExtra(Fixed.SCAN_TYPE,Fixed.TAG_ADD_DEPT);
        startActivityForResult(intent,Fixed.TAG_ADD_DEPT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Fixed.TAG_ADD_DEPT){
            this.dismiss();
            String scanPhone=data.getStringExtra(Fixed.SCAN_RESULT);
            BindingOrgDialog orgDialog=new BindingOrgDialog();
            Bundle args=new Bundle();
            args.putString(Fixed.TAG_PHONE_VALUE,scanPhone);
            orgDialog.setArguments(args);
            orgDialog.setCancelable(false);
            orgDialog.show(getFragmentManager(),"orgDialog");
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
