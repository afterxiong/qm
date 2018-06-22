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

package com.shareshow.airpc.ftpserver;

/**
 * Since STOR and APPE are essentially identical except for append vs truncate,
 * the common code is in this class, and inherited by CmdSTOR and CmdAPPE.
 */

import android.util.Log;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMFileType;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;


abstract public class CmdAbstractStore extends FtpCmd {
	public static final String message = "TEMPLATE!!";
	private static final String TAG = "CmdAbstractStore";
	private long totol;
	
	public static String PATH = "";
	
	public CmdAbstractStore(SessionThread sessionThread, String input){
		super(sessionThread, CmdAbstractStore.class.toString());
	}
	
	public void doStorOrAppe(String param, boolean append){
		myLog.l(Log.DEBUG, "STOR/APPE executing with append=" + append);
		Log.i("test","STOR------>param:"+param);
		//System.out.println("==========STOR======================="+param);
		try {
			param= URLDecoder.decode(param, "utf-8");
		}catch (UnsupportedEncodingException e1){
			e1.printStackTrace();
		}
		//File storeFile = inputPathToChrootedFile(sessionThread.getWorkingDir(), param);
		Globals.setChrootDir(new File(QMFileType.LOCALPATH));
		File storeFile =null;
		ArrayList<String> mm=new ArrayList<String>();
		File file = new File(QMFileType.LOCALPATH);
		if(!file.exists())
			file.mkdirs();
		File[] files=file.listFiles();
		if(files!=null){
			mm.clear();
			for (int k = 0; k < files.length; k++){
				if(files[k].isDirectory())
					continue;
				String name = files[k].getName();
				mm.add(name);
			}
			int count=0;
			String local_name=null;

			try{
				local_name=param.substring(0, param.lastIndexOf("."));
			}catch (Exception e){
				e.getMessage();
			}

			for (int j = 0; j <mm.size(); j++){
				String remote_file_name=mm.get(j);
				try{
					remote_file_name=remote_file_name.substring(0, remote_file_name.lastIndexOf("."));
					if(remote_file_name.startsWith(local_name)){
						count++;
					}
				}catch (Exception e){
					e.getMessage();
				}
			}


			String _param=param;
			for (int k = 0; k <count+1; k++){
				param=_param;
				int kk=0;
				String end="";
				try{
					end=param.substring(param.lastIndexOf("."), param.length());
					param=param.substring(0, param.lastIndexOf("."));
				}catch (Exception e){
					e.getMessage();
				}

				if(k==0)
					param=param+end;
				else
					param= param+"("+k+")"+end;
				for (int j = 0; j <mm.size(); j++) {
					if(param.equals(mm.get(j))){
						kk=1;
					}
				}
				if(kk==0){
					break;
				}
			}
		}

		storeFile=new File(QMFileType.LOCALPATH+ File.separator+param);
	    //storeFile = inputPathToChrootedFile(Globals.getChrootDir(), param);
		System.out.println("++++++++++++++++++++++++"+param);
		PATH=param;
		//====================================================================
		
		String errString = null;
		FileOutputStream out = null;

		storing: {
			// Get a normalized absolute path for the desired file
			/*if(violatesChroot(storeFile)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break storing;
			}*/
			if(storeFile.isDirectory()){
				errString = "451 Can't overwrite a directory\r\n";
				break storing;
			}

			try {
				if(storeFile.exists()){
					if(!append){
						if(!storeFile.delete()){
							errString = "451 Couldn't truncate file\r\n";
							break storing;
						}
						Util.deletedFileNotify(storeFile.getPath());
					}
				}
				out = new FileOutputStream(storeFile, append);
			} catch(FileNotFoundException e){
				try {
					errString = "451 Couldn't open file \"" + param + "\" aka \"" + 
						storeFile.getCanonicalPath() + "\" for writing\r\n";
				} catch (IOException io_e) {
					errString = "451 Couldn't open file, nested exception\r\n";
				}
				break storing;
			}

			if(!sessionThread.startUsingDataSocket()){
				errString = "425 Couldn't open data socket\r\n";
				break storing;
			}

			myLog.l(Log.DEBUG, "Data socket ready");
			sessionThread.writeString("150 Data socket ready\r\n");
			byte[] buffer = new byte[Defaults.getDataChunkSize()];
	
			int numRead;

			if(sessionThread.isBinaryMode()){
				myLog.d("Mode is binary");
			}else{
				myLog.d("Mode is ascii");
			}
			while(true){
				switch(numRead = sessionThread.receiveFromDataSocket(buffer)){
				case -1:
					 if(sessionThread.getSize()!=-1&&storeFile!=null&&storeFile.length()!=sessionThread.getSize()) {
						 errString = "228 Couldn't receive data\r\n";
						 Log.i("test", "删除了!");
					 }else{
						myLog.l(Log.DEBUG, "Returned from final read");
						//文件已经读完
//						String ip=sessionThread.getRemoteIp().toString();
//						ip=ip.split("/")[1];
					//	if(sessionThread.isShareFile()){
							EventBus.getDefault().post(new FtpEvent("STOR"+"&&"+storeFile.getName()));
							Log.i("test",storeFile.getName()+"上传完毕！");
					//	}
					}
					break storing; 
				case 0: 
					errString = "426 Couldn't receive data\r\n";
					break storing;
				case -2:
					errString = "425 Could not connect data socket\r\n";
					break storing;
				default:
					try {
						if(sessionThread.isBinaryMode()) {
							out.write(buffer, 0, numRead);
							totol +=numRead;
						}else{
							// ASCII mode, substitute \r\n to \n
							int startPos=0, endPos;
							for(endPos = 0; endPos < numRead; endPos++ ) {
								if(buffer[endPos] == '\r'){
									// Our hacky method is to drop all \r
									out.write(buffer, startPos, endPos-startPos);
									startPos = endPos+1;
								}
							}
							if(startPos < numRead){
								out.write(buffer, startPos, endPos-startPos);
							}
						}
						out.flush();
						
					}catch (IOException e){
						errString = "451 File IO problem. Device might be full.\r\n";
						myLog.d("Exception while storing: " + e);
						myLog.d("Message: " + e.getMessage());
						myLog.d("Stack trace: ");
						StackTraceElement[] traceElems = e.getStackTrace();
						for(StackTraceElement elem : traceElems) {
							myLog.d(elem.toString());
						}
						break storing;
					}
					break;
				}
			}
		}
		try {
			if(out != null) {
				out.close();
			}
		}catch (IOException e){

		}

		 if(errString != null){
		    Log.i("test","ERRO R:"+errString);
			sessionThread.writeString(errString);
			 if(storeFile.exists()){
				storeFile.delete();
				sessionThread.writeString("427 File recieve not complete\r\n");
			 }
			 DebugLog.showLog(this,"DELET 完成发送finish");
		 }else{
		 	sessionThread.writeString("226 Transmission complete\r\n");
			Util.newFileNotify(storeFile.getPath());
		 }
		EventBus.getDefault().post(new FtpEvent("FINISH&&DEVICEIP="+sessionThread.getRemoteIp()));
		sessionThread.closeDataSocket();
		myLog.l(Log.DEBUG, "STOR finished");
	}
}
