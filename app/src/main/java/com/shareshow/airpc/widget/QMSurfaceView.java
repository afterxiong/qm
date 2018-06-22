package com.shareshow.airpc.widget;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.shareshow.airpc.ports.MoveListener;
import com.shareshow.airpc.util.DebugLog;


public class QMSurfaceView extends SurfaceView implements SurfaceView.OnTouchListener {

	private Context mContext;

	public QMSurfaceView(Context context) {
		super(context);
		this.mContext =context;
		setOnTouchListener(this);
	}
	
	public QMSurfaceView(Context context, int maxWidth, int maxHeight) {
		this(context);

		mScreenWidth =maxWidth;
		mScreenHeight =maxHeight;
	}

	private int mScreenWidth;
	private int mScreenHeight;
	private float mCurrentRate = 1;//??????????
	private float mOldRate = 1;//????η??????
	private boolean mIsFirst = true;//????????δ??????
	private float mOriginalLength;//??????????????????
	private float mCurrentLength;//???????????????
    private int left;
	private int right;
	private int top;
	private int bottom;

	private void scal(){
	
		   //选取的缩放中心的问题
		   android.widget.LinearLayout.LayoutParams sufaceviewParams = (android.widget.LinearLayout.LayoutParams)
					getLayoutParams();
		   if(mCurrentRate>3){
			   mCurrentRate=3;
		   }
		   sufaceviewParams.height=(int) (mScreenHeight*mCurrentRate);
		   sufaceviewParams.width = (int) (mScreenWidth*mCurrentRate);
		   //在这里进行设置控件的位置
		   setLayoutParams(sufaceviewParams);
		 
		   
	}
	
	MoveListener mMoveListener;
	private float rawX;
	private float rawY;

	public void setmMoveListener(MoveListener mMoveListener) {
		this.mMoveListener = mMoveListener;
	}

	private int count;
	private long firClick;
	private long seClick;
	

	@Override
	public boolean onTouch(View v, MotionEvent event){
		/**
		 * 设置双击还原
		 * */
		if(!getSettingPermission()){
			return true;
		 }

		if(MotionEvent.ACTION_DOWN==event.getAction()){
			count++;
			if(mCurrentRate==1)
				mMoveListener.showHead(0);
			else
				mMoveListener.showHead(1);
			if(count==1){
				firClick = System.currentTimeMillis();
			}else if(count==2){
				seClick = System.currentTimeMillis();
				if(seClick-firClick<300){
					//双击事件
					DebugLog.showLog(this,"mCurrenRate"+mCurrentRate);

					if(mCurrentRate!=1){
						mCurrentRate=1;
						v.layout(0, 0, mScreenWidth, mScreenHeight);
					}else{
						mCurrentRate=2;
					}
					
				}
				count=0;
				firClick=0;
				seClick=0;
			  }
		   }

			switch (event.getAction()){
			case MotionEvent.ACTION_MOVE:
				count=0;
				//获得缩放的比例
				if (event.getPointerCount() == 2) {
					if (mIsFirst) {
						mOriginalLength = (float) Math.sqrt(Math.pow(event.getX(0)
								- event.getX(1), 2)
								+ Math.pow(event.getY(0) - event.getY(1), 2));
						mIsFirst = false;
					} else {
						mCurrentLength = (float) Math.sqrt(Math.pow(event.getX(0)
								- event.getX(1), 2)
								+ Math.pow(event.getY(0) - event.getY(1), 2));
						mCurrentRate = (float) (mOldRate * (mCurrentLength / mOriginalLength));
						if(mCurrentRate<=1){
							mCurrentRate=1;
						}
					}
				}
				if(mCurrentRate>1){
					if(event.getPointerCount()==1){
						int dx=(int) (event.getRawX()-rawX);
						int dy=(int) (event.getRawY()-rawY);
						left = v.getLeft()+dx;
						right = v.getRight()+dx;
						top = v.getTop()+dy;
						bottom = v.getBottom()+dy;
						/**在这里可以添加控制条件防止控件移动越界，产生bug*/
						if(left<=0&&top<=0&&right>=mScreenWidth&&bottom>=mScreenHeight){
							v.layout(left, top, right, bottom);
						}
						rawX=(int)event.getRawX();
					    rawY=(int)event.getRawY();
					}
				}
				else{
					if(mMoveListener!=null)
						mMoveListener.move(event);
				}

				break;
			case MotionEvent.ACTION_UP:
				if(mCurrentRate>=3){
					mCurrentRate=3;
				}
				if(mCurrentRate<=1){
					mCurrentRate=1;
				}

				if(mCurrentRate!=mOldRate){
					scal();
				}
				mOldRate = mCurrentRate;
				mIsFirst = true;
				if(mMoveListener!=null)
					mMoveListener.up(event);
				invalidate();
				break;
			case MotionEvent.ACTION_DOWN:
				rawX = event.getRawX();
				rawY = event.getRawY();
				if(mMoveListener!=null)
					mMoveListener.down(event);
				break;
			}
			return true;
	}

	//获取系统设置亮度的权限
	private boolean getSettingPermission(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			if (!Settings.System.canWrite(mContext)){
				Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
				intent.setData(Uri.parse("package:" + mContext.getPackageName()));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				return false;
			}else{
				return true;
			}
		}
		return true;

	}


}