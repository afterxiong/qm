package com.shareshow.aide.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.socks.library.KLog;

/**
 * Created by xiongchengguang on 2018/1/23.
 */

public class DrawName extends View {

    private String name = "马化腾";
    private Paint paint;

    private int width = 0;
    private int height = 0;

    public DrawName(Context context) {
        this(context, null);
    }

    public DrawName(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#00CCBA"));
        paint.setStyle(Paint.Style.FILL);
    }

    public void setName(String name){
        this.name = name;
        initPaint();
        invalidate();
        KLog.d(name + "  重新绘制");
    }

    public void setInvalidate() {
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = px2dp(48);
        int h = px2dp(48);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        RectF rect = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rect, 20, 20, paint);
        if (name.length() > 0) {
            paint.setColor(Color.WHITE);
            if (name.length() >= 3) {
                name = name.substring(0, 3);
                paint.setTextSize(px2dp(14));
            } else {
                paint.setTextSize(px2dp(16));
            }
            float w = paint.measureText(name) / 2;
            float h = (paint.ascent() + paint.descent()) / 2;
            paint.setStrokeWidth(px2dp(30));
            canvas.drawText(name, width / 2 - w, height / 2 - h, paint);
        }
    }

    public int px2dp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
}
