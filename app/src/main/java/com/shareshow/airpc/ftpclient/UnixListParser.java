package com.shareshow.airpc.ftpclient;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UnixListParser implements FTPListParser {

	private static final Pattern PATTERN = Pattern.compile("^([\\-dl])([a-zA-Z\\-]{9,9})\\s+\\d+\\s+(\\S*)\\s+(\\S*)\\s+(\\d+)\\s+(\\S+\\s+\\S+\\s+\\S+\\s+\\S+:\\S+)\\s+(\\S.*)");

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy MMM dd HH:mm", Locale.US);
	private static final Pattern PATTERN2 = Pattern.compile("^([dl\\-])[r\\-][w\\-][xSs\\-][r\\-][w\\-][xSs\\-][r\\-][w\\-][xTt\\-]\\s+(?:\\d+\\s+)?\\S+\\s*\\S+\\s+(\\d+)\\s+(?:(\\w{3})\\s+(\\d{1,2}))\\s+(?:(\\d{4})|(?:(\\d{1,2}):(\\d{1,2})))\\s+([^\\\\*?\"<>|]+)(?: -> ([^\\\\*?\"<>|]+))?$");
	
	private static final DateFormat DATE_FORMAT2 = new SimpleDateFormat("MMM dd yyyy HH:mm", Locale.US);
	
	

	public FTPFile[] parse(String[] lines, int type) throws FTPListParseException {
		
		switch (type) {
		case -1:
			return android(lines);
		case -2:
			return ios_old(lines);
		case 1:
			return ios(lines);
		default:
			return android_box(lines);
		}
	}

	private FTPFile[] android_box(String[] lines) throws FTPListParseException {
		int size = lines.length;
		if (size == 0) {
			return new FTPFile[0];
		}

		if (lines[0].startsWith("total")) {
			size--;
			String[] lines2 = new String[size];
			for (int i = 0; i < size; i++) {
				lines2[i] = lines[(i + 1)];
			}
			lines = lines2;
		}

		Calendar now = Calendar.getInstance();

		int currentYear = now.get(1);
		FTPFile[] ret = new FTPFile[size];
		for (int i = 0; i < size; i++) {
			Matcher m = PATTERN2.matcher(lines[i]);
//[-rw-rw-rw- 1 unknown unknown 7168 Oct 24 15:36 11111.file_icon_xls, drwxrwxrwx 1 unknown unknown 0 Oct 24 15:35 123]
//[-rw-rw-rw- 1 unknown unknown 7168 2016 Nov 10 11:15 11111.file_icon_xls, -rw-rw-rw- 1 unknown unknown 46592 2016 Nov 10 11:15 Android.file_icon_xls, drwxrwxrwx 1 unknown unknown 0 2016 Nov 10 11:14 WF]
			if (m.matches()) {
				ret[i] = new FTPFile();

				String typeString = m.group(1);
				String sizeString = m.group(2);
				String monthString = m.group(3);
				String dayString = m.group(4);
				String yearString = m.group(5);
				String hourString = m.group(6);
				String minuteString = m.group(7);
				String nameString = m.group(8);
				try {
					nameString= URLDecoder.decode(nameString, "utf-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				if (typeString.equals("-")) {
					ret[i].setType(0);
					ret[i].setDir(1);
				} else if (typeString.equals("d")) {
					ret[i].setType(1);
					ret[i].setDir(0);
				} else if (typeString.equals("l")) {
					ret[i].setType(2);
				} else {
					throw new FTPListParseException();
				}
				long fileSize;
				try {
					fileSize = Long.parseLong(sizeString);
				} catch (Throwable t) {
					throw new FTPListParseException();
				}

				ret[i].setSize(fileSize);
				if (dayString.length() == 1) {
					dayString = "0" + dayString;
				}
				StringBuffer mdString = new StringBuffer();
				mdString.append(monthString);
				mdString.append(' ');
				mdString.append(dayString);
				mdString.append(' ');
				boolean checkYear = false;
				if (yearString == null) {
					mdString.append(currentYear);
					checkYear = true;
				} else {
					mdString.append(yearString);
					checkYear = false;
				}
				mdString.append(' ');
				if ((hourString != null) && (minuteString != null)) {
					if (hourString.length() == 1) {
						hourString = "0" + hourString;
					}
					if (minuteString.length() == 1) {
						minuteString = "0" + minuteString;
					}
					mdString.append(hourString);
					mdString.append(':');
					mdString.append(minuteString);
				} else {
					mdString.append("00:00");
				}
				Date md;
				try {
					synchronized (DATE_FORMAT2) {
						md = DATE_FORMAT2.parse(mdString.toString());
					}
				} catch (ParseException e) {
					throw new FTPListParseException();
				}
				if (checkYear) {
					Calendar mc = Calendar.getInstance();
					mc.setTime(md);
					if ((mc.after(now))
							&& (mc.getTimeInMillis() - now.getTimeInMillis() > 86400000L)) {
						mc.set(1, currentYear - 1);
						md = mc.getTime();
					}
				}
				ret[i].setModifiedDate(md);
				ret[i].setName(nameString);
			} else {
				throw new FTPListParseException();
			}
		}
		return ret;
	}


	private FTPFile[] ios(String[] lines) throws FTPListParseException {
		int size = lines.length;
		if (size == 0) {
			return new FTPFile[0];
		}
		if (lines[0].startsWith("total")) {
			size--;
			String[] lines2 = new String[size];
			for (int i = 0; i < size; i++) {
				lines2[i] = lines[i + 1];
			}
			lines = lines2;
		}
		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		FTPFile[] ret = new FTPFile[size];
		for (int i = 0; i < size; i++) {
			Matcher m = PATTERN.matcher(lines[i]);
			if (m.matches()) {
				ret[i] = new FTPFile();
				String typeString = m.group(1);
				String permissString = m.group(3);//owner位置作为文件下载权限
				String groupString = m.group(4);
				String sizeString = m.group(5);
				String dataString = m.group(6);
				String fileString = m.group(7);
				long fileSize;
				try {
					fileSize = Long.parseLong(sizeString);
				} catch (Throwable t) {
					throw new FTPListParseException();
				}
				
				if(typeString.equals("-")){
					ret[i].setType(FTPFile.TYPE_FILE);
					ret[i].setDir(1);
					ret[i].setSize(fileSize);
				}else if (typeString.equals("d")) {
					ret[i].setType(FTPFile.TYPE_DIRECTORY);
					ret[i].setDir(0);
					
					ret[i].setSize(Integer.parseInt(lines[i].split(" ")[1]));
				}else if (typeString.equals("l")) {
					ret[i].setType(FTPFile.TYPE_LINK);
					ret[i].setSize(fileSize);
				}else{
					throw new FTPListParseException();
				}
				
				//unknown
				if(permissString.equals("allowDownload")||permissString.equals("unknown"))
					ret[i].setPermit(0);
				else
					ret[i].setPermit(1);
				ret[i].setLink(groupString);
				boolean checkYear = false;
				Date md;
				try {
					synchronized (DATE_FORMAT) {
						md = DATE_FORMAT.parse(dataString.toString());
					}
				}catch (ParseException e){
					throw new FTPListParseException();
				}
				if (checkYear) {
					Calendar mc = Calendar.getInstance();
					mc.setTime(md);
					if (mc.after(now) && mc.getTimeInMillis() - now.getTimeInMillis() > 24L * 60L * 60L * 1000L) {
						mc.set(Calendar.YEAR, currentYear - 1);
						md = mc.getTime();
					}
				}
				ret[i].setModifiedDate(md);
				ret[i].setName(fileString);
			} else {
				throw new FTPListParseException();
			}
		}
		return ret;
	}
	
	private FTPFile[] android(String[] lines) {
		int size = lines.length;
		if (size == 0) {
			return new FTPFile[0];
		}

		if (lines[0].startsWith("total")) {
			size--;
			String[] lines2 = new String[size];
			for (int i = 0; i < size; i++) {
				lines2[i] = lines[(i + 1)];
			}
			lines = lines2;
		}
		Calendar now = Calendar.getInstance();
		FTPFile[] ret = new FTPFile[size];
		for (int a = 0; a < size; a++) {
			String gg=lines[a];
			ArrayList<String> al = new ArrayList<>();
			String[] ii = gg.split(" ");
			for (int i = 0; i < ii.length; i++) {
				if (ii[i].length() == 0)
					continue;
				al.add(ii[i]);
			}
			String link="";
			int position=0;
			for (int i = 3; i <al.size(); i++) {
				if(isNumber2(al.get(i))){
					position=i;					
					break;
				}
				link+=al.get(i)+" ";
			}
			link=link.trim();
			if (!isNumber(al.get(4))) {
				ret[a] = new FTPFile();
				if(al.get(1).equals("1"))
					ret[a].setDir(1);
				else
					ret[a].setDir(0);
				if(al.get(2).equals("allowDownload"))
					ret[a].setPermit(0);
				else
					ret[a].setPermit(1);
				ret[a].setLink(link);
				ret[a].setSize(Long.parseLong(al.get(position)));
				ret[a].setName(link.substring(link.lastIndexOf("/")+1, link.length()));
				String tt=" "+al.get(position+1)+" "+al.get(position+2)+" "+al.get(position+3)+" ";
				try {
					Date uu=new SimpleDateFormat(" MMM dd HH:mm ", Locale.US).parse(tt);
					ret[a].setModifiedDate(uu);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				ret[a] = new FTPFile();
				if(al.get(1).equals("1"))
					ret[a].setDir(1);
				else
					ret[a].setDir(0);
				if(al.get(2).equals("allowDownload"))
					ret[a].setPermit(0);
				else
					ret[a].setPermit(1);
				ret[a].setLink(al.get(3));
				ret[a].setSize(Long.parseLong(al.get(4)));
				ret[a].setName(al.get(8));
				String tt=" "+al.get(5)+" "+al.get(6)+" "+al.get(7)+" ";
				try {
					Date uu=new SimpleDateFormat(" MMM dd HH:mm ", Locale.US).parse(tt);
					ret[a].setModifiedDate(uu);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
	private static boolean isNumber(String info) {
		try {
			System.out.println(info.length());
			if(info.length()==0)
				return true;
			Integer.parseInt(info);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	private static boolean isNumber2(String info) {
		try {
			System.out.println(info.length());
			if(info.length()==0)
				return true;
			int aa= Integer.parseInt(info);
			if(aa<10000)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	private FTPFile[] ios_old(String[] lines) throws FTPListParseException {
		int size = lines.length;
		if (size == 0) {
			return new FTPFile[0];
		}
		if (lines[0].startsWith("total")) {
			size--;
			String[] lines2 = new String[size];
			for (int i = 0; i < size; i++) {
				lines2[i] = lines[i + 1];
			}
			lines = lines2;
		}
		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		FTPFile[] ret = new FTPFile[size];
		for (int i = 0; i < size; i++) {
			Matcher m = PATTERN2.matcher(lines[i]);
			if (m.matches()) {
				ret[i] = new FTPFile();
				String typeString = m.group(1);
				String sizeString = m.group(2);
				String monthString = m.group(3);
				String dayString = m.group(4);
				String yearString = m.group(5);
				String hourString = m.group(6);
				String minuteString = m.group(7);
				String nameString = m.group(8);

				if (typeString.equals("-")) {
					ret[i].setType(FTPFile.TYPE_FILE);
					ret[i].setDir(1);
				} else if (typeString.equals("d")) {
					ret[i].setType(FTPFile.TYPE_DIRECTORY);
					ret[i].setDir(0);
				} else if (typeString.equals("l")) {
					ret[i].setType(FTPFile.TYPE_LINK);
				} else {
					throw new FTPListParseException();
				}
				
				long fileSize;
				try {
					fileSize = Long.parseLong(sizeString);
				} catch (Throwable t) {
					throw new FTPListParseException();
				}
				ret[i].setSize(fileSize);
				String permissString =m.group(0).split(" ")[2];
				if(permissString.equals("allowDownload"))
						ret[i].setPermit(0);
					else
						ret[i].setPermit(1);
				ret[i].setLink("");
				boolean checkYear = false;
				
				 StringBuffer mdString = new StringBuffer();
					/* 106 */         mdString.append(monthString);
					/* 107 */         mdString.append(' ');
					/* 108 */         mdString.append(dayString);
					/* 109 */         mdString.append(' ');
					/* 111 */         if (yearString == null) {
					/* 112 */           mdString.append(currentYear);
					/* 113 */           checkYear = true;
					/*     */         } else {
					/* 115 */           mdString.append(yearString);
					/* 116 */           checkYear = false;
					/*     */         }
					/* 118 */         mdString.append(' ');
					/* 119 */         if ((hourString != null) && (minuteString != null)) {
					/* 120 */           if (hourString.length() == 1) {
					/* 121 */             hourString = "0" + hourString;
					/*     */           }
					/* 123 */           if (minuteString.length() == 1) {
					/* 124 */             minuteString = "0" + minuteString;
					/*     */           }
					/* 126 */           mdString.append(hourString);
					/* 127 */           mdString.append(':');
					/* 128 */           mdString.append(minuteString);
					/*     */         } else {
					/* 130 */           mdString.append("00:00");
					/*     */         }
				Date md;
				try {
					//Sep 29 2016 16:28
					synchronized (DATE_FORMAT2) {
						md = DATE_FORMAT2.parse(mdString.toString());
					}
				} catch (ParseException e) {
					throw new FTPListParseException();
				}
				if (checkYear) {
					Calendar mc = Calendar.getInstance();
					mc.setTime(md);
					if (mc.after(now) && mc.getTimeInMillis() - now.getTimeInMillis() > 24L * 60L * 60L * 1000L) {
						mc.set(Calendar.YEAR, currentYear - 1);
						md = mc.getTime();
					}
				}
				ret[i].setModifiedDate(md);
				ret[i].setName(nameString);
			} else {
				throw new FTPListParseException();
			}
		}
		return ret;
	}

}
