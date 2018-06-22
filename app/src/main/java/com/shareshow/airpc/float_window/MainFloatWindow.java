package com.shareshow.airpc.float_window;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shareshow.aide.R;


/**
 * Created by Administrator on 2017/6/6 0006.
 */

public class MainFloatWindow extends LinearLayout {
    private final static int MSG_UPDATE_POS = 0x01;
    private final static int MSG_WINDOW_HIDE = 0x02;

    private final static int SPEED = 100;

    private final static int WAIT_TIME = 3000;

    private Context mCtx;
    private ImageView ivDefaultWindow;

    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    private float xInView, yInView;

    private float xDown, yDown;

    private boolean isOnLeft;

    private boolean canHide;


    public MainFloatWindow(Context context, WindowManager windowManager, WindowManager.LayoutParams params, boolean isLeft) {
        super(context);
        mWindowManager = windowManager;
        mParams = params;
        mCtx = context;
        initWindowView();
        this.addView(ivDefaultWindow);
        isOnLeft = isLeft;
        canHide = true;
        floatWindowManager = FloatWindowManager.getIntance(mCtx);
    }



    private void initWindowView(){
        ivDefaultWindow = new ImageView(mCtx);
        this.ivDefaultWindow.setImageResource(R.mipmap.float_window_small);

    }


    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                canHide = false;
                xInView = event.getX();
                yInView = event.getY();
                xDown = event.getRawX();
                yDown = event.getRawY() - WindowUtil.getStatusBarHeight(mCtx);
                break;

            case MotionEvent.ACTION_MOVE:

                Message message = new Message();
                message.what = MSG_UPDATE_POS;
                message.arg1 = (int) (event.getRawX() - xInView);
                message.arg2 = (int) (event.getRawY() - WindowUtil.getStatusBarHeight(mCtx)-yInView);
                updateWindowPos( message.arg1, message.arg2);

                break;

            case MotionEvent.ACTION_UP:
                canHide = true;
                if(xDown == event.getRawX() && yDown == event.getRawY() - WindowUtil.getStatusBarHeight(mCtx)){
                    onClick();
                }else{
                    autoMoveToSide();
                }
                break;
        }

        return true;
    }

    /**
     *
     */
    private void autoMoveToSide() {

        new Thread() {

            public void run() {
                int[] location = new int[2];
                ivDefaultWindow.getLocationOnScreen(location);
                isOnLeft = location[0]+getWidth()/2 < WindowUtil.getScreenWidth(mCtx)/2;
                //判断是否为左边
                int moveParam = isOnLeft ? -10 : 10;
                while (true) {
                    location[0] = location[0] + moveParam;
                    int newX = location[0];
                    int newY = location[1]-ivDefaultWindow.getWidth()/2;
                    if (isOnLeft && newX<=0){
                        newX = 0;
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        mHandler.sendMessage(message);
                        break;
                    }else if(!isOnLeft && newX>= WindowUtil.getScreenWidth(mCtx)){
                        newX = WindowUtil.getScreenWidth(mCtx);
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        mHandler.sendMessage(message);
                        break;
                    } else {
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        mHandler.sendMessage(message);
                    }

                }
            }
        }.start();
    }

    /**
     *
     * @param x
     * @param y
     */
    private void updateWindowPos(final float x, final float y){
        mParams.x = (int) x;
        mParams.y = (int) y;
        mWindowManager.updateViewLayout(MainFloatWindow.this, mParams);
    }

    private void onClick(){
        if (isOnLeft){

            Log.i("test", "small  x:"+mParams.x+"y:"+mParams.y);

            floatWindowManager.createMenuWindow(mCtx, mParams.x, mParams.y,isOnLeft);
            floatWindowManager.removeMainWindow();
        } else {

            floatWindowManager.createMenuWindow(mCtx, mParams.x, mParams.y,isOnLeft);
            floatWindowManager.removeMainWindow();
        }
        canHide = false;
    }

    private boolean isContainMenuView(View inflate) {

        int count = getChildCount();
        for (int i = 0; i <count; i++) {
            if(getChildAt(i).equals(inflate)){
                return true;
            }
        }

        return false;

    }



    /**
     *
     */
    private void waitToHideWindow(){
        if(!canHide){
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(canHide) {
                    mHandler.sendEmptyMessage(MSG_WINDOW_HIDE);
                }
            }
        }.start();
    }

    private MyHandler mHandler = new MyHandler();
    private FloatWindowManager floatWindowManager;
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_POS:
                    updateWindowPos(msg.arg1, msg.arg2);
                    break;
                case MSG_WINDOW_HIDE:
                    break;
            }
        }
    }

    public WindowManager.LayoutParams getParams(){
        return mParams;
    }

    public boolean isLeft(){

        return isOnLeft;
    }

}
