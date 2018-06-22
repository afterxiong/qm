package com.shareshow.aide.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shareshow.aide.R;

/**
 * Created by Administrator on 2018/4/9 0009.
 */

public class FileItemDecoration extends RecyclerView.ItemDecoration {

    int color;

    public FileItemDecoration(Context context) {
        super();
       color = context.getResources().getColor(R.color.file_line);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        Paint paint = new Paint();
        paint.setColor(color);
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            c.drawLine(child.getX(),child.getY() + child.getHeight(),child.getX() + child.getWidth(),child.getY() + child.getHeight(),paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
}
