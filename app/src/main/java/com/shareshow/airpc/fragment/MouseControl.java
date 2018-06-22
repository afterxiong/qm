package com.shareshow.airpc.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shareshow.aide.R;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.socket.command.CommandControlListener;
import com.shareshow.airpc.util.DebugLog;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TEST on 2017/12/18.
 * 用于鼠标控制
 */

public class MouseControl extends Fragment implements View.OnTouchListener {

    @BindView(R.id.mouse_fy)
    ImageView mouseFy;
//    @BindView(R.id.mouse_left)
//    ImageView mouseLeft;
//    @BindView(R.id.mouse_right)
//    ImageView mouseRight;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float moveX;
    private float moveY;
    private CommandControlListener mListener;
    private long startTime;
    private final static long SEND_TIME =100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.fragment_mousecontrol, container, false);
        ButterKnife.bind(this, vv);
        initView();
        return vv;
    }

    private void initView(){
        mouseFy.setOnTouchListener(this);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof CommandControlListener){
            this.mListener = (CommandControlListener)activity;
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener=null;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

//    @OnClick(R.id.mouse_left)
//    public void leftClick(){
//        Toast.makeText(App.getApp(), "鼠标左键", Toast.LENGTH_SHORT).show();
//
//    }
//
//    @OnClick(R.id.mouse_right)
//    public void rightClick(){
//        Toast.makeText(App.getApp(), "鼠标右键", Toast.LENGTH_SHORT).show();
//        control(QMCommander.SHUBIAO.TYPE_SHUBIAO_RIGHT,1);
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                startTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                if((System.currentTimeMillis()-startTime)>SEND_TIME){
                    scroll(QMCommander.SHUBIAO.TYPE_SHUBIAO_MOVE,(moveX - startX), (moveY - startY));
                    DebugLog.showLog(this, "X移动：" + (moveX - startX) + " Y移动:" + (moveY - startY));
                    startX=moveX;
                    startY=moveY;
                    startTime= System.currentTimeMillis();
                }

                break;

            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                if((System.currentTimeMillis()-startTime)<SEND_TIME&&endX==startX&&endY==startY){
                    control(QMCommander.SHUBIAO.TYPE_SHUBIAO_LEFT,1);
                 }
                // DebugLog.showLog(this, "X最终的移动：" + (endX - startX) + "Y最终的移动:" + (endY - startY));
                break;

        }

        return true;
    }


    public void control(int cmd,int count){
        if(mListener!=null){
            mListener.control(cmd,count);
        }
    }

    public void scroll(int cmd,float x,float y){
        if(mListener!=null){
            mListener.scroll(cmd,x,y);
        }
    }





}
