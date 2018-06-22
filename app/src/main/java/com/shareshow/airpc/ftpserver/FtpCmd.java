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


import android.util.Log;


import com.shareshow.airpc.util.DebugLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Constructor;

public abstract class FtpCmd implements Runnable {
	protected SessionThread sessionThread;
	protected MyLog myLog;
	protected static MyLog staticLog = new MyLog(FtpCmd.class.toString());
	
	protected static CmdMap[] cmdClasses = {
			new CmdMap("SYST", CmdSYST.class),
			new CmdMap("USER", CmdUSER.class),
			new CmdMap("PASS", CmdPASS.class),
			new CmdMap("TYPE", CmdTYPE.class),
			new CmdMap("CWD",  CmdCWD.class),
			new CmdMap("PWD",  CmdPWD.class),
			new CmdMap("LIST", CmdLIST.class),
			new CmdMap("PASV", CmdPASV.class),
			new CmdMap("RETR", CmdRETR.class),
			//new CmdMap("NLST", CmdNLST.class),
			new CmdMap("STAT", CmdSTAT.class),
			new CmdMap("NOOP", CmdNOOP.class),
			new CmdMap("STOR", CmdSTOR.class),
			new CmdMap("DELE", CmdDELE.class),
			new CmdMap("RNFR", CmdRNFR.class),
			new CmdMap("RNTO", CmdRNTO.class),
			new CmdMap("RMD",  CmdRMD.class),
			new CmdMap("MKD",  CmdMKD.class),
			new CmdMap("OPTS", CmdOPTS.class),
			new CmdMap("PORT", CmdPORT.class),
			new CmdMap("QUIT", CmdQUIT.class),
			new CmdMap("FEAT", CmdFEAT.class),
			new CmdMap("SIZE", CmdSIZE.class),
			new CmdMap("CDUP", CmdCDUP.class),
			new CmdMap("APPE", CmdAPPE.class),
			new CmdMap("XCUP", CmdCDUP.class), // synonym
			new CmdMap("XPWD", CmdPWD.class),  // synonym
			new CmdMap("XMKD", CmdMKD.class),  // synonym
			new CmdMap("XRMD", CmdRMD.class),   // synonym
	};

	public FtpCmd(SessionThread sessionThread, String logName){
		this.sessionThread = sessionThread;
		myLog = new MyLog(logName);
	}
	
	abstract public void run();
	
	protected static void dispatchCommand(SessionThread session, 
	                                      String inputString){
		String[] strings = inputString.split(" ");
		String unrecognizedCmdMsg = "502 Command not recognized\r\n";

		if(strings == null){
			String errString = "502 Command parse error\r\n";
			staticLog.l(Log.INFO, errString);
			session.writeString(errString);
			return;
		}

		if(strings.length < 1){
			staticLog.l(Log.INFO, "No strings parsed");
			session.writeString(unrecognizedCmdMsg);
			return;
		}

		String verb = strings[0];
		 if(verb.contains("ABOR")){
			 DebugLog.showLog("FtpCmd","中断了。。。");
		 }

		 if (inputString.contains("VERSION")){//文件上传大小

			if (strings !=null && strings[1] !=null){
				int ftpClientVersion = Integer.parseInt(strings[1]);
				session.setFtpClientVision(ftpClientVersion);
			  }
		   }

//		 if(inputString.contains("STAT")){
//			 session.setShareFile(true);
//		 }

		  if (inputString.contains("UPLOADSIZE")){//文件上传大小

			if (strings !=null && strings[1] !=null){
				long uploadFileSize = Long.parseLong(strings[1]);
				session.setSize(uploadFileSize);
				session.writeString("217 uploadsize ok!\r\n");
		        return;
			 }
		  }


		  if(verb.contains("FINISH")){
//			 if(verb.contains("pc")){
////				 String ip=session.getRemoteIp().toString();
////				 ip=ip.split("/")[1];
//				 verb=verb+"&&pcIP="+session.getRemoteIp();
//			 }
			  DebugLog.showLog("FtpCmd","已经接受完毕！"+verb);
			  verb="FINISH&&DEVICEIP="+session.getRemoteIp();
//			  EventBus.getDefault().post(new FtpEvent(verb));
		  }


//		 if (verb.contains("isShareFile")){
//			 DebugLog.showLog("FtpCmd","本地快传的文件...");
//			 session.setShareFile(false);
//		  }


		 if(verb.length() < 1){
			staticLog.l(Log.INFO, "Invalid command verb");
			session.writeString(unrecognizedCmdMsg);
			return;
		 }

		FtpCmd cmdInstance = null;
		verb = verb.trim();
		verb = verb.toUpperCase();


		for(int i=0; i<cmdClasses.length; i++){
			
			if(cmdClasses[i].getName().equals(verb)){
				Constructor<? extends FtpCmd> constructor;
				try {
					constructor = cmdClasses[i].getCommand().getConstructor(
							new Class[] {SessionThread.class, String.class});
				} catch (NoSuchMethodException e) {
					staticLog.l(Log.ERROR, "FtpCmd subclass lacks expected " +
							           "constructor ");
					return;
				}
				try {
					cmdInstance = constructor.newInstance(
							new Object[] {session, inputString});
				} catch(Exception e) {
					staticLog.l(Log.ERROR,
							"Instance creation error on FtpCmd");
					return;
				}
			}
		}

		if(cmdInstance == null){
			staticLog.l(Log.DEBUG, "Ignoring unrecognized FTP verb: " + verb);
			session.writeString(unrecognizedCmdMsg);
			return;
		} else if(session.isAuthenticated() 
				|| cmdInstance.getClass().equals(CmdUSER.class)
				|| cmdInstance.getClass().equals(CmdPASS.class)
				|| cmdInstance.getClass().equals(CmdUSER.class))
		{
			cmdInstance.run();
		} else {
			session.writeString("530 Login first with USER and PASS\r\n");
		}
	}
		
	/**
	 * An FTP parameter is that part of the input string that occurs
	 * after the first space, including any subsequent spaces. Also,
	 * we want to chop off the trailing '\r\n', if present.
	 * 
	 * Some parameters shouldn't be logged or output (e.g. passwords),
	 * so the caller can use silent==true in that case.
	 */
	static public String getParameter(String input, boolean silent) {
		if(input == null){
			return "";
		}
		int firstSpacePosition = input.indexOf(' ');
		if(firstSpacePosition == -1) {
			return "";
		}
		String retString = input.substring(firstSpacePosition+1);
		
		// Remove trailing whitespace
		// todo: trailing whitespace may be significant, just remove \r\n
		retString = retString.replaceAll("\\s+$", "");
		
		if(!silent) {
			staticLog.l(Log.DEBUG, "Parsed argument: " + retString);
		}
		return retString; 
	}
	
	/**
	 * A wrapper around getParameter, for when we don't want it to be silent.
	 */

	static public String getParameter(String input) {
		return getParameter(input, false);
	}

	public static File inputPathToChrootedFile(File existingPrefix, String param) {
		try {
			if(param.charAt(0) == '/') {
				// The STOR contained an absolute path
				File chroot = Globals.getChrootDir();
				return new File(chroot, param);
			}
		} catch (Exception e) {}
		
		// The STOR contained a relative path
		return new File(existingPrefix, param);
	}
	
	public boolean violatesChroot(File file){
		File chroot = Globals.getChrootDir();
		try {
			String canonicalPath = file.getCanonicalPath();
			if(!canonicalPath.startsWith(chroot.toString())) {
				myLog.l(Log.INFO, "Path violated folder restriction, denying");
				myLog.l(Log.DEBUG, "path: " + canonicalPath);
				myLog.l(Log.DEBUG, "chroot: " + chroot.toString());
				return true; // the path must begin with the chroot path
			}
			return false;
		} catch(Exception e){
			myLog.l(Log.INFO, "Path canonicalization problem: " + e.toString());
			myLog.l(Log.INFO, "When checking file: " + file.getAbsolutePath());
			return true;  // for security, assume violation
		}
	}
}
