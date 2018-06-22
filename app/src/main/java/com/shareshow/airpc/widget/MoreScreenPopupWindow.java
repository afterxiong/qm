package com.shareshow.airpc.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shareshow.aide.R;


/**
 * 更多设置
 */
public class MoreScreenPopupWindow extends PopupWindow implements View.OnClickListener {


    private Context context;
    private screenMenuListener listener;

    public MoreScreenPopupWindow(Context context, screenMenuListener listener){
        super(context);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater(inflater);
        setPopupStyle(view);
        initView(view);
        this.listener = listener;
    }


    private void setPopupStyle(View view){
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.menu_anim_style);
        setContentView(view);
    }

    private void initView(View view){
        TextView screen_tp = (TextView) view.findViewById(R.id.screen_tp);
        TextView screen_file = (TextView) view.findViewById(R.id.screen_file);
        TextView screen_fx = (TextView) view.findViewById(R.id.screen_fx);
        screen_tp.setOnClickListener(this);
        screen_file.setOnClickListener(this);
        screen_fx.setOnClickListener(this);

    }


    public View inflater(LayoutInflater inflater){
        return inflater.inflate(R.layout.more_screen, null);
    }


    /**
     * @param v
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.screen_tp:
                if(listener!=null){
                    listener.screenCast();
                }
                dismiss();
                break;

            case R.id.screen_fx:

                //fx();
                if(listener!=null){
                    listener.screenShare();
                }
                dismiss();
                break;

            case R.id.screen_file:
               // file();
                if(listener!=null){
                    listener.screenFile();
                }
                dismiss();
                break;
        }
    }



    public interface screenMenuListener{

           void screenShare();

           void screenFile();

           void screenCast();

    }

}
