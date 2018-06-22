/*
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

/* The code that is common to LIST and NLST is implemented in the abstract
 * class CmdAbstractListing, which is inherited here. 
 * CmdLIST and CmdNLST just override the
 * makeLsString() function in different ways to provide the different forms
 * of output.
 */

package com.shareshow.airpc.ftpserver;

import android.util.Log;

import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.LancherFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.common.QMDevice;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CmdLIST extends CmdAbstractListing implements Runnable {
	// The approximate number of milliseconds in 6 months
	public final static long MS_IN_SIX_MONTHS = 6 * 30 * 24 * 60 * 60 * 1000;
	private static final String TAG = "CmdLIST";
	
	private String input;
	private int type=-1;
	private boolean isNew=true;
	
	public CmdLIST(SessionThread sessionThread, String input) {
		super(sessionThread, input);
		this.input = input;
		String str=sessionThread.getRemoteAddress().toString();
		if(str.contains("/"))
			str=str.substring(1, str.length());
		for (int i = 0; i < QMDevice.getInstance().getSize(); i++) {
			RootPoint point= QMDevice.getInstance().getRootPoint(i);
			if(point.getAddress().equals(str)){
				type=point.getdType();
				isNew=point.isNew();
				break;
			}
		}
	}
	
	public void run() {
		String errString = null;
		mainblock: {
			String listing;
				StringBuilder response = new StringBuilder();
				System.out.println("========CmdLIST========="+input);
				if(type==1&&CmdCWD.name!=null){
					errString = listDirectory(CmdCWD.name,response);
					CmdCWD.name=null;
				}
				else if(input.equals("LIST"))
					errString = listDirectory(null,response);
				else{
					System.out.println("----path-------");
					String path=input.split(" ")[1];
					errString = listDirectory(path,response);
				    }
				
				if(errString != null) {
					break mainblock;
				}
				listing = response.toString();
				System.out.println("aa:"+listing);
			errString = sendListing(listing);
			if(errString != null) {
				break mainblock;
			}
		}	
		
		if(errString != null) {
			sessionThread.writeString(errString);
			myLog.l(Log.DEBUG, "LIST failed with: " + errString);
		} else {
			myLog.l(Log.DEBUG, "LIST completed OK");
		}
	}
	
	protected String makeLsString(LancherFile file, int type){
		if(isNew){
			return getmakeLsString2(file);
		}else{
			return getmakeLsString1(file);
		}
	
		/*switch (MainActivity.number) {
		case 1:
			return getmakeLsString1(file);
		case 2:
			return getmakeLsString2(file);
		}
		return null;*/
	}
	
	private String getmakeLsString1(LancherFile file){
		/*StringBuilder response = new StringBuilder();
		
		if(!new File(file.getPath()).exists()) {
			return null;
		}
		
		String lastNamePart = file.getName();
		// Many clients can't handle files containing these symbols
		if(lastNamePart.contains("*") || 
		   lastNamePart.contains("/"))
		{
			staticLog.l(Log.INFO, "Filename omitted due to disallowed character");
			return null;
		} else {
			// The following line generates many calls in large directories
			//staticLog.l(Log.DEBUG, "Filename: " + lastNamePart);
		}
		String str="";
		if(file.getPermit()==0){
			str= "allowDownload";
		}else{
			str = "forbidDownload-xtxk";
		}		
		String link=file.getPath();
		String dir="";
		if(file.getFileDir()==1)
			dir="-";
		else
			dir="d";
		response.append(dir).append("rw-r--r-- ").append(file.getSize()).append(" ").append(str).append(" ").append(link).append("");
		
		
		// The next field is a 13-byte right-justified space-padded file size
		long fileSize =file.getSize();
		String sizeString = new Long(fileSize).toString();
		int padSpaces = 13 - sizeString.length();
		while(padSpaces-- > 0) {
			response.append(' ');
		}
		response.append(sizeString);
		SimpleDateFormat format;
		long lastModified = file.getUpdate();
		format = new SimpleDateFormat(" MMM dd HH:mm ", Locale.US);
		response.append(format.format(new Date(file.getUpdate())));
		response.append(lastNamePart);
		response.append("\r\n");
		return response.toString();*/
        StringBuilder response = new StringBuilder();
		
		if(!new File(file.getPath()).exists()) {
			return null;
		}
		if(file.getFileDir()==0){
			return null;
		}
		
		String lastNamePart = file.getName();
		// Many clients can't handle files containing these symbols
		if(lastNamePart.contains("*") || 
		   lastNamePart.contains("/"))
		{
			staticLog.l(Log.INFO, "Filename omitted due to disallowed character");
			return null;
		} else {
			// The following line generates many calls in large directories
			//staticLog.l(Log.DEBUG, "Filename: " + lastNamePart);
		}
		String str="";
		if(file.getPermit()==0){
			str= "allowDownload";
		}else{
			str = "forbidDownload-xtxk";
		}		
		String link=file.getPath();
		String dir="";
		if(file.getFileDir()==1)
			dir="-";
		else
			dir="d";
		response.append(dir).append("rw-r--r-- ").append(file.getFileDir()).append(" ").append(str).append(" ").append(link).append("");
		
		// The next field is a 13-byte right-justified space-padded file size
		long fileSize =file.getSize();
		String sizeString = new Long(fileSize).toString();
		int padSpaces = 13 - sizeString.length();
		while(padSpaces-- > 0){
			response.append(' ');
		}
		response.append(sizeString);
		SimpleDateFormat format;
		long lastModified = file.getUpdate();
		format = new SimpleDateFormat(" MMM dd HH:mm ", Locale.US);
		response.append(format.format(new Date(file.getUpdate())));
		response.append(lastNamePart);
		response.append("\r\n");
		return response.toString();
	}

	private String getmakeLsString2(LancherFile file){
		StringBuilder response = new StringBuilder();
		
		if(!new File(file.getPath()).exists()) {
			return null;
		}
		
		String link=file.getPath();
		String str="";
		if(file.getPermit()==0){
			str=  "allowDownload";
		}else{
			str = "forbidDownload-xtxk";
		}
		String dir="";
		if(file.getFileDir()==1)
			dir="-";
		else
			dir="d";
		response.append(dir).append("rw-r--r--").append(QMCommander.icon).
		append(file.getFileDir()).append(QMCommander.icon).
		append(str).append(QMCommander.icon).
		append(link).append(QMCommander.icon).
		append(file.getSize()).append(QMCommander.icon).
		append(new SimpleDateFormat("yyyy MMM dd HH:mm", Locale.US).format(new Date(file.getUpdate()))).append(QMCommander.icon).
		append(file.getName()).append("\r\n");
		return response.toString();
	}
}
