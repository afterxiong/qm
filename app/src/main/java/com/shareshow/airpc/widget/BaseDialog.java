package com.shareshow.airpc.widget;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public abstract class BaseDialog extends DialogFragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = layout(inflater, container, savedInstanceState);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = (int) (dm.widthPixels * 0.75);
//        int height = (int) (dm.heightPixels * 0.6);
//        getDialog().getWindow().setLayout(width,height);

    }

    public abstract View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void initView(View view);

}
