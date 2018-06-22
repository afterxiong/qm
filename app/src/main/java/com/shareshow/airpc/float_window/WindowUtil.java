package com.shareshow.airpc.float_window;



import android.content.Context;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class WindowUtil {
    private static int screenWidth, screenHeight;
    private static int statusBarHeight;

    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public static int getScreenHeight(Context context){
        if(screenHeight == 0){
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            screenHeight = wm.getDefaultDisplay().getHeight();
            screenWidth = wm.getDefaultDisplay().getWidth();
        }
        return screenHeight;
    }

    public static int getScreenWidth(Context context){
        if(screenWidth == 0){
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            screenHeight = wm.getDefaultDisplay().getHeight();
            screenWidth = wm.getDefaultDisplay().getWidth();
        }
        return screenWidth;
    }

    /**
     *
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
