package com.shareshow.aide.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import com.shareshow.aide.util.CacheUserInfo;

/**
 * Created by xiongchengguang on 2017/12/5.
 */

public class WelcomeActivity extends Activity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_welcome);
        handler.postDelayed(waitShow, 1);
    }

    Runnable waitShow = new Runnable(){

        @Override
        public void run(){
            if(CacheUserInfo.get().getUserId().isEmpty()){
                startActivity(LoginActivity.class);
            }else{
                startActivity(MainActivity.class);
            }
        }
    };

    private void startActivity(Class clz){
        Intent intent = new Intent(WelcomeActivity.this, clz);
        startActivity(intent);
       //overridePendingTransition(R.anim.activity_enter_alpha, R.anim.activity_quit_alpha);
        finish();
    }


}
