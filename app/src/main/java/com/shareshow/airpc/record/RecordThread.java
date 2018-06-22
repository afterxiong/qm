package com.shareshow.airpc.record;

import java.io.IOException;

public class RecordThread extends Thread {

	private String filename;
	
	public void setFileName(String name)
	
	
	{
		filename = name;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Process process = Runtime.getRuntime().exec("screenrecord " + filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.run();
	}
	
}
