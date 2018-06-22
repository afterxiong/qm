package com.shareshow.aide.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.shareshow.aide.R;
import com.shareshow.airpc.util.DebugLog;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class DirectionKeyView extends RelativeLayout {

    private Context mContext;
    private Drawable drawable = null;

    private Path topPath;
    private Path leftPath;
    private Path rightPath;
    private Path buttomPath;

    private OnDirectionClickListener mDirectionClickListener = null;

    RectF rectF = new RectF();
    public DirectionKeyView(Context context) {
        super(context);
        init(context);
        DebugLog.showLog(this,"DirectionKeyView");
    }

    public DirectionKeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        DebugLog.showLog(this,"DirectionKeyView2");
    }

    public DirectionKeyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        DebugLog.showLog(this,"DirectionKeyView3");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DirectionKeyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        DebugLog.showLog(this,"DirectionKeyView4");
    }


    private void init(Context context){
        this.mContext = context;
        drawable = ContextCompat.getDrawable(mContext,R.mipmap.control_direction_key_bg_2);
        topPath = new Path();
        leftPath = new Path();
        rightPath = new Path();
        buttomPath = new Path();
    }

    public void setDirectionClickListener (OnDirectionClickListener directionClickListener) {
        this.mDirectionClickListener = directionClickListener;
    }

    int height;
    int width;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        drawable.draw(canvas);

        Paint pain = new Paint();
        pain.setColor(ContextCompat.getColor(mContext,R.color.xtc5e2fd));
        if (isTopTouch) {
            canvas.drawPath(topPath,pain);
        } else if (isLeftTouch) {
            canvas.drawPath(leftPath,pain);
        } else if (isRightTouch) {
            canvas.drawPath(rightPath,pain);
        } else if (isButtomTouch) {
            canvas.drawPath(buttomPath,pain);
        }
    }

    private void pathReset(){
        topPath.reset();
        leftPath.reset();
        rightPath.reset();
        buttomPath.reset();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DebugLog.showLog(this,"onMeasure");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DebugLog.showLog(this,"onAttachedToWindow");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        super.onLayout(changed, l, t, r, b);
        DebugLog.showLog(this,"onLayout");
        pathReset();
        height = b-t;
        width = r-l;
        int widthCenter = width/ 2;
        int heightCenter = height / 2;
//        int angleTopAndButtom = computingTopAndButtomAngle(width,height);
//        DebugLog.showLog(this,"上下 角度为 :"+angleTopAndButtom);
//        int angleLeftAndRight = (360 - angleTopAndButtom * 2) / 2;
//        DebugLog.showLog(this,"左右 角度为 :"+angleLeftAndRight);
//        int angleOffset = angleTopAndButtom - angleLeftAndRight;
//        DebugLog.showLog(this,"偏移量 角度为 :"+angleOffset);
        int startAngle = -135;
//        if (angleOffset > 0 ) {
//            startAngle = startAngle - Math.abs(angleOffset) / 4;
//        } else if ( angleOffset < 0) {
//            startAngle = startAngle + Math.abs(angleOffset) / 4;
//        }
        rectF = new RectF();
//        if (width > height) {
//        	hpianyiliang +=(width - height)/2;
//        } else if (width < height) {
//        	wpianyiliang +=(height - width)/2;
//        }
        rectF.set((int)-(width*0.2),(int)-(height*0.2),(int)(width*1.2),(int)(height*1.2));
        topPath.moveTo(widthCenter,heightCenter);
        leftPath.moveTo(widthCenter,heightCenter);
        buttomPath.moveTo(widthCenter,heightCenter);
        rightPath.moveTo(widthCenter,heightCenter);
        DebugLog.showLog(this,"角度为 :"+startAngle);
        topPath.lineTo(0,0);
        topPath.addArc(rectF,startAngle,90);
//        startAngle+=angleTopAndButtom;
        startAngle+=90;
        DebugLog.showLog(this,"角度为 :"+startAngle);
        rightPath.lineTo(width,0);
        rightPath.addArc(rectF,startAngle,90);
//        startAngle+=angleLeftAndRight;
        startAngle+=90;
        DebugLog.showLog(this,"角度为 :"+startAngle);
        buttomPath.lineTo(width,height);
        buttomPath.addArc(rectF,startAngle,90);
//        startAngle+=angleTopAndButtom;
        startAngle+=90;
        DebugLog.showLog(this,"角度为 :"+startAngle);
        leftPath.lineTo(0,height);
        leftPath.addArc(rectF,startAngle,90);

        topPath.lineTo(widthCenter,heightCenter);
        leftPath.lineTo(widthCenter,heightCenter);
        rightPath.lineTo(widthCenter,heightCenter);
        buttomPath.lineTo(widthCenter,heightCenter);

        Region region2 = new Region();
//        region2.set(-pianyiliang,-pianyiliang,width+pianyiliang,height+pianyiliang);
        region2.set((int)-(width*0.2),(int)-(height*0.2),(int)(width*1.2),(int)(height*1.2));
        topRegion = new Region();
        leftRegion = new Region();
        rightRegion = new Region();
        buttomRegion = new Region();
        topRegion.setPath(topPath,region2);
        leftRegion.setPath(leftPath,region2);
        rightRegion.setPath(rightPath,region2);
        buttomRegion.setPath(buttomPath,region2);
    }

    Region topRegion;
    Region leftRegion;
    Region rightRegion;
    Region buttomRegion;


    private int computingTopAndButtomAngle (int width, int height) {
        double mWidth = width;
        double mHeight = height;
        double hypotenuse = Math.sqrt(Math.pow(mWidth,2.0)+Math.pow(mHeight,2.0));
        DebugLog.showLog(this,"mWidth:"+mWidth+" mHeight:"+mHeight+" hypotenuse:"+hypotenuse);
        int angle = (int)(Math.rint(Math.asin(mHeight / hypotenuse) * 180 / Math.PI));
        DebugLog.showLog(this,"angle:"+angle);
        return 180 - angle * 2;
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        DebugLog.showLog(this,"onKeyDown");
//        return super.onKeyDown(keyCode, event);
//    }

//    int wpianyiliang =50;
//    int hpianyiliang =50;

    boolean isTopTouch = false;
    boolean isLeftTouch = false;
    boolean isRightTouch = false;
    boolean isButtomTouch = false;

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                if (this.mDirectionClickListener != null){
                    if (isTopTouch) {
                        this.mDirectionClickListener.onTopKeyClick();
                    } else if (isLeftTouch) {
                        this.mDirectionClickListener.onLeftKeyClick();
                    } else if (isRightTouch) {
                        this.mDirectionClickListener.onRightKeyClick();
                    } else if (isButtomTouch) {
                        this.mDirectionClickListener.onButtomKeyClick();
                    }
                }
                isTopTouch = false;
                isLeftTouch = false;
                isRightTouch = false;
                isButtomTouch = false;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        isTopTouch = false;
        isLeftTouch = false;
        isRightTouch = false;
        isButtomTouch = false;
        int x = (int)event.getX();
        int y = (int)event.getY();
        if (topRegion.contains(x,y)){
            isTopTouch = true;
            invalidate();
            return true;
        }
        if (leftRegion.contains(x,y)) {
            isLeftTouch = true;
            invalidate();
            return true;
        }
        if (rightRegion.contains(x,y)) {
            isRightTouch = true;
            invalidate();
            return true;
        }
        if (buttomRegion.contains(x,y)) {
            isButtomTouch = true;
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    public interface OnDirectionClickListener {
        public void onTopKeyClick ();
        public void onLeftKeyClick ();
        public void onRightKeyClick ();
        public void onButtomKeyClick ();
    }


}

