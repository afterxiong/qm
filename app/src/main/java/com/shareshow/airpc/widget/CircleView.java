package com.shareshow.airpc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shareshow.aide.R;


public class CircleView extends View {
	public CircleView(Context context) {
		super(context);
	}
	public CircleView(Context context, AttributeSet attr) {
		super(context, attr);
	}
	
	private int startX,startY;
	private float varX;
	public int r=20;
	public void setRadius(int radius) {
	        r = radius;
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		startX=(int)(w/2-3*r);
		varX=startX;
		startY=h;
		Log.i("TAG", "w="+w);
		Log.i("TAG", "h="+h);
		Log.i("TAG", "oldw"+oldw);
		Log.i("TAG", "oldh="+oldh);
	}
	private Paint paintA=new Paint(Paint.ANTI_ALIAS_FLAG);
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paintA.setColor(R.color.xt006960);
		paintA.setStyle(Style.STROKE);
		for(int i=0;i<2;i++){
		 canvas.drawCircle(startX+3*i*20,startY/2, r, paintA);
		}
		paintA.setColor(R.color.xt60006960);
		paintA.setStyle(Style.FILL);
		canvas.drawCircle(varX, startY/2, r-5,paintA);
	}
	public void repaint(int position){
		varX=startX+position*3*r;
		invalidate();
	}
	
	public void repaint(int position,float offset){
		varX=startX+(position+offset)*3*r;
		invalidate();
	}

}
