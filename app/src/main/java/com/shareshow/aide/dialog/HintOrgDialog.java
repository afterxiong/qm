package com.shareshow.aide.dialog;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shareshow.aide.R;

/**
 * Created by xiongchengguang on 2018/1/11.
 */

public class HintOrgDialog extends BaseDialog {
    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_binding_org,container,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HintOrgDialog.this.dismiss();
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 1.0);
        int height = (int) (dm.heightPixels *1.0);
        getDialog().getWindow().setLayout(width, height);
    }

}
