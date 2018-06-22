package com.shareshow.aide.dialog;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;

import retrofit2.Retrofit;

public abstract class BaseDialog extends DialogFragment {
    public Api api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(api==null){
            Retrofit retrofit = RetrofitProvider.get();
            api = retrofit.create(Api.class);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return layout(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int width = (int) (dm.widthPixels * 0.7);
//        int height = (int) (dm.heightPixels * 0.5);
//        getDialog().getWindow().setLayout(width, height);
    }

    public abstract View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

}
