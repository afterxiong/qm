package com.shareshow.airpc.share;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;


/**
 * Created by Administrator on 2017/7/5 0005.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int i) {
        this.space=i;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        super.getItemOffsets(outRect, itemPosition, parent);

        outRect.right=space*2;
        outRect.left=space*2;
        outRect.bottom=space;
        if(itemPosition==0){
            outRect.left=space*5;
        }
    }
}