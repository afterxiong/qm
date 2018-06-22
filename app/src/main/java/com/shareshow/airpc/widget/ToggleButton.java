package com.shareshow.airpc.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.shareshow.aide.R;


public class ToggleButton extends View {

    private Bitmap slideBG;
    private int downX;
    private Bitmap swithBG;
    private float left;
    private int max;
    private long startTime;
    private int downY;
    private int width;

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        slideBG = BitmapFactory.decodeResource(getResources(), R.mipmap.slide_button_background);
        swithBG = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_background);
        max = swithBG.getWidth() - slideBG.getWidth();

        String nameSpace="http://schemas.android.com/apk/res/cn.itcast.mytogglebuttonwh14";
        isOpen=attrs.getAttributeBooleanValue(nameSpace, "open", false);
        Log.i("test", "默认是否打开:"+isOpen);
    }

    // 自定义控件的步骤:
    // 1,加载
    // 2,测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = swithBG.getWidth();
        int height = swithBG.getHeight();
        // 设置测量后最终决定的宽高
        setMeasuredDimension(width, height);
    }

    // 3,排版
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    // 4,绘制
    private  boolean isFristLoad=true;
    // android中自定义控件有一个绘制的方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(swithBG, 0, 0, null);
        if(isFristLoad){
            if(isOpen){
                left=max;
            }
            isFristLoad=false;
        }
        canvas.drawBitmap(slideBG, left, 0, null);
    }

    private boolean isOpen = false;
    private boolean isChange=false;
    // 表示当前自定义控件处理所有的触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = SystemClock.uptimeMillis();
                downX = (int) event.getX();
                downY = (int) event.getY();
                isChange=isOpen;
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int dX = moveX - downX;
                left += dX;
                if (left < 0) {
                    left = 0;
                }
                if (left > max) {
                    left = max;
                }
                // 重绘
                invalidate();

                downX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if (SystemClock.uptimeMillis() - startTime < 200 && Math.abs(upX - downX) < 5 && Math.abs(upY - downY) < 5) {
                    // 点击操作
                    Log.i("test", "点击");
                    //如果是关闭的,打开才有意义
                    if (!isOpen) {
                        Log.i("test", "进入判断");
                        // 抬起手的x坐标在滑块的右边缘的右边并且在背景的右边缘的左边
                        if (upX > slideBG.getWidth() && upX < swithBG.getWidth()) {
                            isOpen=true;
                            left=max;
                        }else{
                            left=0;
                        }
                        invalidate();
                    }else{
                        if(upX>0&&upX<max){
                            isOpen=false;
                            left=0;
                        }else{
                            left=max;
                        }
                        invalidate();
                    }
                } else {
                    Log.i("test", "滑动");
                    if (left > max / 2) {
                        left = max;
                        isOpen=true;
                    } else {
                        left = 0;
                        isOpen=false;
                    }
                    invalidate();
                }

//			if(!((isOpen&&isChange)||!isChange&&!isOpen)){
//
//			}
                //精髓
                isChange=isChange^isOpen;

                if(isChange&&listener!=null){
                    listener.onToggleButtonChange(isOpen);
                }
                break;

            default:
                break;
        }
        return true;
    }
    private OnToggleButtonChangeListener listener;
    public interface OnToggleButtonChangeListener{
        void onToggleButtonChange(boolean isOpen);
    }
    public void setOnToggleButtonChangeListener(OnToggleButtonChangeListener listener){
        this.listener=listener;
    }

}
