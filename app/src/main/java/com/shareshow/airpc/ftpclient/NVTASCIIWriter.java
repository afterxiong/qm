/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.StringTokenizer;

/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ class NVTASCIIWriter extends Writer
/*     */ {
/*     */   private static final String LINE_SEPARATOR = "\r\n";
/*     */   private OutputStream stream;
/*     */   private Writer writer;
/*     */ 
/*     */   public NVTASCIIWriter(OutputStream stream, String charsetName)
/*     */     throws IOException
/*     */   {
/*  62 */     this.stream = stream;
/*  63 */     this.writer = new OutputStreamWriter(stream, charsetName);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  72 */     synchronized (this) {
/*  73 */       this.writer.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush() throws IOException {
/*  78 */     synchronized (this) {
/*  79 */       this.writer.flush();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(char[] cbuf, int off, int len) throws IOException {
/*  84 */     synchronized (this) {
/*  85 */       this.writer.write(cbuf, off, len);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void changeCharset(String charsetName)
/*     */     throws IOException
/*     */   {
/*  99 */     synchronized (this) {
/* 100 */       this.writer = new OutputStreamWriter(this.stream, charsetName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeLine(String str)
/*     */     throws IOException
/*     */   {
/* 113 */     StringBuffer buffer = new StringBuffer();
/* 114 */     boolean atLeastOne = false;
/* 115 */     StringTokenizer st = new StringTokenizer(str, "\r\n");
/* 116 */     int count = st.countTokens();
/* 117 */     for (int i = 0; i < count; i++) {
/* 118 */       String line = st.nextToken();
/* 119 */       if (line.length() > 0) {
/* 120 */         if (atLeastOne) {
/* 121 */           buffer.append('\r');
/* 122 */           buffer.append('\000');
/*     */         }
/* 124 */         buffer.append(line);
/* 125 */         atLeastOne = true;
/*     */       }
/*     */     }
/* 128 */     if (buffer.length() > 0) {
/* 129 */       String statement = buffer.toString();
/*     */ 
/* 131 */       this.writer.write(statement);
/* 132 */       this.writer.write("\r\n");
/* 133 */       this.writer.flush();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.NVTASCIIWriter
 * JD-Core Version:    0.6.2
 */