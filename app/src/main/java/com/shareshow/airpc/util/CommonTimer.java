package com.shareshow.airpc.util;

import java.util.Timer;
import java.util.TimerTask;

public class CommonTimer extends TimerTask {

	public CommonTimer(){
		timer = new Timer();
	}
	public void Start(long period,ITimerCallback callback) {
		timer.schedule(this, period,period);
		cb = callback;
	}

	public void Stop() {
		timer.cancel();
		cb = null;
	}
	
	@Override
	public void run() {
		 if(cb != null){
			 cb.onTimer();
		 }
	}

	public static long get_tick_count()
	{
		 return System.currentTimeMillis();
	}
	
	private Timer timer = null;
	private ITimerCallback cb;
}
