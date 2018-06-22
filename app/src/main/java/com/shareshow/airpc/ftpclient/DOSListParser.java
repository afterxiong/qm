/*    */ package com.shareshow.airpc.ftpclient;
/*    */ 
/*    */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*    */
/*    */
/*    */
/*    */
/*    */

/*    */
/*    */ public class DOSListParser
/*    */   implements FTPListParser
/*    */ {
/* 39 */   private static final Pattern PATTERN = Pattern.compile("^(\\d{2})-(\\d{2})-(\\d{2})\\s+(\\d{2}):(\\d{2})(AM|PM)\\s+(<DIR>|\\d+)\\s+([^\\\\/*?\"<>|]+)$");
/*    */ 
/* 43 */   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy hh:mm a");
/*    */ 
/*    */   public FTPFile[] parse(String[] lines, int type) throws FTPListParseException
/*    */   {
/* 47 */     int size = lines.length;
/* 48 */     FTPFile[] ret = new FTPFile[size];
/* 49 */     for (int i = 0; i < size; i++) {
/* 50 */       Matcher m = PATTERN.matcher(lines[i]);
/* 51 */       if (m.matches()) {
/* 52 */         String month = m.group(1);
/* 53 */         String day = m.group(2);
/* 54 */         String year = m.group(3);
/* 55 */         String hour = m.group(4);
/* 56 */         String minute = m.group(5);
/* 57 */         String ampm = m.group(6);
/* 58 */         String dirOrSize = m.group(7);
/* 59 */         String name = m.group(8);
/* 60 */         ret[i] = new FTPFile();
/* 61 */         ret[i].setName(name);
/* 62 */         if (dirOrSize.equalsIgnoreCase("<DIR>")) {
/* 63 */           ret[i].setType(1);
/* 64 */           ret[i].setSize(0L);
/*    */         } else {
/*    */           long fileSize;
/*    */           try {
/* 68 */             fileSize = Long.parseLong(dirOrSize);
/*    */           } catch (Throwable t) {
/* 70 */             throw new FTPListParseException();
/*    */           }
/* 72 */           ret[i].setType(0);
/* 73 */           ret[i].setSize(fileSize);
/*    */         }
/* 75 */         String mdString = month + "/" + day + "/" + year + " " + hour + ":" + minute + " " + ampm;
/*    */         Date md;
/*    */         try {
/* 79 */           synchronized (DATE_FORMAT) {
/* 80 */             md = DATE_FORMAT.parse(mdString);
/*    */           }
/*    */         } catch (ParseException e) {
/* 83 */           throw new FTPListParseException();
/*    */         }
/* 85 */         ret[i].setModifiedDate(md);
/*    */       } else {
/* 87 */         throw new FTPListParseException();
/*    */       }
/*    */     }
/* 90 */     return ret;
/*    */   }
/*    */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.listparsers.DOSListParser
 * JD-Core Version:    0.6.2
 */