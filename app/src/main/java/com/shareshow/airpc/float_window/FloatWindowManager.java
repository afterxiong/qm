package com.shareshow.airpc.float_window;


import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.shareshow.airpc.socket.common.QMDevice;


public class FloatWindowManager {
    private Context mCtx;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private MainFloatWindow mainFloatWindow;
    private boolean isShow;
	private WindowCallback listener;
	private MenuFloatWindow menuFloatWindow;
	private boolean isTpSuccess;
	private int paramsX=-1;
	private int paramsY=-1;
	private boolean isOnLeft=true;

    private static FloatWindowManager floatManager=null;

	public static FloatWindowManager getIntance(Context context){

		if(floatManager==null){
			floatManager=new FloatWindowManager(context);
		}

		return floatManager;
	}

    private  FloatWindowManager(Context context){
        mCtx = context;
        this.listener=listener;
        mWindowManager = (WindowManager) mCtx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public void createMainFloatWindow(int x,int y, boolean isLeft, WindowCallback listener, boolean flag){

        params = new WindowManager.LayoutParams();
        setLayoutParamsType(params);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.RGBA_8888;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        if(y==-1){
        	if(paramsY!=-1){
        		 params.y=paramsY;
        	}else{
        		 params.y = WindowUtil.getScreenHeight(mCtx)/2;
        	}

        }else{
        	 params.y=y;
        }
        if(x!=-1){
        	params.x=x;
        } else{

        	if(paramsX!=-1&&!flag){
        		params.x=paramsX;
        	}else{
        		params.x=0;
        	}

        }
        this.listener=listener;

        if(!flag){
        	isLeft=isOnLeft;
        }

    	mainFloatWindow = new MainFloatWindow(mCtx, mWindowManager, params,isLeft);
        mWindowManager.addView(mainFloatWindow,params);

    }

    private void setLayoutParamsType(LayoutParams pm){

		if(android.os.Build.VERSION.SDK_INT >=19){

			pm.type = WindowManager.LayoutParams.TYPE_TOAST;

		}else{

			pm.type = WindowManager.LayoutParams.TYPE_PHONE ;

		}

	}


    public void removeMainWindow(){

    	if(mWindowManager!=null&&mainFloatWindow!=null){
    		mWindowManager.removeView(mainFloatWindow);
    		mainFloatWindow=null;
    	}

    }


    public void removeMenuWindow(){

    	if(mWindowManager!=null&&menuFloatWindow!=null){
    		mWindowManager.removeView(menuFloatWindow);
    		menuFloatWindow=null;
    	}

    }


	public void showFloatWindow(){
        if(isShow){
            return;
        }
        mWindowManager.addView(mainFloatWindow,params);
        isShow = true;
    }


    public boolean isShow() {

		return menuFloatWindow!=null | mainFloatWindow!=null;

	}

    public void createMenuWindow(Context context, int x, int y, boolean isLeft){
    	  WindowManager.LayoutParams  params = new WindowManager.LayoutParams();
    	  setLayoutParamsType(params);
          params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
          //params.flags= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
          params.format = PixelFormat.RGBA_8888;
          params.width = WindowManager.LayoutParams.WRAP_CONTENT;
          params.height = WindowManager.LayoutParams.WRAP_CONTENT;
          params.gravity = Gravity.LEFT | Gravity.TOP;
          params.y=y;
          params.x=x;
          params.windowAnimations=android.R.style.Animation_Translucent;
		  isTpSuccess=getIsTouPing();
          menuFloatWindow = new MenuFloatWindow(context,isLeft,listener,params,isTpSuccess);
          mWindowManager.addView(menuFloatWindow,params);  
      }

	private boolean getIsTouPing() {

		return QMDevice.getInstance().hasScreenDevice();
	}

	public void closeWindow(){		
		if(mWindowManager!=null){  	
    		if(mainFloatWindow!=null){
    			isOnLeft=mainFloatWindow.isLeft();
    			paramsX=mainFloatWindow.getParams().x;
    			paramsY=mainFloatWindow.getParams().y;
    			mWindowManager.removeView(mainFloatWindow);
            	mainFloatWindow=null;    			
    		}else if(menuFloatWindow!=null){
    			isOnLeft=menuFloatWindow.isLeft();
    			paramsX=menuFloatWindow.getParams().x;
    			paramsY=menuFloatWindow.getParams().y;
    			mWindowManager.removeView(menuFloatWindow);
        		menuFloatWindow=null; 			
    		}
    	}
	}

	public void setTpSuccess(boolean isTpSuccess) {
		
		this.isTpSuccess=isTpSuccess;
		if(menuFloatWindow!=null){
			menuFloatWindow.setTpSuccess(isTpSuccess);
		}
		
	}
    
}
