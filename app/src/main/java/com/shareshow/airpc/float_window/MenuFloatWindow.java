package com.shareshow.airpc.float_window;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shareshow.aide.R;


/**
 * Created by Administrator on 2017/6/6 0006.
 */

public class MenuFloatWindow extends LinearLayout implements View.OnClickListener {

    private WindowCallback listener;
    private Context mCtx;
    private WindowManager.LayoutParams params;
    private FloatWindowManager floatWindowManager;
    private ImageView screen;
    private TextView text;
    private boolean isLeft;
    private ProgressBar progress;
    private LinearLayout screen_left;
    private LinearLayout window_screen;

    public MenuFloatWindow(Context context, boolean isLeft, WindowCallback listener, WindowManager.LayoutParams params, boolean isTpSuccess){
        super(context);
        View inflate =null;
        this.listener=listener;
        this.mCtx=context;
        this.params=params;
        this.isLeft=isLeft;
        floatWindowManager = FloatWindowManager.getIntance(mCtx);
        if(isLeft){
            inflate = View.inflate(context, R.layout.float_window_left_window, this);
            ImageView img = (ImageView) inflate.findViewById(R.id.float_window_small);
            LinearLayout main_left = (LinearLayout) inflate.findViewById(R.id.window_main_left);
            LinearLayout hide_left = (LinearLayout) inflate.findViewById(R.id.window_hide_left);
            window_screen = (LinearLayout) inflate.findViewById(R.id.window_screen_left);
            progress = (ProgressBar) inflate.findViewById(R.id.progress);
            screen = (ImageView)inflate.findViewById(R.id.cast_screen);
            text = (TextView)inflate.findViewById(R.id.cast_screen_text);
            if(isTpSuccess){
                screen.setImageResource(R.drawable.cast_screen_stop_bg);
                text.setText(getResources().getString(R.string.stop_c));
            }else{
                screen.setImageResource(R.drawable.cast_screen_bg);
                text.setText(getResources().getString(R.string.tp_c));
            }
            main_left.setOnClickListener(this);
            hide_left.setOnClickListener(this);
            window_screen.setOnClickListener(this);
            img.setOnClickListener(this);

        }else{
            inflate = View.inflate(context, R.layout.float_window_right_window, this);
            ImageView img = (ImageView) inflate.findViewById(R.id.float_window_small_right);
            LinearLayout main_right = (LinearLayout) inflate.findViewById(R.id.window_main_right);
            LinearLayout hide_right = (LinearLayout) inflate.findViewById(R.id.window_hide_right);
            window_screen= (LinearLayout) inflate.findViewById(R.id.window_screen_right);
            progress = (ProgressBar) inflate.findViewById(R.id.progress);
            screen = (ImageView)inflate.findViewById(R.id.cast_screen);
            text = (TextView)inflate.findViewById(R.id.cast_screen_text);
            if(isTpSuccess){
                screen.setImageResource(R.drawable.cast_screen_stop_bg);
                text.setText(getResources().getString(R.string.stop_c));
            }else{
                screen.setImageResource(R.drawable.cast_screen_bg);
                text.setText(getResources().getString(R.string.tp_c));
            }

            main_right.setOnClickListener(this);
            window_screen.setOnClickListener(this);
            hide_right.setOnClickListener(this);
            img.setOnClickListener(this);


        }

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.float_window_small:
                floatWindowManager.createMainFloatWindow(params.x, params.y,true,listener,true);
                floatWindowManager.removeMenuWindow();
                break;

            case R.id.float_window_small_right:
                floatWindowManager.createMainFloatWindow(params.x, params.y,false,listener,true);
                floatWindowManager.removeMenuWindow();

                break;

            case R.id.window_hide_left:

                //    Toast.makeText(mCtx, "隐藏", Toast.LENGTH_SHORT).show();

                if(listener!=null){
                    listener.onHideWindow();
                }

                break;
            case R.id.window_hide_right:


                //	Toast.makeText(mCtx, "隐藏", Toast.LENGTH_SHORT).show();
                if(listener!=null){
                    listener.onHideWindow();
                }

                break;
            case R.id.window_screen_left:

                if(listener!=null&&text!=null){
                    if(text.getText().toString().trim().equals(getResources().getString(R.string.tp_c))){
                        listener.onStartCastScreen();
                        window_screen.setVisibility(GONE);
                        progress.setVisibility(VISIBLE);
                    }else{
                        listener.onStopCastScreen();
                    }
                }
                break;

            case R.id.window_screen_right:

                if(listener!=null&&text!=null){
                    if(text.getText().toString().trim().equals(getResources().getString(R.string.tp_c))){
                        listener.onStartCastScreen();
                        window_screen.setVisibility(GONE);
                        progress.setVisibility(VISIBLE);
                    }else{
                        listener.onStopCastScreen();
                    }

                }
                break;


            case R.id.window_main_left:

                if(listener!=null){
                    listener.ReturnMainMenu();
                }

                break;
            case R.id.window_main_right:

                if(listener!=null){
                    listener.ReturnMainMenu();
                }

                break;

            default:
                break;
        }

    }

    public void  setTpSuccess(boolean isTpSuccess) {
        if(screen!=null&&text!=null){
            if(isTpSuccess){
                window_screen.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                screen.setImageResource(R.drawable.cast_screen_stop_bg);
                text.setText(getResources().getString(R.string.stop_c));
            }else{
                window_screen.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                screen.setImageResource(R.drawable.cast_screen_bg);
                text.setText(getResources().getString(R.string.tp_c));
            }
        }
    }

    public WindowManager.LayoutParams getParams(){
        return params;
    }

    public boolean isLeft(){

        return isLeft;
    }
}
