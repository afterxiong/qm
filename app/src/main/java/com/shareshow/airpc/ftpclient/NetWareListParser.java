/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public class NetWareListParser
/*     */   implements FTPListParser
/*     */ {
/*  41 */   private static final Pattern PATTERN = Pattern.compile("^(d|-)\\s+\\[.{8}\\]\\s+\\S+\\s+(\\d+)\\s+(?:(\\w{3})\\s+(\\d{1,2}))\\s+(?:(\\d{4})|(?:(\\d{1,2}):(\\d{1,2})))\\s+([^\\\\/*?\"<>|]+)$");
/*     */ 
/*  46 */   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd yyyy HH:mm", Locale.US);
/*     */ 
/*     */   public FTPFile[] parse(String[] lines, int type) throws FTPListParseException
/*     */   {
/*  50 */     int size = lines.length;
/*     */ 
/*  52 */     Calendar now = Calendar.getInstance();
/*     */ 
/*  54 */     int currentYear = now.get(1);
/*  55 */     FTPFile[] ret = new FTPFile[size];
/*  56 */     for (int i = 0; i < size; i++) {
/*  57 */       Matcher m = PATTERN.matcher(lines[i]);
/*  58 */       if (m.matches()) {
/*  59 */         String typeString = m.group(1);
/*  60 */         String sizeString = m.group(2);
/*  61 */         String monthString = m.group(3);
/*  62 */         String dayString = m.group(4);
/*  63 */         String yearString = m.group(5);
/*  64 */         String hourString = m.group(6);
/*  65 */         String minuteString = m.group(7);
/*  66 */         String nameString = m.group(8);
/*     */ 
/*  68 */         ret[i] = new FTPFile();
/*  69 */         if (typeString.equals("-"))
/*  70 */           ret[i].setType(0);
/*  71 */         else if (typeString.equals("d"))
/*  72 */           ret[i].setType(1);
/*     */         else
/*  74 */           throw new FTPListParseException();
/*     */         long fileSize;
/*     */         try
/*     */         {
/*  78 */           fileSize = Long.parseLong(sizeString);
/*     */         } catch (Throwable t) {
/*  80 */           throw new FTPListParseException();
/*     */         }
/*  82 */         ret[i].setSize(fileSize);
/*  83 */         if (dayString.length() == 1) {
/*  84 */           dayString = "0" + dayString;
/*     */         }
/*  86 */         StringBuffer mdString = new StringBuffer();
/*  87 */         mdString.append(monthString);
/*  88 */         mdString.append(' ');
/*  89 */         mdString.append(dayString);
/*  90 */         mdString.append(' ');
/*  91 */         boolean checkYear = false;
/*  92 */         if (yearString == null) {
/*  93 */           mdString.append(currentYear);
/*  94 */           checkYear = true;
/*     */         } else {
/*  96 */           mdString.append(yearString);
/*  97 */           checkYear = false;
/*     */         }
/*  99 */         mdString.append(' ');
/* 100 */         if ((hourString != null) && (minuteString != null)) {
/* 101 */           if (hourString.length() == 1) {
/* 102 */             hourString = "0" + hourString;
/*     */           }
/* 104 */           if (minuteString.length() == 1) {
/* 105 */             minuteString = "0" + minuteString;
/*     */           }
/* 107 */           mdString.append(hourString);
/* 108 */           mdString.append(':');
/* 109 */           mdString.append(minuteString);
/*     */         } else {
/* 111 */           mdString.append("00:00");
/*     */         }
/*     */         Date md;
/*     */         try {
/* 115 */           synchronized (DATE_FORMAT) {
/* 116 */             md = DATE_FORMAT.parse(mdString.toString());
/*     */           }
/*     */         } catch (ParseException e) {
/* 119 */           throw new FTPListParseException();
/*     */         }
/* 121 */         if (checkYear) {
/* 122 */           Calendar mc = Calendar.getInstance();
/* 123 */           mc.setTime(md);
/* 124 */           if ((mc.after(now)) && (mc.getTimeInMillis() - now.getTimeInMillis() > 86400000L)) {
/* 125 */             mc.set(1, currentYear - 1);
/* 126 */             md = mc.getTime();
/*     */           }
/*     */         }
/* 129 */         ret[i].setModifiedDate(md);
/* 130 */         ret[i].setName(nameString);
/*     */       } else {
/* 132 */         throw new FTPListParseException();
/*     */       }
/*     */     }
/* 135 */     return ret;
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.listparsers.NetWareListParser
 * JD-Core Version:    0.6.2
 */