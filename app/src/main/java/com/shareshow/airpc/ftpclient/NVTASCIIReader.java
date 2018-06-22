/*    */ package com.shareshow.airpc.ftpclient;
/*    */ 
/*    */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/*    */
/*    */
/*    */

/*    */
/*    */ class NVTASCIIReader extends Reader
/*    */ {
/* 37 */   private static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");
/*    */   private InputStream stream;
/*    */   private Reader reader;
/*    */ 
/*    */   public NVTASCIIReader(InputStream stream, String charsetName)
/*    */     throws IOException
/*    */   {
/* 62 */     this.stream = stream;
/* 63 */     this.reader = new InputStreamReader(stream, charsetName);
/*    */   }
/*    */ 
/*    */   public void close() throws IOException {
/* 67 */     synchronized (this) {
/* 68 */       this.reader.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   public int read(char[] cbuf, int off, int len) throws IOException {
/* 73 */     synchronized (this) {
/* 74 */       return this.reader.read(cbuf, off, len);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void changeCharset(String charsetName)
/*    */     throws IOException
/*    */   {
/* 88 */     synchronized (this) {
/* 89 */       this.reader = new InputStreamReader(this.stream, charsetName);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String readLine()
/*    */     throws IOException
/*    */   {
/* 101 */     StringBuffer buffer = new StringBuffer();
/* 102 */     int previous = -1;
/* 103 */     int current = -1;
/*    */     while (true) {
/* 105 */       int i = this.reader.read();
/* 106 */       if (i == -1) {
/* 107 */         if (buffer.length() == 0) {
/* 108 */           return null;
/*    */         }
/* 110 */         return buffer.toString();
/*    */       }
/*    */ 
/* 113 */       previous = current;
/* 114 */       current = i;
/* 115 */       if (current == 10)
/*    */       {
/* 117 */         return buffer.toString();
/* 118 */       }if ((previous == 13) && (current == 0))
/*    */       {
/* 120 */         buffer.append(SYSTEM_LINE_SEPARATOR);
/* 121 */       } else if ((current != 0) && (current != 13))
/* 122 */         buffer.append((char)current);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.NVTASCIIReader
 * JD-Core Version:    0.6.2
 */