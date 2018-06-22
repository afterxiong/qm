package com.shareshow.airpc.util;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by Administrator on 2017/6/2 0002.
 */

public class AnimationUtils {


    public void showScaleAnimation(){
        ScaleAnimation scaleAnimation=new ScaleAnimation(1,0.5f,1,0.5f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(2000);
    }

}
