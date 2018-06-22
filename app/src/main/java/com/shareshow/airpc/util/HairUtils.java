package com.shareshow.airpc.util;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.shareshow.App;

import java.lang.reflect.Method;

/**
 * Created by TEST on 2017/9/15.
 * 获取屏幕准确的宽高
 */

public class HairUtils {

    public static DisplayMetrics getScreenMetrics(){
        try{
            WindowManager manager= (WindowManager) App.getApp().getSystemService(App.getApp().WINDOW_SERVICE);
            Display display=manager.getDefaultDisplay();
            DisplayMetrics metrics=new DisplayMetrics();
            Class c= Class.forName("android.view.Display");
            Method method=c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display,metrics);
           return metrics;
        }catch (Exception e){
            e.printStackTrace();
            DisplayMetrics metrics = App.getApp().getResources().getDisplayMetrics();
            return metrics;
        }
    }

    public static int getScreenWidth(){
        int width=0;
        DisplayMetrics metrics=getScreenMetrics();
        if(metrics!=null){
            width=metrics.widthPixels;
        }

        return width;
    }

    public static int getScreenHeight(){
        int height=0;
        DisplayMetrics metrics=getScreenMetrics();
        if(metrics!=null){
            height=metrics.heightPixels;
        }

        return height;
    }


    public static boolean is2KScreen() {
        int screenHair= getScreenWidth();
        if(screenHair>=2048){
            //2K屏
            return true;
        }else{
            //不是2K屏
            return false;
        }
    }
}
