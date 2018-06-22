package com.shareshow.aide.dialog;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shareshow.aide.R;

/**
 * Created by xiongchengguang on 2018/1/19.
 */

public class LoadingProgress extends BaseDialog {

    private static String title = "加载中...";


    private static LoadingProgress loadingProgress;

    public static LoadingProgress newInstance(String args) {
        if (loadingProgress == null) {
            loadingProgress = new LoadingProgress();
            loadingProgress.setCancelable(false);
        }
        LoadingProgress.title = args;
        return loadingProgress;
    }

    public static LoadingProgress newInstance() {
        if (loadingProgress == null) {
            loadingProgress = new LoadingProgress();
            loadingProgress.setCancelable(false);
        }
        return loadingProgress;
    }

    @Override
    public void onResume(){
        super.onResume();
        getDialog().getWindow().setLayout(px2dp(160), px2dp(160));
    }

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading_progress, container, false);
        TextView progressText = view.findViewById(R.id.progress_tip);
        if (!title.isEmpty()) {
            progressText.setText(title);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int px2dp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }
}
