package com.shareshow.airpc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.shareshow.aide.R;


public class LineEditText extends EditText {

	private Paint mpaint;

	public LineEditText(Context context) {
		super(context);
	
	}

	public LineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context, attrs);
	}

	public LineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initData(context, attrs);
	}

	private void initData(Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		mpaint = new Paint();
		mpaint.setStyle(Paint.Style.STROKE);
		mpaint.setColor(getResources().getColor(R.color.xt00ccba));
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//画底线
		canvas.drawLine(0, this.getHeight()-1, this.getWidth()-1, this.getHeight()-1, mpaint);
	}

}
