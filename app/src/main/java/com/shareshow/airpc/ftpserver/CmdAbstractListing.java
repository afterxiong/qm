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

/*
 * Since the FTP verbs LIST and NLST do very similar things related to listing
 * directory contents, the common tasks that they share have been factored
 * out into this abstract class. Both CmdLIST and CmdNLST inherit from this
 * class. 
 */

package com.shareshow.airpc.ftpserver;

import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import com.shareshow.App;
import com.shareshow.airpc.model.LancherFile;
import com.shareshow.airpc.util.QMDbUtil;
import com.shareshow.airpc.util.QMUtil;

import java.io.File;
import java.util.ArrayList;

public abstract class CmdAbstractListing extends FtpCmd {
	//protected static MyLog staticLog = new MyLog(CmdLIST.class.toString());
	
	public CmdAbstractListing(SessionThread sessionThread, String input) {
		super(sessionThread, CmdAbstractListing.class.toString());
	}
	
	abstract String makeLsString(LancherFile file, int type);

	public String listDirectory(String sPath, StringBuilder response) {
		ArrayList<LancherFile> al = null;
		if(sPath==null){
			al=QMUtil.getInstance().getQmDocumentFile().getShareFile();
		}else{
			al=getAlpheData(sPath);
		}
		
		if(al==null||al.size()==0){
			LancherFile mLancherFile=new LancherFile();
			mLancherFile.setName("bb.jpg");
			mLancherFile.setPath(Environment.getExternalStorageDirectory()+ File.separator+"aa/bb.jpg");
			mLancherFile.setSize(457863);
			mLancherFile.setPermit(0);
			mLancherFile.setUpdate(850261584);
			String curLine = makeLsString(mLancherFile,0);
			if(curLine != null) {
				response.append(curLine);
			}
		}else{
			for (int i = 0; i <al.size(); i++) {
				String curLine = makeLsString(al.get(i),1);
				if(curLine != null) {
					response.append(curLine);
				}
			}
		}
		
		return null;
	}

	private ArrayList<LancherFile> getAlpheData(String name){
		int permit= QMDbUtil.getIntance(App.getApp()).getFir(name);
		ArrayList<LancherFile> shareFile=new ArrayList<LancherFile>();
		Cursor cursor = App.getApp().getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
	            ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.DISPLAY_NAME, ImageColumns.SIZE }, "bucket_display_name = ?",
	            new String[] { name }, ImageColumns.DATE_ADDED);
		 String old_displayName="";
		if(null != cursor&&cursor.getCount() > 0){
			while(cursor.moveToNext()){
				long size=cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE));
	            
	        	String displayName = cursor.getString(cursor.getColumnIndex(ImageColumns.DISPLAY_NAME));
	        	String path =cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
	        	if(old_displayName.equals(path))
	        		continue;
				String update = cursor.getString(cursor.getColumnIndex(ImageColumns.DATE_ADDED));
				LancherFile mLancherFile=new LancherFile();
				mLancherFile.setName(displayName);
				mLancherFile.setPath(path);
				mLancherFile.setSize(size);
				mLancherFile.setPermit(permit);
				mLancherFile.setFileDir(1);
				mLancherFile.setUpdate(Long.parseLong(update)*1000);
				old_displayName=path;	
				shareFile.add(mLancherFile);
			}
		}
	    cursor.close();
	    QMUtil.getInstance().getQmDocumentFile().sortShareFile(shareFile);
	    return shareFile;
	}

	protected String sendListing(String listing) {
		if(sessionThread.startUsingDataSocket()) {
			myLog.l(Log.DEBUG, "LIST/NLST done making socket");
		} else {
			sessionThread.closeDataSocket();
			return "425 Error opening data socket\r\n";
		}
		String mode = sessionThread.isBinaryMode() ? "BINARY" : "ASCII";
		sessionThread.writeString(
				"150 Opening "+mode+" mode data connection for file list\r\n");
		myLog.l(Log.DEBUG, "Sent code 150, sending listing string now");
		if(!sessionThread.sendViaDataSocket(listing)) {
			myLog.l(Log.DEBUG, "sendViaDataSocket failure");
			sessionThread.closeDataSocket();
			return "426 Data socket or network error\r\n";
		}
		sessionThread.closeDataSocket();
		myLog.l(Log.DEBUG, "Listing sendViaDataSocket success");
		sessionThread.writeString("226 Data transmission OK\r\n");
		return null;
	}
}
