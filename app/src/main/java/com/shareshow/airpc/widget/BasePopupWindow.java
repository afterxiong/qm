package com.shareshow.airpc.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.shareshow.aide.R;


/**
 * 更多设置
 */
public abstract class BasePopupWindow extends PopupWindow {

    public BasePopupWindow(Context context) {
        super(context);
        LayoutInflater inflater = inflater = LayoutInflater.from(context);
        View view = inflater(inflater);
        initView(view);
        setPopupStyle(view);
    }


    public void setPopupStyle(View view) {
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.timepopwindow_anim_style);
        setContentView(view);
    }

    public abstract View inflater(LayoutInflater inflater);


    public abstract void initView(View view);

}
