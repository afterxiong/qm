package com.xcg.zxing.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.shareshow.aide.R;


/**
 * Created by xiongchengguang on 2017/3/31.
 */

public class LoadingView extends View {
    private Paint paint;
    private int position = 0;

    private int circleRadius;
    private int lineWidth;
    private int lineHeight;
    private String loadingText;
    private int loadingTextSize;
    private int loadingTextColor;
    private int lineNumber = 12;
    private int loadTextTop;

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0);
        circleRadius = (int) typedArray.getDimension(R.styleable.LoadingView_circle_radius, px2dp(12));
        lineWidth = (int) typedArray.getDimension(R.styleable.LoadingView_line_width, px2dp(2));
        lineHeight = (int) typedArray.getDimension(R.styleable.LoadingView_line_height, px2dp(6));
        loadingText = typedArray.getString(R.styleable.LoadingView_loadText);
        loadingTextColor = typedArray.getColor(R.styleable.LoadingView_loadTextColor, Color.BLACK);
        lineNumber = typedArray.getInt(R.styleable.LoadingView_line_number, 12);
        loadingTextSize = (int) typedArray.getDimension(R.styleable.LoadingView_loadTextSize, px2sp(16));
        loadTextTop = (int) typedArray.getDimension(R.styleable.LoadingView_loadTextTop, px2dp(30));
        typedArray.recycle();
        setPaint();
    }

    public void setPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    private String[] color = {"#FF000000", "#FF101010", "#FF0D0D0D", "#FF252525", "#FF484848", "#FF616161", "#FF828283", "#FF9B9B9B", "#FFBBBCBC", "#FFD7D7D7", "#FFECECEC", "#FFECECEC"};

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setPaint();
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < lineNumber; i++) {
            int index = (i + position) % lineNumber;
            paint.setColor(Color.parseColor(color[index]));
            canvas.drawLine(getWidth() / 2, getHeight() / 2 - circleRadius, getWidth() / 2, getHeight() / 2 - (circleRadius + lineHeight), paint);
            canvas.rotate(360 / lineNumber, getWidth() / 2, getHeight() / 2);
        }
        position++;

        if (position > lineNumber) {
            position = 0;
        }

        if (loadingText != null) {
            paint.setTextSize(loadingTextSize);
            float textWidth = paint.measureText(loadingText) / 2;
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(loadingTextColor);
            canvas.drawText(loadingText, getWidth() / 2 - textWidth, getHeight() / 2 + lineHeight + circleRadius + loadTextTop, paint);
        }

        postInvalidateDelayed(100);

    }

    public int px2dp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }

    public int px2sp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, val, getResources().getDisplayMetrics());
    }
}

