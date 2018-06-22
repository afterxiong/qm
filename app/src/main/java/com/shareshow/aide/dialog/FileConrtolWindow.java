package com.shareshow.aide.dialog;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.shareshow.aide.R;
import com.shareshow.airpc.widget.BasePopupWindow;

/**
 * Created by Administrator on 2018/4/2 0002.
 */

public class FileConrtolWindow extends BasePopupWindow implements View.OnClickListener{


    private View.OnClickListener listener;
    private View view;

    public FileConrtolWindow(Context context,View.OnClickListener listener) {
        super(context);
        this.listener = listener;
    }

    public void setPopupStyle(View view) {
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.file_conrtol_anim_style);
        setContentView(view);
    }

    @Override
    public View inflater(LayoutInflater inflater) {
        return inflater.inflate(R.layout.window_file_conrtol, null);
    }

    @Override
    public void initView(View view) {
       view.findViewById(R.id.share_text).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(view);
        dismiss();
    }

    public void showAsDropDown(View view){
        this.view = view;
        super.showAsDropDown(view);
    }

    public void showAsDropDown(View view, int xoff, int yoff){
        this.view = view;
        super.showAsDropDown(view,xoff,yoff);
    }

    public void dismiss () {
        this.view = null;
        super.dismiss();
    }

}
