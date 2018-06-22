/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

/*     */

import java.util.Date;
import java.util.StringTokenizer;

/*     */

/*     */
/*     */ public class EPLFListParser
/*     */   implements FTPListParser
/*     */ {
/*     */   public FTPFile[] parse(String[] lines, int type)
/*     */     throws FTPListParseException
/*     */   {
/*  36 */     int size = lines.length;
/*  37 */     FTPFile[] ret = null;
/*  38 */     for (int i = 0; i < size; i++) {
/*  39 */       String l = lines[i];
/*     */ 
/*  41 */       if (l.charAt(0) != '+') {
/*  42 */         throw new FTPListParseException();
/*     */       }
/*     */ 
/*  45 */       int a = l.indexOf('\t');
/*  46 */       if (a == -1) {
/*  47 */         throw new FTPListParseException();
/*     */       }
/*  49 */       String facts = l.substring(1, a);
/*  50 */       String name = l.substring(a + 1, l.length());
/*     */ 
/*  52 */       Date md = null;
/*  53 */       boolean dir = false;
/*  54 */       long fileSize = 0L;
/*  55 */       StringTokenizer st = new StringTokenizer(facts, ",");
/*  56 */       while (st.hasMoreTokens()) {
/*  57 */         String f = st.nextToken();
/*  58 */         int s = f.length();
/*  59 */         if (s > 0) {
/*  60 */           if (s == 1) {
/*  61 */             if (f.equals("/"))
/*     */             {
/*  63 */               dir = true;
/*     */             }
/*     */           } else {
/*  66 */             char c = f.charAt(0);
/*  67 */             String value = f.substring(1, s);
/*  68 */             if (c == 's')
/*     */               try
/*     */               {
/*  71 */                 fileSize = Long.parseLong(value);
/*     */               }
/*     */               catch (Throwable t) {
/*     */               }
/*  75 */             else if (c == 'm') {
/*     */               try
/*     */               {
/*  78 */                 long m = Long.parseLong(value);
/*  79 */                 md = new Date(m * 1000L);
/*     */               }
/*     */               catch (Throwable t)
/*     */               {
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*  88 */       if (ret == null) {
/*  89 */         ret = new FTPFile[size];
/*     */       }
/*  91 */       ret[i] = new FTPFile();
/*  92 */       ret[i].setName(name);
/*  93 */       ret[i].setModifiedDate(md);
/*  94 */       ret[i].setSize(fileSize);
/*  95 */       ret[i].setType(dir ? 1 : 0);
/*     */     }
/*  97 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Throwable {
/* 101 */     String[] test = { "+i8388621.29609,m824255902,/,\tdev", "+i8388621.44468,m839956783,r,s10376,\tRFCEPLF" };
/*     */ 
/* 103 */     EPLFListParser parser = new EPLFListParser();
/* 104 */     FTPFile[] f = parser.parse(test,0);
/* 105 */     for (int i = 0; i < f.length; i++)
/* 106 */       System.out.println(f[i]);
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.listparsers.EPLFListParser
 * JD-Core Version:    0.6.2
 */