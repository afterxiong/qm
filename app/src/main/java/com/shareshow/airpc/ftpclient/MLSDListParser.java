/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public class MLSDListParser
/*     */   implements FTPListParser
/*     */ {
/*  45 */   private static final DateFormat MLSD_DATE_FORMAT_1 = new SimpleDateFormat("yyyyMMddhhmmss.SSS Z");
/*     */ 
/*  50 */   private static final DateFormat MLSD_DATE_FORMAT_2 = new SimpleDateFormat("yyyyMMddhhmmss Z");
/*     */ 
/*     */   public FTPFile[] parse(String[] lines, int type) throws FTPListParseException {
/*  53 */     ArrayList list = new ArrayList();
/*  54 */     for (int i = 0; i < lines.length; i++) {
/*  55 */       FTPFile file = parseLine(lines[i]);
/*  56 */       if (file != null){
/*  57 */         list.add(file);
/*     */       }
/*     */     }
/*  60 */     int size = list.size();
/*  61 */     FTPFile[] ret = new FTPFile[size];
/*  62 */     for (int i = 0; i < size; i++) {
/*  63 */       ret[i] = ((FTPFile)list.get(i));
/*     */     }
/*  65 */     return ret;
/*     */   }
/*     */ 
/*     */   private FTPFile parseLine(String line)
/*     */     throws FTPListParseException
/*     */   {
/*  79 */     ArrayList list = new ArrayList();
/*  80 */     StringTokenizer st = new StringTokenizer(line, ";");
/*  81 */     while (st.hasMoreElements()) {
/*  82 */       String aux = st.nextToken().trim();
/*  83 */       if (aux.length() > 0){
/*  84 */         list.add(aux);
/*     */       }
/*     */     }
/*  87 */     if (list.size() == 0) {
/*  88 */       throw new FTPListParseException();
/*     */     }
/*     */ 
/*  91 */     String name = (String)list.remove(list.size() - 1);
/*     */ 
/*  93 */     Properties facts = new Properties();
/*  94 */     for (Iterator i = list.iterator(); i.hasNext(); ) {
/*  95 */       String aux = (String)i.next();
/*  96 */       int sep = aux.indexOf('=');
/*  97 */       if (sep == -1) {
/*  98 */         throw new FTPListParseException();
/*     */       }
/* 100 */       String key = aux.substring(0, sep).trim();
/* 101 */       String value = aux.substring(sep + 1, aux.length()).trim();
/* 102 */       if ((key.length() == 0) || (value.length() == 0)) {
/* 103 */         throw new FTPListParseException();
/*     */       }
/* 105 */       facts.setProperty(key, value);
/*     */     }
/*     */ 
/* 109 */     String typeString = facts.getProperty("type");
/* 110 */     if (typeString == null)
/* 111 */       throw new FTPListParseException();
/*     */     int type;
/* 112 */     if ("file".equalsIgnoreCase(typeString)) {
/* 113 */       type = 0;
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 114 */       if ("dir".equalsIgnoreCase(typeString)) {
/* 115 */         type = 1; } else {
/* 116 */         if ("cdir".equalsIgnoreCase(typeString))
/*     */         {
/* 118 */           return null;
/* 119 */         }if ("pdir".equalsIgnoreCase(typeString))
/*     */         {
/* 121 */           return null;
/*     */         }
/*     */ 
/* 124 */         return null;
/*     */       }
/*     */     }
/*     *
/* 127 */     Date modifiedDate = null;
/* 128 */     String modifyString = facts.getProperty("modify");
/* 129 */     if (modifyString != null) {
/* 130 */       modifyString = modifyString + " +0000";
/*     */       try {
/* 132 */         synchronized (MLSD_DATE_FORMAT_1) {
/* 133 */           modifiedDate = MLSD_DATE_FORMAT_1.parse(modifyString);
/*     */         }
/*     */       } catch (ParseException e1) {
/*     */         try {
/* 137 */           synchronized (MLSD_DATE_FORMAT_2) {
/* 138 */             modifiedDate = MLSD_DATE_FORMAT_2.parse(modifyString);
/*     */           }
/*     */         }
/*     */         catch (ParseException e2)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 146 */     long size = 0L;
/* 147 */     String sizeString = facts.getProperty("size");
/* 148 */     if (sizeString != null) {
/*     */       try {
/* 150 */         size = Long.parseLong(sizeString);
/*     */       }
/*     */       catch (NumberFormatException e) {
/*     */       }
/* 154 */       if (size < 0L) {
/* 155 */         size = 0L;
/*     */       }
/*     */     }
/*     */ 
/* 159 */     FTPFile ret = new FTPFile();
/* 160 */     ret.setType(type);
/* 161 */     ret.setModifiedDate(modifiedDate);
/* 162 */     ret.setSize(size);
/* 163 */     ret.setName(name);
/* 164 */     return ret;
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.listparsers.MLSDListParser
 * JD-Core Version:    0.6.2
 */