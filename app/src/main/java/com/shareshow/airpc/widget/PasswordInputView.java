package com.shareshow.airpc.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.EditText;

import com.shareshow.aide.R;
import com.shareshow.airpc.ports.InputEditListener;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Desc:
 * User: tiansj
 */
@SuppressLint("DrawAllocation")
public class PasswordInputView extends EditText {
    
    private int textLength;

    private float borderWidth=5.0f;
    private int borderColor;
    private int borderColor2;
    private float borderRadius;

    private int passwordLength;
    private int passwordColor;
    private float passwordWidth;
    private float passwordRadius;

    private Paint passwordPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint borderPaint = new Paint(ANTI_ALIAS_FLAG);

    private final int defaultContMargin = 5;
    private final int defaultSplitLineWidth = 1;
    private InputEditListener mEditChaged;

    public PasswordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final Resources res = getResources();

        final int defaultBorderColor = res.getColor(R.color.default_ev_border_color);
        final float defaultBorderRadius = res.getDimension(R.dimen.default_ev_border_radius);
        final float defaultborderWidth = res.getDimension(R.dimen.default_ev_border_width);
        final int defaultPasswordLength = res.getInteger(R.integer.default_ev_password_length);
        final int defaultPasswordColor = res.getColor(R.color.default_ev_password_color);
        final float defaultPasswordWidth = res.getDimension(R.dimen.default_ev_password_width);
        final float defaultPasswordRadius = res.getDimension(R.dimen.default_ev_password_radius);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordInputView, 0, 0);
        try {
        	borderWidth = a.getDimension(R.styleable.PasswordInputView_borderWidth, defaultborderWidth);
            borderColor = a.getColor(R.styleable.PasswordInputView_borderColor, defaultBorderColor);
            borderRadius = a.getDimension(R.styleable.PasswordInputView_borderRadius, defaultBorderRadius);
            passwordLength = a.getInt(R.styleable.PasswordInputView_passwordLength, defaultPasswordLength);
            passwordColor = a.getColor(R.styleable.PasswordInputView_passwordColor, defaultPasswordColor);
            passwordWidth = a.getDimension(R.styleable.PasswordInputView_passwordWidth, defaultPasswordWidth);
            passwordRadius = a.getDimension(R.styleable.PasswordInputView_passwordRadius, defaultPasswordRadius);
        } finally {
            a.recycle();
        }
        borderColor2= Color.parseColor("#aaaaaa");
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
        passwordPaint.setStrokeWidth(passwordWidth);
        passwordPaint.setStyle(Paint.Style.FILL);
        passwordPaint.setColor(passwordColor);
    }

    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // 外边框
        RectF rect = new RectF(0, 0, width, height);


        // 内容区
        RectF rectIn = new RectF(rect.left + defaultContMargin, rect.top + defaultContMargin,
                rect.right - defaultContMargin, rect.bottom - defaultContMargin);
        borderPaint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectIn, borderRadius, borderRadius, borderPaint);

        borderPaint.setStrokeWidth(defaultSplitLineWidth);
        borderPaint.setColor(borderColor2);
        canvas.drawLine(defaultContMargin, height-2, width-defaultContMargin, height-2, borderPaint);
        
        // 分割线
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(defaultSplitLineWidth);
        for (int i = 1; i < passwordLength; i++) {
            float x = width * i / passwordLength;
            canvas.drawLine(x,20, x, height-20, borderPaint);
        }

        // 密码
        float cx, cy = height/ 2;
        float half = width / passwordLength / 2;
        for(int i = 0; i < textLength; i++) {
            cx = width * i / passwordLength + half;
            canvas.drawCircle(cx, cy, passwordWidth, passwordPaint);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.textLength = text.toString().length();
        invalidate();
        if(mEditChaged!=null&&this.textLength==4)
    		mEditChaged.textChange(this,text);
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        borderPaint.setColor(borderColor);
        invalidate();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        borderPaint.setStrokeWidth(borderWidth);
        invalidate();
    }

    public float getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(float borderRadius) {
        this.borderRadius = borderRadius;
        invalidate();
    }

    public int getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
        invalidate();
    }

    public int getPasswordColor() {
        return passwordColor;
    }

    public void setPasswordColor(int passwordColor) {
        this.passwordColor = passwordColor;
        passwordPaint.setColor(passwordColor);
        invalidate();
    }

    public float getPasswordWidth() {
        return passwordWidth;
    }

    public void setPasswordWidth(float passwordWidth) {
        this.passwordWidth = passwordWidth;
        passwordPaint.setStrokeWidth(passwordWidth);
        invalidate();
    }

    public float getPasswordRadius() {
        return passwordRadius;
    }

    public void setPasswordRadius(float passwordRadius) {
        this.passwordRadius = passwordRadius;
        invalidate();
    }
    

	public void setEditChaged(InputEditListener mEditChaged){
		this.mEditChaged=mEditChaged;
	}
}